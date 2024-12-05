package com.example.quizcards.controller;

import com.example.quizcards.dto.ITestDTO;
import com.example.quizcards.dto.ITestModeDTO;
import com.example.quizcards.dto.request.FolderRequest;
import com.example.quizcards.dto.request.TestCreationRequest;
import com.example.quizcards.security.UserPrincipal;
import com.example.quizcards.service.ITestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


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

    @GetMapping("/multiple-choice/{test_id}")
    @PreAuthorize("hasAnyRole('ROLE_FREE_USER', 'ROLE_PREMIUM_USER')")
    public ResponseEntity<?> getMultipleChoiceTest(@PathVariable("test_id") Long testId) {
        return testService.createMultipleChoiceTest(testId);
    }

    @GetMapping("/essay/{test_id}")
    @PreAuthorize("hasAnyRole('ROLE_FREE_USER', 'ROLE_PREMIUM_USER')")
    public ResponseEntity<?> getEssayTest(@PathVariable("test_id") Long testId) {
        return testService.createEssayTest(testId);
    }
}
