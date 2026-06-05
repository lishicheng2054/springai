package com.example.interviewbeginner.modules.interviewschedule;

import com.example.interviewbeginner.common.exception.BusinessException;
import com.example.interviewbeginner.common.exception.ErrorCode;
import com.example.interviewbeginner.modules.interviewschedule.InterviewScheduleEntity.ScheduleStatus;
import com.example.interviewbeginner.modules.interviewschedule.dto.CreateScheduleRequest;
import com.example.interviewbeginner.modules.interviewschedule.dto.ScheduleResponse;
import com.example.interviewbeginner.modules.interviewschedule.dto.UpdateScheduleRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 面试安排服务。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InterviewScheduleService {

    private final InterviewScheduleRepository repository;

    public List<ScheduleResponse> listSchedules() {
        return repository.findAllByOrderByStartTimeDesc().stream()
                .map(ScheduleResponse::fromEntity).toList();
    }

    public ScheduleResponse getSchedule(Long id) {
        return ScheduleResponse.fromEntity(findOrThrow(id));
    }

    @Transactional
    public ScheduleResponse createSchedule(CreateScheduleRequest req) {
        InterviewScheduleEntity e = InterviewScheduleEntity.builder()
                .title(req.title())
                .companyName(req.companyName())
                .roleType(req.roleType())
                .startTime(req.startTime())
                .endTime(req.endTime())
                .location(req.location())
                .meetingLink(req.meetingLink())
                .notes(req.notes())
                .build();
        e = repository.save(e);
        log.info("Schedule created: id={}, title={}", e.getId(), e.getTitle());
        return ScheduleResponse.fromEntity(e);
    }

    @Transactional
    public ScheduleResponse updateSchedule(Long id, UpdateScheduleRequest req) {
        InterviewScheduleEntity e = findOrThrow(id);
        if (req.title() != null) e.setTitle(req.title());
        if (req.companyName() != null) e.setCompanyName(req.companyName());
        if (req.roleType() != null) e.setRoleType(req.roleType());
        if (req.startTime() != null) e.setStartTime(req.startTime());
        if (req.endTime() != null) e.setEndTime(req.endTime());
        if (req.location() != null) e.setLocation(req.location());
        if (req.meetingLink() != null) e.setMeetingLink(req.meetingLink());
        if (req.notes() != null) e.setNotes(req.notes());
        e = repository.save(e);
        return ScheduleResponse.fromEntity(e);
    }

    @Transactional
    public ScheduleResponse updateStatus(Long id, ScheduleStatus status) {
        InterviewScheduleEntity e = findOrThrow(id);
        e.setStatus(status);
        e = repository.save(e);
        log.info("Schedule status updated: id={}, status={}", id, status);
        return ScheduleResponse.fromEntity(e);
    }

    @Transactional
    public void deleteSchedule(Long id) {
        findOrThrow(id);
        repository.deleteById(id);
        log.info("Schedule deleted: id={}", id);
    }

    private InterviewScheduleEntity findOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND,
                        "面试安排不存在: id=" + id));
    }
}
