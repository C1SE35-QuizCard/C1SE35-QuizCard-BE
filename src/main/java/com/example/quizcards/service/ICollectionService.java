package com.example.quizcards.service;

import com.example.quizcards.dto.CollectionUpdateRequest;
import com.example.quizcards.dto.FolderUpdateRequest;
import com.example.quizcards.dto.ICollectionDTO;

import java.util.List;

public interface ICollectionService {
    ICollectionDTO getCollectionById(Long id);
    List<ICollectionDTO> getAllCollection();
    List<ICollectionDTO> getCollectionBySetId(int setId);
    List<ICollectionDTO> getCollectionByFolderId(int folderId);
    void addCollection(int folderId, int setId);
    void deleteCollection(Long id);
    void updateCollection(CollectionUpdateRequest request);

}
