package com.example.quizcards.controller;

import com.example.quizcards.dto.IFlashcardDTO;
import com.example.quizcards.dto.ISetFlashcardDTO;
import com.example.quizcards.dto.request.QueryDTO;
import com.example.quizcards.dto.request.SetFlashcardInitializeRequest;
import com.example.quizcards.dto.request.SetFlashcardRequest;
import com.example.quizcards.dto.response.ApiResponse;
import com.example.quizcards.dto.response.ErrorDetail;
import com.example.quizcards.dto.response.SearchSetFlashResponse;
import com.example.quizcards.security.UserPrincipal;
import com.example.quizcards.service.ISetFlashcardService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
@RequestMapping("/api/v1/set")
public class SetFlashcardController {
    @Autowired
    private ISetFlashcardService setFlashcardService;
    private static final String FETCH_ERROR_MESSAGE = "An error occurred while fetching set flashcards";

    @GetMapping("/list")
    public ResponseEntity<Object> findAllSetFlashcard() {
        try {
            if (setFlashcardService.getAll().isEmpty()) {
                return new ResponseEntity<>("No set flashcard found", HttpStatus.NO_CONTENT);
            } else {
                List<ISetFlashcardDTO> set = setFlashcardService.getAll();
                return ResponseEntity.ok(set);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(FETCH_ERROR_MESSAGE);
        }
    }

    @GetMapping("/list/{id}")
    public ResponseEntity<Object> findAllFlashcardBySetId(@PathVariable("id") Long setId) {
        try {
            if (!setFlashcardService.getAllFlashcardBySetId(setId).isEmpty()) {
                List<IFlashcardDTO> flashcards = setFlashcardService.getAllFlashcardBySetId(setId);
                return ResponseEntity.ok(flashcards);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No flashcards found for set ID " + setId);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(FETCH_ERROR_MESSAGE + e.getMessage());
        }
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<Object> detailSetFlashcardById(@PathVariable("id") Long setId) {
        try {
            if (setFlashcardService.findBySetId(setId) == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Set Flashcard not found");
            }
            ISetFlashcardDTO setFlashcard = setFlashcardService.findBySetId(setId);
            return ResponseEntity.ok(setFlashcard);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(FETCH_ERROR_MESSAGE);
        }
    }

    @GetMapping("/set-detail/{id}")
    public ResponseEntity<?> detailSetFlashcardById_2(@PathVariable("id") Long setId) {
        return setFlashcardService.findBySetId_2(setId);
    }

    @GetMapping("/count-public-set/{id}")
    public ResponseEntity<?> countSetFlashcardCreatedPublicByUserName(@PathVariable("id") Long userId) {
        return setFlashcardService.countSetFlashcardCreatedPublic(userId);
    }

    @GetMapping("/count-set")
    @PreAuthorize("hasAnyRole('ROLE_FREE_USER', 'ROLE_PREMIUM_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> countSetFlashcardCreated() {
        return setFlashcardService.countSetFlashcardCreatedInCurrentUser();
    }

    @GetMapping("/count-set-in-current-date")
    @PreAuthorize("hasAnyRole('ROLE_FREE_USER', 'ROLE_PREMIUM_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> countSetFlashcardCreatedPerDate() {
        return setFlashcardService.countSetFlashcardCreatedPerDayInCurrentUser();
    }

    @GetMapping("/get-current-sets-by-settings")
    @PreAuthorize("hasAnyRole('ROLE_FREE_USER', 'ROLE_PREMIUM_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> getSetSettingsByIdOnCurrentUser(@RequestParam Long limit) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal up = (UserPrincipal) authentication.getPrincipal();
        if (limit == null || limit < 1) {
            limit = Long.MAX_VALUE;
        }
        return setFlashcardService.getListFlashcardsByNearbySetting(up.getId(), limit);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('NO_ROLE')")
    public ResponseEntity<Object> createSetFlashcard(@RequestBody @Validated SetFlashcardRequest request, BindingResult bindingResult) {
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal up = (UserPrincipal) authentication.getPrincipal();
        try {
            setFlashcardService.addSetFlashcard(request.getTitle(),
                    request.getDescriptionSet(),
                    request.getIsApproved(),
                    request.getIsAnonymous(),
                    request.getSharingMode(),
                    up.getId(),
                    request.getCategoryId());
            return ResponseEntity.status(HttpStatus.CREATED).body("Set Flashcard created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while creating the set flashcard");
        }
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('NO_ROLE')")
    public ResponseEntity<Object> deleteSetFlashcardById(@PathVariable("id") Long setId) {
        if (setFlashcardService.findBySetId(setId) != null) {
            try {
                setFlashcardService.deleteSetFlashcard(setId);
                return new ResponseEntity<>("Set Flashcard deleted successfully", HttpStatus.OK);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the set flashcard");
            }
        } else {
            return new ResponseEntity<>("Set Flashcard not found", HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('NO_ROLE')")
    public ResponseEntity<Object> updateSetFlashcard(@Validated @RequestBody SetFlashcardRequest request, BindingResult bindingResult) {
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
            if (setFlashcardService.findBySetId(request.getSetId()) == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Set Flashcard not found");
            }
            setFlashcardService.updateSetFlashcard(request);
            return new ResponseEntity<>("Set Flashcard updated successfully", HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating the set flashcard");
        }
    }

    @PostMapping("/create-new-set")
    @PreAuthorize("hasAnyRole('ROLE_FREE_USER', 'ROLE_PREMIUM_USER')")
    public ResponseEntity<?> createSetFlashcard_2(@Valid @RequestBody SetFlashcardInitializeRequest request) {
        Long setId = setFlashcardService.createNewSetFlashcards(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "Created set successfully",
                        HttpStatus.OK, setId));
    }

    @PutMapping("/update-set")
    @PreAuthorize("hasAnyRole('ROLE_FREE_USER', 'ROLE_PREMIUM_USER')")
    public ResponseEntity<?> updateSetFlashcard_2(@Valid @RequestBody SetFlashcardRequest request) {
        setFlashcardService.updateSetFlashcard(request);
        return ResponseEntity.ok()
                .body(new ApiResponse(true, "Set Flashcard updated successfully"));
    }

    @DeleteMapping("/delete-set")
    @PreAuthorize("hasAnyRole('ROLE_FREE_USER', 'ROLE_PREMIUM_USER')")
    public ResponseEntity<?> deleteSetFlashcardById_2(@Valid @RequestBody SetFlashcardRequest request) {
        setFlashcardService.deleteSetFlashcard(request.getSetId());
        return ResponseEntity.ok()
                .body(new ApiResponse(true, "Set Flashcard deleted successfully"));
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchByTitle(@RequestParam("query") String query) {
        List<SearchSetFlashResponse> set = setFlashcardService.searchByTitleAndCategory(query);
        return ResponseEntity.ok(set);
    }

    @GetMapping("/search-my-course")
    public ResponseEntity<?> searchMyCourse(@RequestParam("query") QueryDTO query) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal up = (UserPrincipal) auth.getPrincipal();
        List<SearchSetFlashResponse> set = setFlashcardService.searchByMyCourse(query, up.getId());
        return ResponseEntity.ok(set);
    }


    @GetMapping("/sort")
    public ResponseEntity<Object> sortByUpdatedDate() {
        try {
            if (setFlashcardService.sortByUpdatedDate().isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No flashcard sets found");
            } else {
                List<ISetFlashcardDTO> set = setFlashcardService.sortByUpdatedDate();
                return ResponseEntity.ok(set);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(FETCH_ERROR_MESSAGE);
        }
    }

    @GetMapping("/list/sets")
    public ResponseEntity<Object> getAllSetByUserId() {
        try {
            if (setFlashcardService.getAllSetByUserId().isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("The user does not have any flashcard sets");
            } else {
                List<ISetFlashcardDTO> set = setFlashcardService.getAllSetByUserId();
                return ResponseEntity.ok(set);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(FETCH_ERROR_MESSAGE);
        }
    }

    @GetMapping("/public")
    public ResponseEntity<Object> getAllSetPublic() {
        try {
            if (setFlashcardService.getAllSetPublic().isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No flashcard sets found");
            } else {
                List<ISetFlashcardDTO> set = setFlashcardService.getAllSetPublic();
                return ResponseEntity.ok(set);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(FETCH_ERROR_MESSAGE);
        }
    }

    @GetMapping("/public/{userId}")
    public ResponseEntity<Object> getAllSetPublicByUserId(@PathVariable("userId") Long userId) {
        try {
            if (setFlashcardService.getAllSetPublicByUserId(userId).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("The user does not have any public flashcard sets");
            } else {
                List<ISetFlashcardDTO> set = setFlashcardService.getAllSetPublicByUserId(userId);
                return ResponseEntity.ok(set);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(FETCH_ERROR_MESSAGE);
        }
    }
}
