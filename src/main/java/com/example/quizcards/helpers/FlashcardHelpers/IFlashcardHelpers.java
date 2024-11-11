package com.example.quizcards.helpers.FlashcardHelpers;

import com.example.quizcards.dto.request.FlashcardCreateRequest;
import com.example.quizcards.dto.request.FlashcardInitializeRequest;
import com.example.quizcards.dto.request.FlashcardRequest;


public interface IFlashcardHelpers {
    void handleAddFlashcard(FlashcardCreateRequest request);

    void handleUpdateFlashcard(FlashcardRequest request);

    void handleDeleteFlashcard(Long cardId, Long setId);

    void handleAdminAddFlashcard(FlashcardCreateRequest request, Long userId);

    void handleAdminUpdateFlashcard(FlashcardRequest request, Long userId);

    void handleAdminDeleteFlashcard(Long cardId, Long setId, Long userId);
}
