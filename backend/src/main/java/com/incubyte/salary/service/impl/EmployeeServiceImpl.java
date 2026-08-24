package com.incubyte.salary.service.impl;

import com.incubyte.salary.dto.EmployeeRequest;
import com.incubyte.salary.dto.EmployeeResponse;
import com.incubyte.salary.dto.PagedResponse;
import com.incubyte.salary.entity.Employee;
import com.incubyte.salary.entity.EmploymentType;
import com.incubyte.salary.exception.DuplicateResourceException;
import com.incubyte.salary.exception.ResourceNotFoundException;
import com.incubyte.salary.mapper.EmployeeMapper;
import com.incubyte.salary.repository.EmployeeRepository;
import com.incubyte.salary.repository.EmployeeSpecifications;
import com.incubyte.salary.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    @Override
    public PagedResponse<EmployeeResponse> search(String query,
                                                    String department,
                                                    String country,
                                                    String designation,
                                                    EmploymentType employmentType,
                                                    BigDecimal minSalary,
                                                    BigDecimal maxSalary,
                                                    Pageable pageable) {
        var spec = EmployeeSpecifications.withFilters(
                query, department, country, designation, employmentType, minSalary, maxSalary);

        Page<Employee> page = employeeRepository.findAll(spec, pageable);
        return PagedResponse.from(page.map(employeeMapper::toResponse));
    }

    @Override
    public String exportCsv(String query,
                            String department,
                            String country,
                            String designation,
                            EmploymentType employmentType,
                            BigDecimal minSalary,
                            BigDecimal maxSalary) {
        var spec = EmployeeSpecifications.withFilters(
                query, department, country, designation, employmentType, minSalary, maxSalary);

        List<Employee> employees = employeeRepository.findAll(spec, Sort.by(Sort.Direction.ASC, "id"));
        StringBuilder csv = new StringBuilder();
        csv.append("Employee Code,First Name,Last Name,Email,Department,Designation,Country,Currency,Base Salary,Bonus,Employment Type,Manager,Date Joined\n");

        for (Employee employee : employees) {
            csv.append(csv(employee.getEmployeeCode())).append(',')
                    .append(csv(employee.getFirstName())).append(',')
                    .append(csv(employee.getLastName())).append(',')
                    .append(csv(employee.getEmail())).append(',')
                    .append(csv(employee.getDepartment())).append(',')
                    .append(csv(employee.getDesignation())).append(',')
                    .append(csv(employee.getCountry())).append(',')
                    .append(csv(employee.getCurrency())).append(',')
                    .append(csv(employee.getBaseSalary())).append(',')
                    .append(csv(employee.getAnnualBonus())).append(',')
                    .append(csv(employee.getEmploymentType())).append(',')
                    .append(csv(employee.getManagerName())).append(',')
                    .append(csv(employee.getDateJoined())).append('\n');
        }
        return csv.toString();
    }

    private String csv(Object value) {
        if (value == null) return "";
        String text = String.valueOf(value);
        return "\"" + text.replace("\"", "\"\"") + "\"";
    }

    @Override
    public EmployeeResponse getById(Long id) {
        return employeeMapper.toResponse(findEmployeeOrThrow(id));
    }

    @Override
    @Transactional
    public EmployeeResponse create(EmployeeRequest request) {
        validateUnique(request, null);
        Employee saved = employeeRepository.save(employeeMapper.toEntity(request));
        return employeeMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public EmployeeResponse update(Long id, EmployeeRequest request) {
        Employee existing = findEmployeeOrThrow(id);
        validateUnique(request, id);
        employeeMapper.updateEntity(existing, request);
        return employeeMapper.toResponse(employeeRepository.save(existing));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employee not found with id: " + id);
        }
        employeeRepository.deleteById(id);
    }

    private Employee findEmployeeOrThrow(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
    }

    /**
     * Guards the two unique columns ourselves before hitting the DB constraint, so callers get a
     * clean 409 with a helpful message instead of a raw constraint-violation stack trace.
     */
    private void validateUnique(EmployeeRequest request, Long idBeingUpdated) {
        employeeRepository.findAll((root, cq, cb) -> cb.equal(root.get("email"), request.email()))
                .stream()
                .filter(e -> !e.getId().equals(idBeingUpdated))
                .findAny()
                .ifPresent(e -> {
                    throw new DuplicateResourceException("An employee with email " + request.email() + " already exists");
                });

        employeeRepository.findAll((root, cq, cb) -> cb.equal(root.get("employeeCode"), request.employeeCode()))
                .stream()
                .filter(e -> !e.getId().equals(idBeingUpdated))
                .findAny()
                .ifPresent(e -> {
                    throw new DuplicateResourceException(
                            "An employee with code " + request.employeeCode() + " already exists");
                });
    }
}
