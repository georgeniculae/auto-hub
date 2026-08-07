package com.autohub.agency.util;

import com.autohub.agency.entity.Branch;
import com.autohub.agency.entity.Car;
import com.autohub.agency.entity.Employee;
import com.autohub.agency.entity.RentalOffice;
import com.autohub.dto.agency.BranchRequest;
import com.autohub.dto.agency.BranchResponse;
import com.autohub.dto.agency.CarRequest;
import com.autohub.dto.agency.CarResponse;
import com.autohub.dto.agency.EmployeeRequest;
import com.autohub.dto.agency.EmployeeResponse;
import com.autohub.dto.agency.RentalOfficeRequest;
import com.autohub.dto.agency.RentalOfficeResponse;
import com.autohub.dto.common.AvailableCarInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssertionUtil {

    public static void assertCarRequest(Car car, CarRequest carRequest) {
        assertEquals(car.getMake(), carRequest.make());
        assertEquals(car.getModel(), carRequest.model());
        assertEquals(car.getBodyType().getDisplayName(), carRequest.bodyCategory().getDisplayName());
        assertEquals(car.getYearOfProduction(), carRequest.yearOfProduction());
        assertEquals(car.getColor(), carRequest.color());
        assertEquals(car.getMileage(), carRequest.mileage());
        assertEquals(car.getCarStatus().getDisplayName(), carRequest.carState().getDisplayName());
        assertEquals(car.getAmount(), carRequest.amount());
        assertEquals(car.getInitialRentalOffice().getId(), carRequest.initialRentalOfficeId());
        assertEquals(car.getActualRentalOffice().getId(), carRequest.actualRentalOfficeId());
    }

    public static void assertCarResponse(Car car, CarResponse carResponse) {
        assertEquals(car.getMake(), carResponse.make());
        assertEquals(car.getModel(), carResponse.model());
        assertEquals(car.getBodyType().getDisplayName(), carResponse.bodyCategory().getDisplayName());
        assertEquals(car.getYearOfProduction(), carResponse.yearOfProduction());
        assertEquals(car.getColor(), carResponse.color());
        assertEquals(car.getMileage(), carResponse.mileage());
        assertEquals(car.getCarStatus().getDisplayName(), carResponse.carState().getDisplayName());
        assertEquals(car.getAmount(), carResponse.amount());
        assertEquals(car.getInitialRentalOffice().getId(), carResponse.initialRentalOfficeId());
        assertEquals(car.getActualRentalOffice().getId(), carResponse.actualRentalOfficeId());
        assertEquals(car.getActualRentalOffice().getCity(), carResponse.carLocation());
    }

    public static void assertAvailableCarInfo(Car car, AvailableCarInfo availableCarInfo) {
        assertEquals(car.getId(), availableCarInfo.id());
        assertEquals(car.getActualRentalOffice().getId(), availableCarInfo.actualRentalOfficeId());
        assertEquals(car.getAmount(), availableCarInfo.amount());
    }

    public static void assertBranchRequest(Branch branch, BranchRequest branchRequest) {
        assertEquals(branch.getName(), branchRequest.name());
        assertEquals(branch.getRegion(), branchRequest.region());
        assertEquals(branch.getAddress(), branchRequest.address());
        assertEquals(branch.getPhoneNumber(), branchRequest.phoneNumber());
    }

    public static void assertBranchResponse(Branch branch, BranchResponse branchResponse) {
        assertEquals(branch.getId(), branchResponse.id());
        assertEquals(branch.getName(), branchResponse.name());
        assertEquals(branch.getRegion(), branchResponse.region());
        assertEquals(branch.getAddress(), branchResponse.address());
        assertEquals(branch.getPhoneNumber(), branchResponse.phoneNumber());
    }

    public static void assertRentalOfficeRequest(RentalOffice rentalOffice, RentalOfficeRequest rentalOfficeRequest) {
        assertEquals(rentalOffice.getName(), rentalOfficeRequest.name());
        assertEquals(rentalOffice.getCity(), rentalOfficeRequest.city());
        assertEquals(rentalOffice.getAddress(), rentalOfficeRequest.address());
        assertEquals(rentalOffice.getBranch().getId(), rentalOfficeRequest.branchId());
    }

    public static void assertRentalOfficeResponse(RentalOffice rentalOffice, RentalOfficeResponse rentalOfficeResponse) {
        assertEquals(rentalOffice.getId(), rentalOfficeResponse.id());
        assertEquals(rentalOffice.getName(), rentalOfficeResponse.name());
        assertEquals(rentalOffice.getCity(), rentalOfficeResponse.city());
        assertEquals(rentalOffice.getAddress(), rentalOfficeResponse.address());
        assertEquals(rentalOffice.getBranch().getId(), rentalOfficeResponse.branchId());
    }

    public static void assertEmployeeRequest(Employee employee, EmployeeRequest employeeRequest) {
        assertEquals(employee.getFirstName(), employeeRequest.firstName());
        assertEquals(employee.getLastName(), employeeRequest.lastName());
        assertEquals(employee.getJobPosition(), employeeRequest.jobPosition());
        assertEquals(employee.getWorkingRentalOffice().getId(), employeeRequest.workingRentalOfficeId());
    }

    public static void assertEmployeeResponse(Employee employee, EmployeeResponse employeeResponse) {
        assertEquals(employee.getId(), employeeResponse.id());
        assertEquals(employee.getFirstName(), employeeResponse.firstName());
        assertEquals(employee.getLastName(), employeeResponse.lastName());
        assertEquals(employee.getJobPosition(), employeeResponse.jobPosition());
        assertEquals(employee.getWorkingRentalOffice().getId(), employeeResponse.workingRentalOfficeId());
    }

}
