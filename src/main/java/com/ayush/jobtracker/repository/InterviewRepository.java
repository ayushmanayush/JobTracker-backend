package com.ayush.jobtracker.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ayush.jobtracker.entity.Interview;
import com.ayush.jobtracker.entity.ReminderStatus;

public interface InterviewRepository extends JpaRepository<Interview,Long>{
    List<Interview> findByApplicationIdOrderByRoundAsc(Long id);
    boolean existsByApplicationIdAndRound(Long applicationId, int round);
    boolean existsByApplicationIdAndCompletedAtIsNull(Long applicationId);
    List<Interview> findByScheduledAtLessThanEqualAndReminderStatus(LocalDateTime time, ReminderStatus status);
    List<Interview> findByApplicationUserIdAndCompletedAtIsNull(Long userId);




        @Query("""
        SELECT i FROM Interview i
        WHERE i.completedAt IS NULL
        AND i.scheduledAt <= :targetTime
        AND (
            i.reminderStatus = com.ayush.jobtracker.entity.ReminderStatus.PENDING
        OR (
                i.reminderStatus = com.ayush.jobtracker.entity.ReminderStatus.PROCESSING
                AND i.reminderProcessedAt <= :retryTime
            )
        )
        ORDER BY i.scheduledAt ASC
        """)
        List<Interview> findEligibleReminders(
                @Param("targetTime") LocalDateTime targetTime,
                @Param("retryTime") LocalDateTime retryTime,
                Pageable pageable
        );
}
