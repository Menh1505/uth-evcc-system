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
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    full_name VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_created_at ON users(created_at);

-- Insert sample data
INSERT INTO users (username, email, full_name) VALUES 
    ('admin', 'admin@evcc.com', 'System Administrator'),
    ('user1', 'user1@evcc.com', 'Test User 1'),
    ('user2', 'user2@evcc.com', 'Test User 2')
ON CONFLICT (username) DO NOTHING;
*/
