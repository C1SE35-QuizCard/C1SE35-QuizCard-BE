package com.example.quizcards.service;

import com.example.quizcards.dto.request.*;
import com.example.quizcards.dto.response.JwtAuthenticationResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface IAuthService {
    ResponseEntity<JwtAuthenticationResponse> registerUser(SignupRequest signupRequest, HttpServletResponse response);

    ResponseEntity<JwtAuthenticationResponse> loginUser(LoginRequest loginRequest, HttpServletResponse response);

    ResponseEntity<JwtAuthenticationResponse> googleLogin(GoogleLoginRequest googleLoginRequest, HttpServletResponse response)
            throws Exception;

    ResponseEntity<?> logoutUser(HttpServletRequest request, HttpServletResponse response);

    ResponseEntity<?> getUserRole();

    ResponseEntity<?> isFreeUser();

    ResponseEntity<?> updatePasswordUser(Long id, UpdatePasswordRequest updatePasswordRequest, HttpServletResponse response);

    ResponseEntity<JwtAuthenticationResponse> getAccessToken(RefreshTokenRequest request, HttpServletResponse response);
}
