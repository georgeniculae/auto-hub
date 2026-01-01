package com.autohub.agency.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.autohub.agency.entity.RentalOffice;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@ActiveProfiles("test")
@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RentalOfficeRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:latest");

    @Autowired
    private RentalOfficeRepository rentalOfficeRepository;

    @Test
    void checkIfConnectionEstablished() {
        assertTrue(postgres.isCreated());
    }

    @Test
    @Transactional(readOnly = true)
    void findAllaRentalOfficeTest_success() {
        try (Stream<RentalOffice> rentalOfficeStream = rentalOfficeRepository.findAllRentalOffices()) {
            List<RentalOffice> rentalOffices = rentalOfficeStream.toList();
            assertEquals(2, rentalOffices.size());
        }
    }

    @Test
    @Transactional(readOnly = true)
    void findByFilterTest_success() {
        try (Stream<RentalOffice> rentalOfficeStream = rentalOfficeRepository.findRentalOfficeByFilter("Rental Office")) {
            List<RentalOffice> rentalOffices = rentalOfficeStream.toList();
            assertEquals(2, rentalOffices.size());
        }
    }

}
