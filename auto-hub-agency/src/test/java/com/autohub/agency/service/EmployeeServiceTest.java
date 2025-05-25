package com.autohub.agency.service;

import com.autohub.agency.entity.Branch;
import com.autohub.agency.entity.Employee;
import com.autohub.agency.mapper.EmployeeMapper;
import com.autohub.agency.mapper.EmployeeMapperImpl;
import com.autohub.agency.repository.EmployeeRepository;
import com.autohub.agency.util.AssertionUtil;
import com.autohub.agency.util.TestUtil;
import com.autohub.dto.agency.EmployeeRequest;
import com.autohub.dto.agency.EmployeeResponse;
import com.autohub.exception.AutoHubNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @InjectMocks
    private EmployeeService employeeService;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private BranchService branchService;

    @Spy
    private EmployeeMapper employeeMapper = new EmployeeMapperImpl();

    @Test
    void findAllEmployeesTest_success() {
        Employee employee = TestUtil.getResourceAsJson("/data/Employee.json", Employee.class);

        when(employeeRepository.findAllEmployee()).thenReturn(Stream.of(employee));

        List<EmployeeResponse> employeeResponses = employeeService.findAllEmployees();
        AssertionUtil.assertEmployeeResponse(employee, employeeResponses.getFirst());
    }

    @Test
    void findEmployeeByIdTest_success() {
        Employee employee = TestUtil.getResourceAsJson("/data/Employee.json", Employee.class);

        when(employeeRepository.findById(anyLong())).thenReturn(Optional.of(employee));

        EmployeeResponse employeeResponse = employeeService.findEmployeeById(1L);
        AssertionUtil.assertEmployeeResponse(employee, employeeResponse);
    }

    @Test
    void findEmployeeByIdTest_errorOnFindingById() {
        when(employeeRepository.findById(anyLong())).thenReturn(Optional.empty());

        AutoHubNotFoundException autoHubNotFoundException =
                assertThrows(AutoHubNotFoundException.class, () -> employeeService.findEmployeeById(1L));

        assertNotNull(autoHubNotFoundException);
        assertEquals("Employee with id 1 does not exist", autoHubNotFoundException.getReason());
    }

    @Test
    void saveEmployeeTest_success() {
        Employee employee = TestUtil.getResourceAsJson("/data/Employee.json", Employee.class);

        EmployeeRequest employeeRequest =
                TestUtil.getResourceAsJson("/data/EmployeeRequest.json", EmployeeRequest.class);

        Branch branch = TestUtil.getResourceAsJson("/data/Branch.json", Branch.class);

        when(branchService.findEntityById(anyLong())).thenReturn(branch);
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        EmployeeResponse savedEmployeeResponse = employeeService.saveEmployee(employeeRequest);
        AssertionUtil.assertEmployeeResponse(employee, savedEmployeeResponse);

        verify(employeeMapper).mapEntityToDto(any(Employee.class));
    }

    @Test
    void updateEmployeeTest_success() {
        Employee employee = TestUtil.getResourceAsJson("/data/Employee.json", Employee.class);

        EmployeeRequest employeeRequest =
                TestUtil.getResourceAsJson("/data/EmployeeRequest.json", EmployeeRequest.class);

        Branch branch = TestUtil.getResourceAsJson("/data/Branch.json", Branch.class);

        when(branchService.findEntityById(anyLong())).thenReturn(branch);
        when(employeeRepository.findById(anyLong())).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        EmployeeResponse employeeResponse = employeeService.updateEmployee(1L, employeeRequest);
        AssertionUtil.assertEmployeeResponse(employee, employeeResponse);
    }

    @Test
    void findEmployeesByBranchIdTest_success() {
        Employee employee = TestUtil.getResourceAsJson("/data/Employee.json", Employee.class);

        when(employeeRepository.findAllEmployeesByBranchId(anyLong())).thenReturn(Stream.of(employee));

        List<EmployeeResponse> employeeResponses = employeeService.findEmployeesByBranchId(1L);
        AssertionUtil.assertEmployeeResponse(employee, employeeResponses.getFirst());
    }

    @Test
    void findEmployeesByFilterTest_success() {
        Employee employee = TestUtil.getResourceAsJson("/data/Employee.json", Employee.class);

        when(employeeRepository.findByFilter(anyString())).thenReturn(Stream.of(employee));

        List<EmployeeResponse> employeeResponses = employeeService.findEmployeesByFilter("Ion");
        AssertionUtil.assertEmployeeResponse(employee, employeeResponses.getFirst());
    }

}
