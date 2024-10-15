package com.example.quizcards.service;

import com.example.quizcards.dto.*;

import java.util.List;

public interface ICategorySetFlashcardService {
    ICategorySetFlashcardDTO getCategorySetFlashcardById(int categoryId);
    List<ICategorySetFlashcardDTO> getAll();
    List<ISetFlashcardDTO> findAllSetFlashcardsByCategoryId(int categoryId);
    void addCategorySetFlashcard(String categoryName);
    void deleteCategorySetFlashcard(int categoryId);
    void updateCategorySetFlashcard(CategorySetFlashcardUpdateRequest request);
}
