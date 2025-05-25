-- liquibase formatted sql

-- changeset George Niculae:1
CREATE TABLE IF NOT EXISTS public.rental_office
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL,
    name
    VARCHAR
(
    255
),
    contact_address VARCHAR
(
    255
),
    phone_number VARCHAR
(
    255
),
    CONSTRAINT pk_rental_office PRIMARY KEY
(
    id
)
    );

-- changeset George Niculae:2
-- preconditions onFail:MARK_RAN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM rental_office
INSERT INTO public.rental_office
    (name, contact_address, phone_number)
VALUES ('Rental Office 1', 'Ploiesti', '0722222222');

INSERT INTO public.rental_office
    (name, contact_address, phone_number)
VALUES ('Rental Office 2', 'Bucuresti', '0722222223');
