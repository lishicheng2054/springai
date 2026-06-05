package com.example.interviewbeginner.modules.resume;

import com.example.interviewbeginner.common.result.Result;
import com.example.interviewbeginner.modules.resume.dto.ResumeRequest;
import com.example.interviewbeginner.modules.resume.dto.ResumeResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 简历接口。
 */
@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor

public class ResumeController {

    private final ResumeService resumeService;

    /**
     * 创建简历。
     */
    @PostMapping
    public Result<Map<String, Long>> createResume(@Valid @RequestBody ResumeRequest request) {
        ResumeResponse response = resumeService.createResume(request);
        return Result.success(Map.of("resumeId", response.id()));
    }

    /**
     * 查看简历详情。
     */
    @GetMapping("/{resumeId}")
    public Result<ResumeResponse> getResume(@PathVariable Long resumeId) {
        return Result.success(resumeService.getResume(resumeId));
    }

    /**
     * 查看简历列表。
     */
    @GetMapping
    public Result<List<ResumeResponse>> listResumes() {
        return Result.success(resumeService.listResumes());
    }
}
