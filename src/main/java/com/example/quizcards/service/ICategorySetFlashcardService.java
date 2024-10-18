package com.example.quizcards.service;

import com.example.quizcards.dto.*;
import com.example.quizcards.dto.request.CategorySetFlashcardCreationRequest;

import java.util.List;

public interface ICategorySetFlashcardService {
    ICategorySetFlashcardDTO getCategorySetFlashcardById(Long categoryId);
    List<ICategorySetFlashcardDTO> getAll();
    List<ISetFlashcardDTO> findAllSetFlashcardsByCategoryId(Long categoryId);
    void addCategorySetFlashcard(String categoryName);
<<<<<<< HEAD
    void deleteCategorySetFlashcard(int categoryId);
    void updateCategorySetFlashcard(CategorySetFlashcardCreationRequest request);
=======
    void deleteCategorySetFlashcard(Long categoryId);
    void updateCategorySetFlashcard(CategorySetFlashcardUpdateRequest request);
>>>>>>> 753d91ca62348b0fe2477bdd7995d53e87a2673e
}
