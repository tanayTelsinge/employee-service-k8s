package com.tanay.employee.controller;

import com.tanay.employee.dto.EmployeeRequest;
import com.tanay.employee.dto.EmployeeResponse;
import com.tanay.employee.mapper.EmployeeMapper;
import com.tanay.employee.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;
    private final EmployeeMapper mapper;


    @GetMapping
    public ResponseEntity<Page<EmployeeResponse>> getAllEmployees(Pageable pageable) {
        Page<EmployeeResponse> employeeResponsePage = employeeService.getAllEmployees(pageable).map(mapper::toResponse);
        return ResponseEntity.ok(employeeResponsePage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable Long id) {
        EmployeeResponse response = mapper.toResponse(employeeService.getEmployeeById(id));
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<EmployeeResponse> createEmployee(@Valid  @RequestBody EmployeeRequest request) {
        EmployeeResponse response = mapper.toResponse(employeeService.createEmployee(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> updateEmployee(@PathVariable Long id, @Valid @RequestBody EmployeeRequest request) {
        EmployeeResponse response = mapper.toResponse(employeeService.updateEmployee(id, request));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

}
