package com.autohub.booking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "booking", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    protected Long id;

    @NotNull(message = "Date of booking cannot be null")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBooking;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @NotEmpty(message = "Username cannot be empty")
    private String customerUsername;

    @NotEmpty(message = "Email cannot be empty")
    private String customerEmail;

    @NotNull(message = "Car id cannot be null")
    private Long actualCarId;

    private Long previousCarId;

    @NotNull(message = "Date from cannot be null")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateFrom;

    @NotNull(message = "Date to cannot be blank")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateTo;

    private BigDecimal amount;

    @NotNull(message = "Rental car price cannot be null")
    private BigDecimal rentalCarPrice;

    @NotNull(message = "Pickup rental office id cannot be null")
    private Long pickupRentalOfficeId;

    private Long returnRentalOfficeId;

    private boolean isUpdated;

}
