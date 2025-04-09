package com.example.quizcards.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "streak_analysis", indexes = {
        @Index(name = "idx_streak_analysis_user_id", columnList = "user_id")
})
public class StreakAnalysis implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    AppUser user;

    @Column
    Long longestStreak;

    @Column
    Long currentStreak;

    @Column
    Long dayLearned;
}
