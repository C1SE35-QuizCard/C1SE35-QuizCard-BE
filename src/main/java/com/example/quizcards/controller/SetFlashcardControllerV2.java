package com.example.quizcards.controller;

import com.example.quizcards.helpers.SetFlashcardHelpers.SetCardPassCheck;
import com.example.quizcards.service.ISetFlashcardService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v2/set")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SetFlashcardControllerV2 {
    ISetFlashcardService setFlashcardService;

    @SetCardPassCheck
    @GetMapping("/list/{set_id}")
    public ResponseEntity<?> findAllFlashcardBySetId(@PathVariable("set_id") Long setId) {
        return ResponseEntity.ok(setFlashcardService.getAllFlashcardBySetId(setId));
    }
}
