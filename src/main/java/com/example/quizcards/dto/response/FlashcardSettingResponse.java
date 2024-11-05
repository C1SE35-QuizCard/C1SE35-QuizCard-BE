package com.example.quizcards.dto.response;

import com.example.quizcards.entities.UserFlashcardSetting;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlashcardSettingResponse {
    private int lastCardIndex;
    private boolean shuffleMode;
    private boolean flipCardMode;
    private LocalDateTime lastAccessed;
}
