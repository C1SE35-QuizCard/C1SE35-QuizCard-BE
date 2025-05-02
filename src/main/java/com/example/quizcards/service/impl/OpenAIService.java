package com.example.quizcards.service.impl;

import com.example.quizcards.dto.AIGenFlashcard;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class OpenAIService {
    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url:https://api.openai.com/v1/chat/completions}")
    private String apiUrl;

    @Value("${openai.api.vision-url:https://api.openai.com/v1/chat/completions}")
    private String visionApiUrl;

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .build();
    private final Gson gson = new Gson();

    public List<AIGenFlashcard> generateFlashcards(String content, int numberOfFlashcards) throws IOException {
        String prompt = String.format(
                "You are an assistant to create flashcards from a document. Document content (extracted): \"\"\"%s\"\"\"\n" +
                        "Create %d flashcards from the above document. Each flashcard must follow the following format:\n" +
                        "Question: [Question text]\n" +
                        "A. [Option A]\n" +
                        "B. [Option B]\n" +
                        "C. [Option C]\n" +
                        "D. [Option D]\n" +
                        "Answer: [Letter of correct option]\n\n" +
                        "⚠ **Important Note:**\n" +
                        "- Make sure each question has exactly 4 options (A, B, C, and D)\n" +
                        "- Include one clear correct answer and mark it with 'Correct Answer: [Letter]' at the end\n" +
                        "- DO NOT add titles such as \"Flashcard 1\", \"Flashcard 2\", etc.\n" +
                        "- DO NOT add any other descriptions or explanations.\n" +
                        "- DO NOT have additional titles at the beginning (Here are ...)\n" +
                        "- If information is missing, ask general questions.\n" +
                        "- Only print out the list of flashcards in the required format.\n" +
                        "- Questions and answers must be in the SAME LANGUAGE as the above document content.",
                content, numberOfFlashcards
        );

        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("model", "gpt-4.1"); // Sử dụng model GPT-4.1

        JsonArray messagesArray = new JsonArray();
        JsonObject messageObject = new JsonObject();
        messageObject.addProperty("role", "user");
        messageObject.addProperty("content", prompt);
        messagesArray.add(messageObject);

        jsonBody.add("messages", messagesArray);
        jsonBody.addProperty("temperature", 0.7);
        jsonBody.addProperty("max_tokens", 4000);

        RequestBody body = RequestBody.create(
                gson.toJson(jsonBody),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(apiUrl)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("OpenAI API trả về lỗi: " + response.code() + " " + response.message());
            }

            JsonObject jsonResponse = gson.fromJson(response.body().string(), JsonObject.class);
            String generatedText = jsonResponse
                    .getAsJsonArray("choices")
                    .get(0)
                    .getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content")
                    .getAsString();

            return parseFlashcards(generatedText);
        }
    }

    public List<AIGenFlashcard> generateFlashcardsFromImage(String base64Image, int numberOfFlashcards) throws IOException {
        // Tạo request body cho API Vision
        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("model", "gpt-4.1"); // Sử dụng model GPT-4.1 Vision

        JsonArray messagesArray = new JsonArray();

        // Tạo message với cả text và image
        JsonObject messageObject = new JsonObject();
        messageObject.addProperty("role", "user");

        JsonArray contentArray = new JsonArray();

        // Thêm hướng dẫn text
        JsonObject textContent = new JsonObject();
        textContent.addProperty("type", "text");
        textContent.addProperty("text", "Extract all text from this image and then create flashcards from the extracted content.");
        contentArray.add(textContent);

        // Thêm hình ảnh
        JsonObject imageContent = new JsonObject();
        imageContent.addProperty("type", "image_url");

        JsonObject imageUrl = new JsonObject();
        imageUrl.addProperty("url", "data:image/jpeg;base64," + base64Image);

        imageContent.add("image_url", imageUrl);
        contentArray.add(imageContent);

        messageObject.add("content", contentArray);
        messagesArray.add(messageObject);

        // Thêm message thứ hai với prompt cụ thể để tạo flashcards
        JsonObject promptMessage = new JsonObject();
        promptMessage.addProperty("role", "user");

        String prompt = String.format(
                "Now, create %d flashcards from the text you extracted. Each flashcard must follow the following format:\n" +
                        "Question: [Question text]\n" +
                        "A. [Option A]\n" +
                        "B. [Option B]\n" +
                        "C. [Option C]\n" +
                        "D. [Option D]\n" +
                        "Answer: [Letter of correct option]\n\n" +
                        "⚠ **Important Note:**\n" +
                        "- Make sure each question has exactly 4 options (A, B, C, and D)\n" +
                        "- Include one clear correct answer and mark it with 'Correct Answer: [Letter]' at the end\n" +
                        "- DO NOT add titles such as \"Flashcard 1\", \"Flashcard 2\", etc.\n" +
                        "- DO NOT add any other descriptions or explanations.\n" +
                        "- DO NOT have additional titles at the beginning (Here are ...)\n" +
                        "- If information is missing, ask general questions.\n" +
                        "- Only print out the list of flashcards in the required format.\n" +
                        "- Questions and answers must be in the SAME LANGUAGE as the document content.",
                numberOfFlashcards
        );

        promptMessage.addProperty("content", prompt);
        messagesArray.add(promptMessage);

        jsonBody.add("messages", messagesArray);
        jsonBody.addProperty("temperature", 0.7);
        jsonBody.addProperty("max_tokens", 4000);

        RequestBody body = RequestBody.create(
                gson.toJson(jsonBody),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(visionApiUrl)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("OpenAI API trả về lỗi: " + response.code() + " " + response.message());
            }

            JsonObject jsonResponse = gson.fromJson(response.body().string(), JsonObject.class);
            String generatedText = jsonResponse
                    .getAsJsonArray("choices")
                    .get(0)
                    .getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content")
                    .getAsString();

            return parseFlashcards(generatedText);
        }
    }

    private List<AIGenFlashcard> parseFlashcards(String text) {
        List<AIGenFlashcard> flashcards = new ArrayList<>();

        // Mẫu để khớp với định dạng cụ thể từ prompt
        Pattern pattern = Pattern.compile(
                "Question:\\s*(.+?)\\s*\\n" +
                        "A\\.\\s*(.+?)\\s*\\n" +
                        "B\\.\\s*(.+?)\\s*\\n" +
                        "C\\.\\s*(.+?)\\s*\\n" +
                        "D\\.\\s*(.+?)\\s*\\n" +
                        "Answer:\\s*([A-D])",
                Pattern.DOTALL
        );
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            String questionText = matcher.group(1).trim();
            String optionA = matcher.group(2).trim();
            String optionB = matcher.group(3).trim();
            String optionC = matcher.group(4).trim();
            String optionD = matcher.group(5).trim();
            String correctAnswerLetter = matcher.group(6).trim();

            // Tạo câu hỏi đầy đủ với tất cả các lựa chọn
            String fullQuestion = questionText + "\n" +
                    "A. " + optionA + "\n" +
                    "B. " + optionB + "\n" +
                    "C. " + optionC + "\n" +
                    "D. " + optionD;

            // Tạo answer đầy đủ dựa trên chữ cái chính xác
            String fullAnswer;
            switch (correctAnswerLetter) {
                case "A":
                    fullAnswer = "A. " + optionA;
                    break;
                case "B":
                    fullAnswer = "B. " + optionB;
                    break;
                case "C":
                    fullAnswer = "C. " + optionC;
                    break;
                case "D":
                    fullAnswer = "D. " + optionD;
                    break;
                default:
                    fullAnswer = correctAnswerLetter;
            }

            flashcards.add(new AIGenFlashcard(fullQuestion, fullAnswer));
        }

        return flashcards;
    }
}
