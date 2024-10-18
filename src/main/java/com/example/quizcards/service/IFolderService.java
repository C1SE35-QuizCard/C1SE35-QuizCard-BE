package com.example.quizcards.service;

import com.example.quizcards.dto.*;
import com.example.quizcards.dto.request.FolderCreationRequest;

import java.util.List;

public interface IFolderService {
    IFolderDTO getFolderById(Long folderId);
    IFolderDTO getFolderByUserId(Long userId);
    List<IFolderDTO> searchFolderByTitle(String title);
    List<ISetFlashcardDTO> getSetByFolderId(Long folderId);
    void addFolder(String title, Long userId);
    void deleteFolder(Long folderId);
    void updateFolder(FolderCreationRequest request);
}
