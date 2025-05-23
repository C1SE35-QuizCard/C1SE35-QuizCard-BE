package com.example.quizcards.service.impl;

import com.example.quizcards.dto.FlashcardSetDTO;
import com.example.quizcards.dto.IFlashcardDTO;
import com.example.quizcards.dto.ISetFlashcardDTO;
import com.example.quizcards.dto.request.QueryDTO;
import com.example.quizcards.dto.request.SetFlashcardInitializeRequest;
import com.example.quizcards.dto.request.SetFlashcardRequest;
import com.example.quizcards.dto.response.ITopCreatorsResponse;
import com.example.quizcards.dto.response.SearchSetFlashResponse;
import com.example.quizcards.entities.*;
import com.example.quizcards.exception.AccessDeniedException;
import com.example.quizcards.exception.BadRequestException;
import com.example.quizcards.exception.ResourceNotFoundException;
import com.example.quizcards.helpers.SetFlashcardHelpers.ISetFlashcardHelpers;
import com.example.quizcards.repository.IFlashcardRepository;
import com.example.quizcards.repository.ISetFlashcardRepository;
import com.example.quizcards.security.UserPrincipal;
import com.example.quizcards.service.IAppUserService;
import com.example.quizcards.service.ISetFlashcardService;
import com.example.quizcards.service.ITagService;
import com.example.quizcards.utils.HandleString;
import com.example.quizcards.utils.RedisUtils;
import com.github.benmanes.caffeine.cache.Cache;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Future;

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

    @Autowired
    private ITagService tagService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    @Qualifier("securityContextExecutor")
    private AsyncTaskExecutor taskExecutor;

    @Override
    public SetFlashcard findById(Long setId) {
        return setFlashcardRepository.findById(setId)
                .orElseThrow(() -> new ResourceNotFoundException("Set", "id", setId));
    }

    @Override
    public List<IFlashcardDTO> getAllFlashcardBySetId(Long setId) {
        return setFlashcardRepository.findAllFlashcardsBySetId(setId);
    }

    @Override
    public List<IFlashcardDTO> getAllFlashcardBySetId(Long setId, String requestPassword) {
        checkAccess(setId, requestPassword);
        return setFlashcardRepository.findAllFlashcardsBySetId(setId);
    }

    @Override
    public List<ISetFlashcardDTO> getAll() {
        return setFlashcardRepository.findAllSetFlashcards();
    }

    @Override
    public Page<ISetFlashcardDTO> getAllSetFlashcardsWithPagination(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size);
        return setFlashcardRepository.findAllSetFlashcardsWithPagination(search, pageable);
    }

    @Override
    public List<ISetFlashcardDTO> getAllLimit(int limitData) {
        return setFlashcardRepository.findAllSetFlashcardsLimit(limitData);
    }

    @Override
    public List<ISetFlashcardDTO> getListFlashcardsByNearbySetting(Long userId, Long limit) {
        return setFlashcardRepository.findAllSetPublicNearbySettings(userId, limit);
    }

    @Override
    @Transactional
    public Long initSetFlashcard(SetFlashcardInitializeRequest request) {
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

        String hashedPassword = (request.getHashPassword() == null || request.getHashPassword().trim().isEmpty())
                ? null
                : passwordEncoder.encode(request.getHashPassword());

        // Add tags if provided
        if (request.getTagNames() != null && !request.getTagNames().isEmpty()) {
            if (request.getTagNames().size() > 5) {
                throw new BadRequestException("A set flashcard can have at most 5 tags");
            }
            Set<Tag> tags = tagService.getOrCreateTags(request.getTagNames());
            set.setTags(tags);
        }

        set.setHashPassword(hashedPassword);

        setFlashcardRepository.saveAndFlush(set);

        List<Flashcard> flashcards = request.getFlashcards()
                .stream().map(dto -> Flashcard.builder()
                        .question(HandleString.popExtraNewLineAndSpace(dto.getQuestion()))
                        .answer(HandleString.popExtraNewLineAndSpace(dto.getAnswer()))
                        .imageLink(dto.getImageLink())
                        .isApproved(true)
                        .set(SetFlashcard.builder().setId(set.getSetId()).build())
                        .build()).toList();

        flashcardRepository.saveAllAndFlush(flashcards);

        return set.getSetId();
    }


    @Override
    public ISetFlashcardDTO findBySetId(Long setId) {
        return setFlashcardRepository.findSetFlashcardsById(setId);
    }

    @Override
    public ISetFlashcardDTO findBySetIdPublish(Long setId) {
        Long userId = Long.MIN_VALUE;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof UserPrincipal up) {
            userId = up.getId();
        }
        ISetFlashcardDTO result = setFlashcardRepository.findSetFlashcardsById(setId);
        if (result == null) {
            return null;
        }
        if (!Objects.equals(result.getUserId(), userId) && !result.getSharingMode()) {
            throw new AccessDeniedException("Set cannot access by you");
        }
        return result;
    }

    @Override
    public Integer countSetFlashcardCreatedPublic(Long userId) {
        return setFlashcardRepository.countAllSetPublicByUserId(userId);
    }

    @Override
    public Integer countSetFlashcardCreatedPublicByUserName(String userName) {
//        return ResponseEntity.ok().body(
//                new ApiResponse(true, "...", HttpStatus.OK,
//                        setFlashcardRepository.countAllSetPublicByUserName(userName))
//        );
        return setFlashcardRepository.countAllSetPublicByUserName(userName);
    }

    @Override
    public Integer countSetFlashcardCreatedInCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal up = (UserPrincipal) authentication.getPrincipal();

        return setFlashcardRepository.countNumberOfSetCreated(up.getId());
    }

    @Override
    public Integer countSetFlashcardCreatedPerDayInCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal up = (UserPrincipal) authentication.getPrincipal();

        return setFlashcardRepository.countNumberOfSetCreatedInCurrentDay(up.getId());
    }

    @Override
    public Map<String, Object> analysisUser(Long userId) {
        long startTime = System.currentTimeMillis();
        System.out.println("Start analysis user data, current millis: " + startTime);
        Future<Integer> setCardCreatedFut = taskExecutor.submit(() ->
                setFlashcardRepository.countNumberOfSetCreated(userId));
        Future<Integer> setCardCreatedPerDayFut = taskExecutor.submit(() ->
                setFlashcardRepository.countNumberOfSetCreatedInCurrentDay(userId));

        try {
            Integer setCardCreated = setCardCreatedFut.get();
            Integer setCardCreatedPerDay = setCardCreatedPerDayFut.get();
            long endTime = System.currentTimeMillis();
            System.out.println("End analysis user data, time range: " + (endTime - startTime) + " millis");
            return Map.of(
                    "setCardCreated", setCardCreated,
                    "setCardCreatedPerDay", setCardCreatedPerDay
            );
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error while analyzing user data",
                    null
            );
        }
    }

    @Override
    public Integer countNumberOfSetCreated(Long userId) {
        return setFlashcardRepository.countNumberOfSetCreated(userId);
    }

    @Override
    public Integer countNumberOfSetCreatedInCurrentDay(Long userId) {
        return setFlashcardRepository.countNumberOfSetCreatedInCurrentDay(userId);
    }

    @Override
    public Page<ISetFlashcardDTO> filterByUserIdAndCategoryName(Long userId, String categoryName, int page, int size) {
        if (page < 0) {
            throw new BadRequestException("Page must be greater than or equal to 0");
        }
        if (size < 1 || size > 100) {
            throw new BadRequestException("Size must be greater than 0 and less than or equal to 100");
        }
        if (userId == null && !StringUtils.hasText(categoryName)) {
            throw new BadRequestException("Unknown user and category to filter");
        }

        Pageable pageable = PageRequest.of(page, size);

        return setFlashcardRepository.filterByUserIdAndCategoryName(userId, categoryName, pageable);
    }

    @Override
    public List<SearchSetFlashResponse> searchByTitleAndCategory(String title) {
        return setFlashcardRepository.searchByTitleAndCategory(title);
    }

    @Override
    @Transactional
    public void addSetFlashcard(String title, String descriptionSet, Boolean isApproved, Boolean
            isAnonymous, Boolean sharingMode, String hashPassword, Long userId, Long categoryId, Set<String> tagNames) {
        if (appUserService.findById(userId).isEmpty()) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        SetFlashcard setFlashcard = new SetFlashcard();
        setFlashcard.setTitle(title);
        setFlashcard.setDescriptionSet(descriptionSet);
        setFlashcard.setIsApproved(isApproved);
        setFlashcard.setIsAnonymous(isAnonymous);
        setFlashcard.setSharingMode(sharingMode);
        setFlashcard.setUser(appUserService.findById(userId).orElse(null));
        setFlashcard.setCategory(CategorySetFlashcard.builder().categoryId(categoryId).build());

        // Add tags if provided
        if (tagNames != null && !tagNames.isEmpty()) {
            Set<Tag> tags = tagService.getOrCreateTags(tagNames);
            setFlashcard.setTags(tags);
        }

        setFlashcardRepository.save(setFlashcard);
    }

    @Override
    @Transactional
    public void deleteSetFlashcardAdmin(Long setId) {
        setFlashcardRepository.deleteSetFlashcardById(setId);
        @SuppressWarnings("unchecked")
        Cache<Object, Object> tokenMetaCache =
                (Cache<Object, Object>) cacheManager.getCache("setPassInfo").getNativeCache();
        tokenMetaCache.invalidate("set_" + setId);
    }

    @Override
    @Transactional
    public void updateSetFlashcardAdmin(SetFlashcardRequest request) {
        if (appUserService.findById(request.getUserId()).isEmpty()) {
            throw new ResourceNotFoundException("User", "id", request.getUserId());
        }
        String hashedPassword = (request.getHashPassword() == null || request.getHashPassword().trim().isEmpty())
                ? null
                : passwordEncoder.encode(request.getHashPassword());
        setFlashcardRepository.updateSetFlashcard(request.getSetId(), request.getTitle(), request.getDescriptionSet(),
                request.getIsApproved(),
                request.getIsAnonymous(),
                request.getSharingMode(),
                hashedPassword,
                request.getUserId(),
                request.getCategoryId()
        );
    }

    @Override
    @Transactional
    public void deleteSetFlashcard(Long setId) {
        setFlashcardHelpers.handleDeleteSetFlashcard(setId);
        setFlashcardRepository.deleteSetFlashcardById(setId);
        @SuppressWarnings("unchecked")
        Cache<Object, Object> tokenMetaCache =
                (Cache<Object, Object>) cacheManager.getCache("setPassInfo").getNativeCache();
        tokenMetaCache.invalidate("set_" + setId);
    }

    @Override
    @Transactional
    public void updateSetFlashcard(SetFlashcardRequest request) {
        setFlashcardHelpers.handleUpdateSetFlashcard(request);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal up = (UserPrincipal) authentication.getPrincipal();

//        String hashedPassword = (request.getHashPassword() == null || request.getHashPassword().trim().isEmpty())
//                ? null
//                : passwordEncoder.encode(request.getHashPassword());

        setFlashcardRepository.updateSetFlashcard(request.getSetId(), request.getTitle(), request.getDescriptionSet(),
                true,
                request.getIsAnonymous(),
                request.getSharingMode(),
                null, // hashedPassword = null
                up.getId(),
                request.getCategoryId()
        );
    }

    @Override
    @Transactional
    public void updatePasswordSet(Long setId, String oldPassword, String newPassword, Boolean logoutAllSession) {
        SetFlashcard setFlashcard = setFlashcardRepository.findById(setId)
                .orElseThrow(() -> new ResourceNotFoundException("Set", "id", setId));

        UserPrincipal up = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!Objects.equals(setFlashcard.getUser().getUserId(), up.getId())) {
            throw new AccessDeniedException("You are not the owner of this set");
        }

        if (setFlashcard.getHashPassword() == null || setFlashcard.getHashPassword().isEmpty()) {
            if (StringUtils.hasText(oldPassword)) {
                throw new BadRequestException("Error old password is not needed");
            }
        }
        else if (!StringUtils.hasText(oldPassword) || !passwordEncoder.matches(oldPassword, setFlashcard.getHashPassword())) {
            throw new BadRequestException("Old password is incorrect");
        }

        String hashedNewPassword = newPassword == null
                ? null : passwordEncoder.encode(newPassword);
        setFlashcard.setHashPassword(hashedNewPassword);
        setFlashcardRepository.save(setFlashcard);
        setFlashcardRepository.flush();
        if (Boolean.TRUE.equals(logoutAllSession)) {
            redisUtils.deleteKey(MessageFormat.format("set:{0}:pass_checked", setId));
            @SuppressWarnings("unchecked")
            Cache<Object, Object> tokenMetaCache =
                    (Cache<Object, Object>) cacheManager.getCache("validatedSets").getNativeCache();
            tokenMetaCache.invalidate(MessageFormat.format("set:{0}:pass_checked_{1}", setId, up.getId()));
        }
        @SuppressWarnings("unchecked")
        Cache<Object, Object> passCache =
                (Cache<Object, Object>) cacheManager.getCache("setPassInfo").getNativeCache();
        passCache.put("set_" + setId, setFlashcard);
    }

    @Override
    public List<ISetFlashcardDTO> sortByUpdatedDate() {
        return setFlashcardRepository.sortByUpdatedDate();
    }

    @Override
    public List<ISetFlashcardDTO> getAllSetByCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal up = (UserPrincipal) auth.getPrincipal();
        return setFlashcardRepository.findAllSetByUserId(up.getId());
    }

    @Override
    public List<ISetFlashcardDTO> getAllSetPublic() {
        return setFlashcardRepository.findAllSetPublic();
    }

    @Override
    public List<ISetFlashcardDTO> getAllPublicSet(Long userId) {
        return setFlashcardRepository.findAllSetPublic(userId);
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
    public List<SearchSetFlashResponse> searchByMyCourse(QueryDTO queryDTO, Long userId) {
        return setFlashcardRepository.searchByMyCourse(queryDTO.getTitle(), userId);
    }

    @Override
    public Page<ISetFlashcardDTO> filterByTagName(String tagName, Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return setFlashcardRepository.filterByTagName(tagName, userId, pageable);
    }

    @Override
    public void checkAccess(Long setId, String requestPassword) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = null;

        if (auth != null && auth.getPrincipal() instanceof UserPrincipal) {
            UserPrincipal up = (UserPrincipal) auth.getPrincipal();
            userId = up.getId();
        }

        SetFlashcard set = setFlashcardRepository.findById(setId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SetFlashcard not found"));

        // Nếu set thuộc về người dùng hiện tại, không cần kiểm tra mật khẩu
        if (set.getUser().getUserId().equals(userId)) {
            return;
        }

        // Nếu set có mật khẩu (không rỗng hoặc null), kiểm tra password nhập vào
        if (set.getHashPassword() != null && !set.getHashPassword().isEmpty()) {
            if (requestPassword == null || !passwordEncoder.matches(requestPassword, set.getHashPassword())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid password or unauthorized access");
            }
        }
    }

    @Override
    public List<ISetFlashcardDTO> getAllSetByUser(Long userId) {
        return setFlashcardRepository.findSetCardByUserId(userId);
    }

    @Override
    public Page<ISetFlashcardDTO> getAllSetByUserWithPagination(
            Long userId,
            int page,
            int size,
            String sortedBy,
            boolean asc
    ) {
        Sort sort = Sort.by(asc ? Sort.Direction.ASC : Sort.Direction.DESC, sortedBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        return setFlashcardRepository.findSetCardByUserId(userId, pageable);
    }
}