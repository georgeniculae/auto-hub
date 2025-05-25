package com.autohub.booking.service;

import com.autohub.booking.entity.Booking;
import com.autohub.booking.mapper.BookingMapper;
import com.autohub.booking.mapper.BookingMapperImpl;
import com.autohub.booking.repository.BookingRepository;
import com.autohub.booking.util.AssertionUtil;
import com.autohub.booking.util.TestUtil;
import com.autohub.dto.booking.BookingRequest;
import com.autohub.dto.common.AuthenticationInfo;
import com.autohub.dto.common.AvailableCarInfo;
import com.autohub.dto.common.BookingClosingDetails;
import com.autohub.dto.common.BookingResponse;
import com.autohub.exception.AutoHubException;
import com.autohub.exception.AutoHubNotFoundException;
import com.autohub.lib.security.ApiKeyAuthenticationToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @InjectMocks
    private BookingService bookingService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CarService carService;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Spy
    private BookingMapper bookingMapper = new BookingMapperImpl();

    @Test
    void findBookingByIdTest_success() {
        Booking booking = TestUtil.getResourceAsJson("/data/Booking.json", Booking.class);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingResponse actualBookingResponse = bookingService.findBookingById(1L);
        assertNotNull(actualBookingResponse);
    }

    @Test
    void findBookingByIdTest_errorOnFindingById() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        AutoHubNotFoundException autoHubNotFoundException =
                assertThrows(AutoHubNotFoundException.class, () -> bookingService.findBookingById(1L));

        assertNotNull(autoHubNotFoundException);
        assertThat(autoHubNotFoundException.getMessage()).contains("Booking with id 1 does not exist");
    }

    @Test
    void findBookingsByLoggedInUserTest_success() {
        Booking booking = TestUtil.getResourceAsJson("/data/Booking.json", Booking.class);

        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        httpServletRequest.addHeader("X-API-KEY", "apikey");
        httpServletRequest.addHeader("X-ROLES", "ROLE_user");

        RequestAttributes servletWebRequest = new ServletWebRequest(httpServletRequest);
        RequestContextHolder.setRequestAttributes(servletWebRequest);

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("user");
        ApiKeyAuthenticationToken apiKeyAuthenticationToken =
                new ApiKeyAuthenticationToken(List.of(simpleGrantedAuthority), "apikey");

        SecurityContextHolder.getContext().setAuthentication(apiKeyAuthenticationToken);

        when(bookingRepository.findBookingsByUser(anyString())).thenReturn(Stream.of(booking));

        List<BookingResponse> bookingsByLoggedInUser = bookingService.findBookingsByLoggedInUser();
        assertFalse(bookingsByLoggedInUser.isEmpty());
    }

    @Test
    void saveBookingTest_success() {
        Booking booking = TestUtil.getResourceAsJson("/data/Booking.json", Booking.class);

        BookingRequest bookingRequest =
                TestUtil.getResourceAsJson("/data/BookingRequest.json", BookingRequest.class);

        AvailableCarInfo availableCarInfo =
                TestUtil.getResourceAsJson("/data/AvailableCarInfo.json", AvailableCarInfo.class);

        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        httpServletRequest.addHeader("X-API-KEY", "apikey");
        httpServletRequest.addHeader("X-ROLES", "ROLE_user");
        httpServletRequest.addHeader("X-EMAIL", "test@email.com");

        RequestAttributes servletWebRequest = new ServletWebRequest(httpServletRequest);
        RequestContextHolder.setRequestAttributes(servletWebRequest);

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("user");
        ApiKeyAuthenticationToken apiKeyAuthenticationToken =
                new ApiKeyAuthenticationToken(List.of(simpleGrantedAuthority), "apikey");

        SecurityContextHolder.getContext().setAuthentication(apiKeyAuthenticationToken);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(anyString(), anyString(), any(Duration.class))).thenReturn(true);
        when(carService.findAvailableCarById(any(AuthenticationInfo.class), anyLong())).thenReturn(availableCarInfo);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingResponse actualBookingResponse = bookingService.saveBooking(bookingRequest);
        assertNotNull(actualBookingResponse);

        verify(bookingMapper).mapEntityToDto(any(Booking.class));
    }

    @Test
    void saveBookingTest_errorOnSavingBooking() {
        BookingRequest bookingRequest =
                TestUtil.getResourceAsJson("/data/BookingRequest.json", BookingRequest.class);

        AvailableCarInfo availableCarInfo =
                TestUtil.getResourceAsJson("/data/AvailableCarInfo.json", AvailableCarInfo.class);

        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        httpServletRequest.addHeader("X-API-KEY", "apikey");
        httpServletRequest.addHeader("X-ROLES", "ROLE_user");
        httpServletRequest.addHeader("X-EMAIL", "test@email.com");

        RequestAttributes servletWebRequest = new ServletWebRequest(httpServletRequest);
        RequestContextHolder.setRequestAttributes(servletWebRequest);

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("user");
        ApiKeyAuthenticationToken apiKeyAuthenticationToken =
                new ApiKeyAuthenticationToken(List.of(simpleGrantedAuthority), "apikey");

        SecurityContextHolder.getContext().setAuthentication(apiKeyAuthenticationToken);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(anyString(), anyString(), any(Duration.class))).thenReturn(true);
        when(carService.findAvailableCarById(any(AuthenticationInfo.class), anyLong())).thenReturn(availableCarInfo);
        when(bookingRepository.save(any(Booking.class))).thenThrow(new RuntimeException("Test"));

        AutoHubException autoHubException =
                assertThrows(AutoHubException.class, () -> bookingService.saveBooking(bookingRequest));

        assertNotNull(autoHubException);
    }

    @Test
    void closeBookingTest_success() {
        Booking booking = TestUtil.getResourceAsJson("/data/Booking.json", Booking.class);

        BookingClosingDetails bookingClosingDetails =
                TestUtil.getResourceAsJson("/data/BookingClosingDetails.json", BookingClosingDetails.class);

        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        httpServletRequest.addHeader("X-API-KEY", "apikey");
        httpServletRequest.addHeader("X-ROLES", "ROLE_user");

        RequestAttributes servletWebRequest = new ServletWebRequest(httpServletRequest);
        RequestContextHolder.setRequestAttributes(servletWebRequest);

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("user");
        ApiKeyAuthenticationToken apiKeyAuthenticationToken =
                new ApiKeyAuthenticationToken(List.of(simpleGrantedAuthority), "apikey");

        SecurityContextHolder.getContext().setAuthentication(apiKeyAuthenticationToken);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        assertDoesNotThrow(() -> bookingService.closeBooking(bookingClosingDetails));
    }

    @Test
    void closeBookingTest_errorOnSavingBooking() {
        Booking booking = TestUtil.getResourceAsJson("/data/Booking.json", Booking.class);

        BookingClosingDetails bookingClosingDetails =
                TestUtil.getResourceAsJson("/data/BookingClosingDetails.json", BookingClosingDetails.class);

        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        httpServletRequest.addHeader("X-API-KEY", "apikey");
        httpServletRequest.addHeader("X-ROLES", "ROLE_user");

        RequestAttributes servletWebRequest = new ServletWebRequest(httpServletRequest);
        RequestContextHolder.setRequestAttributes(servletWebRequest);

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("user");
        ApiKeyAuthenticationToken apiKeyAuthenticationToken =
                new ApiKeyAuthenticationToken(List.of(simpleGrantedAuthority), "apikey");

        SecurityContextHolder.getContext().setAuthentication(apiKeyAuthenticationToken);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenThrow(new RuntimeException("Test"));

        AutoHubException autoHubException =
                assertThrows(AutoHubException.class, () -> bookingService.closeBooking(bookingClosingDetails));

        assertNotNull(autoHubException);
    }

    @Test
    void updateBookingTest_success() {
        Booking booking = TestUtil.getResourceAsJson("/data/Booking.json", Booking.class);
        BookingRequest bookingRequest = TestUtil.getResourceAsJson("/data/BookingRequest.json", BookingRequest.class);

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("user");
        ApiKeyAuthenticationToken apiKeyAuthenticationToken =
                new ApiKeyAuthenticationToken(List.of(simpleGrantedAuthority), "apikey");

        SecurityContextHolder.getContext().setAuthentication(apiKeyAuthenticationToken);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingResponse updatedBookingResponse = bookingService.updateBooking(1L, bookingRequest);
        assertNotNull(updatedBookingResponse);
    }

    @Test
    void updateBookingTest_errorOnSavingBooking() {
        Booking booking = TestUtil.getResourceAsJson("/data/Booking.json", Booking.class);
        BookingRequest bookingRequest = TestUtil.getResourceAsJson("/data/BookingRequest.json", BookingRequest.class);

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("user");
        ApiKeyAuthenticationToken apiKeyAuthenticationToken =
                new ApiKeyAuthenticationToken(List.of(simpleGrantedAuthority), "apikey");

        SecurityContextHolder.getContext().setAuthentication(apiKeyAuthenticationToken);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenThrow(new RuntimeException("Test"));

        AutoHubException autoHubException =
                assertThrows(AutoHubException.class, () -> bookingService.updateBooking(1L, bookingRequest));

        assertNotNull(autoHubException);
    }

    @Test
    void updateBookingTest_updatedCar_success() {
        Booking booking = TestUtil.getResourceAsJson("/data/Booking.json", Booking.class);
        Booking updatedBooking = TestUtil.getResourceAsJson("/data/UpdatedBooking.json", Booking.class);
        BookingRequest bookingRequest =
                TestUtil.getResourceAsJson("/data/UpdatedBookingRequest.json", BookingRequest.class);
        AvailableCarInfo availableCarInfo =
                TestUtil.getResourceAsJson("/data/AvailableCarInfo.json", AvailableCarInfo.class);

        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        httpServletRequest.addHeader("X-API-KEY", "apikey");
        httpServletRequest.addHeader("X-ROLES", "ROLE_user");

        RequestAttributes servletWebRequest = new ServletWebRequest(httpServletRequest);
        RequestContextHolder.setRequestAttributes(servletWebRequest);

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("user");
        ApiKeyAuthenticationToken apiKeyAuthenticationToken =
                new ApiKeyAuthenticationToken(List.of(simpleGrantedAuthority), "apikey");

        SecurityContextHolder.getContext().setAuthentication(apiKeyAuthenticationToken);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(anyString(), anyString(), any(Duration.class))).thenReturn(true);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(carService.findAvailableCarById(any(AuthenticationInfo.class), anyLong())).thenReturn(availableCarInfo);
        when(bookingRepository.save(any(Booking.class))).thenReturn(updatedBooking);

        BookingResponse updatedBookingResponse = bookingService.updateBooking(1L, bookingRequest);
        assertNotNull(updatedBookingResponse);
    }

    @Test
    void calculateAllAmountSpentByUserTest_success() {
        MockHttpServletRequest httpServletRequest = new MockHttpServletRequest();
        httpServletRequest.addHeader("X-USERNAME", "user");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(httpServletRequest));

        when(bookingRepository.sumAmountSpentByLoggedInUser(anyString())).thenReturn(BigDecimal.valueOf(500));

        BigDecimal amount = bookingService.getAmountSpentByLoggedInUser();
        assertEquals(BigDecimal.valueOf(500), amount);
    }

    @Test
    void getSumOfAllBookingAmountTest_success() {
        when(bookingRepository.sumAllBookingsAmount()).thenReturn(BigDecimal.valueOf(500));

        BigDecimal sumOfAllBookingAmount = bookingService.getSumOfAllBookingAmount();
        assertEquals(BigDecimal.valueOf(500), sumOfAllBookingAmount);
    }

    @Test
    void countCustomersWithBookingsTest_success() {
        when(bookingRepository.countUsersWithBookings()).thenReturn(1L);

        Long bookings = bookingService.countUsersWithBookings();
        assertEquals(1, bookings);
    }

    @Test
    void findBookingByDateOfBookingTest_success() {
        Booking booking = TestUtil.getResourceAsJson("/data/Booking.json", Booking.class);

        when(bookingRepository.findByDateOfBooking(LocalDate.of(2099, Month.FEBRUARY, 20)))
                .thenReturn(Optional.of(booking));

        BookingResponse bookingResponse = bookingService.findBookingByDateOfBooking("2099-02-20");
        AssertionUtil.assertBooking(booking, bookingResponse);
    }

    @Test
    void findBookingByDateOfBookingTest_errorOnFindingByDateOfBooking() {
        when(bookingRepository.findByDateOfBooking(LocalDate.of(2099, Month.FEBRUARY, 20)))
                .thenReturn(Optional.empty());

        AutoHubNotFoundException autoHubNotFoundException =
                assertThrows(AutoHubNotFoundException.class, () -> bookingService.findBookingByDateOfBooking("2099-02-20"));

        assertNotNull(autoHubNotFoundException);
        assertEquals("Booking from date: 2099-02-20 does not exist", autoHubNotFoundException.getReason());
    }

    @Test
    void deleteBookingByIdTest_success() {
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("user");
        ApiKeyAuthenticationToken apiKeyAuthenticationToken =
                new ApiKeyAuthenticationToken(List.of(simpleGrantedAuthority), "apikey");

        SecurityContextHolder.getContext().setAuthentication(apiKeyAuthenticationToken);

        when(bookingRepository.existsInProgressBookingsByCustomerUsername(anyString())).thenReturn(false);
        doNothing().when(bookingRepository).deleteByCustomerUsername(anyString());

        assertDoesNotThrow(() -> bookingService.deleteBookingByCustomerUsername("user"));
    }

    @Test
    void deleteBookingByIdTest_error_bookingsInProgress() {
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("user");
        ApiKeyAuthenticationToken apiKeyAuthenticationToken =
                new ApiKeyAuthenticationToken(List.of(simpleGrantedAuthority), "apikey");

        SecurityContextHolder.getContext().setAuthentication(apiKeyAuthenticationToken);

        when(bookingRepository.existsInProgressBookingsByCustomerUsername(anyString())).thenReturn(true);

        AutoHubException autoHubException =
                assertThrows(AutoHubException.class, () -> bookingService.deleteBookingByCustomerUsername("user"));

        assertNotNull(autoHubException);
    }

}
