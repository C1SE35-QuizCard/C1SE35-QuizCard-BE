package com.example.quizcards.dto.response;

import com.example.quizcards.dto.ISetFlashcardDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomeDataGuessUserResponse {
    private List<ISetFlashcardDTO> setsPopular;
}
