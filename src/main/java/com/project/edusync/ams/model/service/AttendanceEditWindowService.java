package com.project.edusync.ams.model.service;

import com.project.edusync.ams.model.exception.EditWindowExpiredException;
import com.project.edusync.common.settings.service.AppSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class AttendanceEditWindowService {

    private static final String EDIT_WINDOW_ENABLED_KEY = "attendance.edit.window.enabled";
    private static final String TEACHER_WINDOW_KEY = "attendance.edit.window.teacher.hours";
    private static final String ADMIN_WINDOW_KEY = "attendance.edit.window.school_admin.hours";

    private final AppSettingService appSettingService;

    public void enforceForAttendanceDate(LocalDate attendanceDate) {
        boolean enabled = appSettingService.getBooleanValue(EDIT_WINDOW_ENABLED_KEY, true);
        if (!enabled || attendanceDate == null) {
            return;
        }

        int windowHours = resolveWindowHoursByRole();
        if (windowHours == 0) {
            return;
        }

        LocalDateTime expiresAt = attendanceDate.atStartOfDay().plusHours(windowHours);
        if (LocalDateTime.now().isAfter(expiresAt)) {
            throw new EditWindowExpiredException(attendanceDate, windowHours, expiresAt.toInstant(ZoneOffset.UTC));
        }
    }

    private int resolveWindowHoursByRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isSchoolOrSuperAdmin = hasAuthority(authentication, "ROLE_SUPER_ADMIN")
                || hasAuthority(authentication, "ROLE_SCHOOL_ADMIN")
                || hasAuthority(authentication, "ROLE_ADMIN");

        String key = isSchoolOrSuperAdmin ? ADMIN_WINDOW_KEY : TEACHER_WINDOW_KEY;
        String fallback = isSchoolOrSuperAdmin ? "0" : "48";
        return Integer.parseInt(appSettingService.getValue(key, fallback));
    }

    private boolean hasAuthority(Authentication authentication, String authority) {
        return authentication != null
                && authentication.getAuthorities() != null
                && authentication.getAuthorities().stream().anyMatch(a -> authority.equalsIgnoreCase(a.getAuthority()));
    }
}

