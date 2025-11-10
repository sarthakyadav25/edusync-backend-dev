package com.project.edusync.iam.service.impl;

import com.project.edusync.common.exception.iam.InvalidTokenException;
import com.project.edusync.common.security.AuthUtil;
import com.project.edusync.iam.model.entity.RefreshToken;
import com.project.edusync.iam.model.entity.User;
import com.project.edusync.iam.repository.RefreshTokenRepository;
import com.project.edusync.iam.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Value("${app.jwt.refresh-expirationTime}")
    private long refreshExpirationTimeMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthUtil authUtil;

    @Override
    @Transactional
    public RefreshToken createRefreshToken(User user, String ipAddress) {
        // Generate a new JWT refresh token
        String tokenString = authUtil.generateRefreshToken(user.getUsername());

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(tokenString)
                .expiryDate(Instant.now().plusMillis(refreshExpirationTimeMs))
                .ipAddress(ipAddress)
                .invalidated(false)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    @Transactional(readOnly = true)
    public RefreshToken verifyRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Refresh token not found."));

        if (refreshToken.isInvalidated()) {
            throw new InvalidTokenException("Refresh token has been invalidated.");
        }

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new InvalidTokenException("Refresh token has expired.");
        }

        return refreshToken;
    }

    @Override
    @Transactional
    public void invalidateToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Refresh token not found."));

        refreshToken.setInvalidated(true);
        refreshTokenRepository.save(refreshToken);
    }
}