CREATE DATABASE IF NOT EXISTS borrowhub CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE borrowhub;

DROP TABLE IF EXISTS Borrow_Transactions;
DROP TABLE IF EXISTS Borrow_Requests;
DROP TABLE IF EXISTS Items;
DROP TABLE IF EXISTS Students;

-- 1. Students Table
CREATE TABLE Students (
    student_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    department VARCHAR(80) NOT NULL,
    college_id VARCHAR(50) NOT NULL UNIQUE,
    phone VARCHAR(20) DEFAULT NULL,
    year_of_study VARCHAR(20) DEFAULT '2nd Year',
    role ENUM('STUDENT', 'ADMIN') DEFAULT 'STUDENT',
    avatar_url VARCHAR(255) DEFAULT NULL,
    karma_points INT DEFAULT 100,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 2. Items Table (Free to borrow)
CREATE TABLE Items (
    item_id INT AUTO_INCREMENT PRIMARY KEY,
    owner_id INT NOT NULL,
    item_name VARCHAR(150) NOT NULL,
    category VARCHAR(60) NOT NULL,
    description TEXT NOT NULL,
    rate_info VARCHAR(60) DEFAULT 'Free to borrow',
    item_condition VARCHAR(50) DEFAULT 'Good Condition',
    image_url VARCHAR(500) DEFAULT NULL,
    availability_status ENUM('AVAILABLE', 'BORROWED', 'UNAVAILABLE') DEFAULT 'AVAILABLE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_items_owner FOREIGN KEY (owner_id) 
        REFERENCES Students(student_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 3. Borrow_Requests Table
CREATE TABLE Borrow_Requests (
    request_id INT AUTO_INCREMENT PRIMARY KEY,
    item_id INT NOT NULL,
    borrower_id INT NOT NULL,
    request_date DATE NOT NULL,
    return_date DATE NOT NULL,
    purpose TEXT DEFAULT NULL,
    request_status ENUM('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED') DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_req_item FOREIGN KEY (item_id) 
        REFERENCES Items(item_id) ON DELETE CASCADE,
    CONSTRAINT fk_req_borrower FOREIGN KEY (borrower_id) 
        REFERENCES Students(student_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 4. Borrow_Transactions Table
CREATE TABLE Borrow_Transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    request_id INT NOT NULL UNIQUE,
    borrowed_date DATE NOT NULL,
    due_date DATE NOT NULL,
    actual_return_date DATE DEFAULT NULL,
    transaction_status ENUM('ACTIVE', 'RETURNED', 'OVERDUE') DEFAULT 'ACTIVE',
    lender_confirmed_return BOOLEAN DEFAULT FALSE,
    borrower_notes TEXT DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_trans_request FOREIGN KEY (request_id) 
        REFERENCES Borrow_Requests(request_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Indexes for performance
CREATE INDEX idx_items_category ON Items(category);
CREATE INDEX idx_items_status ON Items(availability_status);
CREATE INDEX idx_req_borrower ON Borrow_Requests(borrower_id);
CREATE INDEX idx_req_item ON Borrow_Requests(item_id);

-- Sample Data (Pass: 'password123')
INSERT INTO Students (student_id, name, email, password_hash, department, college_id, phone, year_of_study, role, avatar_url, karma_points) VALUES
(1, 'Riya Sharma', 'riya.sharma@campus.edu', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'Computer Science', 'CS2024-042', '9876543210', '2nd Year', 'STUDENT', 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150', 140),
(2, 'Aryan Patel', 'aryan.patel@campus.edu', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'Information Tech', 'IT2024-118', '9876543211', '3rd Year', 'STUDENT', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150', 95),
(3, 'Neha Kapoor', 'neha.kapoor@campus.edu', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'Electronics & Comm', 'EC2023-019', '9876543212', '4th Year', 'STUDENT', 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150', 210),
(4, 'Vikram Singh', 'vikram.singh@campus.edu', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'Mechanical Engg', 'ME2024-088', '9876543213', '2nd Year', 'STUDENT', 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150', 80),
(5, 'Sneha Menon', 'sneha.menon@campus.edu', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'Biotechnology', 'BT2024-007', '9876543214', '1st Year', 'STUDENT', 'https://images.unsplash.com/photo-1517841905240-472988babdf9?w=150', 160),
(6, 'Prof. Arvind Sharma', 'admin@borrowhub.edu', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'Campus Resource Cell', 'STAFF-001', '9876543299', 'Faculty', 'ADMIN', 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150', 500);

-- Items (All Free to borrow, no laptops, no hostel locations)
INSERT INTO Items (item_id, owner_id, item_name, category, description, rate_info, item_condition, image_url, availability_status) VALUES
(1, 1, 'DSA Textbook', 'Textbooks', 'Data Structures and Algorithms in Java by Robert Lafore. Standard 2nd year reference book with neat bookmarks and highlights.', 'Free to borrow', 'Like New', 'https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=600', 'AVAILABLE'),
(2, 2, 'Scientific Calculator', 'Calculators', 'Casio fx-991EX ClassWiz with 552 functions. Permitted in university mid-term and semester examinations.', 'Free to borrow', 'Excellent', 'assets/images/calculator.svg', 'AVAILABLE'),
(3, 3, 'Chemistry Lab Coat & Goggles', 'Lab Equipment', 'Pure white 100% cotton lab coat (Size M, washed and pressed) with ANSI certified splash safety goggles.', 'Free to borrow', 'Like New', 'https://images.unsplash.com/photo-1584017911766-d451b3d0e843?w=600', 'AVAILABLE'),
(4, 4, 'Badminton Set', 'Sports Gear', 'Yonex Carbonex pair with 3 nylon feather shuttlecocks and waterproof carry cover for campus court play.', 'Free to borrow', 'Good Condition', 'https://images.unsplash.com/photo-1626224583764-f87db24ac4ea?w=600', 'AVAILABLE'),
(5, 5, 'Camera (Canon DSLR)', 'Electronics', 'Canon EOS 1500D DSLR with 18-55mm IS II Lens, 64GB SD card and camera bag. Great for campus events and fest coverage.', 'Free to borrow', 'Excellent', 'https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=600', 'BORROWED'),
(6, 4, 'Engineering Mini Drafter', 'Lab Equipment', 'Omega mini drafter with steel clamp and 360 degree protractor head for Engineering Graphics drawing sheets.', 'Free to borrow', 'Well Calibrated', 'https://images.unsplash.com/photo-1581092160607-ee22621dd758?w=600', 'AVAILABLE'),
(7, 2, 'Arduino IoT Starter Kit', 'Electronics', 'Arduino Uno R3 with 30+ sensors, breadboard, jumper wires, and LCD display for semester mini-projects.', 'Free to borrow', 'Complete Kit', 'https://images.unsplash.com/photo-1553406830-ef2513450d76?w=600', 'AVAILABLE');

-- Sample Borrow Requests
INSERT INTO Borrow_Requests (request_id, item_id, borrower_id, request_date, return_date, purpose, request_status) VALUES
(1, 5, 2, CURDATE() - INTERVAL 4 DAY, CURDATE() + INTERVAL 3 DAY, 'Needed for capturing cultural fest inaugurations and student council elections.', 'APPROVED'),
(2, 1, 4, CURDATE() - INTERVAL 1 DAY, CURDATE() + INTERVAL 6 DAY, 'Need to prepare for Trees and Graphs laboratory practical test next Wednesday.', 'PENDING');

-- Sample Transactions
INSERT INTO Borrow_Transactions (transaction_id, request_id, borrowed_date, due_date, transaction_status, lender_confirmed_return, borrower_notes) VALUES
(1, 1, CURDATE() - INTERVAL 4 DAY, CURDATE() + INTERVAL 3 DAY, 'ACTIVE', FALSE, 'Verified on campus during handover.');
