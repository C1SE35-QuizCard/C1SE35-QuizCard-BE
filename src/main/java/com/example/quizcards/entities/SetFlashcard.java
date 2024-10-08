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
@Table(name = "set_flashcards")
public class SetFlashcard implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "set_id")
    private int setId;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(name = "description_set", columnDefinition = "TEXT")
    private String descriptionSet;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updatedAt;

    @Column(name = "is_approved")
    private Boolean isApproved;

    @Column(name = "is_anonymous")
    private Boolean isAnonymous;

    @Column(name = "sharing_mode")
    private Boolean sharingMode;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private AppUser user;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "category_id")
    private CategorySetFlashcard category;
}