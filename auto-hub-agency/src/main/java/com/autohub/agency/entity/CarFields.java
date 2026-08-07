package com.autohub.agency.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CarFields {

    MAKE("MAKE"),
    MODEL("MODEL"),
    BODY_TYPE("BODY TYPE"),
    YEAR_OF_PRODUCTION("YEAR OF PRODUCTION"),
    COLOR("COLOR"),
    MILEAGE("MILEAGE"),
    CAR_STATUS("CAR STATUS"),
    AMOUNT("AMOUNT"),
    INITIAL_RENTAL_OFFICE_ID("INITIAL RENTAL OFFICE ID"),
    ACTUAL_RENTAL_OFFICE_ID("ACTUAL RENTAL OFFICE ID"),
    IMAGE("IMAGE");

    private final String excelValue;

}
