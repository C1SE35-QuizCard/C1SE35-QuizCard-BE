package com.example.quizcards.dto.response;

public interface IProgressAnalysisDTO {
    Long getSetId();
    String getSetTitle();
    Long getTotalCardRecall();
    Long getTotalCardRemember();
    Long getTotalCardNotLearn();
}
