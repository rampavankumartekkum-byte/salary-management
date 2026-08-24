package com.incubyte.salary.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.incubyte.salary.dto.EmployeeRequest;
import com.incubyte.salary.dto.EmployeeResponse;
import com.incubyte.salary.dto.PagedResponse;
import com.incubyte.salary.entity.EmploymentType;
import com.incubyte.salary.exception.ResourceNotFoundException;
import com.incubyte.salary.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmployeeService employeeService;

    private EmployeeResponse sampleResponse() {
        return EmployeeResponse.builder()
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
    }

    @Test
    void getById_returns200AndEmployee_whenFound() throws Exception {
        when(employeeService.getById(1L)).thenReturn(sampleResponse());

        mockMvc.perform(get("/api/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Ada"))
                .andExpect(jsonPath("$.employeeCode").value("ACME00001"));
    }

    @Test
    void getById_returns404_whenNotFound() throws Exception {
        when(employeeService.getById(999L)).thenThrow(new ResourceNotFoundException("Employee not found with id: 999"));

        mockMvc.perform(get("/api/employees/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Employee not found with id: 999"));
    }

    @Test
    void search_returns200AndPagedResults() throws Exception {
        PagedResponse<EmployeeResponse> paged = new PagedResponse<>(
                java.util.List.of(sampleResponse()), 0, 25, 1, 1, true);
        when(employeeService.search(any(), any(), any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(paged);

        mockMvc.perform(get("/api/employees").param("department", "Engineering"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].department").value("Engineering"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void create_returns201_whenRequestValid() throws Exception {
        when(employeeService.create(any(EmployeeRequest.class))).thenReturn(sampleResponse());

        EmployeeRequest request = new EmployeeRequest(
                "ACME00001", "Ada", "Lovelace", "ada.lovelace@acme-corp.example",
                "Engineering", "Senior Software Engineer", "United Kingdom", "GBP",
                BigDecimal.valueOf(95000), BigDecimal.valueOf(9500),
                EmploymentType.FULL_TIME, "Charles Babbage", LocalDate.of(2021, 3, 15)
        );

        mockMvc.perform(post("/api/employees")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/employees/1"));
    }

    @Test
    void create_returns400_whenRequestInvalid() throws Exception {
        // missing required fields (blank names, negative salary)
        String invalidJson = """
                {
                    "employeeCode": "",
                    "firstName": "",
                    "lastName": "Lovelace",
                    "email": "not-an-email",
                    "department": "Engineering",
                    "designation": "Engineer",
                    "country": "UK",
                    "currency": "GBP",
                    "baseSalary": -100,
                    "annualBonus": 0,
                    "employmentType": "FULL_TIME",
                    "dateJoined": "2021-03-15"
                }
                """;

        mockMvc.perform(post("/api/employees")
                        .contentType("application/json")
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.employeeCode").exists())
                .andExpect(jsonPath("$.fieldErrors.email").exists())
                .andExpect(jsonPath("$.fieldErrors.baseSalary").exists());
    }

    @Test
    void delete_returns204_whenSuccessful() throws Exception {
        mockMvc.perform(delete("/api/employees/1"))
                .andExpect(status().isNoContent());
    }
    @Test
    void export_returnsCsvForCurrentFilters() throws Exception {
        when(employeeService.exportCsv(eq("Ada"), eq("Engineering"), isNull(), isNull(), isNull(), isNull(), isNull()))
                .thenReturn("Employee Code,First Name\n\"ACME00001\",\"Ada\"\n");

        mockMvc.perform(get("/api/employees/export")
                        .param("q", "Ada")
                        .param("department", "Engineering"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=employees.csv"))
                .andExpect(content().contentTypeCompatibleWith("text/csv"))
                .andExpect(content().string("Employee Code,First Name\n\"ACME00001\",\"Ada\"\n"));
    }

}
