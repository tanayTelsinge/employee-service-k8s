package com.tanay.employee.repository;

import com.tanay.employee.domain.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByIdAndIsActiveTrue(Long id);

    Page<Employee> findAllByIsActiveTrue(Pageable pageable);
}
