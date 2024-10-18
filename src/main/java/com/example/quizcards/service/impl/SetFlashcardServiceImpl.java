package com.example.quizcards.service.impl;
<<<<<<< HEAD

=======
>>>>>>> 76c78c3d77cc5ca52ad7a6e6e45324f3faa13547
import com.example.quizcards.dto.IFlashcardDTO;
import com.example.quizcards.dto.ISetFlashcardDTO;
import com.example.quizcards.dto.request.SetFlashcardCreationRequest;
import com.example.quizcards.repository.ISetFlashcardRepository;
import com.example.quizcards.service.ISetFlashcardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SetFlashcardServiceImpl implements ISetFlashcardService {

    @Autowired
    private ISetFlashcardRepository setFlashcardRepository ;

    @Override
    public List<IFlashcardDTO> getAllFlashcardBySetId(Long setId){
        return setFlashcardRepository.findAllFlashcardsBySetId(setId);
    }

    @Override
    public List<ISetFlashcardDTO> getAll(){
        return setFlashcardRepository.findAllSetFlashcards();
    }


    @Override
    public ISetFlashcardDTO findBySetId(Long setId){
        return setFlashcardRepository.findSetFlashcardsById(setId);
    }

    @Override
    public void addSetFlashcard(String title, String descriptionSet, Boolean isApproved, Boolean isAnonymous, Boolean sharingMode, Long userId, Long categoryId){
        setFlashcardRepository.createSetFlashcard(title, descriptionSet, isApproved, isAnonymous, sharingMode, userId, categoryId);
    }

    @Override
    public void deleteSetFlashcard(Long setId){
        setFlashcardRepository.deleteSetFlashcardById(setId);
    }

    @Override
    public void updateSetFlashcard(SetFlashcardCreationRequest request){
        setFlashcardRepository.updateSetFlashcard(request.getSetId(), request.getTitle(), request.getDescriptionSet(), request.getIsApproved(), request.getIsAnonymous(), request.getSharingMode(), request.getUserId(), request.getCategoryId());
    }

    @Override
    public List<ISetFlashcardDTO> searchByTitle(String title){
        return setFlashcardRepository.searchByTitle(title);
    }

    @Override
    public List<ISetFlashcardDTO> sortByUpdatedDate(){
        return setFlashcardRepository.sortByUpdatedDate();
    }

    @Override
    public List<ISetFlashcardDTO> getAllSetByUserId(Long userId){
        return setFlashcardRepository.findAllSetByUserId(userId);
    }

    @Override
    public List<ISetFlashcardDTO> getAllSetPublic(){
        return setFlashcardRepository.findAllSetPublic();
    }

    @Override
    public List<ISetFlashcardDTO> getAllSetPublicByUserId(Long userId){
        return setFlashcardRepository.findAllSetPublicByUserId(userId);
    }
}
