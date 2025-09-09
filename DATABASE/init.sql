-- Active: 1755995405122@@127.0.0.1@5433@cy_application
-- User and User Roles
BEGIN;

CREATE TABLE IF NOT EXISTS user_roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    role VARCHAR(100) UNIQUE NOT NULL,
    description VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    document_number VARCHAR(20) UNIQUE NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    birth_date DATE,
    address VARCHAR(255),
    phone_number VARCHAR(20),
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    salary DECIMAL(10,2) NOT NULL,
    role_id UUID NOT NULL,
    FOREIGN KEY (role_id) REFERENCES user_roles(id)
);

CREATE UNIQUE INDEX idx_users_document_number ON users (document_number);
CREATE UNIQUE INDEX idx_users_email ON users (email);
ALTER TABLE users ADD CONSTRAINT check_birth_date CHECK (birth_date <= CURRENT_DATE);
COMMIT;

-- Insert roles

BEGIN;

INSERT INTO user_roles (id, role, description) VALUES
    (gen_random_uuid(), 'CLIENTE', 'Cliente'),
    (gen_random_uuid(), 'ASESOR', 'Asesor'),
    (gen_random_uuid(), 'ADMIN', 'Admin');

COMMIT;

-- Application

BEGIN;

CREATE TABLE IF NOT EXISTS applications_status (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    status VARCHAR(100) UNIQUE NOT NULL,
    description VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS applications_credit_type (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    credit_type VARCHAR(100) NOT NULL,
    min_amount DECIMAL NOT NULL,
    max_amount DECIMAL NOT NULL,
    interest_rate DECIMAL(2,2) NOT NULL,
    automatic_review BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS applications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_email VARCHAR(255) NOT NULL,
    amount DECIMAL NOT NULL,
    credit_period DATE NOT NULL,
    application_status_id UUID NOT NULL,
    application_credit_type_id UUID NOT NULL,
    FOREIGN KEY (application_status_id) REFERENCES applications_status(id),
    FOREIGN KEY (application_credit_type_id) REFERENCES applications_credit_type(id)
);

COMMIT;

BEGIN;

INSERT INTO applications_credit_type (id, credit_type, min_amount, max_amount, interest_rate, automatic_review) VALUES
    (gen_random_uuid(), 'PERSONAL', 100000, 10000000, 0.1, true),
    (gen_random_uuid(), 'HIPOTECARIO', 1000000000, 1000000000, 0.2, false),
    (gen_random_uuid(), 'LIBRANZA', 1000000, 10000000, 0.3, false),
    (gen_random_uuid(), 'VEHICULO', 10000000, 1000000000, 0.4, false);

INSERT INTO applications_status (id, status, description) VALUES
    (gen_random_uuid(), 'PENDIENTE', 'Pendiente de revision'),
    (gen_random_uuid(), 'REVISION', 'En revisión'),
    (gen_random_uuid(), 'CANCELADO', 'Cancelado'),
    (gen_random_uuid(), 'APROBADO', 'Aprobado'),
    (gen_random_uuid(), 'RECHAZADO', 'Rechazado');
COMMIT;