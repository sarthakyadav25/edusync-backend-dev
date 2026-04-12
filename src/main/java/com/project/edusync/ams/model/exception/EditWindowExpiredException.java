package com.project.edusync.ams.model.exception;

import java.time.Instant;
import java.time.LocalDate;

public class EditWindowExpiredException extends RuntimeException {

    private final LocalDate attendanceDate;
    private final int windowHours;
    private final Instant expiredAt;

    public EditWindowExpiredException(LocalDate attendanceDate, int windowHours, Instant expiredAt) {
        super("Attendance edit window has expired. The record for " + attendanceDate + " could not be modified. Window: " + windowHours + " hours. Contact your School Admin.");
        this.attendanceDate = attendanceDate;
        this.windowHours = windowHours;
        this.expiredAt = expiredAt;
    }

    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }

    public int getWindowHours() {
        return windowHours;
    }

    public Instant getExpiredAt() {
        return expiredAt;
    }
}

