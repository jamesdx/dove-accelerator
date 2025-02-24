-- Create database if not exists
CREATE DATABASE IF NOT EXISTS dove_accelerator CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Grant privileges
GRANT ALL PRIVILEGES ON dove_accelerator.* TO 'dove_user'@'%';
FLUSH PRIVILEGES;

-- Use the database
USE dove_accelerator;

-- Add any initial table creation or data seeding here
-- Example:
-- CREATE TABLE example (
--     id BIGINT PRIMARY KEY AUTO_INCREMENT,
--     name VARCHAR(255) NOT NULL,
--     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
-- ); 