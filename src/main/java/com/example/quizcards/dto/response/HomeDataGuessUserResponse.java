package com.example.quizcards.dto.response;

import com.example.quizcards.dto.FlashcardSetDTO;
import com.example.quizcards.dto.ISetFlashcardDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class HomeDataGuessUserResponse {
    private List<FlashcardSetDTO> setsPopular;

    public HomeDataGuessUserResponse(List<FlashcardSetDTO> flashcardSetDTOS) {
    }
}
