package com.example.interviewbeginner.modules.resume;

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
 * 简历实体。
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "resume")
public class ResumeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 候选人姓名 */
    @Column(nullable = false, length = 50)
    private String candidateName;

    /** 目标岗位 */
    @Column(nullable = false, length = 100)
    private String targetPosition;

    /** 简历文本内容 */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String resumeText;

    /** 简历来源类型：TEXT */
    @Column(nullable = false, length = 20)
    @Builder.Default
    private String sourceType = "TEXT";

    /** 创建时间 */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.sourceType == null || this.sourceType.isBlank()) {
            this.sourceType = "TEXT";
        }
    }
}
