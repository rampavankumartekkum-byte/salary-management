package com.incubyte.salary.service;

import com.incubyte.salary.dto.AnalyticsDtos.DashboardResponse;
import com.incubyte.salary.entity.Employee;
import com.incubyte.salary.entity.EmploymentType;
import com.incubyte.salary.repository.EmployeeRepository;
import com.incubyte.salary.service.impl.AnalyticsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(AnalyticsServiceImpl.class)
class AnalyticsServiceImplTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AnalyticsServiceImpl analyticsService;

    private Employee employee(String code, String department, String country, BigDecimal salary) {
        return Employee.builder()
                .employeeCode(code)
                .firstName("Test")
                .lastName("User")
                .email(code.toLowerCase() + "@acme-corp.example")
                .department(department)
                .designation("Software Engineer")
                .country(country)
                .currency("USD")
                .baseSalary(salary)
                .annualBonus(BigDecimal.valueOf(1000))
                .employmentType(EmploymentType.FULL_TIME)
                .dateJoined(LocalDate.of(2022, 1, 10))
                .build();
    }

    @Test
    void dashboard_computesSummaryAndBreakdowns_acrossSeededEmployees() {
        employeeRepository.save(employee("A1", "Engineering", "USA", BigDecimal.valueOf(50000)));
        employeeRepository.save(employee("A2", "Engineering", "USA", BigDecimal.valueOf(70000)));
        employeeRepository.save(employee("A3", "Sales", "India", BigDecimal.valueOf(30000)));

        DashboardResponse dashboard = analyticsService.getDashboard();

        assertThat(dashboard.summary().totalHeadcount()).isEqualTo(3);
        assertThat(dashboard.summary().totalAnnualBaseSalary()).isEqualByComparingTo("150000.00");
        assertThat(dashboard.summary().averageBaseSalary()).isEqualByComparingTo("50000.00");

        assertThat(dashboard.byDepartment())
                .anySatisfy(g -> {
                    assertThat(g.group()).isEqualTo("Engineering");
                    assertThat(g.headcount()).isEqualTo(2);
                });

        assertThat(dashboard.byCountry())
                .anySatisfy(g -> {
                    assertThat(g.group()).isEqualTo("India");
                    assertThat(g.headcount()).isEqualTo(1);
                });

        long bandedTotal = dashboard.salaryBands().stream().mapToLong(b -> b.headcount()).sum();
        assertThat(bandedTotal).isEqualTo(3);
    }
}
