package com.example.interviewbeginner.modules.interview;

import com.example.interviewbeginner.modules.interview.MockAiService.QuestionWithType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 面试题目服务。
 * 负责调用 AI 生成题目并持久化（失败时自动降级到 Mock）。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InterviewQuestionService {

    private final AiInterviewService aiInterviewService;

    /**
     * 生成题目列表并绑定到指定会话。
     *
     * @param session        面试会话
     * @param resumeText     简历文本
     * @param targetPosition 目标岗位
     * @param questionCount  题目数量
     * @return 题目实体列表
     */
    @Transactional
    public List<InterviewQuestionEntity> generateAndPersist(
            InterviewSessionEntity session,
            String resumeText,
            String targetPosition,
            int questionCount) {

        List<QuestionWithType> generated = aiInterviewService.generateQuestionsWithType(
                resumeText, targetPosition, questionCount);

        List<InterviewQuestionEntity> entities = new ArrayList<>();
        for (int i = 0; i < generated.size(); i++) {
            QuestionWithType qt = generated.get(i);
            InterviewQuestionEntity entity = InterviewQuestionEntity.builder()
                    .session(session)
                    .questionIndex(i)
                    .questionType(qt.questionType())
                    .content(qt.content())
                    .difficulty("mid")
                    .build();
            entities.add(entity);
        }

        log.info("Generated {} questions for sessionId={}", entities.size(), session.getSessionId());
        return entities;
    }
}
