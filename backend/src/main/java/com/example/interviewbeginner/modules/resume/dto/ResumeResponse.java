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
        String fileName,
        String fileType,
        Long fileSize,
        LocalDateTime createdAt
) {
    public static ResumeResponse fromEntity(ResumeEntity entity) {
        return new ResumeResponse(
                entity.getId(),
                entity.getCandidateName(),
                entity.getTargetPosition(),
                entity.getResumeText(),
                entity.getSourceType(),
                entity.getFileName(),
                entity.getFileType(),
                entity.getFileSize(),
                entity.getCreatedAt()
        );
    }
}
