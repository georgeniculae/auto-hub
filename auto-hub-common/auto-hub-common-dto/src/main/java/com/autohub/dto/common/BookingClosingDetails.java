package com.autohub.dto.common;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record BookingClosingDetails(
        @NotNull(message = "Booking id cannot be null")
        Long bookingId,

        @NotNull(message = "Return rental office id cannot be null")
        Long returnRentalOfficeId
) {

    @Override
    public String toString() {
        return "BookingClosingDetails{" + "\n" +
                "bookingId=" + bookingId + "\n" +
                "returnRentalOfficeId=" + returnRentalOfficeId + "\n" +
                "}";
    }

}
