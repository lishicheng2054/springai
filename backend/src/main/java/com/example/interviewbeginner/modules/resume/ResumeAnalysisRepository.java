package com.example.interviewbeginner.modules.resume;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResumeAnalysisRepository extends JpaRepository<ResumeAnalysisEntity, Long> {
    Optional<ResumeAnalysisEntity> findByResumeId(Long resumeId);
}
