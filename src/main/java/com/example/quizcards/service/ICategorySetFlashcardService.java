package com.example.quizcards.service;

import com.example.quizcards.dto.*;

import java.util.List;

public interface ICategorySetFlashcardService {
    ICategorySetFlashcardDTO getCategorySetFlashcardById(Long categoryId);
    List<ICategorySetFlashcardDTO> getAll();
    List<ISetFlashcardDTO> findAllSetFlashcardsByCategoryId(Long categoryId);
    void addCategorySetFlashcard(String categoryName);
    void deleteCategorySetFlashcard(Long categoryId);
    void updateCategorySetFlashcard(CategorySetFlashcardUpdateRequest request);
}
