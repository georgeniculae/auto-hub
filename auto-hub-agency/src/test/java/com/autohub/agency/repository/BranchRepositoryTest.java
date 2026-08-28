package com.autohub.agency.repository;

import com.autohub.agency.entity.Branch;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BranchRepositoryTest extends AbstractRepositoryTest {

    @Test
    void findAllBranchesTest_success() {
        try (Stream<Branch> branchStream = branchRepository.findAllBranches()) {
            List<Branch> branches = branchStream.toList();
            assertEquals(2, branches.size());
        }
    }

    @Test
    void findByFilterTest_success() {
        try (Stream<Branch> branchStream = branchRepository.findByFilter("Branch")) {
            List<Branch> branches = branchStream.toList();
            assertEquals(2, branches.size());
        }
    }

}
