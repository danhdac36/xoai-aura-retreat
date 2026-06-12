# Hướng dẫn Tối ưu hóa Code Spring Boot / JPA dựa trên Cơ sở Dữ liệu HoS

Tài liệu này hướng dẫn cách tổ chức và viết mã nguồn Java (Spring Boot + Spring Data JPA + Hibernate) tối ưu dựa trên cấu trúc cơ sở dữ liệu `HoS`. Việc áp dụng các nguyên tắc này sẽ giúp giảm số lượng truy vấn thừa (N+1 query), tăng tốc độ xử lý dữ liệu, kiểm soát tính nhất quán và tối ưu hóa tài nguyên RAM/CPU.

---

## 1. Phòng tránh N+1 Query với JPA Fetching

### 1.1. Luôn sử dụng Lazy Loading cho các mối quan hệ `@ManyToOne` và `@OneToOne`
Mặc định trong JPA, các mối quan hệ `@ManyToOne` và `@OneToOne` được cấu hình là `FetchType.EAGER`. Điều này có nghĩa là mỗi khi bạn truy vấn thực thể cha, Hibernate sẽ tự động sinh thêm các câu lệnh truy vấn phụ để lấy thông tin thực thể con, gây lãng phí hiệu năng nghiêm trọng.
* **Đề xuất:** Luôn chỉ định `fetch = FetchType.LAZY`. Ví dụ trong [Schedule.java](file:///d:/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/spa/entity/Schedule.java):
  ```java
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "treatment_id", nullable = false)
  private TreatmentBooking treatmentBooking;
  ```

### 1.2. Sử dụng `JOIN FETCH` hoặc Entity Graph khi cần lấy dữ liệu liên quan
Khi bạn hiển thị danh sách (ví dụ: danh sách lịch trình trị liệu cùng với thông tin tên phòng trị liệu và tên kỹ thuật viên), sử dụng Lazy Loading đơn thuần sẽ tạo ra một loạt truy vấn con khi bạn gọi `.getRoom().getRoomName()`.
* **Giải pháp:** Sử dụng `JOIN FETCH` trong JPQL ở tầng Repository để lấy toàn bộ dữ liệu liên quan trong **1 câu truy vấn duy nhất**:
  ```java
  public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
      @Query("SELECT s FROM Schedule s " +
             "JOIN FETCH s.treatmentBooking tb " +
             "JOIN FETCH s.room r " +
             "JOIN FETCH s.therapist t " +
             "WHERE s.startTime >= :start AND s.endTime <= :end AND s.isDelete = false")
      List<Schedule> findSchedulesInPeriod(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
  }
  ```

---

## 2. Tự động hóa Cơ chế Xóa mềm (Soft Delete) trong JPA

Hệ thống có nhiều bảng sử dụng trường `is_delete` (như `BOOKING`, `SCHEDULE`, `VILLA`, `TREATMENT_SERVICE`). Nếu bạn lọc thủ công `WHERE is_delete = false` trong từng hàm của Repository, code sẽ rất dài dòng và dễ sót.
* **Giải pháp:** Sử dụng các Annotation của Hibernate để tự động hóa:
  * `@SQLDelete`: Tự động chuyển lệnh `delete` của JPA thành câu lệnh `UPDATE ... SET is_delete = true`.
  * `@SQLRestriction` (hoặc `@Where` đối với Hibernate 5): Tự động thêm điều kiện lọc `is_delete = 0` vào tất cả các câu lệnh `SELECT`.

Ví dụ áp dụng cho [Schedule.java](file:///d:/su26-swp391-se2023-g6/auramoon/src/main/java/com/AuraMoon/auramoon/spa/entity/Schedule.java):
```java
@Entity
@Table(name = "SCHEDULE")
@SQLDelete(sql = "UPDATE SCHEDULE SET is_delete = 1 WHERE schedule_id = ?")
@SQLRestriction("is_delete = 0") // Hibernate 6.x (Spring Boot 3)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {
    // ...
}
```
* **Lưu ý:** Khi sử dụng `@SQLRestriction("is_delete = 0")`, bạn không cần viết thêm điều kiện `isDelete = false` trong các phương thức truy vấn nữa.

---

## 3. Tối ưu hóa bộ nhớ với Projection / DTO

Tránh trả trực tiếp thực thể JPA ra ngoài Controller hoặc API nếu không cần thiết. Việc này khiến Jackson Serialization phải load toàn bộ Proxy lười (Lazy), dẫn đến lỗi `LazyInitializationException` hoặc sinh thêm vô số truy vấn không cần thiết.
* **Giải pháp:** Sử dụng Interface Projection để chỉ lấy đúng những trường cần thiết:
  ```java
  public interface VillaSummary {
      Integer getId();
      String getVillaCode();
      String getVillaStatus();
      // Spring Data JPA sẽ sinh câu SELECT chỉ lấy 3 cột này thay vì SELECT *
  }
  ```
  Sử dụng trong Repository:
  ```java
  List<VillaSummary> findByVillaStatus(String status);
  ```

---

## 4. Quản lý Concurrency (Đặt phòng trùng lặp) bằng Optimistic Locking

Trong hệ thống đặt chỗ khu nghỉ dưỡng, việc nhiều khách hàng cùng lúc chọn đặt một Villa hoặc một lịch trị liệu của cùng một Kỹ thuật viên (Therapist) tại một khung giờ rất dễ xảy ra (Race Condition).
* **Giải pháp:** Sử dụng Khóa lạc quan (Optimistic Locking) bằng cách thêm cột `@Version` vào thực thể:
  ```java
  @Version
  @Column(name = "version")
  private Integer version;
  ```
* **Cách hoạt động:** Khi cập nhật trạng thái Villa thành `BOOKED`, JPA sẽ kiểm tra xem `version` trong DB có khớp với `version` của thực thể hiện tại không. Nếu một giao dịch khác đã cập nhật trước, hệ thống sẽ quăng ra lỗi `OptimisticLockingFailureException`. Bạn chỉ cần catch exception này và hiển thị thông báo: *"Villa hoặc khung giờ này đã có người đặt trước, vui lòng thử lại."*

---

## 5. Đồng bộ hóa Bi-directional Associations (Mối quan hệ 2 chiều)

Khi lưu trữ cấu trúc dạng cha-con như `MealOrder` và `MealOrderItem`, hãy luôn định nghĩa mối quan hệ hai chiều chính xác và sử dụng các helper methods để đồng bộ trạng thái trong Java:

Ví dụ định nghĩa mối quan hệ trong `MealOrder.java`:
```java
@OneToMany(mappedBy = "mealOrder", cascade = CascadeType.ALL, orphanRemoval = true)
private List<MealOrderItem> orderItems = new ArrayList<>();

// Helper method đồng bộ
public void addOrderItem(MealOrderItem item) {
    orderItems.add(item);
    item.setMealOrder(this);
}

public void removeOrderItem(MealOrderItem item) {
    orderItems.remove(item);
    item.setMealOrder(null);
}
```
* Điều này đảm bảo rằng khi bạn gọi `mealOrderRepository.save(mealOrder)`, các `MealOrderItem` con sẽ tự động được gán đúng `meal_order_id` và được lưu vào cơ sở dữ liệu.

---

## 6. Sử dụng Kiểu Dữ liệu Tài chính Chính xác
* Đối với toàn bộ trường số tiền trong hệ thống (như `price`, `total_extra_fb`, `final_amount`, `price_per_day`), tuyệt đối **không dùng `double` hoặc `float`** trong Java do lỗi làm tròn số nhị phân.
* **Quy chuẩn:** Luôn ánh xạ `DECIMAL(18,2)` trong SQL sang `java.math.BigDecimal` trong Java để đảm bảo tính toán tài chính và tiền tệ chính xác 100%.

---

## 7. Cấu hình Transactions hiệu quả
* Đối với các dịch vụ chỉ đọc dữ liệu (như hiển thị thông tin gói, tìm kiếm phòng trống): Luôn khai báo `@Transactional(readOnly = true)` ở tầng Service. Điều này giúp Hibernate tắt chế độ theo dõi thay đổi (dirty checking), tiết kiệm bộ nhớ RAM và tối ưu hóa tốc độ kết nối CSDL.
* Đối với các thao tác đặt chỗ liên quan đến nhiều bảng (Ghi nhận booking -> Cập nhật villa -> Tạo hóa đơn): Luôn đặt `@Transactional` để đảm bảo nếu xảy ra lỗi ở bất kỳ bước nào, toàn bộ quá trình sẽ được `rollback` tự động, tránh tình trạng mồ côi dữ liệu (ví dụ: tạo booking thành công nhưng không tạo được hóa đơn).
