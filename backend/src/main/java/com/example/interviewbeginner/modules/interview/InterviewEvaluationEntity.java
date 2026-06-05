package com.example.interviewbeginner.modules.interview;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 面试评估结果实体。
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "interview_evaluation")
public class InterviewEvaluationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 所属面试会话（一对一） */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false, unique = true)
    private InterviewSessionEntity session;

    /** 综合总分 */
    @Column(nullable = false)
    private Integer totalScore;

    /** 技术能力 */
    private Integer techScore;

    /** 沟通表达 */
    private Integer communicationScore;

    /** 逻辑思维 */
    private Integer logicScore;

    /** 优点（JSON 数组字符串） */
    @Column(columnDefinition = "TEXT")
    private String strengths;

    /** 不足（JSON 数组字符串） */
    @Column(columnDefinition = "TEXT")
    private String weaknesses;

    /** 改进建议（JSON 数组字符串） */
    @Column(columnDefinition = "TEXT")
    private String improvements;

    /** 总结评语 */
    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
