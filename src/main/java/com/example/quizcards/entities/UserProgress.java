package com.example.quizcards.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_progress", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_card_id", columnList = "card_id"),
        @Index(name = "idx_user_id_card_id", columnList = "card_id", unique = true),
        @Index(name = "idx_progress_type", columnList = "progress_type"),
        @Index(name = "idx_marked_for_attention", columnList = "marked_for_attention")
})
public class UserProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "progress_id")
    private Long progressId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private Flashcard flashcard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser appUser;

    @Column(name = "progress_type")
    private Boolean progressType;

    @Column(name = "marked_for_attention")
    private Boolean isAttention;
}