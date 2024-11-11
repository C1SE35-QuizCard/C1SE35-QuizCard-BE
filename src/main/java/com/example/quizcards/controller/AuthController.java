package com.example.quizcards.controller;

import com.example.quizcards.dto.request.*;
import com.example.quizcards.dto.response.JwtAuthenticationResponse;
import com.example.quizcards.entities.AppUser;
import com.example.quizcards.security.UserPrincipal;
import com.example.quizcards.service.IAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
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

    @PostMapping("/refresh-token")
    public ResponseEntity<JwtAuthenticationResponse> getAccessToken(
            @Valid @RequestBody RefreshTokenRequest request, HttpServletResponse response) {
        return authService.getAccessToken(request, response);
    }

    @PostMapping("/oauth2-login")
    public ResponseEntity<JwtAuthenticationResponse> googleLogin(
            @Valid @RequestBody GoogleLoginRequest request, HttpServletResponse response) throws Exception {
        return authService.googleLogin(request, response);
    }

    @GetMapping("/user-role")
    public ResponseEntity<?> getUserRole() {
        return authService.getUserRole();
    }

    @PreAuthorize("hasAnyRole('ROLE_FREE_USER', 'ROLE_PREMIUM_USER', 'ROLE_ADMIN')")
    @PostMapping("update-password")
    public ResponseEntity<?> updatePasswordUser(@Valid @RequestBody UpdatePasswordRequest updatePasswordRequest,
                                                HttpServletResponse response) {
        updatePasswordRequest.validate();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal up = (UserPrincipal) authentication.getPrincipal();

        return authService.updatePasswordUser(up.getId(), updatePasswordRequest, response);
    }
}
