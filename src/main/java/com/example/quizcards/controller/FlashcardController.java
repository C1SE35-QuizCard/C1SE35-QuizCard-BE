package com.example.quizcards.controller;

import com.example.quizcards.dto.request.FlashcardCreationRequest;
import com.example.quizcards.dto.IFlashcardDTO;
import com.example.quizcards.dto.response.ErrorDetail;
import com.example.quizcards.service.IFlashcardService;
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
@RequestMapping("/api/flashcards")
public class FlashcardController {

    @Autowired
    private IFlashcardService flashcardService;
    private static final String FETCH_ERROR_MESSAGE = "An error occurred while fetching flashcards";

    @GetMapping("/list")
    public ResponseEntity<Object> findAllFlashcard(){
        try {
            if (flashcardService.getAll().isEmpty()) {
                return new ResponseEntity<>("No flashcards found", HttpStatus.NO_CONTENT);
            } else {
                List<IFlashcardDTO> flashcards = flashcardService.getAll();
                return ResponseEntity.ok(flashcards);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(FETCH_ERROR_MESSAGE);
        }
    }

    @GetMapping("/list/{id}")
    public ResponseEntity<Object> findAllFlashcardBySetId(@PathVariable("id") Long setId){
        try {
            if (!flashcardService.getAllBySetId(setId).isEmpty()) {
                List<IFlashcardDTO> flashcards = flashcardService.getAllBySetId(setId);
                return ResponseEntity.ok(flashcards);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No flashcards found for set ID " + setId);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(FETCH_ERROR_MESSAGE);
        }
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<Object> detailFlashcardById(@PathVariable("id") Long cardId){
        try {
            if (flashcardService.findByCardId(cardId) == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Flashcard not found");
            }
            IFlashcardDTO flashcard = flashcardService.findByCardId(cardId);
            return ResponseEntity.ok(flashcard);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(FETCH_ERROR_MESSAGE);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createFlashcard(@RequestBody @Validated FlashcardCreationRequest request, BindingResult bindingResult) {
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

            flashcardService.addFlashcard(request.getQuestion(),
                    request.getAnswer(),
                    request.getImageLink(),
                    request.getIsApproved(),
                    request.getSetId());

            return ResponseEntity.status(HttpStatus.CREATED).body("Flashcard created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while creating the flashcard");
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteFlashcardById(@PathVariable("id") Long cardId) {
        if(flashcardService.findByCardId(cardId) != null) {
            try {
                flashcardService.deleteFlashcard(cardId);
                return new ResponseEntity<>("Flashcard deleted successfully", HttpStatus.OK);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the flashcard");
            }
        }else{
            return new ResponseEntity<>("Flashcard not found", HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Object> updateFlashcard(@Validated @RequestBody FlashcardCreationRequest request, BindingResult bindingResult) {
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
            if(flashcardService.findByCardId(request.getCardId()) == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Flashcard not found");
            }
            flashcardService.updateFlashcard(request);
            return new ResponseEntity<>("Flashcard updated successfully", HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating the flashcard");
        }
    }
}
