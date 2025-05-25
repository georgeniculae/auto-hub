-- liquibase formatted sql

-- changeset George Niculae:1
CREATE TABLE IF NOT EXISTS public.invoice
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL,
    customer_username
    VARCHAR
(
    255
) NOT NULL,
    customer_email VARCHAR
(
    255
) NOT NULL,
    car_id BIGINT NOT NULL,
    receptionist_employee_id BIGINT,
    return_branch_id BIGINT,
    booking_id BIGINT NOT NULL,
    date_from DATE NOT NULL,
    date_to DATE NOT NULL,
    car_return_date DATE,
    is_vehicle_damaged BOOLEAN,
    damage_cost DECIMAL,
    rental_car_price DECIMAL NOT NULL,
    additional_payment DECIMAL,
    total_amount DECIMAL,
    comments VARCHAR
(
    255
),
    CONSTRAINT pk_invoice PRIMARY KEY
(
    id
)
    );
