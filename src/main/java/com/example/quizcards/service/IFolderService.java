package com.example.quizcards.service;

import com.example.quizcards.dto.*;
import com.example.quizcards.dto.request.FolderCreationRequest;

import java.util.List;

public interface IFolderService {
    IFolderDTO getFolderById(int folderId);
    IFolderDTO getFolderByIdUserId(Long userId);
    List<IFolderDTO> searchFolderByTitle(String title);
    List<ISetFlashcardDTO> getSetByFolderId(int folderId);
    void addFolder(String title, Long userId);
    void deleteFolder(int folderId);
    void updateFolder(FolderCreationRequest request);
}
