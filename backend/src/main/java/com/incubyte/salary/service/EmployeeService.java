package com.incubyte.salary.service;

import com.incubyte.salary.dto.EmployeeRequest;
import com.incubyte.salary.dto.EmployeeResponse;
import com.incubyte.salary.dto.PagedResponse;
import com.incubyte.salary.entity.EmploymentType;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface EmployeeService {

    PagedResponse<EmployeeResponse> search(String query,
                                            String department,
                                            String country,
                                            String designation,
                                            EmploymentType employmentType,
                                            BigDecimal minSalary,
                                            BigDecimal maxSalary,
                                            Pageable pageable);

    String exportCsv(String query,
                     String department,
                     String country,
                     String designation,
                     EmploymentType employmentType,
                     BigDecimal minSalary,
                     BigDecimal maxSalary);

    EmployeeResponse getById(Long id);

    EmployeeResponse create(EmployeeRequest request);

    EmployeeResponse update(Long id, EmployeeRequest request);

    void delete(Long id);
}
