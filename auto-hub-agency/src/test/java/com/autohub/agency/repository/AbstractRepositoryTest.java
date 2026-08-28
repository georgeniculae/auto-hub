package com.autohub.agency.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@Testcontainers(disabledWithoutDocker = true)
@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
abstract class AbstractRepositoryTest {

    @ServiceConnection
    static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:latest");

    @Autowired
    protected BranchRepository branchRepository;

    @Autowired
    protected CarRepository carRepository;

    @Autowired
    protected EmployeeRepository employeeRepository;

    @Autowired
    protected RentalOfficeRepository rentalOfficeRepository;

    static {
        POSTGRES.start();
    }

    @Test
    void checkIfConnectionEstablished() {
        assertTrue(POSTGRES.isRunning());
    }

}
