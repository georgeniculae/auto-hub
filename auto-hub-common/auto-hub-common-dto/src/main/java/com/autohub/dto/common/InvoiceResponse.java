package com.autohub.dto.common;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record InvoiceResponse(
        Long id,

        @NotEmpty(message = "Username cannot be empty")
        String customerUsername,

        @NotEmpty(message = "Email cannot be empty")
        String customerEmail,

        @NotNull(message = "Car id cannot be null")
        Long carId,

        Long receptionistEmployeeId,

        Long returnRentalOfficeId,

        @NotNull(message = "Booking id cannot be null")
        Long bookingId,

        LocalDate carReturnDate,

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate dateTo,

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate dateFrom,

        Boolean isVehicleDamaged,

        BigDecimal damageCost,

        BigDecimal additionalPayment,

        BigDecimal totalAmount,

        BigDecimal rentalCarPrice,

        String comments
) {

    @Override
    public String toString() {
        return "InvoiceResponse{" +
                "id='" + id + '\'' +
                ", customerUsername='" + customerUsername + '\'' +
                ", customerEmail='" + customerEmail + '\'' +
                ", previousCarId='" + carId + '\'' +
                ", receptionistEmployeeId='" + receptionistEmployeeId + '\'' +
                ", returnRentalOfficeId='" + returnRentalOfficeId + '\'' +
                ", bookingId='" + bookingId + '\'' +
                ", carReturnDate=" + carReturnDate +
                ", dateTo=" + dateTo +
                ", dateFrom=" + dateFrom +
                ", isVehicleDamaged=" + isVehicleDamaged +
                ", damageCost=" + damageCost +
                ", additionalPayment=" + additionalPayment +
                ", totalAmount=" + totalAmount +
                ", rentalCarPrice=" + rentalCarPrice +
                ", comments='" + comments +
                '}';
    }

}
