package com.project.edusync.iam.service;

import com.project.edusync.iam.model.entity.PasswordResetToken;
import com.project.edusync.iam.model.entity.User;

public interface PasswordResetTokenService {

    /**
     * Creates a new, single-use password reset token for a user.
     * @param user The user requesting the reset.
     * @return The persisted PasswordResetToken entity.
     */
    PasswordResetToken createResetToken(User user);

    /**
     * Validates that a token exists and has not expired.
     * @param token The token string.
     * @throws com.project.edusync.common.exception.iam.InvalidTokenException if the token is not valid.
     */
    void validateResetToken(String token);

    /**
     * Retrieves the User associated with a valid token.
     * @param token The token string.
     * @return The associated User entity.
     * @throws com.project.edusync.common.exception.iam.InvalidTokenException if the token is not valid.
     */
    User getUserByToken(String token);

    /**
     * Deletes/Invalidates a token after it has been successfully used.
     * @param token The token string to invalidate.
     */
    void invalidateToken(String token);
}