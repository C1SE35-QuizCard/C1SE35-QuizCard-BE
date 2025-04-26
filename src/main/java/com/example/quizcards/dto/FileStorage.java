package com.example.quizcards.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileStorage {
    private String id;
    private String originalFileName;
    private String contentType;
    private byte[] data;
    private long size;
    private LocalDateTime uploadTime;

    public static FileStorage fromMultipartFile(MultipartFile file) throws IOException {
        FileStorage storage = new FileStorage();
        storage.setId(UUID.randomUUID().toString());
        storage.setOriginalFileName(file.getOriginalFilename());
        storage.setContentType(file.getContentType());
        storage.setData(file.getBytes());
        storage.setSize(file.getSize());
        storage.setUploadTime(LocalDateTime.now());
        return storage;
    }
}
