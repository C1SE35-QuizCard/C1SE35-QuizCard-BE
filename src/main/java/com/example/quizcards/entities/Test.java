package com.example.quizcards.entities;
import jakarta.validation.constraints.AssertTrue;
import lombok.*;
import jakarta.persistence.*;

import java.sql.Time;
import java.sql.Timestamp;

@Entity
@Table(name = "test")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Test {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer testId;

    @Column(name = "test_mode_id")
    private Integer testModeId;

    @Column(name = "set_id")
    private Integer setId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "total_question")
    private Integer totalQuestion;

    @Column(name = "goal_score", nullable = false)
    private Integer goalScore;

    @Column(name = "remaining_time")
    private Time remainingTime;

    @Column(name = "is_testing")
    private Boolean isTesting;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @AssertTrue(message = "Goal score must be greater than 0 and less than total questions")
    public boolean isGoalScoreValid() {
        return goalScore > 0 && goalScore < totalQuestion;
    }
}
