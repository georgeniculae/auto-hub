package com.autohub.agency.controller;

import com.autohub.agency.service.RentalOfficeService;
import com.autohub.dto.agency.RentalOfficeRequest;
import com.autohub.dto.agency.RentalOfficeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/rental-offices")
public class RentalOfficeController {

    private final RentalOfficeService rentalOfficeService;

    @GetMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<List<RentalOfficeResponse>> findAllRentalOffices() {
        List<RentalOfficeResponse> allRentalOffices = rentalOfficeService.findAllRentalOffices();

        return ResponseEntity.ok(allRentalOffices);
    }

    @GetMapping(path = "/{id}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<RentalOfficeResponse> findRentalOfficeById(@PathVariable Long id) {
        RentalOfficeResponse rentalOfficeResponse = rentalOfficeService.findRentalOfficeById(id);

        return ResponseEntity.ok(rentalOfficeResponse);
    }

    @GetMapping(path = "/filter/{filter}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<List<RentalOfficeResponse>> findRentalOfficesByFilter(@PathVariable String filter) {
        List<RentalOfficeResponse> rentalOfficeResponses = rentalOfficeService.findRentalOfficeByFilter(filter);

        return ResponseEntity.ok(rentalOfficeResponses);
    }

    @GetMapping(path = "/count")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Long> countRentalOffices() {
        Long numberOfRentalOffices = rentalOfficeService.countRentalOffices();

        return ResponseEntity.ok(numberOfRentalOffices);
    }

    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<RentalOfficeResponse> addRentalOffice(@RequestBody @Validated RentalOfficeRequest rentalOfficeRequest) {
        RentalOfficeResponse savedRentalOfficeResponse = rentalOfficeService.saveRentalOffice(rentalOfficeRequest);

        return ResponseEntity.ok(savedRentalOfficeResponse);
    }

    @PutMapping(path = "/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<RentalOfficeResponse> updateRentalOffice(
            @PathVariable Long id,
            @RequestBody @Validated RentalOfficeRequest rentalOfficeRequest
    ) {
        RentalOfficeResponse updatedRentalOfficeResponse = rentalOfficeService.updateRentalOffice(id, rentalOfficeRequest);

        return ResponseEntity.ok(updatedRentalOfficeResponse);
    }

    @DeleteMapping(path = "/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> deleteRentalOfficeById(@PathVariable Long id) {
        rentalOfficeService.deleteRentalOfficeById(id);

        return ResponseEntity.noContent().build();
    }

}
