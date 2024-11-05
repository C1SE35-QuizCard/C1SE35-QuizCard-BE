package com.example.quizcards.service;

import com.example.quizcards.dto.IFlashcardDTO;
import com.example.quizcards.dto.ISetFlashcardDTO;
import com.example.quizcards.dto.request.SetFlashcardCreationRequest;
import com.example.quizcards.dto.response.TopCreatorsResponse;

import java.util.List;

public interface ISetFlashcardService {
    List<IFlashcardDTO> getAllFlashcardBySetId(Long setId);
    List<ISetFlashcardDTO> getAll();
    void addSetFlashcard(String title,
                         String descriptionSet,
                         Boolean isApproved,
                         Boolean isAnonymous,
                         Boolean sharingMode,
                         Long userId,
                         Long categoryId);
    void deleteSetFlashcard(Long setId);
    void updateSetFlashcard(SetFlashcardCreationRequest request);
    ISetFlashcardDTO findBySetId(Long setId);
    List<ISetFlashcardDTO> searchByTitle(String title);
    List<ISetFlashcardDTO> sortByUpdatedDate();
    List<ISetFlashcardDTO> getAllSetByUserId(Long userId);
    List<ISetFlashcardDTO> getAllSetPublic();
    List<ISetFlashcardDTO> getAllSetPublicByUserId(Long userId);

    List<ISetFlashcardDTO> loadTop10RecentSetFlashcards(Long userId);
    List<ISetFlashcardDTO> loadTop10RelevantByCategory(Long categoryId, Long userId);
    List<ISetFlashcardDTO> loadTop10PopularFlashcardSets(Long userId);
    List<TopCreatorsResponse> loadTop10PopularCreators();
}
