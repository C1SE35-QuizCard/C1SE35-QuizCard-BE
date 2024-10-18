package com.example.quizcards.service.impl;

import com.example.quizcards.entities.RefreshToken;
import com.example.quizcards.repository.AppUserRepository;
import com.example.quizcards.repository.RefreshTokenRepository;
import com.example.quizcards.service.IRefreshTokenService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements IRefreshTokenService {
    @Value("${jwt.refreshTokenExpirationInSec}")
    private Long refreshTokenDurationSec;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private AppUserRepository userRepository;

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    @Transactional
    public RefreshToken createRefreshToken(Long userID) {
        RefreshToken newRefreshToken = new RefreshToken();

        newRefreshToken.setUser(userRepository.findById(userID).get());
        newRefreshToken.setExpiryDate(Instant.now().plusSeconds(refreshTokenDurationSec).atZone(ZoneId.systemDefault()).toLocalDateTime());
        newRefreshToken.setToken(UUID.randomUUID().toString());

        return refreshTokenRepository.save(newRefreshToken);
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken refreshToken) {
        if (refreshToken.getExpiryDate().compareTo(LocalDateTime.now()) < 0) {
            refreshTokenRepository.delete(refreshToken);
            return null;
        }
        return refreshToken;
    }

    @Override
    @Transactional
    public int deleteByUserId(Long userId) {
        return refreshTokenRepository.deleteByUser(userRepository.findById(userId).get());
    }

    @Override
    @Transactional
    public void updateRefreshTokenWithCurrentExpiredDate(RefreshToken refreshToken) {
        refreshToken.setExpiryDate(
                Instant.now().plusMillis(refreshTokenDurationSec).atZone(ZoneId.systemDefault()).toLocalDateTime()
        );
        refreshTokenRepository.save(refreshToken);
    }
}
