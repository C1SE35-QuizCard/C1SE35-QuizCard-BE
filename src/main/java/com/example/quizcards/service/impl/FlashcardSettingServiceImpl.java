package com.example.quizcards.service.impl;

import com.example.quizcards.dto.request.FlashcardSettingRequest;
import com.example.quizcards.dto.response.FlashcardSettingResponse;
import com.example.quizcards.entities.AppUser;
import com.example.quizcards.entities.SetFlashcard;
import com.example.quizcards.entities.UserFlashcardSetting;
import com.example.quizcards.exception.ResourceNotFoundException;
import com.example.quizcards.repository.IFlashcardSettingRepository;
import com.example.quizcards.service.IFlashcardSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class FlashcardSettingServiceImpl implements IFlashcardSettingService {
    @Autowired
    private IFlashcardSettingRepository flashcardSettingRepository;

    @Override
    public ResponseEntity<FlashcardSettingResponse> getaFlashcardSettingByUserAndSetId(Long userId, Long setId) {
        AppUser au = AppUser.builder().userId(userId).build();
        SetFlashcard s = SetFlashcard.builder().setId(setId).build();
        UserFlashcardSetting setting = flashcardSettingRepository.findByUserAndAndSetFlashcard(au, s)
                .orElseThrow(() -> new ResourceNotFoundException("Flashcard setting", "set id", setId.toString()));
        FlashcardSettingResponse response = new FlashcardSettingResponse(setting.getLastCardId(),
                setting.isShuffleMode(), setting.isFlipCardMode(), setting.getLastAccessed());
        return ResponseEntity.ok(response);
    }

    @Override
    @Transactional
    public ResponseEntity<FlashcardSettingResponse> updateOrCreateNewFlashcardSetting(
            Long userId, FlashcardSettingRequest request) {
        AppUser au = AppUser.builder().userId(userId).build();
        SetFlashcard s = SetFlashcard.builder().setId(request.getSetId()).build();
        Optional<UserFlashcardSetting> setting = flashcardSettingRepository.findByUserAndAndSetFlashcard(au, s);
        return setting.map(userFlashcardSetting -> ResponseEntity.ok().body(updateSetting(userFlashcardSetting, request)))
                .orElseGet(() -> ResponseEntity.ok().body(createSetting(userId, request)));
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
