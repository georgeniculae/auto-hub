package com.autohub.emailnotification.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoice_document", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class InvoiceDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long id;

    @NotNull(message = "Invoice id cannot be null")
    private Long invoiceId;

    @NotEmpty(message = "Customer username cannot be empty")
    private String customerUsername;

    @NotEmpty(message = "Customer email cannot be empty")
    private String customerEmail;

    @NotNull(message = "Created at cannot be null")
    private LocalDateTime createdAt;

    @NotNull(message = "Content cannot be null")
    @JdbcTypeCode(Types.BINARY)
    private byte[] content;

}
