package com.autohub.booking.eventhandling;

import com.autohub.booking.entity.Booking;
import com.autohub.booking.mapper.BookingMapper;
import com.autohub.booking.producer.bookingprocessing.BookingProducerService;
import com.autohub.booking.producer.bookingprocessing.UpdateBookingUpdateCarsProducerService;
import com.autohub.booking.producer.dlq.FailedUpdatedBookingDlqProducerService;
import com.autohub.dto.booking.UpdatedBookingReprocessRequest;
import com.autohub.dto.common.BookingResponse;
import com.autohub.dto.common.UpdateCarsRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdatedBookingProcessorService {

    private final UpdateBookingUpdateCarsProducerService updateBookingUpdateCarsProducerService;
    private final BookingProducerService bookingProducerService;
    private final RedisTemplate<String, String> redisTemplate;
    private final FailedUpdatedBookingDlqProducerService failedUpdatedBookingDlqProducerService;
    private final BookingMapper bookingMapper;

    public void handleBookingUpdate(Booking booking) {
        try {
            updateCarsStatuses(booking.getPreviousCarId(), booking.getActualCarId());
            unlockCar(booking.getActualCarId().toString());

            BookingResponse bookingResponse = bookingMapper.mapEntityToDto(booking);
            bookingProducerService.sendUpdatedBooking(bookingResponse);
        } catch (Exception e) {
            log.error("Error while processing updated booking: {}", e.getMessage());

            UpdatedBookingReprocessRequest reprocessRequest = bookingMapper.mapToUpdatedBookingReprocessRequest(booking);
            failedUpdatedBookingDlqProducerService.sendFailedUpdatedBooking(reprocessRequest);
        }
    }

    private void updateCarsStatuses(Long previousCarId, Long actualCarId) {
        UpdateCarsRequest updateCarsRequest = UpdateCarsRequest.builder()
                .previousCarId(previousCarId)
                .actualCarId(actualCarId)
                .build();

        updateBookingUpdateCarsProducerService.updateCarsStatus(updateCarsRequest);
    }

    private void unlockCar(String carId) {
        redisTemplate.delete(carId);
    }

}
