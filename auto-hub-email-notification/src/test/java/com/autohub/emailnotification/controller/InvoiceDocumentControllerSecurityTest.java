package com.autohub.emailnotification.controller;

import com.autohub.emailnotification.security.SecurityConfig;
import com.autohub.emailnotification.service.InvoicePdfStorageService;
import com.autohub.lib.security.AuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {
        InvoiceDocumentController.class,
        SecurityConfig.class,
        InvoiceDocumentControllerSecurityTest.TestSecurityConfig.class
})
@AutoConfigureMockMvc
@EnableWebMvc
class InvoiceDocumentControllerSecurityTest {

    private static final String PATH = "/invoices";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InvoicePdfStorageService invoicePdfStorageService;

    @TestConfiguration
    static class TestSecurityConfig {

        @Bean
        AuthenticationManager authenticationManager() {
            return mock(AuthenticationManager.class);
        }

        @Bean
        AuthenticationProvider authenticationProvider() {
            return mock(AuthenticationProvider.class);
        }

        @Bean
        AuthenticationFilter authenticationFilter(AuthenticationManager authenticationManager) {
            return new AuthenticationFilter(authenticationManager);
        }

    }

    @Test
    @WithMockUser(username = "user", roles = "user")
    void getInvoiceDocumentTest_success() throws Exception {
        byte[] pdf = "%PDF-1.4 content".getBytes(StandardCharsets.UTF_8);

        when(invoicePdfStorageService.retrievePdf(anyLong())).thenReturn(pdf);

        mockMvc.perform(get(PATH + "/{invoiceId}/document", 1L))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void getInvoiceDocumentTest_forbidden() throws Exception {
        // Authenticated but without the "user" role required by @PreAuthorize("hasRole('user')").
        mockMvc.perform(get(PATH + "/{invoiceId}/document", 1L))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void getInvoiceDocumentTest_unauthenticated_forbidden() throws Exception {
        // 403, not 401: SecurityConfig disables httpBasic and formLogin without registering an
        // AuthenticationEntryPoint, so Spring Security falls back to Http403ForbiddenEntryPoint.
        mockMvc.perform(get(PATH + "/{invoiceId}/document", 1L))
                .andExpect(status().isForbidden());
    }

}
