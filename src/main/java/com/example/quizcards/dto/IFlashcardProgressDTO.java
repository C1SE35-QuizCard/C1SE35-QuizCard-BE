package com.example.quizcards.dto;
public interface IFlashcardProgressDTO {
    Long getSetId();
    String getTitle();
    String getAvatar();
    String getUserName();
    Long getCardId();
    String getQuestion();
    String getAnswer();
    String getImageUrl();
    Boolean getStatusProgress();  // Mapping to progress_type
    Boolean getStatusMark();      // Mapping to marked_for_attention
}

