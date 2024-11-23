package com.example.quizcards.service.impl;

import com.example.quizcards.dto.IFlashcardDTO;
import com.example.quizcards.dto.request.EssayTestRequest;
import com.example.quizcards.dto.request.MultipleChoiceTestRequest;
import com.example.quizcards.dto.request.TestCreationRequest;
import com.example.quizcards.dto.response.ApiResponse;
import com.example.quizcards.entities.*;
import com.example.quizcards.helpers.TestHelpers.ITestHelpers;
import com.example.quizcards.repository.IFlashcardRepository;
import com.example.quizcards.repository.ITestRepository;
import com.example.quizcards.service.ISetFlashcardService;
import com.example.quizcards.service.ITestModeService;
import com.example.quizcards.service.ITestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class TestServiceImpl implements ITestService {
    @Autowired
    private ITestRepository testRepository;
    @Autowired
    private ISetFlashcardService setFlashcardService;
    @Autowired
    private ITestModeService testModeService;
    @Autowired
    private ITestHelpers testHelpers;
    @Autowired
    private IFlashcardRepository flashcardRepository;

    @Override
    public ResponseEntity<?> createTest(Long setId, Long userId, TestCreationRequest request){
        long flashcardCount = setFlashcardService.countFlashcardsBySetId(setId);
        TestMode testMode = TestMode.builder().testModeId(request.getTestModeId()).build();
        AppUser au = AppUser.builder().userId(userId).build();
        SetFlashcard set = SetFlashcard.builder().setId(setId).build();

        if (testModeService.exists(request.getTestModeId()) < 1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, "Test mode ID " + request.getTestModeId() + " does not exist."));
        }
        if(request.getTotalQuestion() > flashcardCount){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, "Total questions cannot exceed the number of available flashcards: " + flashcardCount));
        }
        if (request.getTotalQuestion() < request.getGoalScore()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, "Total questions (" + request.getTotalQuestion() + ") cannot be less than the goal score (" + request.getGoalScore() + ")."));
        }
        Test test = new Test();
        test.setTestMode(testMode);
        test.setUser(au);
        test.setSetFlashcards(set);
        test.setTotalQuestion(request.getTotalQuestion());
        test.setGoalScore(request.getGoalScore());
        test.setRemainingTime(request.getRemainingTime());
        test.setIsTesting(true);
        test.setCreatedAt(LocalDateTime.now());

        testRepository.save(test);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "Test created successfully", HttpStatus.CREATED, test.getTestId()));
    }


    @Override
    public ResponseEntity<?> deleteTest(Long TestId){
        Test test = testRepository.findTestId(TestId);
        testHelpers.handleDeleteTest(TestId);
        testRepository.delete(test);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse(true, "Test deleted successfully"));
    }

    @Override
    public ResponseEntity<?> createMultipleChoiceTest(Long testId){
        Test test = testRepository.findById(testId).orElseThrow();
        List<IFlashcardDTO> flashcards = flashcardRepository.findAllFlashcardsBySetId(test.getSetFlashcards().getSetId());
        List<MultipleChoiceTestRequest> questions = new ArrayList<>();

        Collections.shuffle(flashcards);

        // Chọn số lượng câu hỏi cần thiết
        List<IFlashcardDTO> selectedFlashcards = flashcards.subList(0, test.getTotalQuestion());

        for (IFlashcardDTO flashcard : selectedFlashcards) {
            MultipleChoiceTestRequest question = new MultipleChoiceTestRequest();
            question.setTestId(test.getTestId());
            question.setCardId(flashcard.getCardId());
            question.setQuestion(flashcard.getQuestion());
            question.setCorrectAnswer(flashcard.getAnswer());

            List<String> inCorrectAnswers = new ArrayList<>();

            for (IFlashcardDTO otherFlashcard : flashcards) {
                if (!otherFlashcard.getCardId().equals(flashcard.getCardId())) {
                    inCorrectAnswers.add(otherFlashcard.getAnswer());
                }
            }

            if (inCorrectAnswers.size() > 3) {
                Collections.shuffle(inCorrectAnswers);
                inCorrectAnswers = inCorrectAnswers.subList(0, 3);
            }

            Collections.shuffle(inCorrectAnswers);
            question.setIncorrectAnswers(inCorrectAnswers);
            questions.add(question);
        }
        return ResponseEntity.ok(questions);
    }

    @Override
    public ResponseEntity<?> createEssayTest(Long testId){
        Test test = testRepository.findById(testId).orElseThrow();
        List<IFlashcardDTO> flashcards = flashcardRepository.findAllFlashcardsBySetId(test.getSetFlashcards().getSetId());
        List<EssayTestRequest> questions = new ArrayList<>();

        Collections.shuffle(flashcards);

        List<IFlashcardDTO> selectedFlashcards = flashcards.subList(0, test.getTotalQuestion());

        for (IFlashcardDTO flashcard : selectedFlashcards) {
            EssayTestRequest question = new EssayTestRequest();
            question.setTestId(test.getTestId());
            question.setCardId(flashcard.getCardId());
            question.setQuestion(flashcard.getQuestion());
            question.setCorrectAnswer(flashcard.getAnswer());

            questions.add(question);
        }
        return ResponseEntity.ok(questions);
    }

}
