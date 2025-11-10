package com.project.edusync.iam.controller;

import com.project.edusync.common.model.dto.response.MessageResponse;
import com.project.edusync.common.utils.RequestUtil;
import com.project.edusync.iam.model.dto.auth.*;
import com.project.edusync.iam.model.dto.user.MeResponse;
import com.project.edusync.iam.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.url}/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication API", description = "Endpoints for user authentication, session management, and password reset.")
public class AuthController {

    private final AuthService authService;
    private final RequestUtil requestUtil;

    @Operation(summary = "Authenticate User", description = "Logs in a user with username and password, returning JWT access and refresh tokens.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication successful"),
            @ApiResponse(responseCode = "400", description = "Invalid request data (validation error)"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        LoginResponse loginResponse = authService.loginUser(loginRequest, requestUtil.getClientIp(request));
        return ResponseEntity.ok(loginResponse);
    }

    @Operation(summary = "Logout User", description = "Invalidates the user's session and blacklists the refresh token.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout successful"),
            @ApiResponse(responseCode = "400", description = "Refresh token not provided"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(@Valid @RequestBody LogoutRequest logoutRequest) {
        authService.logoutUser(logoutRequest);
        return ResponseEntity.ok(new MessageResponse("Logout successful."));
    }

    @Operation(summary = "Refresh Access Token", description = "Issues a new access token using a valid refresh token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid or missing refresh token"),
            @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
    })
    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        RefreshTokenResponse response = authService.refreshAccessToken(refreshTokenRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Forgot Password", description = "Initiates the password reset process by sending a reset link to the user's email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "If the email exists, a reset link has been sent."),
            @ApiResponse(responseCode = "400", description = "Invalid email format")
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        authService.initiatePasswordReset(forgotPasswordRequest);
        // Generic response to prevent email enumeration attacks
        return ResponseEntity.ok(new MessageResponse("If your email is registered, a password reset link has been sent."));
    }

    @Operation(summary = "Reset Password", description = "Resets the user's password using a valid token received via email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password has been reset successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid token or password format")
    })
    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        authService.completePasswordReset(resetPasswordRequest);
        return ResponseEntity.ok(new MessageResponse("Password has been reset successfully."));
    }

    @Operation(summary = "Get Current User Profile", description = "Retrieves the complete profile for the currently authenticated user.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User profile retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User profile not found")
    })
    @GetMapping("/me")
    public ResponseEntity<MeResponse> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        // @AuthenticationPrincipal injects the UserDetails object created by our
        // CustomUserDetailService and JWTFilter
        MeResponse userProfile = authService.getUserProfile(userDetails);
        return ResponseEntity.ok(userProfile);
    }
}