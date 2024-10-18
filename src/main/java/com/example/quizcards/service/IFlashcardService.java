package com.example.quizcards.service;

import com.example.quizcards.dto.FlashcardUpdateRequest;
import com.example.quizcards.dto.IFlashcardDTO;


import java.util.List;

public interface IFlashcardService {
    List<IFlashcardDTO> getAllBySetId(Long id);
    List<IFlashcardDTO> getAll();
    void addFlashcard(String question, String answer,String imageLink,Boolean isApproved, Long setId);
    void deleteFlashcard(Long cardId);
    void updateFlashcard(FlashcardUpdateRequest request);
    IFlashcardDTO findByCardId(Long cardId);
}
