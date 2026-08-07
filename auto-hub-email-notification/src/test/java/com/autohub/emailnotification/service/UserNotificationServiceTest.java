package com.autohub.emailnotification.service;

import com.autohub.dto.common.InvoiceResponse;
import com.autohub.emailnotification.util.TestUtil;
import com.autohub.exception.AutoHubException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserNotificationServiceTest {

    private static final byte[] PDF = "%PDF-1.4 content".getBytes(StandardCharsets.UTF_8);

    @InjectMocks
    private UserNotificationService userNotificationService;

    @Mock
    private PdfService pdfService;

    @Mock
    private InvoicePdfStorageService invoicePdfStorageService;

    @Mock
    private EmailService emailService;

    private InvoiceResponse invoice() {
        return TestUtil.getResourceAsJson("/data/InvoiceResponse.json", InvoiceResponse.class);
    }

    @Test
    void notifyCustomerTest_generatesStoresThenSends() {
        InvoiceResponse invoiceResponse = invoice();

        when(pdfService.generatePdf(any(InvoiceResponse.class))).thenReturn(PDF);

        userNotificationService.notifyCustomer(invoiceResponse);

        InOrder inOrder = inOrder(pdfService, invoicePdfStorageService, emailService);
        inOrder.verify(pdfService).generatePdf(invoiceResponse);
        inOrder.verify(invoicePdfStorageService).savePdf(invoiceResponse, PDF);
        inOrder.verify(emailService).sendEmail(anyString(), any(InvoiceResponse.class), any(byte[].class));
    }

    @Test
    void notifyCustomerTest_toleratesSendFailure() {
        InvoiceResponse invoiceResponse = invoice();

        when(pdfService.generatePdf(any(InvoiceResponse.class))).thenReturn(PDF);
        doThrow(new AutoHubException("smtp down"))
                .when(emailService).sendEmail(anyString(), any(InvoiceResponse.class), any(byte[].class));

        assertDoesNotThrow(() -> userNotificationService.notifyCustomer(invoiceResponse));

        verify(invoicePdfStorageService).savePdf(invoiceResponse, PDF);
    }

    @Test
    void notifyCustomerTest_propagatesStorageFailure() {
        InvoiceResponse invoiceResponse = invoice();

        when(pdfService.generatePdf(any(InvoiceResponse.class))).thenReturn(PDF);
        doThrow(new AutoHubException("db down"))
                .when(invoicePdfStorageService).savePdf(any(InvoiceResponse.class), any(byte[].class));

        assertThrows(AutoHubException.class, () -> userNotificationService.notifyCustomer(invoiceResponse));

        verify(emailService, never()).sendEmail(anyString(), any(InvoiceResponse.class), any(byte[].class));
    }

    @Test
    void notifyCustomerTest_propagatesGenerationFailure() {
        InvoiceResponse invoiceResponse = invoice();

        when(pdfService.generatePdf(any(InvoiceResponse.class)))
                .thenThrow(new AutoHubException("render failed"));

        assertThrows(AutoHubException.class, () -> userNotificationService.notifyCustomer(invoiceResponse));

        verify(invoicePdfStorageService, never()).savePdf(any(InvoiceResponse.class), any(byte[].class));
        verify(emailService, never()).sendEmail(anyString(), any(InvoiceResponse.class), any(byte[].class));
    }

}
