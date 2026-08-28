package com.autohub.agency.repository;

import com.autohub.agency.entity.Car;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CarRepositoryTest extends AbstractRepositoryTest {

    @Test
    void findByIdTest_success() {
        Optional<Car> optionalCar = carRepository.findById(1L);
        assertTrue(optionalCar.isPresent());
    }

    @Test
    void findAllCarsCarsTest_success() {
        try (Stream<Car> carsStream = carRepository.findAllCars()) {
            List<Car> cars = carsStream.toList();
            assertEquals(2, cars.size());
        }
    }

    @Test
    void findByFilterTest_success() {
        try (Stream<Car> carStream = carRepository.findByFilter("Golf")) {
            List<Car> cars = carStream.toList();
            assertEquals(1, cars.size());
        }
    }

    @Test
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

    @Test
    void findAllCarsTest_actualRentalOfficeIsInitialized() {
        try (Stream<Car> carsStream = carRepository.findAllCars()) {
            List<Car> cars = carsStream.toList();

            assertFalse(cars.isEmpty(), "seed-ul trebuie sa contina masini, altfel testul nu verifica nimic");
            assertTrue(
                    cars.stream().allMatch(car -> car.getActualRentalOffice() != null
                            && Hibernate.isInitialized(car.getActualRentalOffice())),
                    "actualRentalOffice trebuie adus de join-ul explicit, altfel CarMapper.carLocation declanseaza N+1"
            );
        }
    }

    @Test
    void findAllAvailableCarsByLocationTest_success() {
        try (Stream<Car> carStream = carRepository.findAllAvailableCarsByLocation("Ploiesti")) {
            List<Car> cars = carStream.toList();

            assertEquals(1, cars.size());
            assertEquals("Ploiesti", cars.getFirst().getActualRentalOffice().getCity());
        }
    }

    @Test
    void findAllAvailableCarsByLocationTest_unknownLocation() {
        try (Stream<Car> carStream = carRepository.findAllAvailableCarsByLocation("Cluj")) {
            assertTrue(carStream.toList().isEmpty());
        }
    }

}
