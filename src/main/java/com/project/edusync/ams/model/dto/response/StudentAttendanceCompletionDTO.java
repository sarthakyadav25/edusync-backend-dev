package com.project.edusync.ams.model.dto.response;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record StudentAttendanceCompletionDTO(
        String classUuid,
        String sectionUuid,
        LocalDate fromDate,
        LocalDate toDate,
        int totalStudents,
        List<LocalDate> datesWithRecords,
        List<LocalDate> datesWithoutRecords
) {
}

