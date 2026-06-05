package com.example.interviewbeginner.modules.interviewschedule.dto;

import java.time.LocalDateTime;

/**
 * 更新面试安排请求（所有字段可选）。
 */
public record UpdateScheduleRequest(
        String title,
        String companyName,
        String roleType,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String location,
        String meetingLink,
        String notes
) {
}
