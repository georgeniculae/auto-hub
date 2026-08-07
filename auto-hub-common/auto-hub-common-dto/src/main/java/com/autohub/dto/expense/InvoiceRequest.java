package com.autohub.dto.expense;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record InvoiceRequest(
        @NotNull(message = "Receptionist employee id cannot be null")
        Long receptionistEmployeeId,

        @NotNull(message = "Return rental office id cannot be null")
        Long returnRentalOfficeId,

        @NotNull(message = "Booking id cannot be null")
        Long bookingId,

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate carReturnDate,

        @NotNull
        Boolean isVehicleDamaged,

        BigDecimal damageCost,

        BigDecimal additionalPayment,

        String comments
) {

    @Override
    public String toString() {
        return "InvoiceRequest{" + "\n" +
                "receptionistEmployeeId=" + receptionistEmployeeId + "\n" +
                "returnRentalOfficeId=" + returnRentalOfficeId + "\n" +
                "bookingId=" + bookingId + "\n" +
                "carReturnDate=" + carReturnDate + "\n" +
                "isVehicleDamaged=" + isVehicleDamaged + "\n" +
                "damageCost=" + damageCost + "\n" +
                "additionalPayment=" + additionalPayment + "\n" +
                "comments='" + comments + "\n" +
                "}";
    }

}
