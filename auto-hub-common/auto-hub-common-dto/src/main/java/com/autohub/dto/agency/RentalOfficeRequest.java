package com.autohub.dto.agency;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record RentalOfficeRequest(
        @NotEmpty(message = "Name cannot be empty")
        String name,

        @NotEmpty(message = "City cannot be empty")
        String city,

        @NotEmpty(message = "Address cannot be empty")
        String address,

        @NotNull(message = "Branch id cannot be null")
        Long branchId
) {

    @Override
    public String toString() {
        return "RentalOfficeRequest{" + "\n" +
                "name=" + name + "\n" +
                "city=" + city + "\n" +
                "address=" + address + "\n" +
                "branchId=" + branchId + "\n" +
                "}";
    }

}
