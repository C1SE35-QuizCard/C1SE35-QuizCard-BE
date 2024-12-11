package com.example.quizcards.entities.TestDataPackage;

import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MCQuestion implements IQuestion {
    @Indexed
    private Long id; // ID của câu hỏi
    private String question;
    private List<Answer> answerList;
    private Long answerId;

    @Transient
    private Boolean answerTrue = false;

    public Boolean getAnswerTrue() {
        if (answerId == null || answerList == null || answerList.isEmpty()) {
            return false;
        }
        for (Answer answer : answerList) {
            if (answer.getId() != null && answer.getId().equals(answerId)) {
                answerTrue = true;
                break;
            }
        }
        return answerTrue;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Answer {
        @Indexed
        private Long id; // ID của câu trả lời
        private String answer;
        private Boolean isTrue;
    }
}

