USE HoS;
GO

-- =========================================================================
-- SCRIPT XÓA DỮ LIỆU CŨ VÀ RESET IDENTITY (Để chạy lại nhiều lần không lỗi)
-- =========================================================================
EXEC sp_MSForEachTable 'ALTER TABLE ? NOCHECK CONSTRAINT ALL';
EXEC sp_MSForEachTable 'DELETE FROM ?';
EXEC sp_MSForEachTable 'ALTER TABLE ? WITH CHECK CHECK CONSTRAINT ALL';
EXEC sp_MSForEachTable 'IF OBJECTPROPERTY(OBJECT_ID(''?''), ''TableHasIdentity'') = 1 DBCC CHECKIDENT (''?'', RESEED, 0)';
GO

-- =========================================================================
-- 1. CHÈN DỮ LIỆU BẢNG ROLE & USER
-- =========================================================================
SET IDENTITY_INSERT [ROLE] ON;
INSERT INTO [ROLE] (role_id, role_name) VALUES 
(1, 'Admin'),
(2, 'Guest'),
(3, 'Therapist'),
(4, 'Receptionist');
SET IDENTITY_INSERT [ROLE] OFF;

-- Password mặc định: 123456 (Đã hash Bcrypt)
SET IDENTITY_INSERT [USER] ON;
INSERT INTO [USER] (user_id, role_id, email, password_hash, full_name, gender, date_of_birth, phone, Identify_code, status, is_delete) VALUES
<<<<<<< HEAD
(1, 1, 'admin@auramoon.com', '$2a$10$wE9XG8uW4fGf9sPZ4a0e4O5vW0yqXk6Zl7wE9XG8uW4fGf9sPZ4a0', N'Admin Tổng', 'MALE', '1990-01-01', '0123456789', 'ID001', 'ACTIVE', 0),
(2, 2, 'guest1@gmail.com', '$2a$10$wE9XG8uW4fGf9sPZ4a0e4O5vW0yqXk6Zl7wE9XG8uW4fGf9sPZ4a0', N'Nguyễn Văn Khách', 'MALE', '1995-05-15', '0987654321', 'ID002', 'ACTIVE', 0),
(3, 3, 'therapist1@auramoon.com', '$2a$10$wE9XG8uW4fGf9sPZ4a0e4O5vW0yqXk6Zl7wE9XG8uW4fGf9sPZ4a0', N'Trần Thị Spa', 'FEMALE', '1992-08-20', '0912345678', 'ID003', 'ACTIVE', 0),
(4, 3, 'therapist2@auramoon.com', '$2a$10$wE9XG8uW4fGf9sPZ4a0e4O5vW0yqXk6Zl7wE9XG8uW4fGf9sPZ4a0', N'Lê Văn Massage', 'MALE', '1988-11-10', '0933445566', 'ID004', 'ACTIVE', 0),
(5, 2, 'guest2@gmail.com', '$2a$10$wE9XG8uW4fGf9sPZ4a0e4O5vW0yqXk6Zl7wE9XG8uW4fGf9sPZ4a0', N'Hoàng Thị Khách 2', 'FEMALE', '1998-02-12', '0922334455', 'ID005', 'ACTIVE', 0);
SET IDENTITY_INSERT [USER] OFF;

INSERT INTO THERAPIST (therapist_id, therapist_code, status) VALUES 
(3, 'NV001', 'AVAILABLE'),
(4, 'NV002', 'AVAILABLE');
=======
(1, 1, 'admin@auramoon.com', '$2a$10$wE9XG8uW4fGf9sPZ4a0e4O5vW0yqXk6Zl7wE9XG8uW4fGf9sPZ4a0', N'Admin Tổng', 'Male', '1990-01-01', '0123456789', 'ID001', 'Active', 0),
(2, 2, 'guest1@gmail.com', '$2a$10$wE9XG8uW4fGf9sPZ4a0e4O5vW0yqXk6Zl7wE9XG8uW4fGf9sPZ4a0', N'Nguyễn Văn Khách', 'Male', '1995-05-15', '0987654321', 'ID002', 'Active', 0),
(3, 3, 'therapist1@auramoon.com', '$2a$10$wE9XG8uW4fGf9sPZ4a0e4O5vW0yqXk6Zl7wE9XG8uW4fGf9sPZ4a0', N'Trần Thị Spa', 'Female', '1992-08-20', '0912345678', 'ID003', 'Active', 0),
(4, 3, 'therapist2@auramoon.com', '$2a$10$wE9XG8uW4fGf9sPZ4a0e4O5vW0yqXk6Zl7wE9XG8uW4fGf9sPZ4a0', N'Lê Văn Massage', 'Male', '1988-11-10', '0933445566', 'ID004', 'Active', 0),
(5, 2, 'guest2@gmail.com', '$2a$10$wE9XG8uW4fGf9sPZ4a0e4O5vW0yqXk6Zl7wE9XG8uW4fGf9sPZ4a0', N'Hoàng Thị Khách 2', 'Female', '1998-02-12', '0922334455', 'ID005', 'Active', 0);
SET IDENTITY_INSERT [USER] OFF;

INSERT INTO THERAPIST (therapist_id, therapist_code, status) VALUES 
(3, 'NV001', 'Active'),
(4, 'NV002', 'Active');
>>>>>>> 6a51c98 (update data)

-- =========================================================================
-- 2. CHÈN DỮ LIỆU CƠ SỞ VẬT CHẤT (VILLA, SPA ROOM, SERVICES)
-- =========================================================================
SET IDENTITY_INSERT VILLA_TYPE ON;
INSERT INTO VILLA_TYPE (type_id, type_name, price_per_day, is_delete) VALUES
(1, N'Garden View Villa', 3000000, 0),
(2, N'Ocean View Villa', 5000000, 0),
(3, N'Presidential Suite', 10000000, 0);
SET IDENTITY_INSERT VILLA_TYPE OFF;

SET IDENTITY_INSERT VILLA ON;
INSERT INTO VILLA (villa_id, villa_type, villa_code, limit_person, villa_status, cleaning_status, is_delete) VALUES
<<<<<<< HEAD
(1, 1, 'GV01', 2, 'AVAILABLE', 'CLEAN', 0),
(2, 1, 'GV02', 2, 'AVAILABLE', 'CLEAN', 0),
(3, 2, 'OV01', 4, 'AVAILABLE', 'CLEAN', 0),
(4, 2, 'OV02', 4, 'OCCUPIED', 'CLEAN', 0),
(5, 3, 'PR01', 6, 'AVAILABLE', 'CLEAN', 0);
=======
(1, 1, 'GV01', 2, 'Available', 'Clean', 0),
(2, 1, 'GV02', 2, 'Available', 'Clean', 0),
(3, 2, 'OV01', 4, 'Available', 'Clean', 0),
(4, 2, 'OV02', 4, 'Occupied', 'Clean', 0),
(5, 3, 'PR01', 6, 'Available', 'Clean', 0);
>>>>>>> 6a51c98 (update data)
SET IDENTITY_INSERT VILLA OFF;

SET IDENTITY_INSERT TREATMENT_SERVICE ON;
INSERT INTO TREATMENT_SERVICE (service_id, treatment_code, service_name, duration_minutes, price, is_available, is_delete) VALUES
(1, 'TR001', N'Massage Thái Toàn Thân', 60, 500000, 1, 0),
(2, 'TR002', N'Chăm sóc da mặt chuyên sâu', 90, 800000, 1, 0),
(3, 'TR003', N'Bấm huyệt trị liệu', 45, 400000, 1, 0);
SET IDENTITY_INSERT TREATMENT_SERVICE OFF;

SET IDENTITY_INSERT TREATMENT_ROOM ON;
INSERT INTO TREATMENT_ROOM (room_id, room_code, room_name, status, is_delete) VALUES
<<<<<<< HEAD
(1, 'RM01', N'Phòng Spa VIP 1', 'AVAILABLE', 0),
(2, 'RM02', N'Phòng Spa Đôi 1', 'AVAILABLE', 0);
=======
(1, 'RM01', N'Phòng Spa VIP 1', 'Available', 0),
(2, 'RM02', N'Phòng Spa Đôi 1', 'Available', 0);
>>>>>>> 6a51c98 (update data)
SET IDENTITY_INSERT TREATMENT_ROOM OFF;

SET IDENTITY_INSERT RETREAT_PACKAGE ON;
INSERT INTO RETREAT_PACKAGE (package_id, type_package, package_name, duration_days, services, description, is_active, is_delete, price) VALUES
(1, 'Wellness', N'Gói Trị Liệu Thư Giãn 3 Ngày', 3, 'TR001, TR002', N'Bao gồm 3 ngày nghỉ dưỡng, 2 liệu trình spa', 1, 0, 15000000),
(2, 'Detox', N'Gói Thanh Lọc Cơ Thể 5 Ngày', 5, 'TR001, TR003', N'Thanh lọc cơ thể, ăn kiêng đặc biệt', 1, 0, 25000000);
SET IDENTITY_INSERT RETREAT_PACKAGE OFF;

SET IDENTITY_INSERT MENU_ITEM ON;
INSERT INTO MENU_ITEM (menu_item_id, item_name, price, ingredient, is_available) VALUES
(1, N'Salad Cá Hồi', 250000, N'Cá hồi, rau xà lách, sốt', 1),
(2, N'Ức Gà Nướng Mật Ong', 180000, N'Ức gà, mật ong, gia vị', 1),
(3, N'Nước Ép Cần Tây', 80000, N'Cần tây, táo', 1);
SET IDENTITY_INSERT MENU_ITEM OFF;

-- =========================================================================
-- 3. CHÈN DỮ LIỆU BOOKING, HÓA ĐƠN VÀ LỊCH TRÌNH SPA (DỮ LIỆU ĐỂ TEST DASHBOARD/CHART)
-- =========================================================================
SET IDENTITY_INSERT BOOKING ON;
INSERT INTO BOOKING (booking_id, guest_id, package_id, assigned_villa_id, checkin_date, checkout_date, total_guests, booking_status, payment_status, is_delete, create_at) VALUES
<<<<<<< HEAD
(1, 2, 1, 4, '2026-06-10', '2026-06-13', 2, 'CHECKED_IN', 'PARTIAL', 0, '2026-06-01 10:00:00'),
(2, 5, 2, null, '2026-06-20', '2026-06-25', 1, 'PENDING', 'UNPAID', 0, '2026-06-05 14:00:00'),
(3, 2, 1, 1, '2026-05-10', '2026-05-13', 2, 'CHECKED_OUT', 'PAID', 0, '2026-05-01 09:00:00'); -- Booking cũ để test biểu đồ doanh thu tháng 5
=======
(1, 2, 1, 4, '2026-06-10', '2026-06-13', 2, 'CHECKED-IN', 'DEPOSITED', 0, '2026-06-01 10:00:00'),
(2, 5, 2, null, '2026-06-20', '2026-06-25', 1, 'PENDING', 'UNPAID', 0, '2026-06-05 14:00:00'),
(3, 2, 1, 1, '2026-05-10', '2026-05-13', 2, 'COMPLETED', 'PAID', 0, '2026-05-01 09:00:00'); -- Booking cũ để test biểu đồ doanh thu tháng 5
>>>>>>> 6a51c98 (update data)
SET IDENTITY_INSERT BOOKING OFF;

SET IDENTITY_INSERT GUEST_FOLIO ON;
INSERT INTO GUEST_FOLIO (folio_id, booking_id, total_package_amout, total_extra_fb, final_amount, status, is_delete) VALUES
<<<<<<< HEAD
(1, 1, 15000000, 660000, 15660000, 'OPEN', 0),
(2, 2, 25000000, 0, 25000000, 'OPEN', 0),
(3, 3, 15000000, 500000, 15500000, 'CLOSED', 0);
=======
(1, 1, 15000000, 660000, 15660000, 'PENDING', 0),
(2, 2, 25000000, 0, 25000000, 'PENDING', 0),
(3, 3, 15000000, 500000, 15500000, 'PAID', 0);
>>>>>>> 6a51c98 (update data)
SET IDENTITY_INSERT GUEST_FOLIO OFF;

SET IDENTITY_INSERT TREATMENT_BOOKING ON;
INSERT INTO TREATMENT_BOOKING (treatment_id, booking_id, folio_id, service_id, note, status, is_delete) VALUES
<<<<<<< HEAD
(1, 1, 1, 1, N'Khách đau mỏi vai gáy', 'SCHEDULED', 0),
(2, 1, 1, 2, N'Khách da nhạy cảm', 'PENDING', 0),
(3, 3, 3, 1, N'Khách đã hoàn thành', 'COMPLETED', 0);
=======
(1, 1, 1, 1, N'Khách đau mỏi vai gáy', 'Confirmed', 0),
(2, 1, 1, 2, N'Khách da nhạy cảm', 'Pending', 0),
(3, 3, 3, 1, N'Khách đã hoàn thành', 'Completed', 0);
>>>>>>> 6a51c98 (update data)
SET IDENTITY_INSERT TREATMENT_BOOKING OFF;

SET IDENTITY_INSERT SCHEDULE ON;
INSERT INTO SCHEDULE (schedule_id, treatment_id, therapist_code, room_id, start_time, end_time, is_delete) VALUES
(1, 1, 'NV001', 1, '2026-06-11 09:00:00', '2026-06-11 10:00:00', 0),
(2, 2, 'NV002', 2, '2026-06-12 14:00:00', '2026-06-12 15:30:00', 0),
(3, 3, 'NV001', 1, '2026-05-11 09:00:00', '2026-05-11 10:00:00', 0);
SET IDENTITY_INSERT SCHEDULE OFF;

SET IDENTITY_INSERT MEAL_ORDER ON;
INSERT INTO MEAL_ORDER (meal_order_id, booking_id, folio_id, guest_id, place_order, order_status) VALUES
<<<<<<< HEAD
(1, 1, 1, 2, 'Room', 'DELIVERED');
=======
(1, 1, 1, 2, 'Room', 'Completed');
>>>>>>> 6a51c98 (update data)
SET IDENTITY_INSERT MEAL_ORDER OFF;

SET IDENTITY_INSERT MEAL_ORDER_ITEM ON;
INSERT INTO MEAL_ORDER_ITEM (order_item_id, meal_order_id, menu_item_id, quantity, price) VALUES
(1, 1, 1, 2, 250000),
(2, 1, 3, 2, 80000);
SET IDENTITY_INSERT MEAL_ORDER_ITEM OFF;

SET IDENTITY_INSERT FOLIO_ITEM ON;
INSERT INTO FOLIO_ITEM (folio_item_id, folio_id, service_category, reference_id, description, amount, status) VALUES
(1, 1, 'F&B', 1, N'Đồ ăn phòng: Salad Cá Hồi x 2, Nước Ép x 2', 660000, 'UNPAID'),
(2, 3, 'Spa', 3, N'Liệu trình phát sinh thêm', 500000, 'PAID');
SET IDENTITY_INSERT FOLIO_ITEM OFF;
<<<<<<< HEAD
=======

SET IDENTITY_INSERT PAYMENT ON;
INSERT INTO PAYMENT (payment_id, folio_id, amount, payment_method, payment_gateway, transaction_code, payment_date, status) VALUES
(1, 1, 5000000, 'Bank', 'VNPay', 'TX123456', '2026-06-01 10:05:00', 'SUCCESS'), -- Cọc
(2, 3, 15500000, 'Cash', 'Direct', 'TX000000', '2026-05-13 12:00:00', 'SUCCESS'); -- Thanh toán đủ booking tháng 5
SET IDENTITY_INSERT PAYMENT OFF;
>>>>>>> 6a51c98 (update data)
