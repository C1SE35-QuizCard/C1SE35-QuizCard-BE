package com.example.quizcards.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetFlashcardCreationRequest {
    private Long setId;

    @NotBlank(message = "Title of the set flashcard is empty.")
    private String title;
    private String descriptionSet;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isApproved;
    private Boolean isAnonymous;
    private Boolean sharingMode;
    private Long userId;
    private Long categoryId;
}
