package com.autohub.agency.service;

import com.autohub.agency.entity.Car;
import com.autohub.agency.entity.Employee;
import com.autohub.agency.entity.RentalOffice;
import com.autohub.agency.mapper.CarMapper;
import com.autohub.agency.mapper.CarMapperImpl;
import com.autohub.agency.producer.CarAvailableProducerService;
import com.autohub.agency.repository.CarRepository;
import com.autohub.agency.util.AssertionUtil;
import com.autohub.agency.util.TestUtil;
import com.autohub.dto.agency.CarRequest;
import com.autohub.dto.agency.CarResponse;
import com.autohub.dto.ai.AvailableCarDetails;
import com.autohub.dto.common.AvailableCarInfo;
import com.autohub.dto.common.CarState;
import com.autohub.dto.common.CarStatusUpdate;
import com.autohub.dto.common.CarUpdateDetails;
import com.autohub.dto.common.UpdateCarsRequest;
import com.autohub.exception.AutoHubException;
import com.autohub.exception.AutoHubNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {

    @InjectMocks
    private CarService carService;

    @Mock
    private CarRepository carRepository;

    @Mock
    private RentalOfficeService rentalOfficeService;

    @Mock
    private ExcelParserService excelParserService;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private CarAvailableProducerService carAvailableProducerService;

    @Spy
    private ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    @Spy
    private CarMapper carMapper = new CarMapperImpl();

    @Test
    void findAllCarsTest_success() {
        Car car = TestUtil.getResourceAsJson("/data/Car.json", Car.class);

        when(carRepository.findAllCars()).thenReturn(Stream.of(car));

        List<CarResponse> carResponses = carService.findAllCars();
        AssertionUtil.assertCarResponse(car, carResponses.getFirst());
    }

    @Test
    void findCarsByFilterTest_success() {
        Car car = TestUtil.getResourceAsJson("/data/Car.json", Car.class);

        when(carRepository.findByFilter(anyString())).thenReturn(Stream.of(car));

        List<CarResponse> carResponses = carService.findCarsByFilter("Test");
        AssertionUtil.assertCarResponse(car, carResponses.getFirst());
    }

    @Test
    void findCarByIdTest_success() {
        Car car = TestUtil.getResourceAsJson("/data/Car.json", Car.class);

        when(carRepository.findById(anyLong())).thenReturn(Optional.of(car));

        CarResponse actualCarResponse = carService.findCarById(1L);

        assertNotNull(actualCarResponse);
        verify(carMapper).mapEntityToDto(any(Car.class));
    }

    @Test
    void findCarByIdTest_errorOnFindingById() {
        when(carRepository.findById(anyLong())).thenReturn(Optional.empty());

        AutoHubNotFoundException autoHubNotFoundException =
                assertThrows(AutoHubNotFoundException.class, () -> carService.findCarById(1L));

        assertNotNull(autoHubNotFoundException);
    }

    @Test
    void findCarsByMakeTest_success() {
        Car car = TestUtil.getResourceAsJson("/data/Car.json", Car.class);

        when(carRepository.findCarsByMakeIgnoreCase(anyString())).thenReturn(Stream.of(car));

        List<CarResponse> carResponses = carService.findCarsByMake("Test");

        assertNotNull(carResponses);
        verify(carMapper).mapEntityToDto(any(Car.class));
    }

    @Test
    void saveCarTest_success() {
        RentalOffice rentalOffice = TestUtil.getResourceAsJson("/data/RentalOffice.json", RentalOffice.class);
        Car car = TestUtil.getResourceAsJson("/data/Car.json", Car.class);
        CarRequest carRequest = TestUtil.getResourceAsJson("/data/CarRequest.json", CarRequest.class);

        MockMultipartFile image =
                new MockMultipartFile("car", "car.jpg", MediaType.TEXT_PLAIN_VALUE, "car".getBytes());

        when(rentalOfficeService.findEntityById(anyLong())).thenReturn(rentalOffice);
        when(carRepository.save(any(Car.class))).thenReturn(car);

        CarResponse savedCarResponse = carService.saveCar(carRequest, image);
        AssertionUtil.assertCarResponse(car, savedCarResponse);
    }

    @Test
    void updateCarTest_success() {
        RentalOffice rentalOffice = TestUtil.getResourceAsJson("/data/RentalOffice.json", RentalOffice.class);
        Car car = TestUtil.getResourceAsJson("/data/Car.json", Car.class);
        CarRequest carRequest = TestUtil.getResourceAsJson("/data/CarRequest.json", CarRequest.class);

        MockMultipartFile image =
                new MockMultipartFile("car", "car.jpg", MediaType.TEXT_PLAIN_VALUE, "car".getBytes());

        when(carRepository.findById(anyLong())).thenReturn(Optional.of(car));
        when(rentalOfficeService.findEntityById(anyLong())).thenReturn(rentalOffice);
        when(carRepository.save(any(Car.class))).thenReturn(car);

        CarResponse updatedCarResponse = carService.updateCar(1L, carRequest, image);
        assertNotNull(updatedCarResponse);
    }

    @Test
    void uploadCarsTest_success() throws IOException {
        File excelFile = new File("src/test/resources/file/Cars.xlsx");

        InputStream stream = new FileInputStream(excelFile);

        MockMultipartFile file =
                new MockMultipartFile("file", excelFile.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, stream);

        Car car = TestUtil.getResourceAsJson("/data/Car.json", Car.class);

        when(excelParserService.extractDataFromExcel(any(MultipartFile.class))).thenReturn(List.of(car));
        when(carRepository.saveAll(anyList())).thenReturn(List.of(car));

        List<CarResponse> carResponses = carService.uploadCars(file);
        AssertionUtil.assertCarResponse(car, carResponses.getFirst());
    }

    @Test
    void uploadCarsTest_errorWhileSavingCars() throws IOException {
        File excelFile = new File("src/test/resources/file/Cars.xlsx");

        InputStream stream = new FileInputStream(excelFile);

        MockMultipartFile file =
                new MockMultipartFile("file", excelFile.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, stream);

        when(carRepository.saveAll(anyList())).thenThrow(new AutoHubException("error"));

        AutoHubException autoHubException =
                assertThrows(AutoHubException.class, () -> carService.uploadCars(file));

        assertNotNull(autoHubException);
    }

    @Test
    void updateCarsStatusTest_success() {
        Car car = TestUtil.getResourceAsJson("/data/Car.json", Car.class);

        UpdateCarsRequest updateCarsRequest =
                TestUtil.getResourceAsJson("/data/UpdateCarsRequest.json", UpdateCarsRequest.class);

        when(carRepository.findAllById(anyList())).thenReturn(List.of(car));
        when(carRepository.saveAll(anyList())).thenReturn(List.of(car));

        List<CarResponse> carResponses = carService.updateCarsStatus(updateCarsRequest);
        AssertionUtil.assertCarResponse(car, carResponses.getFirst());
    }

    @Test
    void findAvailableCarTest_success() {
        Car car = TestUtil.getResourceAsJson("/data/Car.json", Car.class);

        when(carRepository.findById(anyLong())).thenReturn(Optional.ofNullable(car));

        AvailableCarInfo availableCarInfo = carService.findAvailableCar(1L);
        AssertionUtil.assertAvailableCarInfo(Objects.requireNonNull(car), availableCarInfo);
    }

    @Test
    void updateCarStatusTest_publishesWhenCarBecomesAvailable() {
        Car car = TestUtil.getResourceAsJson("/data/Car.json", Car.class);
        CarStatusUpdate carStatusUpdate = CarStatusUpdate.builder()
                .carId(1L)
                .carState(CarState.AVAILABLE)
                .build();

        when(carRepository.findById(anyLong())).thenReturn(Optional.of(car));
        when(carRepository.save(any(Car.class))).thenReturn(car);

        carService.updateCarStatus(carStatusUpdate);

        verify(carAvailableProducerService).sendCarAvailable(any(AvailableCarDetails.class));
    }

    @Test
    void updateCarStatusTest_doesNotPublishWhenCarBecomesUnavailable() {
        Car car = TestUtil.getResourceAsJson("/data/Car.json", Car.class);
        CarStatusUpdate carStatusUpdate = CarStatusUpdate.builder()
                .carId(1L)
                .carState(CarState.NOT_AVAILABLE)
                .build();

        when(carRepository.findById(anyLong())).thenReturn(Optional.of(car));
        when(carRepository.save(any(Car.class))).thenReturn(car);

        carService.updateCarStatus(carStatusUpdate);

        verify(carAvailableProducerService, never()).sendCarAvailable(any(AvailableCarDetails.class));
    }

    @Test
    void updateCarWhenBookingIsClosedTest_publishesReturnedCar() {
        Car car = TestUtil.getResourceAsJson("/data/Car.json", Car.class);
        Employee employee = TestUtil.getResourceAsJson("/data/Employee.json", Employee.class);
        CarUpdateDetails carUpdateDetails = CarUpdateDetails.builder()
                .carId(1L)
                .carState(CarState.AVAILABLE)
                .receptionistEmployeeId(1L)
                .build();

        when(carRepository.findById(anyLong())).thenReturn(Optional.of(car));
        when(employeeService.findEntityById(anyLong())).thenReturn(employee);
        when(carRepository.save(any(Car.class))).thenReturn(car);

        carService.updateCarWhenBookingIsClosed(carUpdateDetails);

        verify(carAvailableProducerService).sendCarAvailable(any(AvailableCarDetails.class));
    }

}
