package com.example.interviewbeginner.modules.interviewschedule;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 面试安排 Repository。
 */
@Repository
public interface InterviewScheduleRepository extends JpaRepository<InterviewScheduleEntity, Long> {

    List<InterviewScheduleEntity> findAllByOrderByStartTimeDesc();

    List<InterviewScheduleEntity> findByStartTimeBetweenOrderByStartTimeAsc(
            LocalDateTime from, LocalDateTime to);

    List<InterviewScheduleEntity> findByStatusOrderByStartTimeAsc(
            InterviewScheduleEntity.ScheduleStatus status);
}
