package com.example.quizcards.dto.request;

import com.example.quizcards.validation.ValidOffset;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StreakRequest {
//    @NotNull
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
//    @ValidOffset
//    OffsetDateTime timeFromClient;

    @NotNull
    @ValidOffset
    OffsetDateTime timeFromClient;

    @JsonSetter("timeFromClient")
    public void setTimeFromClient(String timeStr) {
        this.timeFromClient = OffsetDateTime.parse(timeStr, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    @JsonGetter("timeFromClient")
    public String getTimeFromClientAsString() {
        if (timeFromClient != null) {
            return timeFromClient.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        }
        return null; // Không xảy ra vì @NotNull đảm bảo không null
    }
}
