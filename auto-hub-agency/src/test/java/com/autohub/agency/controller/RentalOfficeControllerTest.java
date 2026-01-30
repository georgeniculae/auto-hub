package com.autohub.agency.controller;

import com.autohub.agency.service.RentalOfficeService;
import com.autohub.agency.util.TestUtil;
import com.autohub.dto.agency.RentalOfficeRequest;
import com.autohub.dto.agency.RentalOfficeResponse;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = RentalOfficeController.class)
@AutoConfigureMockMvc
@EnableWebMvc
class RentalOfficeControllerTest {

    private static final String PATH = "/rental-offices";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RentalOfficeService rentalOfficeService;

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void findAllRentalOfficesTest_success() throws Exception {
        RentalOfficeResponse rentalOfficeResponse =
                TestUtil.getResourceAsJson("/data/RentalOfficeResponse.json", RentalOfficeResponse.class);

        when(rentalOfficeService.findAllRentalOffices()).thenReturn(List.of(rentalOfficeResponse));

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser
    void findAllRentalOfficesTest_unauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void findRentalOfficeByIdTest_success() throws Exception {
        RentalOfficeResponse rentalOfficeRequest =
                TestUtil.getResourceAsJson("/data/RentalOfficeResponse.json", RentalOfficeResponse.class);

        when(rentalOfficeService.findRentalOfficeById(anyLong())).thenReturn(rentalOfficeRequest);

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get(PATH + "/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser
    void findRentalOfficeByIdTest_unauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(PATH + "/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void findRentalOfficesByFilterTest_success() throws Exception {
        RentalOfficeResponse rentalOfficeResponse =
                TestUtil.getResourceAsJson("/data/RentalOfficeResponse.json", RentalOfficeResponse.class);

        when(rentalOfficeService.findRentalOfficeByFilter(anyString())).thenReturn(List.of(rentalOfficeResponse));

        MockHttpServletResponse response = mockMvc.perform(get(PATH + "/filter/{filter}", "filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser
    void findRentalOfficesByNameTest_unauthorized() throws Exception {
        mockMvc.perform(get(PATH + "/name/{name}", "name")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void countRentalOfficesTest_success() throws Exception {
        when(rentalOfficeService.countRentalOffices()).thenReturn(1L);

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get(PATH + "/count")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser
    void countRentalOfficesTest_unauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(PATH + "/count")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void addRentalOfficeTest_success() throws Exception {
        RentalOfficeRequest rentalOfficeRequest =
                TestUtil.getResourceAsJson("/data/RentalOfficeRequest.json", RentalOfficeRequest.class);
        RentalOfficeResponse rentalOfficeResponse =
                TestUtil.getResourceAsJson("/data/RentalOfficeResponse.json", RentalOfficeResponse.class);

        String valueAsString = TestUtil.writeValueAsString(rentalOfficeRequest);

        when(rentalOfficeService.saveRentalOffice(any(RentalOfficeRequest.class))).thenReturn(rentalOfficeResponse);

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.post(PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(valueAsString))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser
    void addRentalOfficeTest_unauthorized() throws Exception {
        RentalOfficeResponse rentalOfficeResponse =
                TestUtil.getResourceAsJson("/data/RentalOfficeRequest.json", RentalOfficeResponse.class);

        String valueAsString = TestUtil.writeValueAsString(rentalOfficeResponse);

        mockMvc.perform(MockMvcRequestBuilders.post(PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(valueAsString))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    void addRentalOfficeTest_forbidden() throws Exception {
        RentalOfficeResponse rentalOfficeResponse =
                TestUtil.getResourceAsJson("/data/RentalOfficeResponse.json", RentalOfficeResponse.class);

        String valueAsString = TestUtil.writeValueAsString(rentalOfficeResponse);

        mockMvc.perform(MockMvcRequestBuilders.post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(valueAsString))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void updateRentalOfficeTest_success() throws Exception {
        RentalOfficeRequest rentalOfficeRequest =
                TestUtil.getResourceAsJson("/data/RentalOfficeRequest.json", RentalOfficeRequest.class);
        RentalOfficeResponse rentalOfficeResponse =
                TestUtil.getResourceAsJson("/data/RentalOfficeResponse.json", RentalOfficeResponse.class);

        String valueAsString = TestUtil.writeValueAsString(rentalOfficeRequest);

        when(rentalOfficeService.saveRentalOffice(any(RentalOfficeRequest.class))).thenReturn(rentalOfficeResponse);

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.put(PATH + "/{id}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(valueAsString))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser
    void updateRentalOfficeTest_unauthorized() throws Exception {
        RentalOfficeResponse rentalOfficeRequest =
                TestUtil.getResourceAsJson("/data/RentalOfficeResponse.json", RentalOfficeResponse.class);

        String valueAsString = TestUtil.writeValueAsString(rentalOfficeRequest);

        mockMvc.perform(MockMvcRequestBuilders.put(PATH + "/{id}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(valueAsString))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    void updateRentalOfficeTest_forbidden() throws Exception {
        RentalOfficeResponse rentalOfficeResponse =
                TestUtil.getResourceAsJson("/data/RentalOfficeResponse.json", RentalOfficeResponse.class);

        String valueAsString = TestUtil.writeValueAsString(rentalOfficeResponse);

        mockMvc.perform(MockMvcRequestBuilders.put(PATH + "/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(valueAsString))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void deleteRentalOfficeByIdTest_success() throws Exception {
        doNothing().when(rentalOfficeService).deleteRentalOfficeById(anyLong());

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.delete(PATH + "/{id}", 1L)
                        .with(csrf())
                        .with(user("admin").password("admin").roles("ADMIN")))
                .andExpect(status().isNoContent())
                .andReturn()
                .getResponse();

        assertNotNull(response.getContentAsString());
    }

    @Test
    @WithAnonymousUser
    void deleteRentalOfficeByIdTest_unauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(PATH + "/{id}", 1L)
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    void deleteRentalOfficeByIdTest_forbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(PATH + "/{id}", 1L)
                        .with(user("admin").password("admin").roles("ADMIN")))
                .andExpect(status().isForbidden());
    }

}
