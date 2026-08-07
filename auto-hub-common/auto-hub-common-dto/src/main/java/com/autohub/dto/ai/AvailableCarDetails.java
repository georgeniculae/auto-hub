package com.autohub.dto.ai;

import com.autohub.dto.agency.BodyCategory;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AvailableCarDetails(
        Long id,

        @NotEmpty(message = "Make cannot be empty")
        String make,

        @NotEmpty(message = "Model cannot be empty")
        String model,

        @NotNull(message = "Body category cannot be null")
        BodyCategory bodyCategory,

        @NotNull(message = "Year of production cannot be null")
        Integer yearOfProduction,

        @NotEmpty(message = "Color cannot be empty")
        String color,

        @NotNull(message = "Mileage cannot be null")
        Integer mileage,

        @NotNull(message = "Amount cannot be null")
        BigDecimal amount,

        @NotEmpty(message = "Car location cannot be empty")
        String carLocation
) {

    @Override
    public String toString() {
        return "AvailableCarDetails{" + "\n" +
                "id=" + id + "\n" +
                "make=" + make + "\n" +
                "model=" + model + "\n" +
                "bodyCategory=" + bodyCategory + "\n" +
                "yearOfProduction=" + yearOfProduction + "\n" +
                "color=" + color + "\n" +
                "mileage=" + mileage + "\n" +
                "amount=" + amount + "\n" +
                "carLocation=" + carLocation + "\n" +
                "}";
    }

}
