package com.tanay.employee.mapper;

import com.tanay.employee.dto.EmployeeRequest;
import com.tanay.employee.dto.EmployeeResponse;
import com.tanay.employee.domain.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    Employee toEntity(EmployeeRequest request);

    EmployeeResponse toResponse(Employee employee);

    void updateEntityFromRequest(EmployeeRequest request, @MappingTarget Employee employee);
}