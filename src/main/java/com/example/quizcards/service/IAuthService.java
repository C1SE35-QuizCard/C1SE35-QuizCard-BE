package com.example.quizcards.service;

import com.example.quizcards.dto.request.*;
import com.example.quizcards.dto.response.JwtAuthenticationResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface IAuthService {
    JwtAuthenticationResponse registerUser(SignupRequest signupRequest, HttpServletResponse response);

    JwtAuthenticationResponse loginUser(LoginRequest loginRequest, HttpServletResponse response);

    JwtAuthenticationResponse googleLogin(GoogleLoginRequest googleLoginRequest, HttpServletResponse response)
            throws Exception;

    void logoutUser(HttpServletRequest request, HttpServletResponse response);

    String getUserRole();

    boolean isFreeUser();

    JwtAuthenticationResponse getAccessToken(RefreshTokenRequest request, HttpServletResponse response);

    //    void updatePasswordUser(Long id, UpdatePasswordRequest updatePasswordRequest, HttpServletResponse response);
}
