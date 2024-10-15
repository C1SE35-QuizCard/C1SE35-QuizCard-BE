package com.example.quizcards.dto;

import com.example.quizcards.entities.SetFlashcard;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlashcardCreationRequest {
    private Long cardId;

    @NotBlank(message = "Question of the flashcard is empty.")
    private String question;

    @NotBlank(message = "Answer of the flashcard is empty.")
    private String answer;
    private String imageLink;
    private Boolean isApproved;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int setId;

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public @NotBlank(message = "Question of the flashcard is empty.") String getQuestion() {
        return question;
    }

    public void setQuestion(@NotBlank(message = "Question of the flashcard is empty.") String question) {
        this.question = question;
    }

    public @NotBlank(message = "Answer of the flashcard is empty.") String getAnswer() {
        return answer;
    }

    public void setAnswer(@NotBlank(message = "Answer of the flashcard is empty.") String answer) {
        this.answer = answer;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public Boolean getApproved() {
        return isApproved;
    }

    public void setApproved(Boolean approved) {
        isApproved = approved;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getSetId() {
        return setId;
    }

    public void setSetId(int setId) {
        this.setId = setId;
    }
}
