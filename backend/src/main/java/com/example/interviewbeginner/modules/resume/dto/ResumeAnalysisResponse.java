package com.example.interviewbeginner.modules.resume.dto;

import java.util.List;

/**
 * 简历AI分析响应。
 */
public record ResumeAnalysisResponse(
        Long resumeId,
        String summary,
        List<String> skills,
        List<String> advantages,
        List<String> risks,
        List<String> suggestions,
        String analysisStatus
) {
}
