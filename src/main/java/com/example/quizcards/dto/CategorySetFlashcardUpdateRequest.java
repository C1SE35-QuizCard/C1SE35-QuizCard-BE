package com.example.quizcards.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategorySetFlashcardUpdateRequest {
    private Long categoryId;

    @NotBlank(message = "Name of the Category is empty.")
    private String categoryName;
}
