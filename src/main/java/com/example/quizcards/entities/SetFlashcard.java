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
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "set_id", nullable = false)
    private Integer setId;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "description_set", columnDefinition = "TEXT")
    private String descriptionSet;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_approved")
    private Boolean isApproved;

    @Column(name = "is_anonymous")
    private Boolean isAnonymous;

    @Column(name = "sharing_mode")
    private Boolean sharingMode;

    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_set_user"))
    private AppUser user;

    @ManyToOne
    @JoinColumn(name = "category_id", foreignKey = @ForeignKey(name = "fk_set_category"))
    private CategorySetFlashcard category;
}