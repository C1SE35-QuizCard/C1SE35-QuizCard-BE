package com.example.quizcards.dto;

import java.time.LocalDateTime;

public interface ICollectionDTO {
    Long getId();
    Long getFolderId();
    Long getSetId();
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();
}
