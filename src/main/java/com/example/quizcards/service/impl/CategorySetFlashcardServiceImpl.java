package com.example.quizcards.service.impl;

import com.example.quizcards.dto.request.CategorySetFlashcardCreationRequest;
import com.example.quizcards.dto.ICategorySetFlashcardDTO;
import com.example.quizcards.dto.ISetFlashcardDTO;
import com.example.quizcards.repository.ICategorySetFlashcardRepository;
import com.example.quizcards.service.ICategorySetFlashcardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategorySetFlashcardServiceImpl implements ICategorySetFlashcardService {
    @Autowired
    private ICategorySetFlashcardRepository categoryRepository;

    @Override
    public ICategorySetFlashcardDTO getCategorySetFlashcardById(Long categoryId){
        return categoryRepository.findCategorySetFlashcardById(categoryId);
    }

    @Override
    public List<ICategorySetFlashcardDTO> getAll(){
        return categoryRepository.findAllCategorySetFlashcard();
    }

    @Override
    public List<ISetFlashcardDTO> findAllSetFlashcardsByCategoryId(Long categoryId){
        return categoryRepository.findAllSetFlashcardsByCategoryId(categoryId);
    }

    @Override
    public List<ICategorySetFlashcardDTO> findTop1MostAccessedCategory(Long userId) {
        return categoryRepository.findTop1MostAccessedCategory(userId);
    }

    @Override
    public void addCategorySetFlashcard(String categoryName){
        categoryRepository.createCategorySetFlashcard(categoryName);
    }

    @Override
    public void deleteCategorySetFlashcard(Long categoryId){
        categoryRepository.deleteCategorySetFlashcard(categoryId);
    }

    @Override
    public void updateCategorySetFlashcard(CategorySetFlashcardCreationRequest request){
        categoryRepository.updateCategorySetFlashcard(request.getCategoryId(), request.getCategoryName());
    }
}
