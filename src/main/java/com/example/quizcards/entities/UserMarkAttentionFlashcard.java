package com.example.quizcards.entities;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "user_mark_attention_flashcards")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserMarkAttentionFlashcard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "card_id", nullable = false)
    private Flashcard flashcard;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    private Boolean markedForAttention;
}
