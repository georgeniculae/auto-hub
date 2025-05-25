-- liquibase formatted sql

-- changeset George Niculae:1
CREATE TABLE IF NOT EXISTS public.branch
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
    address VARCHAR
(
    255
),
    rental_office_id BIGINT,
    CONSTRAINT pk_branch PRIMARY KEY
(
    id
)
    );

ALTER TABLE public.branch
    ADD CONSTRAINT FK_BRANCH_ON_RENTAL_OFFICE
        FOREIGN KEY (rental_office_id)
            REFERENCES public.rental_office (id);

-- changeset George Niculae:2
-- preconditions onFail:MARK_RAN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM branch
INSERT INTO public.branch
    (name, address, rental_office_id)
VALUES ('Branch 1', 'Ploiesti', 1);

INSERT INTO public.branch
    (name, address, rental_office_id)
VALUES ('Branch 2', 'Bucuresti', 2);
