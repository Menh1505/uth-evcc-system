-- Script to setup PostgreSQL database for EVCC application

-- Create database (run this as postgres superuser)
-- CREATE DATABASE evcc_db;

-- Create user and grant privileges (optional, if you want a specific user)
-- CREATE USER evcc_user WITH PASSWORD 'evcc_password';
-- GRANT ALL PRIVILEGES ON DATABASE evcc_db TO evcc_user;

-- Switch to evcc_db database and create tables if needed
-- \c evcc_db;

-- The application will auto-create tables using JPA/Hibernate
-- But you can create them manually if needed:

/*
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20) UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_phone ON users(phone);
CREATE INDEX IF NOT EXISTS idx_users_status ON users(status);
CREATE INDEX IF NOT EXISTS idx_users_created_at ON users(created_at);

-- Insert sample data
INSERT INTO users (username, phone, password_hash, status) VALUES 
    ('admin', '0123456789', 'hashedPassword123', 'ACTIVE'),
    ('user1', '0987654321', 'hashedPassword456', 'ACTIVE'),
    ('user2', '0555666777', 'hashedPassword789', 'PENDING')
ON CONFLICT (username) DO NOTHING;
*/

/*CREATE TABLE document_upload (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  owner_id BIGINT NOT NULL,
  doc_type VARCHAR(100) NOT NULL,
  original_filename VARCHAR(512),
  storage_path VARCHAR(1024),
  mime_type VARCHAR(255),
  size BIGINT,
  status VARCHAR(20),
  submitted_at TIMESTAMP,
  reviewed_by BIGINT,
  reviewed_at TIMESTAMP,
  review_note VARCHAR(2000)
);
*/