CREATE TABLE employees
(
    id          BIGSERIAL PRIMARY KEY,
    first_name  VARCHAR(100) NOT NULL,
    last_name   VARCHAR(100) NOT NULL,
    email       VARCHAR(150) NOT NULL UNIQUE,
    department  VARCHAR(100),
    designation VARCHAR(100),
    salary      NUMERIC(15, 2),
    created_at  TIMESTAMP DEFAULT NOW(),
    updated_at  TIMESTAMP DEFAULT NOW(),
    is_active   BOOLEAN   DEFAULT TRUE
);