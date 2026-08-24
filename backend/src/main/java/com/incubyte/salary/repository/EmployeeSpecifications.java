package com.incubyte.salary.repository;

import com.incubyte.salary.entity.Employee;
import com.incubyte.salary.entity.EmploymentType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

/**
 * Builds a single combined Specification from whichever filter fields the
 * caller supplied. Every predicate is null-safe and skipped when its
 * corresponding argument is blank, so this composes cleanly for the
 * employee list/search endpoint regardless of which filters are active.
 */
public final class EmployeeSpecifications {

    private EmployeeSpecifications() {
    }

    public static Specification<Employee> withFilters(String search,
                                                        String department,
                                                        String country,
                                                        String designation,
                                                        EmploymentType employmentType,
                                                        BigDecimal minSalary,
                                                        BigDecimal maxSalary) {
        return Specification.allOf(
                search(search),
                containsIgnoreCase("department", department),
                containsIgnoreCase("country", country),
                containsIgnoreCase("designation", designation),
                employmentTypeEquals(employmentType),
                salaryAtLeast(minSalary),
                salaryAtMost(maxSalary)
        );
    }

    private static Specification<Employee> search(String search) {
        if (!StringUtils.hasText(search)) {
            return null;
        }
        String pattern = "%" + search.trim().toLowerCase() + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("firstName")), pattern),
                cb.like(cb.lower(root.get("lastName")), pattern),
                cb.like(cb.lower(root.get("email")), pattern),
                cb.like(cb.lower(root.get("employeeCode")), pattern)
        );
    }

    private static Specification<Employee> containsIgnoreCase(String field, String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        String pattern = "%" + value.trim().toLowerCase() + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.get(field)), pattern);
    }

    private static Specification<Employee> employmentTypeEquals(EmploymentType employmentType) {
        if (employmentType == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("employmentType"), employmentType);
    }

    private static Specification<Employee> salaryAtLeast(BigDecimal minSalary) {
        if (minSalary == null) {
            return null;
        }
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("baseSalary"), minSalary);
    }

    private static Specification<Employee> salaryAtMost(BigDecimal maxSalary) {
        if (maxSalary == null) {
            return null;
        }
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("baseSalary"), maxSalary);
    }
}
