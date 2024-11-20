package com.example.quizcards.controller;

import com.example.quizcards.dto.*;
import com.example.quizcards.dto.request.FolderRequest;
import com.example.quizcards.dto.request.FolderRequest;
import com.example.quizcards.dto.response.ErrorDetail;
import com.example.quizcards.security.UserPrincipal;
import com.example.quizcards.service.IFolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
@RequestMapping("/api/folder")
public class FolderController {

    @Autowired
    private IFolderService folderService;
    private static final String FETCH_ERROR_MESSAGE = "An error occurred while fetching folder";

    @GetMapping("/{id}")
    public ResponseEntity<Object> getFolderById(@PathVariable("id") Long folderId) {
        try {
            if (folderService.getFolderById(folderId) == null) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No folder found for folder ID " + folderId);
            } else {
                IFolderDTO folder = folderService.getFolderById(folderId);
                return ResponseEntity.ok(folder);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(FETCH_ERROR_MESSAGE);
        }
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<Object> getFoldersByUserId(@PathVariable("id") Long userId){
        try {
            if (folderService.getFoldersByUserId(userId) == null) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No folder found for user ID " + userId);
            } else {
                List<IFolderDTO> folder = folderService.getFoldersByUserId(userId);
                return ResponseEntity.ok(folder);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(FETCH_ERROR_MESSAGE);
        }
    }

    @GetMapping("/search/{title}")
    public ResponseEntity<Object> searchFolderByTitle(@PathVariable("title") String title, @PathVariable("title") Long userId){
        try {
            if (folderService.searchFolderByTitle(title, userId).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No folder found for title " + title);
            } else {
                List<IFolderDTO> folders = folderService.searchFolderByTitle(title, userId);
                return ResponseEntity.ok(folders);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(FETCH_ERROR_MESSAGE);
        }
    }

    @GetMapping("/set/{folder_id}")
    public ResponseEntity<Object> getSetByFolderId(@PathVariable("folder_id") Long folderId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal up = (UserPrincipal) authentication.getPrincipal();
        try {
            if (folderService.findSetByFolderIdAndUserId(folderId, up.getId()).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No set found for folder ID " + folderId);
            } else {
                List<ISetFlashcardDTO> sets = folderService.findSetByFolderIdAndUserId(folderId, up.getId());
                return ResponseEntity.ok(sets);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(FETCH_ERROR_MESSAGE);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Object> addFolder(@RequestBody @Validated FolderRequest request, BindingResult bindingResult) {
        if (request == null) {
            return ResponseEntity.badRequest().body("Invalid request: request cannot be null");
        }
        if (bindingResult.hasErrors()) {
            ErrorDetail errorDetail = new ErrorDetail("Validation errors");
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorDetail.addError(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(errorDetail);
        }
        try {
            folderService.addFolder(request.getTitle(), request.getUserId());
            return ResponseEntity.status(HttpStatus.CREATED).body("Folder created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while creating the folder");
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteFolder(@PathVariable("id") Long folderId) {
        if (folderService.getFolderById(folderId) != null) {
            try {
                folderService.deleteFolder(folderId);
                return new ResponseEntity<>("Folder deleted successfully", HttpStatus.OK);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the Folder");
            }
        } else {
            return new ResponseEntity<>("Folder not found", HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Object> updateFolder(@Validated @RequestBody FolderRequest request, BindingResult bindingResult) {
        if (request == null) {
            return ResponseEntity.badRequest().body("Invalid request: request cannot be null");
        }
        if (bindingResult.hasErrors()) {
            ErrorDetail errorDetail = new ErrorDetail("Validation errors");
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorDetail.addError(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(errorDetail);
        }
        try {
            if (folderService.getFolderById(request.getFolderId()) == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Folder not found");
            }
            folderService.updateFolder(request);
            return new ResponseEntity<>("Folder updated successfully", HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating the Folder");
        }
    }

}
