package com.example.quizcards.dto;

import java.time.LocalDateTime;

public interface ICollectionDTO {
    Long getId();
    int getFolderId();
    int getSetId();
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();
}
