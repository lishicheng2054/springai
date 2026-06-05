package com.example.interviewbeginner.modules.knowledgebase;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 知识库文档实体。
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "knowledge_document")
public class KnowledgeDocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 所属知识库 ID */
    @Column(name = "kb_id", nullable = false)
    private Long kbId;

    /** 原始文件名 */
    @Column(name = "file_name", nullable = false, length = 500)
    private String fileName;

    /** 文件类型 */
    @Column(name = "content_type", length = 100)
    private String contentType;

    /** 文件大小（字节） */
    @Column(name = "file_size")
    private Long fileSize;

    /** 解析后的文本内容 */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /** 解析状态 */
    @Column(name = "parse_status", nullable = false, length = 20)
    @Builder.Default
    private String parseStatus = "SUCCESS";

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.parseStatus == null) this.parseStatus = "SUCCESS";
    }
}
