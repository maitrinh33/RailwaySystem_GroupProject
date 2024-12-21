CREATE DATABASE railway_system;

USE railway_system;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(50) NOT NULL
);

CREATE TABLE schedule (
    id INT AUTO_INCREMENT PRIMARY KEY,
    train_name VARCHAR(255) NOT NULL,
    route VARCHAR(255) NOT NULL,
    departure_station VARCHAR(255) NOT NULL,
    arrival_station VARCHAR(255) NOT NULL,
    departure_date DATE NOT NULL,
    arrival_date DATE NOT NULL,
    departure_time TIME NOT NULL,
    arrival_time TIME NOT NULL,
    capacity INT NOT NULL,
    status VARCHAR(50) NOT NULL
);

CREATE TABLE seat (
    id INT AUTO_INCREMENT PRIMARY KEY,
    seat_number VARCHAR(10) NOT NULL,
    coach_number INT NOT NULL,
    class_type VARCHAR(50) NOT NULL,
    train_id INT NOT NULL,
    is_available BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (train_id) REFERENCES schedule(id) ON DELETE CASCADE
);

CREATE TABLE ticket (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ticket_id VARCHAR(255) NOT NULL,
    departure_date DATE NOT NULL,
    train_name VARCHAR(255) NOT NULL,
    route VARCHAR(255) NOT NULL,
    coach_seat VARCHAR(50) NOT NULL, 
    class_type VARCHAR(50) NOT NULL,
    ticket_type ENUM('adult', 'student', 'children') NOT NULL,
    base_fare DOUBLE NOT NULL,
    subsidy_amount DOUBLE NOT NULL,
    total_fare DOUBLE NOT NULL,
    customer_name VARCHAR(255) NOT NULL,
    passport VARCHAR(255) NOT NULL,
    reservation_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    schedule_id INT NOT NULL,
    seat_id INT NOT NULL,  
    is_paid BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (schedule_id) REFERENCES schedule(id),
    FOREIGN KEY (seat_id) REFERENCES seat(id)  
);

CREATE TABLE fare (
    id INT AUTO_INCREMENT PRIMARY KEY,
    train_name VARCHAR(255) NOT NULL,
    coach_number INT NOT NULL,
    class_type VARCHAR(50) NOT NULL,
    base_fare DOUBLE NOT NULL,
    UNIQUE (train_name, coach_number, class_type)  
);

CREATE TABLE subsidy (
    id INT AUTO_INCREMENT PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    amount DOUBLE NOT NULL,
    type ENUM('adult', 'student', 'children') NOT NULL -- Include 'children' type
);

CREATE TABLE payment (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ticket_id VARCHAR(255) NOT NULL,  -- Reference to the ticket
    total_money DOUBLE NOT NULL,
    cash_received DOUBLE NOT NULL,
    change_returned DOUBLE NOT NULL,
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ticket_id) REFERENCES ticket(ticket_id) ON DELETE CASCADE  -- Linking to the ticket table
);

CREATE TABLE booking (
    booking_id INT AUTO_INCREMENT PRIMARY KEY,
    ticket_id VARCHAR(100) NOT NULL,  -- Reference to the ticket
    payment_id INT NOT NULL,           -- Reference to the payment
    customer_name VARCHAR(100) NOT NULL,
    passport VARCHAR(50) NOT NULL,
    payment_amount DOUBLE NOT NULL,
    booking_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    departure_date DATE NOT NULL,
    train_name VARCHAR(100) NOT NULL,
    seat_id VARCHAR(10) NOT NULL,
    route VARCHAR(255) NOT NULL,          -- Route from ticket table
    class_type VARCHAR(50) NOT NULL,      -- Class type from ticket table
    seat_number VARCHAR(10) NOT NULL,     -- Seat number from ticket table
    coach_number VARCHAR(10) NOT NULL,     -- Coach from ticket table
    status ENUM('Confirmed', 'Cancelled', 'Returned') DEFAULT 'Confirmed',
    FOREIGN KEY (ticket_id) REFERENCES ticket(ticket_id) -- Linking to ticket table
);

CREATE TABLE refund (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ticket_id VARCHAR(255) NOT NULL,  
    booking_id INT NOT NULL,  
    refund_amount DECIMAL(10, 2) NOT NULL,
    refund_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('Refunded', 'Partial', 'Declined') DEFAULT 'Refunded',
    FOREIGN KEY (booking_id) REFERENCES booking(booking_id) ON DELETE CASCADE
);

CREATE TABLE report (
    id INT AUTO_INCREMENT PRIMARY KEY,
    report_date DATE NOT NULL,
    total_bookings INT NOT NULL,
    total_revenue DOUBLE NOT NULL,
    total_profit DOUBLE NOT NULL,
    report_period ENUM('daily', 'monthly', 'quarterly', 'annually') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

select*from booking;
