package com.example.quizcards.service.impl;

import com.example.quizcards.dto.IFlashcardDTO;
import com.example.quizcards.dto.ISetFlashcardDTO;
import com.example.quizcards.dto.SetFlashcardUpdateRequest;
import com.example.quizcards.repository.ISetFlashcardRepository;
import com.example.quizcards.service.ISetFlashcardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SetFlashcardServiceImpl implements ISetFlashcardService {

    @Autowired
    private ISetFlashcardRepository setFlashcardRepository ;

    public List<IFlashcardDTO> getAllFlashcardBySetId(int setId){
        return setFlashcardRepository.findAllFlashcardsBySetId(setId);
    }
    public List<ISetFlashcardDTO> getAll(){
        return setFlashcardRepository.findAllSetFlashcards();
    }
    public ISetFlashcardDTO findBySetId(int setId){
        return setFlashcardRepository.findSetFlashcardsById(setId);
    }
    public void addSetFlashcard(String title, String descriptionSet, Boolean isApproved, Boolean isAnonymous, Boolean sharingMode, Long userId, int categoryId){
        setFlashcardRepository.createSetFlashcard(title, descriptionSet, isApproved, isAnonymous, sharingMode, userId, categoryId);
    }
    public void deleteSetFlashcard(int setId){
        setFlashcardRepository.deleteSetFlashcardById(setId);
    }
    public void updateSetFlashcard(SetFlashcardUpdateRequest request){
        setFlashcardRepository.updateSetFlashcard(request.getSetId(), request.getTitle(), request.getDescriptionSet(), request.getIsApproved(), request.getIsAnonymous(), request.getSharingMode(), request.getUserId(), request.getCategoryId());
    }

    public List<ISetFlashcardDTO> searchByTitle(String title){
        return setFlashcardRepository.searchByTitle(title);
    }

    public List<ISetFlashcardDTO> sortByUpdatedDate(){
        return setFlashcardRepository.sortByUpdatedDate();
    }

    public List<ISetFlashcardDTO> getAllSetByUserId(Long userId){
        return setFlashcardRepository.findAllSetByUserId(userId);
    }
    public List<ISetFlashcardDTO> getAllSetPublic(){
        return setFlashcardRepository.findAllSetPublic();
    }

    public List<ISetFlashcardDTO> getAllSetPublicByUserId(Long userId){
        return setFlashcardRepository.findAllSetPublicByUserId(userId);
    }
}
