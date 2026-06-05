package com.example.interviewbeginner.modules.interview;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 面试评估 Repository。
 */
@Repository
public interface InterviewEvaluationRepository extends JpaRepository<InterviewEvaluationEntity, Long> {

    /**
     * 按会话 ID 查询评估结果。
     */
    Optional<InterviewEvaluationEntity> findBySessionId(Long sessionId);
}
