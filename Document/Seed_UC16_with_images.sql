USE HoS;
GO

-- =========================================================================
-- SEED DATA FOR UC16 (F&B Meal Selection) & UC19 (A-La-Carte Ordering)
-- Project: Xoai Aura Retreat Wellness Resort & Spa
-- =========================================================================

-- Enable IDENTITY_INSERT where needed to maintain explicit primary keys for relationships

-- 1. Seed ROLE
SET IDENTITY_INSERT [ROLE] ON;
INSERT INTO [ROLE] (role_id, role_name) VALUES (1, 'GUEST');
SET IDENTITY_INSERT [ROLE] OFF;

-- 2. Seed USER (representing Minh, Guest ID = 1)
SET IDENTITY_INSERT [USER] ON;
INSERT INTO [USER] (user_id, role_id, email, password_hash, full_name, gender, date_of_birth, phone, Identify_code, status, last_login, is_delete) 
VALUES (1, 1, 'guest@fpt.edu.vn', 'password_hash_placeholder', 'Minh', 'Nam', '1995-08-15', '0987654321', 'ID123456789', 'Active', GETDATE(), 0);
SET IDENTITY_INSERT [USER] OFF;

-- 3. Seed DIETARY_PROFILE (Allergies: hải sản, hạt điều; Preference: vegan)
SET IDENTITY_INSERT DIETARY_PROFILE ON;
INSERT INTO DIETARY_PROFILE (dietary_id, user_id, food_allergies, diatary_preference, update_at, created_at, is_delete) 
VALUES (1, 1, N'hải sản, hạt điều', N'vegan', GETDATE(), GETDATE(), 0);
SET IDENTITY_INSERT DIETARY_PROFILE OFF;

-- 4. Seed MENU_ITEM (All 5 dishes with correct names, price, ingredients and availabilities)
SET IDENTITY_INSERT MENU_ITEM ON;
INSERT INTO MENU_ITEM (menu_item_id, item_name, price, ingredient, is_available, create_at, update_at, is_delete) VALUES
(1, N'Cá Hồi Nướng Hương Thảo', 420.00, N'Cá hồi Na Uy nướng chậm cùng các loại rau củ hữu cơ từ vườn Aura, phục vụ kèm sốt bơ chanh thảo mộc.', 1, GETDATE(), GETDATE(), 0),
(2, N'Salad Aura Thanh Lọc', 280.00, N'Tổng hợp hạt quinoa, bơ sáp Đắk Lắk và rau mầm tươi, cung cấp đầy đủ chất xơ và vitamin cho buổi trưa nhẹ nhàng.', 1, GETDATE(), GETDATE(), 0),
(3, N'Tôm Nướng Muối Hạt & Hạt Điều', 320.00, N'Món ăn này có chứa hải sản và hạt điều, nằm trong danh sách dị ứng của bạn.', 1, GETDATE(), GETDATE(), 0),
(4, N'Bát Cơm Gạo Lứt Chay', 240.00, N'Sự kết hợp cân bằng giữa tinh bột phức hợp, nấm rừng và đậu hũ hữu cơ nướng tương.', 1, GETDATE(), GETDATE(), 0),
(5, N'Nước Ép Cần Tây Hữu Cơ', 80.00, N'Cần tây nguyên chất, táo xanh hữu cơ giúp lọc cơ thể.', 1, GETDATE(), GETDATE(), 0);
SET IDENTITY_INSERT MENU_ITEM OFF;


-- 4.1. Add and seed image URLs for MENU_ITEM
-- If your MENU_ITEM table already has another image column name, change image_url below to match your schema.
IF COL_LENGTH('MENU_ITEM', 'image_url') IS NULL
BEGIN
    ALTER TABLE MENU_ITEM ADD image_url NVARCHAR(MAX) NULL;
END;
GO

UPDATE MENU_ITEM
SET image_url = N'https://lh3.googleusercontent.com/aida/ADBb0uhcj3_Ba-K6jKYvOIV8HbVPdW-XVgmUoglPtSZ7hBhwhteY74jw7D7zrT5GQ4IRHQGDCYpYRum1-ET2R5LhiA82cnSizWjQlfHkXTO-vlmBVNymbUEhlQ6b275jCnkSNIolq-xRPnLe9vu0vaGwV--laSH0jwalr4xCPlZmlYRorrca_LkNb9HP3imYXU4IhnCizTxIx0RXzzYXjwDDYk0Mt2y6uywbMTp338LC8fbJwrqdXhARUunUvjY'
WHERE menu_item_id = 1;

UPDATE MENU_ITEM
SET image_url = N'https://lh3.googleusercontent.com/aida-public/AB6AXuDvCaliK_ZWDU9xw0wEt-Cb-v-FE0m9qYLyg9aIJaoAHWs_IhRNBvb2VMWfdwopQ3A7VMS23w5AgITj5H1A23dsi2p780AWGLAAiU-EQLSIu9lJFWGvbninxoq6GsNK6YzyDRhCsoAiGMjKiP0dU2fKLZUq6qyNRHXDHtSFd1dcciJxt7ByBaPPnrHoAOnQLS1YR-LaMij9YusMh0e9WYQFk0BFh18jz2Q6RS7cajwFNKWoXuDvcB3OI2SVl3Ziq61HrlbIbF3becQ'
WHERE menu_item_id = 2;

UPDATE MENU_ITEM
SET image_url = N'https://lh3.googleusercontent.com/aida-public/AB6AXuAzZUuNZzYCO1zjBkT5-2CqC5apkrAnX-ryycO2n0itZ6abpJ-YECMO0GTu2LWHLLOKkmq4F8gczkuKH2bjLOKtecmnPSfx5gE4mnD90Fuj0WUZPtidEiqwDf3aPEo8q0Oxf8nXGoeEE-1Tvspi3sp6YdHSeNkwcNRGp8DHjMOef0GMC8cFHgr_6maW9bCdlcS_QoYM6TZzOnEToQ_y9p6nJKKC15YqSuS98e4dOjsCL02yTWUA9vu2Xht3Fze1cReJPEbNehvd4zU'
WHERE menu_item_id = 3;

UPDATE MENU_ITEM
SET image_url = N'https://lh3.googleusercontent.com/aida-public/AB6AXuCRAmP6rs8IPSfFwt6W7xbrPqUEGjAp2tBe1aPn9hYypoXFMQhntfq7elR5amMD2Bf9_us_dnC8V6gEoWW36D7u3XP2n_RzVM2CiYpZoAdllefRoX_7AnxPyyzBKB1WsBiQcO7gmPihQJXukRSgEfkLa_xr74R8ad2II3C4QY7czuFhNCERXOA3JeveliO6Y3VitdkMF_qZ82iYKsaUWnNZloDVC9vs1IwMo0CRH15VZi_2A0qMb28VqdjVzsgSsdhy5RIIsq5jxyQ'
WHERE menu_item_id = 4;

UPDATE MENU_ITEM
SET image_url = N'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?q=80&w=800&auto=format&fit=crop'
WHERE menu_item_id = 5;
GO

-- 5. Seed RETREAT_PACKAGE
SET IDENTITY_INSERT RETREAT_PACKAGE ON;
INSERT INTO RETREAT_PACKAGE (package_id, type_package, package_name, duration_days, description, is_active, price, create_at, created_at, update_at, is_delete) 
VALUES (1, 'Health', 'Detox & Yoga Journey', 5, N'Gói trị liệu sức khỏe toàn diện 5 ngày', 1, 800.00, GETDATE(), GETDATE(), GETDATE(), 0);
SET IDENTITY_INSERT RETREAT_PACKAGE OFF;

-- 6. Seed VILLA_TYPE
SET IDENTITY_INSERT VILLA_TYPE ON;
INSERT INTO VILLA_TYPE (type_id, type_name, price_per_day, is_delete) 
VALUES (1, 'Garden Pool Villa', 250.00, 0);
SET IDENTITY_INSERT VILLA_TYPE OFF;

-- 7. Seed VILLA
SET IDENTITY_INSERT VILLA ON;
INSERT INTO VILLA (villa_id, villa_type, villa_code, limit_person, villa_status, cleaning_status, is_delete) 
VALUES (1, 1, 'VILLA-101', 2, 'Available', 'Clean', 0);
SET IDENTITY_INSERT VILLA OFF;

-- 8. Seed BOOKING
SET IDENTITY_INSERT BOOKING ON;
INSERT INTO BOOKING (booking_id, guest_id, package_id, assigned_villa_id, checkin_date, checkout_date, total_guests, create_at, created_at, update_at, booking_status, payment_status, is_delete) 
VALUES (1, 1, 1, 1, GETDATE(), DATEADD(day, 5, GETDATE()), 1, GETDATE(), GETDATE(), GETDATE(), 'Active', 'Deposited', 0);
SET IDENTITY_INSERT BOOKING OFF;

-- 9. Seed GUEST_FOLIO
SET IDENTITY_INSERT GUEST_FOLIO ON;
INSERT INTO GUEST_FOLIO (folio_id, booking_id, total_package_amout, total_extra_fb, final_amount, status, create_at, created_at, update_at, is_delete) 
VALUES (1, 1, 800.00, 0.00, 800.00, 'Active', GETDATE(), GETDATE(), GETDATE(), 0);
SET IDENTITY_INSERT GUEST_FOLIO OFF;

PRINT 'Seed data for UC16 F&B Meal Selection inserted successfully.';
