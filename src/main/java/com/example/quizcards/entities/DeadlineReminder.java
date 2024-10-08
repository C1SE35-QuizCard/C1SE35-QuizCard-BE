package com.example.quizcards.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "deadline_reminders")
public class DeadlineReminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deadline_reminders_id")
    private Integer deadlineRemindersId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "set_id", nullable = false)
    private Integer setId;

    @Column(name = "reminder_time", nullable = false)
    private Timestamp reminderTime;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private AppUser user;

    @ManyToOne
    @JoinColumn(name = "set_id")
    private SetFlashcard setFlashcards;
}
