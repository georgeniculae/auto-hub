package com.autohub.agency.mapper;

import com.autohub.agency.entity.Branch;
import com.autohub.agency.entity.Employee;
import com.autohub.dto.agency.EmployeeRequest;
import com.autohub.dto.agency.EmployeeResponse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface EmployeeMapper {

    @Mapping(target = "workingBranchId", expression = "java(employee.getWorkingBranch().getId())")
    EmployeeResponse mapEntityToDto(Employee employee);

    @Mapping(target = "workingBranch", expression = "java(workingBranch)")
    Employee getNewEmployee(EmployeeRequest employeeRequest, Branch workingBranch);

}
