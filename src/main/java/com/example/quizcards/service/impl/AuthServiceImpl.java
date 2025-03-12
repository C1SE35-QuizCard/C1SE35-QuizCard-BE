package com.example.quizcards.service.impl;

import com.example.quizcards.dto.GoogleInfoUser;
import com.example.quizcards.dto.request.*;
import com.example.quizcards.dto.response.ApiResponse;
import com.example.quizcards.dto.response.JwtAuthenticationResponse;
import com.example.quizcards.entities.AppRole;
import com.example.quizcards.entities.AppUser;
import com.example.quizcards.entities.RefreshToken;
import com.example.quizcards.entities.role.RoleName;
import com.example.quizcards.exception.ErrorsDataException;
import com.example.quizcards.exception.ResourceNotFoundException;
import com.example.quizcards.exception.TokenRefreshException;
import com.example.quizcards.exception.UnauthorizedException;
import com.example.quizcards.repository.IAppUserRepository;
import com.example.quizcards.security.JwtTokenProvider;
import com.example.quizcards.security.UserPrincipal;
import com.example.quizcards.service.IAppRoleService;
import com.example.quizcards.service.IAuthService;
import com.example.quizcards.service.IGoogleHandleService;
import com.example.quizcards.service.IRefreshTokenService;
import com.example.quizcards.utils.CodeRandom;
import com.example.quizcards.utils.CookieSetter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthServiceImpl implements IAuthService {
    @Autowired
    private IGoogleHandleService googleHandleService;

    @Autowired
    private IAppRoleService appRoleService;

    @Autowired
    private IAppUserRepository appUserService;

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


    @Override
    @Transactional
    public JwtAuthenticationResponse registerUser(SignupRequest signupRequest, HttpServletResponse response) {
        if (appUserService.existsByUsername(signupRequest.getUsername())) {
            throw new ErrorsDataException("Register failed", Map.of("username", "Username is already taken"),
                    HttpStatus.BAD_REQUEST);
        }
        if (appUserService.existsByEmail(signupRequest.getEmail())) {
            throw new ErrorsDataException("Register failed", Map.of("email", "Email is already registered"),
                    HttpStatus.BAD_REQUEST);
        }
        AppRole role = appRoleService.findByRoleName(RoleName.ROLE_FREE_USER.name())
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", "Free user"));

        AppUser user = new AppUser();
        user.setFirstName(signupRequest.getFirstName());
        user.setLastName(signupRequest.getLastName());
        user.setDateOfBirth(signupRequest.getDateOfBirth());
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setHashPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setUserCode(CodeRandom.generateRandomCode(28));
        user.setRole(role);
        user.setGender(true);
        user.setEnabled(true);

        appUserService.save(user);

        UserPrincipal up = UserPrincipal.create(user);

        String accessToken = jwtTokenProvider.generateAccessToken(up);

        RefreshToken refreshToken = rfService.createRefreshToken(up.getId());

        return cs.generateTokenToCookie(response, accessToken, refreshToken.getToken());
    }

    @Override
    @Transactional
    public JwtAuthenticationResponse loginUser(LoginRequest loginRequest, HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsernameOrEmail(), loginRequest.getPassword())
        );

        UserPrincipal up = (UserPrincipal) authentication.getPrincipal();

        String accessToken = jwtTokenProvider.generateAccessToken(up);

        RefreshToken refreshToken = rfService.createRefreshToken(up.getId());

        return cs.generateTokenToCookie(response, accessToken, refreshToken.getToken());
    }

    @Override
    @Transactional
    public void logoutUser(HttpServletRequest request, HttpServletResponse response) {
        String rft = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refresh_token".equals(cookie.getName())) {
                    rft = cookie.getValue();
                }
            }
        }

        if (rft != null) {
            ResponseCookie cookie = ResponseCookie.from("access_token", "")
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("None")
                    .path("/")
                    .maxAge(0)
                    .build(); // Thời gian tồn tại của cookie (0)

            ResponseCookie newRefreshTokenCookie = ResponseCookie.from("refresh_token", "")
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("None")
                    .path("/")
                    .maxAge(0)
                    .build();

            response.addHeader("set-Cookie", cookie.toString());
            response.addHeader("set-Cookie", newRefreshTokenCookie.toString());

            rfService.deleteByToken(rft);
        }
    }

    @Override
    public String getUserRole() {
        String role = "ROLE_GUEST";
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() != null
                && authentication.getPrincipal() instanceof UserPrincipal) {
            String username = authentication.getName();
            Optional<AppUser> user = appUserService.findByUsername(username);
            if (user.isPresent()) {
                role = user.get().getRole().getRoleName();
            }
        }
        return role;
    }

    @Override
    public boolean isFreeUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal up = (UserPrincipal) authentication.getPrincipal();
        boolean isFreeUser = false;
        if (!up.getRolesBaseAuthorities().contains(RoleName.ROLE_ADMIN)
                && !up.getRolesBaseAuthorities().contains(RoleName.ROLE_PREMIUM_USER)) {
            if (up.getRolesBaseAuthorities().contains(RoleName.ROLE_FREE_USER)) {
                isFreeUser = true;
            } else {
                throw new RuntimeException("Invalid role");
            }
        }
        return isFreeUser;
    }

    @Override
    @Transactional
    public JwtAuthenticationResponse getAccessToken(RefreshTokenRequest request,
                                                                    HttpServletResponse response) {
        String rft = request.getRefreshToken();
        return rfService.findByToken(rft)
                .map(rfService::verifyExpiration)
                .map(rfService::updateRefreshTokenWithCurrentExpiredDate)
                .flatMap(refreshToken -> {
                    AppUser user = refreshToken.getUser();
                    UserPrincipal up = UserPrincipal.create(user);
                    String token = jwtTokenProvider.generateAccessToken(up);

                    JwtAuthenticationResponse jwtResponse = cs.generateTokenToCookie(response,
                            token,
                            refreshToken.getToken());

                    return Optional.of(jwtResponse);
                })
                .orElseThrow(() -> new TokenRefreshException(rft, "Invalid refresh token!"));
    }

    private JwtAuthenticationResponse authenOAuth2Login(String email,
                                                                        HttpServletResponse response) {
        AppUser user = appUserService.findByEmail(email).get();

        if (!user.getEnabled()) {
            throw new DisabledException("User is disabled");
        }

        UserPrincipal up = UserPrincipal.create(user);

        String accessToken = jwtTokenProvider.generateAccessToken(up);

        RefreshToken refreshToken = rfService.createRefreshToken(up.getId());

        return cs.generateTokenToCookie(response, accessToken, refreshToken.getToken());
    }

    private JwtAuthenticationResponse registerByGoogleInfo(GoogleInfoUser g_user,
                                                                           HttpServletResponse response) {
        AppRole role = appRoleService.findByRoleName(RoleName.ROLE_FREE_USER.name())
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", "Free user"));

        AppUser user = new AppUser();
        user.setFirstName(g_user.getFirstName());
        user.setLastName(g_user.getLastName());
        user.setAvatar(g_user.getAvatarUrl());
        user.setEmail(g_user.getEmail());
        user.setHashPassword("");
        user.setUserCode(g_user.getUserCode());
        user.setRole(role);
        user.setGender(true);
        user.setEnabled(g_user.getEnabled());

        String username = g_user.getUserName();

        while (appUserService.existsByUsername(username)) {
            username = g_user.getUserName() + "-" + UUID.randomUUID().toString().substring(0, 6);
        }

        user.setUsername(username);

        appUserService.save(user);

        UserPrincipal up = UserPrincipal.create(user);

        String accessToken = jwtTokenProvider.generateAccessToken(up);

        RefreshToken refreshToken = rfService.createRefreshToken(up.getId());

        return cs.generateTokenToCookie(response, accessToken, refreshToken.getToken());
    }

    @Override
    @Transactional
    public JwtAuthenticationResponse googleLogin(GoogleLoginRequest request, HttpServletResponse response)
            throws Exception {
        GoogleInfoUser user = googleHandleService.extractDataFromCode(request);
        if (appUserService.existsByEmail(user.getEmail())) {
            return authenOAuth2Login(user.getEmail(), response);
        } else {
            return registerByGoogleInfo(user, response);
        }
    }

//    @Override
//    @Transactional
//    public void updatePasswordUser(Long id, UpdatePasswordRequest updatePasswordRequest, HttpServletResponse response) {
//        AppUser user = appUserService.findById(id).orElseThrow();
//
//        if ((user.getHashPassword() != null && !user.getHashPassword().isEmpty()) &&
//                !passwordEncoder.matches(updatePasswordRequest.getOldPassword(), user.getHashPassword())) {
//            throw new UnauthorizedException("Wrong old password!");
//        }
//
//        user.setHashPassword(passwordEncoder.encode(updatePasswordRequest.getNewPassword()));
//
//        appUserService.save(user);
//    }
}
