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
    gender VARCHAR(6) CHECK (gender IN ('MALE', 'FEMALE', 'OTHER')),
    date_of_birth DATE,
    phone VARCHAR(20),
    Identify_code VARCHAR(255),
    avatar VARCHAR(MAX),
    update_at DATETIME DEFAULT GETDATE(),
    status VARCHAR(10) CHECK (status IN ('ACTIVE', 'INACTIVE', 'BANNED')),
    create_at DATETIME DEFAULT GETDATE(),
    last_login DATETIME,
    verify_token VARCHAR(255),
    booking_id INT,
    is_delete BIT DEFAULT 0, 
    CONSTRAINT FK_USER_ROLE FOREIGN KEY (role_id) REFERENCES [ROLE](role_id)
);

-- 3. Table THERAPIST
CREATE TABLE THERAPIST (
    therapist_id INT PRIMARY KEY,
    therapist_code VARCHAR(6) NOT NULL UNIQUE,
    status VARCHAR(10) CHECK (status IN ('AVAILABLE', 'BUSY', 'OFF_DUTY')),
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
    create_at DATETIME DEFAULT GETDATE(),
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
    status VARCHAR(20) CHECK (status IN ('AVAILABLE', 'OCCUPIED', 'MAINTENANCE')),
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
    villa_status VARCHAR(20) CHECK (villa_status IN ('AVAILABLE', 'OCCUPIED', 'MAINTENANCE')),
    cleaning_status VARCHAR(10) CHECK (cleaning_status IN ('CLEAN', 'DIRTY', 'CLEANING')),
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
    checkin_date DATETIME,
    checkout_date DATETIME,
    total_guests INT,
    create_at DATETIME DEFAULT GETDATE(),
    update_at DATETIME DEFAULT GETDATE(),
    requested_villa_type_id INT,
    booking_status VARCHAR(20) CHECK (booking_status IN ('PENDING', 'CONFIRMED', 'CHECKED_IN', 'CHECKED_OUT', 'CANCELLED')),
    payment_status VARCHAR(20) CHECK (payment_status IN ('UNPAID', 'PARTIAL', 'PAID', 'REFUNDED')),
    is_delete BIT DEFAULT 0,
    CONSTRAINT FK_BOOKING_GUEST FOREIGN KEY (guest_id) REFERENCES [USER](user_id),
    CONSTRAINT FK_BOOKING_PACKAGE FOREIGN KEY (package_id) REFERENCES RETREAT_PACKAGE(package_id),
    CONSTRAINT FK_BOOKING_VILLA FOREIGN KEY (assigned_villa_id) REFERENCES VILLA(villa_id),
    CONSTRAINT FK_BOOKING_VILLA_TYPE FOREIGN KEY (requested_villa_type_id) REFERENCES VILLA_TYPE(type_id)
);

-- 13. Table GUEST_FOLIO
CREATE TABLE GUEST_FOLIO (
    folio_id INT IDENTITY(1,1) PRIMARY KEY,
    booking_id INT NOT NULL,
    total_package_amout DECIMAL(18, 2),
    total_extra_fb DECIMAL(18, 2),
    final_amount DECIMAL(18, 2),
    status VARCHAR(10) CHECK (status IN ('OPEN', 'CLOSED')),
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
    status VARCHAR(20) CHECK (status IN ('PENDING', 'SCHEDULED', 'COMPLETED', 'CANCELLED')),
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
    item_name NVARCHAR(100) NOT NULL,
    price DECIMAL(18, 2),
    image_url VARCHAR(255),
    category VARCHAR(50),
    ingredient NVARCHAR(MAX),
    is_available BIT DEFAULT 1,
    image_url VARCHAR(255),
    category VARCHAR(50),
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
    serving_time VARCHAR(20),
    place_order VARCHAR(100),
    note NVARCHAR(MAX),
    order_status VARCHAR(20) CHECK (order_status IN ('PENDING', 'PREPARING', 'DELIVERED', 'CANCELLED')),
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
    status VARCHAR(10) CHECK (status IN ('UNPAID', 'PAID', 'VOIDED')),
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
    payment_method VARCHAR(20) CHECK (payment_method IN ('CASH', 'CREDIT_CARD', 'BANK_TRANSFER')),
    payment_gateway VARCHAR(20) CHECK (payment_gateway IN ('VNPAY', 'MOMO', 'DIRECT')),
    transaction_code VARCHAR(100),
    payment_date DATETIME DEFAULT GETDATE(),
    status VARCHAR(10) CHECK (status IN ('PENDING', 'SUCCESS', 'FAILED')),
    CONSTRAINT FK_PAYMENT_FOLIO FOREIGN KEY (folio_id) REFERENCES GUEST_FOLIO(folio_id)
);

-- 22. Table AUDIT_LOG
CREATE TABLE AUDIT_LOG (
    log_id INT IDENTITY(1,1) PRIMARY KEY,
    action_type VARCHAR(50) NOT NULL,
    actor_id INT NOT NULL,
    target_id INT,
    details NVARCHAR(MAX),
    timestamp DATETIME DEFAULT GETDATE(),
    CONSTRAINT FK_AUDIT_ACTOR FOREIGN KEY (actor_id) REFERENCES [USER](user_id)
);
GO

-- 23. Table YOGA_CLASS
CREATE TABLE YOGA_CLASS (
    class_id INT IDENTITY(1,1) PRIMARY KEY,
    class_name NVARCHAR(100) NOT NULL,
    description NVARCHAR(MAX),
    duration_minutes INT NOT NULL,
    image_url VARCHAR(255),
    is_delete BIT DEFAULT 0
);
GO

-- 24. Table YOGA_INSTRUCTOR
CREATE TABLE YOGA_INSTRUCTOR (
    instructor_id INT PRIMARY KEY,
    instructor_code VARCHAR(10) NOT NULL UNIQUE,
    status VARCHAR(20) DEFAULT 'AVAILABLE',
    is_delete BIT DEFAULT 0,
    CONSTRAINT FK_YOGA_INST_USER FOREIGN KEY (instructor_id) REFERENCES [USER](user_id)
);
GO

-- 25. Table YOGA_SCHEDULE
CREATE TABLE YOGA_SCHEDULE (
    schedule_id INT IDENTITY(1,1) PRIMARY KEY,
    class_id INT NOT NULL,
    instructor_id INT NOT NULL,
    location NVARCHAR(100) NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    max_capacity INT NOT NULL,
    is_delete BIT DEFAULT 0,
    CONSTRAINT FK_YOGA_SCHED_CLASS FOREIGN KEY (class_id) REFERENCES YOGA_CLASS(class_id),
    CONSTRAINT FK_YOGA_SCHED_INST FOREIGN KEY (instructor_id) REFERENCES YOGA_INSTRUCTOR(instructor_id)
);
GO

-- 26. Table YOGA_REGISTRATION
CREATE TABLE YOGA_REGISTRATION (
    registration_id INT IDENTITY(1,1) PRIMARY KEY,
    booking_id INT NOT NULL,
    yoga_schedule_id INT NOT NULL,
    registered_at DATETIME NOT NULL DEFAULT GETDATE(),
    status VARCHAR(20) NOT NULL DEFAULT 'REGISTERED',
    CONSTRAINT FK_YOGA_REG_BOOKING FOREIGN KEY (booking_id) REFERENCES BOOKING(booking_id),
    CONSTRAINT FK_YOGA_REG_SCHED FOREIGN KEY (yoga_schedule_id) REFERENCES YOGA_SCHEDULE(schedule_id)
);
GO
