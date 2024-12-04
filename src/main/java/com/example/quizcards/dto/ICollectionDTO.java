package com.example.quizcards.dto;

import java.time.LocalDateTime;

public interface ICollectionDTO {
    Long getId();

    Long getFolderId();

    String getFolderName();

    Long getSetId();

    String getSetTitle();

    String getSetDescription();

    Long getUserId();

    String getUserName();

    String getFirstName();

    String getLastName();

    LocalDateTime getCreatedAt();

    LocalDateTime getUpdatedAt();
}
