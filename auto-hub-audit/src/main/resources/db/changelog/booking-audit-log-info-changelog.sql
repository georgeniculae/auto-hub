-- liquibase formatted sql

-- changeset George Niculae:1
CREATE TABLE IF NOT EXISTS public.booking_audit_log_info
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL,
    method_name
    VARCHAR
(
    255
),
    username VARCHAR
(
    255
),
    timestamp TIMESTAMP WITHOUT TIME ZONE,
    parameters_values TEXT[],
    CONSTRAINT pk_booking_audit_log_info PRIMARY KEY
(
    id
)
    );
