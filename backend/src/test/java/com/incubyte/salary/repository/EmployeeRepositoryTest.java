package com.incubyte.salary.repository;

import com.incubyte.salary.entity.Employee;
import com.incubyte.salary.entity.EmploymentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    private Employee buildEmployee(String code, String email, String department, BigDecimal salary) {
        return Employee.builder()
                .employeeCode(code)
                .firstName("Test")
                .lastName("User")
                .email(email)
                .department(department)
                .designation("Software Engineer")
                .country("India")
                .currency("INR")
                .baseSalary(salary)
                .annualBonus(BigDecimal.valueOf(1000))
                .employmentType(EmploymentType.FULL_TIME)
                .dateJoined(LocalDate.of(2022, 1, 10))
                .build();
    }

    @Test
    void savesAndRetrievesEmployee_withGeneratedId() {
        Employee saved = employeeRepository.save(
                buildEmployee("ACME00001", "test1@acme-corp.example", "Engineering", BigDecimal.valueOf(50000)));

        assertThat(saved.getId()).isNotNull();
        assertThat(employeeRepository.findById(saved.getId())).isPresent();
    }

    @Test
    void filtersByDepartment_usingSpecification() {
        employeeRepository.save(buildEmployee("ACME00002", "eng1@acme-corp.example", "Engineering", BigDecimal.valueOf(60000)));
        employeeRepository.save(buildEmployee("ACME00003", "sales1@acme-corp.example", "Sales", BigDecimal.valueOf(45000)));

        var spec = EmployeeSpecifications.withFilters(null, "Engineering", null, null, null, null, null);
        var results = employeeRepository.findAll(spec);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getDepartment()).isEqualTo("Engineering");
    }

    @Test
    void filtersBySalaryRange_usingSpecification() {
        employeeRepository.save(buildEmployee("ACME00004", "low@acme-corp.example", "Engineering", BigDecimal.valueOf(30000)));
        employeeRepository.save(buildEmployee("ACME00005", "high@acme-corp.example", "Engineering", BigDecimal.valueOf(90000)));

        var spec = EmployeeSpecifications.withFilters(
                null, null, null, null, null, BigDecimal.valueOf(50000), BigDecimal.valueOf(100000));
        var results = employeeRepository.findAll(spec);

        assertThat(results).extracting(Employee::getEmail).containsExactly("high@acme-corp.example");
    }

    @Test
    void existsByEmail_reflectsSavedState() {
        employeeRepository.save(buildEmployee("ACME00006", "dup@acme-corp.example", "Engineering", BigDecimal.valueOf(40000)));

        assertThat(employeeRepository.existsByEmail("dup@acme-corp.example")).isTrue();
        assertThat(employeeRepository.existsByEmail("nobody@acme-corp.example")).isFalse();
    }
    @Test
    void filtersByPartialDepartment_usingSpecification() {
        employeeRepository.save(buildEmployee("ACME00007", "eng2@acme-corp.example", "Engineering", BigDecimal.valueOf(60000)));
        employeeRepository.save(buildEmployee("ACME00008", "sales2@acme-corp.example", "Sales", BigDecimal.valueOf(45000)));

        var spec = EmployeeSpecifications.withFilters(null, "engin", null, null, null, null, null);
        var results = employeeRepository.findAll(spec);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getDepartment()).isEqualTo("Engineering");
    }

    @Test
    void filtersByPartialDesignation_usingSpecification() {
        employeeRepository.save(buildEmployee("ACME00009", "dev@acme-corp.example", "Engineering", BigDecimal.valueOf(70000)));
        employeeRepository.findById(1L);
        var employee = employeeRepository.findAll().stream().filter(e -> e.getEmployeeCode().equals("ACME00009")).findFirst().orElseThrow();
        employee.setDesignation("Senior Software Engineer");
        employeeRepository.save(employee);

        var spec = EmployeeSpecifications.withFilters(null, null, null, "software eng", null, null, null);
        var results = employeeRepository.findAll(spec);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getDesignation()).isEqualTo("Senior Software Engineer");
    }

}
