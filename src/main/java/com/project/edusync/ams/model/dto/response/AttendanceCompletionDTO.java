package com.project.edusync.ams.model.dto.response;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record AttendanceCompletionDTO(
        int month,
        int year,
        long totalActiveStaff,
        int totalWorkingDays,
        long totalExpectedRecords,
        long totalActualRecords,
        double completionPercentage,
        boolean isComplete,
        List<UnmarkedStaffAttendanceDTO> unmarkedStaff
) {
    @Builder
    public record UnmarkedStaffAttendanceDTO(
            String staffUuid,
            String staffName,
            String employeeId,
            List<LocalDate> missingDates
    ) {}
}

