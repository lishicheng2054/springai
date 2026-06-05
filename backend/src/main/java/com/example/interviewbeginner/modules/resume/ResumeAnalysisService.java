package com.example.interviewbeginner.modules.resume;

import com.example.interviewbeginner.common.ai.LlmProviderRegistry;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 简历AI分析服务。
 * 使用LLM分析简历内容，生成技能摘要、优缺点、风险点和建议。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeAnalysisService {

    private final LlmProviderRegistry registry;
    private final ResumeAnalysisRepository analysisRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public ResumeAnalysisEntity analyze(ResumeEntity resume) {
        // 检查是否已有分析结果
        var existing = analysisRepository.findByResumeId(resume.getId());
        if (existing.isPresent()) return existing.get();

        try {
            String raw = callAi(resume);
            return parseAndSave(resume.getId(), raw);
        } catch (Exception e) {
            log.warn("AI analysis failed for resume={}", resume.getId(), e);
            return saveFallback(resume.getId());
        }
    }

    public ResumeAnalysisEntity getAnalysis(Long resumeId) {
        return analysisRepository.findByResumeId(resumeId).orElse(null);
    }

    private String callAi(ResumeEntity resume) {
        ChatClient client = registry.getDefaultChatClient();
        String prompt = """
            你是一位资深HR。请分析以下简历，用JSON返回（不要其他内容）：
            {
              "summary": "简历整体评价（30字内）",
              "skills": ["技能1","技能2","技能3"],
              "advantages": ["优势1","优势2"],
              "risks": ["风险点1","风险点2"],
              "suggestions": ["改进建议1","改进建议2"]
            }

            【候选人】%s
            【目标岗位】%s
            【简历内容】%s
            """.formatted(resume.getCandidateName(), resume.getTargetPosition(),
                         resume.getResumeText().substring(0, Math.min(resume.getResumeText().length(), 3000)));
        return client.prompt().user(prompt).call().content();
    }

    @SuppressWarnings("unchecked")
    private ResumeAnalysisEntity parseAndSave(Long resumeId, String raw) {
        try {
            String json = raw.trim();
            int s = json.indexOf('{'), e = json.lastIndexOf('}');
            if (s >= 0 && e > s) json = json.substring(s, e + 1);

            Map<String, Object> map = objectMapper.readValue(json, Map.class);
            ResumeAnalysisEntity analysis = ResumeAnalysisEntity.builder()
                    .resumeId(resumeId)
                    .summary((String) map.get("summary"))
                    .skills(toJson(map.get("skills")))
                    .advantages(toJson(map.get("advantages")))
                    .risks(toJson(map.get("risks")))
                    .suggestions(toJson(map.get("suggestions")))
                    .build();
            return analysisRepository.save(analysis);
        } catch (Exception e) {
            log.warn("Failed to parse AI analysis, using fallback", e);
            return saveFallback(resumeId);
        }
    }

    private ResumeAnalysisEntity saveFallback(Long resumeId) {
        return analysisRepository.save(ResumeAnalysisEntity.builder()
                .resumeId(resumeId)
                .summary("简历分析暂时不可用（AI服务异常）")
                .skills("[]")
                .advantages("[]")
                .risks("[]")
                .suggestions("[]")
                .build());
    }

    private String toJson(Object obj) {
        try { return objectMapper.writeValueAsString(obj); }
        catch (JsonProcessingException e) { return "[]"; }
    }
}
