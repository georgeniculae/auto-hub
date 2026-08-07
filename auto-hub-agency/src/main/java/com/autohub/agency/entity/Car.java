package com.autohub.agency.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;

import java.math.BigDecimal;
import java.sql.Types;

@Entity
@Table(name = "car", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Car extends BaseEntity {

    @NotEmpty(message = "Make cannot be empty")
    private String make;

    @NotEmpty(message = "Model cannot be empty")
    private String model;

    @Enumerated(EnumType.STRING)
    private BodyType bodyType;

    @NotNull(message = "Year of production cannot be null")
    private Integer yearOfProduction;

    @NotEmpty(message = "Color cannot be empty")
    private String color;

    @NotNull(message = "Mileage cannot be null")
    private Integer mileage;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Car status cannot be null")
    private CarStatus carStatus;

    @NotNull(message = "Amount cannot be null")
    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initial_rental_office_id")
    private RentalOffice initialRentalOffice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actual_rental_office_id")
    private RentalOffice actualRentalOffice;

    @JdbcTypeCode(Types.BINARY)
    private byte[] image;

    public Car(byte[] image) {
        this.image = image;
    }

    public Car(
            Long id,
            String make,
            String model,
            BodyType bodyType,
            Integer yearOfProduction,
            String color,
            Integer mileage,
            CarStatus carStatus,
            BigDecimal amount,
            RentalOffice initialRentalOffice,
            RentalOffice actualRentalOffice
    ) {
        super(id);
        this.make = make;
        this.model = model;
        this.bodyType = bodyType;
        this.yearOfProduction = yearOfProduction;
        this.color = color;
        this.mileage = mileage;
        this.carStatus = carStatus;
        this.amount = amount;
        this.initialRentalOffice = initialRentalOffice;
        this.actualRentalOffice = actualRentalOffice;
    }

}
