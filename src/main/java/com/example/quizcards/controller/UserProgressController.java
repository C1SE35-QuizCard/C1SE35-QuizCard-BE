package com.example.quizcards.controller;

import com.example.quizcards.dto.IFlashcardProgressDTO;
import com.example.quizcards.dto.IUserProgressDTO;
import com.example.quizcards.dto.request.UserProgressCreationRequest;
import com.example.quizcards.security.UserPrincipal;
import com.example.quizcards.service.IUserProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @GetMapping("/list")
    @PreAuthorize("hasAnyRole('ROLE_FREE_USER', 'ROLE_PREMIUM_USER')")
    public ResponseEntity<Object> findUserSetProgress() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal up = (UserPrincipal) authentication.getPrincipal();
        try {
            if (userProgressService.findUserSetProgress(up.getId()).isEmpty()) {
                return new ResponseEntity<>("No user progress found", HttpStatus.NO_CONTENT);
            } else {
                List<IUserProgressDTO> userProgress = userProgressService.findUserSetProgress(up.getId());
                return ResponseEntity.ok(userProgress);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(FETCH_ERROR_MESSAGE);
        }
    }

    @GetMapping("/list/{set_id}")
    @PreAuthorize("hasAnyRole('ROLE_FREE_USER', 'ROLE_PREMIUM_USER')")
    public ResponseEntity<Object> findFlashcardsProgressBySetId(@PathVariable("set_id") Long setId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal up = (UserPrincipal) authentication.getPrincipal();
        try {
            if (userProgressService.findFlashcardsProgressBySetId(setId, up.getId()).isEmpty()) {
                return new ResponseEntity<>("No flashcard progress found", HttpStatus.NO_CONTENT);
            } else {
                List<IFlashcardProgressDTO> cardProgress = userProgressService.findFlashcardsProgressBySetId(setId, up.getId());
                return ResponseEntity.ok(cardProgress);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(FETCH_ERROR_MESSAGE);
        }
    }

    @GetMapping("/SetProgress/{set_id}")
    @PreAuthorize("hasAnyRole('ROLE_FREE_USER', 'ROLE_PREMIUM_USER')")
    public ResponseEntity<Object> ProgressRequestGetUserProgress(@PathVariable("set_id") Long setId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal up = (UserPrincipal) authentication.getPrincipal();
        IListUserProgressDTO userProgressDTO = userProgressService.ProgressRequestGetUserProgress(up.getId(), setId);
        return ResponseEntity.ok(userProgressDTO);
    }

    @PostMapping("/create-update")
    @PreAuthorize("hasAnyRole('ROLE_FREE_USER', 'ROLE_PREMIUM_USER')")
    public ResponseEntity<?> addUserProgressOrUpdate(@RequestBody @Validated UserProgressCreationRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal up = (UserPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(userProgressService.addUserProgressOrUpdate(up.getId(), request));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('ROLE_FREE_USER', 'ROLE_PREMIUM_USER')")
    public ResponseEntity<?> deleteUserProgressById(@PathVariable("id") Long progressId) {
        return ResponseEntity.ok(userProgressService.deleteUserProgressById(progressId));
    }

}
