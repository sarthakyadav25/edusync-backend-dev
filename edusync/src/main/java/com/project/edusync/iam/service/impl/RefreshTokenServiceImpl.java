package com.project.edusync.iam.service.impl;

import com.project.edusync.common.exception.ResourceNotFoundException;
import com.project.edusync.common.exception.iam.InvalidTokenException;
import com.project.edusync.iam.model.entity.RefreshToken;
import com.project.edusync.iam.model.entity.User;
import com.project.edusync.iam.repository.RefreshTokenRepository;
import com.project.edusync.iam.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {

    // This value is read from application.yml
    @Value("${app.jwt.refresh-expirationTime}")
    private Long refreshTokenDurationMs;

    // This is our new property for the device limit
    @Value("${app.security.max-devices:2}")
    private int MAX_DEVICES;

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional
    public RefreshToken createRefreshToken(User user, String ipAddress) {
        // --- 1. ENFORCE DEVICE LIMIT ---
        enforceDeviceLimit(user);

        // --- 2. CREATE NEW TOKEN ---
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setIpAddress(ipAddress);
        refreshToken.setInvalidated(false);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));

        log.info("Creating new refresh token for user {}", user.getUsername());
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    @Transactional(readOnly = true)
    public RefreshToken verifyRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByTokenAndInvalidated(token, false)
                .orElseThrow(() -> new ResourceNotFoundException("Refresh token not found or already invalidated"));

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            // We invalidate it just to be sure
            refreshTokenRepository.delete(refreshToken);
            log.warn("Expired refresh token used: {}", token);
            throw new InvalidTokenException("Refresh token has expired");
        }

        return refreshToken;
    }

    @Override
    @Transactional
    public void invalidateToken(String token) {
        Optional<RefreshToken> tokenOpt = refreshTokenRepository.findByTokenAndInvalidated(token, false);
        if (tokenOpt.isPresent()) {
            RefreshToken refreshToken = tokenOpt.get();
            refreshToken.setInvalidated(true);
            refreshTokenRepository.save(refreshToken);
            log.info("Invalidated refresh token for user {}", refreshToken.getUser().getUsername());
        } else {
            log.warn("Attempt to invalidate non-existent or already-invalidated token");
        }
    }

    /**
     * Checks if the user has reached the maximum device limit.
     * If so, finds and invalidates the *oldest* active token to make
     * room for the new one.
     */
    private void enforceDeviceLimit(User user) {
        // 1. Count all *active* (non-invalidated) tokens
        long activeCount = refreshTokenRepository.countByUserAndInvalidated(user, false);

        // 2. Check if the limit is reached or exceeded
        if (activeCount >= MAX_DEVICES) {
            // 3. Find the oldest active token (using CreatedAt from AuditableEntity)
            Optional<RefreshToken> oldestTokenOpt = refreshTokenRepository
                    .findFirstByUserAndInvalidatedOrderByCreatedAtAsc(user, false);

            if (oldestTokenOpt.isPresent()) {
                RefreshToken oldestToken = oldestTokenOpt.get();
                log.warn("User {} has reached max device limit ({}). " +
                                "Invalidating oldest token (ID: {}) to make room for new login.",
                        user.getUsername(), MAX_DEVICES, oldestToken.getId());

                // 4. Invalidate it
                oldestToken.setInvalidated(true);
                refreshTokenRepository.save(oldestToken);
            } else {
                // This case should not be reachable if activeCount > 0, but good to log
                log.error("Inconsistent state: Active token count is {} but no oldest token found for user {}",
                        activeCount, user.getUsername());
            }
        }
    }
}