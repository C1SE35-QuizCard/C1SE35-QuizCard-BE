package com.example.quizcards.dto;

import java.time.LocalDateTime;

public interface IFolderDTO {
    int getFolderId();
    String getTitle();
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();
    String getFullName();
}
