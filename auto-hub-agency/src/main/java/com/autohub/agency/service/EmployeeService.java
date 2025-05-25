package com.autohub.agency.service;

import com.autohub.agency.entity.Branch;
import com.autohub.agency.entity.Employee;
import com.autohub.agency.mapper.EmployeeMapper;
import com.autohub.agency.repository.EmployeeRepository;
import com.autohub.dto.agency.EmployeeRequest;
import com.autohub.dto.agency.EmployeeResponse;
import com.autohub.exception.AutoHubNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final BranchService branchService;
    private final EmployeeMapper employeeMapper;

    @Transactional(readOnly = true)
    public List<EmployeeResponse> findAllEmployees() {
        try (Stream<Employee> employeeStream = employeeRepository.findAllEmployee()) {
            return employeeStream.map(employeeMapper::mapEntityToDto).toList();
        }
    }

    public void deleteEmployeeById(Long id) {
        employeeRepository.deleteById(id);
    }

    public EmployeeResponse findEmployeeById(Long id) {
        Employee employee = findEntityById(id);

        return employeeMapper.mapEntityToDto(employee);
    }

    public Employee findEntityById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new AutoHubNotFoundException("Employee with id " + id + " does not exist"));
    }

    public EmployeeResponse saveEmployee(EmployeeRequest employeeRequest) {
        Long workingBranchId = employeeRequest.workingBranchId();
        Branch workingBranch = branchService.findEntityById(workingBranchId);

        Employee newEmployee = employeeMapper.getNewEmployee(employeeRequest, workingBranch);
        Employee savedEmployee = saveEntity(newEmployee);

        return employeeMapper.mapEntityToDto(savedEmployee);
    }

    public EmployeeResponse updateEmployee(Long id, EmployeeRequest updatedEmployeeRequest) {
        Employee existingEmployee = findEntityById(id);

        Long workingBranchId = updatedEmployeeRequest.workingBranchId();
        Branch workingBranch = branchService.findEntityById(workingBranchId);

        existingEmployee.setFirstName(updatedEmployeeRequest.firstName());
        existingEmployee.setLastName(updatedEmployeeRequest.lastName());
        existingEmployee.setJobPosition(updatedEmployeeRequest.jobPosition());
        existingEmployee.setWorkingBranch(workingBranch);

        Employee savedEmployee = saveEntity(existingEmployee);

        return employeeMapper.mapEntityToDto(savedEmployee);
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> findEmployeesByBranchId(Long id) {
        try (Stream<Employee> employeeStream = employeeRepository.findAllEmployeesByBranchId(id)) {
            return employeeStream.map(employeeMapper::mapEntityToDto).toList();
        }
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> findEmployeesByFilter(String filter) {
        try (Stream<Employee> employeeStream = employeeRepository.findByFilter(filter)) {
            return employeeStream.map(employeeMapper::mapEntityToDto).toList();
        }
    }

    public Long countEmployees() {
        return employeeRepository.count();
    }

    private Employee saveEntity(Employee newEmployee) {
        return employeeRepository.save(newEmployee);
    }

}
