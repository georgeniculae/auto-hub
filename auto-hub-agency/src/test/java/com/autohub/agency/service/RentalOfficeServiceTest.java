package com.autohub.agency.service;

import com.autohub.agency.entity.RentalOffice;
import com.autohub.agency.mapper.RentalOfficeMapper;
import com.autohub.agency.mapper.RentalOfficeMapperImpl;
import com.autohub.agency.repository.RentalOfficeRepository;
import com.autohub.agency.util.AssertionUtil;
import com.autohub.agency.util.TestUtil;
import com.autohub.dto.agency.RentalOfficeRequest;
import com.autohub.dto.agency.RentalOfficeResponse;
import com.autohub.exception.AutoHubNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RentalOfficeServiceTest {

    @InjectMocks
    private RentalOfficeService rentalOfficeService;

    @Mock
    private RentalOfficeRepository rentalOfficeRepository;

    @Spy
    private RentalOfficeMapper rentalOfficeMapper = new RentalOfficeMapperImpl();

    @Captor
    private ArgumentCaptor<RentalOffice> argumentCaptor = ArgumentCaptor.forClass(RentalOffice.class);

    @Test
    void findAllRentalOfficesTest_success() {
        RentalOffice rentalOffice =
                TestUtil.getResourceAsJson("/data/RentalOffice.json", RentalOffice.class);

        when(rentalOfficeRepository.findAllRentalOffices()).thenReturn(Stream.of(rentalOffice));

        List<RentalOfficeResponse> rentalOfficeResponses = rentalOfficeService.findAllRentalOffices();
        AssertionUtil.assertRentalOfficeResponse(rentalOffice, rentalOfficeResponses.getFirst());
    }

    @Test
    void findRentalOfficeByIdTest_success() {
        RentalOffice rentalOffice =
                TestUtil.getResourceAsJson("/data/RentalOffice.json", RentalOffice.class);

        when(rentalOfficeRepository.findById(anyLong())).thenReturn(Optional.of(rentalOffice));

        RentalOfficeResponse rentalOfficeResponse = rentalOfficeService.findRentalOfficeById(1L);
        AssertionUtil.assertRentalOfficeResponse(rentalOffice, rentalOfficeResponse);
    }

    @Test
    void findRentalOfficeByIdTest_errorOnFindingById() {
        when(rentalOfficeRepository.findById(anyLong())).thenReturn(Optional.empty());

        AutoHubNotFoundException autoHubNotFoundException =
                assertThrows(AutoHubNotFoundException.class, () -> rentalOfficeService.findRentalOfficeById(1L));

        assertNotNull(autoHubNotFoundException);
        assertEquals("Rental office with id 1 does not exist", autoHubNotFoundException.getReason());
    }

    @Test
    void saveRentalOfficeTest_success() {
        RentalOffice rentalOffice =
                TestUtil.getResourceAsJson("/data/RentalOffice.json", RentalOffice.class);
        RentalOfficeRequest rentalOfficeRequest =
                TestUtil.getResourceAsJson("/data/RentalOfficeRequest.json", RentalOfficeRequest.class);

        when(rentalOfficeRepository.save(any(RentalOffice.class))).thenReturn(rentalOffice);

        RentalOfficeResponse savedRentalOfficeResponse = rentalOfficeService.saveRentalOffice(rentalOfficeRequest);
        AssertionUtil.assertRentalOfficeResponse(rentalOffice, savedRentalOfficeResponse);

        verify(rentalOfficeRepository).save(argumentCaptor.capture());
        verify(rentalOfficeMapper).mapEntityToDto(any(RentalOffice.class));
    }

    @Test
    void updateRentalOfficeTest_success() {
        RentalOffice rentalOffice = TestUtil.getResourceAsJson("/data/RentalOffice.json", RentalOffice.class);
        RentalOfficeRequest rentalOfficeRequest = TestUtil.getResourceAsJson("/data/RentalOfficeRequest.json", RentalOfficeRequest.class);

        when(rentalOfficeRepository.findById(anyLong())).thenReturn(Optional.of(rentalOffice));
        when(rentalOfficeRepository.save(any(RentalOffice.class))).thenReturn(rentalOffice);

        RentalOfficeResponse updatedRentalOfficeResponse = rentalOfficeService.updateRentalOffice(1L, rentalOfficeRequest);
        AssertionUtil.assertRentalOfficeResponse(rentalOffice, updatedRentalOfficeResponse);
    }

    @Test
    void findRentalOfficeByNameTest_success() {
        RentalOffice rentalOffice = TestUtil.getResourceAsJson("/data/RentalOffice.json", RentalOffice.class);

        when(rentalOfficeRepository.findRentalOfficeByFilter(anyString())).thenReturn(Stream.of(rentalOffice));

        List<RentalOfficeResponse> rentalOfficeResponses =
                rentalOfficeService.findRentalOfficeByFilter("Test Rental Office");

        AssertionUtil.assertRentalOfficeResponse(rentalOffice, rentalOfficeResponses.getFirst());
    }

}
