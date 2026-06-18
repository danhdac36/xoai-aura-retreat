# Quy tắc Kiểm thử Dự án (Project Testing Principles)
**Dự án:** Xoai Aura Retreat Management System (HOS-03)  
**Tài liệu:** Quy chuẩn và Nguyên tắc Viết & Thực thi Kiểm thử tự động (Unit Test / Integration Test)

Tài liệu này định nghĩa các nguyên tắc cốt lõi về kiểm thử nhằm đảm bảo chất lượng mã nguồn, tính bảo mật dữ liệu y tế nhạy cảm (Nghị định 356/2025/NĐ-CP) và sự ổn định của hệ thống trong suốt quá trình phát triển.

---

## 📌 1. Quy tắc về dữ liệu kiểm thử (Test Data Classification)

* **Nguyên tắc dữ liệu giả lập:** Mọi dữ liệu sử dụng trong unit tests hoặc integration tests **BẮT BUỘC** phải là dữ liệu giả lập hoàn toàn (`SYNTHETIC`) hoặc dữ liệu đã được ẩn danh hóa (`ANONYMIZED`).
* **❌ TUYỆT ĐỐI NGHIÊM CẤM:** Sử dụng thông tin cá nhân thật, thông tin y tế nhạy cảm thật, hoặc ID/CCCD thật của khách hàng thực tế vào các tệp tin kiểm thử.
* **Hạt giống Dữ liệu (QA Tenant):** Chỉ sử dụng tenant giả lập (ví dụ: `fpt-edu` hoặc tên khách hàng mẫu như `John Doe`, `Minh`) cho việc tạo fixture test.

---

## 📌 2. Độc lập và Tự cô lập (Isolation & Independent Execution)

* **Không phụ thuộc môi trường:** Unit test không được phép kết nối đến cơ sở dữ liệu thật (Production/Staging DB) hoặc gọi API ngoài trực tiếp.
* **Giả lập (Mockito Mocking):** Sử dụng thư viện Mockito để cô lập hoàn toàn lớp nghiệp vụ cần test (Service/Controller) khỏi các lớp truy xuất dữ liệu (Repository) và dịch vụ bên thứ ba.
* **Không phụ thuộc thứ tự:** Mỗi Test Case phải chạy độc lập, tự khởi tạo dữ liệu giả lập của riêng nó (`@BeforeEach`) và không bị ảnh hưởng bởi kết quả của Test Case chạy trước đó.

---

## 📌 3. Quy trình Phát triển Hướng Kiểm thử (TDD Workflow)

* **Quy trình Red-Green-Refactor:** Khuyến khích viết mã kiểm thử tự động **TRƯỚC** khi viết mã nguồn logic nghiệp vụ.
  1. 🔴 **RED Phase:** Viết tệp kiểm thử `.spec.ts` / `*Test.java` mô tả hành vi kỳ vọng và chạy thử nghiệm để xác nhận kiểm thử **THẤT BẠI** do chưa có code logic.
  2. 🟢 **GREEN Phase:** Viết mã nguồn nghiệp vụ tối thiểu để kiểm thử chạy **VƯỢT QUA (PASS)**.
  3. 🔵 **REFACTOR Phase:** Tối ưu hóa cấu trúc mã nguồn (tách hàm, chuẩn hóa kiểu dữ liệu) mà vẫn đảm bảo kiểm thử tiếp tục xanh.
* Không đánh dấu hoàn thành tác vụ nếu `mvn test` chưa hoàn thành thành công và tất cả các test cases chưa vượt qua.

---

## 📌 4. Tiêu chuẩn bao phủ mã nguồn (Test Coverage Target)

* **Ngưỡng tối thiểu:** Các lớp nghiệp vụ Service, DTO validator, và Logic xử lý mới được tạo hoặc sửa đổi phải đạt độ bao phủ dòng lệnh (Line Coverage) tối thiểu **80%**.
* **Độc lập chất lượng:** Không dùng các thuộc tính `any` (trong JS/TS) hoặc trả về kiểu chung chung (`Object` không tường minh trong Java) làm giảm độ tin cậy của trình biên dịch và mã kiểm thử.

---

## 📌 5. Quy chuẩn đặt tên và cấu trúc tệp (Naming & Folder Conventions)

* **Cấu trúc thư mục:** Tệp kiểm thử phải đặt trong thư mục `src/test/java/` và tuân thủ tuyệt đối cấu trúc package của tệp mã nguồn tương ứng (ví dụ: `com.AuraMoon.auramoon.fnb.service`).
* **Quy ước đặt tên tệp:** Tên tệp test phải kết thúc bằng hậu tố `Test` (Ví dụ: `MealOrderServiceTest.java` cho lớp `MealOrderService.java`).
* **Quy ước đặt tên hàm test:** Sử dụng định dạng tự giải thích: `test[Tên_Hàm]_[Kịch_Bản]_[Kết_Quả_Kỳ_Vọng]`.
  * *Ví dụ:* `testUpdateMealOrderStatus_PreparingToPending_Blocked()`

---

## 📌 6. Kiểm thử quy trình trạng thái bất biến (State Invariance Testing)

* Đối với các thực thể chứa quy trình vòng đời trạng thái (như `MealOrder` với trạng thái `Pending -> Preparing -> Ready`):
  * **Happy Path:** Phải có test case kiểm tra chuyển dịch trạng thái tiến lên hợp lệ.
  * **Error Path:** Bắt buộc viết các test cases kiểm tra việc chặn đứng các dịch chuyển trạng thái ngược dòng (ví dụ: quay lại từ `Ready` về `Preparing`) và ném ra lỗi phù hợp (`FAILED`).

---

## 📌 7. Kiểm chứng Giảm thiểu Dữ liệu (Data Minimization Testing)

* **Tuân thủ quy định riêng tư y tế:** Bắt buộc viết kiểm thử cho các API cung cấp cho vai trò vận hành (như Chef, Therapist) để xác minh tính năng ẩn dữ liệu nhạy cảm không cần thiết.
* **Xác thực Backend:** Đảm bảo test case kiểm chứng rằng thông tin bệnh lý vật lý không bao giờ xuất hiện trong payload trả về của API dành cho Chef, chỉ hiển thị thông tin dị ứng ẩm thực cần thiết cho việc chế biến món ăn.

---

## 📌 8. Xác thực rõ ràng và đủ (Explicit Assertions)

* **Assert cụ thể:** Không chỉ kiểm tra hàm chạy không lỗi, phải thực hiện assert chi tiết cấu trúc dữ liệu trả về, nội dung thông báo lỗi, cờ khả dụng, và giá trị số học (ví dụ: làm tròn phí dịch vụ 5% đến 2 chữ số thập phân).
* **Verify Mock:** Sử dụng `verify(mock, times(N)).method(...)` để kiểm tra chính xác số lần tương tác với cơ sở dữ liệu giả lập, ngăn ngừa side-effect ngoài ý muốn.
