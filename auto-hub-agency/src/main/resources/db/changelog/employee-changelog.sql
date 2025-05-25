-- liquibase formatted sql

-- changeset George Niculae:1
CREATE TABLE IF NOT EXISTS public.employee
(
    id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL,
    first_name
    VARCHAR
(
    255
),
    last_name VARCHAR
(
    255
),
    job_position VARCHAR
(
    255
),
    working_branch_id BIGINT,
    CONSTRAINT pk_employee PRIMARY KEY
(
    id
)
    );

ALTER TABLE public.employee
    ADD CONSTRAINT FK_EMPLOYEE_ON_WORKING_BRANCH
        FOREIGN KEY (working_branch_id)
            REFERENCES public.branch (id);

-- changeset George Niculae:2
-- preconditions onFail:MARK_RAN
-- precondition-sql-check expectedResult:0 SELECT COUNT(*) FROM employee
INSERT INTO public.employee
    (first_name, last_name, job_position, working_branch_id)
VALUES ('Andrei', 'Popescu', 'manager', 1);

INSERT INTO public.employee
    (first_name, last_name, job_position, working_branch_id)
VALUES ('David', 'Alexandrescu', 'worker', 1);

INSERT INTO public.employee
    (first_name, last_name, job_position, working_branch_id)
VALUES ('Alexandru', 'Ionescu', 'manager', 2);

INSERT INTO public.employee
    (first_name, last_name, job_position, working_branch_id)
VALUES ('Claudiu', 'Stefanescu', 'worker', 2);
