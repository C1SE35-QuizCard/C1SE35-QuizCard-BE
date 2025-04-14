package com.example.quizcards.dto;

public interface IProgressDTO {
    Long getProgressId();

    Boolean getProgressType();

//    String getIsAttention();
    Boolean getIsAttention();

    Long getUserId();

    Long getCardId();
}
