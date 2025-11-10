package com.project.edusync.iam.model.dto.auth;

import com.project.edusync.iam.model.dto.user.UserDetailsDto;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        UserDetailsDto userDetailsDto
) {}