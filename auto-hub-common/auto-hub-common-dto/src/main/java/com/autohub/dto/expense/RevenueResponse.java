package com.autohub.dto.expense;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record RevenueResponse(
        Long id,

        @NotNull(message = "Date of revenue cannot be null")
        LocalDate dateOfRevenue,

        @NotNull(message = "Amount from booking cannot be null")
        BigDecimal amountFromBooking
) {

    @Override
    public String toString() {
        return "RevenueRequest{" + "\n" +
                "id=" + id + "\n" +
                "dateOfRevenue=" + dateOfRevenue + "\n" +
                "amountFromBooking=" + amountFromBooking + "\n" +
                "}";
    }
}
