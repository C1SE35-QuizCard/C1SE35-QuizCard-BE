package com.example.quizcards.entities;
import lombok.*;
import jakarta.persistence.*;
import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_subscriptions")
public class UserSubscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_subscriptions_id")
    private Long userSubscriptionsId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser appUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_subscriptions_id", nullable = false)
    private CategorySubscription categorySubscription;

    @Column(name = "expired_date")
    private Timestamp expiredDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_paid", nullable = false)
    private StatusPaid statusPaid;
    public enum StatusPaid {
        UNPAID, PENDING, PAID
    }
}