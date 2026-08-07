package com.autohub.emailnotification.service;

import com.autohub.dto.common.InvoiceResponse;
import com.autohub.emailnotification.entity.InvoiceDocument;
import com.autohub.emailnotification.repository.InvoiceDocumentRepository;
import com.autohub.exception.AutoHubNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoicePdfStorageService {

    private final InvoiceDocumentRepository invoiceDocumentRepository;

    public void savePdf(InvoiceResponse invoiceResponse, byte[] content) {
        InvoiceDocument document = invoiceDocumentRepository.findByInvoiceId(invoiceResponse.id())
                .orElseGet(InvoiceDocument::new);

        document.setInvoiceId(invoiceResponse.id());
        document.setCustomerUsername(invoiceResponse.customerUsername());
        document.setCustomerEmail(invoiceResponse.customerEmail());
        document.setCreatedAt(LocalDateTime.now());
        document.setContent(content);

        invoiceDocumentRepository.save(document);

        log.info("Stored PDF for invoice {}", invoiceResponse.id());
    }

    public byte[] retrievePdf(Long invoiceId) {
        return invoiceDocumentRepository.findByInvoiceId(invoiceId)
                .map(InvoiceDocument::getContent)
                .orElseThrow(() -> new AutoHubNotFoundException("PDF not found for invoice: " + invoiceId));
    }

}
