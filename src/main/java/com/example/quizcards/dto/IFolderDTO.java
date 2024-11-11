package com.example.quizcards.dto;

import java.time.LocalDateTime;

public interface IFolderDTO {
    Long getFolderId();
    String getTitle();
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();
    Long getUserId();
    String getFirstName();
    String getLastName();
    int getSetCount();
}
