package com.example.quizcards.service.impl;

import com.example.quizcards.dto.AIGenFlashcard;
import com.example.quizcards.dto.request.AIGenFlashcardRequest;
import com.example.quizcards.dto.response.AiGenFlashcardResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIGenFlashcardService {

    private final DocumentProcessorService documentProcessorService;
    private final ImageProcessorService imageProcessorService;
    private final OpenAIService openAIService;

    public AiGenFlashcardResponse generateFlashcards(MultipartFile[] files, AIGenFlashcardRequest request) {
        long startTime = System.currentTimeMillis();
        int numberOfFlashcards = request.getNumberOfFlashcards();
        String pdfPassword = request.getPdfPassword();

        // Validate input
        if (numberOfFlashcards < 2 || numberOfFlashcards > 200) {
            return new AiGenFlashcardResponse(
                    null,
                    "Số lượng flashcard phải từ 2 đến 200.",
                    false,
                    System.currentTimeMillis() - startTime
            );
        }

        if (files == null || files.length == 0) {
            return new AiGenFlashcardResponse(
                    null,
                    "Không có file nào được gửi.",
                    false,
                    System.currentTimeMillis() - startTime
            );
        }

        try {
            log.debug("Processing {} files for flashcards", files.length);

            List<AIGenFlashcard> allFlashcards = new ArrayList<>();
            StringBuilder combinedText = new StringBuilder();
            int successfulFiles = 0;
            List<String> successfulFilenames = new ArrayList<>();
            List<MultipartFile> processedTextFiles = new ArrayList<>();

            int remainingFlashcards = numberOfFlashcards;

            // Nếu số lượng file nhiều hơn số flashcard yêu cầu
            if (files.length > numberOfFlashcards) {
                // Chỉ xử lý đủ số file cần thiết
                int filesToProcess = numberOfFlashcards;

                for (int i = 0; i < filesToProcess; i++) {
                    MultipartFile file = files[i];
                    try {
                        processFile(file, allFlashcards, combinedText, successfulFilenames,
                                processedTextFiles, pdfPassword);
                        successfulFiles++;

                        // Mỗi file tạo 1 flashcard
                        remainingFlashcards--;

                        // Dừng khi đã đủ số flashcard yêu cầu
                        if (remainingFlashcards <= 0) {
                            break;
                        }
                    } catch (Exception e) {
                        log.error("Failed to process file: " + file.getOriginalFilename(), e);
                    }
                }
            } else {
                // Trường hợp ít file hơn số flashcard yêu cầu
                int filesCount = files.length;
                int baseFlashcardsPerFile = numberOfFlashcards / filesCount;
                int extraFlashcards = numberOfFlashcards % filesCount;

                for (int i = 0; i < filesCount; i++) {
                    MultipartFile file = files[i];
                    try {
                        // Tính số flashcard cho file này
                        int flashcardsForThisFile = baseFlashcardsPerFile;
                        if (extraFlashcards > 0) {
                            flashcardsForThisFile++;
                            extraFlashcards--;
                        }

                        processFileWithCount(file, flashcardsForThisFile, allFlashcards, combinedText,
                                successfulFilenames, processedTextFiles, pdfPassword);

                        successfulFiles++;
                        remainingFlashcards -= flashcardsForThisFile;
                    } catch (Exception e) {
                        log.error("Failed to process file: " + file.getOriginalFilename(), e);
                    }
                }
            }

            // Xử lý text tổng hợp nếu có và vẫn cần thêm flashcard
            if (combinedText.length() > 0 && remainingFlashcards > 0) {
                List<AIGenFlashcard> textFlashcards = openAIService.generateFlashcards(
                        combinedText.toString(),
                        remainingFlashcards
                );
                allFlashcards.addAll(textFlashcards);

                // Thêm tên các file đã xử lý bằng phương pháp text vào danh sách thành công
                // nếu chưa có trong danh sách
                for (MultipartFile file : processedTextFiles) {
                    if (!successfulFilenames.contains(file.getOriginalFilename())) {
                        successfulFilenames.add(file.getOriginalFilename());
                    }
                }
            }

            // Kiểm tra kết quả cuối cùng
            if (allFlashcards.isEmpty()) {
                return new AiGenFlashcardResponse(
                        null,
                        "Không thể tạo flashcard từ các file đã gửi.",
                        false,
                        System.currentTimeMillis() - startTime
                );
            }

            long processingTime = System.currentTimeMillis() - startTime;

            // Tạo chuỗi tên file cho thông báo
            String filesString = successfulFilenames.stream()
                    .map(name -> "'" + name + "'")
                    .collect(Collectors.joining(", "));

            return new AiGenFlashcardResponse(
                    allFlashcards,
                    String.format("Đã tạo %d flashcard từ %s thành công",
                            allFlashcards.size(),
                            filesString),
                    true,
                    processingTime
            );
        } catch (Exception e) {
            log.error("Error processing files for flashcards", e);
            return new AiGenFlashcardResponse(
                    null,
                    "Lỗi hệ thống khi xử lý tệp tin: " + e.getMessage(),
                    false,
                    System.currentTimeMillis() - startTime
            );
        }
    }

    private void processFile(MultipartFile file, List<AIGenFlashcard> allFlashcards,
                             StringBuilder combinedText, List<String> successfulFilenames,
                             List<MultipartFile> processedTextFiles, String pdfPassword) throws Exception {

        String fileName = file.getOriginalFilename().toLowerCase();
        String contentType = file.getContentType();

        log.debug("Processing file: {} of type {}", fileName, contentType);

        // Determine if file is an image or document
        if (isImageFile(fileName, contentType)) {
            // Process as image
            String extractedText = imageProcessorService.extractTextFromImage(file);

            if (imageProcessorService.isBase64EncodedImage(extractedText)) {
                List<AIGenFlashcard> imageFlashcards = openAIService.generateFlashcardsFromImage(
                        extractedText, 1
                );
                allFlashcards.addAll(imageFlashcards);
                successfulFilenames.add(file.getOriginalFilename());
            } else {
                // If not Base64, it's extracted text
                combinedText.append(extractedText).append("\n\n");
                processedTextFiles.add(file);
            }
        } else {
            // Process as document
            String extractedText = documentProcessorService.extractTextFromDocument(file, pdfPassword);
            combinedText.append(extractedText).append("\n\n");
            processedTextFiles.add(file);
            successfulFilenames.add(file.getOriginalFilename());
        }
    }

    private void processFileWithCount(MultipartFile file, int flashcardCount, List<AIGenFlashcard> allFlashcards,
                                      StringBuilder combinedText, List<String> successfulFilenames,
                                      List<MultipartFile> processedTextFiles, String pdfPassword) throws Exception {

        String fileName = file.getOriginalFilename().toLowerCase();
        String contentType = file.getContentType();

        log.debug("Processing file with count {}: {} of type {}", flashcardCount, fileName, contentType);

        // Determine if file is an image or document
        if (isImageFile(fileName, contentType)) {
            // Process as image
            String extractedText = imageProcessorService.extractTextFromImage(file);

            if (imageProcessorService.isBase64EncodedImage(extractedText)) {
                List<AIGenFlashcard> imageFlashcards = openAIService.generateFlashcardsFromImage(
                        extractedText, flashcardCount
                );
                allFlashcards.addAll(imageFlashcards);
                successfulFilenames.add(file.getOriginalFilename());
            } else {
                // If not Base64, it's extracted text to be processed later
                combinedText.append(extractedText).append("\n\n");
                processedTextFiles.add(file);
            }
        } else {
            // Process as document
            String extractedText = documentProcessorService.extractTextFromDocument(file, pdfPassword);

            // Generate flashcards directly from this document
            List<AIGenFlashcard> docFlashcards = openAIService.generateFlashcards(
                    extractedText, flashcardCount
            );
            allFlashcards.addAll(docFlashcards);
            successfulFilenames.add(file.getOriginalFilename());
        }
    }

    private boolean isImageFile(String fileName, String contentType) {
        // Check based on file extension
        boolean isImageByExtension = fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")
                || fileName.endsWith(".png") || fileName.endsWith(".gif") || fileName.endsWith(".bmp");

        // Check based on content type
        boolean isImageByContentType = contentType != null && contentType.startsWith("image/");

        return isImageByExtension || isImageByContentType;
    }
}