-- User
BEGIN;

CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    document_number VARCHAR(20) UNIQUE NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    birth_date DATE,
    address VARCHAR(255),
    phone_number VARCHAR(20),
    email VARCHAR(255) UNIQUE NOT NULL,
    salary DECIMAL(10,2) NOT NULL
);

CREATE UNIQUE INDEX idx_users_document_number ON users (document_number);
CREATE UNIQUE INDEX idx_users_email ON users (email);
ALTER TABLE users ADD CONSTRAINT check_birth_date CHECK (birth_date <= CURRENT_DATE);
COMMIT;