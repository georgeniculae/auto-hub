package com.autohub.emailnotification.repository;

import com.autohub.emailnotification.entity.InvoiceDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvoiceDocumentRepository extends JpaRepository<InvoiceDocument, Long> {

    Optional<InvoiceDocument> findByInvoiceId(Long invoiceId);

}
