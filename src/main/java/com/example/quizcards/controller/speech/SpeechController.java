package com.example.quizcards.controller.speech;

import com.example.quizcards.service.impl.SpeechService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;

@RestController
@RequestMapping("/v2/speech")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SpeechController {
    SpeechService speechService;

    @GetMapping(value = "/synthesize-stream", produces = "audio/mp3")
    public ResponseEntity<StreamingResponseBody> synthesizeSpeech(@RequestParam(defaultValue = "") String text) {
        StreamingResponseBody stream = outputStream -> {
            try {
                speechService.convertTextToSpeech(text, outputStream);
            } catch (IOException e) {
                throw new RuntimeException("Error streaming audio", e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
        return ResponseEntity.ok()
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Content-Disposition",
                        "Accept-Ranges", "Content-Type")
                .header("Content-Disposition", "inline; filename=\"output.mp3\"")
                .header("Content-Type", "audio/mp3")
                .header("Accept-Ranges", "bytes")
                .body(stream);
    }

    @GetMapping(value = "/synthesize-stream-chunk", produces = "audio/mp3")
    public ResponseEntity<StreamingResponseBody> synthesizeSpeechChunk(
            @RequestParam(defaultValue = "") String text) {
        StreamingResponseBody stream = output -> speechService.streamSpeechChunk(text, output);
        return ResponseEntity.ok()
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS,
                        "Content-Disposition,Accept-Ranges,Content-Type")
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"output.mp3\"")
                .header(HttpHeaders.CACHE_CONTROL, "max-age=86400")
                .body(stream);
    }
}
