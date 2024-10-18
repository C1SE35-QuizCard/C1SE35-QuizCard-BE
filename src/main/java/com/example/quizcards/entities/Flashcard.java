package com.example.quizcards.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "flashcards")
public class Flashcard implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id", nullable = false)
    private Long cardId;

    @Column(name = "term", nullable = false, length = 930)
    private String term;

    @Column(name = "definition", nullable = false, length = 930)
    private String definition;

    @Column(name = "image_url")
    private String imageLink;

    @Column(name = "is_approved")
    private Boolean isApproved;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "set_id", foreignKey = @ForeignKey(name = "fk_flashcard_set"), nullable = false)
    private SetFlashcard set;
}