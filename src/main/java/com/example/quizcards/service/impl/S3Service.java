package com.example.quizcards.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
public class S3Service {

    private final S3Client s3Client;
    private final String bucketName;

    @Value("${cloud.aws.s3.endpoint:https://${cloud.aws.s3.bucket-name}.s3.${cloud.aws.region.static}.amazonaws.com/}")
    private String s3Endpoint;

    @Autowired
    public S3Service(S3Client s3Client, String s3BucketName) {
        this.s3Client = s3Client;
        this.bucketName = s3BucketName;
    }

    public String uploadVideo(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null ?
                originalFilename.substring(originalFilename.lastIndexOf(".")) : ".mp4";
        String filename = "videos/" + UUID.randomUUID() + extension;

        // Tải file lên S3
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(filename)
                .acl("public-read")
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putObjectRequest,
                RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        // Trả về URL
        return s3Endpoint + filename;
    }

    public void deleteFile(String fileUrl) {
        if (fileUrl != null && fileUrl.startsWith(s3Endpoint)) {
            String key = fileUrl.substring(s3Endpoint.length());

            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
        }
    }
}