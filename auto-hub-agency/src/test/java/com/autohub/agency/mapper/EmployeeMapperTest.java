package com.autohub.agency.mapper;

import com.autohub.agency.entity.Branch;
import com.autohub.agency.entity.Employee;
import com.autohub.agency.util.AssertionUtil;
import com.autohub.agency.util.TestUtil;
import com.autohub.dto.agency.EmployeeRequest;
import com.autohub.dto.agency.EmployeeResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class EmployeeMapperTest {

    private final EmployeeMapper employeeMapper = new EmployeeMapperImpl();

    @Test
    void mapEntityToDtoTest_success() {
        Employee employee = TestUtil.getResourceAsJson("/data/Employee.json", Employee.class);

        EmployeeResponse employeeResponse = employeeMapper.mapEntityToDto(employee);

        assertNotNull(employeeResponse);
        AssertionUtil.assertEmployeeResponse(employee, employeeResponse);
    }

    @Test
    void mapEntityToDtoTest_null() {
        assertNull(employeeMapper.mapEntityToDto(null));
    }

    @Test
    void getNewEmployeeTest_success() {
        EmployeeRequest employeeRequest =
                TestUtil.getResourceAsJson("/data/EmployeeRequest.json", EmployeeRequest.class);

        Branch workingBranch = TestUtil.getResourceAsJson("/data/Branch.json", Branch.class);

        Employee employee = employeeMapper.getNewEmployee(employeeRequest, workingBranch);

        assertNotNull(employeeRequest);
        AssertionUtil.assertEmployeeRequest(employee, employeeRequest);
    }

    @Test
    void getNewEmployeeTest_null() {
        assertNull(employeeMapper.getNewEmployee(null, null));
    }

}
