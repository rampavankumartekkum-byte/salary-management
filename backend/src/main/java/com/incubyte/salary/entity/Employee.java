package com.incubyte.salary.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * A single employee salary record.
 *
 * Deliberately holds only the *current* salary snapshot - no revision history.
 * See REQUIREMENTS.md for why history/versioning is out of scope for this MVP.
 */
@Entity
@Table(name = "employees", indexes = {
        @Index(name = "idx_employee_department", columnList = "department"),
        @Index(name = "idx_employee_country", columnList = "country"),
        @Index(name = "idx_employee_designation", columnList = "designation")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_code", nullable = false, unique = true, length = 20)
    private String employeeCode;

    @Column(nullable = false, length = 120)
    private String firstName;

    @Column(nullable = false, length = 120)
    private String lastName;

    @Column(nullable = false, unique = true, length = 200)
    private String email;

    @Column(nullable = false, length = 80)
    private String department;

    @Column(nullable = false, length = 100)
    private String designation;

    @Column(nullable = false, length = 60)
    private String country;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(name = "base_salary", nullable = false, precision = 14, scale = 2)
    private BigDecimal baseSalary;

    @Column(name = "annual_bonus", nullable = false, precision = 14, scale = 2)
    private BigDecimal annualBonus;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type", nullable = false, length = 20)
    private EmploymentType employmentType;

    @Column(name = "manager_name", length = 200)
    private String managerName;

    @Column(name = "date_joined", nullable = false)
    private LocalDate dateJoined;
}
