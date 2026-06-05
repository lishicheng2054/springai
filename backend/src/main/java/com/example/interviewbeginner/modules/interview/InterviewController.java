package com.example.interviewbeginner.modules.interview;

import com.example.interviewbeginner.common.result.Result;
import com.example.interviewbeginner.modules.interview.dto.CreateSessionRequest;
import com.example.interviewbeginner.modules.interview.dto.CreateSessionResponse;
import com.example.interviewbeginner.modules.interview.dto.InterviewResultResponse;
import com.example.interviewbeginner.modules.interview.dto.SubmitAnswerRequest;
import com.example.interviewbeginner.modules.interview.dto.SubmitAnswerResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 模拟面试接口。
 */
@RestController
@RequestMapping("/api/interviews")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewSessionService sessionService;

    /**
     * 创建面试会话，返回第一题。
     */
    @PostMapping("/sessions")
    public Result<CreateSessionResponse> createSession(
            @Valid @RequestBody CreateSessionRequest request) {
        return Result.success(sessionService.createSession(request));
    }

    /**
     * 提交答案，返回下一题或最终结果 ID。
     */
    @PostMapping("/sessions/{sessionId}/answers")
    public Result<SubmitAnswerResponse> submitAnswer(
            @PathVariable String sessionId,
            @Valid @RequestBody SubmitAnswerRequest request) {
        return Result.success(sessionService.submitAnswer(sessionId, request));
    }

    /**
     * 查看会话详情。
     */
    @GetMapping("/sessions/{sessionId}")
    public Result<Map<String, Object>> getSession(@PathVariable String sessionId) {
        InterviewSessionEntity session = sessionService.getSession(sessionId);
        return Result.success(Map.of(
                "sessionId", session.getSessionId(),
                "resumeId", session.getResumeId(),
                "roleType", session.getRoleType(),
                "status", session.getStatus().name(),
                "questionCount", session.getQuestionCount(),
                "currentQuestionIndex", session.getCurrentQuestionIndex(),
                "totalScore", session.getTotalScore() != null ? session.getTotalScore() : 0
        ));
    }

    /**
     * 获取最终评分和总结。
     */
    @GetMapping("/sessions/{sessionId}/result")
    public Result<InterviewResultResponse> getResult(@PathVariable String sessionId) {
        return Result.success(sessionService.getResult(sessionId));
    }
}
