package com.example.interviewbeginner.modules.interviewschedule.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 创建面试安排请求。
 */
public record CreateScheduleRequest(
        @NotBlank String title,
        String companyName,
        String roleType,
        @NotNull LocalDateTime startTime,
        LocalDateTime endTime,
        String location,
        String meetingLink,
        String notes
) {
}
