package com.example.interviewbeginner.modules.interview.dto;

import java.util.List;

/**
 * 面试结果响应。
 */
public record InterviewResultResponse(
        Long sessionId,
        String status,
        Integer totalScore,
        Integer techScore,
        Integer communicationScore,
        Integer logicScore,
        List<String> strengths,
        List<String> weaknesses,
        List<String> improvements,
        String summary
) {
}
