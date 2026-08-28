package com.autohub.agency.repository;

import com.autohub.agency.entity.Employee;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EmployeeRepositoryTest extends AbstractRepositoryTest {

    @Test
    void findAllEmployeesTest_success() {
        try (Stream<Employee> employeeStream = employeeRepository.findAllEmployee()) {
            List<Employee> employees = employeeStream.toList();
            assertEquals(4, employees.size());
        }
    }

    @Test
    void findByFilterTest_success() {
        try (Stream<Employee> employeeStream = employeeRepository.findByFilter("manager")) {
            List<Employee> employees = employeeStream.toList();
            assertEquals(2, employees.size());
        }
    }

    @Test
    void findAllEmployeesByRentalOfficeIdTest_success() {
        try (Stream<Employee> employeeStream = employeeRepository.findAllEmployeesByRentalOfficeId(1L)) {
            List<Employee> employees = employeeStream.toList();
            assertEquals(2, employees.size());
        }
    }

}
