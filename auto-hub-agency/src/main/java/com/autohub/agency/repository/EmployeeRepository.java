package com.autohub.agency.repository;

import com.autohub.agency.entity.Employee;
import jakarta.persistence.QueryHint;
import org.hibernate.jpa.HibernateHints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.stream.Stream;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("""
            From Employee employee""")
    @QueryHints(value = {
            @QueryHint(name = HibernateHints.HINT_FETCH_SIZE, value = "1"),
            @QueryHint(name = HibernateHints.HINT_CACHEABLE, value = "false"),
            @QueryHint(name = HibernateHints.HINT_READ_ONLY, value = "true")
    })
    Stream<Employee> findAllEmployee();

    @Query("""
            From Employee employee
            where upper(employee.firstName) like upper(concat('%', ?1, '%'))
            or upper(employee.lastName) like upper(concat('%', ?1, '%'))
            or upper(employee.jobPosition) like upper(concat('%', ?1, '%'))
            or upper(employee.workingRentalOffice.name) like upper(concat('%', ?1, '%'))""")
    @QueryHints(value = {
            @QueryHint(name = HibernateHints.HINT_FETCH_SIZE, value = "1"),
            @QueryHint(name = HibernateHints.HINT_CACHEABLE, value = "false"),
            @QueryHint(name = HibernateHints.HINT_READ_ONLY, value = "true")
    })
    Stream<Employee> findByFilter(String filter);

    @Query("""
            From Employee employee
            where employee.workingRentalOffice.id = ?1""")
    Stream<Employee> findAllEmployeesByRentalOfficeId(Long id);

}
