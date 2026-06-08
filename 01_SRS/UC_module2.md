# BÁO CÁO PHÂN TÍCH MÃ NGUỒN & THIẾT KẾ CÁC USE CASE - MODULE 2

Tài liệu này phân tích chi tiết kiến trúc hiện tại của dự án `auramoon` liên quan đến **Module 2: Đặt Gói Trị Liệu & Phòng Ở** (do Sinh viên 2 phụ trách), đồng thời đề xuất thiết kế chi tiết (tầng Repository, Service, Controller, DTO) cho từng Use Case trước khi triển khai code thực tế.

---

## I. Cấu Trúc Thực Thể Hiện Có (Existing Entities)

Các thực thể chính của Module 2 đã được định nghĩa tại package `com.AuraMoon.auramoon.booking.entity`:

1. **[RetreatPackage.java](file:///D:/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/booking/entity/RetreatPackage.java):** Gói trị liệu.
   - `id` (package_id): Khóa chính.
   - `packageName` (package_name): Tên gói (tiêu chuẩn GWI).
   - `typePackage` (type_package): Phân loại gói theo mục tiêu sức khỏe (Detox, Yoga, Weight Loss...).
   - `durationDays` (duration_days): Số ngày lưu trú của gói trị liệu.
   - `price` (price): Giá gói.
   - `isActive` (is_active): Trạng thái kích hoạt.
2. **[VillaType.java](file:///D:/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/booking/entity/VillaType.java):** Hạng biệt thự.
   - `id` (type_id): Khóa chính.
   - `typeName` (type_name): Tên loại Villa.
   - `pricePerDay` (price_per_day): Giá thuê mỗi ngày.
3. **[Villa.java](file:///D:/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/booking/entity/Villa.java):** Phòng biệt thự vật lý cụ thể.
   - `id` (villa_id): Khóa chính.
   - `villaType` (villa_type): Liên kết khóa ngoại tới `VillaType`.
   - `villaCode` (villa_code): Mã số phòng (ví dụ: V101, V102...).
   - `villaStatus` (villa_status): Trạng thái phòng (Available, Occupied, Maintenance).
   - `cleaningStatus` (cleaning_status): Trạng thái vệ sinh (Clean, Dirty...).
4. **[Booking.java](file:///D:/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/booking/entity/Booking.java):** Thông tin đặt phòng và gói trị liệu của khách.
   - `id` (booking_id): Khóa chính.
   - `guestId` (guest_id): ID của khách hàng (liên kết chéo với thực thể User ở module Auth).
   - `retreatPackage` (package_id): Gói trị liệu được chọn.
   - `assignedVilla` (assigned_villa_id): Phòng Villa vật lý cụ thể được gán khi Check-in.
   - `checkinDate` & `checkoutDate`: Thời gian lưu trú.
   - `bookingStatus` (booking_status): Trạng thái đặt phòng (PENDING, DEPOSITED, CHECKED_IN, CHECKED_OUT, CANCELLED).
   - `paymentStatus` (payment_status): Trạng thái thanh toán tiền cọc (UNPAID, PAID).

---

## II. Phân Tích & Thiết Kế Chi Tiết Từng Use Case

### 1. UC06: Duyệt và lọc các "Gói trị liệu" theo mục tiêu sức khỏe
* **Phân tích nghiệp vụ:** Khách hàng cần lọc nhanh danh sách các gói trị liệu theo mục tiêu cá nhân (như thải độc, giảm cân, giảm stress).
* **Đề xuất thiết kế:**
  * **Repository:** Tạo [RetreatPackageRepository.java](file:///D:/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/booking/repository/RetreatPackageRepository.java):
    ```java
    @Repository
    public interface RetreatPackageRepository extends JpaRepository<RetreatPackage, Integer> {
        List<RetreatPackage> findByIsActiveTrue();
        List<RetreatPackage> findByTypePackageAndIsActiveTrue(String typePackage);
    }
    ```
  * **Service:** Tạo `RetreatPackageService` xử lý logic lấy danh sách gói, lọc và trả về danh sách DTO.
  * **Controller:** Tạo `RetreatPackageController` ánh xạ URL `/packages` (GET) để hiển thị giao diện danh sách gói.
  * **UI/Thymeleaf:** Xây dựng trang [packages.html] sử dụng CSS thuần (Vanilla CSS) với bảng điều khiển bên (Sidebar) chứa các mục tiêu lọc, danh sách gói hiển thị dạng Card trực quan.

---

### 2. UC07: Chọn gói, chọn ngày, chọn loại Villa và đặt cọc
* **Phân tích nghiệp vụ:** Khách chọn ngày Check-in, hệ thống tính toán ngày Check-out (Check-out = Check-in + số ngày của gói). Hệ thống kiểm tra xem loại biệt thự được chọn còn trống phòng nào trong khoảng thời gian đó không. Sau đó tiến hành thanh toán đặt cọc.
* **Đề xuất thiết kế:**
  * **Kiểm tra phòng trống (Availability Check SQL):**
    Cần viết câu truy vấn kiểm tra số lượng Villa thuộc loại `villaTypeId` đã bị đặt trong khoảng thời gian `[checkinDate, checkoutDate]`. Nếu số phòng đã bị đặt nhỏ hơn tổng số phòng vật lý thuộc loại đó, thì loại Villa đó khả dụng.
  * **Repository:** Tạo [BookingRepository.java](file:///D:/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/booking/repository/BookingRepository.java):
    ```java
    @Repository
    public interface BookingRepository extends JpaRepository<Booking, Integer> {
        @Query("SELECT COUNT(b) FROM Booking b WHERE b.assignedVilla.villaType.id = :typeId " +
               "AND b.bookingStatus IN ('DEPOSITED', 'CHECKED_IN') " +
               "AND (:checkin < b.checkoutDate AND :checkout > b.checkinDate)")
        long countBookedVillas(@Param("typeId") Integer typeId, 
                               @Param("checkin") LocalDate checkin, 
                               @Param("checkout") LocalDate checkout);
    }
    ```
  * **Thanh toán đặt cọc:**
    - Khởi tạo hóa đơn tạm thời (Booking với trạng thái `PENDING` và `paymentStatus = "UNPAID"`).
    - Tạo `PaymentController` điều hướng sang cổng thanh toán mô phỏng. Sau khi thành công, chuyển trạng thái Booking thành `DEPOSITED` và `paymentStatus = "PAID"`.
    - **AHLEI Folio Standard:** Tự động tạo một bản ghi hóa đơn tổng hợp (Central Folio) liên kết với `booking_id` để các module khác (Spa, F&B) sau này đẩy các giao dịch nợ phòng vào.

---

### 3. UC08: Lễ tân thực hiện Check-In (gán số phòng cụ thể & thu thập ID)
* **Phân tích nghiệp vụ:** Khi khách đến, lễ tân kiểm tra đặt phòng của khách. Chọn một phòng Villa vật lý cụ thể (phòng có số hiệu ví dụ: V101) đang trống và sẵn sàng sạch sẽ. Nhập thông tin CCCD/Passport của khách.
* **Đặc biệt lưu ý pháp lý & bảo mật:**
  * **Luật Cư trú 2020:** Bắt buộc thu thập CCCD/Passport.
  * **Nghị định 356/2025/NĐ-CP (Bảo vệ dữ liệu cá nhân):** Dữ liệu định danh là thông tin cá nhân cơ bản/nhạy cảm cần được bảo mật tối đa.
* **Đề xuất thiết kế:**
  * **Mã hóa dữ liệu định danh (Encryption at Rest):**
    - Tạo một lớp tiện ích [SecurityUtils.java](file:///D:/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/common/util/SecurityUtils.java) sử dụng thuật toán **AES-256** để mã hóa chuỗi `identifyCode` trước khi lưu vào cơ sở dữ liệu và giải mã khi hiển thị cho người có thẩm quyền.
  * **Quy trình gán phòng:**
    - Lấy danh sách các `Villa` thuộc loại khách đã đặt mà đang có `villaStatus = "AVAILABLE"` và `cleaningStatus = "CLEAN"`.
    - Gán `assignedVilla` cho `Booking`.
    - Cập nhật trạng thái `Booking` sang `CHECKED_IN`.
    - Cập nhật trạng thái `Villa` sang `OCCUPIED`.

---

### 4. UC09: Quản lý trạng thái Villa vật lý
* **Phân tích nghiệp vụ:** Lễ tân và nhân viên buồng phòng cần xem sơ đồ trực quan và cập nhật nhanh trạng thái của từng Villa vật lý (Trống, Có khách ở, Bảo trì/Dọn dẹp).
* **Đề xuất thiết kế:**
  * **Repository:** Tạo [VillaRepository.java](file:///D:/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/booking/repository/VillaRepository.java):
    ```java
    @Repository
    public interface VillaRepository extends JpaRepository<Villa, Integer> {
        List<Villa> findByIsDeleteFalse();
    }
    ```
  * **Controller:** Cung cấp các API RESTful để đổi trạng thái nhanh:
    - `POST /api/villas/{id}/status?status=MAINTENANCE`
    - `POST /api/villas/{id}/cleaning?status=CLEAN`
  * **UI/Thymeleaf:** Trang quản lý sơ đồ phòng hiển thị danh sách phòng dưới dạng ô lưới (Grid layout) với màu sắc trạng thái rõ ràng giúp Lễ tân thao tác nhanh bằng một cú nhấp chuột.

---

### 5. UC10: Khách xem chi tiết đặt phòng và dòng thời gian lịch trình (Itinerary Timeline)
* **Phân tích nghiệp vụ:** Khách hàng đăng nhập có thể xem toàn bộ lịch trình chuyến đi của mình theo trình tự thời gian bao gồm lịch Spa (Module 3) và lịch ăn uống (Module 4).
* **Đề xuất thiết kế:**
  * **Liên kết dữ liệu (Cross-module Join):**
    - Hệ thống cần truy vấn bảng `Booking` của khách hàng đang đăng nhập.
    - Truy vấn danh sách lịch hẹn Spa (`Spa_Appointment`) đã được đặt tương ứng với `booking_id` của khách hàng.
    - Truy vấn danh sách món ăn đã đặt (`Meal_Order`) theo ngày tương ứng với `booking_id`.
  * **DTO xây dựng timeline:** Tạo `ItineraryTimelineDTO` chứa danh sách các `TimelineEvent` (mỗi event có thời gian bắt đầu, loại sự kiện: Check-in, Spa, Meal, Yoga, Check-out, tiêu đề và chi tiết).
  * **Giao diện:** Thiết kế giao diện Timeline cuộn dọc (Vertical Timeline) mượt mà bằng CSS thuần, hỗ trợ hiển thị hoàn hảo trên giao diện điện thoại di động (Responsive Web Design).

---

> [!IMPORTANT]
> **Cam kết thực hiện:** Toàn bộ phân tích này là cơ sở dữ liệu định hướng cho quá trình triển khai code Module 2. Bất kỳ thay đổi mã nguồn nào của dự án liên quan đến thiết kế này sẽ chỉ được tiến hành sau khi nhận được sự đồng ý và phê duyệt rõ ràng từ bạn.
