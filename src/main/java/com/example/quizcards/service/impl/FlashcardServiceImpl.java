package com.example.quizcards.service.impl;

import com.example.quizcards.dto.request.FlashcardCreationRequest;
import com.example.quizcards.dto.IFlashcardDTO;
import com.example.quizcards.repository.IFlashcardRepository;
import com.example.quizcards.service.IFlashcardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FlashcardServiceImpl implements IFlashcardService {

    @Autowired
    private IFlashcardRepository flashcardRepository;

    @Override
    public List<IFlashcardDTO> getAllBySetId(Long id){
        return flashcardRepository.findAllFlashcardsBySetId(id);
    }

    @Override
    public List<IFlashcardDTO> getAll(){
        return flashcardRepository.findAllFlashcards();
    }

<<<<<<< HEAD
    public void addFlashcard(String question, String answer,String imageLink,Boolean isApproved, Long setId){
        flashcardRepository.createFlashcards(question, answer, imageLink, isApproved, setId);
=======
    @Override
    public void addFlashcard(String term, String definition,String imageLink,Boolean isApproved, Long setId){
        flashcardRepository.createFlashcards(term, definition, imageLink, isApproved, setId);
>>>>>>> 76c78c3d77cc5ca52ad7a6e6e45324f3faa13547
    }

    @Override
    public void deleteFlashcard(Long cardId){
        flashcardRepository.deleteFlashcardById(cardId);
    }

<<<<<<< HEAD
    public void updateFlashcard(FlashcardUpdateRequest request){
=======
    @Override
    public void updateFlashcard(FlashcardCreationRequest request) {
>>>>>>> 76c78c3d77cc5ca52ad7a6e6e45324f3faa13547
        flashcardRepository.updateFlashcards(request.getCardId(), request.getQuestion(), request.getAnswer(), request.getImageLink(), request.getIsApproved(), request.getSetId());
    }

    @Override
    public IFlashcardDTO findByCardId(Long cardId){
        return flashcardRepository.findFlashcardByCardId(cardId);
    }
}
