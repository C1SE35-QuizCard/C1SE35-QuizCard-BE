package com.example.quizcards.service.implement;

import com.example.quizcards.dto.CategorySetFlashcardUpdateRequest;
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

    public ICategorySetFlashcardDTO getCategorySetFlashcardById(int categoryId){
        return categoryRepository.findCategorySetFlashcardById(categoryId);
    }

    public List<ICategorySetFlashcardDTO> getAll(){
        return categoryRepository.findAllCategorySetFlashcard();
    }

    public List<ISetFlashcardDTO> findAllSetFlashcardsByCategoryId(int categoryId){
        return categoryRepository.findAllSetFlashcardsByCategoryId(categoryId);
    }

    public void addCategorySetFlashcard(String categoryName){
        categoryRepository.createCategorySetFlashcard(categoryName);
    }

    public void deleteCategorySetFlashcard(int categoryId){
        categoryRepository.deleteCategorySetFlashcard(categoryId);
    }

    public void updateCategorySetFlashcard(CategorySetFlashcardUpdateRequest request){
        categoryRepository.updateCategorySetFlashcard(request.getCategoryId(), request.getCategory_name());
    }
}
