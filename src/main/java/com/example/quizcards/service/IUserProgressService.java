package com.example.quizcards.service;

import com.example.quizcards.dto.IFlashcardProgressDTO;
import com.example.quizcards.dto.IProgressDTO;
import com.example.quizcards.dto.IUserProgressDTO;
import com.example.quizcards.dto.request.UserProgressCreationRequest;
import com.example.quizcards.entities.UserProgress;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IUserProgressService {
    List<IUserProgressDTO> findUserSetProgress(Long userId);
    List<IFlashcardProgressDTO>findFlashcardsProgressBySetId(Long setId, Long userId);
    void addUserProgress(Boolean progressType, Boolean isAttention, Long userId, Long cardId);
    void deleteUserProgressById(Long progressId);
    void updateUserProgress(UserProgressCreationRequest request);
    IProgressDTO findUserProgressById(Long progressId);
    boolean existsByUserIdAndCardId(Long userId, Long cardId);
    boolean existsByUserIdAndCardIdAndNotId(Long userId, Long cardId, Long progressId);
}
