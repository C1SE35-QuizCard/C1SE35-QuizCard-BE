package com.example.quizcards.entities;
import lombok.*;
import jakarta.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "test_mode")
public class TestMode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "test_mode_id")
    private Long testModeId;

    @Column(name = "test_mode_name")
    private String testModeName;
}
