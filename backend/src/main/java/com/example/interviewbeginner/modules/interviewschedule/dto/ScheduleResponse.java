package com.example.interviewbeginner.modules.interviewschedule.dto;

import com.example.interviewbeginner.modules.interviewschedule.InterviewScheduleEntity;
import java.time.LocalDateTime;

/**
 * 面试安排响应。
 */
public record ScheduleResponse(
        Long id,
        String title,
        String companyName,
        String roleType,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String status,
        String location,
        String meetingLink,
        String notes,
        LocalDateTime createdAt
) {
    public static ScheduleResponse fromEntity(InterviewScheduleEntity e) {
        return new ScheduleResponse(
                e.getId(), e.getTitle(), e.getCompanyName(), e.getRoleType(),
                e.getStartTime(), e.getEndTime(), e.getStatus().name(),
                e.getLocation(), e.getMeetingLink(), e.getNotes(), e.getCreatedAt()
        );
    }
}
