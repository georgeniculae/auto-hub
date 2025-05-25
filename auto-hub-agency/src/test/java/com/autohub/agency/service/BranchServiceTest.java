package com.autohub.agency.service;

import com.autohub.agency.entity.Branch;
import com.autohub.agency.entity.RentalOffice;
import com.autohub.agency.mapper.BranchMapper;
import com.autohub.agency.mapper.BranchMapperImpl;
import com.autohub.agency.repository.BranchRepository;
import com.autohub.agency.util.AssertionUtil;
import com.autohub.agency.util.TestUtil;
import com.autohub.dto.agency.BranchRequest;
import com.autohub.dto.agency.BranchResponse;
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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BranchServiceTest {

    @InjectMocks
    private BranchService branchService;

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private RentalOfficeService rentalOfficeService;

    @Spy
    private BranchMapper branchMapper = new BranchMapperImpl();

    @Test
    void findBranchByIdTest_success() {
        Branch branch = TestUtil.getResourceAsJson("/data/Branch.json", Branch.class);

        when(branchRepository.findById(anyLong())).thenReturn(Optional.of(branch));

        BranchResponse actualBranchResponse = branchService.findBranchById(1L);

        assertNotNull(actualBranchResponse);
        verify(branchMapper).mapEntityToDto(any(Branch.class));
    }

    @Test
    void findBranchByIdTest_errorOnFindingById() {
        when(branchRepository.findById(anyLong())).thenReturn(Optional.empty());

        AutoHubNotFoundException autoHubNotFoundException =
                assertThrows(AutoHubNotFoundException.class, () -> branchService.findBranchById(1L));

        assertNotNull(autoHubNotFoundException);
    }

    @Test
    void updateBranchTest_success() {
        Branch branch = TestUtil.getResourceAsJson("/data/Branch.json", Branch.class);
        BranchRequest branchRequest = TestUtil.getResourceAsJson("/data/BranchRequest.json", BranchRequest.class);
        RentalOffice rentalOffice = TestUtil.getResourceAsJson("/data/RentalOffice.json", RentalOffice.class);

        when(rentalOfficeService.findEntityById(anyLong())).thenReturn(rentalOffice);
        when(branchRepository.findById(anyLong())).thenReturn(Optional.of(branch));
        when(branchRepository.save(branch)).thenReturn(branch);

        BranchResponse updatedBranchResponse = branchService.updateBranch(1L, branchRequest);
        assertNotNull(updatedBranchResponse);
    }

    @Test
    void saveBranchTest_success() {
        Branch branch = TestUtil.getResourceAsJson("/data/Branch.json", Branch.class);
        BranchRequest branchRequest = TestUtil.getResourceAsJson("/data/BranchRequest.json", BranchRequest.class);
        RentalOffice rentalOffice = TestUtil.getResourceAsJson("/data/RentalOffice.json", RentalOffice.class);

        when(rentalOfficeService.findEntityById(anyLong())).thenReturn(rentalOffice);
        when(branchRepository.save(any(Branch.class))).thenReturn(branch);

        BranchResponse savedBranchResponse = branchService.saveBranch(branchRequest);
        AssertionUtil.assertBranchResponse(branch, savedBranchResponse);
    }

    @Test
    void findAllBranchesTest_success() {
        Branch branch = TestUtil.getResourceAsJson("/data/Branch.json", Branch.class);

        when(branchRepository.findAllBranches()).thenReturn(Stream.of(branch));

        List<BranchResponse> branchResponses = branchService.findAllBranches();
        AssertionUtil.assertBranchResponse(branch, branchResponses.getFirst());
    }

    @Test
    void findBranchesByFilterTest_success() {
        Branch branch = TestUtil.getResourceAsJson("/data/Branch.json", Branch.class);

        when(branchRepository.findByFilter(anyString())).thenReturn(Stream.of(branch));

        List<BranchResponse> branchResponses = branchService.findBranchesByFilter("Test");
        AssertionUtil.assertBranchResponse(branch, branchResponses.getFirst());
    }

}
