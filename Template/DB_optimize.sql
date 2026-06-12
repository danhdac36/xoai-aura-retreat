-- =========================================================================
-- SQL MIGRATION / REFACTORING SCRIPT FOR HOS DATABASE OPTIMIZATION
-- =========================================================================

-- 1. Thêm cột xóa mềm & Đồng bộ Auditing cho bảng USER
ALTER TABLE [USER] ADD is_delete BIT DEFAULT 0;
EXEC sp_rename 'USER.created_at', 'create_at', 'COLUMN';
EXEC sp_rename 'USER.last_update', 'update_at', 'COLUMN';
EXEC sp_rename 'USER.Identify_code', 'identity_code', 'COLUMN';

-- 2. Sửa lỗi chính tả trong bảng GUEST_FOLIO và DIETARY_PROFILE
EXEC sp_rename 'GUEST_FOLIO.total_package_amout', 'total_package_amount', 'COLUMN';
EXEC sp_rename 'DIETARY_PROFILE.diatary_preference', 'dietary_preference', 'COLUMN';

-- 3. Bổ sung các ràng buộc Khóa ngoại còn thiếu
-- Bổ sung FK cho MEAL_ORDER
ALTER TABLE MEAL_ORDER ADD CONSTRAINT FK_MEAL_ORDER_GUEST 
    FOREIGN KEY (guest_id) REFERENCES [USER](user_id);
ALTER TABLE MEAL_ORDER ADD CONSTRAINT FK_MEAL_ORDER_STAFF 
    FOREIGN KEY (ordered_by) REFERENCES [USER](user_id);
ALTER TABLE MEAL_ORDER ADD CONSTRAINT FK_MEAL_ORDER_FOLIO 
    FOREIGN KEY (folio_id) REFERENCES GUEST_FOLIO(folio_id);

-- Bổ sung FK cho TREATMENT_BOOKING
ALTER TABLE TREATMENT_BOOKING ADD CONSTRAINT FK_TREATMENT_BOOKING_FOLIO 
    FOREIGN KEY (folio_id) REFERENCES GUEST_FOLIO(folio_id);

-- 4. Tăng độ dài cột cho bảng PAYMENT để tránh tràn dữ liệu
ALTER TABLE PAYMENT ALTER COLUMN payment_method VARCHAR(30);
ALTER TABLE PAYMENT ALTER COLUMN payment_gateway VARCHAR(30);

-- 5. Chuẩn hóa Nhiều-Nhiều cho Gói Trị liệu (RETREAT_PACKAGE và TREATMENT_SERVICE)
-- Tạo bảng trung gian
CREATE TABLE RETREAT_PACKAGE_SERVICE (
    package_id INT NOT NULL,
    service_id INT NOT NULL,
    PRIMARY KEY (package_id, service_id),
    CONSTRAINT FK_PKG_SVC_PACKAGE FOREIGN KEY (package_id) REFERENCES RETREAT_PACKAGE(package_id),
    CONSTRAINT FK_PKG_SVC_SERVICE FOREIGN KEY (service_id) REFERENCES TREATMENT_SERVICE(service_id)
);
-- Lưu ý: Sau khi chạy script này, bạn có thể drop cột 'services' của bảng RETREAT_PACKAGE
-- ALTER TABLE RETREAT_PACKAGE DROP COLUMN services;

-- 6. Tạo các INDEX tối ưu hiệu năng tìm kiếm và JOIN
-- Index khóa ngoại
CREATE NONCLUSTERED INDEX IX_USER_role_id ON [USER](role_id);
CREATE NONCLUSTERED INDEX IX_BOOKING_guest_id ON BOOKING(guest_id);
CREATE NONCLUSTERED INDEX IX_BOOKING_package_id ON BOOKING(package_id);
CREATE NONCLUSTERED INDEX IX_BOOKING_assigned_villa_id ON BOOKING(assigned_villa_id);
CREATE NONCLUSTERED INDEX IX_GUEST_FOLIO_booking_id ON GUEST_FOLIO(booking_id);
CREATE NONCLUSTERED INDEX IX_TREATMENT_BOOKING_booking_id ON TREATMENT_BOOKING(booking_id);
CREATE NONCLUSTERED INDEX IX_TREATMENT_BOOKING_service_id ON TREATMENT_BOOKING(service_id);
CREATE NONCLUSTERED INDEX IX_SCHEDULE_treatment_id ON SCHEDULE(treatment_id);

-- Index khoảng thời gian (Tối ưu check trống phòng và xếp lịch)
CREATE NONCLUSTERED INDEX IX_BOOKING_dates ON BOOKING(checkin_date, checkout_date);
CREATE NONCLUSTERED INDEX IX_SCHEDULE_times ON SCHEDULE(start_time, end_time);

-- 7. Thêm Ràng buộc CHECK cho các trạng thái chính để đảm bảo toàn vẹn miền giá trị
ALTER TABLE BOOKING ADD CONSTRAINT CHK_booking_status 
    CHECK (booking_status IN ('PENDING', 'CONFIRMED', 'CANCELLED', 'CHECKED_IN', 'CHECKED_OUT'));

ALTER TABLE BOOKING ADD CONSTRAINT CHK_payment_status 
    CHECK (payment_status IN ('UNPAID', 'PARTIAL', 'PAID', 'REFUNDED'));

ALTER TABLE GUEST_FOLIO ADD CONSTRAINT CHK_folio_status 
    CHECK (status IN ('UNPAID', 'PAID', 'CANCELLED'));
