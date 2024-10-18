package com.example.quizcards.service;

import com.example.quizcards.dto.CollectionUpdateRequest;
import com.example.quizcards.dto.FolderUpdateRequest;
import com.example.quizcards.dto.ICollectionDTO;

import java.util.List;

public interface ICollectionService {
    ICollectionDTO getCollectionById(Long id);
    List<ICollectionDTO> getAllCollection();
    List<ICollectionDTO> getCollectionBySetId(Long setId);
    List<ICollectionDTO> getCollectionByFolderId(Long folderId);
    void addCollection(Long folderId, Long setId);
    void deleteCollection(Long id);
    void updateCollection(CollectionUpdateRequest request);

}
