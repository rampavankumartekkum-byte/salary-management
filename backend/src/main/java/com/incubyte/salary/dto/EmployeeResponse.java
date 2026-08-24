package com.incubyte.salary.dto;

import com.incubyte.salary.entity.EmploymentType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record EmployeeResponse(
        Long id,
        String employeeCode,
        String firstName,
        String lastName,
        String email,
        String department,
        String designation,
        String country,
        String currency,
        BigDecimal baseSalary,
        BigDecimal annualBonus,
        EmploymentType employmentType,
        String managerName,
        LocalDate dateJoined
) {
}
