-- ====================================================================
-- SCRIPT NẠP DỮ LIỆU MẪU (DATABASE SEED SCRIPT)
-- HỆ THỐNG QUẢN LÝ XOAI AURA RETREAT - MODULE 2
-- CƠ SỞ DỮ LIỆU: SQL SERVER (databaseName: HoS)
-- ====================================================================

-- 1. DỌN DẸP DỮ LIỆU CŨ THEO THỨ TỰ AN TOÀN TRÁNH LỖI KHÓA NGOẠI (FOREIGN KEY)
DELETE FROM CONSENT;
DELETE FROM PHYSICAL_HEALTH_PROFILE;
DELETE FROM DIETARY_PROFILE;
DELETE FROM MEAL_ORDER_ITEM;
DELETE FROM MEAL_ORDER;
DELETE FROM MENU_ITEM;
DELETE FROM TREATMENT_BOOKING;
DELETE FROM SCHEDULE;
DELETE FROM TREATMENT_ROOM;
DELETE FROM TREATMENT_SERVICE;
DELETE FROM THERAPIST;
DELETE FROM REVIEW;
DELETE FROM PAYMENT;
DELETE FROM FOLIO_ITEM;
DELETE FROM GUEST_FOLIO;
DELETE FROM BOOKING;
DELETE FROM VILLA;
DELETE FROM VILLA_TYPE;
DELETE FROM RETREAT_PACKAGE;
DELETE FROM [USER];
DELETE FROM [ROLE];

-- ==========================================
-- 2. THÊM VAI TRÒ (ROLE)
-- ==========================================
SET IDENTITY_INSERT [ROLE] ON;

INSERT INTO [ROLE] (role_id, role_name) VALUES (1, 'Guest');
INSERT INTO [ROLE] (role_id, role_name) VALUES (2, 'Receptionist');
INSERT INTO [ROLE] (role_id, role_name) VALUES (3, 'Manager');
INSERT INTO [ROLE] (role_id, role_name) VALUES (4, 'Chef');
INSERT INTO [ROLE] (role_id, role_name) VALUES (5, 'Therapist');

SET IDENTITY_INSERT [ROLE] OFF;

-- ==========================================
-- 3. THÊM TÀI KHOẢN MẪU (USER)
-- ==========================================
SET IDENTITY_INSERT [USER] ON;

-- Tài khoản Khách hàng (Guest) mẫu - Mật khẩu đăng nhập mặc định: 123
INSERT INTO [USER] (
    user_id, role_id, email, password_hash, full_name, gender, 
    date_of_birth, phone, Identify_code, avatar, status, created_at
) VALUES (
    1, 
    1, -- Khách hàng (Guest)
    'guest@gmail.com', 
    '$2a$10$GRLdNijSQ0UMXiUfy38uAOTtK40tQ6jF8/35H6o2R3gH7P4xqy2sK', -- BCrypt hash của '123'
    N'Nguyễn Văn A', 
    'Male', 
    '1995-05-15', 
    '0901234567', 
    NULL, -- Sẽ được cập nhật mã hóa AES khi check-in
    NULL, 
    'ACTIVE', 
    GETDATE()
);

-- Tài khoản Lễ tân (Receptionist) mẫu - Mật khẩu đăng nhập mặc định: 123
INSERT INTO [USER] (
    user_id, role_id, email, password_hash, full_name, gender, 
    date_of_birth, phone, Identify_code, avatar, status, created_at
) VALUES (
    2, 
    2, -- Lễ tân (Receptionist)
    'receptionist@gmail.com', 
    '$2a$10$GRLdNijSQ0UMXiUfy38uAOTtK40tQ6jF8/35H6o2R3gH7P4xqy2sK', -- BCrypt hash của '123'
    N'Lê Thị B (Lễ Tân)', 
    'Female', 
    '1998-09-20', 
    '0987654321', 
    NULL, 
    NULL, 
    'ACTIVE', 
    GETDATE()
);

SET IDENTITY_INSERT [USER] OFF;

-- ==========================================
-- 4. THÊM CÁC LOẠI BIỆT THỰ (VILLA_TYPE)
-- ==========================================
SET IDENTITY_INSERT VILLA_TYPE ON;

INSERT INTO VILLA_TYPE (type_id, type_name, price_per_day, image, is_delete) 
VALUES (1, 'Ocean View Villa', 5000000.00, 'https://images.unsplash.com/photo-1540555700478-4be289fbecef?auto=format&fit=crop&w=800&q=80', 0);

INSERT INTO VILLA_TYPE (type_id, type_name, price_per_day, image, is_delete) 
VALUES (2, 'Garden View Villa', 3000000.00, 'https://images.unsplash.com/photo-1512917774080-9991f1c4c750?auto=format&fit=crop&w=800&q=80', 0);

INSERT INTO VILLA_TYPE (type_id, type_name, price_per_day, image, is_delete) 
VALUES (3, 'Pool Villa', 4000000.00, 'https://images.unsplash.com/photo-1580587771525-78b9dba3b914?auto=format&fit=crop&w=800&q=80', 0);

SET IDENTITY_INSERT VILLA_TYPE OFF;

-- ==========================================
-- 5. THÊM BIỆT THỰ VẬT LÝ (VILLA)
-- ==========================================
SET IDENTITY_INSERT VILLA ON;

INSERT INTO VILLA (villa_id, villa_type, villa_code, limit_person, villa_status, cleaning_status, is_delete) 
VALUES (101, 1, 'VIL-101', 2, 'AVAILABLE', 'CLEANED', 0);

INSERT INTO VILLA (villa_id, villa_type, villa_code, limit_person, villa_status, cleaning_status, is_delete) 
VALUES (102, 1, 'VIL-102', 2, 'AVAILABLE', 'CLEANED', 0);

INSERT INTO VILLA (villa_id, villa_type, villa_code, limit_person, villa_status, cleaning_status, is_delete) 
VALUES (201, 2, 'VIL-201', 4, 'AVAILABLE', 'CLEANED', 0);

INSERT INTO VILLA (villa_id, villa_type, villa_code, limit_person, villa_status, cleaning_status, is_delete) 
VALUES (301, 3, 'VIL-301', 2, 'AVAILABLE', 'CLEANED', 0);

SET IDENTITY_INSERT VILLA OFF;

-- ==========================================
-- 6. THÊM CÁC GÓI TRỊ LIỆU (RETREAT_PACKAGE)
-- ==========================================
SET IDENTITY_INSERT RETREAT_PACKAGE ON;

INSERT INTO RETREAT_PACKAGE (package_id, type_package, package_name, duration_days, services, description, is_active, price, create_at, is_delete) 
VALUES (
    1, 
    'Yoga', 
    'Sunrise Yoga Weekend', 
    3, 
    'Luyện tập Yoga, thực đơn dinh dưỡng nhẹ nhàng, kiểm tra sức khỏe tổng quát', 
    'Hành trình 3 ngày yên bình giúp tái tạo năng lượng và cân bằng Thân - Tâm - Trí.', 
    1, 
    15000000.00, 
    GETDATE(), 
    0
);

INSERT INTO RETREAT_PACKAGE (package_id, type_package, package_name, duration_days, services, description, is_active, price, create_at, is_delete) 
VALUES (
    2, 
    'Detox', 
    'Mindful Detox Program', 
    5, 
    'Nước detox thanh lọc, tư vấn dinh dưỡng chuyên sâu, liệu pháp massage phục hồi', 
    'Liệu trình thanh lọc toàn diện 5 ngày đào thải độc tố cơ thể và hồi xuân tinh thần.', 
    1, 
    25000000.00, 
    GETDATE(), 
    0
);

INSERT INTO RETREAT_PACKAGE (package_id, type_package, package_name, duration_days, services, description, is_active, price, create_at, is_delete) 
VALUES (
    3, 
    'Stress Relief', 
    'Deep Relaxation Retreat', 
    4, 
    'Thiền định chánh niệm, xông hơi đá nóng phục hồi giấc ngủ, trà đạo organic', 
    'Trốn khỏi cuộc sống xô bồ với 4 ngày nghỉ ngơi sâu xoa dịu hệ thần kinh.', 
    1, 
    18000000.00, 
    GETDATE(), 
    0
);

SET IDENTITY_INSERT RETREAT_PACKAGE OFF;

-- ==========================================
-- 7. THÊM ĐƠN ĐẶT PHÒNG MẪU (BOOKING)
-- ==========================================
SET IDENTITY_INSERT BOOKING ON;

-- Đơn đặt phòng mẫu cho khách hàng ID = 1 (Chọn gói Yoga 3 ngày)
INSERT INTO BOOKING (
    booking_id, guest_id, package_id, assigned_villa_id, 
    checkin_date, checkout_date, total_guests, 
    booking_status, payment_status, create_at, is_delete
) VALUES (
    1, 
    1, -- Nguyễn Văn A
    1, -- Sunrise Yoga Weekend (3 ngày)
    NULL, -- Chưa gán phòng vật lý khi chưa Check-in
    CAST(GETDATE() AS DATE), 
    CAST(DATEADD(day, 2, GETDATE()) AS DATE), -- Ngày về = hôm nay + 2 ngày (tổng 3 ngày)
    2, -- Đoàn gồm 2 khách
    'CONFIRMED', -- Đã xác nhận đơn
    'DEPOSITED', -- Đã thanh toán đặt cọc
    GETDATE(), 
    0
);

SET IDENTITY_INSERT BOOKING OFF;

-- ==========================================
-- 8. THÊM HÓA ĐƠN TRUNG TÂM MẪU (GUEST_FOLIO)
-- ==========================================
SET IDENTITY_INSERT GUEST_FOLIO ON;

INSERT INTO GUEST_FOLIO (
    folio_id, booking_id, total_package_amout, total_extra_fb, final_amount, status, create_at, is_delete
) VALUES (
    1, 
    1, -- Liên kết với đơn đặt phòng ID = 1
    15000000.00, -- Giá trị gói trị liệu
    0.00, 
    15000000.00, 
    'ACTIVE', 
    GETDATE(), 
    0
);

SET IDENTITY_INSERT GUEST_FOLIO OFF;
