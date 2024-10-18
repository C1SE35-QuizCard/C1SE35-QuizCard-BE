package com.example.quizcards.repository;

import com.example.quizcards.dto.IDeadlineReminderDTO;
import com.example.quizcards.entities.DeadlineReminder;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IDeadlineReminderRepository extends JpaRepository<DeadlineReminder, Integer> {

    @Query(value = """
            select d.deadline_reminders_id, d.reminder_time, a.user_id, s.set_id
            from deadline_reminders d, app_users a, set_flashcards s
            where d.deadline_reminders_id = :deadline_reminders_id and d.user_id = a.user_id and d.set_id = s.set_id
            """, nativeQuery = true)
    IDeadlineReminderDTO findDeadlineReminderById(@Param("deadline_reminders_id") Long deadlineRemindersId);

    @Query(value = """
            select d.deadline_reminders_id, d.reminder_time, a.user_id, s.set_id
            from deadline_reminders d, app_users a, set_flashcards s
            where a.user_id = :user_id and d.user_id = a.user_id and d.set_id = s.set_id
            order by d.reminder_time asc
            """, nativeQuery = true)
    List<IDeadlineReminderDTO> findDeadlineReminderByUserId(@Param("user_id") Long UserId);

    @Query(value = """
            select d.deadline_reminders_id, d.reminder_time, a.user_id, s.set_id
            from deadline_reminders d, app_users a, set_flashcards s
            where s.set_id = :set_id and d.user_id = a.user_id and d.set_id = s.set_id
            order by d.reminder_time asc
            """, nativeQuery = true)
    List<IDeadlineReminderDTO> findDeadlineReminderBySetId(@Param("set_id") Long SetId);


    @Modifying
    @Transactional
    @Query(value = """
            insert into deadline_reminders(reminder_time, user_id, set_id)
            values (:reminder_time, :user_id, :set_id)
            """, nativeQuery = true)
    void createDeadlineReminder(@Param("reminder_time") Timestamp reminderTime,
                                @Param("user_id") Long userId,
                                @Param("set_id") Long setId);

    @Modifying
    @Transactional
    @Query(value = """
            delete from deadline_reminders d
            where d.deadline_reminders_id = :deadline_reminders_id
            """, nativeQuery = true)
    void deleteDeadlineReminder(@Param("deadline_reminders_id") Long deadlineRemindersId);

    @Modifying
    @Transactional
    @Query(value = """
            update deadline_reminders d
            set d.reminder_time = :reminder_time, d.user_id = :user_id, d.set_id = :set_id
            where d.deadline_reminders_id = :deadline_reminders_id
            """, nativeQuery = true)
    void updateDeadlineReminder(@Param("deadline_reminders_id") Long deadlineRemindersId,
                                    @Param("reminder_time") Timestamp reminderTime,
                                    @Param("user_id") Long userId,
                                    @Param("set_id") Long setId);

    @Query(value = """
            select count(d.deadline_reminders_id)
            from deadline_reminders d
            where d.user_id = :user_id and d.set_id = :set_id
            """, nativeQuery = true)
    int existsByUserIdAndSetId(@Param("user_id") Long userId, @Param("set_id") Long setId);

    @Query(value = """
            SELECT COUNT(*)
            FROM deadline_reminders
            WHERE user_id = :user_id AND set_id = :set_id AND deadline_reminders_id <> :deadline_reminders_id
            """, nativeQuery = true)
    int existsByUserIdAndSetIdAndNotId(@Param("user_id") Long userId, @Param("set_id") Long setId, @Param("deadline_reminders_id") Long deadlineRemindersId);
}
