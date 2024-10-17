package com.example.quizcards.service;

import com.example.quizcards.dto.request.FlashcardCreationRequest;
import com.example.quizcards.dto.IFlashcardDTO;


import java.util.List;

public interface IFlashcardService {
    List<IFlashcardDTO> getAllBySetId(int id);
    List<IFlashcardDTO> getAll();
    void addFlashcard(String question, String answer,String imageLink,Boolean isApproved, int setId);
    void deleteFlashcard(Long cardId);
    void updateFlashcard(FlashcardCreationRequest request);
    IFlashcardDTO findByCardId(Long cardId);
}
