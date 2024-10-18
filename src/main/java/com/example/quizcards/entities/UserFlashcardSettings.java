package com.example.quizcards.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_flashcard_settings", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_set_id", columnList = "set_id")
})
public class UserFlashcardSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long settingId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @ManyToOne
    @JoinColumn(name = "set_id", nullable = false)
    private SetFlashcard setFlashcard;

    @Column(name = "last_card_index", nullable = false)
    @Min(value = 0)
    private int lastCardIndex;

    @Column(name = "shuffle_mode", nullable = false)
    private boolean shuffleMode;

    @Column(name = "flip_card_mode", nullable = false)
    private boolean flipCardMode;

    @Column(name = "last_accessed")
    private LocalDateTime lastAccessed;
}
