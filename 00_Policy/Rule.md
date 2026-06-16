# Các Nguyên Tắc Dự Án (Project Principles)

File này lưu trữ các nguyên tắc cốt lõi trong quá trình phát triển dự án. Các nguyên tắc này sẽ được tuân thủ nghiêm ngặt và có thể được cập nhật, bổ sung thêm theo thời gian.

## Nguyên tắc 1: Kiến trúc Spring Boot MVC
Đây là dự án viết bằng Java sử dụng mô hình **MVC (Model-View-Controller)** của Spring Boot để thiết kế (Ví dụ: Controller trả về giao diện View như Thymeleaf, xử lý ModelAttributes) chứ **KHÔNG** phải là thiết kế theo hướng RESTful API (trả về dữ liệu JSON).

## Nguyên tắc 2: Định dạng File đầu ra
Mọi file mà người dùng yêu cầu tạo ra đều phải được tạo dưới dạng **Markdown (.md)** (bao gồm cả việc viết code thì code cũng sẽ được bọc bên trong file Markdown thay vì tạo file mã nguồn trực tiếp).

## Nguyên tắc 3: Báo cáo trước khi thay thế code
Sau khi tôi yêu cầu bạn làm gì thì bạn đều phải báo cáo cái mà bạn phân tích và đọc được cho tôi. Sau khi tôi chấp nhận mới được phép thay thế code trong dự án.

## Nguyên tắc 4: Tuân thủ cấu trúc Layout và Module của dự án
Khi thiết kế và xây dựng giao diện (UI), tuyệt đối không viết code Javascript (`<script>`) hay CSS (`<style>`) nội tuyến trực tiếp bên trong file HTML. 
Mọi mã nguồn phụ trợ phải được tách riêng vào các thư mục tĩnh (`static`) và **BẮT BUỘC phải được chia nhỏ theo từng module chuyên biệt**.
Ví dụ: Các file của module Thanh toán (Billing) phải nằm trong đúng đường dẫn module của nó như `static/js/billing/` hoặc `static/css/billing/`. Từ đó mới được nhúng (link) vào file HTML tương ứng.
