package com.example.quizcards.service;

import com.example.quizcards.dto.*;
import com.example.quizcards.dto.request.FolderCreationRequest;

import java.util.List;

public interface IFolderService {
    IFolderDTO getFolderById(Long folderId);
    List<IFolderDTO> getFoldersByUserId(Long userId);
    List<IFolderDTO> searchFolderByTitle(String title, Long userId);
    List<ISetFlashcardDTO> findSetByFolderIdAndUserId(Long folderId, Long userId);
    void addFolder(String title, Long userId);
    void deleteFolder(Long folderId);
    void updateFolder(FolderCreationRequest request);
}
