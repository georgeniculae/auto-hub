package com.autohub.agency.service;

import com.autohub.agency.entity.Branch;
import com.autohub.agency.entity.RentalOffice;
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
    private final RentalOfficeService rentalOfficeService;
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
        RentalOffice rentalOffice = rentalOfficeService.findEntityById(branchRequest.rentalOfficeId());
        Branch newBranch = branchMapper.getNewBranch(branchRequest, rentalOffice);
        Branch savedBranch = saveEntity(newBranch);

        return branchMapper.mapEntityToDto(savedBranch);
    }

    public BranchResponse updateBranch(Long id, BranchRequest updatedBranchRequest) {
        Branch exitingBranch = findEntityById(id);

        Long rentalOfficeId = updatedBranchRequest.rentalOfficeId();
        RentalOffice rentalOffice = rentalOfficeService.findEntityById(rentalOfficeId);

        exitingBranch.setName(updatedBranchRequest.name());
        exitingBranch.setAddress(updatedBranchRequest.address());
        exitingBranch.setRentalOffice(rentalOffice);

        Branch savedBranch = saveEntity(exitingBranch);

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
