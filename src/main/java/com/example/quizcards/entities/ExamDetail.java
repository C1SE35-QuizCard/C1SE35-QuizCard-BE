package com.example.quizcards.entities;
import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "exam_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer mulId;

    @Column(name = "test_id")
    private Integer testId;

    @Column(name = "flashcard_id")
    private Long flashcardId;

    @Column(name = "your_answer", length = 1850)
    private String yourAnswer;

    @Column(name = "is_true")
    private Boolean isTrue;
}
