# SOFTWARE REQUIREMENT SPECIFICATION
**Aura Moon (HOS-03)**
– Hanoi, Jan 2024 –

---

# Table of Contents
I. Record of Changes
II. Software Requirement Specification
1. Overall Requirements
    1.1 Context Diagram
    1.2 Main Business Processes
    1.3 User Requirements
    1.4 System Functionalities
    1.5 Entity Relationship Diagram
2. Use Case Specifications
3. Functional Requirements
4. Non-Functional Requirements
5. Requirement Appendix

---

# I. Record of Changes
| Date | A* M, D | In charge | Change Description |
|---|---|---|---|
| | | | |

*\*A - Added M - Modified D - Deleted*

---

# II. Software Requirement Specification

## 1. Overall Requirements

### 1.1 Context Diagram - My
The Xoai Aura Retreat Management System is a wellness resort management platform designed to support retreat package booking, villa accommodation management, spa scheduling, dietary planning, billing, and business reporting.

The context diagram below illustrates the system boundary and its interactions with external entities, including Guests, Receptionists, Spa Therapists/Yoga Instructors, F&B Staff, Administrators, and Managers. The system also integrates with external services such as Payment Gateway APIs, Authentication APIs, and Notification & Calendar APIs.

`![Context Diagram](images/context_diagram.png)`

### 1.2 Main Business Processes - Hải
`![Main Business Processes](images/main_business_processes.png)`

| Step # | Step Name | Detailed Description | Role | Note |
|---|---|---|---|---|
| 1 | Publish driving course enrollment | 1. Activity: Guest creates an account and fills out the "Health & Dietary Profile" (medical conditions, allergies). System checks the UI to ensure NO consent checkboxes are pre-checked. <br> 2. Input: Email, Password, Sensitive medical/allergy data. <br> 3. Output: User account, Health profile (encrypted in DB). | Guest, System | Crucial: Must strictly comply with Decree 356/2025 regarding sensitive data. |
| 2 | Retreat Package Booking & Deposit | 1. Activity: Guest filters and selects a Retreat Package by goal, chooses dates and Villa type. Redirects to Payment Gateway to pay deposit. <br> 2. Input: Retreat_Package_ID, Arrival/Departure Dates, Villa Type, Card details. <br> 3. Output: Booking Record, Successful deposit payment status. | Guest, System | When clicking the "register" button, user authentication is required |
| 3 | Check-in & Room Assignment | 1. Activity: Receptionist views the expected guest list, assigns a physical Villa number. Collects guest's ID/CCCD info for temporary residence declaration. System encrypts ID. <br> 2. Input: Booking ID, ID/Passport details, Physical Villa number. <br> 3. Output: Villa Status: "Occupied", ID data securely stored. | Receptionist, System | Complies with Residence Law 2020. Receptionist MUST NOT view the guest's medical data. |
| 4 | Spa Scheduling | 1. Activity: Guest selects date/time for a Spa session. System runs DB locking transaction: Finds ONE available therapist AND ONE available room simultaneously. Confirms and calls Google Calendar API. <br> 2. Input: Booking ID, Spa_Service_ID, Desired time slot. <br> 3. Output: Confirmed Spa schedule, Reminder email. | Guest, System | Complex Logic: 2-Dimensional Double-Booking prevention constraint |
| 5 | Spa Treatment Execution | 1. Activity: Therapist views their work schedule and reads physical health notes (other data hidden). Performs treatment and updates status. <br> 2. Input: Spa Schedule, Physical therapy notes. <br> 3. Output: Status: "Completed/No-Show", Updated Guest Folio. | Spa Therapist, System | RBAC: Therapist MUST NOT view the guest's dietary/allergy information. |
| 6 | F&B Meal Ordering | 1. Activity: System cross-references the "Dietary Profile" to filter out menu items containing allergens. Guest selects daily meals from safe menu. <br> 2. Input: Original Menu, Guest's allergy data. <br> 3. Output: Safe Menu, F&B Order. | Guest, System | Applies the Data Minimization principle |
| 7 | Culinary Preparation | 1. Activity: Chef opens Dashboard to view aggregated meal orders and "allergy alerts". Prepares dish and updates status. <br> 2. Input: F&B Order, Food allergy alerts. <br> 3. Output: Updated meal status, A-la-carte charges billed to Folio. | Chef / F&B, System | RBAC: Chef MUST NOT view the guest's physical medical records |
| 8 | Check-out & Consolidated Bill | 1. Activity: Receptionist clicks Check-out. System aggregates Remaining Package fee + Extra services into 1 Consolidated Bill. Collects payment. <br> 2. Input: Guest Folio (Room + Spa + F&B). <br> 3. Output: Final Bill, Villa Status: "Needs Cleaning". | Receptionist, System | Constraint: Guest CANNOT check out if there are pending orders. |
| 9 | Review & Data Deletion | 1. Activity: Guest submits Retreat quality review. Guest exercises "Right to be forgotten". System permanently wipes medical records. <br> 2. Input: Rating/Review, Data deletion request. <br> 3. Output: System review record, User profile "cleansed" | Guest, System | Ensures absolute privacy after the retreat concludes. |

### 1.3 User Requirements - Đắc
#### 1.3.1 Actors
| # | Actor | Description |
|---|---|---|
| 1 | Guest / Customer | A customer who uses the system to register, log in, complete health profiles, browse wellness packages, book villas, schedule spa/treatment sessions, pre-select meals, view itinerary, make payments, and submit reviews. |
| 2 | Receptionist | A front-desk staff member who manages guest check-in/check-out, assigns physical villas, manages villa status, books additional spa services, and processes consolidated invoices. |
| 3 | Spa Therapist / Yoga Trainer | A service provider who views daily treatment schedules, checks treatment-relevant health notes, and updates treatment session status. |
| 4 | Chef / F&B Staff | A food and beverage staff member who views daily meal preparation dashboards, checks food allergy alerts, prepares personalized meals, and updates meal order status. |
| 5 | Administrator | A system administrator who manages staff accounts, assigns user roles, configures role-based access control, and manages master data. |
| 6 | Resort Manager | A management user who monitors revenue dashboards, reviews business performance, and exports monthly reports on room occupancy and therapist utilization. |
| 7 | Payment Gateway | External payment service (Stripe, VNPay, PayPal). |
| 8 | Calendar / Notification Service | External service (Google Calendar, SendGrid). |
| 9 | SSO Provider | External authentication provider (Google Identity, Facebook Login). |

#### 1.3.2 Use Cases (UC)
| ID | Use Case | Feature | Use Case Description |
|---|---|---|---|
| 01 | Register, Verify Email and Log In | Authentication | Guest creates account, verifies email, logs in securely. |
| 02 | Complete Health and Dietary Profile | Health Profile Mgt | Guest provides health conditions, allergies, dietary prefs. |
| 03 | Manage Staff Accounts and Assign Roles | User and Role Mgt | Administrator creates staff accounts and assigns roles. |
| 04 | Manage Master Data | Master Data Mgt | Administrator manages villa types, spa services, packages. |
| 05 | Delete Sensitive Health Data | Data Privacy Mgt | Guest requests permanent deletion of sensitive data. |
| 06 | Browse Wellness Packages | Package Browsing | Guest browses packages and filters them by goals. |
| 07 | Book Wellness Package and Pay Deposit | Booking & Payment | Guest selects package, dates, villa type, pays deposit. |
| 08 | Check In Guest | Reception Mgt | Receptionist checks in guests, assigns villa, collects ID. |
| 09 | Manage Villa Status | Villa Mgt | Receptionist updates villa status. |
| 10 | View Booking Details and Itinerary | Booking Tracking | Guest views booking details, spa schedule, meal plan. |
| 11 | Schedule Spa/Treatment Session | Spa Scheduling | Guest schedules included spa/therapy sessions. |
| 12 | Find Available Therapist & Room | Automatic Scheduling | System automatically checks availability before confirming. |
| 13 | View Daily Work Schedule | Therapist Mgt | Spa Therapist views assigned daily sessions and notes. |
| 14 | Update Treatment Session Status | Treatment Mgt | Spa Therapist marks session Completed/No-Show. |
| 15 | Book Additional Spa Service | Add-on Service | Receptionist manually books extra spa services. |
| 16 | Pre-select Daily Meals | Meal Mgt | Guest selects daily meals from personalized filtered menu. |
| 17 | View Daily Meal Dashboard | F&B Dashboard | Chef views aggregated meal orders and allergy alerts. |
| 18 | Update Meal Order Status | Meal Order Mgt | Chef updates meal status (Preparing -> Ready). |
| 19 | Order A-la-carte Food & Beverage | Add-on Service | Guest orders extra food/drinks outside package. |
| 20 | Enforce Data Minimization for F&B | Data Privacy | System restricts kitchen from viewing medical history. |
| 21 | Generate Consolidated Invoice | Billing Mgt | Receptionist generates consolidated invoice. |
| 22 | Process Final Payment & Check-out | Checkout & Payment | Receptionist processes final payment and updates villa. |
| 23 | Submit Post-stay Review & Rating | Feedback Mgt | Guest submits review and rating after stay. |
| 24 | View Revenue Analytics Dashboard | Analytics & Report | Manager views revenue charts by package, spa, F&B. |
| 25 | Export Monthly Report | Report Export | Manager exports occupancy and utilization reports. |

#### 1.3.2.1 UCs Diagrams
`![UCs for Guest](images/uc_guest.png)`
`![UCs for User](images/uc_user.png)`
`![UCs for Receptionist](images/uc_receptionist.png)`
`![UCs for Spa Therapist](images/uc_therapist.png)`
`![UCs for F&B Staff](images/uc_fb_staff.png)`
`![UCs for Admin / Manager](images/uc_admin_manager.png)`

### 1.4 System Functionalities - Dương
#### 1.4.1 Screens Flow
`![User Screen Flow](images/flow_user.png)`
`![Spa Therapist Screen Flow](images/flow_therapist.png)`
`![F&B Screen Flow](images/flow_fb.png)`
`![Receptionist Screen Flow](images/flow_receptionist.png)`
`![Admin Screen Flow](images/flow_admin.png)`
`![All System Screen Flow](images/flow_all_system.png)`

#### 1.4.2 Screen Authorization
| Screen | Guest | Receptionist | Therapist | F&B / Chef | Admin |
|---|---|---|---|---|---|
| Home Page | X | X | X | X | X |
| About Us | X | X | X | X | X |
| Contact Us | X | X | X | X | X |
| Packages List | X | X | X | X | X |
| Package Detail | X | X | X | X | X |
| Login Screen | X | X | X | X | X |
| Register Screen | X | | | | |
| Book Now [Action] | X | | | | |
| Health & Dietary Profile | X | | | | |
| Payment | X | | | | |
| Success Page | X | | | | |
| Guest Dashboard | X | | | | |
| Itinerary Timeline | X | | | | |
| Spa Scheduling | X | | | | |
| A la-carte Menu | X | | | | |
| Dietary Menu | X | | | | |
| My Profile | X | | | | |
| Update/Delete Profile | X | | | | |
| Review & Rating | X | | | | |
| Receptionist Dashboard | | X | | | |
| Expected Arrival List | | X | | | |
| Check-in Form | | X | | | |
| Villa Status Management | | X | | | |
| Update Villa Status [Action] | | X | | | |
| Manual Spa Booking [Modal] | | X | | | |
| Checkout Management | | X | | | |
| Invoice Detail | | X | | | |
| Process Payment | | X | | | |
| Therapist Dashboard | | | X | | |
| Daily Schedule | | | X | | |
| Session Detail | | | X | | |
| View Health Notes [Modal] | | | X | | |
| Update Session Status [Action] | | | X | | |
| History | | | X | | |
| F&B / Chef Dashboard | | | | X | |
| Daily Meal Prep Board | | | | X | |
| A-la-carte Orders Board | | | | X | |
| Update Prep Status [Action] | | | | X | |
| Admin Dashboard | | | | | X |
| Staff Accounts Management | | | | | X |
| Assign Roles [Modal] | | | | | X |
| Master Data Management | | | | | X |
| Villas Management | | | | | X |
| Packages Management | | | | | X |
| Spa Services Management | | | | | X |
| Revenue Analytics | | | | | X |
| Package Revenue Chart | | | | | X |
| Spa Revenue Chart | | | | | X |
| F&B Revenue Chart | | | | | X |
| Export Center | | | | | X |
| Daily ID Report | | | | | X |
| Excel Reports | | | | | X |

#### 1.4.3 Non-UI Functions
| # | Feature | System Function | Description |
|---|---|---|---|
| 1 | Authentication | Google OAuth API | Allows users to sign in using Google accounts. |
| 2 | Payment | VNPay Payment Gateway API | Processes online payments through VNPay. |
| 3 | Payment | Payment Verification Service | Verifies transaction status and updates booking records. |
| 4 | Data Management| Database Backup Service | Performs scheduled database backups. |

### 1.5 Entity Relationship Diagram - Ngọc
`![ERD Diagram](images/erd_diagram.png)`

---

## 2. Use Case Specifications - Đắc
*(See section 1.3.2 for the complete list of 25 UCs. The detailed flow specifications for core workflows like Booking, Scheduling, Checkout, and Dashboards apply the business rules below).*

---

## 3. Functional Requirements - Hải
### 3.1 Core Feature

#### 3.1.1 Health & Dietary Form Screen
- **Content #1**: The screen is divided into two main sections: "Diet & Allergies" on the left and "Physical Health Status" on the right. At the bottom, there is a consent confirmation area and action buttons.
- **Content #2**: Allows the Guest to input their personal health and dietary profile. This sensitive information is strictly confidential and will be segregated by the system: Chefs will only have access to dietary restrictions, while Spa Therapists will only view physical conditions.
- **Mapped UC**: UC02

**Content #3 Fields:**
- `Field Group: Dietary & Allergies`
  - **(1) Food Allergies**: Checkboxes (e.g., Peanuts, Shellfish). Optional.
  - **(2) Other Allergies**: Input (String, max 255). Optional.
  - **(3) Diet Type**: Buttons (Vegan, Vegetarian, Keto). Required field for automatic menu filtering.
  - **(4) Additional Notes**: Text area (String, max 500).
- `Field Group: Physical Health Status`
  - **(5) Current Medical Conditions**: Text area (String, max 500). e.g., High blood pressure.
  - **(6) Current Medications**: Text area (String, max 500).
  - **(7) Recent Injuries or Issues**: Text area (String, max 500). Visible to Spa Therapists.
- `Field Group: Confirmation & Actions`
  - **(8) Consent**: Checkbox. Strict Constraint: Initial value MUST be Unchecked (Decree 356/2025). Save button disabled if not checked.
  - **(9) Save & Continue Button**: Triggers data submission. Backend must encrypt sensitive fields.
  - **(10) Review Itinerary Button**: Cancels current input.

#### 3.1.2 Data Erasure Request Screen
- **Content #1**: Warning Modal/Dialog with red text alerts indicating data cannot be recovered.
- **Content #2**: Enables Guest to exercise "Right to Erasure". Requires re-authentication before permanent deletion.
- **Mapped UC**: UC05

**Content #3 Fields:**
- `Field Group: Verification`
  - **(1) Password Input**: Required, masked string.
  - **(2) Toggle Visibility**: Toggles masked/visible state.
- `Field Group: Actions`
  - **(3) Hủy (Cancel) Button**: Closes dialog.
  - **(4) Xóa vĩnh viễn (Permanent Delete) Button**: Validates Password -> Executes hard delete query, terminates session.

#### 3.1.3 Retreat Package List Screen
- **Content #1**: Horizontal search and multi-criteria filter bar (Duration, Goal, Price). Grid layout of package cards.
- **Content #2**: Explores Retreat Packages using filters.
- **Mapped UC**: UC06

#### 3.1.4 Package Detail & Checkout Screen
- **Content #1**: Two columns: Left (Package details, images, itinerary), Right (Persistent booking form, date picker, Villa type dropdown, guest info, cost summary).
- **Content #2**: Finalize dates, select accommodation, pay deposit.
- **Mapped UC**: UC07

**Content #3 Fields:**
- `Field Group: Booking Form`
  - **(1) Check-in/Check-out Dates**: LocalDate. Past dates disabled.
  - **(2) Loại Villa (Villa Type)**: Dropdown. Dynamically filters types that have actual availability.
  - **(3) Guest Info**: String, auto-populated if authenticated.
- `Field Group: Payment & Checkout`
  - **(4) Price Summary**: Read-only calculation block.
  - **(5) Thanh toán (Pay Deposit) Button**: Triggers external Payment Gateway (Stripe/VNPay). On success: generates Room_Booking_ID.

#### 3.1.5 Arrivals & Check-in Dashboard Screen
- **Content #1**: Left navigation sidebar, primary main content area (Date header, Data Table of expected arrivals, summary cards).
- **Content #2**: Operational dashboard for Receptionists to assign physical room numbers and process check-in.
- **Mapped UC**: UC08

**Content #3 Fields:**
- `Field Group: Dashboard Overview`
  - **(1) Sidebar Navigation**: Links for front-desk.
  - **(6) Summary Cards**: Read-only numeric counts of pending/completed check-ins.
- `Field Group: Arrivals Data Table`
  - **(2) Guest & Package Info**: Name, Booking ID, Package, Villa Category. Strict RBAC: Must mask Health/Allergy data.
  - **(3) Số phòng (Room Assignment)**: Dropdown. Dynamically populated with physical room numbers matching Villa Category AND Vacant status.
  - **(4) Trạng thái (Status Badge)**: Indicator (e.g., Not Arrived).
  - **(5) Check-in Action Button**: Updates Booking status to Checked-in, Villa status to Occupied.

#### 3.1.6 Guest Itinerary Timeline Screen
- **Content #1**: Vertical Timeline layout with date navigation.
- **Content #2**: Visual overview of scheduled daily activities (F&B, Yoga, Spa).
- **Mapped UC**: UC10

**Content #3 Fields:**
- `Field Group: Date Navigation`
  - **(1) Date Selector**: LocalDate.
- `Field Group: Timeline Events`
  - **(2) Time Milestone**: LocalTime (e.g., 07:00).
  - **(3) Activity Card**: Container aggregating Spa and F&B data.
  - **(4) Activity Title & Icon**: Title (e.g., "Ăn sáng Detox") and icon.
  - **(5) Activity Description**: Brief text.
  - **(6) Location**: Physical venue (e.g., "Nhà hàng Thực dưỡng").

#### 3.1.7 Guest Spa Scheduler Screen
- **Content #1**: Left column (service details, calendar), Right column (time slots grid, Booking Summary block).
- **Content #2**: Schedule Spa therapies. System dynamically renders valid time slots based on resource availability to prevent overbooking.
- **Mapped UC**: UC11, UC12

**Content #3 Fields:**
- `Field Group: Scheduling Inputs`
  - **(1) Service Info**: Read-only.
  - **(2) Date Picker**: LocalDate. Past dates disabled.
  - **(3) Time Slot Grid**: LocalTime grid. Strict Business Rule: Clickable ONLY if Treatment Room > 0 AND Therapist > 0.
- `Field Group: Confirmation`
  - **(4) Booking Summary**: Read-only block. Room field is automatically allocated by the system.
  - **(5) Xác nhận đặt lịch Button**: Execution must be encapsulated in a Database Transaction locking Therapist and Room.

#### 3.1.8 Therapist Daily Schedule Screen
- **Content #1**: Header with Date, KPI cards, chronological Data Table of assigned sessions.
- **Content #2**: Manage daily shifts, view upcoming appointments, securely access physical health notes, update real-time statuses.
- **Mapped UC**: UC13, UC14

**Content #3 Fields:**
- `Field Group: Dashboard Summary`
  - **(1) Date Header**: LocalDate.
  - **(2) Summary Cards**: Dynamic metrics (Total sessions, Completed).
- `Field Group: Schedule Data Table`
  - **(3) Session Details**: Time Range, Guest Name, Assigned Room.
  - **(4) Xem Ghi Chú Button**: Triggers Modal displaying guest notes. Strict RBAC: MUST implement Data Minimization, masking dietary allergy information.
  - **(5) Trạng thái Dropdown**: Enum (Pending, Completed, No-Show). Commits state change. Completing triggers billing to Guest Folio.

#### 3.1.9 Manual Spa Booking Modal
- **Content #1**: Overlay dialog (Modal) with input fields and summary block.
- **Content #2**: Allows Receptionists to manually book additional Spa therapies, routed to the guest's centralized room account.
- **Mapped UC**: UC15

**Content #3 Fields:**
- `Field Group: Guest & Service Selection`
  - **(1) Guest/Room Search Input**: Autocomplete (only "Checked-in" guests).
  - **(2) Spa Service**: Dropdown from Master Data.
- `Field Group: Scheduling`
  - **(3) Date**: LocalDate.
  - **(4) Available Time Slots**: Validation algorithm applies.
- `Field Group: Folio Integration`
  - **(5) Price Summary**: Base Price + Tax + Total.
  - **(6) Xác nhận & Ghi nợ vào Folio Button**: Dual-operation transaction: 1. Insert Spa appointment. 2. Post charge to Guest Folio using Room_Booking_ID.

#### 3.1.10 Personalized Menu & A-la-carte Screen
- **Content #1**: Catalog area (tabs) and Cart/Summary block.
- **Content #2**: Smart dining experience. Cross-references ingredients against health profile to prevent ordering allergens. Orders premium items.
- **Mapped UC**: UC16, UC19

**Content #3 Fields:**
- `Field Group: Menu Navigation`
  - **(1) Category Tabs**: "Thực đơn của bạn" (Package) and "Gọi món ngoài" (A-la-carte).
- `Field Group: Food Items`
  - **(2) Standard Food Card**: Image, Dish Name, Macros, Add Button.
  - **(3) Allergy Warning Card**: Strict Logic Constraint: Intersection check between ingredients and Food_Allergies. Renders alert overlay ("Cảnh báo") and disables selection button.
- `Field Group: Cart & Checkout`
  - **(4) Cart Summary**: Package items = 0 VND. A-la-carte = monetary values.
  - **(5) Xác nhận đặt bàn Button**: Submits order. Posts financial charges to Folio for A-la-carte items.

#### 3.1.11 Chef Dashboard (Kitchen Display System)
- **Content #1**: Kanban board layout (Pending, In Progress, Completed). Order tickets displaying timers, items, allergy alerts.
- **Content #2**: Kitchen display system. Adheres to safety constraints without violating medical privacy.
- **Mapped UC**: UC17, UC18, UC20

**Content #3 Fields:**
- `Field Group: Kanban Columns`
  - **(1) Order Status Columns**: Pending, In Progress, Completed.
- `Field Group: Order Ticket Details`
  - **(2) Allergy Alert Label**: Strict RBAC: Payload restricted to "Dietary Allergies" only. Physical records masked.
  - **(3) Order Meta Info**: Room Number, Guest Name, wait Timer.
  - **(4) Itemized List**: Dishes, quantities, modifications.
  - **(5) State Mutation Buttons**: "Bắt đầu", "Hoàn thành". Updates Kanban column.

#### 3.1.12 Consolidated Billing & Check-out Screen
- **Content #1**: 7:3 two-column structure. Granular breakdown of departmental charges (Room, Spa, F&B) on left, grand total and check-out button on right.
- **Content #2**: Digitizes night audit. Aggregates all pending financial liabilities from POS terminals into a single invoice.
- **Mapped UC**: UC21, UC22

**Content #3 Fields:**
- `Field Group: Consolidated Folio Details`
  - **(1), (2), (3) Departmental Charge Blocks**: Read-only grids. Backend aggregates across modules using Room_Booking_ID.
- `Field Group: Payment Processing`
  - **(4) Payment Summary**: Algebraic sum of balances and fees.
  - **(5) Payment Method**: Toggle/Radio selection.
  - **(6) Thanh toán & Check-out Button**: Strict Constraint: Validates all sub-orders. CANNOT check out if pending/unprocessed Spa/F&B orders exist. Changes status to Vacant/Needs Cleaning.

#### 3.1.13 Manager Revenue Dashboard Screen
- **Content #1**: Standard dashboard structure. Filters, two large visualization widgets (Donut, Line/Bar chart), and a bottom data grid.
- **Content #2**: Real-time business intelligence metrics from completed Folios.
- **Mapped UC**: UC24

**Content #3 Fields:**
- `Field Group: Dashboard Filters`
  - **(1) Filters**: Dropdowns (Time period, Category). Re-renders charts.
- `Field Group: Data Visualizations`
  - **(2) Revenue Breakdown**: Donut / Pie Chart (Packages, Spa, F&B).
  - **(3) Revenue Trend**: Line / Bar Chart (Fluctuation over time).
- `Field Group: Transaction Data`
  - **(4) Recent Transactions Table**: Paginated list.
  - **(5) Tải báo cáo CSV Button**: Exports dataset to .csv/.xlsx.

#### 3.2 User Authentication
##### 3.2.1 Authentication & Login Screen
- **(1) EMAIL**: Required string.
- **(2) MẬT KHẨU**: Masked string.
- **(3) Quên mật khẩu? Link**: Navigates to recovery.
- **(4) Đăng nhập Button**: Submits payload. BCrypt verification. Provisions JWT.
- **(5) Google SSO Button**: OAuth2 flow. Bypasses manual password.
- **(6) Đăng ký ngay Link**: Navigates to registration.

##### 3.2.2 New Account Registration Screen
- **(1) Header**: "Đăng ký".
- **(2) HỌ VÀ TÊN**: Required string.
- **(3) EMAIL**: Required string. Regex validation. Unique constraint.
- **(4) MẬT KHẨU**: Masked string with toggle visibility.
- **(5) XÁC NHẬN MẬT KHẨU**: Must match password field exactly.
- **(6) ĐĂNG KÝ Button**: One-way BCrypt hashing before INSERT.

##### 3.2.3 Password Recovery Screens
- **Phase 1 (Request Reset Link)**:
  - **(1) Header**: "Khôi phục mật khẩu".
  - **(2) Email Input**: Required string.
  - **(3) Gửi yêu cầu Button**: Verifies email, generates Reset_Token (15 min expiry), dispatches email.
- **Phase 2 (Set New Password)**:
  - **(6) Mật khẩu mới**: Masked string.
  - **(7) Xác nhận mật khẩu**: Exact match validation.
  - **(8) Cập nhật Password Button**: Submits new password + Token. Updates DB, revokes token.

#### 3.3 Master Data
##### 3.3.1 Villa Status Management Screen
- **(1) KPI Status Summary**: COUNT queries for Clean, Dirty, Maintenance.
- **(2) Filters**: Dropdowns by Floor, Category.
- **(3) Villa Identification**: Primary key identifier.
- **(4) Housekeeping Status Dropdown**: Transitions state (e.g. Dirty -> Clean).
- **(5) Occupancy Info & Alerts**: Cross-module relational queries (Occupied, Arrival Expected, Vacant, Maintenance).

##### 3.3.2 Performance Reports & Export Screen
- **(1) Xuất PDF Button**: Generates branded document utilizing iTextPDF.
- **(2) Tạo báo cáo mới Button**: Dispatches GET request to trigger recalculation.
- **(3) KPI Summary Cards**: Revenue, Occupancy Rate, Spa Utilization. Trend Analysis (MoM).
- **(4) Data Preview Grid**: Granular record details. Color-coded status badging.
- **(5) Pagination Controls**: Server-side pagination (SQL Limit/Offset).

---

## 4. Non-Functional Requirements - Đắc

### 4.1 External Interfaces
| ID | Interface | Requirements |
|---|---|---|
| EI-01 | User Interface | Responsive web-based interfaces for desktop and tablet. |
| EI-03 | Consent Interface | Sensitive screens shall display consent notices. |
| EI-07 | Payment Gateway | Support Stripe/VNPay/PayPal. |
| EI-08 | Notification Service | Support SendGrid/Google Calendar. |

### 4.2 Quality Attributes

#### 4.2.1 Usability
| ID | Requirement | Measurement |
|---|---|---|
| US-01 | Guest registration simple and intuitive | ≤ 3 minutes |
| US-02 | Retreat package booking completed efficiently | ≤ 5 minutes |
| US-03 | Receptionist completes check-in quickly | ≤ 3 minutes |

#### 4.2.2 Performance
| ID | Requirement | Target |
|---|---|---|
| PF-01 | Average response time | ≤ 2 seconds |
| PF-03 | Booking confirmation (UC07) | ≤ 10 seconds |
| PF-06 | Concurrent active users | 100 users |
| PF-09 | Guest Accounts capacity | 100,000 |
| PF-15 | Database Query | ≤ 1 second |
| PF-17 | Monthly System Uptime | ≥ 99.5% |

---

## 5. Requirement Appendix - My

### 5.1 Business Rules
| ID | Business Rule Name | Detailed Description |
|---|---|---|
| BR-01 | Booking Confirmation | Confirmed only after successful deposit payment. |
| BR-02 | Villa Allocation | Guest selects Type. Physical Villa assigned at Check-in. |
| BR-04 | Dual Resource Spa | Requires both available Therapist and Room simultaneously. |
| BR-06 | Auto Menu Filtering | System auto-excludes allergens from guest's menu. |
| BR-07 | RBAC & Data Min. | Therapists see physical health. Chefs see allergies only. |
| BR-08 | Explicit Consent | Consent checkboxes unchecked by default. |
| BR-09 | Data Encryption | Sensitive health/allergy data must be encrypted in DB. |
| BR-10 | Right to Erasure | Guest can permanently delete health info post-stay. |
| BR-11 | Guest Folio | All Spa/F&B charges posted to central Guest Folio. |
| BR-12 | Check-out Constraints| Cannot check out with unpaid Spa/F&B charges. |
| BR-15 | Audit Trail | System logs login, health data access, booking, payment. |

### 5.2 System Messages
| Code | Type | Message |
|---|---|---|
| MSG-01 | Success | Registration successful. Please verify your email address. |
| MSG-02 | Error | Invalid email or password. |
| MSG-04 | Warning | You must provide consent before submitting health information. |
| MSG-08 | Error | No available Villa could be found for allocation. |
| MSG-10 | Error | No available Therapist or Therapy Room could be found. |
| MSG-12 | Warning | Warning: This meal contains ingredients listed in allergy profile. |
| MSG-13 | Warning | Please settle all outstanding charges before checking out. |
