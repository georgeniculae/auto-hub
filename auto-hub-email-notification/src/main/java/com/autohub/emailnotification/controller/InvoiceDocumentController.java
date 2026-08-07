package com.autohub.emailnotification.controller;

import com.autohub.emailnotification.service.InvoicePdfStorageService;
import com.autohub.emailnotification.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/invoices")
public class InvoiceDocumentController {

    private final InvoicePdfStorageService invoicePdfStorageService;

    @GetMapping(path = "/{invoiceId}/document", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<byte[]> getInvoiceDocument(@PathVariable Long invoiceId) {
        byte[] pdfBytes = invoicePdfStorageService.retrievePdf(invoiceId);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, getContentDisposition(invoiceId))
                .body(pdfBytes);
    }

    private String getContentDisposition(Long invoiceId) {
        return ContentDisposition.attachment()
                .filename(Constants.INVOICE_FILENAME_PREFIX + invoiceId + Constants.PDF_EXTENSION)
                .build()
                .toString();
    }

}
