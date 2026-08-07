package com.autohub.agency.mapper;

import com.autohub.agency.entity.Branch;
import com.autohub.agency.entity.RentalOffice;
import com.autohub.dto.agency.RentalOfficeRequest;
import com.autohub.dto.agency.RentalOfficeResponse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface RentalOfficeMapper {

    @Mapping(target = "branchId", expression = "java(rentalOffice.getBranch().getId())")
    RentalOfficeResponse mapEntityToDto(RentalOffice rentalOffice);

    @Mapping(target = "branch", expression = "java(branch)")
    @Mapping(target = "name", source = "rentalOfficeRequest.name")
    @Mapping(target = "address", source = "rentalOfficeRequest.address")
    RentalOffice getNewRentalOffice(RentalOfficeRequest rentalOfficeRequest, Branch branch);

}
