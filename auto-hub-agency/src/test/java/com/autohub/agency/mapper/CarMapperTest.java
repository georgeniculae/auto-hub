package com.autohub.agency.mapper;

import com.autohub.agency.entity.Car;
import com.autohub.agency.entity.RentalOffice;
import com.autohub.agency.util.AssertionUtil;
import com.autohub.agency.util.TestUtil;
import com.autohub.dto.agency.CarRequest;
import com.autohub.dto.agency.CarResponse;
import com.autohub.dto.ai.AvailableCarDetails;
import com.autohub.dto.common.AvailableCarInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class CarMapperTest {

    private final CarMapper carMapper = new CarMapperImpl();

    @Test
    void mapEntityToDtoTest_success() {
        Car car = TestUtil.getResourceAsJson("/data/Car.json", Car.class);

        CarResponse carResponse = carMapper.mapEntityToDto(car);

        assertNotNull(carResponse);
        AssertionUtil.assertCarResponse(car, carResponse);
    }

    @Test
    void mapEntityToDtoTest_null() {
        assertNull(carMapper.mapEntityToDto(null));
    }

    @Test
    void getNewCarTest_success() {
        MockMultipartFile image =
                new MockMultipartFile("car", "car.jpg", MediaType.TEXT_PLAIN_VALUE, "car".getBytes());

        CarRequest carRequest = TestUtil.getResourceAsJson("/data/CarRequest.json", CarRequest.class);
        RentalOffice initialRentalOffice = TestUtil.getResourceAsJson("/data/RentalOffice.json", RentalOffice.class);
        RentalOffice actualRentalOffice = TestUtil.getResourceAsJson("/data/RentalOffice.json", RentalOffice.class);

        Car car = carMapper.getNewCar(carRequest, image, initialRentalOffice, actualRentalOffice);

        assertNotNull(car);
        AssertionUtil.assertCarRequest(car, carRequest);
    }

    @Test
    void getNewCarTest_null() {
        assertNull(carMapper.getNewCar(null, null, null, null));
    }

    @Test
    void mapToAvailableCarInfoTest_success() {
        Car car = TestUtil.getResourceAsJson("/data/Car.json", Car.class);

        AvailableCarInfo availableCarInfo = carMapper.mapToAvailableCarInfo(car);

        AssertionUtil.assertAvailableCarInfo(car, availableCarInfo);
    }

    @Test
    void mapToAvailableCarInfoTest_null() {
        assertNull(carMapper.mapToAvailableCarInfo(null));
    }

    @Test
    void mapEntityToAvailableCarDetailsTest_success() {
        Car car = TestUtil.getResourceAsJson("/data/Car.json", Car.class);

        AvailableCarDetails availableCarDetails = carMapper.mapEntityToAvailableCarDetails(car);

        assertNotNull(availableCarDetails);
        assertEquals(car.getId(), availableCarDetails.id());
        assertEquals(car.getMake(), availableCarDetails.make());
        assertEquals(car.getModel(), availableCarDetails.model());
        assertEquals(car.getBodyType().name(), availableCarDetails.bodyCategory().name());
        assertEquals(car.getYearOfProduction(), availableCarDetails.yearOfProduction());
        assertEquals(car.getColor(), availableCarDetails.color());
        assertEquals(car.getMileage(), availableCarDetails.mileage());
        assertEquals(car.getAmount(), availableCarDetails.amount());
        assertEquals(car.getActualRentalOffice().getCity(), availableCarDetails.carLocation());
    }

    @Test
    void mapEntityToAvailableCarDetailsTest_null() {
        assertNull(carMapper.mapEntityToAvailableCarDetails(null));
    }

}
