package com.autohub.agency.mapper;

import com.autohub.agency.entity.RentalOffice;
import com.autohub.agency.util.AssertionUtil;
import com.autohub.agency.util.TestUtil;
import com.autohub.dto.agency.RentalOfficeRequest;
import com.autohub.dto.agency.RentalOfficeResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class RentalOfficeMapperTest {

    private final RentalOfficeMapper rentalOfficeMapper = new RentalOfficeMapperImpl();

    @Test
    void mapEntityToDtoTest_success() {
        RentalOffice rentalOffice = TestUtil.getResourceAsJson("/data/RentalOffice.json", RentalOffice.class);

        RentalOfficeResponse rentalOfficeResponse = rentalOfficeMapper.mapEntityToDto(rentalOffice);

        assertNotNull(rentalOfficeResponse);
        AssertionUtil.assertRentalOfficeResponse(rentalOffice, rentalOfficeResponse);
    }

    @Test
    void mapEntityToDtoTest_null() {
        assertNull(rentalOfficeMapper.mapEntityToDto(null));
    }

    @Test
    void getNewRentalOfficeTest_success() {
        RentalOfficeRequest rentalOfficeRequest = TestUtil.getResourceAsJson("/data/RentalOfficeRequest.json", RentalOfficeRequest.class);

        RentalOffice rentalOffice = rentalOfficeMapper.getNewRentalOffice(rentalOfficeRequest);

        assertNotNull(rentalOffice);
        AssertionUtil.assertRentalOfficeRequest(rentalOffice, rentalOfficeRequest);
    }

    @Test
    void getNewRentalOfficeTest_null() {
        assertNull(rentalOfficeMapper.getNewRentalOffice(null));
    }

}
