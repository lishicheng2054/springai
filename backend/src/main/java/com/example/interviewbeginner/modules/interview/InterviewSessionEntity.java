package com.example.interviewbeginner.modules.interview;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 面试会话实体。
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "interview_session")
public class InterviewSessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 会话业务标识（UUID 截断） */
    @Column(nullable = false, unique = true, length = 32)
    private String sessionId;

    /** 关联简历 ID */
    @Column(nullable = false)
    private Long resumeId;

    /** 岗位类型 */
    @Column(nullable = false, length = 50)
    private String roleType;

    /** 题目数量 */
    @Column(nullable = false)
    private Integer questionCount;

    /** 当前题目索引（从 0 开始） */
    @Column(nullable = false)
    @Builder.Default
    private Integer currentQuestionIndex = 0;

    /** 面试模式：TEXT */
    @Column(nullable = false, length = 20)
    @Builder.Default
    private String mode = "TEXT";

    /** 会话状态 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private SessionStatus status = SessionStatus.NEW;

    /** 总分 */
    private Integer totalScore;

    /** 开始时间 */
    private LocalDateTime startedAt;

    /** 结束时间 */
    private LocalDateTime completedAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<InterviewQuestionEntity> questions = new ArrayList<>();

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<InterviewAnswerEntity> answers = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.sessionId == null || this.sessionId.isBlank()) {
            this.sessionId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        }
        if (this.status == null) {
            this.status = SessionStatus.NEW;
        }
        if (this.mode == null || this.mode.isBlank()) {
            this.mode = "TEXT";
        }
    }

    /**
     * 会话状态枚举。
     */
    public enum SessionStatus {
        NEW,
        IN_PROGRESS,
        COMPLETED,
        EVALUATED
    }
}
