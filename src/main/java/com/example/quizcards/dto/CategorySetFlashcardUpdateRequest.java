package com.example.quizcards.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategorySetFlashcardUpdateRequest {
    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public @NotBlank(message = "Name of the Category is empty.") String getCategory_name() {
        return categoryName;
    }

    public void setCategory_name(@NotBlank(message = "Name of the Category is empty.") String category_name) {
        this.categoryName = category_name;
    }

    private int categoryId;

    @NotBlank(message = "Name of the Category is empty.")
    private String categoryName;
}
