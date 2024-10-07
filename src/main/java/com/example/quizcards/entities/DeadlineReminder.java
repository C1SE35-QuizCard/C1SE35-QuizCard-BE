package com.example.quizcards.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.sql.Timestamp;
@Entity
@Table(name = "deadline_reminders")
@Data
public class DeadlineReminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer deadlineRemindersId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "set_id", nullable = false)
    private Integer setId;

    @Column(name = "reminder_time", nullable = false)
    private Timestamp reminderTime;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private AppUser user;

    @ManyToOne
    @JoinColumn(name = "set_id", insertable = false, updatable = false)
    private SetFlashcard setFlashcards;
}
