package com.incubyte.salary.service;

import com.incubyte.salary.dto.EmployeeRequest;
import com.incubyte.salary.dto.EmployeeResponse;
import com.incubyte.salary.dto.PagedResponse;
import com.incubyte.salary.entity.Employee;
import com.incubyte.salary.entity.EmploymentType;
import com.incubyte.salary.exception.DuplicateResourceException;
import com.incubyte.salary.exception.ResourceNotFoundException;
import com.incubyte.salary.mapper.EmployeeMapper;
import com.incubyte.salary.repository.EmployeeRepository;
import com.incubyte.salary.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    private final EmployeeMapper employeeMapper = new EmployeeMapper();

    private EmployeeServiceImpl employeeService;

    private Employee sampleEmployee;
    private EmployeeRequest sampleRequest;

    @BeforeEach
    void setUp() {
        employeeService = new EmployeeServiceImpl(employeeRepository, employeeMapper);

        sampleEmployee = Employee.builder()
                .id(1L)
                .employeeCode("ACME00001")
                .firstName("Ada")
                .lastName("Lovelace")
                .email("ada.lovelace@acme-corp.example")
                .department("Engineering")
                .designation("Senior Software Engineer")
                .country("United Kingdom")
                .currency("GBP")
                .baseSalary(BigDecimal.valueOf(95000))
                .annualBonus(BigDecimal.valueOf(9500))
                .employmentType(EmploymentType.FULL_TIME)
                .managerName("Charles Babbage")
                .dateJoined(LocalDate.of(2021, 3, 15))
                .build();

        sampleRequest = new EmployeeRequest(
                "ACME00001", "Ada", "Lovelace", "ada.lovelace@acme-corp.example",
                "Engineering", "Senior Software Engineer", "United Kingdom", "GBP",
                BigDecimal.valueOf(95000), BigDecimal.valueOf(9500),
                EmploymentType.FULL_TIME, "Charles Babbage", LocalDate.of(2021, 3, 15)
        );
    }

    @Test
    void getById_returnsMappedResponse_whenEmployeeExists() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(sampleEmployee));

        EmployeeResponse response = employeeService.getById(1L);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.firstName()).isEqualTo("Ada");
        assertThat(response.baseSalary()).isEqualByComparingTo("95000");
    }

    @Test
    void getById_throwsResourceNotFound_whenEmployeeMissing() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void create_savesAndReturnsEmployee_whenNoDuplicates() {
        when(employeeRepository.findAll(any(Specification.class))).thenReturn(List.of());
        when(employeeRepository.save(any(Employee.class))).thenReturn(sampleEmployee);

        EmployeeResponse response = employeeService.create(sampleRequest);

        assertThat(response.employeeCode()).isEqualTo("ACME00001");
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void create_throwsDuplicateResourceException_whenEmailAlreadyExists() {
        when(employeeRepository.findAll(any(Specification.class))).thenReturn(List.of(sampleEmployee));

        assertThatThrownBy(() -> employeeService.create(sampleRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining(sampleRequest.email());

        verify(employeeRepository, never()).save(any());
    }

    @Test
    void update_appliesChanges_whenEmployeeExists() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(sampleEmployee));
        when(employeeRepository.findAll(any(Specification.class))).thenReturn(List.of());
        when(employeeRepository.save(any(Employee.class))).thenAnswer(inv -> inv.getArgument(0));

        EmployeeRequest raiseRequest = new EmployeeRequest(
                "ACME00001", "Ada", "Lovelace", "ada.lovelace@acme-corp.example",
                "Engineering", "Principal Software Engineer", "United Kingdom", "GBP",
                BigDecimal.valueOf(120000), BigDecimal.valueOf(12000),
                EmploymentType.FULL_TIME, "Charles Babbage", LocalDate.of(2021, 3, 15)
        );

        EmployeeResponse response = employeeService.update(1L, raiseRequest);

        assertThat(response.designation()).isEqualTo("Principal Software Engineer");
        assertThat(response.baseSalary()).isEqualByComparingTo("120000");
    }

    @Test
    void delete_removesEmployee_whenExists() {
        when(employeeRepository.existsById(1L)).thenReturn(true);

        employeeService.delete(1L);

        verify(employeeRepository).deleteById(1L);
    }

    @Test
    void delete_throwsResourceNotFound_whenEmployeeMissing() {
        when(employeeRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> employeeService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(employeeRepository, never()).deleteById(any());
    }

    @Test
    void search_returnsPagedResponse_fromRepositoryPage() {
        Pageable pageable = PageRequest.of(0, 25);
        when(employeeRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(sampleEmployee), pageable, 1));

        PagedResponse<EmployeeResponse> result = employeeService.search(
                "Ada", null, null, null, null, null, null, pageable);

        assertThat(result.totalElements()).isEqualTo(1);
        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).firstName()).isEqualTo("Ada");
    }
}
