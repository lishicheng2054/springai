package com.example.interviewbeginner.modules.interview;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 答案评估服务。
 * 负责调用 AI 评估单题答案（失败时自动降级到 Mock），并在面试结束时生成综合评估。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnswerEvaluationService {

    private final AiInterviewService aiInterviewService;
    private final InterviewAnswerRepository answerRepository;
    private final InterviewEvaluationRepository evaluationRepository;

    /**
     * 评估单题答案。
     *
     * @param question  题目
     * @param session   会话
     * @param answerText 用户答案
     * @return 保存后的答案实体
     */
    @Transactional
    public InterviewAnswerEntity evaluateAndPersist(
            InterviewQuestionEntity question,
            InterviewSessionEntity session,
            String answerText) {

        MockAiService.AnswerEval eval = aiInterviewService.evaluateAnswer(
                question.getContent(), answerText);

        InterviewAnswerEntity answer = InterviewAnswerEntity.builder()
                .session(session)
                .question(question)
                .answerText(answerText)
                .score(eval.score())
                .feedback(eval.feedback())
                .build();

        InterviewAnswerEntity saved = answerRepository.save(answer);
        log.info("Answer evaluated: sessionId={}, questionIndex={}, score={}",
                session.getSessionId(), question.getQuestionIndex(), eval.score());
        return saved;
    }

    /**
     * 完成面试并生成最终评估。
     *
     * @param session 会话
     * @param answers 所有答案
     * @return 评估实体
     */
    @Transactional
    public InterviewEvaluationEntity completeAndEvaluate(
            InterviewSessionEntity session,
            List<InterviewAnswerEntity> answers) {

        InterviewEvaluationEntity evaluation = aiInterviewService.generateFinalEvaluation(session, answers);
        InterviewEvaluationEntity saved = evaluationRepository.save(evaluation);

        log.info("Interview completed: sessionId={}, totalScore={}",
                session.getSessionId(), saved.getTotalScore());
        return saved;
    }
}
