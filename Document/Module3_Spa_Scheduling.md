# Module 3 — Hệ thống Lên lịch Spa & Trị liệu

## Tổng quan
Module 3 xử lý toàn bộ logic lên lịch và quản lý liệu trình Spa/Trị liệu cho khách đã đặt gói nghỉ dưỡng. Mục tiêu chính: cho phép khách đặt lịch các buổi trị liệu kèm theo, tự động ghép lịch hợp lệ (therapist + phòng + khung giờ), hỗ trợ thao tác thủ công qua lễ tân và cho phép therapist xem/đánh dấu trạng thái buổi trị liệu.

## Actors
- Khách (Guest) — đã đặt gói (booking).
- Hệ thống tự động (Scheduler) — tìm khung giờ trống, ghép therapist & phòng.
- Chuyên viên Spa / Therapist — xem lịch, ghi chú y tế, đánh dấu hoàn thành/no-show.
- Lễ tân (Frontdesk) — đặt lịch thủ công, thêm dịch vụ bổ sung và gắn phí vào folio.
- Quản trị (Admin) — quản lý master data (therapist, phòng, dịch vụ).

## Entities chính (mapping tới DB)
- `Booking` (BOOKING) — thông tin khách, ngày checkin/checkout, booking_id.
- `TreatmentService` (TREATMENT_SERVICE) — dịch vụ spa, thời lượng, mã.
- `TreatmentBooking` (TREATMENT_BOOKING) — bản ghi dịch vụ đã đặt (treatment_id, booking_id, service_id, folio_id, status).
- `Schedule` (SCHEDULE) — khung thời gian thực tế (schedule_id, treatment_id, therapist_code, room_id, start_time, end_time, is_delete).
- `Therapist` (THERAPIST) — therapist_id, therapist_code, trạng thái.
- `TreatmentRoom` (TREATMENT_ROOM) — room_id, room_code, trạng thái.

## Business Rules & Ràng buộc quan trọng
- R1 — Ngăn đặt trùng: một lịch trị liệu hợp lệ phải đảm bảo cả Therapist và TreatmentRoom đều rảnh trong cùng khung giờ.
- R2 — Giao dịch (transaction): khi tạo lịch tự động hoặc thủ công, thao tác phải được thực hiện trong transaction để khoá đồng thời tài nguyên therapist và room (PESSIMISTIC/DB LOCK) để tránh race condition.
- R3 — Phân quyền dữ liệu nhạy cảm: Therapist chỉ xem dữ liệu y tế liên quan đến khách được phân công; lễ tân không được xem dữ liệu y tế nhạy cảm.
- R4 — Trạng thái lịch: `Scheduled`, `Completed`, `No-Show`, `Cancelled`.
- R5 — Tính phí bổ sung: khi lễ tân thêm dịch vụ spa thủ công, tự động tạo `FolioItem` và cộng vào `GuestFolio`.

## Use Cases (Chi tiết)

### UC11 — Khách lên lịch cho các buổi Spa/Trị liệu (Chọn ngày & khung giờ)
- Primary actor: Khách (Guest)
- Trigger: Khách muốn lên lịch cho các buổi thuộc gói đã đặt.
- Preconditions: Khách có `Booking` hợp lệ; các dịch vụ trong gói cho phép đặt lịch; khách đang trong khoảng check-in..checkout.
- Main success scenario:
  1. Khách mở trang "Lịch trình Spa" cho booking.
  2. Khách chọn dịch vụ (treatment), chọn ngày và khoảng thời gian mong muốn.
  3. Hệ thống kiểm tra tính khả dụng sơ bộ (therapist & room) cho khung giờ đó.
  4. Hệ thống trả về lựa chọn khả dụng (nếu có) và khách xác nhận.
  5. Hệ thống tạo `TreatmentBooking` và `Schedule` tương ứng, gửi email/xác nhận.
- Alternative flows:
  - A1: Không có khung giờ trống → hệ thống đề xuất khung giờ thay thế hoặc đưa vào waitlist.
  - A2: Khách huỷ trước khi xác nhận → không tạo bản ghi.
- Postconditions: Lịch được tạo với trạng thái `Scheduled` và liên kết tới booking và folio (nếu có phí phát sinh).

### UC12 — Hệ thống tự động ghép khung giờ trống (Auto-scheduler)
- Primary actor: Hệ thống Scheduler
- Trigger: Khách yêu cầu tìm khung giờ hoặc background job cân bằng lịch.
- Preconditions: Danh sách therapist/room và giờ làm việc có sẵn; dịch vụ có thời lượng xác định.
- Main success scenario:
  1. Scheduler nhận yêu cầu đặt lịch (service, khoảng ngày, ưu tiên nếu có).
  2. Scheduler quét khoảng thời gian trong ngày, kiểm tra rảnh của therapist và room (khoảng start..end đủ thời lượng) bằng cách kiểm tra `Schedule` hiện tại và thời gian làm việc của therapist/room.
  3. Nếu tìm thấy cặp (therapist, room) rảnh thì tạo `TreatmentBooking` và `Schedule` trong một transaction và trả kết quả cho user.
- Alternative flows:
  - A1: Không tìm thấy cặp phù hợp → trả về "no availability" và gợi ý thời gian khác.
- Non-functional: Thuật toán cần tối ưu (O(n) theo số slot) và chịu được nhiều request đồng thời; cần lock nguồn lực khi ghi.

### UC13 — Therapist xem lịch và ghi chú y tế của khách
- Primary actor: Therapist
- Trigger: Therapist mở ứng dụng/portal để xem lịch ca làm việc.
- Preconditions: Therapist đã được đăng nhập và chỉ xem những khách hàng được phân công.
- Main scenario:
  1. Therapist truy vấn `Schedule` theo ngày/therapist_code.
  2. Hệ thống trả về danh sách các `TreatmentBooking` kèm thông tin booking cơ bản và trường y tế liên quan (medicalConditions, injuries) cho khách hàng tương ứng.
  3. Therapist có thể thêm ghi chú ngắn trước/sau buổi trị liệu.
- Security: Chỉ hiển thị dữ liệu y tế cần thiết và chỉ khi therapist là người phụ trách schedule đó.

### UC14 — Therapist đánh dấu trạng thái buổi trị liệu (Completed / No-Show)
- Primary actor: Therapist
- Trigger: Sau khi buổi kết thúc hoặc khách vắng mặt.
- Preconditions: Therapist có quyền sửa trạng thái cho schedule được phân công.
- Main scenario:
  1. Therapist chọn lịch và cập nhật trạng thái `Completed` hoặc `No-Show`.
  2. Hệ thống lưu trạng thái, cập nhật `TreatmentBooking.status` và nếu `Completed`, có thể ghi thời lượng thực tế/ghi chú; nếu `No-Show` có thể trigger chính sách phạt.
  3. Nếu phát sinh phí (ví dụ no-show charge), hệ thống tạo `FolioItem` và cập nhật `GuestFolio`.
- Postconditions: Trạng thái lịch cập nhật, folio cập nhật nếu cần.

### UC15 — Lễ tân đặt lịch thủ công & tính phí vào folio
- Primary actor: Lễ tân
- Trigger: Khách yêu cầu thêm dịch vụ spa bổ sung hoặc lễ tân cần khắc phục yêu cầu.
- Preconditions: Lễ tân có quyền tạo patch booking/schedule và tạo folio item.
- Main scenario:
  1. Lễ tân chọn booking của khách, chọn dịch vụ spa, chọn khung giờ khả dụng (kiểm tra availability tương tự Scheduler) hoặc đánh dấu "đặt tạm".
  2. Sau khi xác nhận, hệ thống tạo `TreatmentBooking` + `Schedule` và đồng thời tạo `FolioItem` (service_category=SPA, amount=price) và liên kết đến `GuestFolio` của booking.
  3. Hệ thống cập nhật folio tổng.
- Alternative: Nếu không còn slot trống, lễ tân có thể đặt vào waitlist hoặc liên hệ therapist để thu xếp.

## Giao diện API đề xuất (REST)
- GET /api/bookings/{bookingId}/treatments/availability?serviceId=&date=&duration=  — trả các slot khả dụng
- POST /api/bookings/{bookingId}/treatments — tạo TreatmentBooking (body: serviceId, startTime, therapistCode?, roomId?, autoAssign=true/false)
- POST /api/scheduler/autoassign — (internal) tìm & book slot tự động
- GET /api/therapists/{code}/schedules?date= — therapist lấy lịch trong ngày
- PATCH /api/schedules/{scheduleId}/status — cập nhật trạng thái (Completed/No-Show/Cancelled)
- POST /api/bookings/{bookingId}/folio-items — tạo folio item (dùng bởi lễ tân hoặc hệ thống khi phát sinh phí)

## Kiến nghị về Implemention/Thiết kế kỹ thuật
- Ghi lịch bằng transaction + optimistic/pessimistic locking (ví dụ: SELECT ... FOR UPDATE trên bảng SCHEDULE / THERAPIST / TREATMENT_ROOM) để đảm bảo không bị double-booking.
- Map `duration_minutes` từ `TREATMENT_SERVICE` để tính end_time = start_time + duration.
- Sử dụng chỉ mục trên `Schedule(start_time, end_time, therapist_code, room_id)` để tăng hiệu năng kiểm tra conflict.
- Triển khai background job / worker để chạy auto-scheduler và gửi thông báo email/SMS.
- Các hành động sinh phí phải tách ra service `BillingIntegrationService` để ghi `FolioItem` và đảm bảo atomic khi cập nhật folio.

## Acceptance Criteria (Kiểm thử chấp nhận)
- AC1: Hệ thống không cho phép tạo hai schedule cùng therapist + room trùng khung giờ.
- AC2: Therapist chỉ nhìn thấy dữ liệu y tế của khách được phân công.
- AC3: Khi lễ tân thêm dịch vụ, `GuestFolio` được cập nhật tương ứng.
- AC4: Auto-scheduler tìm và block slot hợp lệ, phản hồi trong vòng 2s cho request phổ biến.

---

Tôi đã tạo file này tại: `Document/Module3_Spa_Scheduling.md`.
Nếu bạn muốn, tôi có thể:
- Chuyển các use case thành user stories chi tiết (Given/When/Then),
- Viết unit/integration tests cho scheduler service,
- Hoặc implement repository + service skeleton cho Module 3 ngay bây giờ.
