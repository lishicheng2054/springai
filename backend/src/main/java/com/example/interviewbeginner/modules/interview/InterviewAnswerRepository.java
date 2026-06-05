package com.example.interviewbeginner.modules.interview;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 面试答案 Repository。
 */
@Repository
public interface InterviewAnswerRepository extends JpaRepository<InterviewAnswerEntity, Long> {

    /**
     * 按会话 ID 查询所有答案。
     */
    List<InterviewAnswerEntity> findBySessionId(Long sessionId);
}
