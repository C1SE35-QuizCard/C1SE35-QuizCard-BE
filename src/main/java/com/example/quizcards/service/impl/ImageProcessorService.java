package com.example.quizcards.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

@Service
@Slf4j
public class ImageProcessorService {
    @Value("${tesseract.datapath:/usr/share/tesseract-ocr/4.00/tessdata}")
    private String tessdataPath;

    @Value("${ocr.method:openai}")
    private String ocrMethod;

    @Value("${tesseract.languages:jpn+kor+chi_sim+vie+eng}")
    private String tesseractLanguages;

    @Cacheable(cacheNames = "extractedText", value = "extractedText", key = "#file.originalFilename + #file.size")
    public String extractTextFromImage(MultipartFile file) throws IOException, TesseractException {
        log.debug("Processing image: {}", file.getOriginalFilename());

        // Kiểm tra loại file
        String fileName = file.getOriginalFilename() != null ? file.getOriginalFilename().toLowerCase() : "";
        if (!(fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png"))) {
            throw new IllegalArgumentException("Định dạng ảnh không được hỗ trợ. Hỗ trợ: JPG, JPEG, PNG");
        }

        if ("tesseract".equals(ocrMethod)) {
            log.debug("Using Tesseract OCR for image processing");
            return extractTextWithTesseract(file);
        } else {
            log.debug("Using OpenAI for image processing, returning Base64 encoded image");
            // Mặc định sẽ trả về hình ảnh dưới dạng Base64 để OpenAI xử lý
            return Base64.getEncoder().encodeToString(file.getBytes());
        }
    }

    private String extractTextWithTesseract(MultipartFile file) throws IOException, TesseractException {
        log.debug("Extracting text from image using Tesseract OCR");
        // Tạo file tạm thời
        Path tempFile = Files.createTempFile("ocr-image-", getFileExtension(file.getOriginalFilename()));
        try (FileOutputStream fos = new FileOutputStream(tempFile.toFile())) {
            fos.write(file.getBytes());
        }

        // Sử dụng Tesseract OCR để trích xuất văn bản
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath(tessdataPath);
        tesseract.setLanguage(tesseractLanguages);
        tesseract.setPageSegMode(1); // Automatic page segmentation with OSD
        tesseract.setOcrEngineMode(1); // Neural net LSTM engine

        try {
            String extractedText = tesseract.doOCR(tempFile.toFile());
            log.debug("Extracted text length: {}", extractedText.length());
            return extractedText;
        } finally {
            // Xóa file tạm dù có lỗi hay không
            try {
                Files.deleteIfExists(tempFile);
            } catch (IOException e) {
                log.warn("Failed to delete temporary file: {}", tempFile, e);
            }
        }
    }

    public boolean isBase64EncodedImage(String text) {
        return text != null && !text.contains(" ") && !text.contains("\n") && text.length() > 100;
    }

    private String getFileExtension(String fileName) {
        if (fileName == null) return ".tmp";

        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return fileName.substring(lastDotIndex);
        }
        return ".tmp";
    }
}
