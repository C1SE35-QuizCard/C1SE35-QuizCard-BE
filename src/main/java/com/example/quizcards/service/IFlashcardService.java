package com.example.quizcards.service;

import com.example.quizcards.dto.request.FlashcardCreateRequest;
import com.example.quizcards.dto.request.FlashcardInitializeRequest;
import com.example.quizcards.dto.request.FlashcardRequest;
import com.example.quizcards.dto.IFlashcardDTO;


import java.util.List;

public interface IFlashcardService {
    List<IFlashcardDTO> getAllBySetId(Long id);

    List<IFlashcardDTO> getAll();

    void addFlashcard(String question, String answer, String imageLink, Boolean isApproved, Long setId);

    void deleteFlashcard(Long cardId);

    void updateFlashcard(FlashcardRequest request);

    void addFlashcard_2(FlashcardCreateRequest request);

    void deleteFlashcard_2(Long setId, Long cardId);

    IFlashcardDTO findByCardId(Long cardId);
}
