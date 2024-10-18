package com.example.quizcards.service;

import com.example.quizcards.dto.IFlashcardDTO;
import com.example.quizcards.dto.ISetFlashcardDTO;
import com.example.quizcards.dto.request.SetFlashcardCreationRequest;

import java.util.List;

public interface ISetFlashcardService {
    List<IFlashcardDTO> getAllFlashcardBySetId(Long setId);
    List<ISetFlashcardDTO> getAll();
<<<<<<< HEAD
    ISetFlashcardDTO findBySetId(int setId);
    void addSetFlashcard(String title, String descriptionSet, Boolean isApproved, Boolean isAnonymous, Boolean sharingMode, Long userId, int categoryId);
    void deleteSetFlashcard(int setId);
    void updateSetFlashcard(SetFlashcardCreationRequest request);
=======
    ISetFlashcardDTO findBySetId(Long setId);
    void addSetFlashcard(String title,
                         String descriptionSet,
                         Boolean isApproved,
                         Boolean isAnonymous,
                         Boolean sharingMode,
                         Long userId,
                         Long categoryId);
    void deleteSetFlashcard(Long setId);
    void updateSetFlashcard(SetFlashcardUpdateRequest request);
>>>>>>> 753d91ca62348b0fe2477bdd7995d53e87a2673e
    List<ISetFlashcardDTO> searchByTitle(String title);
    List<ISetFlashcardDTO> sortByUpdatedDate();
    List<ISetFlashcardDTO> getAllSetByUserId(Long userId);
    List<ISetFlashcardDTO> getAllSetPublic();
    List<ISetFlashcardDTO> getAllSetPublicByUserId(Long userId);
}
