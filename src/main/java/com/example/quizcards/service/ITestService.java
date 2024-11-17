package com.example.quizcards.service;

import com.example.quizcards.dto.ITestDTO;
import com.example.quizcards.dto.request.TestCreationRequest;
import com.example.quizcards.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ITestService {
    ResponseEntity<?> createTest(Long SetId,
                                 Long UserId,
                                 TestCreationRequest request);

    ResponseEntity<?> deleteTest(Long TestId);
}
