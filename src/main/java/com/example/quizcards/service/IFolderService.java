package com.example.quizcards.service;

import com.example.quizcards.dto.IFolderDTO;
import com.example.quizcards.dto.ISetFlashcardDTO;
import com.example.quizcards.dto.request.CreateFolderRequest;
import com.example.quizcards.dto.request.FolderRequest;
import com.example.quizcards.dto.request.UpdateFolderRequest;
import com.example.quizcards.dto.response.AdminFolderResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IFolderService {
    // User methods
    List<IFolderDTO> getFoldersByUserId();
    List<IFolderDTO> searchFolderByTitle(String title);
    List<ISetFlashcardDTO> findSetByFolderIdAndUserId(Long folderId);
    void addFolder(String title);
    void updateFolder(UpdateFolderRequest request);
    void addFolder_2(FolderRequest request);
    void updateFolder_2(FolderRequest request);
    void deleteFolder_2(Long folderId);

    // Admin methods
    List<AdminFolderResponse> getAllFoldersWithUserInfo(int page, int size);
    ResponseEntity<?> getFolderById(Long id);
    ResponseEntity<?> createFolder(CreateFolderRequest request);
    ResponseEntity<?> updateFolder(Long id, CreateFolderRequest request);
    ResponseEntity<?> deleteFolder(Long id);
    ResponseEntity<?> searchFolders(String title, String username, String createdAt, int page, int size);
}
