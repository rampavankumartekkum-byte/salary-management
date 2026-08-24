package com.incubyte.salary.mapper;

import com.incubyte.salary.dto.EmployeeRequest;
import com.incubyte.salary.dto.EmployeeResponse;
import com.incubyte.salary.entity.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    public EmployeeResponse toResponse(Employee employee) {
        return EmployeeResponse.builder()
                .id(employee.getId())
                .employeeCode(employee.getEmployeeCode())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .department(employee.getDepartment())
                .designation(employee.getDesignation())
                .country(employee.getCountry())
                .currency(employee.getCurrency())
                .baseSalary(employee.getBaseSalary())
                .annualBonus(employee.getAnnualBonus())
                .employmentType(employee.getEmploymentType())
                .managerName(employee.getManagerName())
                .dateJoined(employee.getDateJoined())
                .build();
    }

    public Employee toEntity(EmployeeRequest request) {
        return Employee.builder()
                .employeeCode(request.employeeCode())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .department(request.department())
                .designation(request.designation())
                .country(request.country())
                .currency(request.currency())
                .baseSalary(request.baseSalary())
                .annualBonus(request.annualBonus())
                .employmentType(request.employmentType())
                .managerName(request.managerName())
                .dateJoined(request.dateJoined())
                .build();
    }

    /** Applies request fields onto an existing managed entity, in place, for updates. */
    public void updateEntity(Employee employee, EmployeeRequest request) {
        employee.setEmployeeCode(request.employeeCode());
        employee.setFirstName(request.firstName());
        employee.setLastName(request.lastName());
        employee.setEmail(request.email());
        employee.setDepartment(request.department());
        employee.setDesignation(request.designation());
        employee.setCountry(request.country());
        employee.setCurrency(request.currency());
        employee.setBaseSalary(request.baseSalary());
        employee.setAnnualBonus(request.annualBonus());
        employee.setEmploymentType(request.employmentType());
        employee.setManagerName(request.managerName());
        employee.setDateJoined(request.dateJoined());
    }
}
