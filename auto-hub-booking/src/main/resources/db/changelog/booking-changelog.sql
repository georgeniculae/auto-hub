-- liquibase formatted sql

-- changeset George Niculae:1
CREATE TABLE IF NOT EXISTS public.booking
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL,
    date_of_booking
    date
    NOT
    NULL,
    status
    VARCHAR
(
    255
),
    customer_username VARCHAR
(
    255
) NOT NULL,
    customer_email VARCHAR
(
    255
) NOT NULL,
    actual_car_id BIGINT NOT NULL,
    previous_car_id BIGINT,
    date_from date NOT NULL,
    date_to date NOT NULL,
    amount DECIMAL,
    rental_car_price DECIMAL NOT NULL,
    rental_branch_id BIGINT NOT NULL,
    return_branch_id BIGINT,
    CONSTRAINT pk_booking PRIMARY KEY
(
    id
)
    );

-- changeset George Niculae:2 runInTransaction:false
ALTER
SYSTEM SET wal_level = logical;
SELECT pg_create_logical_replication_slot('booking_slot', 'pgoutput');
