package com.carrental.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Table(name = "booking", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Booking extends BaseEntity {

    @NotNull(message = "Date of booking cannot be null")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private LocalDate dateOfBooking;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @NotEmpty(message = "Username cannot be empty")
    private String customerUsername;

    @NotEmpty(message = "Customer email cannot be empty")
    private String customerEmail;

    @NotNull(message = "Car id cannot be null")
    private Long carId;

    @NotNull(message = "Date from cannot be null")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private LocalDate dateFrom;

    @NotNull(message = "Date to cannot be blank")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private LocalDate dateTo;

    private Double amount;

    private Double rentalCarPrice;

    @NotNull(message = "Rental branch id cannot be null")
    private Long rentalBranchId;

    private Long returnBranchId;

}
