package com.example.quizcards.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FolderCreationRequest {
    private Long folderId;

    @NotBlank(message = "Title of the folder is empty.")
    private String title;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long userId;
}
