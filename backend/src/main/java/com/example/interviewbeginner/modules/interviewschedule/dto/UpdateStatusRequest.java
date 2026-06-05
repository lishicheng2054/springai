package com.example.interviewbeginner.modules.interviewschedule.dto;

import com.example.interviewbeginner.modules.interviewschedule.InterviewScheduleEntity.ScheduleStatus;
import jakarta.validation.constraints.NotNull;

/**
 * 更新面试状态请求。
 */
public record UpdateStatusRequest(
        @NotNull ScheduleStatus status
) {
}
