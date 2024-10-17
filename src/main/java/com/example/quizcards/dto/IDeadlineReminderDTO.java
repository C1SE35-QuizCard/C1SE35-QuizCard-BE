package com.example.quizcards.dto;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public interface IDeadlineReminderDTO {
    int getDeadlineRemindersId();
    Timestamp getReminderTime();
    Long getUserId();
    int getSetId();
}
