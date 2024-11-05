package com.example.quizcards.service;

import com.example.quizcards.dto.ISetFlashcardDTO;
import com.example.quizcards.dto.request.FlashcardSettingRequest;
import com.example.quizcards.dto.response.FlashcardSettingResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IFlashcardSettingService {
    ResponseEntity<FlashcardSettingResponse> getaFlashcardSettingByUserAndSetId(Long userId, Long setId);
    ResponseEntity<FlashcardSettingResponse> updateOrCreateNewFlashcardSetting(
            Long userId,
            FlashcardSettingRequest request);
}
