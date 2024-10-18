package com.example.quizcards.service;

import com.example.quizcards.dto.IDeadlineReminderDTO;
import com.example.quizcards.dto.request.DeadlineReminderCreationRequest;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public interface IDeadlineReminderService {
    IDeadlineReminderDTO getDeadlineReminderById(int id);
    List<IDeadlineReminderDTO> getDeadlineReminderByUserId(Long userId);
    List<IDeadlineReminderDTO> getDeadlineReminderBySetId(int setId);
    void addDeadlineReminder(Timestamp reminderTime, Long userId, int setId);
    void deleteDeadlineReminder(int deadlineRemindersId);
    void updateDeadlineReminder(DeadlineReminderCreationRequest request);
    boolean existsByUserIdAndSetId(Long userId, int setId);
    boolean existsByUserIdAndSetIdAndNotId(Long userId, int setId, int deadlineRemindersId);
}
