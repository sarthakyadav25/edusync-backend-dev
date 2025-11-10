package com.project.edusync.iam.service;

import com.project.edusync.iam.model.dto.auth.*;
import com.project.edusync.iam.model.dto.user.MeResponse;
import com.project.edusync.iam.model.dto.user.UserDetailsDto;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface AuthService {

    LoginResponse loginUser(LoginRequest loginRequest, String ipAddress);

    void logoutUser(LogoutRequest logoutRequest);

    RefreshTokenResponse refreshAccessToken(RefreshTokenRequest refreshTokenRequest);

    void initiatePasswordReset(ForgotPasswordRequest forgotPasswordRequest);

    void completePasswordReset(ResetPasswordRequest resetPasswordRequest);

    MeResponse getUserProfile(UserDetails userDetails);

    @Transactional(readOnly = true)
    MeResponse getUserProfile(UserDetailsDto userDetailsDto);
}