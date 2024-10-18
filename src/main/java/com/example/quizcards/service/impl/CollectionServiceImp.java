package com.example.quizcards.service.impl;

import com.example.quizcards.dto.CollectionUpdateRequest;
import com.example.quizcards.dto.ICollectionDTO;
import com.example.quizcards.repository.ICollectionRepository;
import com.example.quizcards.service.ICollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollectionServiceImp implements ICollectionService {

    @Autowired
    private ICollectionRepository collectionRepository;

    public ICollectionDTO getCollectionById(Long id){
        return collectionRepository.findCollectionById(id);
    }
    public List<ICollectionDTO> getAllCollection(){
        return collectionRepository.findAllCollection();
    }
    public List<ICollectionDTO> getCollectionBySetId(Long setId){
        return collectionRepository.findCollectionBySetId(setId);
    }
    public List<ICollectionDTO> getCollectionByFolderId(Long folderId){
        return collectionRepository.findCollectionByFolderId(folderId);
    }
    public void addCollection(Long folderId, Long setId){
        collectionRepository.createCollection(folderId, setId);
    }
    public void deleteCollection(Long id){
        collectionRepository.deleteCollectionById(id);
    }
    public void updateCollection(CollectionUpdateRequest request){
        collectionRepository.updateCollection(request.getId(), request.getFolderId(), request.getSetId());
    }
}
