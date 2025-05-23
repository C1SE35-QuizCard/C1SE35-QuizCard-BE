package com.example.quizcards.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SpeechService {
    @Value("${ws-detect-language.api-key}")
    @NonFinal
    String detectLanguageApiKey;

    @Value("${ws-detect-language.uri-detect-language}")
    @NonFinal
    String detectLanguageUri;

    CacheManager cacheManager;

    Path cacheDir = Paths.get("tts-cache");

    long MAX_CACHE_BYTES = 500L * 1024 * 1024;
    long MIN_FREE_SPACE = 100L * 1024 * 1024;

    public void convertTextToSpeech(String text, OutputStream outputStream) throws Exception {
        if (!StringUtils.hasText(text)) {
            return;
        }

        String languageCode = detectLanguage(text);

        try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
            SynthesisInput input = SynthesisInput.newBuilder().setText(text).build();

            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                    .setLanguageCode(languageCode)
                    .setSsmlGender(SsmlVoiceGender.FEMALE)
                    .build();

            AudioConfig audioConfig = AudioConfig.newBuilder()
                    .setAudioEncoding(AudioEncoding.MP3)
                    .build();

            SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);

            // Nhận dữ liệu âm thanh
            ByteString audioContents = response.getAudioContent();
            if (audioContents.isEmpty()) {
                log.error("No audio content returned from Google API");
                return;
            }

            // Ghi toàn bộ vào outputStream
            outputStream.write(audioContents.toByteArray());
            outputStream.flush(); // Đảm bảo dữ liệu được gửi ngay
        } catch (IOException e) {
            log.error("Error from server: {}", e.getMessage());
        }
    }

    public String detectLanguage(String text) {
        String language = "en-US"; // default language
        RestTemplate restTemplate = new RestTemplate();
        String url = detectLanguageUri;
        String apiKey = detectLanguageApiKey;

        try {
            // Thiết lập headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            // Tạo body request
            String body = "{\"q\":\"" + text + "\"}";
            HttpEntity<String> entity = new HttpEntity<>(body, headers);

            // Gửi request và nhận response
            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, String.class);

            // Parse JSON response
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            JsonNode detections = root.path("data").path("detections");

            if (detections.isArray() && !detections.isEmpty()) {
                JsonNode firstDetection = detections.get(0);
                language = firstDetection.path("language").asText();
            }
        } catch (Exception e) {
            // Xử lý lỗi (có thể log lỗi ở đây)
            System.err.println("Error detecting language: " + e.getMessage());
            return language; // Trả về default language nếu có lỗi
        }

        return language;
    }

    public void streamSpeechChunk(String text, OutputStream output) throws IOException {
        if (!StringUtils.hasText(text)) {
            return;
        }
        String hash = DigestUtils.sha256Hex(text);
        Path cachedFile = cacheDir.resolve(hash + ".mp3");
        if (Files.exists(cachedFile)) {
            log.info("Streaming from disk cache: {}", cachedFile);
            try (InputStream in = Files.newInputStream(cachedFile)) {
                chunk(in, output);
            }
            return;
        }

        @SuppressWarnings("unchecked")
        Cache<Object, Object> languageCache =
                (Cache<Object, Object>) cacheManager.getCache("textLangDetectForSpeech").getNativeCache();

        String language = (String) languageCache.get(text, currentText ->
                detectLanguage(currentText.toString())
        );

        ByteString audioContent;
        try {
            audioContent = synthesizeWithGoogle(text, language);
        } catch (Exception e) {
            log.error("TTS synthesis failed: {}", e.getMessage());
            throw new IOException(e);
        }

        InputStream ttsStream = audioContent.newInput();
        Files.createDirectories(cacheDir);
        try (OutputStream fileOut = Files.newOutputStream(cachedFile)) {
            chunkAndCache(ttsStream, output, fileOut);
        }
    }

    private void chunk(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[4096];
        int len;
        while ((len = in.read(buffer)) != -1) {
            out.write(buffer, 0, len);
            out.flush();
        }
    }

    private void chunkAndCache(InputStream in, OutputStream out, OutputStream fileOut) throws IOException {
        byte[] buffer = new byte[4096];
        int len;
        while ((len = in.read(buffer)) != -1) {
            fileOut.write(buffer, 0, len);
            out.write(buffer, 0, len);
            out.flush();
        }
    }

    private boolean hasEnoughDiskSpace(long newFileSize) throws IOException {
        FileStore store = Files.getFileStore(cacheDir);
        long usable = store.getUsableSpace();
        return usable - newFileSize > MIN_FREE_SPACE;
    }

    private void evictOldFilesIfNeeded() throws IOException {
        if (!Files.exists(cacheDir)) return;
        List<Path> files = Files.list(cacheDir)
                .filter(p -> p.toString().endsWith(".mp3"))
                .sorted(Comparator.comparingLong(p -> p.toFile().lastModified()))
                .toList();

        long total = files.stream().mapToLong(p -> p.toFile().length()).sum();
        Iterator<Path> it = files.iterator();
        while (total > MAX_CACHE_BYTES && it.hasNext()) {
            Path oldest = it.next();
            long len = Files.size(oldest);
            Files.delete(oldest);
            total -= len;
            log.info("Evicted cache file: {}", oldest);
        }
    }

    private ByteString synthesizeWithGoogle(String text, String languageCode) throws Exception {
        try (TextToSpeechClient client = TextToSpeechClient.create()) {
            SynthesisInput input = SynthesisInput.newBuilder().setText(text).build();
            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                    .setLanguageCode(languageCode)
                    .setSsmlGender(SsmlVoiceGender.FEMALE)
                    .build();
            AudioConfig config = AudioConfig.newBuilder()
                    .setAudioEncoding(AudioEncoding.MP3)
                    .build();
            SynthesizeSpeechResponse resp = client.synthesizeSpeech(input, voice, config);
            return resp.getAudioContent();
        }
    }
}
