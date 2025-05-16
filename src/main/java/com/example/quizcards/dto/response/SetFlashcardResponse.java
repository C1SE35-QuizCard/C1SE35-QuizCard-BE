package com.example.quizcards.dto.response;

import com.example.quizcards.entities.SetFlashcard;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetFlashcardResponse {
    private Long setId;
    private String title;
    private String descriptionSet;
    private Boolean isApproved;
    private Boolean isAnonymous;
    private Boolean sharingMode;
    private String userName;
    private String categoryName;

    public static SetFlashcardResponse fromEntity(SetFlashcard set) {
        return new SetFlashcardResponse(
                set.getSetId(),
                set.getTitle(),
                set.getDescriptionSet(),
                set.getIsApproved(),
                set.getIsAnonymous(),
                set.getSharingMode(),
                set.getUser() != null ? set.getUser().getUsername() : null,
                set.getCategory() != null ? set.getCategory().getCategoryName() : null

        );
    }
}