package com.project.edusync.ams.model.service;

import com.project.edusync.ams.model.dto.request.StaffAttendanceRequestDTO;
import com.project.edusync.ams.model.enums.AttendanceSource;
import com.project.edusync.ams.model.exception.AttendanceProcessingException;
import com.project.edusync.common.settings.service.AppSettingService;
import com.project.edusync.uis.model.entity.Staff;
import com.project.edusync.uis.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GeoFenceValidator {

    private static final String GEOFENCE_ENABLED_KEY = "attendance.geofence.enabled";
    private static final String GEOFENCE_LAT_KEY = "attendance.geofence.latitude";
    private static final String GEOFENCE_LNG_KEY = "attendance.geofence.longitude";
    private static final String GEOFENCE_RADIUS_KEY = "attendance.geofence.radius.meters";

    private final AppSettingService appSettingService;
    private final StaffRepository staffRepository;

    /**
     * Lenient verifier used by createAttendance: when coordinates are present,
     * compute proximity server-side and set geoVerified accordingly.
     */
    public boolean verifyByCoordinatesIfPresent(Double latitude, Double longitude) {
        if (latitude == null || longitude == null) {
            return false;
        }

        Optional<Double> schoolLat = parseSettingAsDoubleSafely(GEOFENCE_LAT_KEY);
        Optional<Double> schoolLng = parseSettingAsDoubleSafely(GEOFENCE_LNG_KEY);
        Optional<Integer> radius = parseSettingAsIntSafely(GEOFENCE_RADIUS_KEY);

        if (schoolLat.isEmpty() || schoolLng.isEmpty() || radius.isEmpty()) {
            return false;
        }

        double distance = haversineMeters(latitude, longitude, schoolLat.get(), schoolLng.get());
        return distance <= radius.get();
    }

    public boolean validateAndResolveGeoVerified(StaffAttendanceRequestDTO request, Long performedByUserId, Long targetStaffId) {
        boolean geofenceEnabled = appSettingService.getBooleanValue(GEOFENCE_ENABLED_KEY, false);
        if (!geofenceEnabled) {
            return false;
        }

        if (request.getSource() == AttendanceSource.BIOMETRIC || request.getSource() == AttendanceSource.SYSTEM) {
            return false;
        }

        boolean adminRole = hasAuthority("ROLE_SUPER_ADMIN") || hasAuthority("ROLE_SCHOOL_ADMIN") || hasAuthority("ROLE_ADMIN");
        boolean selfCheckIn = isSelfCheckIn(performedByUserId, targetStaffId);

        if (adminRole || !selfCheckIn) {
            return false;
        }

        if (request.getLatitude() == null || request.getLongitude() == null) {
            throw new AttendanceProcessingException("Location data is required for self check-in");
        }

        double schoolLat = parseSettingAsDouble(GEOFENCE_LAT_KEY);
        double schoolLng = parseSettingAsDouble(GEOFENCE_LNG_KEY);
        int radius = Integer.parseInt(appSettingService.getValue(GEOFENCE_RADIUS_KEY, "200"));

        double distance = haversineMeters(request.getLatitude(), request.getLongitude(), schoolLat, schoolLng);
        if (distance > radius) {
            throw new AttendanceProcessingException(
                    "GEO_FENCE_VIOLATION: You are not within school premises. You are approximately "
                            + Math.round(distance)
                            + "m away. Allowed radius: "
                            + radius
                            + "m."
            );
        }

        return true;
    }

    public static double haversineMeters(double lat1, double lon1, double lat2, double lon2) {
        final double r = 6_371_000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return r * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    private boolean isSelfCheckIn(Long performedByUserId, Long targetStaffId) {
        if (performedByUserId == null || targetStaffId == null) {
            return false;
        }
        Optional<Staff> actorStaff = staffRepository.findByUserProfile_User_Id(performedByUserId);
        return actorStaff.map(staff -> targetStaffId.equals(staff.getId())).orElse(false);
    }

    private boolean hasAuthority(String authority) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
                && authentication.getAuthorities() != null
                && authentication.getAuthorities().stream().anyMatch(a -> authority.equalsIgnoreCase(a.getAuthority()));
    }

    private double parseSettingAsDouble(String key) {
        String raw = appSettingService.getValue(key, "").trim();
        if (raw.isBlank()) {
            throw new AttendanceProcessingException("Geo-fence coordinates are not configured in app settings");
        }
        try {
            return Double.parseDouble(raw);
        } catch (NumberFormatException ex) {
            throw new AttendanceProcessingException("Invalid geo-fence setting for key: " + key);
        }
    }

    private Optional<Double> parseSettingAsDoubleSafely(String key) {
        String raw = appSettingService.getValue(key, "").trim();
        if (raw.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(Double.parseDouble(raw));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    private Optional<Integer> parseSettingAsIntSafely(String key) {
        String raw = appSettingService.getValue(key, "").trim();
        if (raw.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(Integer.parseInt(raw));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }
}

