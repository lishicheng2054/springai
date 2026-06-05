package com.example.interviewbeginner.modules.resume.dto;

import com.example.interviewbeginner.modules.resume.ResumeEntity;

import java.time.LocalDateTime;

/**
 * 简历响应体。
 */
public record ResumeResponse(
        Long id,
        String candidateName,
        String targetPosition,
        String resumeText,
        String sourceType,
        LocalDateTime createdAt
) {
    /**
     * 从实体转换为响应体。
     */
    public static ResumeResponse fromEntity(ResumeEntity entity) {
        return new ResumeResponse(
                entity.getId(),
                entity.getCandidateName(),
                entity.getTargetPosition(),
                entity.getResumeText(),
                entity.getSourceType(),
                entity.getCreatedAt()
        );
    }
}
