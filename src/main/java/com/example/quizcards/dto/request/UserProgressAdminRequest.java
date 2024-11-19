package com.example.quizcards.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProgressAdminRequest {
    private Long progressId;
    private Boolean progressType;
    private Boolean isAttention;

    @NotNull
    private Long userId;

    @NotNull
    private Long cardId;
}
