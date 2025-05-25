package com.autohub.agency.service;

import com.autohub.agency.entity.Branch;
import com.autohub.agency.entity.Car;
import com.autohub.agency.mapper.CarMapper;
import com.autohub.agency.mapper.CarMapperImpl;
import com.autohub.agency.repository.CarRepository;
import com.autohub.agency.util.AssertionUtil;
import com.autohub.agency.util.TestUtil;
import com.autohub.dto.agency.CarRequest;
import com.autohub.dto.agency.CarResponse;
import com.autohub.dto.common.AvailableCarInfo;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {

    @InjectMocks
    private CarService carService;

    @Mock
    private CarRepository carRepository;

    @Mock
    private BranchService branchService;

    @Mock
    private ExcelParserService excelParserService;

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
        Branch branch = TestUtil.getResourceAsJson("/data/Branch.json", Branch.class);
        Car car = TestUtil.getResourceAsJson("/data/Car.json", Car.class);
        CarRequest carRequest = TestUtil.getResourceAsJson("/data/CarRequest.json", CarRequest.class);

        MockMultipartFile image =
                new MockMultipartFile("car", "car.jpg", MediaType.TEXT_PLAIN_VALUE, "car".getBytes());

        when(branchService.findEntityById(anyLong())).thenReturn(branch);
        when(carRepository.save(any(Car.class))).thenReturn(car);

        CarResponse savedCarResponse = carService.saveCar(carRequest, image);
        AssertionUtil.assertCarResponse(car, savedCarResponse);
    }

    @Test
    void updateCarTest_success() {
        Branch branch = TestUtil.getResourceAsJson("/data/Branch.json", Branch.class);
        Car car = TestUtil.getResourceAsJson("/data/Car.json", Car.class);
        CarRequest carRequest = TestUtil.getResourceAsJson("/data/CarRequest.json", CarRequest.class);

        MockMultipartFile image =
                new MockMultipartFile("car", "car.jpg", MediaType.TEXT_PLAIN_VALUE, "car".getBytes());

        when(carRepository.findById(anyLong())).thenReturn(Optional.of(car));
        when(branchService.findEntityById(anyLong())).thenReturn(branch);
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

}
