package com.example.quizcards.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetFlashcardRequest {
    @NotNull(message = "Missing set id")
    private Long setId;

    @NotBlank(message = "Title of the set flashcard is empty.")
    private String title;
    private String descriptionSet;

    private Boolean isApproved;
    private Boolean isAnonymous;
    private Boolean sharingMode;

    private Long categoryId;

    private Long userId;
}
