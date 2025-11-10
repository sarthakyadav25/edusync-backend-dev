package com.project.edusync.iam.service;

import com.project.edusync.iam.model.entity.RefreshToken;
import com.project.edusync.iam.model.entity.User;

public interface RefreshTokenService {

    /**
     * Creates and persists a new refresh token for a user.
     * @param user The user for whom to create the token.
     * @param ipAddress The IP address of the user.
     * @return The persisted RefreshToken entity.
     */
    RefreshToken createRefreshToken(User user, String ipAddress);

    /**
     * Verifies that a token is valid, unexpired, and not invalidated.
     * @param token The refresh token string.
     * @return The valid RefreshToken entity.
     * @throws com.project.edusync.common.exception.iam.InvalidTokenException if the token is not valid.
     */
    RefreshToken verifyRefreshToken(String token);

    /**
     * Marks a token as invalidated, typically for logout or token rotation.
     * @param token The refresh token string to invalidate.
     * @throws com.project.edusync.common.exception.iam.InvalidTokenException if the token does not exist.
     */
    void invalidateToken(String token);
}