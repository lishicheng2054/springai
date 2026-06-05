package com.example.interviewbeginner.modules.knowledgebase;

import com.example.interviewbeginner.common.ai.LlmProviderRegistry;
import com.example.interviewbeginner.common.exception.BusinessException;
import com.example.interviewbeginner.common.exception.ErrorCode;
import com.example.interviewbeginner.modules.knowledgebase.dto.ChatRequest;
import com.example.interviewbeginner.modules.knowledgebase.dto.ChatResponse;
import com.example.interviewbeginner.modules.knowledgebase.dto.ChatResponse.Source;
import com.example.interviewbeginner.modules.knowledgebase.dto.CreateKbRequest;
import com.example.interviewbeginner.modules.knowledgebase.dto.DocumentResponse;
import com.example.interviewbeginner.modules.knowledgebase.dto.KbResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * 知识库核心服务。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeBaseService {

    private final KnowledgeBaseRepository kbRepository;
    private final KnowledgeDocumentRepository docRepository;
    private final DocumentParseService parseService;
    private final LlmProviderRegistry registry;

    // ===== 知识库 CRUD =====

    public List<KbResponse> listKbs() {
        return kbRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(KbResponse::fromEntity).toList();
    }

    @Transactional
    public KbResponse createKb(CreateKbRequest req) {
        KnowledgeBaseEntity kb = KnowledgeBaseEntity.builder()
                .name(req.name())
                .description(req.description())
                .build();
        kb = kbRepository.save(kb);
        log.info("KnowledgeBase created: id={}, name={}", kb.getId(), kb.getName());
        return KbResponse.fromEntity(kb);
    }

    public KbResponse getKb(Long kbId) {
        return KbResponse.fromEntity(findKbOrThrow(kbId));
    }

    @Transactional
    public void deleteKb(Long kbId) {
        findKbOrThrow(kbId);
        docRepository.deleteByKbId(kbId);
        kbRepository.deleteById(kbId);
        log.info("KnowledgeBase deleted: id={}", kbId);
    }

    // ===== 文档管理 =====

    public List<DocumentResponse> listDocuments(Long kbId) {
        findKbOrThrow(kbId);
        return docRepository.findByKbIdOrderByCreatedAtDesc(kbId).stream()
                .map(DocumentResponse::fromEntity).toList();
    }

    @Transactional
    public DocumentResponse uploadDocument(Long kbId, MultipartFile file) {
        KnowledgeBaseEntity kb = findKbOrThrow(kbId);

        DocumentParseService.ParseResult parsed = parseService.parse(file);
        KnowledgeDocumentEntity doc = KnowledgeDocumentEntity.builder()
                .kbId(kbId)
                .fileName(parsed.fileName())
                .contentType(parsed.contentType())
                .fileSize(parsed.fileSize())
                .content(parsed.text())
                .build();
        doc = docRepository.save(doc);

        // 更新文档计数
        kb.setDocCount((int) docRepository.countByKbId(kbId));
        kbRepository.save(kb);

        log.info("Document uploaded: kbId={}, docId={}, name={}", kbId, doc.getId(), doc.getFileName());
        return DocumentResponse.fromEntity(doc);
    }

    @Transactional
    public void deleteDocument(Long kbId, Long docId) {
        findKbOrThrow(kbId);
        KnowledgeDocumentEntity doc = docRepository.findById(docId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "文档不存在"));
        if (!doc.getKbId().equals(kbId)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "文档不属于该知识库");
        }
        docRepository.deleteById(docId);

        KnowledgeBaseEntity kb = findKbOrThrow(kbId);
        kb.setDocCount((int) docRepository.countByKbId(kbId));
        kbRepository.save(kb);
    }

    // ===== RAG 问答 =====

    public ChatResponse chat(Long kbId, ChatRequest req) {
        findKbOrThrow(kbId);

        // 1. 搜索相关文档片段
        List<KnowledgeDocumentEntity> matches = searchDocuments(kbId, req.question());

        // 2. 构建 Context
        String context = buildContext(matches);

        // 3. 调用 LLM 生成答案
        String answer = generateAnswer(context, req.question());

        // 4. 构建引用来源
        List<Source> sources = buildSources(matches);

        return new ChatResponse(answer, sources);
    }

    // ===== 私有方法 =====

    private KnowledgeBaseEntity findKbOrThrow(Long kbId) {
        return kbRepository.findById(kbId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND,
                        "知识库不存在: id=" + kbId));
    }

    private List<KnowledgeDocumentEntity> searchDocuments(Long kbId, String query) {
        // 先用 ILIKE 模糊搜索
        List<KnowledgeDocumentEntity> results = docRepository.searchByKeyword(kbId, query);
        if (!results.isEmpty()) return results;

        // 没匹配到关键词，尝试拆词逐个搜索
        String[] words = query.split("\\s+");
        if (words.length > 1) {
            for (String word : words) {
                if (word.length() < 2) continue;
                List<KnowledgeDocumentEntity> r = docRepository.searchByKeyword(kbId, word);
                if (!r.isEmpty()) {
                    results = new ArrayList<>(r);
                    break;
                }
            }
        }

        return results != null ? results : List.of();
    }

    private String buildContext(List<KnowledgeDocumentEntity> docs) {
        if (docs.isEmpty()) return "未找到相关文档内容。";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(docs.size(), 3); i++) {
            KnowledgeDocumentEntity doc = docs.get(i);
            String snippet = doc.getContent();
            if (snippet.length() > 1500) snippet = snippet.substring(0, 1500);
            sb.append("【文档").append(i + 1).append(": ").append(doc.getFileName()).append("】\n");
            sb.append(snippet).append("\n\n");
        }
        return sb.toString();
    }

    private String generateAnswer(String context, String question) {
        try {
            ChatClient client = registry.getDefaultChatClient();
            String prompt = """
                你是一个知识库助手。请根据以下文档内容回答用户问题。
                如果文档内容不包含相关信息，请直接说"文档中未找到相关信息"。
                回答要求简洁准确，并引用具体文档。

                【文档内容】
                %s

                【用户问题】
                %s
                """.formatted(context, question);

            return client.prompt().user(prompt).call().content();
        } catch (Exception e) {
            log.warn("AI answer generation failed", e);
            return "AI 服务暂时不可用，请稍后重试。\n\n匹配到的文档摘要：\n" + context;
        }
    }

    private List<Source> buildSources(List<KnowledgeDocumentEntity> docs) {
        return docs.stream().limit(3).map(d -> {
            String snippet = d.getContent();
            if (snippet.length() > 200) snippet = snippet.substring(0, 200) + "...";
            return new Source(d.getId(), d.getFileName(), snippet);
        }).toList();
    }
}
