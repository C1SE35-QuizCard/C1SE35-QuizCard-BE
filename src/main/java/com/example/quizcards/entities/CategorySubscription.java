package com.example.quizcards.entities;
import lombok.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "category_subscriptions")
@Data // Tạo các getter, setter, toString, equals, và hashCode
@NoArgsConstructor // Tạo constructor không tham số
@AllArgsConstructor // Tạo constructor với tất cả các tham số
public class CategorySubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer categorySubscriptionsId;

    @Column(name = "category_subscriptions_name", length = 100)
    private String categorySubscriptionsName;

    @Column(nullable = false)
    private BigDecimal price; // Sử dụng BigDecimal cho giá trị tiền tệ

    @Lob // Đánh dấu trường này là một trường BLOB
    private String subscriptionsDescription;

    private Integer maxFlashcards;

    private Integer maxSetPerSession;

    @Column(name = "expired_month")
    private Integer expiredMonth;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Timestamp updatedAt;
}