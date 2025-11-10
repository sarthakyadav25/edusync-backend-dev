// File: com/project/edusync/iam/service/impl/PasswordResetTokenServiceImpl.java
package com.project.edusync.iam.service.impl;

import com.project.edusync.common.exception.iam.InvalidTokenException;
import com.project.edusync.iam.model.entity.PasswordResetToken;
import com.project.edusync.iam.model.entity.User;
import com.project.edusync.iam.repository.PasswordResetTokenRepository;
import com.project.edusync.iam.service.PasswordResetTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetTokenServiceImpl implements PasswordResetTokenService {

    // You must add this property to your application.yml
    // Example: 3600000 (for 1 hour)
    @Value("${app.jwt.password-reset-expirationTime}")
    private long resetExpirationTimeMs;

    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Override
    @Transactional
    public PasswordResetToken createResetToken(User user) {
        // Clean up any existing tokens for this user
        passwordResetTokenRepository.deleteByUser(user);

        String tokenString = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .user(user)
                .token(tokenString)
                .expiryDate(Instant.now().plusMillis(resetExpirationTimeMs))
                .build();

        return passwordResetTokenRepository.save(resetToken);
    }

    @Override
    @Transactional(readOnly = true)
    public void validateResetToken(String token) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid password reset token."));

        if (resetToken.getExpiryDate().isBefore(Instant.now())) {
            passwordResetTokenRepository.delete(resetToken); // Clean up expired token
            throw new InvalidTokenException("Password reset token has expired.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByToken(String token) {
        return passwordResetTokenRepository.findByToken(token)
                .map(PasswordResetToken::getUser)
                .orElseThrow(() -> new InvalidTokenException("Invalid password reset token."));
    }

    @Override
    @Transactional
    public void invalidateToken(String token) {
        // We delete the token on use to ensure it's single-use
        passwordResetTokenRepository.findByToken(token)
                .ifPresent(passwordResetTokenRepository::delete);
    }
}