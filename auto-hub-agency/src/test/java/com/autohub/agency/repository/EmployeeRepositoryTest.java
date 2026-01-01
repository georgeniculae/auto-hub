package com.autohub.agency.repository;

import com.autohub.agency.entity.Employee;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EmployeeRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:latest");

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    void checkIfConnectionEstablished() {
        assertTrue(postgres.isCreated());
    }

    @Test
    @Transactional(readOnly = true)
    void findAllEmployeesTest_success() {
        try (Stream<Employee> employeeStream = employeeRepository.findAllEmployee()) {
            List<Employee> employees = employeeStream.toList();
            assertEquals(4, employees.size());
        }
    }

    @Test
    @Transactional(readOnly = true)
    void findByFilterTest_success() {
        try (Stream<Employee> employeeStream = employeeRepository.findByFilter("manager")) {
            List<Employee> employees = employeeStream.toList();
            assertEquals(2, employees.size());
        }
    }

    @Test
    @Transactional(readOnly = true)
    void findAllEmployeesByBranchIdTest_success() {
        try (Stream<Employee> employeeStream = employeeRepository.findAllEmployeesByBranchId(1L)) {
            List<Employee> employees = employeeStream.toList();
            assertEquals(2, employees.size());
        }
    }

}
