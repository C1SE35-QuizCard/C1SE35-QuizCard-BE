package com.example.quizcards.service;

import com.example.quizcards.dto.IFlashcardProgressDTO;
import com.example.quizcards.dto.IProgressDTO;
import com.example.quizcards.dto.IUserProgressDTO;
import com.example.quizcards.dto.request.UserProgressRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IUserProgressService {
    List<IUserProgressDTO> findUserSetProgress(Long userId);

    List<IFlashcardProgressDTO> findFlashcardsProgressBySetId(Long setId, Long userId);

    void addUserProgress(Boolean progressType, Boolean isAttention, Long userId, Long cardId);

    void deleteUserProgressById(Long progressId);

    void updateUserProgress(UserProgressRequest request);

    void addUserProgress_2(UserProgressRequest request);

    void deleteUserProgressById_2(Long progressId);

    void updateUserProgress_2(UserProgressRequest request);

    ResponseEntity<?> assignUserProgress(UserProgressRequest request);

    ResponseEntity<?> resetUserProgress(Long setId);

    IProgressDTO findUserProgressById(Long progressId);

    boolean existsByUserIdAndCardId(Long userId, Long cardId);

    boolean existsByUserIdAndCardIdAndNotId(Long userId, Long cardId, Long progressId);
}
