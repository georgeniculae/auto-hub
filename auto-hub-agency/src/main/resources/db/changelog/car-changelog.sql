-- liquibase formatted sql

-- changeset George Niculae:1
CREATE TABLE IF NOT EXISTS public.car
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL,
    make
    VARCHAR
(
    255
),
    model VARCHAR
(
    255
),
    body_type VARCHAR
(
    255
),
    year_of_production INTEGER NOT NULL,
    color VARCHAR
(
    255
),
    mileage INTEGER NOT NULL,
    car_status VARCHAR
(
    255
) NOT NULL,
    amount DECIMAL NOT NULL,
    original_branch_id BIGINT,
    actual_branch_id BIGINT,
    image BYTEA,
    CONSTRAINT pk_car PRIMARY KEY
(
    id
)
    );

ALTER TABLE public.car
    ADD CONSTRAINT FK_CAR_ON_ACTUAL_BRANCH
        FOREIGN KEY (actual_branch_id)
            REFERENCES public.branch (id);

ALTER TABLE public.car
    ADD CONSTRAINT FK_CAR_ON_ORIGINAL_BRANCH
        FOREIGN KEY (original_branch_id)
            REFERENCES public.branch (id);

-- changeset George Niculae:2
-- preconditions onFail:MARK_RAN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM car
INSERT INTO public.car
(make, model, body_type, year_of_production, color, mileage, car_status, amount, original_branch_id, actual_branch_id)
VALUES ('Volkswagen', 'Golf', 'HATCHBACK', 2010, 'black', 250000, 'AVAILABLE', 500, 1, 1);

INSERT INTO public.car
(make, model, body_type, year_of_production, color, mileage, car_status, amount, original_branch_id, actual_branch_id)
VALUES ('Audi', 'A4', 'SEDAN', 2015, 'white', 187000, 'AVAILABLE', 800, 2, 2);
