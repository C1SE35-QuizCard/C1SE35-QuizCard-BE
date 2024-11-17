package com.example.quizcards.controller;

import com.example.quizcards.dto.ITestDTO;
import com.example.quizcards.dto.ITestModeDTO;
import com.example.quizcards.dto.request.FolderCreationRequest;
import com.example.quizcards.dto.request.TestCreationRequest;
import com.example.quizcards.dto.response.ErrorDetail;
import com.example.quizcards.security.UserPrincipal;
import com.example.quizcards.service.ITestModeService;
import com.example.quizcards.service.ITestService;
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

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
@RequestMapping("/api/v1/test")
public class TestController {
    @Autowired
    private ITestService testService;

    @PostMapping("/create/{set_id}")
    @PreAuthorize("hasAnyRole('ROLE_FREE_USER', 'ROLE_PREMIUM_USER')")
    public ResponseEntity<?> createTest(@PathVariable("set_id") Long setId,
                                               @RequestBody @Validated TestCreationRequest request){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal up = (UserPrincipal) authentication.getPrincipal();
        return testService.createTest(setId, up.getId(), request);
    }

    @DeleteMapping("/delete/{test_id}")
    public ResponseEntity<?> deleteTest(@PathVariable("test_id") Long TestId) {
        return testService.deleteTest(TestId);
    }
}
