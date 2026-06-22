# QUY TẮC CHUNG LÀM VIỆC NHÓM VỚI AI (AI TEAM RULES)
## Dự án: Xoai Aura Retreat & Spa Management System

---

## 1. Nguyên Tắc Cốt Lõi
*   **AI là trợ lý, Con người là kiểm duyệt:** AI có trách nhiệm sinh code, viết tài liệu và kiểm thử nhanh chóng. Tuy nhiên, lập trình viên (con người) chịu trách nhiệm tối cao về chất lượng code, độ an toàn bảo mật và tính đúng đắn trước khi commit/merge.
*   **Không đoán nghiệp vụ:** AI không được tự ý phỏng đoán hoặc suy diễn nghiệp vụ. Nếu tài liệu bị thiếu hoặc mập mờ, AI phải đặt câu hỏi làm rõ với người dùng.

---

## 2. Quy Trình Làm Việc 4 Bước Tuần Tự (Bắt Buộc)

Mọi tính năng/sửa đổi mã nguồn do AI thực hiện phải tuân thủ nghiêm ngặt quy trình sau:

```
[Tài liệu đặc tả]
       │
       ▼
  BƯỚC 1: AI Đọc tài liệu
  (Xác nhận hiểu và tóm tắt nghiệp vụ, thực thể, quy tắc nghiệp vụ)
       │
       ▼
  BƯỚC 2: AI Đặc tả Thiết kế & Kiểm thử
  (Tạo file EDS mô tả kiến trúc/API và file TDD mô tả test cases trước khi code)
       │
       ▼
  BƯỚC 3: AI Sinh Code Production & Unit Test
  (Viết Repository, Service, Controller và Mockito Unit Tests tương ứng)
       │
       ▼
  BƯỚC 4: Chạy Kiểm Thử & Báo Cáo
  (Chạy thử nghiệm tự động, sửa lỗi cho đến khi pass 100%, làm báo cáo tổng hợp)
```

---

## 3. Thứ Tự Đọc Tài Liệu Bắt Buộc Của AI
Khi bắt đầu một phiên làm việc mới, AI phải đọc các tài liệu theo thứ tự sau để đồng bộ ngữ cảnh:
1.  `Quy_tac_AI_Test/AI_TEAM_RULES.md` — Tài liệu này (Quy tắc chung).
2.  `Quy_tac_AI_Test/AI_RULES.md` — Quy tắc viết code kỹ thuật.
3.  `Quy_tac_AI_Test/TESTING_GUIDELINES.md` — Quy chuẩn viết và chạy test.
4.  `README.md` — Tổng quan về dự án.
5.  `02_Requirement/Module_X.md` — Đề bài và đặc tả nghiệp vụ của module đang làm việc.
6.  Database Schema hoặc các Entity liên quan.

---

## 4. Quy Tắc Quản Lý Mã Nguồn (Git)
*   **Làm việc trên nhánh phát triển:** Toàn bộ công việc thực hiện trên nhánh `SourceCode` (hoặc nhánh tính năng được chỉ định), cấm commit trực tiếp lên `main`.
*   **Tránh Side-Effects:** Chỉ sửa đổi mã nguồn nằm trong phạm vi Module được phân công. Không sửa đổi các file dùng chung hoặc file của module khác khi chưa có sự đồng ý của Tech Lead.
*   **Immutable History (Changelog):** Mọi tài liệu thiết kế (.md) phải có phần Changelog để lưu vết chỉnh sửa, tuyệt đối không xóa thông tin lịch sử cũ.
