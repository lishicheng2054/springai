package com.example.interviewbeginner.modules.knowledgebase.dto;

import java.util.List;

/**
 * RAG 问答响应。
 */
public record ChatResponse(
        String answer,
        List<Source> sources
) {
    public record Source(
            Long documentId,
            String fileName,
            String snippet
    ) {
    }
}
