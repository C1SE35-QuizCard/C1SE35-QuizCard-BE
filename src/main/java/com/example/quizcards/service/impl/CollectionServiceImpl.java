package com.example.quizcards.service.impl;

import com.example.quizcards.dto.ICollectionDTO;
import com.example.quizcards.dto.request.CollectionRequest;
import com.example.quizcards.helpers.CollectionHelpers.ICollectionHelpers;
import com.example.quizcards.repository.ICollectionRepository;
import com.example.quizcards.service.ICollectionService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollectionServiceImpl implements ICollectionService {
    @Autowired
    private ICollectionRepository collectionRepository;

    @Autowired
    private ICollectionHelpers collectionHelpers;

    @Override
    public ICollectionDTO getCollectionById(Long id) {
        return collectionRepository.findCollectionById(id);
    }

    @Override
    public List<ICollectionDTO> getAllCollection() {
        return collectionRepository.findAllCollection();
    }

    @Override
    public List<ICollectionDTO> getCollectionBySetId(Long setId) {
        return collectionRepository.findCollectionBySetId(setId);
    }

    @Override
    public List<ICollectionDTO> getCollectionByFolderId(Long folderId) {
        return collectionRepository.findCollectionByFolderId(folderId);
    }

    @Override
    public void addCollection(Long folderId, Long setId) {
        collectionRepository.createCollection(folderId, setId);
    }

    @Override
    public void deleteCollection(Long id) {
        collectionRepository.deleteCollectionById(id);
    }

    @Override
    public void updateCollection(CollectionRequest request) {
        collectionRepository.updateCollection(request.getId(), request.getFolderId(), request.getSetId());
    }

    @Override
    @Transactional
    public void addCollection_2(CollectionRequest request) {
        collectionHelpers.handleAddCollection(request);
        collectionRepository.createCollection(request.getFolderId(), request.getSetId());
    }

    @Override
    @Transactional
    public void deleteCollection_2(Long collectionId) {
        collectionHelpers.handleDeleteCollection(collectionId);
        collectionRepository.deleteCollectionById(collectionId);
    }
}
