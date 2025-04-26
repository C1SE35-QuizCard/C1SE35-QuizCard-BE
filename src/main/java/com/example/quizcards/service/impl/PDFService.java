package com.example.quizcards.service.impl;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Service
public class PDFService {

    public Map<String, Object> checkPasswordProtection(MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        long startTime = System.currentTimeMillis();

        try {
            // Kiểm tra xem file có phải PDF không
            if (file == null || !file.getContentType().equals("application/pdf")) {
                result.put("success", false);
                result.put("message", "File không phải định dạng PDF");
                return result;
            }

            // Kiểm tra bảo vệ mật khẩu
            boolean isPasswordProtected = isPdfPasswordProtected(file.getInputStream());

            // Chuẩn bị kết quả
            result.put("success", true);
            result.put("isPasswordProtected", isPasswordProtected);
            result.put("fileName", file.getOriginalFilename());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Lỗi khi xử lý file: " + e.getMessage());
        }

        return result;
    }

    private boolean isPdfPasswordProtected(InputStream inputStream) {
        try {
            PDDocument document = PDDocument.load(inputStream);
            boolean isEncrypted = document.isEncrypted();
            document.close();
            return isEncrypted;
        } catch (org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException e) {
            // Nếu gặp lỗi mật khẩu không hợp lệ, tức là file có mật khẩu
            return true;
        } catch (IOException e) {
            // Xử lý các lỗi khác
            throw new RuntimeException("Không thể đọc file PDF: " + e.getMessage(), e);
        }
    }
}