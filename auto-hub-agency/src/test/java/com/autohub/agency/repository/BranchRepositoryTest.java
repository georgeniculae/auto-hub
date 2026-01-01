package com.autohub.agency.repository;

import com.autohub.agency.entity.Branch;
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
class BranchRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:latest");

    @Autowired
    private BranchRepository branchRepository;

    @Test
    void checkIfConnectionEstablished() {
        assertTrue(postgres.isCreated());
    }

    @Test
    @Transactional(readOnly = true)
    void findAllBranchesTest_success() {
        try (Stream<Branch> branchStream = branchRepository.findAllBranches()) {
            List<Branch> branches = branchStream.toList();
            assertEquals(2, branches.size());
        }
    }

    @Test
    @Transactional(readOnly = true)
    void findByFilterTest_success() {
        try (Stream<Branch> branchStream = branchRepository.findByFilter("Branch")) {
            List<Branch> branches = branchStream.toList();
            assertEquals(2, branches.size());
        }
    }

}
