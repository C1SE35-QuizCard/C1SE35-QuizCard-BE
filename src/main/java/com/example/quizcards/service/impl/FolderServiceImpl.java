package com.example.quizcards.service.impl;

import com.example.quizcards.dto.request.FolderRequest;
import com.example.quizcards.dto.IFolderDTO;
import com.example.quizcards.dto.ISetFlashcardDTO;
import com.example.quizcards.helpers.FolderHelpers.IFolderHelpers;
import com.example.quizcards.repository.IFolderRepository;
import com.example.quizcards.repository.ISetFlashcardRepository;
import com.example.quizcards.security.UserPrincipal;
import com.example.quizcards.service.IFolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FolderServiceImpl implements IFolderService {

    @Autowired
    private IFolderRepository folderRepository;

    @Autowired
    private IFolderHelpers folderHelpers;

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
    public List<ISetFlashcardDTO> findSetByFolderIdAndUserId(Long folderId, Long userId){
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
    public void updateFolder(FolderRequest request) {
        folderRepository.updateFolder(request.getFolderId(), request.getTitle(), request.getUserId());
    }

    @Override
    public void addFolder_2(FolderRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal up = (UserPrincipal) authentication.getPrincipal();
        folderRepository.createFolder(request.getTitle(), up.getId());
    }

    @Override
    public void deleteFolder_2(Long folderId) {
        folderHelpers.handleDeleteFolder(folderId);
        folderRepository.deleteFolderById(folderId);
    }

    @Override
    public void updateFolder_2(FolderRequest request) {
        folderHelpers.handleUpdateFolder(request);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal up = (UserPrincipal) authentication.getPrincipal();
        folderRepository.updateFolder(request.getFolderId(), request.getTitle(), up.getId());
    }
}
