package com.example.quizcards.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollectionCreationRequest {
    private Long id;
    private int folderId;
    private int setId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
