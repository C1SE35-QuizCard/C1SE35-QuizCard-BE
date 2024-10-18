package com.example.quizcards.service.impl;

import com.example.quizcards.dto.request.FlashcardSettingRequest;
import com.example.quizcards.dto.response.FlashcardSettingResponse;
import com.example.quizcards.entities.AppUser;
import com.example.quizcards.entities.SetFlashcard;
import com.example.quizcards.entities.UserFlashcardSetting;
import com.example.quizcards.exception.ResourceNotFoundException;
import com.example.quizcards.repository.IAppUserRepository;
import com.example.quizcards.repository.IFlashcardSettingRepository;
import com.example.quizcards.service.IFlashcardSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class FlashcardSettingServiceImpl implements IFlashcardSettingService {
    @Autowired
    private IFlashcardSettingRepository flashcardSettingRepository;

    @Autowired
    private IAppUserRepository appUserRepository;

    @Override
    public ResponseEntity<FlashcardSettingResponse> getaFlashcardSettingByUserAndSetId(Long userId, Long setId) {
        AppUser au = AppUser.builder().userId(userId).build();
        SetFlashcard s = SetFlashcard.builder().setId(setId).build();
        UserFlashcardSetting setting = flashcardSettingRepository.findByUserAndAndSetFlashcard(au, s)
                .orElseThrow(() -> new ResourceNotFoundException("Set", "id", setId.toString()));
        return ResponseEntity.ok(FlashcardSettingResponse.buildResponse(setting));
    }

    @Override
    @Transactional
    public ResponseEntity<FlashcardSettingResponse> updateOrCreateNewFlashcardSetting(
            Long userId, FlashcardSettingRequest request) {
        AppUser au = AppUser.builder().userId(userId).build();
        SetFlashcard s = SetFlashcard.builder().setId(request.getSetId()).build();
        return flashcardSettingRepository.findByUserAndAndSetFlashcard(au, s)
                .map(uc_setting -> {
                    uc_setting = updateSetting(uc_setting, request);
                    return ResponseEntity.ok().body(FlashcardSettingResponse.buildResponse(uc_setting));
                }).orElse(ResponseEntity.ok().body(FlashcardSettingResponse.buildResponse(
                        createSetting(userId, request)
                )));
    }

    private UserFlashcardSetting updateSetting(UserFlashcardSetting setting, FlashcardSettingRequest request) {
        setting.setLastCardIndex(request.getLastCardIndex());
        setting.setShuffleMode(request.isShuffleMode());
        setting.setFlipCardMode(request.isFlipCardMode());
        setting.setLastAccessed(request.getLastAccessed());
        return flashcardSettingRepository.save(setting);
    }

    private UserFlashcardSetting createSetting(Long userId, FlashcardSettingRequest request) {
        AppUser au = AppUser.builder().userId(userId).build();
        SetFlashcard s = SetFlashcard.builder().setId(request.getSetId()).build();
        return flashcardSettingRepository.save(UserFlashcardSetting.builder()
                .user(au)
                .setFlashcard(s)
                .lastCardIndex(request.getLastCardIndex())
                .shuffleMode(request.isShuffleMode())
                .flipCardMode(request.isFlipCardMode())
                .lastAccessed(request.getLastAccessed())
                .build());
    }
}
