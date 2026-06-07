-- 1. TẠM TẮT KHÓA NGOẠI
ALTER TABLE GUEST_FOLIO NOCHECK CONSTRAINT ALL;
ALTER TABLE FOLIO_ITEM NOCHECK CONSTRAINT ALL;
ALTER TABLE PAYMENT NOCHECK CONSTRAINT ALL;

-- 2. DỌN SẠCH DỮ LIỆU LỖI CŨ
DELETE FROM PAYMENT;
DELETE FROM FOLIO_ITEM;
DELETE FROM GUEST_FOLIO;

-- 3. TẠO HÓA ĐƠN GỐC & TỰ BẮT MÃ ID MỚI SINH RA
DECLARE @NewFolioId INT;

INSERT INTO GUEST_FOLIO (booking_id, total_package_amout, total_extra_fb, final_amount, status, create_at, update_at)
VALUES (1, 12600000.00, 2750000.00, 19400000.00, 'UNPAID', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

SET @NewFolioId = SCOPE_IDENTITY();

-- 4. TẠO CÁC DỊCH VỤ PHÁT SINH (TRỎ CHÍNH XÁC VÀO ID VỪA SINH)
INSERT INTO FOLIO_ITEM (folio_id, service_category, description, amount, create_at, create_by, status)
VALUES 
(@NewFolioId, 'Dịch vụ Spa Phát sinh', 'Massage đá nóng phục hồi', 1850000.00, CURRENT_TIMESTAMP, 1, 'ACTIVE'),
(@NewFolioId, 'Dịch vụ Spa Phát sinh', 'Trị liệu thảo dược toàn thân', 2200000.00, CURRENT_TIMESTAMP, 1, 'ACTIVE'),
(@NewFolioId, 'Ẩm thực Phát sinh', 'Dinner A-la-carte (Vegan Fusion)', 1250000.00, CURRENT_TIMESTAMP, 1, 'ACTIVE'),
(@NewFolioId, 'Ẩm thực Phát sinh', 'Rượu vang hữu cơ (Organic Red)', 1500000.00, CURRENT_TIMESTAMP, 1, 'ACTIVE');

-- 5. TẠO THANH TOÁN TIỀN CỌC TRƯỚC (TRỎ CHÍNH XÁC VÀO ID VỪA SINH)
INSERT INTO PAYMENT (folio_id, amount, payment_method, payment_gateway, transaction_code, payment_date, status)
VALUES 
(@NewFolioId, 4050000.00, 'CARD', 'VN_PAY', 'TXN-99882211', CURRENT_TIMESTAMP, 'SUCCESS');

-- 6. BẬT LẠI KHÓA NGOẠI
ALTER TABLE GUEST_FOLIO CHECK CONSTRAINT ALL;
ALTER TABLE FOLIO_ITEM CHECK CONSTRAINT ALL;
ALTER TABLE PAYMENT CHECK CONSTRAINT ALL;
