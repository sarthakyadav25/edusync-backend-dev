package com.project.edusync.iam.repository;

import com.project.edusync.iam.model.entity.RefreshToken;
import com.project.edusync.iam.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    /**
     * Finds an active (non-invalidated) token by its token string.
     */
    Optional<RefreshToken> findByTokenAndInvalidated(String token, boolean invalidated);

    /**
     * Counts all active (non-invalidated) tokens for a specific user.
     * This tells us how many "devices" are currently active.
     */
    long countByUserAndInvalidated(User user, boolean invalidated);

    /**
     * Finds the oldest active (non-invalidated) token for a user,
     * ordered by its creation date. We use this to decide which token
     * to invalidate when the user exceeds the device limit.
     */
    Optional<RefreshToken> findFirstByUserAndInvalidatedOrderByCreatedAtAsc(User user, boolean invalidated);
}