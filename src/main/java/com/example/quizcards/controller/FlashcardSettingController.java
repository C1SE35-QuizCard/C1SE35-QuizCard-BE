package com.example.quizcards.controller;

import com.example.quizcards.dto.request.FlashcardSettingRequest;
import com.example.quizcards.dto.request.StoreSetFlashcadDTO;
import com.example.quizcards.dto.response.FlashcardSettingResponse;
import com.example.quizcards.security.UserPrincipal;
import com.example.quizcards.service.IFlashcardSettingService;
import com.example.quizcards.service.ISetFlashcardService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/flashcard-settings")
public class FlashcardSettingController {
    @Autowired
    private IFlashcardSettingService flashcardSettingService;
    @Autowired
    private ISetFlashcardService setFlashcardService;
    @GetMapping("/{setId}")
    @PreAuthorize("hasAnyRole('ROLE_FREE_USER', 'ROLE_PREMIUM_USER', 'ROLE_ADMIN')")
    public ResponseEntity<FlashcardSettingResponse> getSetSettingsByIdOnCurrentUser(@PathVariable("setId") Long setId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal up = (UserPrincipal) authentication.getPrincipal();
        return flashcardSettingService.getaFlashcardSettingByUserAndSetId(up.getId(), setId);
    }
    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ROLE_FREE_USER', 'ROLE_PREMIUM_USER', 'ROLE_ADMIN')")
    public  ResponseEntity<?> saveSetFlashcard(@RequestBody StoreSetFlashcadDTO storeSetFlashcadDTO) {
        try {
            boolean exists=setFlashcardService.existsBySetFlashcard_SetIdAndSetFlashcard_SharingMode(storeSetFlashcadDTO.getSetId());
            if(!exists) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No flashcards found for set ID " + storeSetFlashcadDTO.getSetId());
            }
            return flashcardSettingService.save(storeSetFlashcadDTO.getSetId());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    @PatchMapping("/update")
    @PreAuthorize("hasAnyRole('ROLE_FREE_USER', 'ROLE_PREMIUM_USER', 'ROLE_ADMIN')")
    public ResponseEntity<FlashcardSettingResponse> updateOrCreateNewFlashcardSetting(
            @Valid @RequestBody FlashcardSettingRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal up = (UserPrincipal) authentication.getPrincipal();
        return flashcardSettingService.updateOrCreateNewFlashcardSetting(up.getId(), request);
    }
}
