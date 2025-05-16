package com.example.quizcards.service.impl;

import com.example.quizcards.dto.IFolderDTO;
import com.example.quizcards.dto.ISetFlashcardDTO;
import com.example.quizcards.dto.request.CreateFolderRequest;
import com.example.quizcards.dto.request.FolderRequest;
import com.example.quizcards.dto.request.UpdateFolderRequest;
import com.example.quizcards.dto.response.AdminFolderResponse;
import com.example.quizcards.dto.response.ApiResponse;
import com.example.quizcards.entities.Folder;
import com.example.quizcards.entities.SetFlashcard;
import com.example.quizcards.exception.ResourceNotFoundException;
import com.example.quizcards.helpers.FolderHelpers.IFolderHelpers;
import com.example.quizcards.repository.IFolderRepository;
import com.example.quizcards.repository.IAppUserRepository;
import com.example.quizcards.security.UserPrincipal;
import com.example.quizcards.service.IFolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FolderServiceImpl implements IFolderService {

    @Autowired
    private IFolderRepository folderRepository;

    @Autowired
    private IAppUserRepository userRepository;

    @Autowired
    private IFolderHelpers folderHelpers;

    // User methods
    @Override
    public List<IFolderDTO> getFoldersByUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal up = (UserPrincipal) authentication.getPrincipal();
        return folderRepository.findFoldersByUserId(up.getId());
    }

    @Override
    public List<IFolderDTO> searchFolderByTitle(String title) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal up = (UserPrincipal) authentication.getPrincipal();
        return folderRepository.searchFolderByTitle(title, up.getId());
    }

    @Override
    public List<ISetFlashcardDTO> findSetByFolderIdAndUserId(Long folderId) {
        return folderRepository.findSetByFolderId(folderId);
    }

    @Override
    public void addFolder(String title) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal up = (UserPrincipal) authentication.getPrincipal();
        folderRepository.createFolder(title, up.getId());
    }

    @Override
    public void updateFolder(UpdateFolderRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal up = (UserPrincipal) authentication.getPrincipal();
        folderRepository.updateFolder(request.getFolderId(), request.getTitle(), up.getId());
    }

    @Override
    public void addFolder_2(FolderRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal up = (UserPrincipal) authentication.getPrincipal();
        folderRepository.createFolder(request.getTitle(), up.getId());
    }

    @Override
    public void updateFolder_2(FolderRequest request) {
        folderHelpers.handleFolderOwner(request.getFolderId());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal up = (UserPrincipal) authentication.getPrincipal();
        folderRepository.updateFolder(request.getFolderId(), request.getTitle(), up.getId());
    }

    @Override
    public void deleteFolder_2(Long folderId) {
        folderHelpers.handleFolderOwner(folderId);
        folderRepository.deleteFolderById(folderId);
    }

    // Admin methods
    @Override
    public List<AdminFolderResponse> getAllFoldersWithUserInfo(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Folder> folders = folderRepository.findAll(pageable);
        
        return folders.getContent().stream()
            .map(folder -> AdminFolderResponse.builder()
                .folderId(folder.getFolderId())
                .title(folder.getTitle())
                .createdAt(folder.getCreatedAt())
                .updatedAt(folder.getUpdatedAt())
                .userId(folder.getUser().getUserId())
                .build())
            .collect(Collectors.toList());
    }

    @Override
    public ResponseEntity<?> getFolderById(Long id) {
        try {
            Folder folder = folderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Folder", "id", id));

            AdminFolderResponse response = AdminFolderResponse.builder()
                .folderId(folder.getFolderId())
                .title(folder.getTitle())
                .createdAt(folder.getCreatedAt())
                .updatedAt(folder.getUpdatedAt())
                .userId(folder.getUser().getUserId())
                .build();

            return ResponseEntity.ok(new ApiResponse(true, "Successfully retrieved folder", 
                HttpStatus.OK, response));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(false, e.getMessage(), HttpStatus.NOT_FOUND, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, "Error retrieving folder: " + e.getMessage(), 
                    HttpStatus.INTERNAL_SERVER_ERROR, null));
        }
    }

    @Override
    public ResponseEntity<?> createFolder(CreateFolderRequest request) {
        try {
            // Validate request
            if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Title is required", HttpStatus.BAD_REQUEST, null));
            }
            if (request.getUserId() == null) {
                return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "User ID is required", HttpStatus.BAD_REQUEST, null));
            }

            // Check if user exists
            var user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));

            // Check if folder with same title already exists for this user
            List<IFolderDTO> existingFolders = folderRepository.findFoldersByUserId(request.getUserId());
            boolean folderExists = existingFolders.stream()
                .anyMatch(folder -> folder.getTitle().equalsIgnoreCase(request.getTitle().trim()));

            if (folderExists) {
                return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "A folder with this title already exists", 
                        HttpStatus.BAD_REQUEST, null));
            }

            // Create new folder
            Folder folder = new Folder();
            folder.setTitle(request.getTitle().trim());
            folder.setUser(user);
            folder.setCreatedAt(LocalDateTime.now());
            folder.setUpdatedAt(LocalDateTime.now());
            
            // Save folder
            Folder savedFolder = folderRepository.save(folder);
            
            // Build response
            AdminFolderResponse response = AdminFolderResponse.builder()
                .folderId(savedFolder.getFolderId())
                .title(savedFolder.getTitle())
                .createdAt(savedFolder.getCreatedAt())
                .updatedAt(savedFolder.getUpdatedAt())
                .userId(savedFolder.getUser().getUserId())
                .build();

            return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "Successfully created folder", 
                    HttpStatus.CREATED, response));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(false, e.getMessage(), HttpStatus.NOT_FOUND, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, "Error creating folder: " + e.getMessage(), 
                    HttpStatus.INTERNAL_SERVER_ERROR, null));
        }
    }

    @Override
    public ResponseEntity<?> updateFolder(Long id, CreateFolderRequest request) {
        try {
            Folder folder = folderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Folder", "id", id));

            // Update folder information
            folder.setTitle(request.getTitle());
            folder.setUpdatedAt(LocalDateTime.now());

            Folder updatedFolder = folderRepository.save(folder);

            AdminFolderResponse response = AdminFolderResponse.builder()
                .folderId(updatedFolder.getFolderId())
                .title(updatedFolder.getTitle())
                .createdAt(updatedFolder.getCreatedAt())
                .updatedAt(updatedFolder.getUpdatedAt())
                .userId(updatedFolder.getUser() != null ? updatedFolder.getUser().getUserId() : null)
                .build();

            return ResponseEntity.ok(new ApiResponse(true, "Successfully updated folder", 
                HttpStatus.OK, response));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(false, e.getMessage(), HttpStatus.NOT_FOUND, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, "Error updating folder: " + e.getMessage(), 
                    HttpStatus.INTERNAL_SERVER_ERROR, null));
        }
    }

    @Override
    public ResponseEntity<?> deleteFolder(Long id) {
        try {
            Folder folder = folderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Folder", "id", id));
            
            folderRepository.delete(folder);
            return ResponseEntity.ok(new ApiResponse(true, "Successfully deleted folder", 
                HttpStatus.OK, null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(false, e.getMessage(), HttpStatus.NOT_FOUND, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, "Error deleting folder: " + e.getMessage(), 
                    HttpStatus.INTERNAL_SERVER_ERROR, null));
        }
    }

    @Override
    public ResponseEntity<?> searchFolders(String title, String username, String createdAt, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Folder> folders = folderRepository.findAll(pageable);
            
            // Filter results with fuzzy search
            List<AdminFolderResponse> filteredFolders = folders.getContent().stream()
                .filter(folder -> {
                    boolean matchesTitle = title == null || title.isEmpty() || 
                        folder.getTitle().toLowerCase().contains(title.toLowerCase());
                    
                    boolean matchesUsername = username == null || username.isEmpty() || 
                        folder.getUser().getUsername().toLowerCase().contains(username.toLowerCase());
                    
                    boolean matchesCreatedAt = createdAt == null || createdAt.isEmpty() || 
                        folder.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME)
                            .toLowerCase().contains(createdAt.toLowerCase());
                    
                    return matchesTitle && matchesUsername && matchesCreatedAt;
                })
                .map(folder -> AdminFolderResponse.builder()
                    .folderId(folder.getFolderId())
                    .title(folder.getTitle())
                    .createdAt(folder.getCreatedAt())
                    .updatedAt(folder.getUpdatedAt())
                    .userId(folder.getUser().getUserId())
                    .build())
                .collect(Collectors.toList());

            if (filteredFolders.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "No folders found matching the search criteria", 
                        HttpStatus.NOT_FOUND, null));
            }

            return ResponseEntity.ok(new ApiResponse(true, "Successfully retrieved filtered folders", 
                HttpStatus.OK, filteredFolders));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, "Error searching folders: " + e.getMessage(), 
                    HttpStatus.INTERNAL_SERVER_ERROR, null));
        }
    }
}
