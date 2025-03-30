package com.example.quizcards.service;


import com.example.quizcards.dto.IFlashcardDTO;
import com.example.quizcards.dto.request.FlashcardRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IFlashcardService {
    List<IFlashcardDTO> getAllBySetId(Long id);

    List<IFlashcardDTO> getAll();

    void addFlashcard(String question, String answer, String imageLink, Boolean isApproved, Long setId);

    void deleteFlashcard(Long cardId);

    void updateFlashcard(FlashcardRequest request);

    ResponseEntity<?> addFlashcard_2(FlashcardRequest request);

    ResponseEntity<?> updateFlashcard_2(FlashcardRequest request);

    void deleteFlashcard_2(Long cardId, Long setId);

    IFlashcardDTO findByCardId(Long cardId);
    List<IFlashcardDTO> getRandomFlashcardsBySetId(Long setId);
}
