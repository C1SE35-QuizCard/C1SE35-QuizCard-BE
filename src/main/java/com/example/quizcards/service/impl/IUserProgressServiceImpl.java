package com.example.quizcards.service.impl;

import com.example.quizcards.dto.IFlashcardProgressDTO;
import com.example.quizcards.dto.IProgressDTO;
import com.example.quizcards.dto.IUserProgressDTO;
import com.example.quizcards.dto.request.UserProgressCreationRequest;
import com.example.quizcards.dto.response.ApiResponse;
import com.example.quizcards.entities.AppUser;
import com.example.quizcards.entities.Flashcard;
import com.example.quizcards.entities.UserProgress;
import com.example.quizcards.exception.ResourceNotFoundException;
import com.example.quizcards.helpers.UserProgressHelpers.IUserProgressHelpers;
import com.example.quizcards.repository.IUserProgressRepository;
import com.example.quizcards.service.IUserProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class IUserProgressServiceImpl implements IUserProgressService {

    @Autowired
    private IUserProgressRepository userProgressRepository;

    @Autowired
    private IUserProgressHelpers userProgressHelpers;

    @Override
    public List<IUserProgressDTO> findUserSetProgress(Long userId){
        return userProgressRepository.findUserSetProgress(userId);
    }

    @Override
    public List<IFlashcardProgressDTO>findFlashcardsProgressBySetId(Long setId, Long userId){
        return userProgressRepository.findFlashcardsProgressBySetId(setId, userId);
    }

    @Override
    public IProgressDTO findUserProgressById(Long progressId){
        return userProgressRepository.findUserProgressById(progressId);
    }


    @Override
    public ResponseEntity<?> addUserProgressOrUpdate(Long userId, UserProgressCreationRequest request){
        if(userProgressRepository.existsByUserIdAndCardId(userId, request.getCardId()) > 0){
            UserProgress userProgress = userProgressRepository.
                    findUserProgressByUserIdAndCardId(userId, request.getCardId());
            userProgress.setIsAttention(request.getIsAttention());
            userProgress.setProgressType(request.getProgressType());
            userProgressRepository.save(userProgress);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse(true, "User progress updated successfully"));
        }
        AppUser au = AppUser.builder().userId(userId).build();
        Flashcard fl = Flashcard.builder().cardId(request.getCardId()).build();
        UserProgress userProgress = new UserProgress();
        userProgress.setAppUser(au);
        userProgress.setFlashcard(fl);
        userProgress.setIsAttention(request.getIsAttention());
        userProgress.setProgressType(request.getProgressType());
        userProgressRepository.save(userProgress);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "User progress created successfully"));
    }

    @Override
    public ResponseEntity<?> deleteUserProgressById(Long progressId){
        UserProgress userProgress = userProgressRepository.findById(progressId)
                .orElseThrow(() -> new ResourceNotFoundException("User Progress", "id", progressId));
        userProgressHelpers.handleDeleteUserProgress(progressId);
        userProgressRepository.delete(userProgress);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse(true, "User Progress deleted successfully"));
    }
}
