package com.autohub.agency.repository;

import com.autohub.agency.entity.RentalOffice;
import jakarta.persistence.QueryHint;
import org.hibernate.jpa.HibernateHints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.stream.Stream;

public interface RentalOfficeRepository extends JpaRepository<RentalOffice, Long> {

    @Query("""
            From RentalOffice rentalOffice""")
    @QueryHints(value = {
            @QueryHint(name = HibernateHints.HINT_FETCH_SIZE, value = "1"),
            @QueryHint(name = HibernateHints.HINT_CACHEABLE, value = "false"),
            @QueryHint(name = HibernateHints.HINT_READ_ONLY, value = "true")
    })
    Stream<RentalOffice> findAllRentalOffices();

    @Query("""
            Select rentalOffice
            From RentalOffice rentalOffice
            left join rentalOffice.branch branch
            where upper(rentalOffice.name) like upper(concat('%', ?1, '%'))
            or upper(rentalOffice.city) like upper(concat('%', ?1, '%'))
            or upper(rentalOffice.address) like upper(concat('%', ?1, '%'))
            or upper(branch.name) like upper(concat('%', ?1, '%'))""")
    @QueryHints(value = {
            @QueryHint(name = HibernateHints.HINT_FETCH_SIZE, value = "1"),
            @QueryHint(name = HibernateHints.HINT_CACHEABLE, value = "false"),
            @QueryHint(name = HibernateHints.HINT_READ_ONLY, value = "true")
    })
    Stream<RentalOffice> findRentalOfficeByFilter(String filter);

}
