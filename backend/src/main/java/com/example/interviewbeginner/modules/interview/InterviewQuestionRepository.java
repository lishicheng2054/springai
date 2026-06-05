package com.example.interviewbeginner.modules.interview;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 面试题目 Repository。
 */
@Repository
public interface InterviewQuestionRepository extends JpaRepository<InterviewQuestionEntity, Long> {

    /**
     * 按会话 ID 和题目索引查询。
     */
    List<InterviewQuestionEntity> findBySessionIdOrderByQuestionIndexAsc(Long sessionId);
}
