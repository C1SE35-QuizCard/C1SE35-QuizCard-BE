package com.example.quizcards.entities.TestDataPackage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "test_exams")
@CompoundIndex(name = "testId_userId_idx", def = "{'testId': 1, 'userId': 1}") // Index gộp
public class TestData {
    @Id
    private String id; // MongoDB ObjectId

    @Indexed
    private Long testId;

    @Indexed
    private Long userId;

    private String testType;

    private List<IQuestion> questions;
}
