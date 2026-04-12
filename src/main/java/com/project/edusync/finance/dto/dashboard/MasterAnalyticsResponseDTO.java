package com.project.edusync.finance.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterAnalyticsResponseDTO {

    private List<FinancePayrollPoint> financePayrollTrend;
    private List<AttendancePoint> attendanceTrend;
    private List<DemographicPoint> demographics;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FinancePayrollPoint {
        private String month;
        private BigDecimal expected;
        private BigDecimal collected;
        private BigDecimal payroll;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AttendancePoint {
        private String day;
        private double student;
        private double staff;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DemographicPoint {
        private String name;
        private long value;
        private String color;
    }
}

