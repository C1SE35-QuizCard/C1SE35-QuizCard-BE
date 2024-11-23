package com.example.quizcards.service;

import com.example.quizcards.entities.RefreshToken;

import java.util.Optional;

public interface IRefreshTokenService {
    Optional<RefreshToken> findByToken(String token);

    RefreshToken createRefreshToken(Long userId);

    RefreshToken verifyExpiration(RefreshToken refreshToken);

    int deleteByUserId(Long userId);

    RefreshToken updateRefreshTokenWithCurrentExpiredDate(RefreshToken refreshToken);

    void deleteByToken(String refreshToken);
}
