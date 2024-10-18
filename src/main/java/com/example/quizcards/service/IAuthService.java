package com.example.quizcards.service;

import com.example.quizcards.dto.request.LoginRequest;
import com.example.quizcards.dto.request.SignupRequest;
import com.example.quizcards.dto.request.UpdatePasswordRequest;
import com.example.quizcards.dto.response.JwtAuthenticationResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface IAuthService {
    ResponseEntity<JwtAuthenticationResponse> registerUser(SignupRequest signupRequest, HttpServletResponse response);

    ResponseEntity<JwtAuthenticationResponse> loginUser(LoginRequest loginRequest, HttpServletResponse response);

    ResponseEntity<?> logoutUser(HttpServletRequest request, HttpServletResponse response);

    ResponseEntity<?> updatePasswordUser(Long id, UpdatePasswordRequest updatePasswordRequest, HttpServletResponse response);
}
