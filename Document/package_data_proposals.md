# Đề xuất Danh sách 20 Gói Trị liệu Nghỉ dưỡng (Retreat Packages)

Dưới đây là đề xuất danh sách **20 gói nghỉ dưỡng trị liệu thực tế**, được phân chia theo 5 mục tiêu sức khỏe chính (Wellness Goals) phù hợp tiêu chuẩn GWI (Global Wellness Institute). Các mức giá được tính toán hợp lý bằng đơn vị Việt Nam Đồng (VNĐ) để phục vụ chạy thử nghiệm hệ thống.

---

## 1. Cơ chế Hiển thị Hình ảnh Minh họa cho mỗi Gói

Do cấu trúc bảng `RETREAT_PACKAGE` trong [DB.sql](file:///d:/su26-swp391-se2023-g6/Template/DB.sql) không chứa cột lưu đường dẫn ảnh (`image`), hệ thống sẽ **tự động ánh xạ hình ảnh minh họa** ở tầng giao diện Thymeleaf dựa trên trường Loại gói (`type_package`) như sau:

* **Loại gói `Yoga`** (Các gói từ 1 - 4): Ánh xạ đến ảnh [yoga-retreat.jpg](file:///d:/su26-swp391-se2023-g6/auramoon/src/main/resources/static/img/yoga-retreat.jpg) (ảnh tập yoga thanh bình dưới nắng sớm).
* **Loại gói `Detox` & `Weight Loss`** (Các gói từ 5 - 8 và 13 - 16): Ánh xạ đến ảnh [detox-retreat.jpg](file:///d:/su26-swp391-se2023-g6/auramoon/src/main/resources/static/img/detox-retreat.jpg) (ảnh nước detox trái cây tươi mát).
* **Loại gói `Stress Relief` & `Spa`** (Các gói từ 9 - 12 và 17 - 20): Ánh xạ đến ảnh [spa-retreat.jpg](file:///d:/su26-swp391-se2023-g6/auramoon/src/main/resources/static/img/spa-retreat.jpg) (ảnh phòng spa trị liệu thảo dược ấm cúng).
* **Các loại gói tùy chỉnh khác sau này:** Hệ thống sẽ tự động sử dụng ảnh mặc định vừa được bổ sung vào dự án: [retreat-default.jpg](file:///d:/su26-swp391-se2023-g6/auramoon/src/main/resources/static/img/retreat-default.jpg) để đảm bảo không bị lỗi hiển thị ảnh (404).

---

## 2. Chi tiết 20 Gói Trị liệu Đề xuất

### Nhóm 1: Yoga & Thiền định (Ảnh minh họa: `yoga-retreat.jpg`)

| ID | Tên gói | Loại gói | Số ngày | Giá tiền (VNĐ) | Dịch vụ bao gồm | Mô tả ngắn |
|---|---|---|---|---|---|---|
| 1 | Sunrise Yoga Weekend | Yoga | 3 | 15.000.000 đ | Luyện tập Yoga, thực đơn dinh dưỡng nhẹ nhàng, kiểm tra sức khỏe tổng quát | Hành trình 3 ngày yên bình giúp tái tạo năng lượng và cân bằng cơ thể. |
| 2 | Hatha Yoga Flow | Yoga | 5 | 22.000.000 đ | Lớp Hatha chuyên sâu, thiền hành, trà thảo mộc organic, ngâm chân thảo dược | 5 ngày kết nối sâu sắc với hơi thở và cơ bắp qua các bài tập Hatha truyền thống. |
| 3 | Mindful Meditation & Silence | Yoga | 4 | 18.500.000 đ | Thiền chánh niệm, tản bộ trong rừng, nước uống thảo quả, ngâm tắm khoáng | 4 ngày thanh tịnh tâm trí, rời xa thiết bị công nghệ để tìm lại bình yên nội tại. |
| 4 | Spinal Yoga Therapy | Yoga | 3 | 16.000.000 đ | Yoga trị liệu đau lưng, massage mô sâu, chườm ấm ngải cứu, ăn uống thực dưỡng | Gói trị liệu chuyên sâu dành riêng cho nhân viên văn phòng giảm đau mỏi vai gáy. |

---

### Nhóm 2: Detox & Thanh lọc (Ảnh minh họa: `detox-retreat.jpg`)

| ID | Tên gói | Loại gói | Số ngày | Giá tiền (VNĐ) | Dịch vụ bao gồm | Mô tả ngắn |
|---|---|---|---|---|---|---|
| 5 | Mindful Detox Program | Detox | 5 | 25.000.000 đ | Nước detox thanh lọc, tư vấn dinh dưỡng chuyên sâu, liệu pháp massage phục hồi | Liệu trình thanh lọc toàn diện 5 ngày đào thải độc tố cơ thể và hồi xuân tinh thần. |
| 6 | Green Cleanse Detox | Detox | 3 | 14.800.000 đ | Thực đơn sinh tố xanh, sauna đá muối hồng, massage thải độc bạch huyết | Thanh lọc nhanh 3 ngày loại bỏ mệt mỏi, làm sáng da và nhẹ bụng tức thì. |
| 7 | Ayurveda Panchakarma | Detox | 7 | 38.000.000 đ | Trị liệu dầu ấm Shirodhara, xông hơi thảo dược Ấn Độ, ăn uống theo thể trạng | Hành trình phục hồi sức khỏe cổ truyền Ấn Độ giúp cân bằng các Dosha trong cơ thể. |
| 8 | Digestive Restorative Detox | Detox | 4 | 21.000.000 đ | Thực đơn súp thực dưỡng, trà giải độc gan, massage bụng chuyên sâu, yoga nhẹ | Phục hồi hệ vi sinh đường ruột và cải thiện chức năng gan, tụy sau 4 ngày. |

---

### Nhóm 3: Giải tỏa áp lực & Giấc ngủ ngon (Ảnh minh họa: `spa-retreat.jpg`)

| ID | Tên gói | Loại gói | Số ngày | Giá tiền (VNĐ) | Dịch vụ bao gồm | Mô tả ngắn |
|---|---|---|---|---|---|---|
| 9 | Deep Relaxation Retreat | Stress Relief | 4 | 18.000.000 đ | Thiền định chánh niệm, xông hơi đá nóng phục hồi giấc ngủ, trà đạo organic | Trốn khỏi cuộc sống xô bồ với 4 ngày nghỉ ngơi sâu xoa dịu hệ thần kinh. |
| 10 | Deep Sleep Therapy | Stress Relief | 3 | 15.500.000 đ | Trị liệu tinh dầu oải hương, massage đầu cổ, trà ngủ ngon, thiền chuông xoay | Giải pháp đặc biệt dành cho người mất ngủ kinh niên tái tạo nhịp sinh học tự nhiên. |
| 11 | Aromatherapy Healing Retreat | Stress Relief | 5 | 24.500.000 đ | Massage tinh dầu sả gừng, tắm bồn thảo mộc thiên nhiên, tư vấn trị liệu tâm lý | 5 ngày đắm mình trong thế giới mùi hương thảo mộc giúp giải tỏa lo âu áp lực. |
| 12 | Brain Energy Rejuvenation | Stress Relief | 3 | 16.500.000 đ | Liệu pháp thiền nổi, tắm rừng Shinrin-yoku, thực đơn bổ não chống oxy hóa | Phục hồi sự tập trung và minh mẫn cho trí não sau những dự án làm việc căng thẳng. |

---

### Nhóm 4: Giảm cân & Thể hình (Ảnh minh họa: `detox-retreat.jpg`)

| ID | Tên gói | Loại gói | Số ngày | Giá tiền (VNĐ) | Dịch vụ bao gồm | Mô tả ngắn |
|---|---|---|---|---|---|---|
| 13 | Active Slimming Journey | Weight Loss | 6 | 32.000.000 đ | Huấn luyện viên thể hình cá nhân, đo chỉ số cơ thể, thực đơn đong đếm calories | 6 ngày vận động khoa học kết hợp thực đơn đốt mỡ thừa lành mạnh. |
| 14 | Cardio & Yoga Fit | Weight Loss | 4 | 19.800.000 đ | Lớp HIIT nhẹ, Yoga năng động, massage cơ sâu hồi phục lực, nước uống protein | Cân bằng giữa đốt calo cao và kéo giãn dẻo dai cơ thể trong 4 ngày nghỉ dưỡng. |
| 15 | Macrobiotic Weight Management | Weight Loss | 5 | 23.000.000 đ | Học nấu ăn thực dưỡng, ăn chay hữu cơ, massage thon gọn đùi bụng | Xây dựng thói quen ăn uống lành mạnh bền vững để duy trì cân nặng lý tưởng. |
| 16 | Kickboxing & Retreat | Weight Loss | 3 | 17.000.000 đ | Lớp Kickboxing cơ bản, xông hơi hồng ngoại, thực đơn giàu đạm tốt cho cơ | Đánh thức năng lượng thể chất mạnh mẽ và cải thiện sức bền tim mạch nhanh chóng. |

---

### Nhóm 5: Spa & Trị liệu Tự nhiên (Ảnh minh họa: `spa-retreat.jpg`)

| ID | Tên gói | Loại gói | Số ngày | Giá tiền (VNĐ) | Dịch vụ bao gồm | Mô tả ngắn |
|---|---|---|---|---|---|---|
| 17 | Tibetan Sound Healing | Spa | 3 | 17.500.000 đ | Trị liệu tần số âm thanh chuông xoay, massage ấn huyệt Tây Tạng, trà gừng thảo mộc | Đưa cơ thể vào trạng thái tự chữa lành thông qua sóng âm thanh trầm ấm từ chuông đồng. |
| 18 | Herbal Hot Stone Detox | Spa | 4 | 20.500.000 đ | Massage đá nóng bazan, đắp bùn khoáng thiên nhiên, ngâm bồn sữa gạo | Nuôi dưỡng làn da mịn màng khỏe mạnh và lưu thông khí huyết toàn thân. |
| 19 | Traditional Oriental Healing | Spa | 5 | 27.000.000 đ | Châm cứu bấm huyệt Đông y, giác hơi xông thuốc, thực đơn trà sâm quý | Phục hồi sức khỏe toàn diện dựa trên tinh hoa y học cổ truyền Đông Á. |
| 20 | Premium Spa Pampering | Spa | 7 | 42.000.000 đ | 90 phút trị liệu spa mỗi ngày, chăm sóc da mặt ngọc pearls, tắm sữa ong chúa | Kỳ nghỉ dưỡng nuông chiều bản thân tối đa tại biệt thự cao cấp bậc nhất. |

---

## 3. SQL Seed Script Sẵn sàng Thực thi khi Bạn Đồng ý

Dưới đây là đoạn mã SQL Insert tương ứng để nạp trực tiếp 20 gói trị liệu này vào bảng `RETREAT_PACKAGE`:

```sql
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
(19, 'Spa', N'Traditional Oriental Healing', 5, N'Châm cứu bấm huyệt Đông y, giác hơi xông thuốc, thực đơn trà sâm quý', N'Phục hồi sức khỏe toàn diện dựa trên tinh hoa y học cổ truyền Đông Á.', 1, 27000000.00, GETDATE(), 0),
(20, 'Spa', N'Premium Spa Pampering', 7, N'90 phút trị liệu spa mỗi ngày, chăm sóc da mặt ngọc pearls, tắm sữa ong chúa', N'Kỳ nghỉ dưỡng nuông chiều bản thân tối đa tại biệt thự cao cấp bậc nhất.', 1, 42000000.00, GETDATE(), 0);

SET IDENTITY_INSERT RETREAT_PACKAGE OFF;
```

---

## 4. SQL Update Script (Cho Dữ Liệu Đã Seed Trước Đó)

Nếu cơ sở dữ liệu của bạn đã được seed bằng các tên tiếng Việt trước đó, bạn có thể thực thi script update dưới đây để chuyển đổi toàn bộ sang tên tiếng Anh chuẩn quốc tế:

```sql
UPDATE RETREAT_PACKAGE SET package_name = N'Mindful Meditation & Silence' WHERE package_id = 3;
UPDATE RETREAT_PACKAGE SET package_name = N'Spinal Yoga Therapy' WHERE package_id = 4;
UPDATE RETREAT_PACKAGE SET package_name = N'Digestive Restorative Detox' WHERE package_id = 8;
UPDATE RETREAT_PACKAGE SET package_name = N'Deep Sleep Therapy' WHERE package_id = 10;
UPDATE RETREAT_PACKAGE SET package_name = N'Aromatherapy Healing Retreat' WHERE package_id = 11;
UPDATE RETREAT_PACKAGE SET package_name = N'Brain Energy Rejuvenation' WHERE package_id = 12;
UPDATE RETREAT_PACKAGE SET package_name = N'Macrobiotic Weight Management' WHERE package_id = 15;
UPDATE RETREAT_PACKAGE SET package_name = N'Tibetan Sound Healing' WHERE package_id = 17;
UPDATE RETREAT_PACKAGE SET package_name = N'Herbal Hot Stone Detox' WHERE package_id = 18;
UPDATE RETREAT_PACKAGE SET package_name = N'Traditional Oriental Healing' WHERE package_id = 19;
```
```
