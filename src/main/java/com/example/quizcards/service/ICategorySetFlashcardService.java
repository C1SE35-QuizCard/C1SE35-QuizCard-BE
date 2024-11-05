package com.example.quizcards.service;

import com.example.quizcards.dto.*;
import com.example.quizcards.dto.request.CategorySetFlashcardCreationRequest;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ICategorySetFlashcardService {
    ICategorySetFlashcardDTO getCategorySetFlashcardById(Long categoryId);
    List<ICategorySetFlashcardDTO> getAll();
    List<ISetFlashcardDTO> findAllSetFlashcardsByCategoryId(Long categoryId);
    List<ICategorySetFlashcardDTO> findTop1MostAccessedCategory(Long userId);
    void addCategorySetFlashcard(String categoryName);
    void deleteCategorySetFlashcard(Long categoryId);
    void updateCategorySetFlashcard(CategorySetFlashcardCreationRequest request);
}
