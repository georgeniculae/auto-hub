package com.autohub.dto.common;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AvailableCarInfo(
        Long id,
        Long actualRentalOfficeId,
        BigDecimal amount
) {
}
