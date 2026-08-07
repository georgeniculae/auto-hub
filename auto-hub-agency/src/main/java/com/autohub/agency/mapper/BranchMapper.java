package com.autohub.agency.mapper;

import com.autohub.agency.entity.Branch;
import com.autohub.dto.agency.BranchRequest;
import com.autohub.dto.agency.BranchResponse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface BranchMapper {

    BranchResponse mapEntityToDto(Branch branch);

    Branch getNewBranch(BranchRequest branchRequest);

}
