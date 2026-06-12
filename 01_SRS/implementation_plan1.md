# Cải Tiến Giao Diện Module 6 & Đồng Bộ 18 Gói Trị Liệu

Kế hoạch này tập trung vào việc giải quyết triệt để hai vấn đề người dùng vừa phản hồi:
1. **Số lượng gói trị liệu:** Nạp lại dữ liệu đầy đủ 20 gói trị liệu từ tài liệu đề xuất gốc, đồng thời thiết lập ẩn 2 gói (chỉ giữ 18 gói hoạt động) để khớp chính xác với yêu cầu "18 gói trị liệu".
2. **Lỗi phông chữ:** Khắc phục lỗi font chữ hiển thị sai (đang bị trình duyệt hiển thị mặc định sang Times New Roman/Arial thô cứng) bằng cách cấu hình thẻ `<style>` toàn cục liên kết trực tiếp font `Playfair Display` (cho Serif/Tiêu đề) và `Inter` (cho Sans-serif/Nội dung).

---

## User Review Required

> [!IMPORTANT]
> **Phương án đồng bộ 18 gói trị liệu:**
> - Chúng tôi sẽ thực thi script nạp toàn bộ 20 gói từ [package_data_proposals.md](file:///d:/su26-swp391-se2023-g6/Document/package_data_proposals.md).
> - Để hiển thị đúng **18 gói** như yêu cầu, chúng tôi sẽ chuyển trạng thái của 2 gói trị liệu cuối (ID 19 và 20) thành không hoạt động (`is_active = 0`). Như vậy, trang danh sách sẽ hiển thị chính xác 18 gói trị liệu cao cấp.
> 
> **Phương án đồng bộ phông chữ:**
> - Bổ sung thẻ `<style>` cấu hình font-family toàn cục vào phần `<head>` của cả 4 trang HTML để đảm bảo các font `Playfair Display` và `Inter` hiển thị đồng bộ, sang trọng trên mọi thiết bị và trình duyệt.

---

## Open Questions

> [!NOTE]
> Khách hàng có muốn ẩn cụ thể 2 gói nào khác ngoài gói ID 19 (Traditional Oriental Healing) và ID 20 (Premium Spa Pampering) không? Nếu không có phản hồi khác, chúng tôi sẽ mặc định ẩn 2 gói cuối này để giữ lại 18 gói hoạt động.

---

## Proposed Changes

### Tầng Dữ Liệu (Database Seed)

#### [MODIFY] Thực thi SQL Seed Gói Trị Liệu
- Chạy script nạp lại 20 gói trị liệu tiếng Anh chuẩn, sau đó cập nhật 2 gói cuối (ID 19, 20) thành `is_active = 0` để giao diện hiển thị đúng 18 gói hoạt động.
```sql
-- Dọn dẹp bảng gói trị liệu
DELETE FROM RETREAT_PACKAGE;

-- Nạp 20 gói trị liệu
SET IDENTITY_INSERT RETREAT_PACKAGE ON;
INSERT INTO RETREAT_PACKAGE (package_id, type_package, package_name, duration_days, services, description, is_active, price, create_at, is_delete) VALUES
(1, 'Yoga', N'Sunrise Yoga Weekend', 3, N'Luyện tập Yoga, thực đơn dinh dưỡng nhẹ nhàng, kiểm tra sức khỏe tổng quát', N'Hành trình 3 ngày yên bình giúp tái tạo năng lượng và cân bằng cơ thể.', 1, 15000000.00, GETDATE(), 0),
(2, 'Yoga', N'Hatha Yoga Flow', 5, N'Lớp Hatha chuyên sâu, thiền hành, trà thảo mộc organic, ngâm chân thảo dược', N'5 ngày kết nối sâu sắc với hơi thở và cơ bắp qua các bài tập Hatha truyền thống.', 1, 22000000.00, GETDATE(), 0),
(3, 'Yoga', N'Mindful Meditation & Silence', 4, N'Thiền chánh niệm, tản bộ trong rừng, nước uống thảo quả, ngâm tắm khoáng', N'4 ngày thanh tịnh tâm trí, rời xa thiết bị công nghệ để tìm lại bình yên nội tại.', 1, 18500000.00, GETDATE(), 0),
(4, 'Yoga', N'Spinal Yoga Therapy', 3, N'Yoga trị liệu đau lưng, massage mô sâu, chườm ấm ngải cứu, ăn uống thực dưỡng', N'Gói trị liệu chuyên sâu dành riêng cho nhân viên văn phòng giảm đau mỏi vai gáy.', 1, 16000000.00, GETDATE(), 0),
(5, 'Detox', N'Mindful Detox Program', 5, N'Nước detox thanh lọc, tư vấn dinh dưỡng chuyên sâu, liệu pháp massage phục hồi', N'Liệu trình thanh lọc toàn diện 5 ngày đào thải độc tố cơ thể và hồi xuân tinh thần.', 1, 25000000.00, GETDATE(), 0),
(6, 'Detox', N'Green Cleanse Detox', 3, N'Thực đơn sinh tố xanh, sauna đá muối hồng, massage thải độc bạch huyết', N'Thanh lọc nhanh 3 ngày loại bỏ mệt mỏi, làm sáng da và nhẹ bụng tức thì.', 1, 14800000.00, GETDATE(), 0),
(7, 'Detox', N'Ayurveda Panchakarma', 7, N'Trị liệu dầu ấm Shirodhara, xông hơi thảo dược Ấn Độ, ăn uống theo thể trạng', N'Hành trình phục hồi sức khỏe cổ truyền Ấn Độ giúp cân bằng các Dosha trong cơ thể.', 1, 38000000.00, GETDATE(), 0),
(8, 'Detox', N'Digestive Restorative Detox', 4, N'Thực đơn súp thực dưỡng, trà giải độc gan, massage bụng chuyên sâu, yoga nhẹ', N'Phục hồi hệ vi sinh đường ruột và cải thiện chức năng gan, tụy sau 4 ngày.', 1, 21000000.00, GETDATE(), 0),
(9, 'Stress Relief', N'Deep Relaxation Retreat', 4, N'Thiền định chánh niệm, xông hơi đá nóng phục hồi giấc ngủ, trà đạo organic', N'Trốn khỏi cuộc sống xô bồ với 4 ngày nghỉ ngơi sâu xoa dịu hệ thần kinh.', 1, 18000000.00, GETDATE(), 0),
(10, 'Stress Relief', N'Deep Sleep Therapy', 3, N'Trị liệu tinh dầu oải hương, massage đầu cổ, trà ngủ ngon, thiền chuông xoay', N'Giải pháp đặc biệt dành cho người mất ngủ kinh niên tái tạo nhịp sinh học tự nhiên.', 1, 15500000.00, GETDATE(), 0),
(11, 'Stress Relief', N'Aromatherapy Healing Retreat', 5, N'Massage tinh dầu sả gừng, tắm bồn thảo mộc thiên nhiên, tư vấn trị liệu tâm lý', N'5 ngày đắm mình trong thế giới mùi hương thảo mộc giúp giải tỏa lo âu áp lực.', 1, 24500000.00, GETDATE(), 0),
(12, 'Stress Relief', N'Brain Energy Rejuvenation', 3, N'Liệu pháp thiền nổi, tắm rừng Shinrin-yoku, thực đơn bổ não chống oxy hóa', N'Phục hồi sự tập trung và minh mẫn cho trí não sau những dự án làm việc căng thẳng.', 1, 16500000.00, GETDATE(), 0),
(13, 'Weight Loss', N'Active Slimming Journey', 6, N'Huấn luyện viên thể hình cá nhân, đo chỉ số cơ thể, thực đơn đong đếm calories', N'6 ngày vận động khoa học kết hợp thực đơn đốt mỡ thừa lành mạnh.', 1, 32000000.00, GETDATE(), 0),
(14, 'Weight Loss', N'Cardio & Yoga Fit', 4, N'Lớp HIIT nhẹ, Yoga năng động, massage cơ sâu hồi phục lực, nước uống protein', N'Cân bằng giữa đốt calo cao và kéo giãn dẻo dai cơ thể trong 4 ngày nghỉ dưỡng.', 1, 19800000.00, GETDATE(), 0),
(15, 'Weight Loss', N'Macrobiotic Weight Management', 5, N'Học nấu ăn thực dưỡng, ăn chay hữu cơ, massage thon gọn đùi bụng', N'Xây dựng thói quen ăn uống lành mạnh bền vững để duy trì cân nặng lý tưởng.', 1, 23000000.00, GETDATE(), 0),
(16, 'Weight Loss', N'Kickboxing & Retreat', 3, N'Lớp Kickboxing cơ bản, xông hơi hồng ngoại, thực đơn giàu đạm tốt cho cơ', N'Đánh thức năng lượng thể chất mạnh mẽ và cải thiện sức bền tim mạch nhanh chóng.', 1, 17000000.00, GETDATE(), 0),
(17, 'Spa', N'Tibetan Sound Healing', 3, N'Trị liệu tần số âm thanh chuông xoay, massage ấn huyệt Tây Tạng, trà gừng thảo mộc', N'Đưa cơ thể vào trạng thái tự chữa lành thông qua sóng âm thanh trầm ấm từ chuông đồng.', 1, 17500000.00, GETDATE(), 0),
(18, 'Spa', N'Herbal Hot Stone Detox', 4, N'Massage đá nóng bazan, đắp bùn khoáng thiên nhiên, ngâm bồn sữa gạo', N'Nuôi dưỡng làn da mịn màng khỏe mạnh và lưu thông khí huyết toàn thân.', 1, 20500000.00, GETDATE(), 0),
(19, 'Spa', N'Traditional Oriental Healing', 5, N'Châm cứu bấm huyệt Đông y, giác hơi xông thuốc, thực đơn trà sâm quý', N'Phục hồi sức khỏe toàn diện dựa trên tinh hoa y học cổ truyền Đông Á.', 0, 27000000.00, GETDATE(), 0), -- Ẩn đi để còn 18 gói
(20, 'Spa', N'Premium Spa Pampering', 7, N'90 phút trị liệu spa mỗi ngày, chăm sóc da mặt ngọc pearls, tắm sữa ong chúa', N'Kỳ nghỉ dưỡng nuông chiều bản thân tối đa tại biệt thự cao cấp bậc nhất.', 0, 42000000.00, GETDATE(), 0); -- Ẩn đi để còn 18 gói
SET IDENTITY_INSERT RETREAT_PACKAGE OFF;
```

---

### Tầng View (Sửa Phông Chữ & Sửa Lỗi Giao Diện)

#### [MODIFY] [packages.html](file:///d:/su26-swp391-se2023-g6/auramoon/src/main/resources/templates/booking/packages.html)
- Cấu hình lại Tailwind config để mở rộng `fontFamily`:
  - `serif: ['"Playfair Display"', 'serif']`
  - `sans: ['Inter', 'sans-serif']`
- Thêm thẻ `<style>` toàn cục cấu hình `font-family` trực tiếp để tránh trình duyệt render sai font mặc định.
- Nâng cấp thẩm mỹ thẻ gói: Bo tròn `rounded-2xl`, thêm hiệu ứng hover đổi màu viền và bóng mờ, làm các nhãn dịch vụ nổi bật hơn.

#### [MODIFY] [package-detail.html](file:///d:/su26-swp391-se2023-g6/auramoon/src/main/resources/templates/booking/package-detail.html)
- **Khắc phục lỗi biên dịch Thymeleaf dòng 23** (sửa thẻ `<main>` bị dính chữ).
- Thêm thẻ `<style>` toàn cục cấu hình `font-family` trực tiếp (Playfair Display & Inter).
- Tinh chỉnh khoảng cách dòng và kích thước chữ của các Tab chi tiết (Itinerary, Spa, Nutrition) để trông thoáng đạt và sang trọng hơn.

#### [MODIFY] [create-form.html](file:///d:/su26-swp391-se2023-g6/auramoon/src/main/resources/templates/booking/create-form.html)
- Thêm thẻ `<style>` toàn cục cấu hình `font-family` trực tiếp.
- Làm đẹp các ô input nhập ngày và số lượng khách.
- Nâng cấp thiết kế cho lưới chọn Villa (`villaTypes`), đảm bảo căn đều và có chỉ báo được chọn (checked) sang trọng.

#### [MODIFY] [success.html](file:///d:/su26-swp391-se2023-g6/auramoon/src/main/resources/templates/booking/success.html)
- Thêm thẻ `<style>` toàn cục cấu hình `font-family` trực tiếp.
- Nâng cấp Success Box thành dạng vé điện tử (e-ticket) với bóng đổ sang trọng.

---

## Verification Plan

### Automated Tests
Chạy toàn bộ unit test để bảo toàn logic nghiệp vụ:
```powershell
$env:JAVA_HOME="C:\Program Files\Java\jdk-21.0.11"; .\mvnw.cmd test
```

### Manual Verification
1. Truy cập `http://localhost:8080/packages` và đếm tổng số gói trị liệu hiển thị trên giao diện (phải hiển thị chính xác **18 gói**).
2. Kiểm tra toàn bộ phông chữ trên tiêu đề, tên gói (phải hiển thị dạng chữ có chân mềm mại của `Playfair Display`) và phông chữ phần mô tả (chữ không chân gọn gàng của `Inter`).
3. Truy cập chi tiết gói và kiểm tra các tab thông tin hoạt động bình thường, không còn lỗi trắng trang hay lỗi 500.
4. Đặt phòng thử, chọn Villa và chuyển tiếp đến trang thành công để đảm bảo toàn bộ luồng hoạt động mượt mà.
