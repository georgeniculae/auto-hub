package com.autohub.agency.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "rental_office", schema = "public")
@NoArgsConstructor
@Builder
@Getter(onMethod_ = @JsonProperty)
@Setter(onMethod_ = @JsonProperty)
@JsonIgnoreProperties(ignoreUnknown = true, value = "branches")
public class RentalOffice extends BaseEntity {

    @OneToMany(
            mappedBy = "rentalOffice",
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.REMOVE
            },
            fetch = FetchType.LAZY
    )
    @JsonIgnore
    @Builder.Default
    private List<Branch> branches = new ArrayList<>();

    @NotEmpty(message = "Name cannot be empty")
    private String name;

    @NotEmpty(message = "Contact address domain cannot be empty")
    private String contactAddress;

    private String phoneNumber;

    public RentalOffice(List<Branch> branches, String name, String contactAddress, String phoneNumber) {
        this.branches = Objects.requireNonNullElseGet(branches, ArrayList::new);
        this.name = name;
        this.contactAddress = contactAddress;
        this.phoneNumber = phoneNumber;
    }

}
