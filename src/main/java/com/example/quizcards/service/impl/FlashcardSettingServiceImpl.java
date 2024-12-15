package com.example.quizcards.service.impl;

import com.example.quizcards.dto.SortListDTO;
import com.example.quizcards.dto.request.FlashcardSettingRequest;
import com.example.quizcards.dto.response.FlashcardSettingResponse;
import com.example.quizcards.entities.AppUser;
import com.example.quizcards.entities.SetFlashcard;
import com.example.quizcards.entities.UserFlashcardSetting;
import com.example.quizcards.exception.ResourceNotFoundException;
import com.example.quizcards.repository.IAppUserRepository;
import com.example.quizcards.repository.IFlashcardSettingRepository;
import com.example.quizcards.repository.ISetFlashcardRepository;
import com.example.quizcards.security.UserPrincipal;
import com.example.quizcards.service.IFlashcardSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class FlashcardSettingServiceImpl implements IFlashcardSettingService {
    @Autowired
    private IFlashcardSettingRepository flashcardSettingRepository;
    @Autowired
    private ISetFlashcardRepository setFlashcardRepository;

    @Autowired
    private IAppUserRepository appUserRepository;
    @Override
    public List<SortListDTO> getSortedFlashcards(String sortBy) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal up = (UserPrincipal) authentication.getPrincipal();
        if ("recent".equals(sortBy)) {
            return flashcardSettingRepository.findAllRecentByUserId(up.getId());
        } else if ("created".equals(sortBy)) {
            return flashcardSettingRepository.findSortedByCreated(up.getId());
        } else if ("learned".equals(sortBy)) {
            return flashcardSettingRepository.findSortedByLearned(up.getId());
        }
        return new ArrayList<>();
    }
    @Override
    public ResponseEntity<FlashcardSettingResponse> getaFlashcardSettingByUserAndSetId(Long userId, Long setId) {
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        SetFlashcard s = SetFlashcard.builder().setId(setId).build();
        UserFlashcardSetting setting = flashcardSettingRepository.findByUser_UserIdAndSetFlashcard_SetId(userId, setId)
                .orElseThrow(() -> new ResourceNotFoundException("Flashcard setting", "set id", setId.toString()));
        FlashcardSettingResponse response = new FlashcardSettingResponse(setting.getLastCardId(),
                setting.isShuffleMode(), setting.isFlipCardMode(), setting.getLastAccessed());
        return ResponseEntity.ok(response);
    }

    @Override
    @Transactional
    public ResponseEntity<FlashcardSettingResponse> updateOrCreateNewFlashcardSetting(
            Long userId, FlashcardSettingRequest request) {
        Optional<UserFlashcardSetting> setting = flashcardSettingRepository.findByUser_UserIdAndSetFlashcard_SetId(userId,request.getSetId());
        System.out.println(setting);
        return setting.map(userFlashcardSetting -> ResponseEntity.ok().body(updateSetting(userFlashcardSetting, request)))
                .orElseGet(() -> ResponseEntity.ok().body(createSetting(userId, request)));
    }

    @Override
    public ResponseEntity<?> save(Long setId) {
        if(!existsUserFlashcardSetting(setId)){
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserPrincipal up = (UserPrincipal) authentication.getPrincipal();
            flashcardSettingRepository.save(up.getId(),setId);
            return ResponseEntity.status(HttpStatus.CREATED).body("Save successfully!");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("You have already saved !");
    }

    @Override
    public Boolean existsUserFlashcardSetting(Long setId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal up = (UserPrincipal) authentication.getPrincipal();
        Long exist= flashcardSettingRepository.existsUserFlashcardSetting(setId,up.getId());
        if(exist==null){
            return false;
        }
        return true;
    }


    private FlashcardSettingResponse updateSetting(UserFlashcardSetting setting, FlashcardSettingRequest request) {
        setting.setLastCardId(request.getLastCardId() == null ? setting.getLastCardId() : request.getLastCardId());
        setting.setShuffleMode(request.getShuffleMode() == null ? setting.isShuffleMode() : request.getShuffleMode());
        setting.setFlipCardMode(request.getFlipCardMode() == null ? setting.isFlipCardMode() : request.getFlipCardMode());
        setting.setLastAccessed(LocalDateTime.now());
        UserFlashcardSetting new_setting = flashcardSettingRepository.save(setting);
        return new FlashcardSettingResponse(new_setting.getLastCardId(), new_setting.isShuffleMode(),
                new_setting.isFlipCardMode(), new_setting.getLastAccessed());
    }

    private FlashcardSettingResponse createSetting(Long userId, FlashcardSettingRequest request) {
        AppUser au = AppUser.builder().userId(userId).build();
        SetFlashcard s = SetFlashcard.builder().setId(request.getSetId()).build();
        UserFlashcardSetting new_setting = flashcardSettingRepository.save(UserFlashcardSetting.builder()
                .user(au)
                .setFlashcard(s)
                .lastCardId(request.getLastCardId() == null ? -1 : request.getLastCardId())
                .shuffleMode(request.getShuffleMode() != null && request.getShuffleMode())
                .flipCardMode(request.getFlipCardMode() != null && request.getShuffleMode())
                .lastAccessed(LocalDateTime.now())
                .build());
        return new FlashcardSettingResponse(new_setting.getLastCardId(), new_setting.isShuffleMode(),
                new_setting.isFlipCardMode(), new_setting.getLastAccessed());
    }
}
