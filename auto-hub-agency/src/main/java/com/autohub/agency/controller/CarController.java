package com.autohub.agency.controller;

import com.autohub.agency.service.CarService;
import com.autohub.dto.agency.CarRequest;
import com.autohub.dto.agency.CarResponse;
import com.autohub.dto.common.AvailableCarInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/cars")
public class CarController {

    private final CarService carService;

    @GetMapping
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<List<CarResponse>> findAllCars() {
        List<CarResponse> carResponses = carService.findAllCars();

        return ResponseEntity.ok(carResponses);
    }

    @GetMapping(path = "/available")
    public ResponseEntity<List<CarResponse>> findAllAvailableCars() {
        List<CarResponse> carResponses = carService.findAllAvailableCars();

        return ResponseEntity.ok(carResponses);
    }

    @GetMapping(path = "/availability/location/{location}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<List<CarResponse>> findAllAvailableCarsByLocation(@PathVariable String location) {
        List<CarResponse> carResponses = carService.findAllAvailableCarsByLocation(location);

        return ResponseEntity.ok(carResponses);
    }

    @GetMapping(path = "/{id}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<CarResponse> findCarById(@PathVariable Long id) {
        CarResponse carResponse = carService.findCarById(id);

        return ResponseEntity.ok(carResponse);
    }

    @GetMapping(path = "/make/{make}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<List<CarResponse>> findCarsByMake(@PathVariable String make) {
        List<CarResponse> carResponses = carService.findCarsByMake(make);

        return ResponseEntity.ok(carResponses);
    }

    @GetMapping(path = "/filter/{filter}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<List<CarResponse>> findCarsByFilter(@PathVariable String filter) {
        List<CarResponse> carResponses = carService.findCarsByFilter(filter);

        return ResponseEntity.ok(carResponses);
    }

    @GetMapping(path = "/{id}/availability")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<AvailableCarInfo> findCarsByFilter(@PathVariable Long id) {
        AvailableCarInfo availableCarInfo = carService.findAvailableCar(id);

        return ResponseEntity.ok(availableCarInfo);
    }

    @GetMapping(path = "/{id}/image")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<byte[]> findCarImage(@PathVariable Long id) {
        byte[] carImage = carService.getCarImage(id);

        return ResponseEntity.ok(carImage);
    }

    @GetMapping(path = "/count")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<Long> countCars() {
        Long numberOfCars = carService.countCars();

        return ResponseEntity.ok(numberOfCars);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<CarResponse> addCar(@RequestPart @Validated CarRequest carRequest,
                                              @RequestPart MultipartFile image) {
        CarResponse savedCarResponse = carService.saveCar(carRequest, image);

        return ResponseEntity.ok(savedCarResponse);
    }

    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<List<CarResponse>> uploadCars(@RequestParam("file") MultipartFile file) {
        List<CarResponse> savedCarResponses = carService.uploadCars(file);

        return ResponseEntity.ok(savedCarResponses);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<CarResponse> updateCar(
            @PathVariable Long id,
            @RequestPart @Validated CarRequest carRequest,
            @RequestPart MultipartFile image
    ) {
        CarResponse updatedCarResponse = carService.updateCar(id, carRequest, image);

        return ResponseEntity.ok(updatedCarResponse);
    }

    @DeleteMapping(path = "/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> deleteCarById(@PathVariable Long id) {
        carService.deleteCarById(id);

        return ResponseEntity.noContent().build();
    }

}
