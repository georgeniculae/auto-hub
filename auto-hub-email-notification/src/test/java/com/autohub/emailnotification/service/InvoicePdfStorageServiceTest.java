package com.autohub.emailnotification.service;

import com.autohub.dto.common.InvoiceResponse;
import com.autohub.emailnotification.entity.InvoiceDocument;
import com.autohub.emailnotification.repository.InvoiceDocumentRepository;
import com.autohub.emailnotification.util.TestUtil;
import com.autohub.exception.AutoHubNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvoicePdfStorageServiceTest {

    private static final byte[] CONTENT = "%PDF-1.4 content".getBytes(StandardCharsets.UTF_8);

    @InjectMocks
    private InvoicePdfStorageService invoicePdfStorageService;

    @Mock
    private InvoiceDocumentRepository invoiceDocumentRepository;

    private InvoiceResponse invoice() {
        return TestUtil.getResourceAsJson("/data/InvoiceResponse.json", InvoiceResponse.class);
    }

    @Test
    void savePdfTest_storesMetadataAndContent() {
        InvoiceResponse invoiceResponse = invoice();

        when(invoiceDocumentRepository.findByInvoiceId(anyLong())).thenReturn(Optional.empty());

        invoicePdfStorageService.savePdf(invoiceResponse, CONTENT);

        ArgumentCaptor<InvoiceDocument> captor = ArgumentCaptor.forClass(InvoiceDocument.class);
        verify(invoiceDocumentRepository).save(captor.capture());

        InvoiceDocument saved = captor.getValue();

        assertEquals(invoiceResponse.id(), saved.getInvoiceId());
        assertEquals(invoiceResponse.customerUsername(), saved.getCustomerUsername());
        assertEquals(invoiceResponse.customerEmail(), saved.getCustomerEmail());
        assertArrayEquals(CONTENT, saved.getContent());
        assertNull(saved.getId(), "un document nou nu trebuie sa aiba id");
    }

    @Test
    void savePdfTest_updatesExistingDocumentInsteadOfInserting() {
        InvoiceResponse invoiceResponse = invoice();

        InvoiceDocument existing = InvoiceDocument.builder()
                .id(7L)
                .invoiceId(invoiceResponse.id())
                .customerUsername(invoiceResponse.customerUsername())
                .customerEmail(invoiceResponse.customerEmail())
                .createdAt(LocalDateTime.now().minusDays(1))
                .content("old".getBytes(StandardCharsets.UTF_8))
                .build();

        when(invoiceDocumentRepository.findByInvoiceId(anyLong())).thenReturn(Optional.of(existing));

        invoicePdfStorageService.savePdf(invoiceResponse, CONTENT);

        ArgumentCaptor<InvoiceDocument> captor = ArgumentCaptor.forClass(InvoiceDocument.class);
        verify(invoiceDocumentRepository).save(captor.capture());

        InvoiceDocument saved = captor.getValue();

        assertEquals(7L, saved.getId(), "trebuie actualizat randul existent, nu inserat unul nou");
        assertArrayEquals(CONTENT, saved.getContent());
    }

    @Test
    void retrievePdfTest_returnsStoredContent() {
        InvoiceDocument document = InvoiceDocument.builder()
                .id(1L)
                .invoiceId(1L)
                .customerUsername("admin")
                .customerEmail("admin@test.com")
                .createdAt(LocalDateTime.now())
                .content(CONTENT)
                .build();

        when(invoiceDocumentRepository.findByInvoiceId(anyLong())).thenReturn(Optional.of(document));

        assertArrayEquals(CONTENT, invoicePdfStorageService.retrievePdf(1L));
    }

    @Test
    void retrievePdfTest_throwsWhenMissing() {
        when(invoiceDocumentRepository.findByInvoiceId(anyLong())).thenReturn(Optional.empty());

        assertThrows(AutoHubNotFoundException.class, () -> invoicePdfStorageService.retrievePdf(999L));
    }

}
