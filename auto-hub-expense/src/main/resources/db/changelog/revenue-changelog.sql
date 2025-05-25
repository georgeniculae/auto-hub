-- liquibase formatted sql

-- changeset George Niculae:1
CREATE TABLE IF NOT EXISTS public.revenue
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL,
    date_of_revenue
    date
    NOT
    NULL,
    amount_from_booking
    DECIMAL
    NOT
    NULL,
    CONSTRAINT
    pk_revenue
    PRIMARY
    KEY
(
    id
)
    );
