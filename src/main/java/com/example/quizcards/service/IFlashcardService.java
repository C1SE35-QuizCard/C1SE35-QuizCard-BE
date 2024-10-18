package com.example.quizcards.service;

import com.example.quizcards.dto.request.FlashcardCreationRequest;
import com.example.quizcards.dto.IFlashcardDTO;


import java.util.List;

public interface IFlashcardService {
    List<IFlashcardDTO> getAllBySetId(Long id);
    List<IFlashcardDTO> getAll();
    void addFlashcard(String term, String definition,String imageLink,Boolean isApproved, Long setId);
    void deleteFlashcard(Long cardId);
    void updateFlashcard(FlashcardCreationRequest request);
    IFlashcardDTO findByCardId(Long cardId);
}
