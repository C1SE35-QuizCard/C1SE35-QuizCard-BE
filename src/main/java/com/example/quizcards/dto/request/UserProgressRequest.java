package com.example.quizcards.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProgressRequest {
    private Long progressId;

    private Boolean progressType;

    private Boolean isAttention;

    private Long userId;

    @NotNull
    private Long cardId;

    @JsonProperty("progressType")
    public void setProgressType(Boolean progressType) {
        this.progressType = (progressType != null) ? progressType : false;
    }

    @JsonProperty("isAttention")
    public void setIsAttention(Boolean isAttention) {
        this.isAttention = (isAttention != null) ? isAttention : false;
    }
}
