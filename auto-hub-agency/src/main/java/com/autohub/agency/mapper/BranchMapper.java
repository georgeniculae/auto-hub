package com.autohub.agency.mapper;

import com.autohub.agency.entity.Branch;
import com.autohub.agency.entity.RentalOffice;
import com.autohub.dto.agency.BranchRequest;
import com.autohub.dto.agency.BranchResponse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface BranchMapper {

    @Mapping(target = "rentalOfficeId", expression = "java(branch.getRentalOffice().getId())")
    BranchResponse mapEntityToDto(Branch branch);

    @Mapping(target = "rentalOffice", expression = "java(rentalOffice)")
    @Mapping(target = "name", expression = "java(branchRequest.name())")
    Branch getNewBranch(BranchRequest branchRequest, RentalOffice rentalOffice);

}
