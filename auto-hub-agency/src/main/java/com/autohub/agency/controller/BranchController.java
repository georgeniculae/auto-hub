package com.autohub.agency.controller;

import com.autohub.agency.service.BranchService;
import com.autohub.dto.agency.BranchRequest;
import com.autohub.dto.agency.BranchResponse;
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
@RequestMapping(path = "/branches")
public class BranchController {

    private final BranchService branchService;

    @GetMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<List<BranchResponse>> findAllBranches() {
        List<BranchResponse> branchResponses = branchService.findAllBranches();

        return ResponseEntity.ok(branchResponses);
    }

    @GetMapping(path = "/{id}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<BranchResponse> findBranchById(@PathVariable Long id) {
        BranchResponse branchResponse = branchService.findBranchById(id);

        return ResponseEntity.ok(branchResponse);
    }

    @GetMapping(path = "/filter/{filter}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<List<BranchResponse>> findBranchesByFilter(@PathVariable String filter) {
        List<BranchResponse> branchResponses = branchService.findBranchesByFilter(filter);

        return ResponseEntity.ok(branchResponses);
    }

    @GetMapping(path = "/count")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<Long> countBranches() {
        Long numberOfBranches = branchService.countBranches();

        return ResponseEntity.ok(numberOfBranches);
    }

    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<BranchResponse> addBranch(@RequestBody @Validated BranchRequest branchRequest) {
        BranchResponse savedBranchResponse = branchService.saveBranch(branchRequest);

        return ResponseEntity.ok(savedBranchResponse);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<BranchResponse> updateBranch(
            @PathVariable Long id,
            @RequestBody @Validated BranchRequest branchRequest
    ) {
        BranchResponse updatedBranchResponse = branchService.updateBranch(id, branchRequest);

        return ResponseEntity.ok(updatedBranchResponse);
    }

    @DeleteMapping(path = "/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> deleteBranchById(@PathVariable Long id) {
        branchService.deleteBranchById(id);

        return ResponseEntity.noContent().build();
    }

}
