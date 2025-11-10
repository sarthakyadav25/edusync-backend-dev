package com.project.edusync.iam.model.dto.auth;

public record RefreshTokenResponse(
        String accessToken,
        String refreshToken
) {}