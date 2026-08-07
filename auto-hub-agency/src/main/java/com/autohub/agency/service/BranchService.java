package com.autohub.agency.service;

import com.autohub.agency.entity.Branch;
import com.autohub.agency.mapper.BranchMapper;
import com.autohub.agency.repository.BranchRepository;
import com.autohub.dto.agency.BranchRequest;
import com.autohub.dto.agency.BranchResponse;
import com.autohub.exception.AutoHubNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class BranchService {

    private final BranchRepository branchRepository;
    private final BranchMapper branchMapper;

    @Transactional(readOnly = true)
    public List<BranchResponse> findAllBranches() {
        try (Stream<Branch> branchStream = branchRepository.findAllBranches()) {
            return branchStream.map(branchMapper::mapEntityToDto).toList();
        }
    }

    public BranchResponse findBranchById(Long id) {
        Branch branch = findEntityById(id);

        return branchMapper.mapEntityToDto(branch);
    }

    public Branch findEntityById(Long id) {
        return branchRepository.findById(id)
                .orElseThrow(() -> new AutoHubNotFoundException("Branch with id " + id + " does not exist"));
    }

    public BranchResponse saveBranch(BranchRequest branchRequest) {
        Branch newBranch = branchMapper.getNewBranch(branchRequest);
        Branch savedBranch = saveEntity(newBranch);

        return branchMapper.mapEntityToDto(savedBranch);
    }

    public BranchResponse updateBranch(Long id, BranchRequest updatedBranchRequest) {
        Branch existingBranch = findEntityById(id);

        existingBranch.setName(updatedBranchRequest.name());
        existingBranch.setRegion(updatedBranchRequest.region());
        existingBranch.setAddress(updatedBranchRequest.address());
        existingBranch.setPhoneNumber(updatedBranchRequest.phoneNumber());

        Branch savedBranch = saveEntity(existingBranch);

        return branchMapper.mapEntityToDto(savedBranch);
    }

    public void deleteBranchById(Long id) {
        branchRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<BranchResponse> findBranchesByFilter(String filter) {
        try (Stream<Branch> branchesStream = branchRepository.findByFilter(filter)) {
            return branchesStream.map(branchMapper::mapEntityToDto).toList();
        }
    }

    public Long countBranches() {
        return branchRepository.count();
    }

    private Branch saveEntity(Branch branch) {
        return branchRepository.save(branch);
    }

}
