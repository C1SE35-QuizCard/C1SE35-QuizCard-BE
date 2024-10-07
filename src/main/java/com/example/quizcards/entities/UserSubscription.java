package com.example.quizcards.entities;
import lombok.*;
import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "user_subscriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userSubscriptionsId;

    private Long userId;

    @Column(name = "category_subscriptions_id", nullable = false)
    private Integer categorySubscriptionsId;

    private Timestamp expiredDate;

    @Enumerated(EnumType.STRING)
    private StatusPaid statusPaid;

    public enum StatusPaid {
        Unpaid,
        Pending,
        Paid
    }
}