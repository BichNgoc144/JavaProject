-- 1. Tạo database
CREATE DATABASE net_management;

-- 2. Sử dụng database đó
USE net_management;

-- 3. Tạo bảng users
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(10) NOT NULL
);

-- 4. Thêm tài khoản admin mặc định
INSERT INTO users (username, password, role) 
VALUES ('admin@gmail.com', '12345678', 'admin');

CREATE TABLE machines (
    id INT AUTO_INCREMENT PRIMARY KEY,
    machine_code VARCHAR(50) NOT NULL UNIQUE,
    machine_name VARCHAR(100) NOT NULL,
    hourly_rate DECIMAL(10, 2) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE deposit (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    deposit_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) -- Giả sử bảng `users` có id người dùng
);

CREATE TABLE machine_usage (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    machine_name VARCHAR(50) NOT NULL,
    start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- Thời gian bắt đầu sử dụng máy
    end_time TIMESTAMP,  -- Thời gian kết thúc
    total_amount DECIMAL(10, 2) NOT NULL,  -- Số tiền tính cho phiên sử dụng
    FOREIGN KEY (user_id) REFERENCES users(id)  -- Liên kết với bảng users
);

/*SELECT * FROM net_management.users;

SELECT * FROM users WHERE role = 'admin';

SELECT * FROM users;

SELECT * FROM deposit;

SELECT * FROM machine_usage;

SELECT machine_code, machine_name, hourly_rate, description FROM machines;*/

