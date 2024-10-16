package com.example.quizcards.service.impl;

import com.example.quizcards.dto.request.LoginRequest;
import com.example.quizcards.dto.request.SignupRequest;
import com.example.quizcards.dto.request.UpdatePasswordRequest;
import com.example.quizcards.dto.response.ApiResponse;
import com.example.quizcards.dto.response.JwtAuthenticationResponse;
import com.example.quizcards.entities.AppRole;
import com.example.quizcards.entities.AppUser;
import com.example.quizcards.entities.RefreshToken;
import com.example.quizcards.entities.role.RoleName;
import com.example.quizcards.exception.BadRequestException;
import com.example.quizcards.repository.AppRoleRepository;
import com.example.quizcards.repository.AppUserRepository;
import com.example.quizcards.repository.RefreshTokenRepository;
import com.example.quizcards.security.JwtTokenProvider;
import com.example.quizcards.security.UserPrincipal;
import com.example.quizcards.service.IAuthService;
import com.example.quizcards.service.IRefreshTokenService;
import com.example.quizcards.utils.CodeRandom;
import com.example.quizcards.utils.CookieSetter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Random;
import java.util.UUID;

@Service
public class AuthServiceImpl implements IAuthService {
    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private AppRoleRepository appRoleRepository;

    @Autowired
    private IRefreshTokenService rfService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private CookieSetter cs;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional
    public ResponseEntity<JwtAuthenticationResponse> registerUser(SignupRequest signupRequest, HttpServletResponse response) {
        if (appUserRepository.existsByUsername(signupRequest.getUsername())) {
            throw new BadRequestException("Username is already taken");
        }
        if (appUserRepository.existsByEmail(signupRequest.getEmail())) {
            throw new BadRequestException("Email is already registered");
        }
        AppRole role = appRoleRepository.findByRoleName(RoleName.ROLE_FREE_USER.name())
                .orElseThrow(() -> new BadRequestException("Role not found"));

        AppUser user = new AppUser();
        user.setFirstName(signupRequest.getFirstName());
        user.setLastName(signupRequest.getLastName());
        user.setDateOfBirth(signupRequest.getDateOfBirth());
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setHashPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setUserCode(createUserCode());
        user.setRole(role);
        user.setGender(true);
        user.setEnabled(true);

        appUserRepository.save(user);

        UserPrincipal up = UserPrincipal.create(user);

        String accessToken = jwtTokenProvider.generateAccessToken(up);

        RefreshToken refreshToken = rfService.createRefreshToken(up.getId());

        return cs.generateTokenToCookie(response, accessToken, refreshToken.getToken(), "User register successfully!");
    }

    @Override
    @Transactional
    public ResponseEntity<JwtAuthenticationResponse> loginUser(LoginRequest loginRequest, HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsernameOrEmail(), loginRequest.getPassword())
        );

        UserPrincipal up = (UserPrincipal) authentication.getPrincipal();

        String accessToken = jwtTokenProvider.generateAccessToken(up);

        RefreshToken refreshToken = rfService.createRefreshToken(up.getId());

        return cs.generateTokenToCookie(response, accessToken, refreshToken.getToken(), "User login successfully!");
    }

    @Override
    @Transactional
    public ResponseEntity<?> logoutUser(HttpServletRequest request, HttpServletResponse response) {
        String rft = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("rft".equals(cookie.getName())) {
                    rft = cookie.getValue();
                }
            }
        }

        if (rft != null) {
            ResponseCookie cookie = ResponseCookie.from("token", "")
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("None")
                    .path("/")
                    .maxAge(0)
                    .build(); // Thời gian tồn tại của cookie (0)

            ResponseCookie newRefreshTokenCookie = ResponseCookie.from("rft", "")
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("None")
                    .path("/")
                    .maxAge(0)
                    .build();

            response.addHeader("Set-Cookie", cookie.toString());
            response.addHeader("Set-Cookie", newRefreshTokenCookie.toString());

            refreshTokenRepository.deleteByToken(rft);
        }

        return ResponseEntity.ok(new ApiResponse(true, "User logout successfully"));
    }

    @Override
    @Transactional
    public ResponseEntity<?> updatePasswordUser(Long id, UpdatePasswordRequest updatePasswordRequest, HttpServletResponse response) {
        AppUser user = appUserRepository.findById(id).orElseThrow();

        if ((user.getHashPassword() != null && !user.getHashPassword().isEmpty()) &&
                !passwordEncoder.matches(updatePasswordRequest.getOldPassword(), user.getHashPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "Wrong old password!"));
        }

        user.setHashPassword(passwordEncoder.encode(updatePasswordRequest.getNewPassword()));

        appUserRepository.save(user);

        return ResponseEntity.ok().body(new ApiResponse(true,
                "User password has been updated successfully"));
    }


    private String createUserCode() {
        String newUserCode;
        do {
            newUserCode = CodeRandom.generateRandomCode(24);
        } while (appUserRepository.existsByUserCode(newUserCode));
        return newUserCode;
    }
}
