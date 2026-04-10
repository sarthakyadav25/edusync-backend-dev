package com.project.edusync.ams.model.enums;

/**
 * Defines the source method by which an attendance record was captured.
 */
public enum AttendanceSource {
    MANUAL,
    WEB,
    MOBILE,
    SELF_CAPTURE,
    BIOMETRIC,
    SYSTEM // e.g., automatically marked based on a timetable
}