package com.project.edusync.iam.repository;

import com.project.edusync.iam.model.entity.RefreshToken;
import com.project.edusync.iam.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    void deleteByUser(User user); // Good for cleanup if needed
}