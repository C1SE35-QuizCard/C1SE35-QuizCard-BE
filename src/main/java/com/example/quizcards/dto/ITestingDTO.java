package com.example.quizcards.dto;

import java.util.List;

public interface ITestingDTO {
    Long getCardId();
    String getQuestion();
    String correctAnswer();
    List<String> incorrectAnswers();
}
