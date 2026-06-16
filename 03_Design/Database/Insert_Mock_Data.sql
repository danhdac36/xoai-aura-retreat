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
(1, 1, 'admin@auramoon.com', '$2a$10$wE9XG8uW4fGf9sPZ4a0e4O5vW0yqXk6Zl7wE9XG8uW4fGf9sPZ4a0', N'Admin Tổng', 'Male', '1990-01-01', '0123456789', 'ID001', 'Active', 0),
(2, 2, 'guest1@gmail.com', '$2a$10$wE9XG8uW4fGf9sPZ4a0e4O5vW0yqXk6Zl7wE9XG8uW4fGf9sPZ4a0', N'Nguyễn Văn Khách', 'Male', '1995-05-15', '0987654321', 'ID002', 'Active', 0),
(3, 3, 'therapist1@auramoon.com', '$2a$10$wE9XG8uW4fGf9sPZ4a0e4O5vW0yqXk6Zl7wE9XG8uW4fGf9sPZ4a0', N'Trần Thị Spa', 'Female', '1992-08-20', '0912345678', 'ID003', 'Active', 0),
(4, 3, 'therapist2@auramoon.com', '$2a$10$wE9XG8uW4fGf9sPZ4a0e4O5vW0yqXk6Zl7wE9XG8uW4fGf9sPZ4a0', N'Lê Văn Massage', 'Male', '1988-11-10', '0933445566', 'ID004', 'Active', 0),
(5, 2, 'guest2@gmail.com', '$2a$10$wE9XG8uW4fGf9sPZ4a0e4O5vW0yqXk6Zl7wE9XG8uW4fGf9sPZ4a0', N'Hoàng Thị Khách 2', 'Female', '1998-02-12', '0922334455', 'ID005', 'Active', 0);
SET IDENTITY_INSERT [USER] OFF;

INSERT INTO THERAPIST (therapist_id, therapist_code, status) VALUES 
(3, 'NV001', 'Active'),
(4, 'NV002', 'Active');

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
(1, 1, 'GV01', 2, 'Available', 'Clean', 0),
(2, 1, 'GV02', 2, 'Available', 'Clean', 0),
(3, 2, 'OV01', 4, 'Available', 'Clean', 0),
(4, 2, 'OV02', 4, 'Occupied', 'Clean', 0),
(5, 3, 'PR01', 6, 'Available', 'Clean', 0);
SET IDENTITY_INSERT VILLA OFF;

SET IDENTITY_INSERT TREATMENT_SERVICE ON;
INSERT INTO TREATMENT_SERVICE (service_id, treatment_code, service_name, duration_minutes, price, is_available, is_delete) VALUES
(1, 'TR001', N'Massage Thái Toàn Thân', 60, 500000, 1, 0),
(2, 'TR002', N'Chăm sóc da mặt chuyên sâu', 90, 800000, 1, 0),
(3, 'TR003', N'Bấm huyệt trị liệu', 45, 400000, 1, 0);
SET IDENTITY_INSERT TREATMENT_SERVICE OFF;

SET IDENTITY_INSERT TREATMENT_ROOM ON;
INSERT INTO TREATMENT_ROOM (room_id, room_code, room_name, status, is_delete) VALUES
(1, 'RM01', N'Phòng Spa VIP 1', 'Available', 0),
(2, 'RM02', N'Phòng Spa Đôi 1', 'Available', 0);
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
(1, 2, 1, 4, '2026-06-10', '2026-06-13', 2, 'CHECKED_IN', 'PARTIAL', 0, '2026-06-01 10:00:00'),
(2, 5, 2, null, '2026-06-20', '2026-06-25', 1, 'PENDING', 'UNPAID', 0, '2026-06-05 14:00:00'),
(3, 2, 1, 1, '2026-05-10', '2026-05-13', 2, 'CHECKED_OUT', 'PAID', 0, '2026-05-01 09:00:00'),
(4, 2, 2, 2, '2026-01-10', '2026-01-15', 2, 'CHECKED_OUT', 'PAID', 0, '2026-01-01 09:00:00'),
(5, 5, 1, 3, '2026-02-14', '2026-02-18', 2, 'CHECKED_OUT', 'PAID', 0, '2026-02-01 09:00:00'),
(6, 2, 2, 1, '2026-03-08', '2026-03-12', 2, 'CHECKED_OUT', 'PAID', 0, '2026-03-01 09:00:00'),
(7, 5, 1, 2, '2026-04-30', '2026-05-04', 2, 'CHECKED_OUT', 'PAID', 0, '2026-04-01 09:00:00'),
(8, 2, 2, 3, '2026-06-01', '2026-06-05', 2, 'CHECKED_OUT', 'PAID', 0, '2026-05-20 09:00:00');
SET IDENTITY_INSERT BOOKING OFF;

SET IDENTITY_INSERT GUEST_FOLIO ON;
INSERT INTO GUEST_FOLIO (folio_id, booking_id, total_package_amout, total_extra_fb, final_amount, status, is_delete, create_at) VALUES
(1, 1, 15000000, 660000, 15660000, 'OPEN', 0, '2026-06-10 14:00:00'),
(2, 2, 25000000, 0, 25000000, 'OPEN', 0, '2026-06-20 14:00:00'),
(3, 3, 15000000, 500000, 15500000, 'PAID', 0, '2026-05-13 12:00:00'),
(4, 4, 25000000, 1200000, 26200000, 'PAID', 0, '2026-01-15 12:00:00'),
(5, 5, 15000000, 800000, 15800000, 'PAID', 0, '2026-02-18 12:00:00'),
(6, 6, 25000000, 2000000, 27000000, 'PAID', 0, '2026-03-12 12:00:00'),
(7, 7, 15000000, 1500000, 16500000, 'PAID', 0, '2026-05-04 12:00:00'),
(8, 8, 25000000, 3000000, 28000000, 'PAID', 0, '2026-06-05 12:00:00');
SET IDENTITY_INSERT GUEST_FOLIO OFF;

SET IDENTITY_INSERT TREATMENT_BOOKING ON;
INSERT INTO TREATMENT_BOOKING (treatment_id, booking_id, folio_id, service_id, note, status, is_delete) VALUES
(1, 1, 1, 1, N'Khách đau mỏi vai gáy', 'SCHEDULED', 0),
(2, 1, 1, 2, N'Khách da nhạy cảm', 'PENDING', 0),
(3, 3, 3, 1, N'Khách đã hoàn thành', 'COMPLETED', 0),
(4, 4, 4, 2, N'Làm dịch vụ thêm', 'COMPLETED', 0),
(5, 5, 5, 3, N'Trị liệu bấm huyệt', 'COMPLETED', 0),
(6, 6, 6, 1, N'Massage thái', 'COMPLETED', 0),
(7, 7, 7, 2, N'Chăm sóc da', 'COMPLETED', 0),
(8, 8, 8, 3, N'Bấm huyệt', 'COMPLETED', 0);
SET IDENTITY_INSERT TREATMENT_BOOKING OFF;

SET IDENTITY_INSERT SCHEDULE ON;
INSERT INTO SCHEDULE (schedule_id, treatment_id, therapist_code, room_id, start_time, end_time, is_delete) VALUES
(1, 1, 'NV001', 1, '2026-06-11 09:00:00', '2026-06-11 10:00:00', 0),
(2, 2, 'NV002', 2, '2026-06-12 14:00:00', '2026-06-12 15:30:00', 0),
(3, 3, 'NV001', 1, '2026-05-11 09:00:00', '2026-05-11 10:00:00', 0),
(4, 4, 'NV002', 2, '2026-01-12 10:00:00', '2026-01-12 11:30:00', 0),
(5, 5, 'NV001', 1, '2026-02-15 14:00:00', '2026-02-15 14:45:00', 0),
(6, 6, 'NV002', 2, '2026-03-10 16:00:00', '2026-03-10 17:00:00', 0),
(7, 7, 'NV001', 1, '2026-05-02 09:00:00', '2026-05-02 10:30:00', 0),
(8, 8, 'NV002', 2, '2026-06-03 15:00:00', '2026-06-03 15:45:00', 0);
SET IDENTITY_INSERT SCHEDULE OFF;

SET IDENTITY_INSERT MEAL_ORDER ON;
INSERT INTO MEAL_ORDER (meal_order_id, booking_id, folio_id, guest_id, place_order, order_status) VALUES
(1, 1, 1, 2, 'Room', 'DELIVERED'),
(2, 4, 4, 2, 'Restaurant', 'DELIVERED'),
(3, 5, 5, 5, 'Room', 'DELIVERED'),
(4, 6, 6, 2, 'Restaurant', 'DELIVERED'),
(5, 7, 7, 5, 'Room', 'DELIVERED'),
(6, 8, 8, 2, 'Restaurant', 'DELIVERED');
SET IDENTITY_INSERT MEAL_ORDER OFF;

SET IDENTITY_INSERT MEAL_ORDER_ITEM ON;
INSERT INTO MEAL_ORDER_ITEM (order_item_id, meal_order_id, menu_item_id, quantity, price) VALUES
(1, 1, 1, 2, 250000),
(2, 1, 3, 2, 80000),
(3, 2, 2, 3, 180000),
(4, 3, 1, 1, 250000),
(5, 4, 2, 4, 180000),
(6, 5, 3, 5, 80000),
(7, 6, 1, 3, 250000);
SET IDENTITY_INSERT MEAL_ORDER_ITEM OFF;

SET IDENTITY_INSERT FOLIO_ITEM ON;
INSERT INTO FOLIO_ITEM (folio_item_id, folio_id, service_category, reference_id, description, amount, status) VALUES
(1, 1, 'F_AND_B', 1, N'Đồ ăn phòng: Salad Cá Hồi x 2, Nước Ép x 2', 660000, 'UNPAID'),
(2, 3, 'SPA', 3, N'Liệu trình phát sinh thêm', 500000, 'PAID'),
(3, 4, 'F_AND_B', 2, N'Đồ ăn nhà hàng', 540000, 'PAID'),
(4, 4, 'SPA', 4, N'Chăm sóc da mặt', 800000, 'PAID'),
(5, 5, 'F_AND_B', 3, N'Đồ ăn phòng', 250000, 'PAID'),
(6, 5, 'SPA', 5, N'Bấm huyệt trị liệu', 400000, 'PAID'),
(7, 6, 'F_AND_B', 4, N'Đồ ăn nhà hàng', 720000, 'PAID'),
(8, 6, 'SPA', 6, N'Massage Thái', 500000, 'PAID'),
(9, 7, 'F_AND_B', 5, N'Nước ép cần tây', 400000, 'PAID'),
(10, 7, 'SPA', 7, N'Chăm sóc da', 800000, 'PAID'),
(11, 8, 'F_AND_B', 6, N'Salad cá hồi', 750000, 'PAID'),
(12, 8, 'SPA', 8, N'Bấm huyệt', 400000, 'PAID');
SET IDENTITY_INSERT FOLIO_ITEM OFF;

SET IDENTITY_INSERT PAYMENT ON;
INSERT INTO PAYMENT (payment_id, folio_id, amount, payment_method, payment_gateway, transaction_code, payment_date, status) VALUES
(1, 1, 5000000, 'BANK_TRANSFER', 'VNPAY', 'TX123456', '2026-06-01 10:05:00', 'SUCCESS'),
(2, 3, 15500000, 'CASH', 'DIRECT', 'TX000000', '2026-05-13 12:00:00', 'SUCCESS'),
(3, 4, 26200000, 'CREDIT_CARD', 'MOMO', 'TX000001', '2026-01-15 12:00:00', 'SUCCESS'),
(4, 5, 15800000, 'CASH', 'DIRECT', 'TX000002', '2026-02-18 12:00:00', 'SUCCESS'),
(5, 6, 27000000, 'BANK_TRANSFER', 'VNPAY', 'TX000003', '2026-03-12 12:00:00', 'SUCCESS'),
(6, 7, 16500000, 'CREDIT_CARD', 'MOMO', 'TX000004', '2026-05-04 12:00:00', 'SUCCESS'),
(7, 8, 28000000, 'CASH', 'DIRECT', 'TX000005', '2026-06-05 12:00:00', 'SUCCESS');
SET IDENTITY_INSERT PAYMENT OFF;
