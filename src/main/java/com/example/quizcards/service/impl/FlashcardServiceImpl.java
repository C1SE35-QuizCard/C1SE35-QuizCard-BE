package com.example.quizcards.service.impl;

import com.example.quizcards.dto.FlashcardUpdateRequest;
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

    public List<IFlashcardDTO> getAllBySetId(Long id){
        return flashcardRepository.findAllFlashcardsBySetId(id);
    }

    public List<IFlashcardDTO> getAll(){
        return flashcardRepository.findAllFlashcards();
    }

    public void addFlashcard(String term, String definition,String imageLink,Boolean isApproved, Long setId){
        flashcardRepository.createFlashcards(term, definition, imageLink, isApproved, setId);
    }

    public void deleteFlashcard(Long cardId){
        flashcardRepository.deleteFlashcardById(cardId);
    }

    public void updateFlashcard(FlashcardUpdateRequest request){
        flashcardRepository.updateFlashcards(request.getCardId(), request.getTerm(), request.getDefinition(), request.getImageLink(), request.getIsApproved(), request.getSetId());
    }

    public IFlashcardDTO findByCardId(Long cardId){
        return flashcardRepository.findFlashcardByCardId(cardId);
    }
}
