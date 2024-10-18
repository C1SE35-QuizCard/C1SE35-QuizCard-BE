package com.example.quizcards.dto;

import com.example.quizcards.entities.Folder;
import com.example.quizcards.entities.SetFlashcard;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollectionUpdateRequest {
    private Long id;
    private Long folderId;
    private Long setId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
