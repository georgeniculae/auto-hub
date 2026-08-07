package com.autohub.agency.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "rental_office", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class RentalOffice extends BaseEntity {

    @NotEmpty(message = "Name cannot be empty")
    private String name;

    @NotEmpty(message = "City cannot be empty")
    private String city;

    @NotEmpty(message = "Address cannot be empty")
    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private Branch branch;

}
