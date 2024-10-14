package com.example.quizcards.controller;

import com.example.quizcards.dto.request.LoginRequest;
import com.example.quizcards.dto.request.SignupRequest;
import com.example.quizcards.dto.response.JwtAuthenticationResponse;
import com.example.quizcards.service.IAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Autowired
    private IAuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<JwtAuthenticationResponse> registerUser(
            @Valid @RequestBody SignupRequest signupRequest,
            HttpServletResponse response) {
        return authService.registerUser(signupRequest, response);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> authenticateUser(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletResponse response) {
        return authService.loginUser(loginRequest, response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(
            HttpServletRequest loginRequest,
            HttpServletResponse response) {
        return authService.logoutUser(loginRequest, response);
    }
}
