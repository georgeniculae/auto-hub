package com.autohub.dto.booking;

import com.autohub.dto.common.BookingState;
import lombok.Builder;
import org.jspecify.annotations.NonNull;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record CreatedBookingReprocessRequest(
        Long id,

        @NonNull
        LocalDate dateOfBooking,

        BookingState status,

        String customerUsername,

        String customerEmail,

        @NonNull
        Long actualCarId,

        Long previousCarId,

        @NonNull
        LocalDate dateFrom,

        @NonNull
        LocalDate dateTo,

        BigDecimal rentalCarPrice,

        Long pickupRentalOfficeId,

        Long returnRentalOfficeId
) {
}
