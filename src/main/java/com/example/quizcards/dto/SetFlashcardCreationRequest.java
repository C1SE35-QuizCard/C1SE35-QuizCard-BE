package com.example.quizcards.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetFlashcardCreationRequest {
    private int setId;

    @NotBlank(message = "Title of the set flashcard is empty.")
    private String title;
    private String descriptionSet;

    public int getSetId() {
        return setId;
    }

    public void setSetId(int setId) {
        this.setId = setId;
    }

    public @NotBlank(message = "Title of the set flashcard is empty.") String getTitle() {
        return title;
    }

    public void setTitle(@NotBlank(message = "Title of the set flashcard is empty.") String title) {
        this.title = title;
    }

    public String getDescriptionSet() {
        return descriptionSet;
    }

    public void setDescriptionSet(String descriptionSet) {
        this.descriptionSet = descriptionSet;
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

    public Boolean getApproved() {
        return isApproved;
    }

    public void setApproved(Boolean approved) {
        isApproved = approved;
    }

    public Boolean getAnonymous() {
        return isAnonymous;
    }

    public void setAnonymous(Boolean anonymous) {
        isAnonymous = anonymous;
    }

    public Boolean getSharingMode() {
        return sharingMode;
    }

    public void setSharingMode(Boolean sharingMode) {
        this.sharingMode = sharingMode;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isApproved;
    private Boolean isAnonymous;
    private Boolean sharingMode;
    private Long userId;
    private int categoryId;
}
