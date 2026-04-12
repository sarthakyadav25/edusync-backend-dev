package com.project.edusync.ams.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class StaffShiftMapRequestDTO {
    @NotNull
    private UUID staffUuid;

    @NotNull
    private UUID shiftUuid;

    @NotNull
    private LocalDate effectiveFrom;
}

