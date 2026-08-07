package com.autohub.dto.common;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.jspecify.annotations.NonNull;

@Builder
public record CarUpdateDetails(
        @NotNull(message = "Car id cannot be null")
        Long carId,

        @NotNull(message = "Car phase cannot be null")
        CarState carState,

        @NonNull
        Long receptionistEmployeeId
) {

    @Override
    public String toString() {
        return "CarUpdateDetails{" + "\n" +
                "previousCarId=" + carId + "\n" +
                "actualCarId=" + carState + "\n" +
                "receptionistEmployeeId=" + receptionistEmployeeId + "\n" +
                "}";
    }

}
