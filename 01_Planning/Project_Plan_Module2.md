# KẾ HOẠCH PHÁT TRIỂN MODULE 2 (PROJECT PLAN)
## RETREAT PACKAGE & ACCOMMODATION BOOKING

---

## I. Mục tiêu & Phạm vi Module 2
Module 2 chịu trách nhiệm xử lý luồng nghiệp vụ tìm kiếm, lựa chọn, đặt chỗ nghỉ dưỡng trị liệu, thanh toán đặt cọc và quản lý nhận phòng (Check-in), lộ trình lưu trú của khách tại **Xoai Aura Retreat**.

### 1. Danh sách Use Cases (UC) thuộc phạm vi
*   **UC06**: Browse Wellness Packages (Tìm kiếm & Lọc gói trị liệu).
*   **UC07**: Book Retreat Package & Pay Deposit (Đặt phòng, chọn loại phòng & thanh toán đặt cọc).
*   **UC08**: Check In Guest (Làm thủ tục nhận phòng, gán số phòng vật lý & mã hóa CCCD/Passport).
*   **UC09**: Manage Villa Status (Quản lý trạng thái hoạt động thực tế và vệ sinh của biệt thự).
*   **UC10**: View Booking Details & Itinerary Timeline (Xem lộ trình cá nhân tổng hợp chéo).

---

## II. Phân rã công việc (Work Breakdown Structure - WBS)

Kế hoạch thực hiện Module 2 được chia làm **5 Giai đoạn cốt lõi**:

### Giai đoạn 1: Phân tích & Đặc tả Thiết kế (Requirement & Design)
*   **Task 1.1**: Phân tích nghiệp vụ Module 2, đặc tả chi tiết các Use Cases (lưu tại `02_Requirement/Module_2.md`). [ĐÃ HOÀN THÀNH]
*   **Task 1.2**: Đặc tả thiết kế kỹ thuật **EDS** cho Module 2 (Kiến trúc Class, API Endpoints, cấu trúc JSON Request/Response, giải thuật gán phòng và tích hợp VNPay).
*   **Task 1.3**: Đặc tả kịch bản kiểm thử hướng phát triển **TDD** (Danh sách các Test Cases kiểm thử đơn vị Happy Path, Error Path, Validation Error).

### Giai đoạn 2: Thiết lập Cơ sở dữ liệu & Cấu hình môi trường (DB & Environment Setup)
*   **Task 2.1**: Đồng bộ và kiểm tra các thực thể JPA tương ứng trong mã nguồn Spring Boot:
    *   `Booking`
    *   `RetreatPackage`
    *   `Villa`
    *   `VillaType`
*   **Task 2.2**: Cấu hình môi trường thử nghiệm thanh toán Sandbox (VNPay Properties trong `application.properties`).

### Giai đoạn 3: Phát triển Mã nguồn (Implementation - Code Production)
*   **Task 3.1**: Phát triển tầng dữ liệu (Repository layer):
    *   `BookingRepository`, `VillaRepository`, `VillaTypeRepository`, `RetreatPackageRepository`.
*   **Task 3.2**: Phát triển tầng xử lý nghiệp vụ (Service layer & Implementations):
    *   `RetreatPackageService`: Xử lý tìm kiếm và bộ lọc gói trị liệu.
    *   `BookingService`: Xử lý tạo đơn đặt phòng, liên kết VNPay, xác nhận thanh toán đặt cọc và tự động mở hóa đơn nợ (`GuestFolio`).
    *   `CheckInService`: Xử lý gán số phòng vật lý trống/sạch, yêu cầu bắt buộc và mã hóa CCCD/Passport của khách (tuân thủ Luật Cư trú 2020).
    *   `VillaService`: Xử lý cập nhật và quản lý trạng thái dọn dẹp buồng phòng.
    *   `ItineraryService`: Xử lý tổng hợp chéo lịch trình để hiển thị timeline.
*   **Task 3.3**: Phát triển tầng giao tiếp (Controller layer):
    *   Các endpoints MVC phục vụ hiển thị giao diện Thymeleaf và tương tác của khách hàng/nhân viên lễ tân.
*   **Task 3.4**: Hoàn thiện Giao diện người dùng (Frontend - UI Thymeleaf/HTML/CSS):
    *   Trang chủ, danh sách gói trị liệu, trang chi tiết gói và form đặt phòng.
    *   Màn hình danh sách dự kiến đến hôm nay và form làm thủ tục Check-in cho Lễ tân.
    *   Trang hiển thị dòng thời gian Itinerary Timeline của khách hàng.

### Giai đoạn 4: Viết và Chạy Kiểm thử (Testing & Validation)
*   **Task 4.1**: Viết các Unit Test classes tương ứng sử dụng Mockito (JUnit 5):
    *   `BookingServiceTest`, `CheckInServiceTest`, `ReviewServiceTest`.
*   **Task 4.2**: Chạy kiểm thử tự động (`mvn test`) và đảm bảo tỷ lệ Pass đạt 100%.
*   **Task 4.3**: Khắc phục các lỗi kiểm thử (Refactoring) và tối ưu mã nguồn.

### Giai đoạn 5: Đóng gói & Bàn giao (Release & Document Walkthrough)
*   **Task 5.1**: Tạo Báo cáo Kết quả Kiểm thử (Test Report) đối chiếu sự tuân thủ chuẩn EDS/TDD.
*   **Task 5.2**: Viết tài liệu bàn giao `walkthrough.md` tổng kết các chức năng đã xây dựng thành công.

---

## III. Kế hoạch thời gian & Mốc quan trọng (Milestones)

| Mốc Milestone | Nội dung công việc chính | Thời lượng dự kiến | Trạng thái |
| :--- | :--- | :--- | :--- |
| **M1: Specification Approved** | Hoàn thành viết và phê duyệt các tài liệu Phân tích yêu cầu, EDS và TDD của Module 2. | 3 Ngày | Đang thực hiện |
| **M2: Database & Model Sync** | Đồng bộ database, tạo đầy đủ các thực thể và cấu hình kết nối. | 1 Ngày | Hoàn thành |
| **M3: Core Logic & Unit Tests** | Phát triển xong toàn bộ tầng Service, Repository và viết Unit Test đạt 100% tỷ lệ pass. | 5 Ngày | Hoàn thành |
| **M4: UI & Integration** | Hoàn thiện giao diện Thymeleaf, kết nối Front-end với Backend, tích hợp thành công VNPay Sandbox. | 4 Ngày | Hoàn thành |
| **M5: Acceptance & Release** | Chạy kiểm thử hệ thống, hoàn thành báo cáo kiểm thử và bàn giao mã nguồn. | 2 Ngày | Đang thực hiện |

---

## IV. Quản lý Rủi ro & Cách khắc phục (Risk Mitigation)

1.  **Trùng lịch phòng vật lý khi Check-in cùng lúc (Race Condition):**
    *   *Mô tả rủi ro:* Hai lễ tân cùng Check-in cho hai đoàn khác nhau và chọn gán chung một số phòng Villa vật lý tại cùng một thời điểm.
    *   *Khắc phục:* Áp dụng cơ chế khóa dữ liệu (Pessimistic Locking / `@Transactional`) khi lễ tân bấm xác nhận gán phòng để đảm bảo tính nhất quán dữ liệu.
2.  **Rò rỉ dữ liệu thông tin cá nhân (PII Leak):**
    *   *Mô tả rủi ro:* Số CCCD/Passport của khách hàng bị hiển thị dưới dạng văn bản thuần (Plaintext) trên giao diện hoặc bị rò rỉ qua các log hệ thống.
    *   *Khắc phục:* Luôn mã hóa dữ liệu CCCD bằng thuật toán bảo mật trước khi lưu vào cơ sở dữ liệu. Che giấu thông tin (Masking) trên giao diện lễ tân (chỉ hiển thị dạng `********1234`).
3.  **Lỗi giao dịch từ cổng thanh toán VNPay:**
    *   *Mô tả rủi ro:* Khách hàng đã thanh toán thành công trên cổng VNPay nhưng kết nối mạng bị gián đoạn khiến VNPay Callback không thể cập nhật trạng thái đơn hàng về hệ thống Aura Moon.
    *   *Khắc phục:* Xây dựng cơ chế xác minh chủ động (Query Transaction API của VNPay) để kiểm tra đối chiếu định kỳ trạng thái của các đơn hàng PENDING.
