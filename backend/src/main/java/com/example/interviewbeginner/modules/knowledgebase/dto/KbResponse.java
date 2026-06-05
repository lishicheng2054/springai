package com.example.interviewbeginner.modules.knowledgebase.dto;

import com.example.interviewbeginner.modules.knowledgebase.KnowledgeBaseEntity;

import java.time.LocalDateTime;

/**
 * 知识库响应。
 */
public record KbResponse(
        Long id,
        String name,
        String description,
        String status,
        Integer docCount,
        LocalDateTime createdAt
) {
    public static KbResponse fromEntity(KnowledgeBaseEntity e) {
        return new KbResponse(
                e.getId(), e.getName(), e.getDescription(),
                e.getStatus(), e.getDocCount(), e.getCreatedAt()
        );
    }
}
