package com.example.quizcards.controller;

import com.example.quizcards.dto.request.*;
import com.example.quizcards.dto.response.ApiResponse;
import com.example.quizcards.dto.response.JwtAuthenticationResponse;
import com.example.quizcards.security.UserPrincipal;
import com.example.quizcards.service.IAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class
AuthController {
    @Autowired
    private IAuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<JwtAuthenticationResponse> registerUser(
            @Valid @RequestBody SignupRequest signupRequest,
            HttpServletResponse response) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.registerUser(signupRequest, response));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> authenticateUser(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletResponse response) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.loginUser(loginRequest, response));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(
            HttpServletRequest loginRequest,
            HttpServletResponse response) {
        authService.logoutUser(loginRequest, response);
        return ResponseEntity.ok(new ApiResponse(true, "User logout successfully"));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<JwtAuthenticationResponse> getAccessToken(
            @Valid @RequestBody RefreshTokenRequest request, HttpServletResponse response) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.getAccessToken(request, response));
    }

    @PostMapping("/oauth2-login")
    public ResponseEntity<JwtAuthenticationResponse> googleLogin(
            @Valid @RequestBody GoogleLoginRequest request, HttpServletResponse response) throws Exception {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.googleLogin(request, response));
    }

    @GetMapping("/user-role")
    public ResponseEntity<?> getUserRole() {
        return ResponseEntity.status(200).body(authService.getUserRole());
    }

    @GetMapping("/user-info")
    @PreAuthorize("hasAnyRole('ROLE_FREE_USER', 'ROLE_PREMIUM_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> getUserInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> results = new HashMap<>();
        UserPrincipal up = (UserPrincipal) auth.getPrincipal();
        results.put("username", up.getUsername());
        results.put("role", up.getRolesBaseAuthorities());
        results.put("id", up.getId());
        results.put("email", up.getEmail());
        results.put("avatar", up.getAvatar());
        results.put("firstname", up.getFirstName());
        results.put("lastname", up.getLastName());
        return ResponseEntity.ok(results);
    }

//    @PostMapping("update-password")
//    @PreAuthorize("hasAnyRole('ROLE_FREE_USER', 'ROLE_PREMIUM_USER', 'ROLE_ADMIN')")
//    public ResponseEntity<?> updatePasswordUser(@Valid @RequestBody UpdatePasswordRequest updatePasswordRequest,
//                                                HttpServletResponse response) {
//        updatePasswordRequest.validate();
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        UserPrincipal up = (UserPrincipal) authentication.getPrincipal();
//
//        return authService.updatePasswordUser(up.getId(), updatePasswordRequest, response);
//    }
}
