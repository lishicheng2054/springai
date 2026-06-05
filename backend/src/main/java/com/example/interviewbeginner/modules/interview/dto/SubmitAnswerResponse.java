package com.example.interviewbeginner.modules.interview.dto;

/**
 * 提交答案响应。
 * <p>
 * 有两种情况：
 * <ol>
 *   <li>hasNext=true：返回下一题</li>
 *   <li>hasNext=false：返回结果ID，面试结束</li>
 * </ol>
 */
public record SubmitAnswerResponse(
        boolean hasNext,
        NextQuestion nextQuestion,
        Long resultId
) {

    public record NextQuestion(
            Long questionId,
            String content
    ) {
    }

    /**
     * 还有下一题。
     */
    public static SubmitAnswerResponse next(Long questionId, String content) {
        return new SubmitAnswerResponse(true, new NextQuestion(questionId, content), null);
    }

    /**
     * 面试结束。
     */
    public static SubmitAnswerResponse finished(Long resultId) {
        return new SubmitAnswerResponse(false, null, resultId);
    }
}
