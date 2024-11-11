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

    @Override
    public IFolderDTO getFolderById(Long folderId) {
        return folderRepository.findFolderById(folderId);
    }

    @Override
    public List<IFolderDTO> getFoldersByUserId(Long userId) {
        return folderRepository.findFoldersByUserId(userId);
    }

    @Override
    public List<IFolderDTO> searchFolderByTitle(String title, Long userId) {
        return folderRepository.searchFolderByTitle(title, userId);
    }

    @Override
    public List<ISetFlashcardDTO> findSetByFolderIdAndUserId(Long folderId, Long userId) {
        return folderRepository.findSetByFolderIdAndUserId(folderId, userId);
    }


    @Override
    public void addFolder(String title, Long userId) {
        folderRepository.createFolder(title, userId);
    }

    @Override
    public void deleteFolder(Long folderId) {
        folderRepository.deleteFolderById(folderId);
    }

    @Override
    public void updateFolder(FolderCreationRequest request){
        folderRepository.updateFolder(request.getFolderId(), request.getTitle(), request.getUserId());
    }
}
