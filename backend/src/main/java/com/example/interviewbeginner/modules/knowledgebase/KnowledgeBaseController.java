package com.example.interviewbeginner.modules.knowledgebase;

import com.example.interviewbeginner.common.result.Result;
import com.example.interviewbeginner.modules.knowledgebase.dto.ChatRequest;
import com.example.interviewbeginner.modules.knowledgebase.dto.ChatResponse;
import com.example.interviewbeginner.modules.knowledgebase.dto.CreateKbRequest;
import com.example.interviewbeginner.modules.knowledgebase.dto.DocumentResponse;
import com.example.interviewbeginner.modules.knowledgebase.dto.KbResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 知识库接口。
 */
@RestController
@RequestMapping("/api/knowledgebases")
@RequiredArgsConstructor
public class KnowledgeBaseController {

    private final KnowledgeBaseService kbService;

    /** 创建知识库 */
    @PostMapping
    public Result<Map<String, Long>> createKb(@Valid @RequestBody CreateKbRequest req) {
        KbResponse r = kbService.createKb(req);
        return Result.success(Map.of("kbId", r.id()));
    }

    /** 知识库列表 */
    @GetMapping
    public Result<List<KbResponse>> listKbs() {
        return Result.success(kbService.listKbs());
    }

    /** 知识库详情 */
    @GetMapping("/{kbId}")
    public Result<KbResponse> getKb(@PathVariable Long kbId) {
        return Result.success(kbService.getKb(kbId));
    }

    /** 删除知识库 */
    @DeleteMapping("/{kbId}")
    public Result<Map<String, String>> deleteKb(@PathVariable Long kbId) {
        kbService.deleteKb(kbId);
        return Result.success(Map.of("status", "deleted"));
    }

    /** 上传文档 */
    @PostMapping("/{kbId}/documents")
    public Result<DocumentResponse> uploadDocument(
            @PathVariable Long kbId,
            @RequestParam("file") MultipartFile file) {
        return Result.success(kbService.uploadDocument(kbId, file));
    }

    /** 文档列表 */
    @GetMapping("/{kbId}/documents")
    public Result<List<DocumentResponse>> listDocuments(@PathVariable Long kbId) {
        return Result.success(kbService.listDocuments(kbId));
    }

    /** 删除文档 */
    @DeleteMapping("/{kbId}/documents/{docId}")
    public Result<Map<String, String>> deleteDocument(
            @PathVariable Long kbId, @PathVariable Long docId) {
        kbService.deleteDocument(kbId, docId);
        return Result.success(Map.of("status", "deleted"));
    }

    /** RAG 问答 */
    @PostMapping("/{kbId}/chat")
    public Result<ChatResponse> chat(@PathVariable Long kbId,
                                     @Valid @RequestBody ChatRequest req) {
        return Result.success(kbService.chat(kbId, req));
    }
}
