package com.example.quizcards.entities;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "user_progress")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer progressId;

    @ManyToOne
    @JoinColumn(name = "card_id", nullable = false)
    private Flashcard flashcard;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    private Boolean progressType; // Đúng/ Sai hoặc loại tiến trình nào đó
}