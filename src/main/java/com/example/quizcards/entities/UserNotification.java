package com.example.quizcards.entities;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "user_notification")
@Data
public class UserNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "is_read")
    private Boolean isRead;

    @ManyToOne
    @JoinColumn(name = "notifications_id", nullable = false)
    private Notification notifications;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;
}
