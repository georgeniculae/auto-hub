package com.autohub.dto.agency;

import com.autohub.dto.common.CarState;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CarResponse(
        Long id,

        @NotEmpty(message = "Make cannot be empty")
        String make,

        @NotEmpty(message = "Model cannot be null")
        String model,

        @NotNull(message = "Body category cannot be null")
        BodyCategory bodyCategory,

        @NotNull(message = "Year of production cannot be null")
        Integer yearOfProduction,

        @NotNull(message = "Color cannot be null")
        String color,

        @NotNull(message = "Mileage cannot be null")
        Integer mileage,

        CarState carState,

        @NotNull(message = "Amount cannot be null")
        BigDecimal amount,

        @NotNull(message = "Initial rental office id cannot be null")
        Long initialRentalOfficeId,

        @NotNull(message = "Actual rental office id cannot be null")
        Long actualRentalOfficeId,

        @NotEmpty(message = "Car location cannot be empty")
        String carLocation
) {

    @Override
    public String toString() {
        return "CarResponse{" + "\n" +
                "id=" + id +
                "make='" + make + "\n" +
                "model='" + model + "\n" +
                "bodyCategory=" + bodyCategory + "\n" +
                "yearOfProduction=" + yearOfProduction + "\n" +
                "color='" + color + "\n" +
                "mileage=" + mileage + "\n" +
                "actualCarId=" + carState + "\n" +
                "amount=" + amount + "\n" +
                "initialRentalOfficeId=" + initialRentalOfficeId + "\n" +
                "actualRentalOfficeId=" + actualRentalOfficeId + "\n" +
                "carLocation=" + carLocation + "\n" +
                "}";
    }

}
