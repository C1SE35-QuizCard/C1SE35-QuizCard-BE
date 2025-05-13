package com.example.quizcards.controller;

import com.example.quizcards.dto.request.AIGenFlashcardRequest;
import com.example.quizcards.dto.response.AiGenFlashcardResponse;
import com.example.quizcards.service.impl.AIGenFlashcardService;
import com.example.quizcards.service.impl.PDFService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/v1/generate")
@RequiredArgsConstructor
@Slf4j
public class AIGenFlashcardController {

    private final AIGenFlashcardService aiGenFlashcardService;

    private final PDFService pdfService;

    @PostMapping(value = "/flashcards", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_PREMIUM_USER')")
    public ResponseEntity<AiGenFlashcardResponse> generateFlashcards(
            @RequestParam("files") MultipartFile[] files,
            @Valid @ModelAttribute AIGenFlashcardRequest request) {

        AiGenFlashcardResponse response = aiGenFlashcardService.generateFlashcards(files, request);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/check-pdf-password-protection")
    public ResponseEntity<Map<String, Object>> checkPDFPasswordProtection(
            @RequestParam("file") MultipartFile file) {

        Map<String, Object> result = pdfService.checkPasswordProtection(file);

        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
    }
}