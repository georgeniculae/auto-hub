package com.autohub.emailnotification.controller;

import com.autohub.emailnotification.service.InvoicePdfStorageService;
import com.autohub.exception.AutoHubNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = InvoiceDocumentController.class)
@AutoConfigureMockMvc(addFilters = false)
class InvoiceDocumentControllerTest {

    private static final String PATH = "/invoices";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InvoicePdfStorageService invoicePdfStorageService;

    @Test
    @WithMockUser(username = "admin", roles = "user")
    void getInvoiceDocumentTest_success() throws Exception {
        byte[] pdf = "%PDF-1.4 content".getBytes(StandardCharsets.UTF_8);

        when(invoicePdfStorageService.retrievePdf(anyLong())).thenReturn(pdf);

        mockMvc.perform(get(PATH + "/{invoiceId}/document", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"invoice-1.pdf\""))
                .andExpect(content().bytes(pdf));
    }

    @Test
    @WithMockUser(username = "admin", roles = "user")
    void getInvoiceDocumentTest_propagatesNotFound() throws Exception {
        when(invoicePdfStorageService.retrievePdf(anyLong()))
                .thenThrow(new AutoHubNotFoundException("PDF not found for invoice: 999"));

        mockMvc.perform(get(PATH + "/{invoiceId}/document", 999L))
                .andExpect(result -> assertInstanceOf(
                        AutoHubNotFoundException.class,
                        result.getResolvedException()
                ));
    }

}
