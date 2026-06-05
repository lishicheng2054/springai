package com.example.interviewbeginner.modules.interview;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 面试会话 Repository。
 */
@Repository
public interface InterviewSessionRepository extends JpaRepository<InterviewSessionEntity, Long> {

    /**
     * 根据业务 sessionId 查询。
     */
    Optional<InterviewSessionEntity> findBySessionId(String sessionId);
}
