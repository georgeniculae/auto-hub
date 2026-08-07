package com.autohub.agency.mapper;

import com.autohub.agency.entity.Employee;
import com.autohub.agency.entity.RentalOffice;
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

    @Mapping(target = "workingRentalOfficeId", expression = "java(employee.getWorkingRentalOffice().getId())")
    EmployeeResponse mapEntityToDto(Employee employee);

    @Mapping(target = "workingRentalOffice", expression = "java(workingRentalOffice)")
    Employee getNewEmployee(EmployeeRequest employeeRequest, RentalOffice workingRentalOffice);

}
