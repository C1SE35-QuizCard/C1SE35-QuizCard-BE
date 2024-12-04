package com.example.quizcards.entities.TestDataPackage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ESQuestion {
    @Indexed
    private Long id; // ID của câu hỏi
    private String question;
    private String answer; // Câu trả lời cho câu hỏi tự luận
    private String yourAnswer;

    private boolean answerTrue = false;

    public boolean checkTrueAnswer() {
        if (question == null || answer == null || yourAnswer == null) {
            return false;
        }
        answerTrue = answer.equals(yourAnswer);
        return answerTrue;
    }
}
