package com.autohub.booking.controller;

import com.autohub.booking.service.BookingService;
import com.autohub.dto.booking.BookingRequest;
import com.autohub.dto.common.BookingResponse;
import com.autohub.lib.aspect.LogActivity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping(path = "/list")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<List<BookingResponse>> findAllBookings() {
        List<BookingResponse> bookingResponses = bookingService.findAllBookings();

        return ResponseEntity.ok(bookingResponses);
    }

    @GetMapping(path = "/{id}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<BookingResponse> findBookingById(@PathVariable Long id) {
        BookingResponse bookingResponse = bookingService.findBookingById(id);

        return ResponseEntity.ok(bookingResponse);
    }

    @GetMapping(path = "/count")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<Long> countBookings() {
        Long numberOfBookings = bookingService.countBookings();

        return ResponseEntity.ok(numberOfBookings);
    }

    @GetMapping(path = "/count-by-current-user")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<Long> countByLoggedInUser() {
        Long numberOfBookings = bookingService.countByLoggedInUser();

        return ResponseEntity.ok(numberOfBookings);
    }

    @GetMapping(path = "/current-date")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<LocalDate> getCurrentDate() {
        LocalDate currentDate = bookingService.getCurrentDate();

        return ResponseEntity.ok(currentDate);
    }

    @GetMapping(path = "/by-logged-in-user")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<List<BookingResponse>> getBookingsByLoggedInUser() {
        List<BookingResponse> bookings = bookingService.findBookingsByLoggedInUser();

        return ResponseEntity.ok(bookings);
    }

    @PostMapping("/new")
    @PreAuthorize("hasRole('user')")
    @LogActivity(
            sentParameters = "bookingRequest",
            activityDescription = "Booking add"
    )
    public ResponseEntity<BookingResponse> addBooking(@RequestBody @Validated BookingRequest bookingRequest) {
        BookingResponse saveBookingResponse = bookingService.saveBooking(bookingRequest);

        return ResponseEntity.accepted().body(saveBookingResponse);
    }

    @PutMapping(path = "/{id}")
    @PreAuthorize("hasRole('user')")
    @LogActivity(
            sentParameters = "bookingRequest",
            activityDescription = "Booking update"
    )
    public ResponseEntity<BookingResponse> updateBooking(
            @PathVariable Long id,
            @RequestBody @Validated BookingRequest bookingRequest
    ) {
        BookingResponse updatedBookingResponse = bookingService.updateBooking(id, bookingRequest);

        return ResponseEntity.accepted().body(updatedBookingResponse);
    }

}
