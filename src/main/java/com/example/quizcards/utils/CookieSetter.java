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

    public  ResponseEntity<JwtAuthenticationResponse> generateTokenToCookie(HttpServletResponse response,
                                                                                  String accessToken,
                                                                                  String refreshToken,
                                                                                  String message) {
        ResponseCookie cookie = ResponseCookie.from("token", accessToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(jwtExpirationInSec)
                .build(); // Thời gian tồn tại của cookie (0)

        ResponseCookie newRefreshTokenCookie = ResponseCookie.from("rft", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(refreshTokenDurationSec)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
        response.addHeader("Set-Cookie", newRefreshTokenCookie.toString());

        return new ResponseEntity<>(new JwtAuthenticationResponse(
                accessToken, refreshToken, message), HttpStatus.OK);
    }
}
