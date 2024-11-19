package com.example.quizcards.dto;

import java.time.LocalDateTime;

public interface ISetFlashcardDTO {
    Long getSetId();

    String getTitle();

    String getDescriptionSet();

    LocalDateTime getCreatedAt();

    LocalDateTime getUpdatedAt();

    Boolean getIsApproved();

    Boolean getIsAnonymous();

    Boolean getSharingMode();

    String getFirstName();

    String getLastName();

    String getUserName();

    String getAvatar();

    String getCategoryName();

    int getCardCount();
}
