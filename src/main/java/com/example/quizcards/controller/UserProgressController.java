package com.example.quizcards.controller;

import com.example.quizcards.dto.IFlashcardProgressDTO;
import com.example.quizcards.dto.IUserProgressDTO;
import com.example.quizcards.dto.request.UserProgressAdminRequest;
import com.example.quizcards.dto.response.ErrorDetail;
import com.example.quizcards.service.IUserProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
@RequestMapping("/api/v1/progress/user")
public class UserProgressController {

    @Autowired
    private IUserProgressService userProgressService;
    private static final String FETCH_ERROR_MESSAGE = "An error occurred while fetching user progress";

    @GetMapping("/{user_id}")
    public ResponseEntity<Object> findUserSetProgress(@PathVariable("user_id") Long userId) {
        try {
            if (userProgressService.findUserSetProgress(userId).isEmpty()) {
                return new ResponseEntity<>("No user progress found", HttpStatus.NO_CONTENT);
            } else {
                List<IUserProgressDTO> userProgress = userProgressService.findUserSetProgress(userId);
                return ResponseEntity.ok(userProgress);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(FETCH_ERROR_MESSAGE);
        }
    }

    @GetMapping("/{user_id}/set/{set_id}")
    public ResponseEntity<Object> findFlashcardsProgressBySetId(@PathVariable("user_id") Long userId, @PathVariable("set_id") Long setId) {
        try {
            if (userProgressService.findFlashcardsProgressBySetId(setId, userId).isEmpty()) {
                return new ResponseEntity<>("No flashcard progress found", HttpStatus.NO_CONTENT);
            } else {
                List<IFlashcardProgressDTO> cardProgress = userProgressService.findFlashcardsProgressBySetId(setId, userId);
                return ResponseEntity.ok(cardProgress);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(FETCH_ERROR_MESSAGE);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Object> addUserProgress(@RequestBody @Validated UserProgressAdminRequest request, BindingResult bindingResult) {
        if (request == null) {
            return ResponseEntity.badRequest().body("Invalid request: request cannot be null");
        }
        if (bindingResult.hasErrors()) {
            ErrorDetail errorDetail = new ErrorDetail("Validation errors");
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorDetail.addError(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(errorDetail);
        }
        try {
            if (userProgressService.existsByUserIdAndCardId(request.getUserId(), request.getCardId())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("A progress for this user and card already exists.");
            }
            userProgressService.addUserProgress(request.getProgressType(),
                    request.getIsAttention(),
                    request.getUserId(),
                    request.getCardId());
            return ResponseEntity.status(HttpStatus.CREATED).body("User Progress created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while creating the User Progress");
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteUserProgressById(@PathVariable("id") Long progressId) {
        if (userProgressService.findUserProgressById(progressId) != null) {
            try {
                userProgressService.deleteUserProgressById(progressId);
                return new ResponseEntity<>("User Progress deleted successfully", HttpStatus.OK);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the User Progress");
            }
        } else {
            return new ResponseEntity<>("User Progress not found", HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Object> updateUserProgress(@Validated @RequestBody UserProgressAdminRequest request, BindingResult bindingResult) {
        if (request == null) {
            return ResponseEntity.badRequest().body("Invalid request: request cannot be null");
        }
        if (bindingResult.hasErrors()) {
            ErrorDetail errorDetail = new ErrorDetail("Validation errors");
            for (FieldError error : bindingResult.getFieldErrors()) {
                errorDetail.addError(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(errorDetail);
        }
        try {

            if (userProgressService.existsByUserIdAndCardIdAndNotId(request.getUserId(), request.getCardId(), request.getProgressId())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("A progress for this user and card already exists.");
            }
            userProgressService.updateUserProgress(request);
            return new ResponseEntity<>("User Progress updated successfully", HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating the User Progress");
        }
    }
}
