package com.example.quizcards.service;

import com.example.quizcards.dto.request.TestCreationRequest;
import org.springframework.http.ResponseEntity;

public interface ITestService {
    ResponseEntity<?> createTest(Long SetId,
                                 Long UserId,
                                 TestCreationRequest request);

    ResponseEntity<?> deleteTest(Long TestId);

    ResponseEntity<?> createMultipleChoiceTest(Long testId);

    ResponseEntity<?> createEssayTest(Long testId);

}
