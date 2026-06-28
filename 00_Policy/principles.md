# Các Nguyên Tắc Dự Án (Project Principles)

File này lưu trữ các nguyên tắc cốt lõi trong quá trình phát triển dự án. Các nguyên tắc này sẽ được tuân thủ nghiêm ngặt và có thể được cập nhật, bổ sung thêm theo thời gian.

## Nguyên tắc 1: Kiến trúc Spring Boot MVC

Đây là dự án viết bằng Java sử dụng mô hình **MVC (Model-View-Controller)** của Spring Boot để thiết kế (Ví dụ: Controller trả về giao diện View như Thymeleaf, xử lý ModelAttributes)

Cách đặt thên method, class, tất cả phải tuân thủ theo phương pháp java naming convention

Ưu tiên OOP.

Mọi bước chuyển đổi dữ liệu cần được thực hiện ở bằng lớp DTO (request/response tương ứng) tránh việc code vào entity.

## Nguyên tắc 2: Định dạng File đầu ra

Mọi file mà người dùng yêu cầu tạo ra đều phải được tạo dưới dạng **Markdown (.md)** (bao gồm cả việc viết code thì code cũng sẽ được bọc bên trong file Markdown thay vì tạo file mã nguồn trực tiếp).

File phải được lưu trong thư mục 07_Reports

## Nguyên tắc 3: Báo cáo trước khi thay thế code

Sau khi tôi yêu cầu bạn làm gì thì bạn đều phải báo cáo cái mà bạn phân tích và đọc được cho tôi. Sau khi tôi chấp nhận mới được phép thay thế code trong dự án.

## Nguyên tắc 4: Tuân thủ cấu trúc Layout và Module của dự án

Trang html của tôi sẽ chứa toài bộ html, css, javascript. Hãy refactor lại đúng như mô tả kiến trúc dưới đây:

Khi thiết kế và xây dựng giao diện (UI), tuyệt đối không viết code Javascript (`<script>`) hay CSS (`<style>`) nội tuyến trực tiếp bên trong file HTML.
Mọi mã nguồn phụ trợ phải được tách riêng vào các thư mục tĩnh (`static`) và **BẮT BUỘC phải được chia nhỏ theo từng module chuyên biệt**.
Ví dụ: Các file của module Module 1: Authentication & Sensitive Health Profile phải nằm trong đúng đường dẫn module của nó như `static/js/auth/` hoặc `static/css/auth/`. Từ đó mới được nhúng (link) vào file HTML tương ứng.
