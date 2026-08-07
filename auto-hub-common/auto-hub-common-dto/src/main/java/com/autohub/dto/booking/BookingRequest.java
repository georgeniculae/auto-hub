package com.autohub.dto.booking;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Builder
public record BookingRequest(
        @NotNull(message = "Date of booking cannot be null")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate dateOfBooking,

        @NotNull(message = "Car id cannot be null")
        Long carId,

        @NotNull(message = "Date from cannot be null")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate dateFrom,

        @NotNull(message = "Date to cannot be blank")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate dateTo,

        @NotNull(message = "Pickup rental office id cannot be null")
        Long pickupRentalOfficeId
) {

    @Override
    public String toString() {
        return "BookingRequest{" + "\n" +
                "dateOfBooking=" + dateOfBooking + "\n" +
                "previousCarId=" + carId + "\n" +
                "dateFrom=" + dateFrom + "\n" +
                "dateTo=" + dateTo + "\n" +
                "pickupRentalOfficeId=" + pickupRentalOfficeId + "\n" +
                "}";
    }

}
