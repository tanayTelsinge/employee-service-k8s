package com.tanay.employee.service;

import com.tanay.employee.domain.Employee;
import com.tanay.employee.dto.EmployeeRequest;
import com.tanay.employee.exception.EmployeeNotFoundException;
import com.tanay.employee.mapper.EmployeeMapper;
import com.tanay.employee.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository, EmployeeMapper employeeMapper) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
    }


    public Page<Employee> getAllEmployees(Pageable pageable) {
        return employeeRepository.findAllByIsActiveTrue(pageable);
    }

    public Employee getEmployeeById(Long id) {
        return employeeRepository.findByIdAndIsActiveTrue(id).orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    public Employee updateEmployee(Long id, EmployeeRequest request) {
        Employee existing = getEmployeeById(id);
        employeeMapper.updateEntityFromRequest(request, existing);
        return employeeRepository.save(existing);
    }

    public Employee createEmployee(EmployeeRequest request) {
        return employeeRepository.save(employeeMapper.toEntity(request));
    }

    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findByIdAndIsActiveTrue(id).orElseThrow(() -> new EmployeeNotFoundException(id));
        employee.setIsActive(false);
        employeeRepository.save(employee);
    }
}
