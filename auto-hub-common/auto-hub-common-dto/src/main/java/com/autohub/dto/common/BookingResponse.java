package com.autohub.dto.common;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record BookingResponse(
        Long id,

        @NotNull(message = "Date of booking cannot be null")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate dateOfBooking,

        BookingState status,

        @NotEmpty(message = "Username cannot be empty")
        String customerUsername,

        @NotEmpty(message = "Email cannot be empty")
        String customerEmail,

        @NotNull(message = "Car id cannot be null")
        Long carId,

        @NotNull(message = "Date from cannot be null")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate dateFrom,

        @NotNull(message = "Date to cannot be blank")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate dateTo,

        BigDecimal rentalCarPrice,

        @NotNull(message = "Pickup rental office id cannot be null")
        Long pickupRentalOfficeId,

        Long returnRentalOfficeId
) {

    @Override
    public String toString() {
        return "BookingResponse{" + "\n" +
                "id=" + id + "\n" +
                "dateOfBooking=" + dateOfBooking + "\n" +
                "status=" + status + "\n" +
                "customerUsername='" + customerUsername + "\n" +
                "previousCarId=" + carId + "\n" +
                "dateFrom=" + dateFrom + "\n" +
                "dateTo=" + dateTo + "\n" +
                "rentalCarPrice=" + rentalCarPrice + "\n" +
                "pickupRentalOfficeId=" + pickupRentalOfficeId + "\n" +
                "returnRentalOfficeId=" + returnRentalOfficeId + "\n" +
                "}";
    }

}
