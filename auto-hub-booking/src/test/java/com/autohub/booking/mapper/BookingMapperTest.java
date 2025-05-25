package com.autohub.booking.mapper;

import com.autohub.booking.entity.Booking;
import com.autohub.booking.util.AssertionUtil;
import com.autohub.booking.util.TestUtil;
import com.autohub.dto.booking.BookingRequest;
import com.autohub.dto.common.AuthenticationInfo;
import com.autohub.dto.common.AvailableCarInfo;
import com.autohub.dto.common.BookingResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class BookingMapperTest {

    private final BookingMapper bookingMapper = new BookingMapperImpl();

    @Test
    void mapEntityToDtoTest_success() {
        Booking booking = TestUtil.getResourceAsJson("/data/Booking.json", Booking.class);

        BookingResponse bookingResponse = bookingMapper.mapEntityToDto(booking);

        AssertionUtil.assertBooking(booking, bookingResponse);
    }

    @Test
    void mapEntityToDtoTest_null() {
        assertNull(bookingMapper.mapEntityToDto(null));
    }

    @Test
    void getNewBookingTest_success() {
        BookingRequest bookingRequest =
                TestUtil.getResourceAsJson("/data/BookingRequest.json", BookingRequest.class);

        AvailableCarInfo availableCarInfo =
                TestUtil.getResourceAsJson("/data/AvailableCarInfo.json", AvailableCarInfo.class);

        AuthenticationInfo authenticationInfo = AuthenticationInfo.builder()
                .apikey("apikey")
                .username("user")
                .email("user@mail.com")
                .roles(List.of("admin"))
                .build();

        Booking actualBooking = bookingMapper.getNewBooking(bookingRequest, availableCarInfo, authenticationInfo);

        AssertionUtil.assertBooking(actualBooking, bookingRequest);
    }

    @Test
    void getNewBookingTest_null() {
        assertNull(bookingMapper.getNewBooking(null, null, null));
    }

}
