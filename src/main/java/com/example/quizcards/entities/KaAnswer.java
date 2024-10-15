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
@Table(name = "ka_answers")
public class KaAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    private Long answerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private KaPlayer kaPlayer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ka_question_id", nullable = false)
    private KaQuestion kaQuestion;

    @Column(name = "player_answer", length = 255, nullable = false)
    private String playerAnswer;

    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect;

    @Column(name = "answer_time")
    private Timestamp answerTime;

    @Column(name = "answered_first")
    private Boolean answeredFirst;

    @Column(name = "is_fastest")
    private Boolean isFastest;

    @Column(name = "round_number")
    private Integer roundNumber;

}
