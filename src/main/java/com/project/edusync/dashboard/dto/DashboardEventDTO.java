package com.project.edusync.dashboard.dto;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record DashboardEventDTO(
        UUID id,
        String type,
        String severity,
        String title,
        String message,
        String actionUrl,
        Boolean isRead,
        Map<String, Object> metadata,
        LocalDateTime createdAt
) {}
