package com.example.quizcards.service.impl;

import com.example.quizcards.dto.request.FlashcardCreateRequest;
import com.example.quizcards.dto.request.FlashcardInitializeRequest;
import com.example.quizcards.dto.request.FlashcardRequest;
import com.example.quizcards.dto.IFlashcardDTO;
import com.example.quizcards.helpers.FlashcardHelpers.IFlashcardHelpers;
import com.example.quizcards.repository.IFlashcardRepository;
import com.example.quizcards.service.IFlashcardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FlashcardServiceImpl implements IFlashcardService {

    @Autowired
    private IFlashcardRepository flashcardRepository;

    @Autowired
    private IFlashcardHelpers flashcardHelpers;

    @Override
    public List<IFlashcardDTO> getAllBySetId(Long id) {
        return flashcardRepository.findAllFlashcardsBySetId(id);
    }

    @Override
    public List<IFlashcardDTO> getAll() {
        return flashcardRepository.findAllFlashcards();
    }

    @Override
    public void addFlashcard(String question, String answer, String imageLink, Boolean isApproved, Long setId) {
        flashcardRepository.createFlashcards(question, answer, imageLink, isApproved, setId);
    }

    @Override
    public void deleteFlashcard(Long cardId) {
        flashcardRepository.deleteFlashcardById(cardId);
    }

    @Override
    public void updateFlashcard(FlashcardRequest request) {
        flashcardHelpers.handleUpdateFlashcard(request);
        flashcardRepository.updateFlashcards(request.getCardId(), request.getQuestion(), request.getAnswer(), request.getImageLink(), request.getIsApproved(), request.getSetId());
    }

    @Override
    public void addFlashcard_2(FlashcardCreateRequest request) {
        flashcardHelpers.handleAddFlashcard(request);
        flashcardRepository.createFlashcards(request.getQuestion(), request.getAnswer(), request.getImageData(),
                true, request.getSetId());
    }

    @Override
    public void deleteFlashcard_2(Long setId, Long cardId) {
        flashcardHelpers.handleDeleteFlashcard(cardId, setId);
        flashcardRepository.deleteFlashcardById(cardId);
    }

    @Override
    public IFlashcardDTO findByCardId(Long cardId) {
        return flashcardRepository.findFlashcardByCardId(cardId);
    }
}
