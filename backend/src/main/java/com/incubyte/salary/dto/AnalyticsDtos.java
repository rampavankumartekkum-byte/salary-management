package com.incubyte.salary.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Container for the small set of DTOs that back the analytics dashboard.
 * Kept together since they're simple, closely related, and only ever
 * produced by AnalyticsService - splitting into one-record-per-file would
 * just add navigation overhead for no real benefit.
 */
public final class AnalyticsDtos {

    private AnalyticsDtos() {
    }

    public record PayrollSummary(
            long totalHeadcount,
            BigDecimal totalAnnualBaseSalary,
            BigDecimal totalAnnualBonus,
            BigDecimal averageBaseSalary,
            BigDecimal medianBaseSalary
    ) {
    }

    public record GroupBreakdown(
            String group,
            long headcount,
            BigDecimal totalBaseSalary,
            BigDecimal averageBaseSalary
    ) {
    }

    public record SalaryBand(
            String label,
            BigDecimal rangeStart,
            BigDecimal rangeEnd,
            long headcount
    ) {
    }

    public record DashboardResponse(
            PayrollSummary summary,
            List<GroupBreakdown> byDepartment,
            List<GroupBreakdown> byCountry,
            List<GroupBreakdown> byDesignation,
            List<SalaryBand> salaryBands
    ) {
    }
}
