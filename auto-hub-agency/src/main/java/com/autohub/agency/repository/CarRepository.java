package com.autohub.agency.repository;

import com.autohub.agency.entity.Car;
import jakarta.persistence.QueryHint;
import org.hibernate.jpa.HibernateHints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.lang.NonNull;

import java.util.Optional;
import java.util.stream.Stream;

public interface CarRepository extends JpaRepository<Car, Long> {

    @Query("""
            Select new Car(
            car.id,
            car.make,
            car.model,
            car.bodyType,
            car.yearOfProduction,
            car.color,
            car.mileage,
            car.carStatus,
            car.amount,
            initialOffice,
            actualOffice
            )
            From Car car
            left join car.initialRentalOffice initialOffice
            left join car.actualRentalOffice actualOffice
            where car.id = ?1""")
    @NonNull
    Optional<Car> findById(@NonNull Long id);

    @Query("""
            Select new Car(
            car.id,
            car.make,
            car.model,
            car.bodyType,
            car.yearOfProduction,
            car.color,
            car.mileage,
            car.carStatus,
            car.amount,
            initialOffice,
            actualOffice
            )
            From Car car
            left join car.initialRentalOffice initialOffice
            left join car.actualRentalOffice actualOffice
            where upper(car.make) like upper(concat('%', ?1, '%'))
            or upper(car.model) like upper(concat('%', ?1, '%'))""")
    @QueryHints(value = {
            @QueryHint(name = HibernateHints.HINT_FETCH_SIZE, value = "1"),
            @QueryHint(name = HibernateHints.HINT_CACHEABLE, value = "false"),
            @QueryHint(name = HibernateHints.HINT_READ_ONLY, value = "true")
    })
    Stream<Car> findByFilter(String filter);

    @Query("""
            Select new Car(
            car.id,
            car.make,
            car.model,
            car.bodyType,
            car.yearOfProduction,
            car.color,
            car.mileage,
            car.carStatus,
            car.amount,
            initialOffice,
            actualOffice
            )
            From Car car
            left join car.initialRentalOffice initialOffice
            left join car.actualRentalOffice actualOffice""")
    @QueryHints(value = {
            @QueryHint(name = HibernateHints.HINT_FETCH_SIZE, value = "1"),
            @QueryHint(name = HibernateHints.HINT_CACHEABLE, value = "false"),
            @QueryHint(name = HibernateHints.HINT_READ_ONLY, value = "true")
    })
    @NonNull
    Stream<Car> findAllCars();

    @Query("""
            Select new Car(
            car.id,
            car.make,
            car.model,
            car.bodyType,
            car.yearOfProduction,
            car.color,
            car.mileage,
            car.carStatus,
            car.amount,
            initialOffice,
            actualOffice
            )
            From Car car
            left join car.initialRentalOffice initialOffice
            left join car.actualRentalOffice actualOffice
            where car.carStatus = 'AVAILABLE'""")
    @QueryHints(value = {
            @QueryHint(name = HibernateHints.HINT_FETCH_SIZE, value = "1"),
            @QueryHint(name = HibernateHints.HINT_CACHEABLE, value = "false"),
            @QueryHint(name = HibernateHints.HINT_READ_ONLY, value = "true")
    })
    @NonNull
    Stream<Car> findAllAvailableCars();

    @Query("""
            Select new Car(
            car.id,
            car.make,
            car.model,
            car.bodyType,
            car.yearOfProduction,
            car.color,
            car.mileage,
            car.carStatus,
            car.amount,
            initialOffice,
            actualOffice
            )
            From Car car
            left join car.initialRentalOffice initialOffice
            left join car.actualRentalOffice actualOffice
            where upper(car.make) like upper(concat('%', ?1, '%'))""")
    @QueryHints(value = {
            @QueryHint(name = HibernateHints.HINT_FETCH_SIZE, value = "1"),
            @QueryHint(name = HibernateHints.HINT_CACHEABLE, value = "false"),
            @QueryHint(name = HibernateHints.HINT_READ_ONLY, value = "true")
    })
    Stream<Car> findCarsByMakeIgnoreCase(String make);

    @Query("""
            Select new Car(car.image)
            From Car car
            where car.id = ?1""")
    Optional<Car> findImageByCarId(Long id);

    @Query("""
            Select new Car(
            car.id,
            car.make,
            car.model,
            car.bodyType,
            car.yearOfProduction,
            car.color,
            car.mileage,
            car.carStatus,
            car.amount,
            initialOffice,
            actualOffice
            )
            From Car car
            left join car.initialRentalOffice initialOffice
            left join car.actualRentalOffice actualOffice
            where car.carStatus = 'AVAILABLE'
            and upper(actualOffice.city) = upper(?1)""")
    @QueryHints(value = {
            @QueryHint(name = HibernateHints.HINT_FETCH_SIZE, value = "1"),
            @QueryHint(name = HibernateHints.HINT_CACHEABLE, value = "false"),
            @QueryHint(name = HibernateHints.HINT_READ_ONLY, value = "true")
    })
    Stream<Car> findAllAvailableCarsByLocation(String location);

}
