package com.example.quizcards.entities;
import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "test_mode")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestMode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer testModeId;

    @Column(name = "test_mode_name", nullable = false)
    private String testModeName;
}
