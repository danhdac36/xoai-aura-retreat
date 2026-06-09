# BÁO CÁO PHÂN TÍCH YÊU CẦU: MODULE 3 - XẾP LỊCH SPA & TRỊ LIỆU
**Môn học:** SWP391  
**Nhóm:** G6 (Dự án AuraMoon Retreat)  
**Phụ trách Module:** Sinh viên 3  

---

## 1. Xác định Bài toán & Các bên liên quan (Problem & Stakeholders)

### 1.1. Bài toán cốt lõi (The Problem)
Khu nghỉ dưỡng AuraMoon định vị là một "Retreat Resort" cao cấp, nơi các dịch vụ Spa và Trị liệu không chỉ là phụ trợ mà là dịch vụ lõi. Tuy nhiên, quy trình quản lý lịch hẹn hiện tại hoàn toàn phụ thuộc vào con người, dẫn đến tình trạng:
- **Tắc nghẽn tài nguyên:** Có chuyên viên rảnh nhưng không có phòng trống (hoặc ngược lại).
- **Rủi ro sức khỏe:** Chuyên viên không nắm được tình trạng bệnh lý của khách (VD: đau thắt lưng, cao huyết áp) trước khi bước vào phòng.
- **Trải nghiệm kém:** Khách hàng phải xuống tận quầy hoặc gọi điện để đặt lịch thủ công.

### 1.2. Các tập người dùng và Bên liên quan (User Classes & Stakeholders)
Dựa trên bài toán, chúng tôi xác định 4 tập người dùng trực tiếp tham gia vào hệ thống:
1. **Khách hàng (Guest):** Người sử dụng dịch vụ, mong muốn sự riêng tư, tiện lợi và an toàn.
2. **Lễ tân (Receptionist):** Người điều phối trực tiếp tại quầy, xử lý các ca phát sinh hoặc khách walk-in.
3. **Chuyên viên Trị liệu (Spa Therapist):** Người trực tiếp cung cấp dịch vụ, cần biết lịch làm việc và tình trạng sức khỏe khách.
4. **Quản lý Spa (Spa Manager - Stakeholder):** Người giám sát hiệu suất phòng và nhân sự, báo cáo doanh thu.

---

## 2. Kế hoạch Khơi gợi Yêu cầu (Elicitation Plan)

Để thu thập thông tin chính xác, team BA đã sử dụng kết hợp 3 kỹ thuật:

| Đối tượng | Kỹ thuật (Technique) | Mục đích / Biểu mẫu (Tài liệu thu thập) |
| :--- | :--- | :--- |
| **Quản lý Spa** | Phỏng vấn sâu (Interview) | Tìm hiểu quy tắc phân bổ phòng: Phòng loại nào (VIP, Thường) dùng cho liệu trình nào? Chuyên viên nào có chứng chỉ đặc biệt? |
| **Lễ tân** | Quan sát (Observation) | "Shadowing" lễ tân trong 2 giờ cao điểm để xem cách họ dùng sổ tay và Excel ghi nhận lịch đặt, cách họ xử lý khi khách đổi giờ. |
| **Khách hàng** | Khảo sát (Survey) | Gửi Google Form cho 50 khách hàng cũ để hỏi về trải nghiệm book lịch: *Điều gì làm bạn khó chịu nhất khi muốn đặt một suất Massage 60 phút?* |
| **Therapist** | Phân tích tài liệu (Doc Analysis) | Xin mẫu "Phiếu khai báo sức khỏe" bằng giấy để số hóa vào hệ thống. |

---

## 3. Phân tích Nỗi đau & Hệ thống hiện tại (Pain points & Current State)

### 3.1. Hệ thống hiện tại (As-is System)
- Đang sử dụng **Sổ ghi chép giấy** kết hợp **Google Sheets**.
- Khách hàng điền "Phiếu sức khỏe" bằng giấy cứng, cất trong kẹp hồ sơ.

### 3.2. Nỗi đau (Pain points)
Từ kết quả phỏng vấn và khảo sát, team đúc kết được các "nỗi đau" lớn nhất:
1. **Lễ tân:** Phải "căng mắt" nhìn Google Sheets để tìm 1 ô trống thỏa mãn cả 3 điều kiện: `Giờ khách muốn` + `Có chuyên viên rảnh` + `Có phòng trống`. Việc này tốn trung bình 5-7 phút/lượt đặt, và cực kỳ dễ dẫn đến **Double-booking** (trùng lịch).
2. **Chuyên viên:** Phiếu sức khỏe giấy hay bị thất lạc. Có trường hợp khách bị thoát vị đĩa đệm nhưng chuyên viên không biết, suýt thực hiện thao tác bẻ khớp nguy hiểm.
3. **Khách hàng:** Phải xếp hàng tại quầy chờ lễ tân dò lịch. Không chủ động được thời gian nghỉ dưỡng.

### 3.3. Giá trị hiện tại chưa đạt được
Dịch vụ Spa vốn dĩ mang lại "sự thư giãn" (Relaxation), nhưng quy trình rườm rà hiện tại lại mang tới "sự căng thẳng" (Stress) cho cả khách lẫn nhân viên.

---

## 4. Đề xuất Tính năng & Mục tiêu Kinh doanh (Features $\rightarrow$ Business Objectives)

Để giải quyết triệt để các Pain Points trên, team đề xuất các Tính năng (Features) tương ứng. Các tính năng này không được xây dựng vô định, mà phải mũi tên chỉ ngược lại phục vụ trực tiếp cho **Mục tiêu kinh doanh (Business Objectives)** của Resort.

### Bảng Mapping: Đi từ Pain Point $\rightarrow$ Feature $\rightarrow$ Business Objective

| Pain Point | Tính năng đề xuất (Features) | Map với Use Case | Đóng góp vào Mục tiêu Kinh doanh (Business Objectives) |
| :--- | :--- | :--- | :--- |
| Khách bị động, phải chờ lễ tân dò lịch | **Self-service Booking:** Khách tự xem khung giờ trống và book lịch trên điện thoại. | `UC11` | **BO1:** Tăng mức độ hài lòng (CSAT) của khách hàng lên 4.5/5. Giảm 80% thời gian chờ đợi. |
| Lễ tân mất 7 phút để xếp lịch, hay bị trùng phòng/người | **Smart Scheduling Engine:** Thuật toán tự động tìm khung giờ trống khớp MỘT chuyên viên rảnh VÀ MỘT phòng trống. | `UC12`, `UC15` | **BO2:** Tăng 30% hiệu suất khai thác phòng Spa. Trí tuệ hóa quy trình, **giảm tỷ lệ Double-booking xuống 0%**. |
| Thiếu thông tin y tế gây rủi ro | **Therapist Dashboard:** Màn hình riêng cho Chuyên viên xem lịch trong ngày, đính kèm cảnh báo sức khỏe (Medical Notes). | `UC13`, `UC14` | **BO3:** Loại bỏ 100% rủi ro y khoa do thiếu thông tin. Nâng tầm tiêu chuẩn dịch vụ Retreat cao cấp. |

### Kết luận
Bộ tính năng của Module 3 không chỉ dừng lại ở việc "số hóa" một cuốn sổ tay, mà nó giải quyết bài toán phức tạp (Complexity) về **Quản lý tài nguyên đa chiều (Khách - Chuyên viên - Phòng)**, mang lại giá trị vận hành thực tế cực kỳ lớn cho AuraMoon Retreat.
