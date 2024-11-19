package com.example.quizcards.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlashcardSettingRequest {
    @NotNull(message = "Set id is required.")
    private Long setId;

    @NotNull(message = "Last card index is required.")
    @Min(value = 0)
    private int lastCardIndex;

    @NotNull(message = "Shuffle mode is required.")
    private boolean shuffleMode;

    @NotNull(message = "Flip card mode is required.")
    private boolean flipCardMode;

    private LocalDateTime lastAccessed;
}
