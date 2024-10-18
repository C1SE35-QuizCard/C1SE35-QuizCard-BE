package com.example.quizcards.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeadlineReminderCreationRequest {
    private int deadlineRemindersId;

    @NotNull(message = "Reminder time cannot be null.")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    private Timestamp reminderTime;

    private Long userId;
    private int setId;
}
