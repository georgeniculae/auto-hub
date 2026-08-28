package com.autohub.agency.repository;

import com.autohub.agency.entity.RentalOffice;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RentalOfficeRepositoryTest extends AbstractRepositoryTest {

    @Test
    void findAllaRentalOfficeTest_success() {
        try (Stream<RentalOffice> rentalOfficeStream = rentalOfficeRepository.findAllRentalOffices()) {
            List<RentalOffice> rentalOffices = rentalOfficeStream.toList();
            assertEquals(2, rentalOffices.size());
        }
    }

    @Test
    void findByFilterTest_success() {
        try (Stream<RentalOffice> rentalOfficeStream = rentalOfficeRepository.findRentalOfficeByFilter("Rental Office")) {
            List<RentalOffice> rentalOffices = rentalOfficeStream.toList();
            assertEquals(2, rentalOffices.size());
        }
    }

}
