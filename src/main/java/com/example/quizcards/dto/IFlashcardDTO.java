package com.example.quizcards.dto;

import java.time.LocalDateTime;

public interface IFlashcardDTO {
    Long getCardId();

    String getQuestion();

    String getAnswer();

    String getImageUrl();

    String getVideoUrl();

    Boolean getIsApproved();

    LocalDateTime getCreatedAt();

    LocalDateTime getUpdatedAt();

    String getTitle();
}
