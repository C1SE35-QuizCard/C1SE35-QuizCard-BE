package com.example.quizcards.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlashcardRequest {
    @NotNull(message = "Missing card id")
    private Long cardId;

    @NotBlank(message = "Question of the flashcard is empty.")
    private String question;

    @NotBlank(message = "Answer of the flashcard is empty.")
    private String answer;
    private String imageLink;
    private Boolean isApproved;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @NotNull(message = "Missing set id")
    private Long setId;
}
