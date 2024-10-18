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

    public ICategorySetFlashcardDTO getCategorySetFlashcardById(Long categoryId){
        return categoryRepository.findCategorySetFlashcardById(categoryId);
    }

    public List<ICategorySetFlashcardDTO> getAll(){
        return categoryRepository.findAllCategorySetFlashcard();
    }

    public List<ISetFlashcardDTO> findAllSetFlashcardsByCategoryId(Long categoryId){
        return categoryRepository.findAllSetFlashcardsByCategoryId(categoryId);
    }

    public void addCategorySetFlashcard(String categoryName){
        categoryRepository.createCategorySetFlashcard(categoryName);
    }

    public void deleteCategorySetFlashcard(Long categoryId){
        categoryRepository.deleteCategorySetFlashcard(categoryId);
    }

<<<<<<< HEAD:src/main/java/com/example/quizcards/service/implement/CategorySetFlashcardServiceImpl.java
    public void updateCategorySetFlashcard(CategorySetFlashcardCreationRequest request){
=======
    public void updateCategorySetFlashcard(CategorySetFlashcardUpdateRequest request){
>>>>>>> 753d91ca62348b0fe2477bdd7995d53e87a2673e:src/main/java/com/example/quizcards/service/impl/CategorySetFlashcardServiceImpl.java
        categoryRepository.updateCategorySetFlashcard(request.getCategoryId(), request.getCategoryName());
    }
}
