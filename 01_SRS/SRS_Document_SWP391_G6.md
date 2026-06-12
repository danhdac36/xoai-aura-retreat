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

These interactions enable the efficient management of retreat operations, guest services, payment processing, and business analytics.

`![Context Diagram](images/context_diagram.png)`

### 1.2 Main Business Processes - Hải
`![Main Business Processes](images/main_business_processes.png)`

| Step # | Step Name | Detailed Description | Role | Note |
|---|---|---|---|---|
| 1 | Publish driving course enrollment | 1. Activity:<br>- Guest creates an account and fills out the "Health & Dietary Profile" (medical conditions, allergies).<br>- System checks the UI to ensure NO consent checkboxes are pre-checked.<br>2. Input: Email, Password, Sensitive medical/allergy data.<br>3. Output: User account, Health profile (encrypted in DB). | Guest, System | Crucial: Must strictly comply with Decree 356/2025 regarding sensitive data. |
| 2 | Retreat Package Booking & Deposit | 1. Activity:<br>- Guest filters and selects a Retreat Package by goal, chooses dates and Villa type.<br>- Redirects to Payment Gateway (Stripe/VNPay) to pay the deposit.<br>2. Input: Retreat_Package_ID, Arrival/Departure Dates, Villa Type, Card details.<br>3. Output: Booking Record, Successful deposit payment status. | Guest, System | When clicking the "register for course" button, user authentication is required |
| 3 | Check-in & Room Assignment | 1. Activity:<br>- Receptionist views the expected guest list, assigns a physical Villa number.<br>- Collects guest's ID/CCCD info for temporary residence declaration.<br>- System encrypts the ID and changes room status.<br>2. Input: Booking ID, ID/Passport details, Physical Villa number.<br>3. Output: Villa Status: "Occupied", ID data securely stored. | Receptionist, System | Complies with Residence Law 2020. Receptionist MUST NOT view the guest's medical data. |
| 4 | Spa Scheduling | 1. Activity:<br>- Guest selects date/time for a Spa session.<br>- System runs a DB locking transaction: Finds ONE available therapist AND ONE available room simultaneously. If available, confirms the schedule and calls Google Calendar API to notify the guest.<br>2. Input: Booking ID, Spa_Service_ID, Desired time slot.<br>3. Output: Confirmed Spa schedule, Reminder email (sent 1h prior). | Guest, System | Complex Logic: 2-Dimensional Double-Booking prevention constraint |
| 5 | Spa Treatment Execution | 1. Activity:<br>- Therapist views their work schedule and reads physical health notes (other data is hidden).<br>- Performs treatment and updates the session status.<br>- System automatically accumulates charges (if the guest uses extra off-package services) into the Guest Folio.<br>2. Input: Spa Schedule, Physical therapy notes.<br>3. Output: Status: "Completed/No-Show", Updated Guest Folio. | Spa Therapist, System | RBAC: Therapist MUST NOT view the guest's dietary/allergy information. |
| 6 | F&B Meal Ordering | 1. Activity:<br>- System cross-references the "Dietary Profile" to filter out menu items containing allergens.<br>- Guest selects daily meals from this pre-filtered safe menu.<br>2. Input: Original Menu, Guest's allergy data.<br>3. Output: Safe Menu, F&B Order. | Guest, System | Applies the Data Minimization principle |
| 7 | Culinary Preparation | 1. Activity:<br>- Chef opens the Dashboard to view aggregated meal orders and "allergy alerts".<br>- Chef prepares the dish and updates the status (Preparing -> Ready to deliver).<br>2. Input: F&B Order, Food allergy alerts.<br>3. Output: Updated meal status, A-la-carte charges billed to Guest Folio. | Chef / F&B, System | RBAC: Chef MUST NOT view the guest's physical medical records |
| 8 | Check-out & Consolidated Bill | 1. Activity:<br>- Receptionist clicks Check-out. System scans to check if there are any pending/unpaid Spa/F&B orders.<br>- If clear, the system aggregates: Remaining Package fee + Extra services into 1 Consolidated Bill.<br>- Receptionist collects payment and releases the room.<br>2. Input: Guest Folio (Room + Spa + F&B).<br>3. Output: Final Bill, Villa Status: "Needs Cleaning". | Receptionist, System | Constraint: Guest CANNOT check out if there are pending orders. |
| 9 | Review & Data Deletion | 1. Activity:<br>- Guest submits a Retreat quality review form.<br>- Guest exercises their "Right to be forgotten". System executes a command to permanently wipe medical and allergy records from the DB.<br>2. Input: Rating/Review, Data deletion request.<br>3. Output: System review record, User profile "cleansed" of sensitive data | Guest, System | Ensures absolute privacy after the retreat concludes. |

### 1.3 User Requirements - Đắc
#### 1.3.1 Actors
| # | Actor | Description |
|---|---|---|
| 1 | Guest / Customer | A customer who uses the system to register, log in, complete health and dietary profiles, browse wellness packages, book villas, schedule spa/treatment sessions, pre-select meals, view itinerary, make payments, and submit reviews. |
| 2 | Receptionist | A front-desk staff member who manages guest check-in/check-out, assigns physical villas, manages villa status, books additional spa services for guests, and processes consolidated invoices and final payments. |
| 3 | Spa Therapist / Yoga Trainer | A service provider who views daily treatment schedules, checks treatment-relevant health notes, and updates treatment session status such as Completed or No-Show. |
| 4 | Chef / F&B Staff | A food and beverage staff member who views daily meal preparation dashboards, checks food allergy alerts, prepares personalized meals, and updates meal order status. |
| 5 | Administrator | A system administrator who manages staff accounts, assigns user roles, configures role-based access control, and manages master data such as villa types, spa services, retreat packages, and staff records. |
| 6 | Resort Manager | A management user who monitors revenue dashboards, reviews business performance, and exports monthly reports on room occupancy and therapist utilization. |
| 7 | Payment Gateway | An external payment service such as Stripe, VNPay, or PayPal that processes deposit payments and final payments securely. |
| 8 | Calendar / Notification Service | An external service such as Google Calendar or SendGrid that synchronizes spa/yoga schedules and sends reminder notifications to guests. |
| 9 | SSO Provider | An external authentication provider such as Google Identity or Facebook Login that supports secure single sign-on for guests. |

#### 1.3.2 Use Cases (UC)
| ID | Use Case | Feature | Use Case Description |
|---|---|---|---|
| 01 | Register, Verify Email and Log In | Authentication | The Guest creates an account, verifies email, and logs in securely using system credentials or SSO. |
| 02 | Complete Health and Dietary Profile | Health Profile Management | The Guest provides health conditions, allergies, and dietary preferences with explicit consent. |
| 03 | Manage Staff Accounts and Assign Roles | User and Role Management | The Administrator creates staff accounts and assigns strict roles such as Therapist, Chef, and Receptionist. |
| 04 | Manage Master Data | Master Data Management | The Administrator manages villa types, spa services, retreat packages, and staff records. |
| 05 | Delete Sensitive Health Data | Data Privacy Management | The Guest requests permanent deletion of sensitive health and allergy data after the stay. |
| 06 | Browse Wellness Packages | Package Browsing | The Guest browses available wellness packages and filters them by goals such as Detoxification, Yoga, Stress Relief, or Weight Loss. |
| 07 | Book Wellness Package and Pay Deposit | Booking and Payment | The Guest selects a package, arrival date, villa type, and pays a secure deposit. |
| 08 | Check In Guest | Reception Management | The Receptionist views expected arrivals, checks in guests, assigns a specific villa, and collects required identity information. |
| 09 | Manage Villa Status | Villa Management | The Receptionist updates villa status such as Available, Occupied, Under Maintenance, or Cleaning Required. |
| 10 | View Booking Details and Itinerary Timeline | Booking Tracking | The Guest views booking details, villa information, spa schedule, meal plan, and billing timeline. |
| 11 | Schedule Spa/Treatment Session | Spa Scheduling | The Guest schedules included spa or therapy sessions by selecting a date and time slot. |
| 12 | Find Available Therapist and Treatment Room | Automatic Scheduling | The System automatically checks both therapist availability and treatment room availability before confirming a session. |
| 13 | View Daily Work Schedule | Therapist Schedule Management | The Spa Therapist or Yoga Trainer views assigned daily sessions and treatment-relevant health notes. |
| 14 | Update Treatment Session Status | Treatment Management | The Spa Therapist marks a session as Completed or No-Show. |
| 15 | Book Additional Spa Service | Additional Service Booking | The Receptionist manually books extra spa services for guests and posts the charge to the guest folio. |
| 16 | Pre-select Daily Meals | Meal Management | The Guest selects daily meals from a personalized menu filtered by allergies and dietary restrictions. |
| 17 | View Daily Meal Preparation Dashboard | F&B Dashboard | The Chef or F&B Staff views aggregated meal orders and relevant food allergy alerts for the selected date. |
| 18 | Update Meal Order Status | Meal Order Management | The Chef updates meal order status from Preparing to Ready for Delivery. |
| 19 | Order A-la-carte Food and Beverage | Additional F&B Service | The Guest orders additional food or beverages outside the package and the system posts the charge to the folio. |
| 20 | Enforce Data Minimization for F&B Staff | Data Privacy and RBAC | The System restricts kitchen staff from viewing medical history and only displays food allergies and dietary restrictions. |
| 21 | Generate Consolidated Invoice | Billing Management | The Receptionist generates a consolidated invoice including remaining package charges, additional spa services, and F&B orders. |
| 22 | Process Final Payment and Complete Check-out | Checkout and Payment | The Receptionist processes the final payment and updates villa status after successful check-out. |
| 23 | Submit Post-stay Review and Rating | Feedback Management | The Guest submits a review and rating after completing the stay. |
| 24 | View Revenue Analytics Dashboard | Analytics and Reporting | The Resort Manager views revenue charts categorized by package, spa, and F&B income. |
| 25 | Export Monthly Occupancy and Therapist Utilization Report | Report Export | The Resort Manager exports monthly reports on room occupancy and therapist utilization to Excel. |

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
#### 1.5.1 Entity Relationship Diagram
`![ERD Diagram](images/erd_diagram.png)`

#### 1.5.2 Entities Description
| # | Entity | Description |
|---|---|---|
| 1 | User | Stores personal information and account details of users in the system. |
| 2 | Role | Defines user roles (e.g., guest, staff, administrator). |
| 3 | Consent | Stores the status and version of user consent. |
| 4 | Physical_Health_Profile | Stores physical health information of users, such as medical conditions and injuries. |
| 5 | Dietary_Profile | Stores information regarding dietary preferences and food allergies of users. |
| 6 | Retreat_Package | Contains the catalog of retreat packages offered by the facility. |
| 7 | Villa_Type | Categorizes villa types, including capacity and pricing information. |
| 8 | Villa | Lists specific villa units associated with different types. |
| 9 | Booking | Manages guest reservation details. |
| 10 | Review | Stores feedback and ratings provided by guests after their stay. |
| 11 | Guest_Folio | A summary table of guest expenses and costs during a booking period. |
| 12 | Folio_Item | Contains detailed line items (charges) associated with a specific folio. |
| 13 | Payment | Records detailed information regarding guest payment transactions. |
| 14 | Spa_Booking | Manages individual spa service appointments for guests. |
| 15 | Spa_Service | Catalog of spa services available for booking. |
| 16 | Treatment_Room | Manages the treatment rooms used for spa services. |
| 17 | Therapist | Store information and status of therapists when they are at work. |
| 18 | Schedule | Store schedule and slot to manage session of treatment room and therapist. |
| 19 | Meal_Order | Manages food orders placed by guests. |
| 20 | Meal_Order_Item | Details the specific food items included in a meal order. |
| 21 | Menu_Item | Catalog of food items available on the menu. |

---

## 2. Use Case Specifications - Đắc

### 2.1 Authentication & Sensitive Health Profile
**ID and Name:** UC-02 – Complete Health & Dietary Profile
**Primary Actor:** Guest | **Secondary Actors:** System Administrator
**Description:** This use case allows a guest to create and maintain a sensitive Health & Dietary Profile containing health conditions, food allergies, and dietary preferences. The profile is used to personalize retreat experiences while ensuring privacy, consent management, and strict role-based access control.
**Trigger:** Guest selects “Health & Dietary Profile” after successful authentication.
**Preconditions:**
- Guest account exists and is authenticated.
- Guest account status is Active.
- Guest has accepted Privacy Policy.
**Postconditions:**
- Health and dietary profile is encrypted and stored.
- Authorized services may access only relevant information.
- Audit logs are recorded.

**Normal Flow:**
1. Guest opens Health & Dietary Profile.
2. System displays profile form.
3. System displays consent statement with unchecked consent box by default (BR-08).
4. Guest enters dietary preferences.
5. Guest enters allergy information.
6. Guest enters health conditions.
7. Guest grants explicit consent.
8. Guest submits profile.
9. System validates required information.
10. System encrypts sensitive information before persistence (BR-09).
11. System stores profile.
12. System records audit log (BR-15).
13. System displays confirmation.

**Alternative Flows:**
- A1. Guest provides dietary profile only.
  → Health information remains empty.
- A2. Guest updates existing profile.
  → System overwrites current values and records update history.

**Exceptions:**
- E1. Consent not granted.
  → System rejects submission and displays MSG-03.
- E2. Unexpected storage failure.
  → System displays MSG-15.

**Priority:** High | **Frequency of Use:** Medium
**Business Rules:**
- BR-07 – Role-Based Access Control and Data Minimization.
- BR-08 – Explicit consent is mandatory.
- BR-09 – Sensitive data encryption is required.
- BR-10 – Guest has the right to request permanent deletion.
- BR-15 – System audit logging is mandatory.
**Other Information:** Health information is classified as Sensitive Personal Data.
**Assumptions:** Guests provide accurate and updated information.

### 2.2 Retreat Package & Accommodation Booking

**ID and Name:** UC07 – Book Retreat Package
**Primary Actor:** Guest | **Secondary Actors:** Payment Gateway API
**Description:** This use case allows a guest to browse retreat packages, select stay dates, choose a preferred Villa Type, and secure the reservation through a deposit payment.
**Trigger:** Guest selects “Book Retreat Package”.
**Preconditions:**
- Guest is authenticated.
- Retreat package exists and is active.
- Villa inventory exists.
**Postconditions:**
- Booking is created successfully.
- Deposit transaction is stored.
- Confirmation is generated.
- Audit records are created.

**Normal Flow:**
1. Guest opens Retreat Package page.
2. System displays available packages.
3. Guest selects package.
4. Guest selects stay dates.
5. Guest selects Villa Type only (BR-02).
6. System checks inventory.
7. System calculates package cost and deposit.
8. Guest confirms booking.
9. System redirects to Payment Gateway.
10. Guest completes deposit payment.
11. Gateway returns success result.
12. System confirms reservation (BR-01).
13. System generates itinerary.
14. System records booking logs (BR-15).
15. System displays MSG-04.

**Alternative Flows:**
- A1. Guest changes package.
  → System recalculates pricing.
- A2. Villa inventory changes.
  → System refreshes availability.

**Exceptions:**
- E1. Deposit payment failed.
  → System displays MSG-05.
- E2. Booking confirmation timeout.
  → Reservation remains Pending.

**Priority:** High | **Frequency of Use:** High
**Business Rules:**
- BR-01 – Booking requires successful deposit.
- BR-02 – Guest selects Villa Type only.
- BR-15 – Audit Trail is mandatory.
**Other Information:** Actual Villa assignment occurs during Check-in.
**Assumptions:** Payment services are available.

### 2.3 Spa & Therapy Scheduling Engine

**ID and Name:** UC11 – Schedule Therapy Session
**Primary Actor:** Guest | **Secondary Actors:** Spa Therapist / Yoga Trainer, Calendar & Notification Service
**Description:** This use case allows a guest with an active retreat booking to schedule therapy or wellness sessions included in the purchased retreat package. The system automatically coordinates therapist availability and treatment room availability to prevent resource conflicts.
**Trigger:** Guest selects “Schedule Therapy Session”.
**Preconditions:**
- Guest has an active retreat booking.
- Guest has available therapy sessions included in the package.
- Therapist schedule exists.
- Treatment room availability exists.
**Postconditions:**
- Therapy session reservation is successfully created.
- Therapist and treatment room allocation are completed.
- Notification and reminder are generated.
- Audit logs are stored.

**Normal Flow:**
1. Guest opens Therapy Scheduling.
2. System displays available therapy services.
3. Guest selects therapy type.
4. Guest selects preferred date and time.
5. System validates service eligibility based on purchased package (BR-05).
6. System checks therapist availability.
7. System checks treatment room availability.
8. System prevents resource collision and double booking (BR-04).
9. System allocates therapist and treatment room.
10. System creates therapy reservation.
11. System synchronizes reminder notification.
12. System records audit activity (BR-15).
13. System displays MSG-08.

**Alternative Flows:**
- A1. Guest purchases additional therapy service.
  → System allows reservation beyond package scope.
- A2. Guest modifies session time.
  → System recalculates availability.

**Exceptions:**
- E1. No therapist or room available.
  → System displays MSG-09.
- E2. Booking validation failed.
  → System rejects scheduling request.

**Priority:** High | **Frequency of Use:** High
**Business Rules:**
- BR-04 – Two-dimensional Spa Scheduling.
- BR-05 – Spa service scope restriction.
- BR-15 – Audit Trail.
**Other Information:** Only assigned therapists are allowed to update session status.
**Assumptions:** Availability information is synchronized in real time.

### 2.4 Dietary & Food Service Management

**ID and Name:** UC17 – View Daily Meal Preparation Dashboard
**Primary Actor:** F&B Staff / Chef | **Secondary Actors:** None
**Description:** This use case allows F&B staff to view meal requests and dietary constraints for guests in order to prepare meals that satisfy health restrictions and dietary requirements.
**Trigger:** Chef opens Daily Meal Dashboard.
**Preconditions:**
- Guest meal selection exists.
- Guest has active accommodation.
**Postconditions:**
- Meal preparation dashboard is displayed.
- Restricted information remains hidden.

**Normal Flow:**
1. Chef opens Daily Meal Dashboard.
2. System retrieves meal orders.
3. System retrieves dietary preferences.
4. System retrieves allergy information only (BR-07).
5. System automatically filters incompatible menu items (BR-06).
6. System groups meal preparation requests.
7. System displays preparation instructions.
8. Chef confirms preparation readiness.

**Alternative Flows:**
- A1. Guest updates meal preference.
  → Dashboard refreshes automatically.
- A2. Menu becomes unavailable.
  → System recommends replacement dishes.

**Exceptions:**
- E1. Unauthorized access attempt.
  → System rejects access and displays MSG-14.
- E2. Missing dietary profile.
  → Dashboard shows warning.

**Priority:** Medium | **Frequency of Use:** High
**Business Rules:**
- BR-06 – Automatic Menu Filtering.
- BR-07 – RBAC and Data Minimization.
**Other Information:** Medical conditions are never shown to Chef.
**Assumptions:** Guest dietary profile exists before meal service.

### 2.5 Consolidated Billing & Checkout

#### 2.5.1 UC21 – Generate Consolidated Invoice
**ID and Name:** UC21 – Generate Consolidated Invoice
**Primary Actor:** Receptionist | **Secondary Actors:** Payment Gateway API
**Description:** This use case allows the receptionist to consolidate all eligible charges into a final invoice before checkout.
**Trigger:** Receptionist initiates checkout.
**Preconditions:**
- Guest booking exists.
- Guest Folio exists.
- Charges are posted.
**Postconditions:**
- Consolidated invoice is generated.
- Outstanding balance is calculated.
- Audit logs are recorded.

**Normal Flow:**
1. Receptionist opens Checkout.
2. System retrieves Guest Folio.
3. System retrieves Package charges.
4. System retrieves Spa charges.
5. System retrieves F&B charges.
6. System aggregates all charges using Room_Booking_ID (BR-11).
7. System deducts deposit amount (BR-12).
8. System calculates final payable amount.
9. System generates invoice.
10. System stores invoice record.
11. System records audit logs.

**Alternative Flows:**
- A1. Additional services detected.
  → Invoice recalculation occurs.
- A2. Promotion applies.
  → Final amount is adjusted.

**Exceptions:**
- E1. Transaction data missing.
  → System displays MSG-15.

**Priority:** High | **Frequency of Use:** High
**Business Rules:**
- BR-11 – Guest Folio Consolidation.
- BR-12 – Checkout Constraint.
- BR-15 – Audit Trail.
**Other Information:** Only posted transactions appear in invoice.
**Assumptions:** All service usage has been synchronized.

#### 2.5.2 UC22 – Process Final Payment
**ID and Name:** UC22 – Process Final Payment
**Primary Actor:** Receptionist | **Secondary Actors:** Payment Gateway API
**Description:** This use case allows the receptionist to collect and process the final payment after all eligible charges have been consolidated into the final invoice. The payment process finalizes the guest stay and prepares checkout completion.
**Trigger:** Receptionist selects “Process Final Payment” after invoice confirmation.
**Preconditions:**
- Consolidated invoice has been generated.
- Guest booking status is Active.
- Outstanding balance exists.
- Payment service is available.
**Postconditions:**
- Final payment is completed successfully.
- Invoice status becomes Paid.
- Guest booking status becomes Completed.
- Checkout becomes available.
- Audit logs are stored.

**Normal Flow:**
1. Receptionist opens Final Payment screen.
2. System displays invoice summary.
3. System calculates remaining balance after deposit deduction (BR-12).
4. Receptionist confirms payment amount.
5. Guest selects payment method.
6. System redirects transaction to Payment Gateway.
7. Gateway validates payment.
8. System receives successful response.
9. System records transaction.
10. System updates invoice status to Paid.
11. System updates booking status to Completed.
12. System records audit logs (BR-15).
13. System displays payment success confirmation.

**Alternative Flows:**
- A1. Guest changes payment method.
  → System regenerates payment request.
- A2. Payment requires additional verification.
  → System waits for asynchronous confirmation.

**Exceptions:**
- E1. Outstanding Spa/F&B fees still exist.
  → System rejects checkout and displays MSG-11.
- E2. Payment failed.
  → Invoice remains unpaid.
- E3. Gateway timeout occurs.
  → Transaction status becomes Pending.
- E4. Unexpected payment exception.
  → System displays MSG-15.

**Priority:** High | **Frequency of Use:** High
**Business Rules:**
- BR-12 – Checkout Constraint.
- BR-15 – Audit Trail.
**Other Information:** Checkout cannot be completed until payment is fully confirmed.
**Assumptions:** Payment gateway remains available.

### 2.6 Revenue Analytics & Reporting

#### 2.6.1 UC24 – View Revenue Dashboard
**ID and Name:** UC24 – View Revenue Dashboard
**Primary Actor:** Manager | **Secondary Actors:** System Administrator
**Description:** This use case allows managers to monitor business performance through analytical dashboards that summarize retreat revenue, occupancy indicators, therapist utilization, and food service performance.
**Trigger:** Manager selects “Revenue Dashboard”.
**Preconditions:**
- Manager account is active.
- Reporting data exists.
- Data synchronization has completed.
**Postconditions:**
- Dashboard information is displayed.
- Reporting indicators become available for business decisions.

**Normal Flow:**
1. Manager opens Revenue Dashboard.
2. System retrieves completed transaction records only (BR-13).
3. System retrieves retreat package revenue.
4. System retrieves Spa revenue.
5. System retrieves F&B revenue.
6. System calculates occupancy indicators.
7. System calculates therapist utilization.
8. System generates analytical charts.
9. Manager filters reporting period.
10. System refreshes dashboard.
11. Manager exports report if required.

**Alternative Flows:**
- A1. Manager changes reporting dimensions.
  → Dashboard recalculates indicators.
- A2. Manager exports dashboard.
  → System generates downloadable report.

**Exceptions:**
- E1. Reporting data unavailable.
  → System displays empty dashboard.
- E2. Aggregation calculation failed.
  → System displays MSG-15.

**Priority:** Medium | **Frequency of Use:** Medium
**Business Rules:** BR-13 – Reporting and Review Logic.
**Other Information:** Dashboard is read-only and cannot modify operational data.
**Assumptions:** Operational data synchronization runs successfully.

#### 2.6.2 UC23 – Submit Retreat Review
**ID and Name:** UC23 – Submit Retreat Review
**Primary Actor:** Guest | **Secondary Actors:** None
**Description:** This use case allows guests to submit ratings and reviews after completing their retreat experience.
**Trigger:** Guest selects “Submit Review”.
**Preconditions:**
- Retreat booking status is Completed.
- Checkout process has finished.
**Postconditions:**
- Review is stored successfully.
- Rating becomes available in reporting.

**Normal Flow:**
1. Guest opens Review page.
2. System validates retreat completion status.
3. System displays review form.
4. Guest enters rating score.
5. Guest enters textual review.
6. Guest submits review.
7. System validates submission.
8. System stores review.
9. System displays successful confirmation.

**Alternative Flows:**
- A1. Guest edits review.
  → System updates review.

**Exceptions:**
- E1. Guest has not completed retreat.
  → System rejects review submission.
- E2. Unexpected system error.
  → System displays MSG-15.

**Priority:** Low | **Frequency of Use:** Medium
**Business Rules:** BR-13 – Only completed stays may submit reviews.
**Other Information:** Reviews contribute to analytics and quality monitoring.
**Assumptions:** Guests provide honest feedback.

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

### 3.2 User Authentication
#### 3.2.1 Authentication & Login Screen
- **(1) EMAIL**: Required string.
- **(2) MẬT KHẨU**: Masked string.
- **(3) Quên mật khẩu? Link**: Navigates to recovery.
- **(4) Đăng nhập Button**: Submits payload. BCrypt verification. Provisions JWT.
- **(5) Google SSO Button**: OAuth2 flow. Bypasses manual password.
- **(6) Đăng ký ngay Link**: Navigates to registration.

#### 3.2.2 New Account Registration Screen
- **(1) Header**: "Đăng ký".
- **(2) HỌ VÀ TÊN**: Required string.
- **(3) EMAIL**: Required string. Regex validation. Unique constraint.
- **(4) MẬT KHẨU**: Masked string with toggle visibility.
- **(5) XÁC NHẬN MẬT KHẨU**: Must match password field exactly.
- **(6) ĐĂNG KÝ Button**: One-way BCrypt hashing before INSERT.

#### 3.2.3 Password Recovery Screens
- **Phase 1 (Request Reset Link)**:
  - **(1) Header**: "Khôi phục mật khẩu".
  - **(2) Email Input**: Required string.
  - **(3) Gửi yêu cầu Button**: Verifies email, generates Reset_Token (15 min expiry), dispatches email.
- **Phase 2 (Set New Password)**:
  - **(6) Mật khẩu mới**: Masked string.
  - **(7) Xác nhận mật khẩu**: Exact match validation.
  - **(8) Cập nhật Password Button**: Submits new password + Token. Updates DB, revokes token.

### 3.3 Master Data
#### 3.3.1 Villa Status Management Screen
- **(1) KPI Status Summary**: COUNT queries for Clean, Dirty, Maintenance.
- **(2) Filters**: Dropdowns by Floor, Category.
- **(3) Villa Identification**: Primary key identifier.
- **(4) Housekeeping Status Dropdown**: Transitions state (e.g. Dirty -> Clean).
- **(5) Occupancy Info & Alerts**: Cross-module relational queries (Occupied, Arrival Expected, Vacant, Maintenance).

#### 3.3.2 Performance Reports & Export Screen
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
| EI-01 | User Interface | The system shall provide responsive web-based interfaces for desktop and tablet devices. |
| EI-02 | Navigation | Screen flow shall follow Left → Right and Top → Bottom principles. |
| EI-03 | Consent Interface | Sensitive health-related screens shall display consent notices before data collection. |
| EI-04 | Dashboard Interface | Dashboard shall support filtering, sorting, searching, and exporting. |
| EI-05 | Validation Interface | Input validation messages shall appear immediately. |
| EI-06 | System Messages | System shall follow predefined messages MSG-01 → MSG-15. |
| EI-07 | Payment Gateway | Support deposit payment and final payment integration (Stripe/VNPay/PayPal). |
| EI-08 | Notification Service | Support email verification and appointment reminders (SendGrid/Google Calendar). |
| EI-09 | Reporting Interface | Support exporting reports and invoice generation. |

### 4.2 Quality Attributes

#### 4.2.1 Usability
| ID | Requirement | Measurement |
|---|---|---|
| US-01 | Guest registration shall be simple and intuitive | ≤ 3 minutes |
| US-02 | Retreat package booking shall be completed efficiently | ≤ 5 minutes |
| US-03 | Receptionist shall complete check-in quickly | ≤ 3 minutes |
| US-04 | Therapist shall update treatment status efficiently | ≤ 30 seconds |
| US-05 | Chef shall locate dietary information quickly | ≤ 15 seconds |
| US-06 | Manager shall access reports efficiently | ≤ 10 seconds |
| US-07 | User interfaces shall remain visually consistent | Across all modules |
| US-08 | Consent requirements shall be clearly displayed | Mandatory |

##### 4.2.1.1 Training Requirements
| User Role | Training Time |
|---|---|
| Guest | No training required |
| Receptionist | ≤ 2 hours |
| Therapist | ≤ 1 hour |
| Administrator | ≤ 4 hours |

##### 4.2.1.2 Usability Standards
| Standard Type | Requirement |
|---|---|
| Accessibility | WCAG principles |
| Responsive Design | Supported |
| Navigation Consistency | Required |

#### 4.2.2 Performance
| ID | Requirement | Target |
|---|---|---|
| PF-01 | Average response time | ≤ 2 seconds |
| PF-02 | Maximum response time | ≤ 5 seconds |
| PF-03 | Booking confirmation (UC07) | ≤ 10 seconds |
| PF-04 | Final payment processing (UC22) | ≤ 15 seconds |
| PF-05 | Revenue dashboard loading (UC24) | ≤ 8 seconds |
| PF-06 | Concurrent active users | 100 users |
| PF-07 | Booking requests | 30 requests/minute |
| PF-08 | Dashboard requests | 50 requests/minute |
| PF-09 | Guest Accounts | 100,000 |
| PF-10 | Bookings | 50,000 |
| PF-11 | Audit Logs | 1,000,000 |
| PF-12 | Payment Records | 500,000 |
| PF-13 | CPU Utilization | ≤ 70% |
| PF-14 | Memory Utilization | ≤ 8 GB |
| PF-15 | Database Query | ≤ 1 second |
| PF-16 | Encryption Overhead | ≤ 10% |
| PF-17 | Monthly System Uptime | ≥ 99.5% |
| PF-18 | Recovery Time | ≤ 30 minutes |
| PF-19 | Transaction Data Loss | Not allowed |

##### 4.2.2.2 Related Business Rules
| Rule ID | Description |
|---|---|
| BR-09 | Sensitive Data Encryption |
| BR-15 | Audit Trail |

---

## 5. Requirement Appendix - My

### 5.1 Business Rules
| ID | Business Rule Name | Detailed Description (Constraints & Logic) | Reference (UC/Source) |
|---|---|---|---|
| BR-01 | Booking Confirmation and Deposit Payment | A Retreat Package booking shall only be confirmed after the system receives a successful deposit payment result from the payment gateway. | UC07 |
| BR-02 | Villa Management and Allocation | Guests may only select a Villa Type when making a booking. The system shall only confirm a booking if sufficient capacity is available for the selected Villa Type. A specific Villa shall be assigned by the Receptionist during Check-in. | UC07, UC08 |
| BR-03 | Villa Status and Allocation Constraints | A Villa shall not be assigned to more than one active booking during the same period. Villas with a status of Maintenance or Out of Service shall not be allocated. | UC08, UC09 |
| BR-04 | Dual Resource Spa Scheduling | A Spa appointment shall only be valid when both an available Therapist and an available Therapy Room exist at the requested time. The system shall prevent double booking. | UC12 |
| BR-05 | Spa Service Eligibility and Update Control | Guests may only book Spa services included in their purchased Retreat Package. Only the assigned Therapist may update the treatment session status. Additional Spa services may only be added by a Receptionist. | UC11, UC14, UC15 |
| BR-06 | Automatic F&B Menu Filtering | The system shall automatically exclude menu items that contain allergens or conflict with the guest’s declared dietary preferences. Chefs and F&B Staff shall only have access to allergy and dietary information necessary for their work. | UC16, UC17, UC20 |
| BR-07 | Role-Based Access Control (RBAC) and Data Minimization | Therapists may only access health information required for treatment purposes. Chefs may only access food allergy and dietary preference information. Receptionists shall not have access to guest health records. Access control shall be enforced at the backend level. | UC03, UC13, UC17, UC20 |
| BR-08 | Consent for Sensitive Data Collection | The system shall obtain explicit consent from guests before collecting health-related or allergy information. Consent checkboxes shall be unchecked by default. | UC02 |
| BR-09 | Sensitive Data Encryption | Health information, allergies, dietary preferences, and personal identification data shall be encrypted when stored in the database. | UC02, UC08 |
| BR-10 | Right to Data Erasure | Guests may request the permanent deletion of their health and allergy information after their retreat stay has been completed. | UC05 |
| BR-11 | Guest Folio and Consolidated Billing | All Spa and F&B charges shall be recorded in the Guest Folio using the corresponding Booking_ID and included in the final bill. | UC15, UC19, UC21 |
| BR-12 | Check-out Constraints | Guests shall not be allowed to complete the Check-out process if any Spa or F&B charges remain unpaid. Any previously paid deposit shall be deducted from the final invoice. | UC21, UC22 |
| BR-13 | Reporting and Review Logic | Revenue and occupancy reports shall only include completed transactions. Only guests who have completed their retreat stay may submit reviews and ratings. Each booking may submit only one review. | UC23, UC24, UC25 |
| BR-14 | Guest Stay Registration Information | During Check-in, the system shall collect and store guest identification information to comply with accommodation registration regulations. | UC08, Residence Law 2020 |
| BR-15 | Audit Trail Management | The system shall maintain audit logs for critical activities such as login, health data access, booking, payment, and Check-out to support monitoring and traceability. | UC07, UC11, UC21, UC22 |
| BR-16 | Meal Order Status Workflow | Meal Order status shall only progress in the following sequence: Pending → Preparing → Ready for Delivery. Status reversal shall not be permitted. Only Chefs or F&B Staff may update Meal Order status. | UC18 |
| BR-17 | Spa Appointment Notification and Synchronization | After a Spa appointment is successfully booked, the system shall send confirmation and reminder notifications to the guest. Notification failures shall not invalidate a confirmed appointment. | UC11 |
| BR-18 | Authentication and Single Sign-On (SSO) | The system shall support authentication through Google and Facebook. Accounts registered via SSO must complete email verification before being allowed to book a Retreat Package. The system shall prevent duplicate account creation. | UC01 |

### 5.2 System Messages
| Code | Type | Context | Message |
|---|---|---|---|
| MSG-01 | Success | Account registration successful | Registration successful. Please verify your email address. |
| MSG-02 | Error | Login failed | Invalid email or password. |
| MSG-03 | Success | Email verification successful | Your email has been verified. You may now log in. |
| MSG-04 | Warning | Health data consent not provided | You must provide consent before submitting health information. |
| MSG-05 | Success | Retreat Package booking successful | Retreat Package booking created successfully. |
| MSG-06 | Error | Deposit payment failed | Deposit payment failed. Please try again. |
| MSG-07 | Success | Check-in successful | Check-in completed successfully. |
| MSG-08 | Error | No suitable Villa available | No available Villa could be found for allocation. |
| MSG-09 | Success | Spa appointment booked successfully | Spa appointment booked successfully. |
| MSG-10 | Error | Therapist or therapy room unavailable | No available Therapist or Therapy Room could be found. |
| MSG-11 | Success | Meal order placed successfully | Meal order has been recorded successfully. |
| MSG-12 | Warning | Meal contains allergens | Warning: This meal contains ingredients listed in the guest’s allergy profile. |
| MSG-13 | Warning | Outstanding charges during check-out | Please settle all outstanding charges before checking out. |
| MSG-14 | Success | Check-out completed successfully | Check-out completed successfully. |
| MSG-15 | Success | Health data deleted successfully | Health data has been deleted successfully. |
| MSG-16 | Success | Review submitted successfully | Thank you for submitting your review. |
| MSG-17 | Success | Excel report exported successfully | Report exported successfully. |
| MSG-18 | Error | Unauthorized access | You do not have permission to access this function. |
| MSG-19 | Error | Unexpected system error | An unexpected system error has occurred. Please try again later. |
