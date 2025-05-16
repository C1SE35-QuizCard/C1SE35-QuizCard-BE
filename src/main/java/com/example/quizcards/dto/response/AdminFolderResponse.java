package com.example.quizcards.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminFolderResponse {
    private Long folderId;
    private String title;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long userId;
} 