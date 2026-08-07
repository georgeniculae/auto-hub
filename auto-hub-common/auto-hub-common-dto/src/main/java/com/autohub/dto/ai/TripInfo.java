package com.autohub.dto.ai;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;

@Builder(toBuilder = true)
public record TripInfo(
        @NotEmpty(message = "Start location cannot be empty")
        String startLocation,

        @NotEmpty
        String destination,

        @NotNull
        Integer peopleCount,

        @NotEmpty
        String tripKind,

        @NotNull
        LocalDate tripDate
) {
}
