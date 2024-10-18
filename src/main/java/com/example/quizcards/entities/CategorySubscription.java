package com.example.quizcards.entities;
import lombok.*;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "category_subscriptions")
public class CategorySubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_subscriptions_id")
    private Long id;

    @Column(name = "category_subscriptions_name", length = 100, nullable = false)
    private String name;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "subscriptions_description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "max_flashcards")
    private Integer maxFlashcards;

    @Column(name = "max_set_per_session")
    private Integer maxSetPerSession;

    @Column(name = "expired_month", nullable = false)
    private int expiredMonth;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}