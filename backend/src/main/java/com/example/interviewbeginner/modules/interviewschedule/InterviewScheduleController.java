package com.example.interviewbeginner.modules.interviewschedule;

import com.example.interviewbeginner.common.result.Result;
import com.example.interviewbeginner.modules.interviewschedule.dto.CreateScheduleRequest;
import com.example.interviewbeginner.modules.interviewschedule.dto.ScheduleResponse;
import com.example.interviewbeginner.modules.interviewschedule.dto.UpdateScheduleRequest;
import com.example.interviewbeginner.modules.interviewschedule.dto.UpdateStatusRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 面试安排接口。
 */
@RestController
@RequestMapping("/api/interview-schedules")
@RequiredArgsConstructor
public class InterviewScheduleController {

    private final InterviewScheduleService service;

    @PostMapping
    public Result<ScheduleResponse> create(@Valid @RequestBody CreateScheduleRequest req) {
        return Result.success(service.createSchedule(req));
    }

    @GetMapping
    public Result<List<ScheduleResponse>> list() {
        return Result.success(service.listSchedules());
    }

    @GetMapping("/{scheduleId}")
    public Result<ScheduleResponse> get(@PathVariable Long scheduleId) {
        return Result.success(service.getSchedule(scheduleId));
    }

    @PutMapping("/{scheduleId}")
    public Result<ScheduleResponse> update(
            @PathVariable Long scheduleId,
            @RequestBody UpdateScheduleRequest req) {
        return Result.success(service.updateSchedule(scheduleId, req));
    }

    @PatchMapping("/{scheduleId}/status")
    public Result<ScheduleResponse> updateStatus(
            @PathVariable Long scheduleId,
            @Valid @RequestBody UpdateStatusRequest req) {
        return Result.success(service.updateStatus(scheduleId, req.status()));
    }

    @DeleteMapping("/{scheduleId}")
    public Result<Map<String, String>> delete(@PathVariable Long scheduleId) {
        service.deleteSchedule(scheduleId);
        return Result.success(Map.of("status", "deleted"));
    }
}
