package com.autohub.agency.repository;

import com.autohub.agency.entity.Car;
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
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CarRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:latest");

    @Autowired
    private CarRepository carRepository;

    @Test
    void checkIfConnectionEstablished() {
        assertTrue(postgres.isCreated());
    }

    @Test
    void findByIdTest_success() {
        Optional<Car> optionalCar = carRepository.findById(1L);
        assertTrue(optionalCar.isPresent());
    }

    @Test
    @Transactional(readOnly = true)
    void findAllCarsCarsTest_success() {
        try (Stream<Car> carsStream = carRepository.findAllCars()) {
            List<Car> cars = carsStream.toList();
            assertEquals(2, cars.size());
        }
    }

    @Test
    @Transactional(readOnly = true)
    void findByFilterTest_success() {
        try (Stream<Car> carStream = carRepository.findByFilter("Golf")) {
            List<Car> cars = carStream.toList();
            assertEquals(1, cars.size());
        }
    }

    @Test
    @Transactional(readOnly = true)
    void findCarsByMakeIgnoreCaseTest_success() {
        try (Stream<Car> carStream = carRepository.findCarsByMakeIgnoreCase("Volkswagen")) {
            List<Car> cars = carStream.toList();
            assertEquals(1, cars.size());
        }
    }

    @Test
    void findImageByCarIdTest_success() {
        Optional<Car> optionalCar = carRepository.findImageByCarId(1L);
        assertTrue(optionalCar.isPresent());
    }

}
