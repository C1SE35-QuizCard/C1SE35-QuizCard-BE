package com.example.quizcards.service.impl;

import com.example.quizcards.dto.request.LoginRequest;
import com.example.quizcards.dto.request.SignupRequest;
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

    @Value("${jwt.jwtExpirationInMs}")
    private Long jwtExpirationInMs;

    @Value("${jwt.refreshTokenExpirationInMs}")
    private Long refreshTokenExpirationInMs;
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
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setGender(true);
        user.setEnabled(true);

        appUserRepository.save(user);

        UserPrincipal up = UserPrincipal.create(user);

        String accessToken = jwtTokenProvider.generateAccessToken(up);

        RefreshToken refreshToken = rfService.createRefreshToken(up.getId());

        return generateTokenToCookie(response, accessToken, refreshToken.getToken(), "User register successfully!");
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

        return generateTokenToCookie(response, accessToken, refreshToken.getToken(), "User login successfully!");
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
            ResponseCookie cookie = ResponseCookie.from("token",  "")
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

    private String createUserCode() {
        String newUserCode;
        do {
            newUserCode = generateRandomCode(24);
        } while (appUserRepository.existsByUserCode(newUserCode));
        return newUserCode;
    }

    private String generateRandomCode(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        Random random;

        try {
            random = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            random = new Random();
        }

        for (int i = 0; i < length; i++) {
            code.append(characters.charAt(random.nextInt(characters.length())));
        }
        return code.toString();
    }

    public ResponseEntity<JwtAuthenticationResponse> generateTokenToCookie(HttpServletResponse response,
                                                                           String accessToken,
                                                                           String refreshToken,
                                                                           String message) {
        ResponseCookie cookie = ResponseCookie.from("token", accessToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(jwtExpirationInMs)
                .build(); // Thời gian tồn tại của cookie (0)

        ResponseCookie newRefreshTokenCookie = ResponseCookie.from("rft", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(refreshTokenExpirationInMs)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
        response.addHeader("Set-Cookie", newRefreshTokenCookie.toString());

        return new ResponseEntity<>(new JwtAuthenticationResponse(
                accessToken, refreshToken, message), HttpStatus.OK);
    }
}
