package com.example.quizcards.service.impl;

import com.example.quizcards.dto.request.FolderCreationRequest;
import com.example.quizcards.dto.IFolderDTO;
import com.example.quizcards.dto.ISetFlashcardDTO;
import com.example.quizcards.repository.IFolderRepository;
import com.example.quizcards.service.IFolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FolderServiceImpl implements IFolderService {

    @Autowired
    private IFolderRepository folderRepository;

    public IFolderDTO getFolderById(Long folderId) {
        return folderRepository.findFolderById(folderId);
    }

    public IFolderDTO getFolderByIdUserId(Long userId) {
        return folderRepository.findFolderByIdUserId(userId);
    }

    public List<IFolderDTO> searchFolderByTitle(String title) {
        return folderRepository.searchFolderByTitle(title);
    }

    public List<ISetFlashcardDTO> getSetByFolderId(Long folderId) {
        return folderRepository.findSetByFolderId(folderId);
    }

    public void addFolder(String title, Long userId) {
        folderRepository.createFolder(title, userId);
    }

    public void deleteFolder(Long folderId) {
        folderRepository.deleteFolderById(folderId);
    }
<<<<<<< HEAD:src/main/java/com/example/quizcards/service/implement/FolderServiceImpl.java
    public void updateFolder(FolderCreationRequest request){
=======

    public void updateFolder(FolderUpdateRequest request) {
>>>>>>> 753d91ca62348b0fe2477bdd7995d53e87a2673e:src/main/java/com/example/quizcards/service/impl/FolderServiceImpl.java
        folderRepository.updateFolder(request.getFolderId(), request.getTitle(), request.getUserId());
    }
}
