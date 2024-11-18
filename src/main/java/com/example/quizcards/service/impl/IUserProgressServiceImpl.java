package com.example.quizcards.service.impl;

import com.example.quizcards.dto.IFlashcardProgressDTO;
import com.example.quizcards.dto.IProgressDTO;
import com.example.quizcards.dto.IUserProgressDTO;
import com.example.quizcards.dto.request.UserProgressAdminRequest;
import com.example.quizcards.repository.IUserProgressRepository;
import com.example.quizcards.service.IUserProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IUserProgressServiceImpl implements IUserProgressService {

    @Autowired
    private IUserProgressRepository userProgressRepository;

    @Override
    public List<IUserProgressDTO> findUserSetProgress(Long userId) {
        return userProgressRepository.findUserSetProgress(userId);
    }

    @Override
    public List<IFlashcardProgressDTO> findFlashcardsProgressBySetId(Long setId, Long userId) {
        return userProgressRepository.findFlashcardsProgressBySetId(setId, userId);
    }

    @Override
    public void addUserProgress(Boolean progressType, Boolean isAttention, Long userId, Long cardId) {
        userProgressRepository.createUserProgress(progressType, isAttention, userId, cardId);
    }

    @Override
    public void deleteUserProgressById(Long progressId) {
        userProgressRepository.deleteUserProgressById(progressId);
    }

    @Override
    public void updateUserProgress(UserProgressAdminRequest request) {
        userProgressRepository.updateUserProgress(request.getProgressId(), request.getProgressType(), request.getIsAttention(), request.getUserId(), request.getCardId());
    }

    @Override
    public IProgressDTO findUserProgressById(Long progressId) {
        return userProgressRepository.findUserProgressById(progressId);
    }

    @Override
    public boolean existsByUserIdAndCardId(Long userId, Long cardId) {
        return userProgressRepository.existsByUserIdAndCardId(userId, cardId) != 0;
    }

    @Override
    public boolean existsByUserIdAndCardIdAndNotId(Long userId, Long cardId, Long progressId) {
        return userProgressRepository.existsByUserIdAndCardIdAndNotId(userId, cardId, progressId) > 0;
    }
}
