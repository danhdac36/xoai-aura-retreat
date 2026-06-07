# Software Requirements Specification (SRS) - AuraMoon Resort Management System

## 1. Introduction
### 1.1 Purpose
Tài liệu này đặc tả các yêu cầu phần mềm cho hệ thống quản lý khu nghỉ dưỡng AuraMoon, cung cấp cái nhìn tổng quan về chức năng và phi chức năng của hệ thống.

### 1.2 Scope
Hệ thống AuraMoon là một nền tảng quản lý tích hợp dành cho khu nghỉ dưỡng cao cấp, bao gồm các module:
- Quản lý xác thực và người dùng (Auth/User)
- Quản lý đặt phòng và Villa (Booking/Villa)
- Quản lý dịch vụ Spa (Spa/Treatment)
- Quản lý ẩm thực (F&B/Meal Order)
- Quản lý thanh toán và hóa đơn (Billing/Payment)

## 2. Overall Description
### 2.1 User Classes and Characteristics
- **Guest (Khách hàng):** Xem thông tin, đặt phòng, đặt dịch vụ spa/fnb.
- **Admin/Manager:** Quản lý toàn bộ hệ thống, báo cáo, quản lý nhân sự.
- **Staff (Nhân viên):** Tiếp nhận yêu cầu, cập nhật trạng thái phòng/dịch vụ.
- **Therapist (Kỹ thuật viên Spa):** Quản lý lịch trình trị liệu.

### 2.2 Operating Environment
- Backend: Java 21, Spring Boot 4.0.6 (Snapshot/Experimental)
- Database: Microsoft SQL Server
- Frontend: Thymeleaf (Server-side rendering)

## 3. System Features

### 3.1 Module Authentication (Auth)
- **FEAT-01:** Đăng ký/Đăng nhập người dùng.
- **FEAT-02:** Quản lý phân quyền (Role-based Access Control).
- **FEAT-03:** Quản lý hồ sơ sức khỏe và ăn uống (Physical/Dietary Profile).

### 3.2 Module Booking
- **FEAT-04:** Quản lý loại Villa và phòng.
- **FEAT-05:** Đặt phòng và quản lý gói nghỉ dưỡng (Retreat Package).
- **FEAT-06:** Quản lý trạng thái Villa (Trống, Đang ở, Đang dọn dẹp).

### 3.3 Module Spa
- **FEAT-07:** Quản lý dịch vụ trị liệu (Treatment Service).
- **FEAT-08:** Đặt lịch hẹn và phân bổ kỹ thuật viên (Schedule/Therapist).
- **FEAT-09:** Quản lý phòng trị liệu.

### 3.4 Module F&B
- **FEAT-10:** Quản lý thực đơn (Menu Items).
- **FEAT-11:** Đặt món ăn tại phòng hoặc nhà hàng (Meal Order).

### 3.5 Module Billing & Payment
- **FEAT-12:** Quản lý hóa đơn tổng hợp (Guest Folio).
- **FEAT-13:** Xử lý thanh toán qua các cổng thanh toán.

## 4. Non-functional Requirements
- **Security:** Mã hóa mật khẩu (BCrypt), bảo mật dữ liệu khách hàng.
- **Performance:** Hệ thống phản hồi nhanh cho các truy vấn đặt phòng.
- **Availability:** Đảm bảo hệ thống hoạt động 24/7.
