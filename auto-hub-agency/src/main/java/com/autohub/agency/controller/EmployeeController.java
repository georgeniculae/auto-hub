package com.autohub.agency.controller;

import com.autohub.agency.service.EmployeeService;
import com.autohub.dto.agency.EmployeeRequest;
import com.autohub.dto.agency.EmployeeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<List<EmployeeResponse>> findAllEmployees() {
        List<EmployeeResponse> employeeResponses = employeeService.findAllEmployees();

        return ResponseEntity.ok(employeeResponses);
    }

    @GetMapping(path = "/{id}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<EmployeeResponse> findEmployeeById(@PathVariable("id") Long id) {
        EmployeeResponse employeeResponse = employeeService.findEmployeeById(id);

        return ResponseEntity.ok(employeeResponse);
    }

    @GetMapping(path = "/branch/{id}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<List<EmployeeResponse>> findEmployeesByBranchId(@PathVariable("id") Long id) {
        List<EmployeeResponse> employeeResponses = employeeService.findEmployeesByBranchId(id);

        return ResponseEntity.ok(employeeResponses);
    }

    @GetMapping(path = "/filter/{filter}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<List<EmployeeResponse>> findEmployeesByFilter(@PathVariable("filter") String filter) {
        List<EmployeeResponse> employeeResponses = employeeService.findEmployeesByFilter(filter);

        return ResponseEntity.ok(employeeResponses);
    }

    @GetMapping(path = "/count")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Long> countEmployees() {
        Long numberOfEmployees = employeeService.countEmployees();

        return ResponseEntity.ok(numberOfEmployees);
    }

    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<EmployeeResponse> addEmployee(@RequestBody @Validated EmployeeRequest employeeRequest) {
        EmployeeResponse savedEmployeeResponse = employeeService.saveEmployee(employeeRequest);

        return ResponseEntity.ok(savedEmployeeResponse);
    }

    @PutMapping(path = "/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<EmployeeResponse> updateEmployee(@PathVariable("id") Long id,
                                                           @RequestBody @Validated EmployeeRequest employeeRequest) {
        EmployeeResponse updatedEmployeeResponse = employeeService.updateEmployee(id, employeeRequest);

        return ResponseEntity.ok(updatedEmployeeResponse);
    }

    @DeleteMapping(path = "/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> deleteEmployeeById(@PathVariable("id") Long id) {
        employeeService.deleteEmployeeById(id);

        return ResponseEntity.noContent().build();
    }

}
