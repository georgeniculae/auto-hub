package com.autohub.dto.agency;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

@Builder
public record BranchRequest(
        @NotEmpty(message = "Name cannot be empty")
        String name,

        @NotEmpty(message = "Region cannot be empty")
        String region,

        @NotEmpty(message = "Address cannot be empty")
        String address,

        @NotEmpty(message = "Phone number cannot be empty")
        String phoneNumber
) {

    @Override
    public String toString() {
        return "BranchRequest{" + "\n" +
                "name=" + name + "\n" +
                "region=" + region + "\n" +
                "address=" + address + "\n" +
                "phoneNumber=" + phoneNumber + "\n" +
                "}";
    }

}
