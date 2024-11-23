package com.example.quizcards.utils;

import com.example.quizcards.dto.response.JwtAuthenticationResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class CookieSetter {
    @Value("${jwt.jwtExpirationInSec}")
    private Long jwtExpirationInSec;

    @Value("${jwt.refreshTokenExpirationInSec}")
    private Long refreshTokenDurationSec;

    public ResponseEntity<JwtAuthenticationResponse> generateTokenToCookie(HttpServletResponse response,
                                                                           String accessToken,
                                                                           String refreshToken,
                                                                           String message) {
        ResponseCookie cookie = ResponseCookie.from("access_token", accessToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(10000)
                .build();

        ResponseCookie newRefreshTokenCookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(refreshTokenDurationSec)
                .build();

        response.addHeader("set-cookie", cookie.toString());
        response.addHeader("set-cookie", newRefreshTokenCookie.toString());

        return new ResponseEntity<>(new JwtAuthenticationResponse(
                accessToken, refreshToken, message), HttpStatus.CREATED);
    }
}
