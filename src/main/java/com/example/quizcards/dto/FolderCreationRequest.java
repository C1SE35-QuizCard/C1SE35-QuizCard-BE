package com.example.quizcards.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public class FolderCreationRequest {
    private int folderId;

    public int getFolderId() {
        return folderId;
    }

    public void setFolderId(int folderId) {
        this.folderId = folderId;
    }

    public @NotBlank(message = "Title of the folder is empty.") String getTitle() {
        return title;
    }

    public void setTitle(@NotBlank(message = "Title of the folder is empty.") String title) {
        this.title = title;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @NotBlank(message = "Title of the folder is empty.")
    private String title;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long userId;
}
