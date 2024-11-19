package com.example.quizcards.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgressResponse {
    private Long progressId;
    private Long cardId;
//    private Long setId;
    private Long userId;
    private Integer progress;
    private Integer mark;
}
