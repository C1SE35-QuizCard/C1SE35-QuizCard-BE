package com.example.quizcards.service;

import com.example.quizcards.dto.*;
import com.example.quizcards.dto.request.FolderCreationRequest;

import java.util.List;

public interface IFolderService {
    IFolderDTO getFolderById(Long folderId);
    IFolderDTO getFolderByIdUserId(Long userId);
    List<IFolderDTO> searchFolderByTitle(String title);
    List<ISetFlashcardDTO> getSetByFolderId(Long folderId);
    void addFolder(String title, Long userId);
<<<<<<< HEAD
    void deleteFolder(int folderId);
    void updateFolder(FolderCreationRequest request);
=======
    void deleteFolder(Long folderId);
    void updateFolder(FolderUpdateRequest request);
>>>>>>> 753d91ca62348b0fe2477bdd7995d53e87a2673e
}
