package com.autohub.agency.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "branch", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Branch extends BaseEntity {

    @NotEmpty(message = "Name cannot be empty")
    private String name;

    @NotEmpty(message = "Region cannot be empty")
    private String region;

    @NotEmpty(message = "Address cannot be empty")
    private String address;

    @NotEmpty(message = "Phone number cannot be empty")
    private String phoneNumber;

}
