package com.project.edusync.iam.model.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ForgotPasswordRequest(
        @NotBlank(message = "Email is required.")
        @Email(message = "Please provide a valid email address.")
        @Size(max = 100, message = "Email must not exceed 100 characters.")
        String email
) {}
