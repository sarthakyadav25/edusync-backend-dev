package com.project.edusync.ams.model.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class BulkStaffShiftMapRequestDTO {
    @NotEmpty
    private List<UUID> staffUuids;

    @NotNull
    private UUID shiftUuid;

    @NotNull
    private LocalDate effectiveFrom;
}

