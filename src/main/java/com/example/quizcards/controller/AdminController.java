package com.example.quizcards.controller;

import com.example.quizcards.dto.FlashcardSetDTO;
import com.example.quizcards.dto.response.ITopCreatorsResponse;
import com.example.quizcards.dto.response.ApiResponse;
import com.example.quizcards.dto.response.RevenueStatisticsResponse;
import com.example.quizcards.dto.response.AdminFolderResponse;
import com.example.quizcards.service.ISetFlashcardService;
import com.example.quizcards.service.IUserSubscriptionService;
import com.example.quizcards.dto.request.CreateFolderRequest;
import com.example.quizcards.service.IFolderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class AdminController {

    @Autowired
    private ISetFlashcardService setFlashcardService;

    @Autowired
    private IUserSubscriptionService userSubscriptionService;

    @Autowired
    private IFolderService folderService;

    // Top Creators API
    @GetMapping("/top-creators")
    public ResponseEntity<?> getTopCreators() {
        try {
            List<ITopCreatorsResponse> topCreators = setFlashcardService.loadTop10PopularCreators();
            return ResponseEntity.ok(new ApiResponse(true, "Successfully retrieved top creators", HttpStatus.OK, topCreators));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error retrieving top creators: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null));
        }
    }

    // Top Sets API
    @GetMapping("/top-sets")
    public ResponseEntity<?> getTopSets() {
        try {
            List<FlashcardSetDTO> topSets = setFlashcardService.loadTop10PopularFlashcardSets(null);
            return ResponseEntity.ok(new ApiResponse(true, "Successfully retrieved top sets", HttpStatus.OK, topSets));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error retrieving top sets: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null));
        }
    }

    // Revenue Statistics API
    @GetMapping("/revenue/statistics")
    public ResponseEntity<?> getRevenueStatistics(
            @RequestParam(value = "year", required = false) Integer year) {
        try {
            List<RevenueStatisticsResponse> statistics = userSubscriptionService.getRevenueStatisticsByYear(year);
            return ResponseEntity.ok(new ApiResponse(true, "Successfully retrieved revenue statistics", HttpStatus.OK, statistics));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error retrieving revenue statistics: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null));
        }
    }

    // Folder Management APIs
    @GetMapping("/folders")
    public ResponseEntity<?> getAllFolders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            List<AdminFolderResponse> folders = folderService.getAllFoldersWithUserInfo(page, size);
            return ResponseEntity.ok(new ApiResponse(true, "Successfully retrieved folders", HttpStatus.OK, folders));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error retrieving folders: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null));
        }
    }

    @GetMapping("/folders/{id}")
    public ResponseEntity<?> getFolderById(@PathVariable Long id) {
        return folderService.getFolderById(id);
    }

    @PostMapping("/folders")
    public ResponseEntity<?> createFolder(@Valid @RequestBody CreateFolderRequest request) {
        return folderService.createFolder(request);
    }

    @PutMapping("/folders/{id}")
    public ResponseEntity<?> updateFolder(
            @PathVariable Long id,
            @Valid @RequestBody CreateFolderRequest request) {
        return folderService.updateFolder(id, request);
    }

    @DeleteMapping("/folders/{id}")
    public ResponseEntity<?> deleteFolder(@PathVariable Long id) {
        return folderService.deleteFolder(id);
    }

    @GetMapping("/folders/search")
    public ResponseEntity<?> searchFolders(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String createdAt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return folderService.searchFolders(title, username, createdAt, page, size);
    }
}
