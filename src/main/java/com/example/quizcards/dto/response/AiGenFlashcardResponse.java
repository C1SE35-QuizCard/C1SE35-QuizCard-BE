package com.example.quizcards.dto.response;

import com.example.quizcards.dto.AIGenFlashcard;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiGenFlashcardResponse {
    private List<AIGenFlashcard> flashcards;
    private String message;
    private boolean success;
    private long processingTimeMs;
}
