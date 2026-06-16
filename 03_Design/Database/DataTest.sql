USE HoS;
GO

-- =========================================================================
-- BƯỚC 1: XÓA SẠCH DỮ LIỆU CŨ THEO THỨ TỰ NGƯỢC (ĐỂ KHÔNG BỊ DÍNH KHÓA NGOẠ)
-- =========================================================================
DELETE FROM AUDIT_LOG;
DELETE FROM PAYMENT;
DELETE FROM REVIEW;
DELETE FROM FOLIO_ITEM;
DELETE FROM MEAL_ORDER_ITEM;
DELETE FROM MEAL_ORDER;
DELETE FROM MENU_ITEM;
DELETE FROM SCHEDULE;
DELETE FROM TREATMENT_BOOKING;
DELETE FROM GUEST_FOLIO;
DELETE FROM BOOKING;
DELETE FROM RETREAT_PACKAGE;
DELETE FROM VILLA;
DELETE FROM VILLA_TYPE;
DELETE FROM TREATMENT_ROOM;
DELETE FROM TREATMENT_SERVICE;
DELETE FROM DIETARY_PROFILE;
DELETE FROM PHYSICAL_HEALTH_PROFILE;
DELETE FROM CONSENT;
DELETE FROM THERAPIST;
DELETE FROM [USER];

-- Xóa dữ liệu cũ trong bảng ROLE và bật IDENTITY_INSERT để nạp lại đúng ID mong muốn
DELETE FROM [ROLE];
SET IDENTITY_INSERT [ROLE] ON;
INSERT INTO [ROLE] (role_id, role_name) VALUES 
(1, 'ADMIN'), (2, 'MANAGER'), (3, 'THERAPIST'), (4, 'RECEPTIONIST'), (5, 'CHEFF'), (6, 'GUEST');
SET IDENTITY_INSERT [ROLE] OFF;

-- =========================================================================
-- BƯỚC 2: CHÈN LẠI BẢNG USER (Sinh Id từ 1 đến 18)
-- =========================================================================
-- 1 Admin, 1 Manager, 10 Therapists, 1 Receptionist, 1 Chef, 4 Guests
INSERT INTO [USER] (role_id, email, password_hash, full_name, gender, status) VALUES 
(1, 'admin@hos.com', 'hash', 'Admin System', 'Male', 'Active'), -- user_id = 1
(2, 'manager1@hos.com', 'hash', 'Manager One', 'Female', 'Active'), -- user_id = 2
(3, 'therapist1@hos.com', 'hash', 'Therapist A', 'Female', 'Active'), -- user_id = 3
(3, 'therapist2@hos.com', 'hash', 'Therapist B', 'Male', 'Active'), -- user_id = 4
(4, 'recep1@hos.com', 'hash', 'Receptionist A', 'Female', 'Active'), -- user_id = 5
(5, 'chef1@hos.com', 'hash', 'Chef John', 'Male', 'Active'), -- user_id = 6
(6, 'guest1@hos.com', 'hash', 'Guest One', 'Male', 'Active'), -- user_id = 7
(6, 'guest2@hos.com', 'hash', 'Guest Two', 'Female', 'Active'), -- user_id = 8
(6, 'guest3@hos.com', 'hash', 'Guest Three', 'Male', 'Active'), -- user_id = 9
(6, 'guest4@hos.com', 'hash', 'Guest Four', 'Female', 'Active'), -- user_id = 10
(3, 'therapist3@hos.com', 'hash', 'Therapist C', 'Female', 'Active'), -- user_id = 11
(3, 'therapist4@hos.com', 'hash', 'Therapist D', 'Male', 'Active'), -- user_id = 12
(3, 'therapist5@hos.com', 'hash', 'Therapist E', 'Female', 'Active'), -- user_id = 13
(3, 'therapist6@hos.com', 'hash', 'Therapist F', 'Male', 'Active'), -- user_id = 14
(3, 'therapist7@hos.com', 'hash', 'Therapist G', 'Female', 'Active'), -- user_id = 15
(3, 'therapist8@hos.com', 'hash', 'Therapist H', 'Male', 'Active'), -- user_id = 16
(3, 'therapist9@hos.com', 'hash', 'Therapist I', 'Female', 'Active'), -- user_id = 17
(3, 'therapist10@hos.com', 'hash', 'Therapist J', 'Male', 'Active'); -- user_id = 18

-- Lấy chính xác các ID của GUEST vừa tạo để dùng cho các bảng Profile và Booking
DECLARE @G1 INT = (SELECT user_id FROM [USER] WHERE email = 'guest1@hos.com');
DECLARE @G2 INT = (SELECT user_id FROM [USER] WHERE email = 'guest2@hos.com');
DECLARE @G3 INT = (SELECT user_id FROM [USER] WHERE email = 'guest3@hos.com');
DECLARE @G4 INT = (SELECT user_id FROM [USER] WHERE email = 'guest4@hos.com');

-- =========================================================================
-- BƯỚC 3: SỬA LỖI FK_CONSENT, FK_PHYSICAL, FK_DIETARY (Gắn vào đúng ID của Guest)
-- =========================================================================
INSERT INTO CONSENT (user_id, consent_status, consent_version) VALUES 
(@G1, 1, 'V1.0'), (@G2, 1, 'V1.0'), (@G3, 1, 'V1.0'), (@G4, 1, 'V1.0');

INSERT INTO PHYSICAL_HEALTH_PROFILE (user_id, medical_conditions, injuries) VALUES 
(@G1, N'Không có', N'Không có'),
(@G2, N'Cao huyết áp nhẹ', N'Đau khớp gối trái'),
(@G3, N'Không có', N'Chấn thương cổ tay cũ'),
(@G4, N'Tiểu đường tuýp 2', N'Không có');

INSERT INTO DIETARY_PROFILE (user_id, food_allergies, diatary_preference) VALUES 
(@G1, N'Hải sản', N'Ăn chay bán phần'),
(@G2, N'Không có', N'Ít tinh bột (Low-carb)'),
(@G3, N'Đậu phộng', N'Không ăn cay'),
(@G4, N'Không có', N'Ăn thuần chay (Vegan)');

-- =========================================================================
-- BƯỚC 4: CHÈN BẢNG THERAPIST (Map chính xác cặp user_id có role_id = 3)
-- =========================================================================
INSERT INTO THERAPIST (therapist_id, therapist_code, status) VALUES 
((SELECT user_id FROM [USER] WHERE email = 'therapist1@hos.com'), 'TH0001', 'Active'),
((SELECT user_id FROM [USER] WHERE email = 'therapist2@hos.com'), 'TH0002', 'Active'),
((SELECT user_id FROM [USER] WHERE email = 'therapist3@hos.com'), 'TH0003', 'Active'),
((SELECT user_id FROM [USER] WHERE email = 'therapist4@hos.com'), 'TH0004', 'Active'),
((SELECT user_id FROM [USER] WHERE email = 'therapist5@hos.com'), 'TH0005', 'Active'),
((SELECT user_id FROM [USER] WHERE email = 'therapist6@hos.com'), 'TH0006', 'Active'),
((SELECT user_id FROM [USER] WHERE email = 'therapist7@hos.com'), 'TH0007', 'Active'),
((SELECT user_id FROM [USER] WHERE email = 'therapist8@hos.com'), 'TH0008', 'Active'),
((SELECT user_id FROM [USER] WHERE email = 'therapist9@hos.com'), 'TH0009', 'Active'),
((SELECT user_id FROM [USER] WHERE email = 'therapist10@hos.com'), 'TH0010', 'Active');


-- =========================================================================
-- BƯỚC 5: CÁC BẢNG DANH MỤC KHÁC (CẤU TRÚC ĐỘC LẬP)
-- =========================================================================
INSERT INTO TREATMENT_SERVICE (treatment_code, service_name, duration_minutes, price, is_available) VALUES 
('TR01', N'Massage toàn thân', 60, 500000, 1), ('TR02', N'Chăm sóc da mặt', 45, 300000, 1),
('TR03', N'Gội đầu dưỡng sinh', 30, 200000, 1), ('TR04', N'Xông hơi thảo dược', 40, 250000, 1),
('TR05', N'Ngâm chân thuốc bắc', 30, 150000, 1), ('TR06', N'Trị liệu đau lưng', 60, 700000, 1),
('TR07', N'Tẩy tế bào chết', 45, 400000, 1), ('TR08', N'Massage chân', 45, 350000, 1),
('TR09', N'Thiền định', 60, 200000, 1), ('TR10', N'Yoga trị liệu', 60, 300000, 1);

INSERT INTO TREATMENT_ROOM (room_code, room_name, status) VALUES 
('R01', N'Phòng Thảo Dược 1', 'Available'), ('R02', N'Phòng Thảo Dược 2', 'Available'),
('R03', N'Phòng Massage Body VIP', 'Occupied'), ('R04', N'Phòng Chăm Sóc Da', 'Available'),
('R05', N'Phòng Trị Liệu Đá Nóng', 'Available'), ('R06', N'Phòng Thiền Yên Tĩnh', 'Available'),
('R07', N'Phòng Yoga 1', 'Occupied'), ('R08', N'Phòng Xông Hơi Ướt', 'Available'),
('R09', N'Phòng Xông Hơi Khô', 'Available'), ('R10', N'Phòng Gội Đầu Dưỡng Sinh', 'Available');

INSERT INTO VILLA_TYPE (type_name, limit_person, price_per_day) VALUES 
(N'Villa Đơn', 2, 2000000), (N'Villa Đôi', 4, 3500000), (N'Villa Gia Đình', 6, 5000000), (N'Villa VIP', 2, 8000000), (N'Villa View Biển', 4, 6000000),
(N'Villa Vườn', 2, 2500000), (N'Villa Pool', 4, 4500000), (N'Villa Gỗ', 2, 3000000), (N'Villa Hiện Đại', 4, 4000000), (N'Villa Trăng Mật', 2, 5500000);

-- Lấy Id động của Villa Type
DECLARE @VT1 INT = (SELECT TOP 1 type_id FROM VILLA_TYPE WHERE type_name = N'Villa Đơn');
DECLARE @VT2 INT = (SELECT TOP 1 type_id FROM VILLA_TYPE WHERE type_name = N'Villa Đôi');
DECLARE @VT3 INT = (SELECT TOP 1 type_id FROM VILLA_TYPE WHERE type_name = N'Villa Gia Đình');

INSERT INTO VILLA (villa_type, villa_code, max_number, villa_status, cleaning_status) VALUES 
(@VT1, 'V01', 2, 'Available', 'Clean'), (@VT1, 'V02', 2, 'Available', 'Clean'),
(@VT2, 'V03', 4, 'Occupied', 'Dirty'), (@VT2, 'V04', 4, 'Available', 'Clean'),
(@VT3, 'V05', 6, 'Available', 'Clean'), (@VT1, 'V06', 2, 'Available', 'Clean'),
(@VT2, 'V07', 4, 'Available', 'Clean'), (@VT1, 'V08', 2, 'Available', 'Clean'),
(@VT2, 'V09', 4, 'Occupied', 'Dirty'), (@VT1, 'V10', 2, 'Available', 'Clean');

INSERT INTO RETREAT_PACKAGE (type_package, package_name, duration_days, services, description, price) VALUES 
(N'Thải độc', N'Gói Thanh Lọc Thân Tâm', 3, 'TR04, TR05', N'Thanh lọc độc tố', 5000000),
(N'Trị liệu', N'Gói Phục Hồi Cột Sống', 5, 'TR01, TR06', N'Chuyên sâu đau lưng', 8500000),
(N'Thư giãn', N'Gói Trẻ Hóa Làn Da', 2, 'TR02, TR03', N'Chăm sóc sắc đẹp', 4000000),
(N'Tinh thần', N'Gói Tĩnh Tâm Thiền Định', 3, 'TR09, TR10', N'Cân bằng giảm stress', 3500000),
(N'Cao cấp', N'Gói Hoàng Gia Toàn Diện', 7, 'ALL', N'Trải nghiệm tất cả', 15000000),
(N'Ngắn ngày', N'Gói Năng Lượng Ngày Mới', 1, 'TR01', N'F5 bản thân trong 1 ngày', 1200000),
(N'Cặp đôi', N'Gói Trăng Mật Ngọt Ngào', 3, 'TR01, TR02', N'Thiết kế riêng cặp đôi', 9000000),
(N'Trị liệu', N'Gói Giảm Căng Thẳng', 4, 'TR01, TR08', N'Phục hồi cơ bắp', 6000000),
(N'Thần trí', N'Gói Ngủ Ngon Sâu Giấc', 3, 'TR04, TR05', N'Cải thiện chứng mất ngủ', 4800000),
(N'Đặc biệt', N'Gói Trải Nghiệm HoS', 2, 'TR01, TR10', N'Gói làm quen Resort', 3000000);

-- =========================================================================
-- BƯỚC 6: CHÈN BOOKING (Lấy ID động từ Villa và Package)
-- =========================================================================
DECLARE @P1 INT = (SELECT TOP 1 package_id FROM RETREAT_PACKAGE WHERE package_name = N'Gói Thanh Lọc Thân Tâm');
DECLARE @P2 INT = (SELECT TOP 1 package_id FROM RETREAT_PACKAGE WHERE package_name = N'Gói Phục Hồi Cột Sống');
DECLARE @V1 INT = (SELECT TOP 1 villa_id FROM VILLA WHERE villa_code = 'V01');
DECLARE @V3 INT = (SELECT TOP 1 villa_id FROM VILLA WHERE villa_code = 'V03');

INSERT INTO BOOKING (guest_id, package_id, assigned_villa_id, checkin_date, checkout_date, total_guests, booking_status, payment_status) VALUES 
(@G1, @P1, @V1, '2026-06-01', '2026-06-04', 2, 'Completed', 'Paid'),
(@G2, @P2, @V3, '2026-06-02', '2026-06-07', 3, 'Checked In', 'Paid'),
(@G3, @P1, @V1, '2026-06-10', '2026-06-12', 1, 'Completed', 'Paid'),
(@G4, @P2, @V3, '2026-06-14', '2026-06-17', 2, 'Confirmed', 'Unpaid'),
(@G1, @P1, @V1, '2026-07-01', '2026-07-02', 1, 'Confirmed', 'Paid'),
(@G2, @P2, @V3, '2026-07-05', '2026-07-12', 4, 'Confirmed', 'Unpaid'),
(@G3, @P1, @V1, '2026-08-01', '2026-08-04', 2, 'Confirmed', 'Unpaid'),
(@G4, @P2, @V3, '2026-06-15', '2026-06-19', 2, 'Confirmed', 'Paid'),
(@G1, @P1, @V1, '2026-06-20', '2026-06-23', 2, 'Confirmed', 'Unpaid'),
(@G2, @P2, @V3, '2026-06-25', '2026-06-27', 2, 'Confirmed', 'Unpaid');

-- Lấy danh sách ID của Booking vừa sinh ra
DECLARE @B1 INT = (SELECT TOP 1 booking_id FROM BOOKING WHERE guest_id = @G1 AND checkin_date = '2026-06-01');
DECLARE @B2 INT = (SELECT TOP 1 booking_id FROM BOOKING WHERE guest_id = @G2 AND checkin_date = '2026-06-02');
DECLARE @B3 INT = (SELECT TOP 1 booking_id FROM BOOKING WHERE guest_id = @G3 AND checkin_date = '2026-06-10');
DECLARE @B4 INT = (SELECT TOP 1 booking_id FROM BOOKING WHERE guest_id = @G4 AND checkin_date = '2026-06-14');
DECLARE @B5 INT = (SELECT TOP 1 booking_id FROM BOOKING WHERE guest_id = @G1 AND checkin_date = '2026-07-01');
DECLARE @B6 INT = (SELECT TOP 1 booking_id FROM BOOKING WHERE guest_id = @G2 AND checkin_date = '2026-07-05');
DECLARE @B7 INT = (SELECT TOP 1 booking_id FROM BOOKING WHERE guest_id = @G3 AND checkin_date = '2026-08-01');
DECLARE @B8 INT = (SELECT TOP 1 booking_id FROM BOOKING WHERE guest_id = @G4 AND checkin_date = '2026-06-15');
DECLARE @B9 INT = (SELECT TOP 1 booking_id FROM BOOKING WHERE guest_id = @G1 AND checkin_date = '2026-06-20');
DECLARE @B10 INT = (SELECT TOP 1 booking_id FROM BOOKING WHERE guest_id = @G2 AND checkin_date = '2026-06-25');

-- =========================================================================
-- BƯỚC 7: CÁC BẢNG LIÊN QUAN ĐẾN BOOKING & FOLIO
-- =========================================================================
INSERT INTO GUEST_FOLIO (booking_id, total_package_amout, total_extra_fb, final_amount, status) VALUES 
(@B1, 5000000, 500000, 5500000, 'Closed'), (@B2, 8500000, 1200000, 9700000, 'Active'),
(@B3, 4000000, 300000, 4300000, 'Closed'), (@B4, 3500000, 0, 3500000, 'Active'),
(@B5, 1200000, 150000, 1350000, 'Active'), (@B6, 15000000, 0, 15000000, 'Active'),
(@B7, 9000000, 0, 9000000, 'Active'), (@B8, 6000000, 400000, 6400000, 'Active'),
(@B9, 4800000, 0, 4800000, 'Active'), (@B10, 3000000, 200000, 3200000, 'Active');

DECLARE @F1 INT = (SELECT TOP 1 folio_id FROM GUEST_FOLIO WHERE booking_id = @B1);
DECLARE @F2 INT = (SELECT TOP 1 folio_id FROM GUEST_FOLIO WHERE booking_id = @B2);
DECLARE @F3 INT = (SELECT TOP 1 folio_id FROM GUEST_FOLIO WHERE booking_id = @B3);
DECLARE @F4 INT = (SELECT TOP 1 folio_id FROM GUEST_FOLIO WHERE booking_id = @B4);
DECLARE @F5 INT = (SELECT TOP 1 folio_id FROM GUEST_FOLIO WHERE booking_id = @B5);
DECLARE @F6 INT = (SELECT TOP 1 folio_id FROM GUEST_FOLIO WHERE booking_id = @B6);
DECLARE @F7 INT = (SELECT TOP 1 folio_id FROM GUEST_FOLIO WHERE booking_id = @B7);
DECLARE @F8 INT = (SELECT TOP 1 folio_id FROM GUEST_FOLIO WHERE booking_id = @B8);
DECLARE @F9 INT = (SELECT TOP 1 folio_id FROM GUEST_FOLIO WHERE booking_id = @B9);
DECLARE @F10 INT = (SELECT TOP 1 folio_id FROM GUEST_FOLIO WHERE booking_id = @B10);

-- SỬA LỖI FK_SCHEDULE_THERAPIST: Chèn Treatment Booking và Schedule chuẩn mã Therapist
DECLARE @S1 INT = (SELECT TOP 1 service_id FROM TREATMENT_SERVICE WHERE treatment_code = 'TR01');
DECLARE @S2 INT = (SELECT TOP 1 service_id FROM TREATMENT_SERVICE WHERE treatment_code = 'TR02');

INSERT INTO TREATMENT_BOOKING (booking_id, folio_id, service_id, note, status) VALUES 
(@B1, @F1, @S1, N'Nhẹ tay', 'Completed'), (@B2, @F2, @S2, N'Đau lưng', 'Confirmed'),
(@B3, @F3, @S1, N'Da nhạy cảm', 'Completed'), (@B4, @F4, @S2, N'Yên tĩnh', 'Confirmed'),
(@B5, @F5, @S1, N'Nước ấm', 'Confirmed'), (@B6, @F6, @S2, N'Cơ đùi', 'Confirmed'),
(@B7, @F7, @S1, N'Không', 'Confirmed'), (@B8, @F8, @S2, N'Lực mạnh', 'Confirmed'),
(@B9, @F9, @S1, N'Thêm sả', 'Confirmed'), (@B10, @F10, @S2, N'Ấm vừa', 'Confirmed');

DECLARE @TB1 INT = (SELECT TOP 1 treatment_id FROM TREATMENT_BOOKING WHERE booking_id = @B1);
DECLARE @TB2 INT = (SELECT TOP 1 treatment_id FROM TREATMENT_BOOKING WHERE booking_id = @B2);
DECLARE @TB3 INT = (SELECT TOP 1 treatment_id FROM TREATMENT_BOOKING WHERE booking_id = @B3);
DECLARE @TB4 INT = (SELECT TOP 1 treatment_id FROM TREATMENT_BOOKING WHERE booking_id = @B4);
DECLARE @TB5 INT = (SELECT TOP 1 treatment_id FROM TREATMENT_BOOKING WHERE booking_id = @B5);
DECLARE @TB6 INT = (SELECT TOP 1 treatment_id FROM TREATMENT_BOOKING WHERE booking_id = @B6);
DECLARE @TB7 INT = (SELECT TOP 1 treatment_id FROM TREATMENT_BOOKING WHERE booking_id = @B7);
DECLARE @TB8 INT = (SELECT TOP 1 treatment_id FROM TREATMENT_BOOKING WHERE booking_id = @B8);
DECLARE @TB9 INT = (SELECT TOP 1 treatment_id FROM TREATMENT_BOOKING WHERE booking_id = @B9);
DECLARE @TB10 INT = (SELECT TOP 1 treatment_id FROM TREATMENT_BOOKING WHERE booking_id = @B10);

-- Fix lỗi "FK_SCHEDULE_THERAPIST": Dùng chính xác mã code từ bảng THERAPIST
INSERT INTO SCHEDULE (treatment_id, therapist_code, room_id, start_time, end_time) VALUES 
(@TB1, 'TH0001', 1, '2026-06-02 09:00:00', '2026-06-02 10:00:00'),
(@TB2, 'TH0002', 2, '2026-06-03 14:00:00', '2026-06-03 15:00:00'),
(@TB3, 'TH0003', 3, '2026-06-11 10:00:00', '2026-06-11 10:45:00'),
(@TB4, 'TH0004', 4, '2026-06-15 16:00:00', '2026-06-15 17:00:00'),
(@TB5, 'TH0005', 5, '2026-07-01 08:30:00', '2026-07-01 09:00:00'),
(@TB6, 'TH0006', 6, '2026-07-06 07:00:00', '2026-07-06 08:00:00'),
(@TB7, 'TH0007', 7, '2026-08-02 15:00:00', '2026-08-02 16:00:00'),
(@TB8, 'TH0008', 8, '2026-06-16 13:00:00', '2026-06-16 13:45:00'),
(@TB9, 'TH0009', 9, '2026-06-21 09:30:00', '2026-06-21 10:10:00'),
(@TB10, 'TH0010', 10, '2026-06-26 11:00:00', '2026-06-26 11:30:00');

-- =========================================================================
-- BƯỚC 8: CÁC BẢNG NHÀ HÀNG & HÓA ĐƠN EXTRA
-- =========================================================================
INSERT INTO MENU_ITEM (item_name, price, ingredient, is_available) VALUES 
(N'Salad Ức Gà', 120000, N'Ức gà', 1), (N'Súp Hạt Sen', 80000, N'Hạt sen', 1),
(N'Nước Ép Cần Tây', 50000, N'Cần tây', 1), (N'Cháo Thực Dưỡng', 60000, N'Gạo lứt', 1),
(N'Cá Hồi Áp Chảo', 250000, N'Cá hồi', 1), (N'Trà Thảo Mộc', 45000, N'Cúc hoa', 1),
(N'Sinh Tố Detox', 75000, N'Việt quất', 1), (N'Đậu Hũ Sốt Nấm', 90000, N'Đậu hũ', 1),
(N'Gà Cuộn Lá Chanh', 180000, N'Thịt gà', 1), (N'Canh Rong Biển', 70000, N'Rong biển', 1);

DECLARE @CH INT = (SELECT user_id FROM [USER] WHERE email = 'chef1@hos.com');

INSERT INTO MEAL_ORDER (booking_id, folio_id, guest_id, ordered_by, place_order, order_status) VALUES 
(@B1, @F1, @G1, @CH, N'Nhà hàng', 'Served'), (@B2, @F2, @G2, @CH, N'Villa V03', 'Served'),
(@B3, @F3, @G3, @CH, N'Hồ bơi', 'Served'), (@B4, @F4, @G4, @CH, N'Nhà hàng', 'Pending'),
(@B5, @F5, @G1, @CH, N'Villa V01', 'Pending'), (@B6, @F6, @G2, @CH, N'Nhà hàng', 'Pending'),
(@B7, @F7, @G3, @CH, N'Hồ bơi', 'Pending'), (@B8, @F8, @G4, @CH, N'Villa V06', 'Served'),
(@B9, @F9, @G1, @CH, N'Nhà hàng', 'Pending'), (@B10, @F10, @G2, @CH, N'Villa V07', 'Served');

DECLARE @MO1 INT = (SELECT TOP 1 meal_order_id FROM MEAL_ORDER WHERE booking_id = @B1);
DECLARE @MI1 INT = (SELECT TOP 1 menu_item_id FROM MENU_ITEM WHERE item_name = N'Salad Ức Gà');

INSERT INTO MEAL_ORDER_ITEM (meal_order_id, menu_item_id, quantity, price) VALUES 
(@MO1, @MI1, 2, 120000), (@MO1, @MI1+1, 1, 80000), (@MO1+1, @MI1+2, 2, 50000), (@MO1+2, @MI1+3, 1, 60000), (@MO1+3, @MI1+4, 1, 250000),
(@MO1+4, @MI1+5, 2, 45000), (@MO1+5, @MI1+6, 1, 75000), (@MO1+6, @MI1+7, 2, 90000), (@MO1+7, @MI1+8, 1, 180000), (@MO1+8, @MI1+9, 1, 70000);

INSERT INTO FOLIO_ITEM (folio_id, service_category, reference_id, description, amount, status) VALUES 
(@F1, N'Package', @P1, N'Gói Thanh Lọc', 5000000, 'Post'), (@F1, N'F&B', @MO1, N'Nhà hàng', 340000, 'Post'),
(@F2, N'Package', @P2, N'Gói Phục Hồi', 8500000, 'Post'), (@F3, N'Package', @P1, N'Gói Thanh Lọc', 4000000, 'Post'),
(@F4, N'Package', @P2, N'Gói Phục Hồi', 3500000, 'Post'), (@F5, N'Package', @P1, N'Gói Thanh Lọc', 120000, 'Post'),
(@F6, N'Package', @P2, N'Gói Phục Hồi', 15000000, 'Post'), (@F8, N'Package', @P2, N'Gói Phục Hồi', 6000000, 'Post'),
(@F9, N'Package', @P1, N'Gói Thanh Lọc', 4800000, 'Post'), (@F10, N'Package', @P2, N'Gói Phục Hồi', 3000000, 'Post');

INSERT INTO REVIEW (booking_id, rating, comment) VALUES 
(@B1, 5, N'Dịch vụ xuất sắc'), (@B2, 4, N'Đồ ăn ngon'), (@B3, 5, N'Resort rất yên tĩnh'), (@B4, 3, N'Check-in hơi lâu'), (@B5, 5, N'Sẽ quay lại'),
(@B6, 4, N'Đáng tiền'), (@B7, 5, N'Thiết kế đẹp'), (@B8, 5, N'Đỡ đau lưng'), (@B9, 4, N'Thân thiện'), (@B10, 2, N'Chậm trễ');

INSERT INTO PAYMENT (folio_id, amount, payment_method, payment_gateway, transaction_code, status) VALUES 
(@F1, 5340000, 'CARD', 'VNPAY', 'TX99823123', 'Success'), (@F3, 4000000, 'CASH', 'COUNTER', 'CS11029301', 'Success'),
(@F2, 2000000, 'TRANSFER', 'BANK', 'TR55610239', 'Success'), (@F4, 0, 'CARD', 'VNPAY', 'TX00000000', 'Pending'),
(@F5, 120000, 'CASH', 'COUNTER', 'CS88231023', 'Success'), (@F6, 15000000, 'TRANSFER', 'BANK', 'TR66712391', 'Success'),
(@F7, 0, 'CARD', 'MOMO', 'MM99123812', 'Pending'), (@F8, 6000000, 'CARD', 'VNPAY', 'TX11230192', 'Success'),
(@F9, 0, 'CASH', 'COUNTER', 'CS00000000', 'Pending'), (@F10, 3200000, 'TRANSFER', 'BANK', 'TR88123012', 'Success');

INSERT INTO AUDIT_LOG (action_type, actor_id, target_id, details) VALUES 
('LOGIN', 1, 1, N'Admin đăng nhập'), ('CREATE_USER', 1, @G1, N'Tạo mới guest1'),
('CREATE_BOOKING', 5, @B1, N'Tạo mới booking 1'), ('ASSIGN_ROOM', 5, 3, N'Xếp phòng R03'),
('UPDATE_PAYMENT', 2, @F1, N'Xác nhận thanh toán Folio 1'), ('UPDATE_CLEANING', 1, 1, N'Cập nhật V01 sang Clean'),
('CANCEL_BOOKING', 5, @B5, N'Khách hủy lịch'), ('EXPORT_FOLIO', 2, @F2, N'Xuất hóa đơn'),
('LOGIN', 3, 3, N'Therapist A đăng nhập'), ('UPDATE_PROFILE', @G1, 1, N'Khách cập nhật dị ứng');
GO