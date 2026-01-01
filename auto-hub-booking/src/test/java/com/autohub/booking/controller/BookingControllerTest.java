package com.autohub.booking.controller;

import com.autohub.booking.service.BookingService;
import com.autohub.booking.util.TestUtil;
import com.autohub.dto.booking.BookingRequest;
import com.autohub.dto.common.BookingResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = BookingController.class)
@AutoConfigureMockMvc
@EnableWebMvc
class BookingControllerTest {

    private static final String PATH = "/bookings";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookingService bookingService;

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void findAllBookingTest_success() throws Exception {
        BookingResponse bookingResponse =
                TestUtil.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);

        when(bookingService.findAllBookings()).thenReturn(List.of(bookingResponse));

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get(PATH + "/list")
                        .contextPath(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser
    void findAllBookingTest_unauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(PATH)
                        .contextPath(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void findBookingByIdTest_success() throws Exception {
        BookingResponse bookingResponse =
                TestUtil.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);

        when(bookingService.findBookingById(anyLong())).thenReturn(bookingResponse);

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get(PATH + "/{id}", 1L)
                        .contextPath(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser()
    void findBookingByIdTest_unauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(PATH + "/{id}", 1)
                        .contextPath(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void getBookingsByLoggedInUserTest_success() throws Exception {
        BookingResponse bookingResponse =
                TestUtil.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);

        when(bookingService.findBookingsByLoggedInUser()).thenReturn(List.of(bookingResponse));

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get(PATH + "/by-logged-in-user")
                        .contextPath(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser
    void getBookingsByLoggedInUserTest_unauthorized() throws Exception {
        BookingResponse bookingResponse =
                TestUtil.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);

        when(bookingService.findBookingsByLoggedInUser()).thenReturn(List.of(bookingResponse));

        mockMvc.perform(MockMvcRequestBuilders.get(PATH + "/by-logged-in-user")
                        .contextPath(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void countBookingsTest_success() throws Exception {
        when(bookingService.countBookings()).thenReturn(1L);

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get(PATH + "/count")
                        .contextPath(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser
    void countBookingsTest_unauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(PATH + "/count")
                        .contextPath(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void countByLoggedInUserTest_success() throws Exception {
        when(bookingService.countBookings()).thenReturn(1L);

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get(PATH + "/count-by-current-user")
                        .contextPath(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser
    void countByLoggedInUserTest_unauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(PATH + "/count-by-current-user")
                        .contextPath(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void getCurrentDateTest_success() throws Exception {
        when(bookingService.countBookings()).thenReturn(1L);

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get(PATH + "/current-date")
                        .contextPath(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser
    void getCurrentDateTest_unauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(PATH + "/current-date")
                        .contextPath(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void addBookingTest_success() throws Exception {
        BookingResponse bookingResponse =
                TestUtil.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);

        String content = TestUtil.writeValueAsString(bookingResponse);

        when(bookingService.saveBooking(any(BookingRequest.class))).thenReturn(bookingResponse);

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.post(PATH + "/new")
                        .contextPath(PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isAccepted())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser
    void addBookingTest_unauthorized() throws Exception {
        BookingResponse bookingResponse =
                TestUtil.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);

        String content = TestUtil.writeValueAsString(bookingResponse);

        mockMvc.perform(MockMvcRequestBuilders.post(PATH)
                        .contextPath(PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    void addBookingTest_forbidden() throws Exception {
        BookingResponse bookingResponse =
                TestUtil.getResourceAsJson("/data/BookingRequest.json", BookingResponse.class);

        String content = TestUtil.writeValueAsString(bookingResponse);

        mockMvc.perform(MockMvcRequestBuilders.post(PATH)
                        .contextPath(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void updateBookingTest_success() throws Exception {
        BookingResponse bookingResponse =
                TestUtil.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);

        String content = TestUtil.writeValueAsString(bookingResponse);

        when(bookingService.updateBooking(anyLong(), any(BookingRequest.class)))
                .thenReturn(bookingResponse);

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.put(PATH + "/{id}", 1L)
                        .contextPath(PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isAccepted())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser
    void updateBookingTest_unauthorized() throws Exception {
        BookingResponse bookingResponse =
                TestUtil.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);

        String content = TestUtil.writeValueAsString(bookingResponse);

        mockMvc.perform(MockMvcRequestBuilders.put(PATH + "/edit/{id}", 1L)
                        .contextPath(PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    void updateBookingTest_forbidden() throws Exception {
        BookingResponse bookingResponse =
                TestUtil.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);

        String content = TestUtil.writeValueAsString(bookingResponse);

        mockMvc.perform(MockMvcRequestBuilders.put(PATH + "/edit/{id}", 1L)
                        .contextPath(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void deleteBookingByUnauthorizedTest_success() throws Exception {
        doNothing().when(bookingService).deleteBookingByCustomerUsername(anyString());

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.delete(PATH + "/{id}", 1L)
                        .contextPath(PATH)
                        .with(csrf()))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser
    void deleteBookingByIdTest_forbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(PATH + "/{id}", 1L)
                        .contextPath(PATH))
                .andExpect(status().isForbidden());
    }

}
