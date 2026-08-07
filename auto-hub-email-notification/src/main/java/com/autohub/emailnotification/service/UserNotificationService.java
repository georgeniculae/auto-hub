package com.autohub.emailnotification.service;

import com.autohub.dto.common.InvoiceResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserNotificationService {

    private final PdfService pdfService;
    private final InvoicePdfStorageService invoicePdfStorageService;
    private final EmailService emailService;

    public void notifyCustomer(InvoiceResponse invoiceResponse) {
        byte[] pdfBytes = pdfService.generatePdf(invoiceResponse);

        invoicePdfStorageService.savePdf(invoiceResponse, pdfBytes);

        try {
            emailService.sendEmail(invoiceResponse.customerEmail(), invoiceResponse, pdfBytes);

            log.info("Invoice {} emailed to {}", invoiceResponse.id(), invoiceResponse.customerEmail());
        } catch (Exception e) {
            log.error("Failed to email invoice {}: {}", invoiceResponse.id(), e.getMessage(), e);
        }
    }

}
