package com.example.quizcards.dto;

import java.time.LocalDateTime;

public interface ISetFlashcardDTO {
    int getSetId();
    String getTitle();
    String getDescriptionSet();
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();
    Boolean getIsApproved();
    Boolean getIsAnonymous();
    Boolean getSharingMode();
    String getFullName();
    String getCategoryName();
}
