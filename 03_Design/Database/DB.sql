CREATE DATABASE HoS;
GO
USE HoS;
GO

-- 1. Table ROLE
CREATE TABLE [ROLE] (
    role_id INT IDENTITY(1,1) PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL
);

-- 2. Table USER
CREATE TABLE [USER] (
    user_id INT IDENTITY(1,1) PRIMARY KEY,
    role_id INT NOT NULL,
    email VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name NVARCHAR(50),
    gender VARCHAR(6),
    date_of_birth DATE,
    phone VARCHAR(20),
    Identify_code VARCHAR(20),
    avatar VARCHAR(MAX),
    last_update DATETIME DEFAULT GETDATE(),
    status VARCHAR(10),
    created_at DATETIME DEFAULT GETDATE(),
    last_login DATETIME,
    CONSTRAINT FK_USER_ROLE FOREIGN KEY (role_id) REFERENCES [ROLE](role_id)
);

-- 3. Table THERAPIST
CREATE TABLE THERAPIST (
    therapist_id INT PRIMARY KEY,
    therapist_code VARCHAR(6) NOT NULL UNIQUE,
    status VARCHAR(10),
    CONSTRAINT FK_THERAPIST_USER FOREIGN KEY (therapist_id) REFERENCES [USER](user_id)
);

-- 4. Table CONSENT
CREATE TABLE CONSENT (
    consent_id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT NOT NULL,
    consent_status BIT DEFAULT 0,
    update_at DATETIME DEFAULT GETDATE(),
    consent_version VARCHAR(8),
    is_delete BIT DEFAULT 0,
    CONSTRAINT FK_CONSENT_USER FOREIGN KEY (user_id) REFERENCES [USER](user_id)
);

-- 5. Table PHYSICAL_HEALTH_PROFILE
CREATE TABLE PHYSICAL_HEALTH_PROFILE (
    profile_id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT NOT NULL,
    medical_conditions NVARCHAR(MAX),
    injuries NVARCHAR(MAX),
    update_at DATETIME DEFAULT GETDATE(),
    CONSTRAINT FK_PHYSICAL_USER FOREIGN KEY (user_id) REFERENCES [USER](user_id)
);

-- 6. Table DIETARY_PROFILE
CREATE TABLE DIETARY_PROFILE (
    dietary_id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT NOT NULL,
    food_allergies NVARCHAR(MAX),
    diatary_preference NVARCHAR(MAX),
    update_at DATETIME DEFAULT GETDATE(),
    CONSTRAINT FK_DIETARY_USER FOREIGN KEY (user_id) REFERENCES [USER](user_id)
);

-- 7. Table TREATMENT_SERVICE
CREATE TABLE TREATMENT_SERVICE (
    service_id INT IDENTITY(1,1) PRIMARY KEY,
    treatment_code VARCHAR(10) NOT NULL UNIQUE,
    service_name NVARCHAR(50) NOT NULL,
    duration_minutes INT,
    price DECIMAL(18, 2),
    is_available BIT DEFAULT 1,
    is_delete BIT DEFAULT 0
);

-- 8. Table TREATMENT_ROOM
CREATE TABLE TREATMENT_ROOM (
    room_id INT IDENTITY(1,1) PRIMARY KEY,
    room_code VARCHAR(10) NOT NULL UNIQUE,
    image VARCHAR(MAX),
    room_name NVARCHAR(50),
    status VARCHAR(20),
    is_delete BIT DEFAULT 0
);

-- 9. Table VILLA_TYPE
CREATE TABLE VILLA_TYPE (
    type_id INT IDENTITY(1,1) PRIMARY KEY,
    type_name NVARCHAR(50) NOT NULL,
    image NVARCHAR(MAX),
    price_per_day DECIMAL(18, 2),
    is_delete BIT DEFAULT 0
);

-- 10. Table VILLA
CREATE TABLE VILLA (
    villa_id INT IDENTITY(1,1) PRIMARY KEY,
    villa_type INT NOT NULL,
    villa_code VARCHAR(10) NOT NULL UNIQUE,
    limit_person INT,
    villa_status VARCHAR(10),
    cleaning_status VARCHAR(10),
    is_delete BIT DEFAULT 0,
    CONSTRAINT FK_VILLA_TYPE FOREIGN KEY (villa_type) REFERENCES VILLA_TYPE(type_id)
);

-- 11. Table RETREAT_PACKAGE
CREATE TABLE RETREAT_PACKAGE (
    package_id INT IDENTITY(1,1) PRIMARY KEY,
    type_package NVARCHAR(50),
    package_name NVARCHAR(50),
    duration_days INT,
    services NVARCHAR(MAX),
    description NVARCHAR(MAX),
    is_active BIT DEFAULT 1,
    is_delete BIT DEFAULT 0,
    price DECIMAL(18, 2),
    create_at DATETIME DEFAULT GETDATE(),
    update_at DATETIME DEFAULT GETDATE(),
);

-- 12. Table BOOKING
CREATE TABLE BOOKING (
    booking_id INT IDENTITY(1,1) PRIMARY KEY,
    guest_id INT NOT NULL,
    package_id INT,
    assigned_villa_id INT,
    checkin_date DATE,
    checkout_date DATE,
    total_guests INT,
    create_at DATETIME DEFAULT GETDATE(),
    update_at DATETIME DEFAULT GETDATE(),
    booking_status VARCHAR(10),
    payment_status VARCHAR(10),
    is_delete BIT DEFAULT 0,
    CONSTRAINT FK_BOOKING_GUEST FOREIGN KEY (guest_id) REFERENCES [USER](user_id),
    CONSTRAINT FK_BOOKING_PACKAGE FOREIGN KEY (package_id) REFERENCES RETREAT_PACKAGE(package_id),
    CONSTRAINT FK_BOOKING_VILLA FOREIGN KEY (assigned_villa_id) REFERENCES VILLA(villa_id)
);

-- 13. Table GUEST_FOLIO
CREATE TABLE GUEST_FOLIO (
    folio_id INT IDENTITY(1,1) PRIMARY KEY,
    booking_id INT NOT NULL,
    total_package_amout DECIMAL(18, 2),
    total_extra_fb DECIMAL(18, 2),
    final_amount DECIMAL(18, 2),
    status VARCHAR(10),
    is_delete BIT DEFAULT 0,
    create_at DATETIME DEFAULT GETDATE(),
    update_at DATETIME DEFAULT GETDATE(),
    CONSTRAINT FK_FOLIO_BOOKING FOREIGN KEY (booking_id) REFERENCES BOOKING(booking_id)
);

-- 14. Table TREATMENT_BOOKING
CREATE TABLE TREATMENT_BOOKING (
    treatment_id INT IDENTITY(1,1) PRIMARY KEY,
    booking_id INT NOT NULL,
    folio_id INT,
    service_id INT NOT NULL,
    note NVARCHAR(MAX),
    status VARCHAR(10),
    is_delete BIT DEFAULT 0,
    create_at DATETIME DEFAULT GETDATE(),
    update_at DATETIME DEFAULT GETDATE(),
    CONSTRAINT FK_TREATMENT_BOOKING_REF FOREIGN KEY (booking_id) REFERENCES BOOKING(booking_id),
    CONSTRAINT FK_TREATMENT_SERVICE FOREIGN KEY (service_id) REFERENCES TREATMENT_SERVICE(service_id)
);

-- 15. Table SCHEDULE
CREATE TABLE SCHEDULE (
    schedule_id INT IDENTITY(1,1) PRIMARY KEY,
    treatment_id INT NOT NULL,
    therapist_code VARCHAR(6),
    room_id INT,
    start_time DATETIME,
    end_time DATETIME,
    is_delete BIT DEFAULT 0,
    CONSTRAINT FK_SCHEDULE_TREATMENT_BOOKING FOREIGN KEY (treatment_id) REFERENCES TREATMENT_BOOKING(treatment_id),
    CONSTRAINT FK_SCHEDULE_THERAPIST FOREIGN KEY (therapist_code) REFERENCES THERAPIST(therapist_code),
    CONSTRAINT FK_SCHEDULE_ROOM FOREIGN KEY (room_id) REFERENCES TREATMENT_ROOM(room_id)
);

-- 16. Table MENU_ITEM
CREATE TABLE MENU_ITEM (
    menu_item_id INT IDENTITY(1,1) PRIMARY KEY,
    item_name NVARCHAR(20) NOT NULL,
    price DECIMAL(18, 2),
    ingredient NVARCHAR(MAX),
    is_available BIT DEFAULT 1,
    create_at DATETIME DEFAULT GETDATE(),
    update_at DATETIME DEFAULT GETDATE()
);

-- 17. Table MEAL_ORDER
CREATE TABLE MEAL_ORDER (
    meal_order_id INT IDENTITY(1,1) PRIMARY KEY,
    booking_id INT NOT NULL,
    folio_id INT NOT NULL,
    guest_id INT NOT NULL,
    ordered_at DATETIME DEFAULT GETDATE(),
    ordered_by INT,
    place_order VARCHAR(100),
    note NVARCHAR(MAX),
    order_status VARCHAR(10),
    CONSTRAINT FK_MEAL_BOOKING FOREIGN KEY (booking_id) REFERENCES BOOKING(booking_id),
);

-- 18. Table MEAL_ORDER_ITEM
CREATE TABLE MEAL_ORDER_ITEM (
    order_item_id INT IDENTITY(1,1) PRIMARY KEY,
    meal_order_id INT NOT NULL,
    menu_item_id INT NOT NULL,
    quantity INT,
    price DECIMAL(18, 2),
    CONSTRAINT FK_ITEM_ORDER FOREIGN KEY (meal_order_id) REFERENCES MEAL_ORDER(meal_order_id),
    CONSTRAINT FK_ITEM_MENU FOREIGN KEY (menu_item_id) REFERENCES MENU_ITEM(menu_item_id)
);

-- 19. Table FOLIO_ITEM
CREATE TABLE FOLIO_ITEM (
    folio_item_id INT IDENTITY(1,1) PRIMARY KEY,
    folio_id INT NOT NULL,
    service_category NVARCHAR(50),
    reference_id INT,
    description NVARCHAR(MAX),
    amount DECIMAL(18, 2),
    create_at DATETIME DEFAULT GETDATE(),
    create_by INT,
    status VARCHAR(10),
    CONSTRAINT FK_FOLIO_ITEM_REF FOREIGN KEY (folio_id) REFERENCES GUEST_FOLIO(folio_id)
);

-- 20. Table REVIEW
CREATE TABLE REVIEW (
    review_id INT IDENTITY(1,1) PRIMARY KEY,
    booking_id INT NOT NULL,
    rating INT CHECK (rating >= 1 AND rating <= 5),
    comment NVARCHAR(MAX),
    CONSTRAINT FK_REVIEW_BOOKING FOREIGN KEY (booking_id) REFERENCES BOOKING(booking_id)
);

-- 21. Table PAYMENT
CREATE TABLE PAYMENT (
    payment_id INT IDENTITY(1,1) PRIMARY KEY,
    folio_id INT,
    amount DECIMAL(18, 2),
    payment_method VARCHAR(10),
    payment_gateway VARCHAR(10),
    transaction_code VARCHAR(100),
    payment_date DATETIME DEFAULT GETDATE(),
    status VARCHAR(10),
    CONSTRAINT FK_PAYMENT_FOLIO FOREIGN KEY (folio_id) REFERENCES GUEST_FOLIO(folio_id)
);
GO

-- =========================================================
-- DUMMY DATA: Lịch làm việc Chuyên viên Spa (SCHEDULE)
-- =========================================================

-- 1. Insert ROLE
INSERT INTO [ROLE] (role_name) VALUES ('Guest'), ('Therapist');

-- 2. Insert USER (1 Guest, 1 Therapist)
INSERT INTO [USER] (role_id, email, password_hash, full_name, status)
VALUES 
(1, 'guest@example.com', 'hash123', N'Nguyễn Văn Khách', 'Active'),
(2, 'spa@example.com', 'hash123', N'Trần Thị Spa', 'Active');

-- 3. Insert THERAPIST
INSERT INTO THERAPIST (therapist_id, therapist_code, status)
VALUES (2, 'SPA001', 'Active');

-- 4. Insert TREATMENT_ROOM
INSERT INTO TREATMENT_ROOM (room_code, room_name, status)
VALUES ('R01', N'Phòng Hoa Sen', 'Available');

-- 5. Insert TREATMENT_SERVICE
INSERT INTO TREATMENT_SERVICE (treatment_code, service_name, duration_minutes, price)
VALUES ('SV01', N'Massage Toàn Thân', 60, 500000);

-- 6. Insert RETREAT_PACKAGE (Required cho Booking)
INSERT INTO RETREAT_PACKAGE (type_package, package_name, duration_days, price)
VALUES ('Relax', N'Gói Thư Giãn Cuối Tuần', 2, 2000000);

-- 7. Insert BOOKING
INSERT INTO BOOKING (guest_id, package_id, total_guests, booking_status)
VALUES (1, 1, 1, 'Confirmed');

-- 8. Insert TREATMENT_BOOKING
INSERT INTO TREATMENT_BOOKING (booking_id, service_id, status)
VALUES (1, 1, 'Pending');

-- 9. Insert SCHEDULE (Lịch làm việc Chuyên viên Spa)
INSERT INTO SCHEDULE (treatment_id, therapist_code, room_id, start_time, end_time)
VALUES 
(1, 'SPA001', 1, '2026-06-14 09:00:00', '2026-06-14 10:00:00'),
(1, 'SPA001', 1, '2026-06-14 14:00:00', '2026-06-14 15:00:00');
GO

-- =========================================================
-- DUMMY DATA: Lịch làm việc Chuyên viên Spa thứ 3 (SPA003)
-- =========================================================

-- 10. Insert USER (Spa Employee 3)
INSERT INTO [USER] (role_id, email, password_hash, full_name, status)
VALUES (2, 'spa3@example.com', 'hash123', N'Lê Thị Spa Ba', 'Active');

DECLARE @Spa3Id INT = SCOPE_IDENTITY();

-- 11. Insert THERAPIST (Spa Employee 3)
INSERT INTO THERAPIST (therapist_id, therapist_code, status)
VALUES (@Spa3Id, 'SPA003', 'Active');

-- 12. Insert SCHEDULE cho SPA003 (3 ngày, mỗi ngày 5 khung giờ)
INSERT INTO SCHEDULE (treatment_id, therapist_code, room_id, start_time, end_time)
VALUES 
-- Ngày 1: 2026-06-15
(1, 'SPA003', 1, '2026-06-15 08:00:00', '2026-06-15 09:00:00'),
(1, 'SPA003', 1, '2026-06-15 10:00:00', '2026-06-15 11:00:00'),
(1, 'SPA003', 1, '2026-06-15 13:00:00', '2026-06-15 14:00:00'),
(1, 'SPA003', 1, '2026-06-15 15:00:00', '2026-06-15 16:00:00'),
(1, 'SPA003', 1, '2026-06-15 17:00:00', '2026-06-15 18:00:00'),

-- Ngày 2: 2026-06-16
(1, 'SPA003', 1, '2026-06-16 08:00:00', '2026-06-16 09:00:00'),
(1, 'SPA003', 1, '2026-06-16 10:00:00', '2026-06-16 11:00:00'),
(1, 'SPA003', 1, '2026-06-16 13:00:00', '2026-06-16 14:00:00'),
(1, 'SPA003', 1, '2026-06-16 15:00:00', '2026-06-16 16:00:00'),
(1, 'SPA003', 1, '2026-06-16 17:00:00', '2026-06-16 18:00:00'),

-- Ngày 3: 2026-06-17
(1, 'SPA003', 1, '2026-06-17 08:00:00', '2026-06-17 09:00:00'),
(1, 'SPA003', 1, '2026-06-17 10:00:00', '2026-06-17 11:00:00'),
(1, 'SPA003', 1, '2026-06-17 13:00:00', '2026-06-17 14:00:00'),
(1, 'SPA003', 1, '2026-06-17 15:00:00', '2026-06-17 16:00:00'),
(1, 'SPA003', 1, '2026-06-17 17:00:00', '2026-06-17 18:00:00');
GO
