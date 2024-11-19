package com.example.quizcards.service.impl;

import com.example.quizcards.dto.IFlashcardProgressDTO;
import com.example.quizcards.dto.IProgressDTO;
import com.example.quizcards.dto.IUserProgressDTO;
import com.example.quizcards.dto.request.UserProgressRequest;
import com.example.quizcards.dto.response.ProgressResponse;
import com.example.quizcards.entities.SetFlashcard;
import com.example.quizcards.exception.ResourceNotFoundException;
import com.example.quizcards.repository.ISetFlashcardRepository;
import com.example.quizcards.repository.IUserProgressRepository;
import com.example.quizcards.security.UserPrincipal;
import com.example.quizcards.service.IUserProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class UserProgressServiceImpl implements IUserProgressService {

    @Autowired
    private IUserProgressRepository userProgressRepository;

    @Autowired
    private ISetFlashcardRepository setRepository;

    @Override
    public List<IUserProgressDTO> findUserSetProgress(Long userId) {
        return userProgressRepository.findUserSetProgress(userId);
    }

    @Override
    public List<IFlashcardProgressDTO> findFlashcardsProgressBySetId(Long setId, Long userId) {
        return userProgressRepository.findFlashcardsProgressBySetId(setId, userId);
    }

    @Override
    public ResponseEntity<?> findAllProgressByUserAndSet(Long setId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal up = (UserPrincipal) authentication.getPrincipal();
        SetFlashcard set = setRepository.findById(setId).orElseThrow(
                () -> new ResourceNotFoundException("Set", "id", setId)
        );
        if (!set.getSharingMode() && !Objects.equals(up.getId(), set.getUser().getUserId())) {
            throw new ResourceNotFoundException("Set not public", "id", setId);
        }
        List<ProgressResponse> progressResponses = userProgressRepository.findAllProgressByUserAndSet_Performance(
                up.getId(), setId);
        return ResponseEntity.ok().body(progressResponses);
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
    public void updateUserProgress(UserProgressRequest request) {
        userProgressRepository.updateUserProgress(request.getProgressId(), request.getProgressType(), request.getIsAttention(), request.getUserId(), request.getCardId());
    }

    @Override
    public void addUserProgress_2(UserProgressRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal up = (UserPrincipal) authentication.getPrincipal();
        userProgressRepository.createUserProgress(request.getProgressType(), request.getIsAttention(),
                up.getId(), request.getCardId());
    }

    @Override
    public void deleteUserProgressById_2(Long progressId) {
        userProgressRepository.deleteUserProgressById(progressId);
    }

    @Override
    public void updateUserProgress_2(UserProgressRequest request) {
        if (request.getProgressId() == null) {
            throw new NullPointerException("Progress id is null");
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal up = (UserPrincipal) authentication.getPrincipal();
        userProgressRepository.updateUserProgress(request.getProgressId(), request.getProgressType(),
                request.getIsAttention(), up.getId(), request.getCardId());
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
