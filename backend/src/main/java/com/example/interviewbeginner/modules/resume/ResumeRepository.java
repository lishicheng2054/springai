package com.example.interviewbeginner.modules.resume;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 简历 Repository。
 */
@Repository
public interface ResumeRepository extends JpaRepository<ResumeEntity, Long> {

    /**
     * 按创建时间降序查询所有简历。
     */
    List<ResumeEntity> findAllByOrderByCreatedAtDesc();
}
