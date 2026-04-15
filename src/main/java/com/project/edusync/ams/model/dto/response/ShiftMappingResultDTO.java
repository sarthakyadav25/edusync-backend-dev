package com.project.edusync.ams.model.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record ShiftMappingResultDTO(
        int success,
        int failed,
        List<String> errors
) {
}

