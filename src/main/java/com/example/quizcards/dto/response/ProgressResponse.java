package com.example.quizcards.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public interface ProgressResponse {
    Long getProgressId();
    Long getCardId();
//    private Long setId;
    Long getUserId();
    Integer getProgress();
    Integer getMark();
}
