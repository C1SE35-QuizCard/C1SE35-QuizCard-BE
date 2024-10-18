package com.example.quizcards.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlashcardUpdateRequest {
    private Long cardId;

    @NotBlank(message = "Term of the flashcard is empty.")
    private String term;

    @NotBlank(message = "Definition of the flashcard is empty.")
    private String definition;
    private String imageLink;
    private Boolean isApproved;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long setId;
}
