package com.autohub.agency.mapper;

import com.autohub.agency.entity.RentalOffice;
import com.autohub.dto.agency.RentalOfficeRequest;
import com.autohub.dto.agency.RentalOfficeResponse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface RentalOfficeMapper {

    RentalOfficeResponse mapEntityToDto(RentalOffice rentalOffice);

    RentalOffice getNewRentalOffice(RentalOfficeRequest rentalOfficeRequest);

}
