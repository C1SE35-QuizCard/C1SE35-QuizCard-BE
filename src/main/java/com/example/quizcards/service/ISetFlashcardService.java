package com.example.quizcards.service;

import com.example.quizcards.dto.FlashcardSetDTO;
import com.example.quizcards.dto.IFlashcardDTO;
import com.example.quizcards.dto.ISetFlashcardDTO;
import com.example.quizcards.dto.request.QueryDTO;
import com.example.quizcards.dto.request.SetFlashcardInitializeRequest;
import com.example.quizcards.dto.request.SetFlashcardRequest;
import com.example.quizcards.dto.response.SearchSetFlashResponse;
import com.example.quizcards.dto.response.ITopCreatorsResponse;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ISetFlashcardService {
    List<IFlashcardDTO> getAllFlashcardBySetId(Long setId);

    ResponseEntity<?> getAllFlashcardBySetId_2(Long setId);


    List<ISetFlashcardDTO> getAll();

    ResponseEntity<?> getListFlashcardsByNearbySetting(Long userId, Long limit);

    void addSetFlashcard(String title,
                         String descriptionSet,
                         Boolean isApproved,
                         Boolean isAnonymous,
                         Boolean sharingMode,
                         Long userId,
                         Long categoryId);

    void deleteSetFlashcard(Long setId);

    void updateSetFlashcard(SetFlashcardRequest request);

    Long createNewSetFlashcards(SetFlashcardInitializeRequest request);

    ISetFlashcardDTO findBySetId(Long setId);

    ResponseEntity<?> findBySetId_2(Long setId);

    ResponseEntity<?> countSetFlashcardCreatedPublic(Long userId);

    ResponseEntity<?> countSetFlashcardCreatedPublicByUserName(String userName);

    ResponseEntity<?> countSetFlashcardCreatedInCurrentUser();

    ResponseEntity<?> countSetFlashcardCreatedPerDayInCurrentUser();

    List<SearchSetFlashResponse> searchByTitleAndCategory(String title);

    List<ISetFlashcardDTO> sortByUpdatedDate();

    List<ISetFlashcardDTO> getAllSetByUserId();

    List<ISetFlashcardDTO> getAllSetPublic();

    List<ISetFlashcardDTO> getAllSetPublicByUserId(Long userId);

    List<ISetFlashcardDTO> loadTop10RecentSetFlashcards(Long userId);

    List<ISetFlashcardDTO> loadTop10RelevantByCategory(Long categoryId, Long userId);

    List<FlashcardSetDTO> loadTop10PopularFlashcardSets(Long userId);

    List<ITopCreatorsResponse> loadTop10PopularCreators();
    boolean existsBySetFlashcard_SetIdAndSetFlashcard_SharingMode(Long setId);

    long countFlashcardsBySetId(Long setId);
    List<SearchSetFlashResponse> searchByMyCourse(QueryDTO queryDTO,Long userId);
}
