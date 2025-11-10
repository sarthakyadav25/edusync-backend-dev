package com.project.edusync.iam.model.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record LogoutRequest(
        @NotBlank(message = "Refresh token is required to log out.")
        String refreshToken
) {}