package com.example.quizcards.service;

import com.example.quizcards.dto.IFlashcardProgressDTO;
import com.example.quizcards.dto.IProgressDTO;
import com.example.quizcards.dto.IUserProgressDTO;
import com.example.quizcards.dto.request.UserProgressCreationRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IUserProgressService {
    List<IUserProgressDTO> findUserSetProgress(Long userId);
    List<IFlashcardProgressDTO>findFlashcardsProgressBySetId(Long setId, Long userId);
    IProgressDTO findUserProgressById(Long progressId);


    ResponseEntity<?> addUserProgressOrUpdate(Long userId, UserProgressCreationRequest request);
    ResponseEntity<?> deleteUserProgressById(Long progressId);
}
