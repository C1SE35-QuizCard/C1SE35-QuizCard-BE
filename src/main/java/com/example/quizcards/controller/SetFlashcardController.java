package com.example.quizcards.controller;

import com.example.quizcards.dto.*;
import com.example.quizcards.dto.response.ErrorDetail;
import com.example.quizcards.service.ISetFlashcardService;
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
@RequestMapping("/api/set")
public class SetFlashcardController {
    @Autowired
    private ISetFlashcardService setFlashcardService;
    private static final String FETCH_ERROR_MESSAGE = "An error occurred while fetching set flashcards";

    @GetMapping("/list")
    public ResponseEntity<Object> findAllSetFlashcard(){
        try {
            if (setFlashcardService.getAll().isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                List<ISetFlashcardDTO> set = setFlashcardService.getAll();
                return ResponseEntity.ok(set);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(FETCH_ERROR_MESSAGE + e.getMessage());
        }
    }

    @GetMapping("/list/{id}")
    public ResponseEntity<Object> findAllFlashcardBySetId(@PathVariable("id") int setId){
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
    public ResponseEntity<Object> detailSetFlashcardById(@PathVariable("id") int setId){
        try {
            if (setFlashcardService.findBySetId(setId) == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Set Flashcard not found");
            }
            ISetFlashcardDTO setFlashcard = setFlashcardService.findBySetId(setId);
            return ResponseEntity.ok(setFlashcard);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(FETCH_ERROR_MESSAGE + e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createSetFlashcard(@RequestBody @Validated SetFlashcardCreationRequest request, BindingResult bindingResult) {
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
            setFlashcardService.addSetFlashcard(request.getTitle(), request.getDescriptionSet(), request.getIsApproved(), request.getIsAnonymous(), request.getSharingMode(), request.getUserId(), request.getCategoryId());
            return ResponseEntity.status(HttpStatus.CREATED).body("Set Flashcard created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while creating the set flashcard: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteSetFlashcardById(@PathVariable("id") int setId) {
        if(setFlashcardService.findBySetId(setId) != null) {
            try {
                setFlashcardService.deleteSetFlashcard(setId);
                return new ResponseEntity<>("Set Flashcard deleted successfully", HttpStatus.OK);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the set flashcard: " + e.getMessage());
            }
        }else{
            return new ResponseEntity<>("Set Flashcard not found", HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Object> updateSetFlashcard(@Validated @RequestBody SetFlashcardUpdateRequest request, BindingResult bindingResult) {
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
            if(setFlashcardService.findBySetId(request.getSetId()) == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Set Flashcard not found");
            }
            setFlashcardService.updateSetFlashcard(request);
            return new ResponseEntity<>("Set Flashcard updated successfully", HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating the set flashcard: " + e.getMessage());
        }
    }

}
