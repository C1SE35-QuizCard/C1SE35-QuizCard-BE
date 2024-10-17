package com.example.quizcards.controller;

import com.example.quizcards.dto.*;
import com.example.quizcards.dto.request.CollectionCreationRequest;
import com.example.quizcards.dto.response.ErrorDetail;
import com.example.quizcards.service.ICollectionService;
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
@RequestMapping("/api/collection")
public class CollectionControllder {

    @Autowired
    private ICollectionService collectionService;
    private static final String FETCH_ERROR_MESSAGE = "An error occurred while fetching collection";

    @GetMapping("/{id}")
    public ResponseEntity<Object> getCollectionById(@PathVariable("id") Long id){
        try {
            if (collectionService.getCollectionById(id) == null) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No collection found for collection ID " + id);
            } else {
                ICollectionDTO collectionDTO = collectionService.getCollectionById(id);
                return ResponseEntity.ok(collectionDTO);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(FETCH_ERROR_MESSAGE);
        }
    }

    @GetMapping("/list")
    public ResponseEntity<Object> getAllCollection(){
        try {
            if (collectionService.getAllCollection().isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No collection found");
            } else {
                List<ICollectionDTO> collectionDTOs = collectionService.getAllCollection();
                return ResponseEntity.ok(collectionDTOs);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(FETCH_ERROR_MESSAGE);
        }
    }

    @GetMapping("/set/{set_id}")
    public ResponseEntity<Object> getCollectionBySetId(@PathVariable("set_id") int setId){
        try {
            if (collectionService.getCollectionBySetId(setId).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No collection found for set ID " + setId);
            } else {
                List<ICollectionDTO> collectionDTO = collectionService.getCollectionBySetId(setId);
                return ResponseEntity.ok(collectionDTO);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(FETCH_ERROR_MESSAGE);
        }
    }

    @GetMapping("/folder/{folder_id}")
    public ResponseEntity<Object> getCollectionByFolderId(@PathVariable("folder_id") int folderId){
        try {
            if (collectionService.getCollectionByFolderId(folderId).isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No collection found for folder ID " + folderId);
            } else {
                List<ICollectionDTO> collectionDTO = collectionService.getCollectionByFolderId(folderId);
                return ResponseEntity.ok(collectionDTO);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(FETCH_ERROR_MESSAGE);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Object> addCollection(@RequestBody @Validated CollectionCreationRequest request, BindingResult bindingResult) {
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
            collectionService.addCollection(request.getFolderId(), request.getSetId());
            return ResponseEntity.status(HttpStatus.CREATED).body("Collection created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while creating the Collection");
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteCollection(@PathVariable("id") Long id) {
        if(collectionService.getCollectionById(id) != null) {
            try {
                collectionService.deleteCollection(id);
                return new ResponseEntity<>("Collection deleted successfully", HttpStatus.OK);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the Collection");
            }
        }else{
            return new ResponseEntity<>("Collection not found", HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Object> updateCollection(@Validated @RequestBody CollectionCreationRequest request, BindingResult bindingResult) {
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
            if(collectionService.getCollectionById(request.getId()) == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Collection not found");
            }
            collectionService.updateCollection(request);
            return new ResponseEntity<>("Collection updated successfully", HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating the Collection");
        }
    }
}
