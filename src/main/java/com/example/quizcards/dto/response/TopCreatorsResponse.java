package com.example.quizcards.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopCreatorsResponse {
    private Long id;
    private String username;
    private String avatar;
    private Long totalSets;
}
