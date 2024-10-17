package com.example.quizcards.service;

import com.example.quizcards.dto.request.CollectionCreationRequest;
import com.example.quizcards.dto.ICollectionDTO;

import java.util.List;

public interface ICollectionService {
    ICollectionDTO getCollectionById(Long id);
    List<ICollectionDTO> getAllCollection();
    List<ICollectionDTO> getCollectionBySetId(int setId);
    List<ICollectionDTO> getCollectionByFolderId(int folderId);
    void addCollection(int folderId, int setId);
    void deleteCollection(Long id);
    void updateCollection(CollectionCreationRequest request);

}
