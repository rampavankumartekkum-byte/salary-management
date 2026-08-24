CREATE TABLE employees (
    id              BIGSERIAL PRIMARY KEY,
    employee_code   VARCHAR(20)     NOT NULL UNIQUE,
    first_name      VARCHAR(120)    NOT NULL,
    last_name       VARCHAR(120)    NOT NULL,
    email           VARCHAR(200)    NOT NULL UNIQUE,
    department      VARCHAR(80)     NOT NULL,
    designation     VARCHAR(100)    NOT NULL,
    country         VARCHAR(60)     NOT NULL,
    currency        VARCHAR(3)      NOT NULL,
    base_salary     NUMERIC(14, 2)  NOT NULL CHECK (base_salary >= 0),
    annual_bonus    NUMERIC(14, 2)  NOT NULL DEFAULT 0 CHECK (annual_bonus >= 0),
    employment_type VARCHAR(20)     NOT NULL,
    manager_name    VARCHAR(200),
    date_joined     DATE            NOT NULL
);

CREATE INDEX idx_employee_department ON employees (department);
CREATE INDEX idx_employee_country ON employees (country);
CREATE INDEX idx_employee_designation ON employees (designation);
CREATE INDEX idx_employee_salary ON employees (base_salary);
