package com.example.interviewbeginner.modules.interview;

import com.example.interviewbeginner.common.ai.LlmProviderRegistry;
import com.example.interviewbeginner.modules.interview.InterviewQuestionEntity.QuestionType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * AI 面试服务。
 * <p>
 * 使用 Spring AI ChatClient 调用真实 LLM 生成面试题和评分。
 * 当 AI 不可用时自动降级到 MockAiService。
 */
@Slf4j
@Service
public class AiInterviewService {

    private final LlmProviderRegistry registry;
    private final MockAiService mockFallback;
    private final ObjectMapper objectMapper;

    public AiInterviewService(LlmProviderRegistry registry,
                              MockAiService mockFallback,
                              ObjectMapper objectMapper) {
        this.registry = registry;
        this.mockFallback = mockFallback;
        this.objectMapper = objectMapper;
    }

    /**
     * 生成带类型的面试题列表。
     */
    public List<MockAiService.QuestionWithType> generateQuestionsWithType(
            String resumeText, String targetPosition, int questionCount) {
        try {
            ChatClient client = registry.getDefaultChatClient();
            String prompt = buildQuestionPrompt(resumeText, targetPosition, questionCount);
            String response = client.prompt().user(prompt).call().content();
            return parseQuestions(response);
        } catch (Exception e) {
            log.warn("AI question generation failed, falling back to Mock: {}", e.getMessage());
            return mockFallback.generateQuestionsWithType(resumeText, targetPosition, questionCount);
        }
    }

    /**
     * 评估单题答案。
     */
    public MockAiService.AnswerEval evaluateAnswer(String question, String userAnswer) {
        try {
            ChatClient client = registry.getDefaultChatClient();
            String prompt = buildEvalPrompt(question, userAnswer);
            String response = client.prompt().user(prompt).call().content();
            return parseEval(response);
        } catch (Exception e) {
            log.warn("AI answer evaluation failed, falling back to Mock: {}", e.getMessage());
            return mockFallback.evaluateAnswer(question, userAnswer);
        }
    }

    /**
     * 生成最终面试评估。
     */
    public InterviewEvaluationEntity generateFinalEvaluation(
            InterviewSessionEntity session,
            List<InterviewAnswerEntity> answers) {
        try {
            ChatClient client = registry.getDefaultChatClient();
            String prompt = buildFinalEvalPrompt(answers);
            String response = client.prompt().user(prompt).call().content();
            return parseFinalEval(response, session);
        } catch (Exception e) {
            log.warn("AI final evaluation failed, falling back to Mock: {}", e.getMessage());
            return mockFallback.generateFinalEvaluation(session, answers);
        }
    }

    // ========== Prompt 构建 ==========

    private String buildQuestionPrompt(String resume, String position, int count) {
        return """
            你是一位资深技术面试官。请根据以下简历内容生成%s道面试题。

            【候选人简历】
            %s

            【目标岗位】%s

            【要求】
            1. 题目类型包括：INTRO（自我介绍类）、TECH（技术类）、PROJECT（项目经验类）、HR（综合素质类）
            2. 难度适中，逐步深入
            3. 用JSON格式返回，格式如下（不要输出其他内容）：
            [{"content":"题目内容","questionType":"INTRO"},{"content":"题目内容","questionType":"TECH"},...]
            """.formatted(count, resume, position);
    }

    private String buildEvalPrompt(String question, String answer) {
        return """
            你是一位资深技术面试官。请对以下面试回答进行评分。

            【问题】%s

            【回答】%s

            【要求】
            用JSON格式返回评分结果（不要输出其他内容）：
            {"score":整數(0-100),"feedback":"评语（中文，30字以内）"}
            """.formatted(question, answer);
    }

    private String buildFinalEvalPrompt(List<InterviewAnswerEntity> answers) {
        StringBuilder qa = new StringBuilder();
        for (InterviewAnswerEntity a : answers) {
            qa.append("Q: ").append(a.getQuestion().getContent()).append("\n");
            qa.append("A: ").append(a.getAnswerText()).append("\n\n");
        }
        return """
            你是一位资深技术面试官。请根据以下面试问答记录，给出综合评估。

            【问答记录】
            %s

            【要求】
            用JSON格式返回评估结果（不要输出其他内容）：
            {
              "totalScore": 整数(0-100),
              "techScore": 整数,
              "communicationScore": 整数,
              "logicScore": 整数,
              "strengths": ["优点1", "优点2"],
              "weaknesses": ["不足1", "不足2"],
              "improvements": ["建议1", "建议2"],
              "summary": "综合评语（中文，50字以内）"
            }
            """.formatted(qa.toString());
    }

    // ========== JSON 解析 ==========

    @SuppressWarnings("unchecked")
    private List<MockAiService.QuestionWithType> parseQuestions(String raw) {
        try {
            String json = extractJsonArray(raw);
            List<Map<String, String>> list = objectMapper.readValue(json, List.class);
            List<MockAiService.QuestionWithType> result = new ArrayList<>();
            for (Map<String, String> m : list) {
                QuestionType type;
                try {
                    type = QuestionType.valueOf(m.get("questionType").toUpperCase());
                } catch (Exception e) {
                    type = QuestionType.TECH;
                }
                result.add(new MockAiService.QuestionWithType(m.get("content"), type));
            }
            return result;
        } catch (Exception e) {
            log.warn("Failed to parse AI question response: {}", e.getMessage());
            throw new RuntimeException("AI题目解析失败", e);
        }
    }

    private MockAiService.AnswerEval parseEval(String raw) {
        try {
            String json = extractJsonObject(raw);
            Map<String, Object> map = objectMapper.readValue(json, Map.class);
            int score = (Integer) map.get("score");
            String feedback = (String) map.get("feedback");
            return new MockAiService.AnswerEval(score, feedback);
        } catch (Exception e) {
            log.warn("Failed to parse AI eval response: {}", e.getMessage());
            throw new RuntimeException("AI评分解析失败", e);
        }
    }

    @SuppressWarnings("unchecked")
    private InterviewEvaluationEntity parseFinalEval(String raw, InterviewSessionEntity session) {
        try {
            String json = extractJsonObject(raw);
            Map<String, Object> map = objectMapper.readValue(json, Map.class);
            return InterviewEvaluationEntity.builder()
                    .session(session)
                    .totalScore((Integer) map.get("totalScore"))
                    .techScore((Integer) map.get("techScore"))
                    .communicationScore((Integer) map.get("communicationScore"))
                    .logicScore((Integer) map.get("logicScore"))
                    .strengths(objectMapper.writeValueAsString(map.get("strengths")))
                    .weaknesses(objectMapper.writeValueAsString(map.get("weaknesses")))
                    .improvements(objectMapper.writeValueAsString(map.get("improvements")))
                    .summary((String) map.get("summary"))
                    .build();
        } catch (Exception e) {
            log.warn("Failed to parse AI final eval response: {}", e.getMessage());
            throw new RuntimeException("AI评估解析失败", e);
        }
    }

    private String extractJsonArray(String raw) {
        String trimmed = raw.trim();
        int start = trimmed.indexOf('[');
        int end = trimmed.lastIndexOf(']');
        if (start >= 0 && end > start) {
            return trimmed.substring(start, end + 1);
        }
        throw new RuntimeException("Response does not contain a JSON array: " + trimmed.substring(0, Math.min(100, trimmed.length())));
    }

    private String extractJsonObject(String raw) {
        String trimmed = raw.trim();
        int start = trimmed.indexOf('{');
        int end = trimmed.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return trimmed.substring(start, end + 1);
        }
        throw new RuntimeException("Response does not contain a JSON object: " + trimmed.substring(0, Math.min(100, trimmed.length())));
    }
}
