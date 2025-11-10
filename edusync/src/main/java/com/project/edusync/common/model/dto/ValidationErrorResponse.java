// File: com/project/edusync/common/model/dto/ValidationErrorResponse.java
package com.project.edusync.common.model.dto;

import java.time.Instant;
import java.util.Map;

/**
 * A specialized error response for DTO validation failures, 
 * including a map of field-specific errors.
 */
public record ValidationErrorResponse(
        int statusCode,
        String message,
        String path,
        Instant timestamp,
        Map<String, String> fieldErrors
) {}