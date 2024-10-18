package com.example.quizcards.service;

import com.example.quizcards.dto.FolderUpdateRequest;
import com.example.quizcards.dto.IFolderDTO;
import com.example.quizcards.dto.ISetFlashcardDTO;
import com.example.quizcards.dto.SetFlashcardUpdateRequest;

import java.util.List;

public interface IFolderService {
    IFolderDTO getFolderById(Long folderId);
    IFolderDTO getFolderByIdUserId(Long userId);
    List<IFolderDTO> searchFolderByTitle(String title);
    List<ISetFlashcardDTO> getSetByFolderId(Long folderId);
    void addFolder(String title, Long userId);
    void deleteFolder(Long folderId);
    void updateFolder(FolderUpdateRequest request);
}
