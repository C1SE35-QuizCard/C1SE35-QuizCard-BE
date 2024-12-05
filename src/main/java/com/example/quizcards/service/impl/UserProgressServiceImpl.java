package com.example.quizcards.service.impl;

import com.example.quizcards.dto.IFlashcardProgressDTO;
import com.example.quizcards.dto.IProgressDTO;
import com.example.quizcards.dto.IUserProgressDTO;
import com.example.quizcards.dto.request.UserProgressRequest;
import com.example.quizcards.dto.response.ApiResponse;
import com.example.quizcards.dto.response.ProgressResponse;
import com.example.quizcards.entities.AppUser;
import com.example.quizcards.entities.Flashcard;
import com.example.quizcards.entities.SetFlashcard;
import com.example.quizcards.entities.UserProgress;
import com.example.quizcards.exception.BadRequestException;
import com.example.quizcards.exception.ResourceNotFoundException;
import com.example.quizcards.repository.IFlashcardRepository;
import com.example.quizcards.repository.ISetFlashcardRepository;
import com.example.quizcards.repository.IUserProgressRepository;
import com.example.quizcards.security.UserPrincipal;
import com.example.quizcards.service.IUserProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserProgressServiceImpl implements IUserProgressService {

    @Autowired
    private IUserProgressRepository userProgressRepository;

    @Autowired
    private ISetFlashcardRepository setRepository;

    @Autowired
    private IFlashcardRepository cardRepository;

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
    public void updateUserProgress(UserProgressRequest request) {
        userProgressRepository.updateUserProgress(request.getProgressId(), request.getProgressType(), request.getIsAttention(), request.getUserId(), request.getCardId());
    }

    @Override
    public void addUserProgress_2(UserProgressRequest request) {
        if (request.getCardId() == null) {
            throw new BadRequestException("Progress id is null");
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal up = (UserPrincipal) authentication.getPrincipal();
        userProgressRepository.createUserProgress(request.getProgressType(), request.getIsAttention(),
                up.getId(), request.getCardId());
    }

    @Override
    public void deleteUserProgressById_2(Long progressId) {
        if (progressId == null) {
            throw new BadRequestException("Progress id is null");
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal up = (UserPrincipal) authentication.getPrincipal();
        UserProgress ups = userProgressRepository.findById(progressId).orElseThrow(
                () -> new ResourceNotFoundException("Progress", "id", progressId)
        );
        if (!Objects.equals(ups.getAppUser().getUserId(), up.getId())) {
            throw new ResourceNotFoundException("Progress not owner", "id", progressId);
        }
        userProgressRepository.deleteUserProgressById(progressId);
    }

    @Override
    public void updateUserProgress_2(UserProgressRequest request) {
        if (request.getProgressId() == null) {
            throw new BadRequestException("Progress id is null");
        }
        if (request.getCardId() == null) {
            throw new BadRequestException("Progress id is null");
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal up = (UserPrincipal) authentication.getPrincipal();
        UserProgress ups = userProgressRepository.findById(request.getProgressId()).orElseThrow(
                () -> new ResourceNotFoundException("Progress", "id", request.getProgressId())
        );
        if (!Objects.equals(ups.getAppUser().getUserId(), up.getId())) {
            throw new ResourceNotFoundException("Progress not owner", "id", request.getProgressId());
        }
        userProgressRepository.updateUserProgress(request.getProgressId(), request.getProgressType(),
                request.getIsAttention(), up.getId(), request.getCardId());
    }

    @Override
    public ResponseEntity<?> assignUserProgress(UserProgressRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal up = (UserPrincipal) authentication.getPrincipal();
        UserProgress ups = new UserProgress();
        ups.setAppUser(AppUser.builder().userId(up.getId()).build());
        ups.setFlashcard(Flashcard.builder().cardId(request.getCardId()).build());
        Optional<UserProgress> check = userProgressRepository.
                findAll(Example.of(ups, ExampleMatcher.matching().withIgnoreCase())).stream().findAny();
        if (check.isEmpty()) {
            ups.setProgressType(request.getProgressType());
            ups.setIsAttention(request.getIsAttention() == null ? false : request.getIsAttention());
        } else {
            ups = check.get();
            ups.setProgressType(request.getProgressType() == null ? ups.getProgressType() : request.getProgressType());
            ups.setIsAttention(request.getIsAttention() == null ? ups.getIsAttention() : request.getIsAttention());
        }
        ups = userProgressRepository.save(ups);

        Map<String, Object> result = new HashMap<>();
        result.put("progressId", ups.getProgressId());
        result.put("statusProgress", ups.getProgressType());
        result.put("statusMark", ups.getIsAttention());
        result.put("userId", up.getId());
        result.put("cardId", request.getCardId());
        return ResponseEntity.ok().body(result);
    }

    @Override
    public ResponseEntity<?> resetUserProgress(Long setId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal up = (UserPrincipal) authentication.getPrincipal();
        userProgressRepository.deleteUserProgressByUserAndSetId(up.getId(), setId);
        return ResponseEntity.ok().build();
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
