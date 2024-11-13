package com.example.quizcards.service.impl;

import com.example.quizcards.dto.IFlashcardDTO;
import com.example.quizcards.dto.ISetFlashcardDTO;
import com.example.quizcards.dto.request.FlashcardInitializeRequest;
import com.example.quizcards.dto.request.SetFlashcardRequest;
import com.example.quizcards.dto.request.SetFlashcardInitializeRequest;
import com.example.quizcards.dto.response.ApiResponse;
import com.example.quizcards.dto.response.TopCreatorsResponse;
import com.example.quizcards.entities.AppUser;
import com.example.quizcards.entities.CategorySetFlashcard;
import com.example.quizcards.entities.Flashcard;
import com.example.quizcards.entities.SetFlashcard;
import com.example.quizcards.helpers.SetFlashcardHelpers.ISetFlashcardHelpers;
import com.example.quizcards.repository.IFlashcardRepository;
import com.example.quizcards.repository.ISetFlashcardRepository;
import com.example.quizcards.security.UserPrincipal;
import com.example.quizcards.service.ISetFlashcardService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class SetFlashcardServiceImpl implements ISetFlashcardService {

    @Autowired
    private ISetFlashcardHelpers setFlashcardHelpers;

    @Autowired
    private ISetFlashcardRepository setFlashcardRepository;

    @Autowired
    private IFlashcardRepository flashcardRepository;

    @Override
    public List<IFlashcardDTO> getAllFlashcardBySetId(Long setId) {
        return setFlashcardRepository.findAllFlashcardsBySetId(setId);
    }

    @Override
    public List<ISetFlashcardDTO> getAll() {
        return setFlashcardRepository.findAllSetFlashcards();
    }

    @Override
    @Transactional
    public ResponseEntity<?> createNewSetFlashcards(SetFlashcardInitializeRequest request) {
        setFlashcardHelpers.handleAddSetFlashcard(request);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal up = (UserPrincipal) auth.getPrincipal();
        SetFlashcard set = SetFlashcard.builder()
                .title(request.getTitle())
                .descriptionSet(request.getDescriptionSet())
                .isApproved(true)
                .isAnonymous(request.getIsAnonymous())
                .sharingMode(request.getSharingMode())
                .user(AppUser.builder().userId(up.getId()).build())
                .category(CategorySetFlashcard.builder().categoryId(request.getCategoryId()).build())
                .build();

        setFlashcardRepository.save(set);

        List<Flashcard> flashcards = request.getFlashcards()
                .stream().map(dto -> Flashcard.builder()
                        .question(dto.getQuestion())
                        .answer(dto.getAnswer())
                        .imageLink(dto.getImageData())
                        .isApproved(true)
                        .set(SetFlashcard.builder().setId(set.getSetId()).build())
                        .build()).toList();

        flashcardRepository.saveAll(flashcards);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "Created set successfully"));
    }


    @Override
    public ISetFlashcardDTO findBySetId(Long setId) {
        return setFlashcardRepository.findSetFlashcardsById(setId);
    }

    @Override
    public ResponseEntity<?> countSetFlashcardCreatedInCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal up = (UserPrincipal) authentication.getPrincipal();

        ApiResponse apiResponse = new ApiResponse(true, "ok", HttpStatus.OK,
                setFlashcardRepository.countNumberOfSetCreated(up.getId()));

        return ResponseEntity.status(200).body(apiResponse);
    }

    @Override
    public ResponseEntity<?> countSetFlashcardCreatedPerDayInCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal up = (UserPrincipal) authentication.getPrincipal();

        ApiResponse apiResponse = new ApiResponse(true, "ok", HttpStatus.OK,
                setFlashcardRepository.countNumberOfSetCreatedInCurrentDay(up.getId()));

        return ResponseEntity.status(200).body(apiResponse);
    }

    @Override
    public void addSetFlashcard(String title, String descriptionSet, Boolean isApproved, Boolean isAnonymous, Boolean sharingMode, Long userId, Long categoryId) {
        setFlashcardRepository.createSetFlashcard(title, descriptionSet, isApproved, isAnonymous, sharingMode, userId, categoryId);
    }

    @Override
    public void deleteSetFlashcard(Long setId) {
        setFlashcardHelpers.handleDeleteSetFlashcard(setId);
        setFlashcardRepository.deleteSetFlashcardById(setId);
    }

    @Override
    public void updateSetFlashcard(SetFlashcardRequest request) {
        setFlashcardHelpers.handleUpdateSetFlashcard(request);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal up = (UserPrincipal) authentication.getPrincipal();

        setFlashcardRepository.updateSetFlashcard(request.getSetId(), request.getTitle(), request.getDescriptionSet(),
                true,
                request.getIsAnonymous(),
                request.getSharingMode(),
                up.getId(),
                request.getCategoryId()
        );
    }

    @Override
    public List<ISetFlashcardDTO> searchByTitle(String title) {
        return setFlashcardRepository.searchByTitle(title);
    }

    @Override
    public List<ISetFlashcardDTO> sortByUpdatedDate() {
        return setFlashcardRepository.sortByUpdatedDate();
    }

    @Override
    public List<ISetFlashcardDTO> getAllSetByUserId(Long userId) {
        return setFlashcardRepository.findAllSetByUserId(userId);
    }

    @Override
    public List<ISetFlashcardDTO> getAllSetPublic() {
        return setFlashcardRepository.findAllSetPublic();
    }

    @Override
    public List<ISetFlashcardDTO> getAllSetPublicByUserId(Long userId) {
        return setFlashcardRepository.findAllSetPublicByUserId(userId);
    }

    @Override
    public List<ISetFlashcardDTO> loadTop10RecentSetFlashcards(Long userId) {
        return setFlashcardRepository.findTop10RecentSetFlashcards(userId);
    }

    @Override
    public List<ISetFlashcardDTO> loadTop10RelevantByCategory(Long categoryId, Long userId) {
        return setFlashcardRepository.findTop10RelevantByCategory(categoryId, userId);
    }

    @Override
    public List<ISetFlashcardDTO> loadTop10PopularFlashcardSets(Long userId) {
        return setFlashcardRepository.findTop10PopularFlashcardSets(userId);
    }

    @Override
    public List<TopCreatorsResponse> loadTop10PopularCreators() {
        return setFlashcardRepository.findTop10PopularCreators();
    }
}
