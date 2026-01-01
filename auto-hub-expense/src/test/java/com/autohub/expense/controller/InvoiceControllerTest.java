package com.autohub.expense.controller;

import com.autohub.dto.common.InvoiceResponse;
import com.autohub.dto.expense.InvoiceRequest;
import com.autohub.expense.service.InvoiceService;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = InvoiceController.class)
@AutoConfigureMockMvc
@EnableWebMvc
class InvoiceControllerTest {

    private static final String PATH = "/invoices";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InvoiceService invoiceService;

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void findAllInvoicesTest_success() throws Exception {
        InvoiceResponse invoiceResponse =
                TestUtil.getResourceAsJson("/data/InvoiceResponse.json", InvoiceResponse.class);

        when(invoiceService.findAllInvoices()).thenReturn(List.of(invoiceResponse));

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
    void findAllInvoicesTest_unauthorized() throws Exception {
        mockMvc.perform(get(PATH)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void findAllActiveInvoicesTest_success() throws Exception {
        InvoiceResponse invoiceResponse =
                TestUtil.getResourceAsJson("/data/InvoiceResponse.json", InvoiceResponse.class);

        when(invoiceService.findAllActiveInvoices()).thenReturn(List.of(invoiceResponse));

        MockHttpServletResponse response = mockMvc.perform(get(PATH + "/active")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertNotNull(response);
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void findAllActiveInvoicesTest_unauthorized() throws Exception {
        mockMvc.perform(get(PATH + "/active")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void findInvoiceByIdTest_success() throws Exception {
        InvoiceResponse invoiceResponse =
                TestUtil.getResourceAsJson("/data/InvoiceResponse.json", InvoiceResponse.class);

        when(invoiceService.findInvoiceById(anyLong())).thenReturn(invoiceResponse);

        MockHttpServletResponse response = mockMvc.perform(get(PATH + "/{id}", 1L)
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
    void findInvoiceByIdTest_unauthorized() throws Exception {
        mockMvc.perform(get(PATH + "/{id}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void findAllInvoicesByCustomerUsernameTest_success() throws Exception {
        InvoiceResponse invoiceResponse =
                TestUtil.getResourceAsJson("/data/InvoiceResponse.json", InvoiceResponse.class);

        when(invoiceService.findAllInvoicesByCustomerUsername(anyString())).thenReturn(List.of(invoiceResponse));

        MockHttpServletResponse response = mockMvc.perform(get(PATH + "/by-customer/{customerUsername}", "user")
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
    void findAllInvoicesByCustomerUsernameTest_unauthorized() throws Exception {
        mockMvc.perform(get(PATH + "/by-customer/{customerUsername}", "user")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void countInvoicesTest_success() throws Exception {
        when(invoiceService.countInvoices()).thenReturn(1L);

        MockHttpServletResponse response = mockMvc.perform(get(PATH + "/count")
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
    void countInvoicesTest_unauthorized() throws Exception {
        mockMvc.perform(get(PATH + "/count")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void countActiveInvoicesTest_success() throws Exception {
        when(invoiceService.countAllActiveInvoices()).thenReturn(1L);

        MockHttpServletResponse response = mockMvc.perform(get(PATH + "/active-count")
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
    void countActiveInvoicesTest_unauthorized() throws Exception {
        mockMvc.perform(get(PATH + "/active-count")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin", password = "admin", roles = "ADMIN")
    void closeInvoiceTest_success() throws Exception {
        InvoiceRequest invoiceRequest =
                TestUtil.getResourceAsJson("/data/InvoiceRequest.json", InvoiceRequest.class);

        String content = TestUtil.writeValueAsString(invoiceRequest);

        MockHttpServletResponse response = mockMvc.perform(put(PATH + "/{id}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isAccepted())
                .andReturn()
                .getResponse();

        assertNotNull(response);
    }

    @Test
    @WithAnonymousUser
    void closeInvoiceTest_unauthorized() throws Exception {
        InvoiceRequest invoiceRequest =
                TestUtil.getResourceAsJson("/data/InvoiceRequest.json", InvoiceRequest.class);

        String content = TestUtil.writeValueAsString(invoiceRequest);

        mockMvc.perform(put(PATH + "/{id}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isUnauthorized());
    }

}
