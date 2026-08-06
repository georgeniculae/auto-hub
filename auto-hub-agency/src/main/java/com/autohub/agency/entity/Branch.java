package com.autohub.agency.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "branch", schema = "public")
@NoArgsConstructor
@Builder
@Getter(onMethod_ = @JsonProperty)
@Setter(onMethod_ = @JsonProperty)
@JsonIgnoreProperties(ignoreUnknown = true, value = "employees")
public class Branch extends BaseEntity {

    @NotEmpty(message = "Name cannot be empty")
    private String name;

    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rental_office_id")
    private RentalOffice rentalOffice;

    @OneToMany(
            mappedBy = "workingBranch",
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.REMOVE
            },
            fetch = FetchType.LAZY
    )
    @JsonIgnore
    @Builder.Default
    private List<Employee> employees = new ArrayList<>();

    public Branch(String name, String address, RentalOffice rentalOffice, List<Employee> employees) {
        this.name = name;
        this.address = address;
        this.rentalOffice = rentalOffice;
        this.employees = Objects.requireNonNullElseGet(employees, ArrayList::new);
    }

}
