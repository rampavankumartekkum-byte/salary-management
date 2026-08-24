package com.incubyte.salary.dto;

import com.incubyte.salary.entity.EmploymentType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EmployeeRequest(
        @NotBlank(message = "employeeCode is required")
        @Size(max = 20)
        String employeeCode,

        @NotBlank(message = "firstName is required")
        @Size(max = 120)
        String firstName,

        @NotBlank(message = "lastName is required")
        @Size(max = 120)
        String lastName,

        @NotBlank(message = "email is required")
        @Email(message = "email must be a valid email address")
        @Size(max = 200)
        String email,

        @NotBlank(message = "department is required")
        String department,

        @NotBlank(message = "designation is required")
        String designation,

        @NotBlank(message = "country is required")
        String country,

        @NotBlank(message = "currency is required")
        @Size(min = 3, max = 3, message = "currency must be a 3-letter ISO code")
        String currency,

        @NotNull(message = "baseSalary is required")
        @DecimalMin(value = "0.0", inclusive = true, message = "baseSalary cannot be negative")
        BigDecimal baseSalary,

        @NotNull(message = "annualBonus is required")
        @DecimalMin(value = "0.0", inclusive = true, message = "annualBonus cannot be negative")
        BigDecimal annualBonus,

        @NotNull(message = "employmentType is required")
        EmploymentType employmentType,

        String managerName,

        @NotNull(message = "dateJoined is required")
        @PastOrPresent(message = "dateJoined cannot be in the future")
        LocalDate dateJoined
) {
}
