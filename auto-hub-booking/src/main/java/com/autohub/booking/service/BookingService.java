package com.autohub.booking.service;

import com.autohub.booking.entity.Booking;
import com.autohub.booking.entity.BookingStatus;
import com.autohub.booking.mapper.BookingMapper;
import com.autohub.booking.repository.BookingRepository;
import com.autohub.dto.booking.BookingRequest;
import com.autohub.dto.common.AuthenticationInfo;
import com.autohub.dto.common.AvailableCarInfo;
import com.autohub.dto.common.BookingClosingDetails;
import com.autohub.dto.common.BookingResponse;
import com.autohub.exception.AutoHubException;
import com.autohub.exception.AutoHubNotFoundException;
import com.autohub.exception.AutoHubResponseStatusException;
import com.autohub.lib.exceptionhandling.ExceptionUtil;
import com.autohub.lib.util.AuthenticationUtil;
import com.autohub.lib.util.HttpRequestUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.retry.RetryListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService implements RetryListener {

    private static final String LOCKED = "Locked";
    private final BookingRepository bookingRepository;
    private final CarService carService;
    private final RedisTemplate<String, String> redisTemplate;
    private final BookingMapper bookingMapper;

    @Transactional(readOnly = true)
    public List<BookingResponse> findAllBookings() {
        try (Stream<Booking> bookingStream = bookingRepository.findAllBookings()) {
            return bookingStream.map(bookingMapper::mapEntityToDto).toList();
        }
    }

    public BookingResponse findBookingById(Long id) {
        Booking booking = findEntityById(id);

        return bookingMapper.mapEntityToDto(booking);
    }

    public Long countBookings() {
        return bookingRepository.count();
    }

    public Long countUsersWithBookings() {
        return bookingRepository.countUsersWithBookings();
    }

    public BookingResponse findBookingByDateOfBooking(String searchString) {
        Booking booking = bookingRepository.findByDateOfBooking(LocalDate.parse(searchString))
                .orElseThrow(() -> new AutoHubNotFoundException("Booking from date: " + searchString + " does not exist"));

        return bookingMapper.mapEntityToDto(booking);
    }

    public Long countByLoggedInUser() {
        return bookingRepository.countByCustomerUsername(HttpRequestUtil.extractUsername());
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> findBookingsByLoggedInUser() {
        String username = HttpRequestUtil.extractUsername();

        try (Stream<Booking> bookingStream = bookingRepository.findBookingsByUser(username)) {
            return bookingStream.map(bookingMapper::mapEntityToDto).toList();
        }
    }

    public BigDecimal getAmountSpentByLoggedInUser() {
        return bookingRepository.sumAmountSpentByLoggedInUser(HttpRequestUtil.extractUsername());
    }

    public BigDecimal getSumOfAllBookingAmount() {
        return bookingRepository.sumAllBookingsAmount();
    }

    public LocalDate getCurrentDate() {
        return LocalDate.now();
    }

    public BookingResponse saveBooking(BookingRequest newBookingRequest) {
        try {
            validateBookingDates(newBookingRequest);
            AuthenticationInfo authenticationInfo = AuthenticationUtil.getAuthenticationInfo();
            lockCar(newBookingRequest.carId().toString());

            AvailableCarInfo availableCarInfo = carService.findAvailableCarById(authenticationInfo, newBookingRequest.carId());
            Booking createdBooking = bookingMapper.getNewBooking(newBookingRequest, availableCarInfo, authenticationInfo);

            Booking savedBooking = bookingRepository.save(createdBooking);

            return bookingMapper.mapEntityToDto(savedBooking);
        } catch (Exception e) {
            log.error("Error occurred while saving booking: {}", e.getMessage());

            throw ExceptionUtil.handleException(e);
        }
    }

    public BookingResponse updateBooking(Long id, BookingRequest updatedBookingRequest) {
        try {
            validateBookingDates(updatedBookingRequest);
            Booking savedUpdatedBooking = processUpdatedBooking(id, updatedBookingRequest);

            return bookingMapper.mapEntityToDto(savedUpdatedBooking);
        } catch (Exception e) {
            log.error("Error occurred while updating booking: {}", e.getMessage());

            throw ExceptionUtil.handleException(e);
        }
    }

    public void closeBooking(BookingClosingDetails bookingClosingDetails) {
        try {
            Booking existingBooking = findEntityById(bookingClosingDetails.bookingId());
            existingBooking.setStatus(BookingStatus.CLOSED);
            existingBooking.setReturnBranchId(bookingClosingDetails.returnBranchId());
            bookingRepository.save(existingBooking);
        } catch (Exception e) {
            log.error("Error occurred while closing booking: {}", e.getMessage());

            throw new AutoHubException(e.getMessage());
        }
    }

    @Transactional
    public void deleteBookingByCustomerUsername(String username) {
        boolean existsInProgressBookingsByCustomer = bookingRepository.existsInProgressBookingsByCustomerUsername(username);

        if (existsInProgressBookingsByCustomer) {
            throw new AutoHubException("There are bookings in progress for this user");
        }

        bookingRepository.deleteByCustomerUsername(username);
    }

    private void validateBookingDates(BookingRequest newBookingRequest) {
        LocalDate dateFrom = newBookingRequest.dateFrom();
        LocalDate dateTo = newBookingRequest.dateTo();
        LocalDate currentDate = LocalDate.now();

        if (dateFrom.isBefore(currentDate) || dateTo.isBefore(currentDate)) {
            throw new AutoHubResponseStatusException(HttpStatus.BAD_REQUEST, "A date of booking cannot be in the past");
        }

        if (dateFrom.isAfter(dateTo)) {
            throw new AutoHubResponseStatusException(HttpStatus.BAD_REQUEST, "Date from is after date to");
        }
    }

    private Booking findEntityById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new AutoHubNotFoundException("Booking with id " + id + " does not exist"));
    }

    private Booking processUpdatedBooking(Long id, BookingRequest updatedBookingRequest) {
        Booking existingBooking = findEntityById(id);

        final long existingCarId = existingBooking.getActualCarId();
        existingBooking.setAmount(getAmount(updatedBookingRequest, existingBooking.getRentalCarPrice()));
        existingBooking.setDateFrom(updatedBookingRequest.dateFrom());
        existingBooking.setDateTo(updatedBookingRequest.dateTo());

        Optional<Booking> bookingWithChangedCar =
                processBookingWhenCarIsChanged(updatedBookingRequest, existingCarId, existingBooking);

        Booking processedBooking = bookingWithChangedCar.orElse(existingBooking);

        return bookingRepository.save(processedBooking);
    }

    private Optional<Booking> processBookingWhenCarIsChanged(BookingRequest updatedBookingRequest,
                                                             long existingCarId,
                                                             Booking existingBooking) {
        long newCarId = updatedBookingRequest.carId();

        if (existingCarId == newCarId) {
            return Optional.empty();
        }

        lockCar(updatedBookingRequest.carId().toString());
        AuthenticationInfo authenticationInfo = AuthenticationUtil.getAuthenticationInfo();
        AvailableCarInfo availableCarInfo = carService.findAvailableCarById(authenticationInfo, newCarId);

        existingBooking.setAmount(getAmount(updatedBookingRequest, availableCarInfo.amount()));
        existingBooking.setActualCarId(availableCarInfo.id());
        existingBooking.setPreviousCarId(existingCarId);
        existingBooking.setRentalBranchId(availableCarInfo.actualBranchId());
        Booking savedIntermediateBooking = bookingRepository.save(existingBooking);

        return Optional.of(savedIntermediateBooking);
    }

    private void lockCar(String carId) {
        Boolean isUsed = redisTemplate.opsForValue().setIfAbsent(carId, LOCKED, Duration.ofSeconds(30));

        if (Boolean.FALSE.equals(isUsed)) {
            throw new AutoHubResponseStatusException(HttpStatus.BAD_REQUEST, "Car is unavailable");
        }
    }

    private BigDecimal getAmount(BookingRequest bookingRequest, BigDecimal amount) {
        LocalDate dateFrom = bookingRequest.dateFrom();
        LocalDate dateTo = bookingRequest.dateTo();

        int bookingDays = Period.between(dateFrom, dateTo).getDays();

        if (bookingDays == 0) {
            return amount;
        }

        return amount.multiply(BigDecimal.valueOf(bookingDays));
    }

}
