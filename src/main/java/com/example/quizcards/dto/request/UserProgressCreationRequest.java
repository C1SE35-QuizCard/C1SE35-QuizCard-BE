package com.example.quizcards.dto.request;

import com.example.quizcards.entities.Flashcard;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProgressCreationRequest {
    private Long progressId;
    private Boolean progressType;
    private Boolean isAttention;

    @NotNull(message = "Card Id is required.")
    private Long cardId;
}
