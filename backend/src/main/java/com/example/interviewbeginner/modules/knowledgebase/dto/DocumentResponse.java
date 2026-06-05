package com.example.interviewbeginner.modules.knowledgebase.dto;

import com.example.interviewbeginner.modules.knowledgebase.KnowledgeDocumentEntity;

import java.time.LocalDateTime;

/**
 * 文档响应。
 */
public record DocumentResponse(
        Long id,
        Long kbId,
        String fileName,
        String contentType,
        Long fileSize,
        String contentPreview,
        String parseStatus,
        LocalDateTime createdAt
) {
    public static DocumentResponse fromEntity(KnowledgeDocumentEntity e) {
        String preview = e.getContent();
        if (preview != null && preview.length() > 200) {
            preview = preview.substring(0, 200) + "...";
        }
        return new DocumentResponse(
                e.getId(), e.getKbId(), e.getFileName(), e.getContentType(),
                e.getFileSize(), preview, e.getParseStatus(), e.getCreatedAt()
        );
    }
}
