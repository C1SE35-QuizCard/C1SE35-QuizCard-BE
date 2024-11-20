package com.example.quizcards.service;

import com.example.quizcards.dto.request.ExamDetailRequest;
import org.springframework.http.ResponseEntity;

public interface IExamDetailService {
    ResponseEntity<?> addExamDetail(ExamDetailRequest request);

    ResponseEntity<?> deleteExamDetail(Long ExamDetailId);
}
