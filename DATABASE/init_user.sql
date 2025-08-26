-- User and User Roles
BEGIN;

CREATE TABLE IF NOT EXISTS user_roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    role VARCHAR(100) NOT NULL,
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
    (gen_random_uuid(), 'USER', 'User'),
    (gen_random_uuid(), 'ADMIN', 'Admin');

COMMIT;

-- Application

BEGIN;

CREATE TABLE IF NOT EXISTS applications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_document_number VARCHAR(20) NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    credit_period DATE NOT NULL,
    credit_type VARCHAR(100) NOT NULL,
    credit_status VARCHAR(100) DEFAULT 'Pendiente de revision'
);

COMMIT;