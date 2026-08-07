package com.autohub.emailnotification.repository;

import com.autohub.emailnotification.entity.InvoiceDocument;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class InvoiceDocumentRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:latest");

    @Autowired
    private InvoiceDocumentRepository invoiceDocumentRepository;

    private static InvoiceDocument document(Long invoiceId, byte[] content) {
        return InvoiceDocument.builder()
                .invoiceId(invoiceId)
                .customerUsername("admin")
                .customerEmail("admin@test.com")
                .createdAt(LocalDateTime.now())
                .content(content)
                .build();
    }

    @Test
    void checkIfConnectionEstablished() {
        assertTrue(postgres.isCreated());
    }

    @Test
    void saveAndFindByInvoiceIdTest_contentSurvivesRoundTrip() {
        byte[] content = "%PDF-1.4 test content".getBytes(StandardCharsets.UTF_8);

        invoiceDocumentRepository.save(document(1L, content));

        Optional<InvoiceDocument> found = invoiceDocumentRepository.findByInvoiceId(1L);

        assertTrue(found.isPresent());
        assertArrayEquals(content, found.get().getContent());
        assertEquals("admin", found.get().getCustomerUsername());
    }

    @Test
    void findByInvoiceIdTest_missingInvoice() {
        assertTrue(invoiceDocumentRepository.findByInvoiceId(999L).isEmpty());
    }

}
