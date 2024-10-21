package com.example.quizcards.controller;

import com.example.quizcards.dto.IDeadlineReminderDTO;
import com.example.quizcards.dto.IFlashcardDTO;
import com.example.quizcards.dto.request.DeadlineReminderCreationRequest;
import com.example.quizcards.dto.request.FlashcardCreationRequest;
import com.example.quizcards.dto.response.ErrorDetail;
import com.example.quizcards.service.IDeadlineReminderService;
import com.example.quizcards.service.IFlashcardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
@RequestMapping("/api/deadline")
public class DeadlineReminderControllder {

    @Autowired
    private IDeadlineReminderService deadlineReminderService;
    private static final String FETCH_ERROR_MESSAGE = "An error occurred while fetching deadline reminders.";

    @GetMapping("/detail/{id}")
    public ResponseEntity<Object> getDeadlineReminderById(@PathVariable("id") Long id){
        try {
            if (deadlineReminderService.getDeadlineReminderById(id) == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Deadline reminders not found");
            }
            IDeadlineReminderDTO reminder = deadlineReminderService.getDeadlineReminderById(id);
            return ResponseEntity.ok(reminder);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(FETCH_ERROR_MESSAGE);
        }
    }

    @GetMapping("/set/{set_id}")
    public ResponseEntity<Object> getDeadlineReminderBySetId(@PathVariable("set_id") Long setId){
        try {
            if (deadlineReminderService.getDeadlineReminderBySetId(setId).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Deadline reminders not found");
            }
            List<IDeadlineReminderDTO> reminder = deadlineReminderService.getDeadlineReminderBySetId(setId);
            return ResponseEntity.ok(reminder);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(FETCH_ERROR_MESSAGE);
        }
    }

    @GetMapping("/user/{user_id}")
    public ResponseEntity<Object> getDeadlineReminderByUserId(@PathVariable("user_id") Long userId){
        try {
            if (deadlineReminderService.getDeadlineReminderByUserId(userId).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Deadline reminders not found");
            }
            List<IDeadlineReminderDTO> reminder = deadlineReminderService.getDeadlineReminderByUserId(userId);
            return ResponseEntity.ok(reminder);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(FETCH_ERROR_MESSAGE);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createDeadlineReminder(@RequestBody @Validated DeadlineReminderCreationRequest request, BindingResult bindingResult) {
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
            if (request.getReminderTime().before(Timestamp.valueOf(LocalDateTime.now()))) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Reminder time must be at least the current time.");
            }
//            if (deadlineReminderService.existsByUserIdAndSetId(request.getUserId(), request.getSetId())) {
//                return ResponseEntity.status(HttpStatus.CONFLICT).body("A reminder for this user and set already exists.");
//            }
            deadlineReminderService.addDeadlineReminder(request.getReminderTime(), request.getUserId(), request.getSetId());
            return ResponseEntity.status(HttpStatus.CREATED).body("Deadline reminder created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while creating the deadline reminder");
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteDeadlineReminder(@PathVariable("id") Long deadlineRemindersId) {
        if(deadlineReminderService.getDeadlineReminderById(deadlineRemindersId) != null) {
            try {
                deadlineReminderService.deleteDeadlineReminder(deadlineRemindersId);
                return new ResponseEntity<>("Flashcard deleted successfully", HttpStatus.OK);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the deadline reminder");
            }
        }else{
            return new ResponseEntity<>("Deadline reminder not found", HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Object> updateDeadlineReminder(@Validated @RequestBody DeadlineReminderCreationRequest request, BindingResult bindingResult) {
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
            if (deadlineReminderService.getDeadlineReminderById(request.getDeadlineRemindersId()) == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Deadline reminder not found");
            }
            if (request.getReminderTime().before(Timestamp.valueOf(LocalDateTime.now()))) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Reminder time must be at least the current time.");
            }
//            if (deadlineReminderService.existsByUserIdAndSetIdAndNotId(request.getUserId(), request.getSetId(), request.getDeadlineRemindersId())) {
//                return ResponseEntity.status(HttpStatus.CONFLICT).body("A reminder for this user and set already exists.");
//            }
            deadlineReminderService.updateDeadlineReminder(request);
            return new ResponseEntity<>("Deadline reminder updated successfully", HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating the Deadline reminder");
        }
    }

}
