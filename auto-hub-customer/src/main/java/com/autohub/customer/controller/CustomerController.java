package com.autohub.customer.controller;

import com.autohub.customer.service.CustomerService;
import com.autohub.dto.customer.RegisterRequest;
import com.autohub.dto.customer.RegistrationResponse;
import com.autohub.dto.customer.UserInfo;
import com.autohub.dto.customer.UserUpdateRequest;
import com.autohub.lib.aspect.LogActivity;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping(path = "/infos")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<List<UserInfo>> findAllUsers() {
        List<UserInfo> allCustomers = customerService.findAllUsers();

        return ResponseEntity.ok(allCustomers);
    }

    @GetMapping(path = "/current")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<UserInfo> getCurrentUser() {
        return ResponseEntity.ok(customerService.getCurrentUser());
    }

    @GetMapping(path = "/{username}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<UserInfo> findUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(customerService.findUserByUsername(username));
    }

    @PostMapping("/register")
    @LogActivity(
            sentParameters = "registerRequest",
            activityDescription = "User registration"
    )
    public ResponseEntity<RegistrationResponse> registerUser(@RequestBody @Validated RegisterRequest registerRequest) {
        return ResponseEntity.ok(customerService.registerCustomer(registerRequest));
    }

    @PutMapping(path = "/{id}")
    @PreAuthorize("hasRole('admin')")
    @LogActivity(
            sentParameters = "id",
            activityDescription = "User update"
    )
    public ResponseEntity<UserInfo> updateUser(
            @PathVariable String id,
            @RequestBody @Validated UserUpdateRequest userUpdateRequest
    ) {
        return ResponseEntity.ok(customerService.updateUser(id, userUpdateRequest));
    }

    @GetMapping(path = "/count")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Integer> countUsers() {
        return ResponseEntity.ok(customerService.countUsers());
    }

    @DeleteMapping(path = "/{username}")
    @PreAuthorize("hasRole('admin')")
    @LogActivity(
            sentParameters = "username",
            activityDescription = "User deletion"
    )
    public ResponseEntity<Void> deleteUserByUsername(@PathVariable String username) {
        customerService.deleteUserByUsername(username);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "/current")
    @PreAuthorize("hasRole('admin')")
    @LogActivity(
            activityDescription = "Current user deletion"
    )
    public ResponseEntity<Void> deleteCurrentUser() {
        customerService.deleteCurrentUser();

        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/sign-out")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<Void> signOut() {
        customerService.signOut();

        return ResponseEntity.noContent().build();
    }

}
