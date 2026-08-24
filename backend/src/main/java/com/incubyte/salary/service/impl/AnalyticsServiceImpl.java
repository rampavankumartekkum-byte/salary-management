package com.incubyte.salary.service.impl;

import com.incubyte.salary.dto.AnalyticsDtos.DashboardResponse;
import com.incubyte.salary.dto.AnalyticsDtos.GroupBreakdown;
import com.incubyte.salary.dto.AnalyticsDtos.PayrollSummary;
import com.incubyte.salary.dto.AnalyticsDtos.SalaryBand;
import com.incubyte.salary.service.AnalyticsService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * All aggregates are computed in the database rather than pulled into Java, since this needs to
 * stay fast at 10k+ rows (and much more in a real org) - summing/grouping/percentile-ing in the
 * app layer would mean loading every row into memory on every dashboard refresh.
 *
 * Known simplification: amounts are aggregated as raw baseSalary values without FX-normalizing
 * across currencies (see REQUIREMENTS.md - "Live FX conversion" is explicitly out of scope).
 * For the seeded demo data this is still directionally useful per group, but a real multi-currency
 * deployment would need to normalize to one reporting currency before summing across countries.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalyticsServiceImpl implements AnalyticsService {

    private static final BigDecimal[] BAND_EDGES = {
            BigDecimal.valueOf(30_000), BigDecimal.valueOf(60_000), BigDecimal.valueOf(90_000),
            BigDecimal.valueOf(120_000), BigDecimal.valueOf(150_000)
    };

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public DashboardResponse getDashboard() {
        return new DashboardResponse(
                fetchSummary(),
                fetchBreakdown("department"),
                fetchBreakdown("country"),
                fetchBreakdown("designation"),
                fetchSalaryBands()
        );
    }

    private PayrollSummary fetchSummary() {
        Tuple row = (Tuple) entityManager.createNativeQuery("""
                SELECT
                    COUNT(*)                                            AS headcount,
                    COALESCE(SUM(base_salary), 0)                       AS total_base,
                    COALESCE(SUM(annual_bonus), 0)                      AS total_bonus,
                    COALESCE(AVG(base_salary), 0)                       AS avg_base,
                    COALESCE(PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY base_salary), 0) AS median_base
                FROM employees
                """, Tuple.class).getSingleResult();

        return new PayrollSummary(
                ((Number) row.get("headcount")).longValue(),
                scaled(row.get("total_base")),
                scaled(row.get("total_bonus")),
                scaled(row.get("avg_base")),
                scaled(row.get("median_base"))
        );
    }

    @SuppressWarnings("unchecked")
    private List<GroupBreakdown> fetchBreakdown(String column) {
        // column is one of a fixed internal allow-list (never user input), safe to inline.
        List<Tuple> rows = entityManager.createNativeQuery("""
                SELECT %s AS grp,
                       COUNT(*) AS headcount,
                       COALESCE(SUM(base_salary), 0) AS total_base,
                       COALESCE(AVG(base_salary), 0) AS avg_base
                FROM employees
                GROUP BY %s
                ORDER BY total_base DESC
                """.formatted(column, column), Tuple.class).getResultList();

        List<GroupBreakdown> result = new ArrayList<>();
        for (Tuple row : rows) {
            result.add(new GroupBreakdown(
                    String.valueOf(row.get("grp")),
                    ((Number) row.get("headcount")).longValue(),
                    scaled(row.get("total_base")),
                    scaled(row.get("avg_base"))
            ));
        }
        return result;
    }

    private List<SalaryBand> fetchSalaryBands() {
        List<SalaryBand> bands = new ArrayList<>();
        BigDecimal previous = BigDecimal.ZERO;
        for (BigDecimal edge : BAND_EDGES) {
            bands.add(countBand(previous, edge));
            previous = edge;
        }
        bands.add(countBandOpenEnded(previous));
        return bands;
    }

    private SalaryBand countBand(BigDecimal start, BigDecimal end) {
        Number count = (Number) entityManager.createNativeQuery("""
                SELECT COUNT(*) FROM employees WHERE base_salary >= :start AND base_salary < :end
                """)
                .setParameter("start", start)
                .setParameter("end", end)
                .getSingleResult();
        return new SalaryBand(formatLabel(start, end), start, end, count.longValue());
    }

    private SalaryBand countBandOpenEnded(BigDecimal start) {
        Number count = (Number) entityManager.createNativeQuery("""
                SELECT COUNT(*) FROM employees WHERE base_salary >= :start
                """)
                .setParameter("start", start)
                .getSingleResult();
        return new SalaryBand(start.toBigInteger() + "+", start, null, count.longValue());
    }

    private String formatLabel(BigDecimal start, BigDecimal end) {
        return start.toBigInteger() + " - " + end.toBigInteger();
    }

    private BigDecimal scaled(Object value) {
        return new BigDecimal(value.toString()).setScale(2, RoundingMode.HALF_UP);
    }
}
