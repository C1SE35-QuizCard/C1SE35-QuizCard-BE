package com.example.quizcards.service.impl;

import com.example.quizcards.dto.FlashcardSetDTO;
import com.example.quizcards.dto.IFlashcardDTO;
import com.example.quizcards.dto.ISetFlashcardDTO;
import com.example.quizcards.dto.request.QueryDTO;
import com.example.quizcards.dto.request.SetFlashcardInitializeRequest;
import com.example.quizcards.dto.request.SetFlashcardRequest;
import com.example.quizcards.dto.response.ApiResponse;
import com.example.quizcards.dto.response.SearchSetFlashResponse;
import com.example.quizcards.dto.response.ITopCreatorsResponse;
import com.example.quizcards.entities.AppUser;
import com.example.quizcards.entities.CategorySetFlashcard;
import com.example.quizcards.entities.Flashcard;
import com.example.quizcards.entities.SetFlashcard;
import com.example.quizcards.exception.AccessDeniedException;
import com.example.quizcards.exception.ResourceNotFoundException;
import com.example.quizcards.helpers.SetFlashcardHelpers.ISetFlashcardHelpers;
import com.example.quizcards.repository.IFlashcardRepository;
import com.example.quizcards.repository.ISetFlashcardRepository;
import com.example.quizcards.security.UserPrincipal;
import com.example.quizcards.service.IAppUserService;
import com.example.quizcards.service.ISetFlashcardService;
import com.example.quizcards.utils.HandleString;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class SetFlashcardServiceImpl implements ISetFlashcardService {

    @Autowired
    private ISetFlashcardHelpers setFlashcardHelpers;

    @Autowired
    private ISetFlashcardRepository setFlashcardRepository;

    @Autowired
    private IFlashcardRepository flashcardRepository;

    @Autowired
    private IAppUserService appUserService;

    @Override
    public List<IFlashcardDTO> getAllFlashcardBySetId(Long setId) {
        return setFlashcardRepository.findAllFlashcardsBySetId(setId);
    }

    @Override
    public ResponseEntity<?> getAllFlashcardBySetId_2(Long setId) {
        return null;
    }

//    @Override
//    public ResponseEntity<?> getAllFlashcardBySetId_2(Long setId) {
//        Long userId = Long.MIN_VALUE;
//        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof UserPrincipal) {
//            UserPrincipal up = (UserPrincipal) auth.getPrincipal();
//            userId = up.getId();
//        }
//        if (setId == null) {
//            throw new BadRequestException("Set id is null.");
//        }
//        SetFlashcard set = setFlashcardRepository.findById(setId).orElseThrow(
//                () -> new ResourceNotFoundException("Set", "id", setId)
//        );
//        if (!userId.equals(set.getUser().getUserId()) && !set.getSharingMode()) {
//            throw new AccessDeniedException("Set cannot access by you");
//        }
//        List<IFlashcardDTO> results = setFlashcardRepository.findAllFlashcardsBySetId(setId);
//        if (results.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
//                    new ApiResponse(false, "This set does not contain any cards.",
//                            HttpStatus.NOT_FOUND, null)
//            );
//        }
//        return ResponseEntity.status(HttpStatus.OK).body(
//                new ApiResponse(false, "Get data ok.",
//                        HttpStatus.OK, results)
//        );
//    }


    @Override
    public List<ISetFlashcardDTO> getAll() {
        return setFlashcardRepository.findAllSetFlashcards();
    }

    @Override
    public ResponseEntity<?> getListFlashcardsByNearbySetting(Long userId, Long limit) {
        return ResponseEntity.ok(setFlashcardRepository.findAllSetPublicNearbySettings(userId, limit));
    }

    @Override
    @Transactional
    public Long createNewSetFlashcards(SetFlashcardInitializeRequest request) {
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
                        .question(HandleString.popExtraNewLineAndSpace(dto.getQuestion()))
                        .answer(HandleString.popExtraNewLineAndSpace(dto.getAnswer()))
                        .imageLink(dto.getImageLink())
                        .isApproved(true)
                        .set(SetFlashcard.builder().setId(set.getSetId()).build())
                        .build()).toList();

        flashcardRepository.saveAll(flashcards);

        return set.getSetId();
    }


    @Override
    public ISetFlashcardDTO findBySetId(Long setId) {
        return setFlashcardRepository.findSetFlashcardsById(setId);
    }

    @Override
    public ResponseEntity<?> findBySetId_2(Long setId) {
        Long userId = Long.MIN_VALUE;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof UserPrincipal) {
            UserPrincipal up = (UserPrincipal) auth.getPrincipal();
            userId = up.getId();
        }
        ISetFlashcardDTO result = setFlashcardRepository.findSetFlashcardsById(setId);
        if (result == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponse(false, "This set does not exist or this set does not contain any cards.",
                            HttpStatus.NOT_FOUND, null)
            );
        }
        if (!Objects.equals(result.getUserId(), userId) && !result.getSharingMode()) {
            throw new AccessDeniedException("Set cannot access by you");
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                new ApiResponse(true, "Get data ok.",
                        HttpStatus.OK, result)
        );
    }

    @Override
    public ResponseEntity<?> countSetFlashcardCreatedPublic(Long userId) {
        return ResponseEntity.ok().body(
                new ApiResponse(true, "...", HttpStatus.OK,
                        setFlashcardRepository.countAllSetPublicByUserId(userId))
        );
    }

    @Override
    public ResponseEntity<?> countSetFlashcardCreatedPublicByUserName(String userName) {
        return ResponseEntity.ok().body(
                new ApiResponse(true, "...", HttpStatus.OK,
                        setFlashcardRepository.countAllSetPublicByUserName(userName))
        );
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
    public List<SearchSetFlashResponse> searchByTitleAndCategory(String title) {
        return setFlashcardRepository.searchByTitleAndCategory(title);
    }

    @Override
    @Transactional
    public void addSetFlashcard(String title, String descriptionSet, Boolean isApproved, Boolean isAnonymous, Boolean sharingMode, Long userId, Long categoryId) {
        if (appUserService.findById(userId).isEmpty()) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        setFlashcardRepository.createSetFlashcard(title, descriptionSet, isApproved, isAnonymous, sharingMode, userId, categoryId);
    }

    @Override
    @Transactional
    public void deleteSetFlashcardAdmin(Long setId) {
        setFlashcardRepository.deleteSetFlashcardById(setId);
    }

    @Override
    @Transactional
    public void updateSetFlashcardAdmin(SetFlashcardRequest request) {
        if (appUserService.findById(request.getUserId()).isEmpty()) {
            throw new ResourceNotFoundException("User", "id", request.getUserId());
        }
        setFlashcardRepository.updateSetFlashcard(request.getSetId(), request.getTitle(), request.getDescriptionSet(),
                request.getIsApproved(),
                request.getIsAnonymous(),
                request.getSharingMode(),
                request.getUserId(),
                request.getCategoryId()
        );
    }

    @Override
    @Transactional
    public void deleteSetFlashcard(Long setId) {
        setFlashcardHelpers.handleDeleteSetFlashcard(setId);
        setFlashcardRepository.deleteSetFlashcardById(setId);
    }

    @Override
    @Transactional
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
    public List<ISetFlashcardDTO> sortByUpdatedDate() {
        return setFlashcardRepository.sortByUpdatedDate();
    }

    @Override
    public List<ISetFlashcardDTO> getAllSetByUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal up = (UserPrincipal) auth.getPrincipal();
        return setFlashcardRepository.findAllSetByUserId(up.getId());
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
    public List<FlashcardSetDTO> loadTop10PopularFlashcardSets(Long userId) {
        return setFlashcardRepository.findTopFlashcardSets();
    }

    @Override
    public List<ITopCreatorsResponse> loadTop10PopularCreators() {
        return setFlashcardRepository.findTop10PopularCreators();
    }

    @Override
    public boolean existsBySetFlashcard_SetIdAndSetFlashcard_SharingMode(Long setId) {
        Long exist = setFlashcardRepository.existsBySetIdAndSharingModeTrue(setId);
        if (exist == null) {
            return false;
        }
        return true;
    }

    @Override
    public long countFlashcardsBySetId(Long setId) {
        return setFlashcardRepository.countFlashcardsBySetId(setId);
    }

    @Override
    public List<SearchSetFlashResponse> searchByMyCourse(QueryDTO queryDTO,Long userId) {
        return setFlashcardRepository.searchByMyCourse(queryDTO.getTitle(),userId);
    }
}
