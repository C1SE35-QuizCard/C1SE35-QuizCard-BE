package com.example.quizcards.dto.response;

import com.example.quizcards.dto.IDeadlineReminderDTO;
import com.example.quizcards.dto.ISetFlashcardDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomeDataFreeUserResponse {
    private List<ISetFlashcardDTO> setsRecentAccessed;
    private List<ISetFlashcardDTO> setsRelevantCategory;
    private List<ISetFlashcardDTO> setsPopular;
    private List<TopCreatorsResponse> topCreators;
    private List<IDeadlineReminderDTO> deadlines;
    private FreeUserProfileResponse personData;
    private String relevantCategory;
    private String roleName;
}
