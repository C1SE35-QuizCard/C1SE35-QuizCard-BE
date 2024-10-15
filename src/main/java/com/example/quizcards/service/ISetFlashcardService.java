package com.example.quizcards.service;

import com.example.quizcards.dto.IFlashcardDTO;
import com.example.quizcards.dto.ISetFlashcardDTO;
import com.example.quizcards.dto.SetFlashcardUpdateRequest;

import java.util.List;

public interface ISetFlashcardService {
    List<IFlashcardDTO> getAllFlashcardBySetId(int setId);
    List<ISetFlashcardDTO> getAll();
    ISetFlashcardDTO findBySetId(int setId);
    void addSetFlashcard(String title, String descriptionSet, Boolean isApproved, Boolean isAnonymous, Boolean sharingMode, Long userId, int categoryId);
    void deleteSetFlashcard(int setId);
    void updateSetFlashcard(SetFlashcardUpdateRequest request);
    List<ISetFlashcardDTO> searchByTitle(String title);
    List<ISetFlashcardDTO> sortByUpdatedDate();
    List<ISetFlashcardDTO> getAllSetByUserId(Long userId);
    List<ISetFlashcardDTO> getAllSetPublic();
    List<ISetFlashcardDTO> getAllSetPublicByUserId(Long userId);
}
