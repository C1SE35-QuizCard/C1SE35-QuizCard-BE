package com.example.quizcards.service.impl;

import com.example.quizcards.dto.request.ExamDetailRequest;
import com.example.quizcards.dto.response.ApiResponse;
import com.example.quizcards.entities.ExamDetail;
import com.example.quizcards.entities.Flashcard;
import com.example.quizcards.entities.Test;
import com.example.quizcards.entities.TestMode;
import com.example.quizcards.helpers.ExamDetailHelpers.IExamDetailHelpers;
import com.example.quizcards.repository.IExamDetailRepository;
import com.example.quizcards.service.IExamDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ExamDetailServiceImpl implements IExamDetailService {

    @Autowired
    IExamDetailRepository examDetailRepository;

    @Autowired
    IExamDetailHelpers examDetailHelpers;

    @Override
    public ResponseEntity<?> addExamDetail(ExamDetailRequest request){
        Flashcard flashcard = Flashcard.builder().cardId(request.getCardId()).build();
        Test test = Test.builder().testId(request.getTestId()).build();
        ExamDetail examDetail = new ExamDetail();
        examDetail.setTest(test);
        examDetail.setFlashcard(flashcard);
        examDetail.setYourAnswer(request.getYourAnswer());
        examDetail.setIsTrue(request.isTrue());

        examDetailRepository.save(examDetail);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "Exam Detail created successfully", HttpStatus.CREATED, examDetail.getExId()));
    }

    @Override
    public ResponseEntity<?> deleteExamDetail(Long ExamDetailId){
        ExamDetail examDetail = examDetailRepository.findById(ExamDetailId)
                .orElseThrow(() -> new RuntimeException("ExamDetail not found for ID: " + ExamDetailId));
        examDetailHelpers.handleDeleteExamDetail(ExamDetailId);
        examDetailRepository.delete(examDetail);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse(true, "Test deleted successfully"));
    }
}
