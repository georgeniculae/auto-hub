package com.autohub.dto.expense;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record InvoiceReprocessRequest(
        @NotNull
        Long invoiceId,

        @NotNull
        Long carId,

        Long receptionistEmployeeId,

        @NotNull
        Long returnRentalOfficeId,

        @NotNull
        Long bookingId,

        LocalDate carReturnDate,

        @NotNull
        Boolean isVehicleDamaged,

        BigDecimal damageCost,

        BigDecimal additionalPayment,

        BigDecimal totalAmount,

        String comments
) {

    @Override
    public String toString() {
        return "InvoiceReprocessRequest{" +
                "invoiceId='" + invoiceId + '\'' +
                ", previousCarId='" + carId + '\'' +
                ", receptionistEmployeeId='" + receptionistEmployeeId + '\'' +
                ", returnRentalOfficeId='" + returnRentalOfficeId + '\'' +
                ", bookingId='" + bookingId + '\'' +
                ", carReturnDate=" + carReturnDate +
                ", isVehicleDamaged=" + isVehicleDamaged +
                ", damageCost=" + damageCost +
                ", additionalPayment=" + additionalPayment +
                ", totalAmount=" + totalAmount +
                ", comments='" + comments + '\'' +
                '}';
    }

}