package com.example.quizcards.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "test_sub")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class _Subscription implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

//    @Lob
    private String endpoint;

//    @Lob
    @Column(name = "data_keys")
    private String keys; // lưu cả p256dh và auth dưới dạng JSON string
}
