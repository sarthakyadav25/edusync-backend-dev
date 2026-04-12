package com.project.edusync.finance.service.implementation;

import com.project.edusync.ams.model.repository.StaffDailyAttendanceRepository;
import com.project.edusync.ams.model.repository.StudentDailyAttendanceRepository;
import com.project.edusync.common.config.CacheNames;
import com.project.edusync.finance.dto.dashboard.MasterAnalyticsResponseDTO;
import com.project.edusync.finance.repository.InvoiceRepository;
import com.project.edusync.finance.repository.PaymentRepository;
import com.project.edusync.finance.service.MasterDashboardAnalyticsService;
import com.project.edusync.hrms.model.enums.PayrollRunStatus;
import com.project.edusync.hrms.repository.PayrollRunRepository;
import com.project.edusync.uis.model.enums.StaffCategory;
import com.project.edusync.uis.repository.StaffRepository;
import com.project.edusync.uis.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class MasterDashboardAnalyticsServiceImpl implements MasterDashboardAnalyticsService {

    private static final DateTimeFormatter MONTH_LABEL = DateTimeFormatter.ofPattern("MMM", Locale.ENGLISH);
    private static final DateTimeFormatter DAY_LABEL = DateTimeFormatter.ofPattern("MMM dd", Locale.ENGLISH);
    private static final Pattern CLASS_NUMBER_PATTERN = Pattern.compile("(\\d{1,2})");

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final PayrollRunRepository payrollRunRepository;
    private final StudentDailyAttendanceRepository studentDailyAttendanceRepository;
    private final StaffDailyAttendanceRepository staffDailyAttendanceRepository;
    private final StudentRepository studentRepository;
    private final StaffRepository staffRepository;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheNames.MASTER_DASHBOARD_ANALYTICS, key = "'master'")
    public MasterAnalyticsResponseDTO getMasterAnalytics() {
        return MasterAnalyticsResponseDTO.builder()
                .financePayrollTrend(buildFinancePayrollTrend())
                .attendanceTrend(buildAttendanceTrend())
                .demographics(buildDemographics())
                .build();
    }

    private List<MasterAnalyticsResponseDTO.FinancePayrollPoint> buildFinancePayrollTrend() {
        List<MasterAnalyticsResponseDTO.FinancePayrollPoint> points = new ArrayList<>();
        YearMonth currentMonth = YearMonth.now();

        for (int i = 5; i >= 0; i--) {
            YearMonth target = currentMonth.minusMonths(i);
            int year = target.getYear();
            int month = target.getMonthValue();

            BigDecimal expected = nullSafe(invoiceRepository.sumExpectedByIssueYearMonth(year, month));
            BigDecimal collected = nullSafe(paymentRepository.sumCollectedByYearMonth(year, month));
            BigDecimal payroll = nullSafe(payrollRunRepository.sumTotalNetByMonthAndStatuses(
                    year,
                    month,
                    EnumSet.of(PayrollRunStatus.PROCESSED, PayrollRunStatus.APPROVED, PayrollRunStatus.DISBURSED)
            ));

            points.add(MasterAnalyticsResponseDTO.FinancePayrollPoint.builder()
                    .month(target.format(MONTH_LABEL))
                    .expected(expected)
                    .collected(collected)
                    .payroll(payroll)
                    .build());
        }

        return points;
    }

    private List<MasterAnalyticsResponseDTO.AttendancePoint> buildAttendanceTrend() {
        List<MasterAnalyticsResponseDTO.AttendancePoint> points = new ArrayList<>();
        LocalDate today = LocalDate.now();

        long totalActiveStudents = studentRepository.countByIsActiveTrue();
        long totalActiveStaff = staffRepository.countByIsActiveTrue();

        for (int i = 13; i >= 0; i--) {
            LocalDate date = today.minusDays(i);

            long presentStudents = studentDailyAttendanceRepository.countDistinctPresentStudentsByDate(date);
            long presentStaff = staffDailyAttendanceRepository.countDistinctPresentStaffByDate(date);

            points.add(MasterAnalyticsResponseDTO.AttendancePoint.builder()
                    .day(date.format(DAY_LABEL))
                    .student(toPercent(presentStudents, totalActiveStudents))
                    .staff(toPercent(presentStaff, totalActiveStaff))
                    .build());
        }

        return points;
    }

    private List<MasterAnalyticsResponseDTO.DemographicPoint> buildDemographics() {
        long primaryStudents = 0;
        long secondaryStudents = 0;

        for (Object[] row : studentRepository.countActiveStudentsGroupedByClassName()) {
            String className = row[0] == null ? "" : row[0].toString();
            String normalized = className.toLowerCase(Locale.ENGLISH);
            long count = row[1] == null ? 0L : ((Number) row[1]).longValue();
            Integer classNumber = extractClassNumber(className);

            if (classNumber != null && classNumber >= 1 && classNumber <= 5) {
                primaryStudents += count;
            } else if (classNumber != null && classNumber >= 6 && classNumber <= 12) {
                secondaryStudents += count;
            } else if (normalized.contains("primary")) {
                primaryStudents += count;
            } else if (normalized.contains("secondary")) {
                secondaryStudents += count;
            }
        }

        long teachingStaff = staffRepository.countByIsActiveTrueAndCategory(StaffCategory.TEACHING);
        long supportStaff = staffRepository.countByIsActiveTrueAndCategory(StaffCategory.NON_TEACHING_ADMIN)
                + staffRepository.countByIsActiveTrueAndCategory(StaffCategory.NON_TEACHING_SUPPORT);

        return List.of(
                MasterAnalyticsResponseDTO.DemographicPoint.builder()
                        .name("Students Primary")
                        .value(primaryStudents)
                        .color("#3b82f6")
                        .build(),
                MasterAnalyticsResponseDTO.DemographicPoint.builder()
                        .name("Students Secondary")
                        .value(secondaryStudents)
                        .color("#60a5fa")
                        .build(),
                MasterAnalyticsResponseDTO.DemographicPoint.builder()
                        .name("Teaching Staff")
                        .value(teachingStaff)
                        .color("#8b5cf6")
                        .build(),
                MasterAnalyticsResponseDTO.DemographicPoint.builder()
                        .name("Support Staff")
                        .value(supportStaff)
                        .color("#c4b5fd")
                        .build()
        );
    }

    private Integer extractClassNumber(String className) {
        Matcher matcher = CLASS_NUMBER_PATTERN.matcher(className == null ? "" : className);
        if (!matcher.find()) {
            return null;
        }
        return Integer.parseInt(matcher.group(1));
    }

    private BigDecimal nullSafe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private double toPercent(long numerator, long denominator) {
        if (denominator <= 0) {
            return 0.0;
        }
        double raw = (numerator * 100.0) / denominator;
        double clamped = Math.max(0.0, Math.min(100.0, raw));
        return BigDecimal.valueOf(clamped).setScale(1, RoundingMode.HALF_UP).doubleValue();
    }
}


