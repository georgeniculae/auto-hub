package com.autohub.expense.controller;

import com.autohub.dto.expense.RevenueResponse;
import com.autohub.expense.service.RevenueService;
import com.autohub.expense.util.TestUtil;
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
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = RevenueController.class)
@AutoConfigureMockMvc
@EnableWebMvc
class RevenueControllerTest {

    private static final String PATH = "/revenues";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RevenueService revenueService;

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void findAllRevenuesTest_success() throws Exception {
        RevenueResponse revenueResponse =
                TestUtil.getResourceAsJson("/data/RevenueResponse.json", RevenueResponse.class);

        when(revenueService.findAllRevenues()).thenReturn(List.of(revenueResponse));

        MockHttpServletResponse response = mockMvc.perform(get(PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response);
    }

    @Test
    @WithAnonymousUser
    void findAllRevenuesTest_unauthorized() throws Exception {
        RevenueResponse revenueResponse =
                TestUtil.getResourceAsJson("/data/RevenueResponse.json", RevenueResponse.class);

        when(revenueService.findAllRevenues()).thenReturn(List.of(revenueResponse));

        mockMvc.perform(get(PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void findAllInvoicesTest_success() throws Exception {
        when(revenueService.getTotalAmount()).thenReturn(BigDecimal.valueOf(550));

        MockHttpServletResponse response = mockMvc.perform(get(PATH + "/total")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response);
    }

    @Test
    @WithAnonymousUser
    void findAllInvoicesTest_unauthorized() throws Exception {
        when(revenueService.getTotalAmount()).thenReturn(BigDecimal.valueOf(550.0));

        mockMvc.perform(get(PATH + "/total")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void findRevenuesByDateTest_success() throws Exception {
        RevenueResponse revenueResponse =
                TestUtil.getResourceAsJson("/data/RevenueResponse.json", RevenueResponse.class);

        when(revenueService.findRevenuesByDate(any(LocalDate.class))).thenReturn(List.of(revenueResponse));

        MockHttpServletResponse response = mockMvc.perform(get(PATH + "/{date}", "2099-02-20")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response);
    }

    @Test
    @WithAnonymousUser
    void findRevenuesByDateTest_unauthorized() throws Exception {
        RevenueResponse revenueResponse =
                TestUtil.getResourceAsJson("/data/RevenueResponse.json", RevenueResponse.class);

        when(revenueService.findRevenuesByDate(any(LocalDate.class))).thenReturn(List.of(revenueResponse));

        mockMvc.perform(get(PATH + "/{date}", "2099-02-20")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse();
    }

}
