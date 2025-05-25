package com.autohub.booking.mapper;

import com.autohub.booking.entity.Booking;
import com.autohub.dto.booking.BookingRequest;
import com.autohub.dto.booking.CreatedBookingReprocessRequest;
import com.autohub.dto.booking.UpdatedBookingReprocessRequest;
import com.autohub.dto.common.AuthenticationInfo;
import com.autohub.dto.common.AvailableCarInfo;
import com.autohub.dto.common.BookingResponse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.time.LocalDate;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface BookingMapper {

    @Mapping(target = "carId", source = "actualCarId")
    BookingResponse mapEntityToDto(Booking booking);

    @Mapping(target = "customerUsername", expression = "java(authenticationInfo.username())")
    @Mapping(target = "customerEmail", expression = "java(authenticationInfo.email())")
    @Mapping(target = "actualCarId", expression = "java(availableCarInfo.id())")
    @Mapping(target = "dateOfBooking", expression = "java(getDateOfBooking())")
    @Mapping(target = "rentalBranchId", expression = "java(availableCarInfo.actualBranchId())")
    @Mapping(target = "status", constant = "IN_PROGRESS")
    @Mapping(target = "rentalCarPrice", expression = "java(availableCarInfo.amount())")
    Booking getNewBooking(BookingRequest bookingRequest, AvailableCarInfo availableCarInfo, AuthenticationInfo authenticationInfo);

    CreatedBookingReprocessRequest mapToCreatedBookingReprocessRequest(Booking booking);

    UpdatedBookingReprocessRequest mapToUpdatedBookingReprocessRequest(Booking booking);

    @Mapping(target = "carId", source = "actualCarId")
    BookingResponse mapReprocessRequestToBookingResponse(CreatedBookingReprocessRequest reprocessRequest);

    @Mapping(target = "carId", source = "actualCarId")
    BookingResponse mapReprocessRequestToBookingResponse(UpdatedBookingReprocessRequest reprocessRequest);

    default LocalDate getDateOfBooking() {
        return LocalDate.now();
    }

}
