package com.project.edusync.common.model.dto;

import java.time.Instant;

/**
 * A standardized error response sent to the client for most exceptions.
 */
public record ErrorResponse(
        int statusCode,
        String message,
        String path,
        Instant timestamp
) {}