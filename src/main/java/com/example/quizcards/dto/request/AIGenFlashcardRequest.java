package com.example.quizcards.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AIGenFlashcardRequest {
    @Min(value = 2, message = "Số lượng flashcard phải từ 2 trở lên")
    @Max(value = 200, message = "Số lượng flashcard không được vượt quá 200")
    private Integer numberOfFlashcards = 10;

    private String pdfPassword;

    private String promptTemplate;
}
