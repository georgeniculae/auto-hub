package com.autohub.emailnotification.service;

import com.autohub.emailnotification.config.TemplateEngineConfig;
import com.autohub.emailnotification.util.TestUtil;
import com.autohub.dto.common.InvoiceResponse;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PdfServiceTest {

    private final PdfService pdfService = new PdfService(new TemplateEngineConfig().templateEngine());

    @Test
    void generatePdfTest_producesValidPdfBytes() {
        InvoiceResponse invoiceResponse =
                TestUtil.getResourceAsJson("/data/InvoiceResponse.json", InvoiceResponse.class);

        byte[] pdf = pdfService.generatePdf(invoiceResponse);

        assertNotNull(pdf);
        assertTrue(pdf.length > 0, "PDF-ul nu trebuie sa fie gol");
        assertTrue(
                new String(pdf, 0, 5, StandardCharsets.US_ASCII).startsWith("%PDF-"),
                "octetii trebuie sa inceapa cu semnatura %PDF-"
        );
    }

}
