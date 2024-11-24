package com.example.quizcards.dto;


import java.time.LocalDateTime;

public interface IDeadlineReminderDTO {
    Long getDeadlineRemindersId();

    LocalDateTime getReminderTime();

    Long getUserId();

    Long getSetId();

    String getTitle();

    int getCardCount();
}
