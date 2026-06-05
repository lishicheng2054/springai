package com.example.interviewbeginner.modules.resume;

import com.example.interviewbeginner.common.exception.BusinessException;
import com.example.interviewbeginner.common.exception.ErrorCode;
import com.example.interviewbeginner.modules.resume.dto.ResumeRequest;
import com.example.interviewbeginner.modules.resume.dto.ResumeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 简历服务。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeRepository resumeRepository;

    /**
     * 创建简历。
     */
    @Transactional
    public ResumeResponse createResume(ResumeRequest request) {
        ResumeEntity entity = ResumeEntity.builder()
                .candidateName(request.candidateName())
                .targetPosition(request.targetPosition())
                .resumeText(request.resumeText())
                .sourceType(request.sourceType() != null ? request.sourceType() : "TEXT")
                .build();

        ResumeEntity saved = resumeRepository.save(entity);
        log.info("Resume created: id={}, candidate={}", saved.getId(), saved.getCandidateName());
        return ResumeResponse.fromEntity(saved);
    }

    /**
     * 根据 ID 查询简历。
     */
    public ResumeResponse getResume(Long id) {
        ResumeEntity entity = resumeRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESUME_NOT_FOUND, "简历不存在: id=" + id));
        return ResumeResponse.fromEntity(entity);
    }

    /**
     * 查询简历列表。
     */
    public List<ResumeResponse> listResumes() {
        return resumeRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(ResumeResponse::fromEntity)
                .toList();
    }
}
