**SOFTWARE REQUIREMENT SPECIFICATION**

**Project Name (HOS-03)**

– Hanoi, Jan 2024 –

**Table of Contents**

[I. Record of Changes 3](#_heading=h.gogimfbbs6xi)

[II. Software Requirement Specification 4](#_heading=h.3kmm91mcabtx)

[1\. Overall Requirements 4](#_heading=h.h6lfhntpyasj)

[1.1 Context Diagram 4](#_heading=h.jf878bizy4mg)

[1.2 Main Business Processes 5](#_heading=h.ra7omcqnaxtn)

[1.3 User Requirements 5](#_heading=h.wm80rp859u3f)

[1.4 System Functionalities 7](#_heading=h.xcm22yxde9rm)

[1.5 Entity Relationship Diagram 7](#_heading=h.4vqk11f0svki)

[2\. Use Case Specifications 8](#_heading=h.6zuvva4dwf3g)

[2.1 &lt;<Feature Name1&gt;> 8](#_heading=h.khs21frkcmi3)

[2.2 Xyz Feature 10](#_heading=h.ngzmqu5gb7v4)

[3\. Functional Requirements 11](#_heading=h.j6qjjdplk7bs)

[3.1 Feature Name1 11](#_heading=h.ybwkhu5f1jux)

[3.2 User Authentication 11](#_heading=h.onmyrhezh09x)

[3.3 System Administration 12](#_heading=h.xyyzhdep0ep3)

[4\. Non-Functional Requirements 13](#_heading=h.bpj11dlyu00)

[3.1 External Interfaces 13](#_heading=h.p8y5x1cjxyzd)

[3.2 Quality Attributes 13](#_heading=h.qvj4u1d8rhtt)

[5\. Requirement Appendix 14](#_heading=h.91nul92t74j)

[5.1 Business Rules 14](#_heading=h.192d7kdel6w6)

[5.2 System Messages 14](#_heading=h.isnvovrhur7h)

[5.3 Other Requirements… 15](#_heading=h.3oyfqqa5yz2e)

# I. Record of Changes

| **Date** | **A\*  <br>M, D** | **In charge** | **Change Description** |
| --- | --- | --- | --- |
|     |     |     |     |
| --- | --- | --- | --- |
|     |     |     |     |
| --- | --- | --- | --- |
|     |     |     |     |
| --- | --- | --- | --- |
|     |     |     |     |
| --- | --- | --- | --- |
|     |     |     |     |
| --- | --- | --- | --- |
|     |     |     |     |
| --- | --- | --- | --- |
|     |     |     |     |
| --- | --- | --- | --- |
|     |     |     |     |
| --- | --- | --- | --- |
|     |     |     |     |
| --- | --- | --- | --- |
|     |     |     |     |
| --- | --- | --- | --- |
|     |     |     |     |
| --- | --- | --- | --- |
|     |     |     |     |
| --- | --- | --- | --- |
|     |     |     |     |
| --- | --- | --- | --- |

\*A - Added M - Modified D - Deleted

# II. Software Requirement Specification

## 1\. Overall Requirements

### 1.1 Context Diagram - My

The Xoai Aura Retreat Management System is a wellness resort management platform designed to support retreat package booking, villa accommodation management, spa scheduling, dietary planning, billing, and business reporting.

The context diagram below illustrates the system boundary and its interactions with external entities, including Guests, Receptionists, Spa Therapists/Yoga Instructors, F&B Staff, Administrators, and Managers. The system also integrates with external services such as Payment Gateway APIs, Authentication APIs, and Notification & Calendar APIs.

These interactions enable the efficient management of retreat operations, guest services, payment processing, and business analytics.

[Context Diagram](https://drive.google.com/file/d/11_WfkEVhWSdekAlrh9CIz8mSRiV3k7bq/view?usp=sharing)

### 1.2 Main Business Processes - Hải

[Main Business Processes](https://drive.google.com/file/d/1PoYklOGFGtD73upbl_p68I7gUDY_4eDJ/view?usp=drive_link)

| **Step #** | **Step Name** | **Detailed Description** | **Role** | **Note** |
| --- | --- | --- | --- | --- |
| **1** | Publish driving course enrollment | **1\. Activity:**<br><br>\- Guest creates an account and fills out the "Health & Dietary Profile" (medical conditions, allergies).<br><br>\- System checks the UI to ensure NO consent checkboxes are pre-checked.<br><br>**2\. Input:** Email, Password, Sensitive medical/allergy data.<br><br>**3\. Output:** User account, Health profile (encrypted in DB). | Guest, System | **Crucial:** Must strictly comply with Decree 356/2025 regarding sensitive data. |
| --- | --- | --- | --- | --- |
| **2** | Retreat Package Booking & Deposit | **1\. Activity:**<br><br>\- Guest filters and selects a Retreat Package by goal, chooses dates and Villa type.<br><br>\- Redirects to Payment Gateway (Stripe/VNPay) to pay the deposit.<br><br>**2\. Input:** Retreat_Package_ID, Arrival/Departure Dates, Villa Type, Card details.<br><br>**3\. Output:** Booking Record, Successful deposit payment status. | Guest, System | When clicking the "register for course" button, user authentication is required |
| --- | --- | --- | --- | --- |
| **3** | Check-in & Room Assignment | **1\. Activity:**<br><br>\- Receptionist views the expected guest list, assigns a physical Villa number.<br><br>\- Collects guest's ID/CCCD info for temporary residence declaration.<br><br>\- System encrypts the ID and changes room status.<br><br>**2\. Input:** Booking ID, ID/Passport details, Physical Villa number.<br><br>**3\. Output:** Villa Status: "Occupied", ID data securely stored. | Receptionist, System | Complies with Residence Law 2020. Receptionist MUST NOT view the guest's medical data. |
| --- | --- | --- | --- | --- |
| **4** | Spa Scheduling | 1\. **Activity**:<br><br>\- Guest selects date/time for a Spa session.<br><br>\- System runs a DB locking transaction: Finds ONE available therapist AND ONE available room simultaneously. If available, confirms the schedule and calls Google Calendar API to notify the guest.<br><br>2\. **Input**: Booking ID, Spa_Service_ID, Desired time slot.<br><br>3\. **Output**: Confirmed Spa schedule, Reminder email (sent 1h prior). | Guest, System | **Complex Logic:** 2-Dimensional Double-Booking prevention constraint |
| --- | --- | --- | --- | --- |
| **5** | Spa Treatment Execution | **1\. Activity:**<br><br>\- Therapist views their work schedule and reads physical health notes (other data is hidden).<br><br>\- Performs treatment and updates the session status.<br><br>\- System automatically accumulates charges (if the guest uses extra off-package services) into the Guest Folio.<br><br>**2\. Input:** Spa Schedule, Physical therapy notes.<br><br>**3\. Output:** Status: "Completed/No-Show", Updated Guest Folio. | Spa Therapist, System | **RBAC:** Therapist MUST NOT view the guest's dietary/allergy information. |
| --- | --- | --- | --- | --- |
| **6** | F&B Meal Ordering | **1\. Activity:**<br><br>\- System cross-references the "Dietary Profile" to filter out menu items containing allergens.<br><br>\- Guest selects daily meals from this pre-filtered safe menu.<br><br>**2\. Input:** Original Menu, Guest's allergy data.<br><br>**3\. Output:** Safe Menu, F&B Order. | Guest, System | Applies the Data Minimization principle |
| --- | --- | --- | --- | --- |
| **7** | Culinary Preparation | **1\. Activity:**<br><br>\- Chef opens the Dashboard to view aggregated meal orders and "allergy alerts".<br><br>\- Chef prepares the dish and updates the status (Preparing -> Ready to deliver).<br><br>**2\. Input:** F&B Order, Food allergy alerts.<br><br>**3\. Output:** Updated meal status, A-la-carte charges billed to Guest Folio. | Chef / F&B, System | **RBAC:** Chef MUST NOT view the guest's physical medical records |
| --- | --- | --- | --- | --- |
| **8** | Check-out & Consolidated Bill | **1\. Activity:**<br><br>\- Receptionist clicks Check-out. System scans to check if there are any pending/unpaid Spa/F&B orders.<br><br>\- If clear, the system aggregates: Remaining Package fee + Extra services into 1 Consolidated Bill.<br><br>\- Receptionist collects payment and releases the room.<br><br>**2\. Input:** Guest Folio (Room + Spa + F&B).<br><br>**3\. Output:** Final Bill, Villa Status: "Needs Cleaning". | Receptionist, System | **Constraint:** Guest CANNOT check out if there are pending orders. |
| --- | --- | --- | --- | --- |
| **9** | Review & Data Deletion | **1\. Activity:**<br><br>\- Guest submits a Retreat quality review form.<br><br>\- Guest exercises their "Right to be forgotten". System executes a command to permanently wipe medical and allergy records from the DB.<br><br>**2\. Input:** Rating/Review, Data deletion request.<br><br>**3\. Output:** System review record, User profile "cleansed" of sensitive data | Guest, System | Ensures absolute privacy after the retreat concludes. |
| --- | --- | --- | --- | --- |

### 1.3 User Requirements - Đắc

#### 1.3.1 Actors

_\[An actor is someone/something that interacts with the system._

- _The only external entities that interact with the system_
- ﻿_Actors are outside the system and not part of it_
- ﻿_A user is an individual, whereas an actor represents the role played by all users of the same type_
- _There are other types of actors in addition to or in place of human actors: external systems, I/O devices, or timers_

_Following are some questions you might ask to help user representatives identify actors_

- _Who (or what) is notified when something occurs within the system?_
- _Who (or what) provides information or services to the system?_
- _Who (or what) helps the system respond to and complete a task?_

_This part gives the description of system actors, you can follow the table form as below\]_

| **#** | **Actor** | **Description** |
| --- | --- | --- |
| 1   | Guest / Customer | A customer who uses the system to register, log in, complete health and dietary profiles, browse wellness packages, book villas, schedule spa/treatment sessions, pre-select meals, view itinerary, make payments, and submit reviews. |
| --- | --- | --- |
| 2   | Receptionist | A front-desk staff member who manages guest check-in/check-out, assigns physical villas, manages villa status, books additional spa services for guests, and processes consolidated invoices and final payments. |
| --- | --- | --- |
| 3   | Spa Therapist / Yoga Trainer | A service provider who views daily treatment schedules, checks treatment-relevant health notes, and updates treatment session status such as Completed or No-Show. |
| --- | --- | --- |
| 4   | Chef / F&B Staff | A food and beverage staff member who views daily meal preparation dashboards, checks food allergy alerts, prepares personalized meals, and updates meal order status. |
| --- | --- | --- |
| 5   | Administrator | A system administrator who manages staff accounts, assigns user roles, configures role-based access control, and manages master data such as villa types, spa services, retreat packages, and staff records. |
| --- | --- | --- |
| 6   | Resort Manager | A management user who monitors revenue dashboards, reviews business performance, and exports monthly reports on room occupancy and therapist utilization. |
| --- | --- | --- |
| 7   | Payment Gateway | An external payment service such as Stripe, VNPay, or PayPal that processes deposit payments and final payments securely. |
| --- | --- | --- |
| 8   | Calendar / Notification Service | An external service such as Google Calendar or SendGrid that synchronizes spa/yoga schedules and sends reminder notifications to guests. |
| --- | --- | --- |
| 9   | SSO Provider | An external authentication provider such as Google Identity or Facebook Login that supports secure single sign-on for guests. |
| --- | --- | --- |

#### 1.3.2 Use Cases (UC)

_\[A use case (UC) describes a sequence of interactions between a system and an external actor that results in the actor being able to achieve some outcome of value. The names of use cases are always written in the form of a verb followed by an object. Select strong, descriptive names to make it evident from the name that the use case will deliver something valuable for some user._

_Following are some questions you might ask to help user representatives identify use cases_

- _What will the actor use the system for?_
- _Will the actor create, store, change, remove, or read data in the system?_
- _Will the actor need to inform the system about external events or changes?_
- _Will the actor need to be informed about certain occurrences in the system?_

_This part describes the use cases you could define, you can follow the table form as below\]_

| **ID** | **Use Case** | **Feature** | **Use Case Description** |
| --- | --- | --- | --- |
| 01  | Register, Verify Email and Log In | Authentication1 | The Guest creates an account, verifies email, and logs in securely using system credentials or SSO. |
| --- | --- | --- | --- |
| 02  | Complete Health and Dietary Profile | Health Profile Management | The Guest provides health conditions, allergies, and dietary preferences with explicit consent. |
| --- | --- | --- | --- |
| 03  | Manage Staff Accounts and Assign Roles | User and Role Management | The Administrator creates staff accounts and assigns strict roles such as Therapist, Chef, and Receptionist. |
| --- | --- | --- | --- |
| 04  | Manage Master Data | Master Data Management | The Administrator manages villa types, spa services, retreat packages, and staff records. |
| --- | --- | --- | --- |
| 05  | Delete Sensitive Health Data | Data Privacy Management | The Guest requests permanent deletion of sensitive health and allergy data after the stay. |
| --- | --- | --- | --- |
| 06  | Browse Wellness Packages | Package Browsing | The Guest browses available wellness packages and filters them by goals such as Detoxification, Yoga, Stress Relief, or Weight Loss. |
| --- | --- | --- | --- |
| 07  | Book Wellness Package and Pay Deposit | Booking and Payment | The Guest selects a package, arrival date, villa type, and pays a secure deposit. |
| --- | --- | --- | --- |
| 08  | Check In Guest | Reception Management | The Receptionist views expected arrivals, checks in guests, assigns a specific villa, and collects required identity information. |
| --- | --- | --- | --- |
| 09  | Manage Villa Status | Villa Management | The Receptionist updates villa status such as Available, Occupied, Under Maintenance, or Cleaning Required. |
| --- | --- | --- | --- |
| 10  | View Booking Details and Itinerary Timeline | Booking Tracking | The Guest views booking details, villa information, spa schedule, meal plan, and billing timeline. |
| --- | --- | --- | --- |
| 11  | Schedule Spa/Treatment Session | Spa Scheduling | The Guest schedules included spa or therapy sessions by selecting a date and time slot. |
| --- | --- | --- | --- |
| 12  | Find Available Therapist and Treatment Room | Automatic Scheduling | The System automatically checks both therapist availability and treatment room availability before confirming a session. |
| --- | --- | --- | --- |
| 13  | View Daily Work Schedule | Therapist Schedule Management | The Spa Therapist or Yoga Trainer views assigned daily sessions and treatment-relevant health notes. |
| --- | --- | --- | --- |
| 14  | Update Treatment Session Status | Treatment Management | The Spa Therapist marks a session as Completed or No-Show. |
| --- | --- | --- | --- |
| 15  | Book Additional Spa Service | Additional Service Booking | The Receptionist manually books extra spa services for guests and posts the charge to the guest folio. |
| --- | --- | --- | --- |
| 16  | Pre-select Daily Meals | Meal Management | The Guest selects daily meals from a personalized menu filtered by allergies and dietary restrictions. |
| --- | --- | --- | --- |
| 17  | View Daily Meal Preparation Dashboard | F&B Dashboard | The Chef or F&B Staff views aggregated meal orders and relevant food allergy alerts for the selected date. |
| --- | --- | --- | --- |
| 18  | Update Meal Order Status | Meal Order Management | The Chef updates meal order status from Preparing to Ready for Delivery. |
| --- | --- | --- | --- |
| 19  | Order A-la-carte Food and Beverage | Additional F&B Service | The Guest orders additional food or beverages outside the package and the system posts the charge to the folio. |
| --- | --- | --- | --- |
| 20  | Enforce Data Minimization for F&B Staff | Data Privacy and RBAC | The System restricts kitchen staff from viewing medical history and only displays food allergies and dietary restrictions. |
| --- | --- | --- | --- |
| 21  | Generate Consolidated Invoice | Billing Management | The Receptionist generates a consolidated invoice including remaining package charges, additional spa services, and F&B orders. |
| --- | --- | --- | --- |
| 22  | Process Final Payment and Complete Check-out | Checkout and Payment | The Receptionist processes the final payment and updates villa status after successful check-out. |
| --- | --- | --- | --- |
| 23  | Submit Post-stay Review and Rating | Feedback Management | The Guest submits a review and rating after completing the stay. |
| --- | --- | --- | --- |
| 24  | View Revenue Analytics Dashboard | Analytics and Reporting | The Resort Manager views revenue charts categorized by package, spa, and F&B income. |
| --- | --- | --- | --- |
| 25  | Export Monthly Occupancy and Therapist Utilization Report | Report Export | The Resort Manager exports monthly reports on room occupancy and therapist utilization to Excel. |
| --- | --- | --- | --- |

#### 1.3.2 Use Case Diagrams

_In this section, you need to provide the UC diagram(s) to show the actor-UCs and UC-UC relationships like the sample below. You can have multiple UC diagrams for the system, each diagram is for one actor or one workflow\]_

##### 1.3.2.1 UCs for Guest

##### 1.3.2.2 UCs for User

##### 1.3.2.3 UCs for Receptionist

##### 1.3.2.4 UCs for Spa Therapist / Yoga Trainer

##### 1.3.2.5 UCs for F&B Staff / Chef

##### 1.3.2.6 UCs for Administrator / Manager

##### [Link](https://app.diagrams.net/#G1YncpWgVmU244V3KrwqgE1e0nYMXlNedF#%7B%22pageId%22%3A%22eFBbMIPbn18NP5xC_-qE%22%7D)

### 1.4 System Functionalities - Dương

_\[Provide functionality overview of software system: screen flow, screen descriptions, system user roles, screen authorization, non-screen functions, ERD\]_

#### 1.4.1 Screens Flow

_\[This part shows the system screens and the relationship among screens. You can draw the Screens Flow for the system in the form of diagram as below.\]_

##### 1.4.1.1 User Screen

[Link](https://drive.google.com/file/d/1U4d7F3pLs_-YoxhPNGxx9ETU0EttOFbf/view?usp=sharing)

##### 1.4.1.2 Spa Therapist Screen

[Link](https://drive.google.com/file/d/1AnkOjnU7ZvZUiBOPnfWBZjzMf3UO-xXm/view?usp=sharing)

##### 1.4.1.3 F&B Screen

[Link](https://drive.google.com/file/d/1mJMnmUsTXdhT-t4qXD-jXv278YJXzzHM/view?usp=sharing)

##### 1.4.1.4 Receptionist Screen

[Link](https://drive.google.com/file/d/1wuPfRU24YecTAOEjnpYzZbvMYWqMera_/view?usp=sharing)

##### 1.4.1.5 Admin Screen

[Link](https://drive.google.com/file/d/1dZef4KxB4NK_VIhdDVCAE3O1F0CnfmUC/view?usp=sharing)

##### 1.4.1.6 All System

[Screen Flow](https://drive.google.com/file/d/1IXyWIHj3ku43vv0RJHPECeEu1_UzhsSk/view?usp=sharing)

#### 1.4.2 Screen Authorization

_\[Provide the system roles authorization to the system features (down to screens, and event to the screen activities if applicable) in the table form as below – replace Role-Name1, Role-Name2,… with your specific system user role names\]_

#### 

| **Screen** | **Guest** | **Receptionist** | **Therapist** | **F&B / Chef** | **Admin** |
| --- | --- | --- | --- | --- | --- |
| Home Page | X   | X   | X   | X   | X   |
| --- | --- | --- | --- | --- | --- |
| About Us | X   | X   | X   | X   | X   |
| --- | --- | --- | --- | --- | --- |
| Contact Us | X   | X   | X   | X   | X   |
| --- | --- | --- | --- | --- | --- |
| Packages List | X   | X   | X   | X   | X   |
| --- | --- | --- | --- | --- | --- |
| Package Detail | X   | X   | X   | X   | X   |
| --- | --- | --- | --- | --- | --- |
| Login Screen | X   | X   | X   | X   | X   |
| --- | --- | --- | --- | --- | --- |
| Register Screen | X   |     |     |     |     |
| --- | --- | --- | --- | --- | --- |
| Book Now \[Action\] | X   |     |     |     |     |
| --- | --- | --- | --- | --- | --- |
| Health & Dietary Profile | X   |     |     |     |     |
| --- | --- | --- | --- | --- | --- |
| Payment | X   |     |     |     |     |
| --- | --- | --- | --- | --- | --- |
| Success Page | X   |     |     |     |     |
| --- | --- | --- | --- | --- | --- |
| Guest Dashboard | X   |     |     |     |     |
| --- | --- | --- | --- | --- | --- |
| Itinerary Timeline | X   |     |     |     |     |
| --- | --- | --- | --- | --- | --- |
| Spa Scheduling | X   |     |     |     |     |
| --- | --- | --- | --- | --- | --- |
| A la-carte Menu | X   |     |     |     |     |
| --- | --- | --- | --- | --- | --- |
| Dietary Menu | X   |     |     |     |     |
| --- | --- | --- | --- | --- | --- |
| My Profile | X   |     |     |     |     |
| --- | --- | --- | --- | --- | --- |
| Update/Delete Profile | X   |     |     |     |     |
| --- | --- | --- | --- | --- | --- |
| Review & Rating | X   |     |     |     |     |
| --- | --- | --- | --- | --- | --- |
| Receptionist Dashboard |     | X   |     |     |     |
| --- | --- | --- | --- | --- | --- |
| Expected Arrival List |     | X   |     |     |     |
| --- | --- | --- | --- | --- | --- |
| Check-in Form |     | X   |     |     |     |
| --- | --- | --- | --- | --- | --- |
| Villa Status Management |     | X   |     |     |     |
| --- | --- | --- | --- | --- | --- |
| Update Villa Status \[Action\] |     | X   |     |     |     |
| --- | --- | --- | --- | --- | --- |
| Manual Spa Booking \[Modal\] |     | X   |     |     |     |
| --- | --- | --- | --- | --- | --- |
| Checkout Management |     | X   |     |     |     |
| --- | --- | --- | --- | --- | --- |
| Invoice Detail |     | X   |     |     |     |
| --- | --- | --- | --- | --- | --- |
| Process Payment |     | X   |     |     |     |
| --- | --- | --- | --- | --- | --- |
| Therapist Dashboard |     |     | X   |     |     |
| --- | --- | --- | --- | --- | --- |
| Daily Schedule |     |     | X   |     |     |
| --- | --- | --- | --- | --- | --- |
| Session Detail |     |     | X   |     |     |
| --- | --- | --- | --- | --- | --- |
| View Health Notes \[Modal\] |     |     | X   |     |     |
| --- | --- | --- | --- | --- | --- |
| Update Session Status \[Action\] |     |     | X   |     |     |
| --- | --- | --- | --- | --- | --- |
| History |     |     | X   |     |     |
| --- | --- | --- | --- | --- | --- |
| F&B / Chef Dashboard |     |     |     | X   |     |
| --- | --- | --- | --- | --- | --- |
| Daily Meal Prep Board |     |     |     | X   |     |
| --- | --- | --- | --- | --- | --- |
| A-la-carte Orders Board |     |     |     | X   |     |
| --- | --- | --- | --- | --- | --- |
| Update Prep Status \[Action\] |     |     |     | X   |     |
| --- | --- | --- | --- | --- | --- |
| Admin Dashboard |     |     |     |     | X   |
| --- | --- | --- | --- | --- | --- |
| Staff Accounts Management |     |     |     |     | X   |
| --- | --- | --- | --- | --- | --- |
| Assign Roles \[Modal\] |     |     |     |     | X   |
| --- | --- | --- | --- | --- | --- |
| Master Data Management |     |     |     |     | X   |
| --- | --- | --- | --- | --- | --- |
| Villas Management |     |     |     |     | X   |
| --- | --- | --- | --- | --- | --- |
| Packages Management |     |     |     |     | X   |
| --- | --- | --- | --- | --- | --- |
| Spa Services Management |     |     |     |     | X   |
| --- | --- | --- | --- | --- | --- |
| Revenue Analytics |     |     |     |     | X   |
| --- | --- | --- | --- | --- | --- |
| Package Revenue Chart |     |     |     |     | X   |
| --- | --- | --- | --- | --- | --- |
| Spa Revenue Chart |     |     |     |     | X   |
| --- | --- | --- | --- | --- | --- |
| F&B Revenue Chart |     |     |     |     | X   |
| --- | --- | --- | --- | --- | --- |
| Export Center |     |     |     |     | X   |
| --- | --- | --- | --- | --- | --- |
| Daily ID Report |     |     |     |     | X   |
| --- | --- | --- | --- | --- | --- |
| Excel Reports |     |     |     |     | X   |
| --- | --- | --- | --- | --- | --- |

#### 1.4.3 Non-UI Functions

_\[Provide the descriptions for the non-screen system functions, i.e batch/cron job, service, API, etc.\]_

| **#** | **Feature** | **System Function** | **Description** |
| --- | --- | --- | --- |
| 1   | Authentication | Google OAuth API | Allows users to sign in using Google accounts. |
| --- | --- | --- | --- |
| 2   | Payment | VNPay Payment Gateway API | Processes online payments through VNPay. |
| --- | --- | --- | --- |
| 3   | Payment | Payment Verification Service | Verifies transaction status and updates booking records. |
| --- | --- | --- | --- |
| 4   | Data Management | Database Backup Service | Performs scheduled database backups. |
| --- | --- | --- | --- |

### 1.5 Entity Relationship Diagram - Ngọc

**_1.5.1 Entity Relationship Diagram_**

_\[ the_ **_ERD_** _using the Crow-Foot notation\]_

[Figure x - ERD](https://drive.google.com/file/d/1oWAww-BpAlODydsSWfGnFlNlPFawkdOB/view?usp=sharing)

**_1.5.2 Entities Description_**

| **#** | **Entity** | **Description** |
| --- | --- | --- |
| 1   | User | Stores personal information and account details of users in the system. |
| --- | --- | --- |
| 2   | Role | Defines user roles (e.g., guest, staff, administrator). |
| --- | --- | --- |
| 3   | Consent | Stores the status and version of user consent. |
| --- | --- | --- |
| 4   | Physical_Health_Profile | Stores physical health information of users, such as medical conditions and injuries. |
| --- | --- | --- |
| 5   | Dietary_Profile | Stores information regarding dietary preferences and food allergies of users. |
| --- | --- | --- |
| 6   | Retreat_Package | Contains the catalog of retreat packages offered by the facility. |
| --- | --- | --- |
| 7   | Villa_Type | Categorizes villa types, including capacity and pricing information. |
| --- | --- | --- |
| 8   | Villa | Lists specific villa units associated with different types. |
| --- | --- | --- |
| 9   | Booking | Manages guest reservation details. |
| --- | --- | --- |
| 10  | Review | Stores feedback and ratings provided by guests after their stay. |
| --- | --- | --- |
| 11  | Guest_Folio | A summary table of guest expenses and costs during a booking period. |
| --- | --- | --- |
| 12  | Folio_Item | Contains detailed line items (charges) associated with a specific folio. |
| --- | --- | --- |
| 13  | Payment | Records detailed information regarding guest payment transactions. |
| --- | --- | --- |
| 14  | Spa_Booking | Manages individual spa service appointments for guests. |
| --- | --- | --- |
| 15  | Spa_Service | Catalog of spa services available for booking. |
| --- | --- | --- |
| 16  | Treatment_Room | Manages the treatment rooms used for spa services. |
| --- | --- | --- |
| 17  | Therapist | Store information and status of therapists when they are at work. |
| --- | --- | --- |
| 18  | Schedule | Store schedule and slot to manage session of treatment room and therapist |
| --- | --- | --- |
| 19  | Meal_Order | Manages food orders placed by guests. |
| --- | --- | --- |
| 20  | Meal_Order_Item | Details the specific food items included in a meal order. |
| --- | --- | --- |
| 21  | Menu_Item | Catalog of food items available on the menu. |
| --- | --- | --- |

## 2\. Use Case Specifications - Đắc

_\[Provide specifications for the use cases (UCs) those are covered in the system. The UCs are grouped by the system features and even sub features._ **_You just need to provide UC specifications for complex UCs involving in the main workflows (business processes)_**_. Other UCs (i.e CRUD or data-viewing UCs) are simple, and you just need to refer the descriptions in the Functional Requirement (part 3) below)\]_

### 2.1 Authentication & Sensitive Health Profile

<div class="joplin-table-wrapper"><table><thead><tr><th><p>ID and Name:</p></th><th colspan="3"><p><strong>UC-02 – Complete Health &amp; Dietary Profile</strong></p></th></tr><tr><th><p>Primary Actor:</p></th><th><p>Guest</p></th><th><p>Secondary Actors:</p></th><th><p>System Administrator</p></th></tr><tr><th><p>Description:</p></th><th colspan="3"><p>This use case allows a guest to create and maintain a sensitive Health &amp; Dietary Profile containing health conditions, food allergies, and dietary preferences. The profile is used to personalize retreat experiences while ensuring privacy, consent management, and strict role-based access control.</p></th></tr><tr><th><p>Trigger:</p></th><th colspan="3"><p>Guest selects “Health &amp; Dietary Profile” after successful authentication.</p></th></tr><tr><th><p>Preconditions:</p></th><th colspan="3"><ul><li>Guest account exists and is authenticated.</li><li>Guest account status is Active.</li><li>Guest has accepted Privacy Policy.</li></ul></th></tr><tr><th><p>Postconditions:</p></th><th colspan="3"><ul><li>Health and dietary profile is encrypted and stored.</li><li>Authorized services may access only relevant information.</li><li>Audit logs are recorded.</li></ul></th></tr><tr><th><p>Normal Flow:</p></th><th colspan="3"><ol><li>Guest opens Health &amp; Dietary Profile.</li><li>System displays profile form.</li><li>System displays consent statement with unchecked consent box by default (BR-08).</li><li>Guest enters dietary preferences.</li><li>Guest enters allergy information.</li><li>Guest enters health conditions.</li><li>Guest grants explicit consent.</li><li>Guest submits profile.</li><li>System validates required information.</li><li>System encrypts sensitive information before persistence (BR-09).</li><li>System stores profile.</li><li>System records audit log (BR-15).</li><li>System displays confirmation.</li></ol></th></tr><tr><th><p>Alternative Flows:</p></th><th colspan="3"><ul><li>A1. Guest provides dietary profile only.<br>→ Health information remains empty.</li><li>A2. Guest updates existing profile.<br>→ System overwrites current values and records update history.</li></ul></th></tr><tr><th><p>Exceptions:</p></th><th colspan="3"><ul><li>E1. Consent not granted.<br>→ System rejects submission and displays MSG-03.</li><li>E2. Unexpected storage failure.<br>→ System displays MSG-15.</li></ul><p></p></th></tr><tr><th><p>Priority:</p></th><th colspan="3"><p>High</p></th></tr><tr><th><p>Frequency of Use:</p></th><th colspan="3"><p>Medium</p></th></tr><tr><th><p>Business Rules:</p></th><th colspan="3"><p>BR-07 – Role-Based Access Control and Data Minimization.</p><p>BR-08 – Explicit consent is mandatory.</p><p>BR-09 – Sensitive data encryption is required.</p><p>BR-10 – Guest has the right to request permanent deletion.</p><p>BR-15 – System audit logging is mandatory.</p></th></tr><tr><th><p>Other Information:</p></th><th colspan="3"><ul><li>Health information is classified as Sensitive Personal Data.</li></ul></th></tr><tr><th><p>Assumptions:</p></th><th colspan="3"><ul><li>Guests provide accurate and updated information.</li></ul></th></tr></thead></table></div>

### 2.2 Retreat Package & Accommodation Booking

<div class="joplin-table-wrapper"><table><thead><tr><th><p>ID and Name:</p></th><th colspan="3"><p><strong>UC07 – Book Retreat Package</strong></p></th></tr><tr><th><p>Primary Actor:</p></th><th><p>Guest</p></th><th><p>Secondary Actors:</p></th><th><p>Payment Gateway API</p></th></tr><tr><th><p>Description:</p></th><th colspan="3"><p>This use case allows a guest to browse retreat packages, select stay dates, choose a preferred Villa Type, and secure the reservation through a deposit payment.</p></th></tr><tr><th><p>Trigger:</p></th><th colspan="3"><p>Guest selects “Book Retreat Package”.</p></th></tr><tr><th><p>Preconditions:</p></th><th colspan="3"><ul><li>Guest is authenticated.</li><li>Retreat package exists and is active.</li><li>Villa inventory exists.</li></ul></th></tr><tr><th><p>Postconditions:</p></th><th colspan="3"><ul><li>Booking is created successfully.</li><li>Deposit transaction is stored.</li><li>Confirmation is generated.</li><li>Audit records are created.</li></ul></th></tr><tr><th><p>Normal Flow:</p></th><th colspan="3"><ol><li>Guest opens Retreat Package page.</li><li>System displays available packages.</li><li>Guest selects package.</li><li>Guest selects stay dates.</li><li>Guest selects Villa Type only (BR-02).</li><li>System checks inventory.</li><li>System calculates package cost and deposit.</li><li>Guest confirms booking.</li><li>System redirects to Payment Gateway.</li><li>Guest completes deposit payment.</li><li>Gateway returns success result.</li><li>System confirms reservation (BR-01).</li><li>System generates itinerary.</li><li>System records booking logs (BR-15).</li><li>System displays MSG-04.</li></ol></th></tr><tr><th><p>Alternative Flows:</p></th><th colspan="3"><ul><li>A1. Guest changes package.<br>→ System recalculates pricing.</li><li>A2. Villa inventory changes.<br>→ System refreshes availability.</li></ul></th></tr><tr><th><p>Exceptions:</p></th><th colspan="3"><ul><li>E1. Deposit payment failed.<br>→ System displays MSG-05.</li><li>E2. Booking confirmation timeout.<br>→ Reservation remains Pending.</li></ul><p></p></th></tr><tr><th><p>Priority:</p></th><th colspan="3"><p>High</p></th></tr><tr><th><p>Frequency of Use:</p></th><th colspan="3"><p>High</p></th></tr><tr><th><p>Business Rules:</p></th><th colspan="3"><p>BR-01 – Booking requires successful deposit.</p><p>BR-02 – Guest selects Villa Type only.</p><p>BR-15 – Audit Trail is mandatory.</p></th></tr><tr><th><p>Other Information:</p></th><th colspan="3"><ul><li>Actual Villa assignment occurs during Check-in.</li></ul></th></tr><tr><th><p>Assumptions:</p></th><th colspan="3"><ul><li>Payment services are available.</li></ul></th></tr></thead></table></div>

### 2.3 Spa & Therapy Scheduling Engine

<div class="joplin-table-wrapper"><table><thead><tr><th><p>ID and Name:</p></th><th colspan="3"><p><strong>UC11 – Schedule Therapy Session</strong></p></th></tr><tr><th><p>Primary Actor:</p></th><th><p>Guest</p></th><th><p>Secondary Actors:</p></th><th><p>Spa Therapist / Yoga Trainer</p><p>Calendar &amp; Notification Service</p></th></tr><tr><th><p>Description:</p></th><th colspan="3"><p>This use case allows a guest with an active retreat booking to schedule therapy or wellness sessions included in the purchased retreat package. The system automatically coordinates therapist availability and treatment room availability to prevent resource conflicts.</p></th></tr><tr><th><p>Trigger:</p></th><th colspan="3"><p>Guest selects “Schedule Therapy Session”.</p></th></tr><tr><th><p>Preconditions:</p></th><th colspan="3"><ul><li>Guest has an active retreat booking.</li><li>Guest has available therapy sessions included in the package.</li><li>Therapist schedule exists.</li><li>Treatment room availability exists.</li></ul></th></tr><tr><th><p>Postconditions:</p></th><th colspan="3"><ul><li>Therapy session reservation is successfully created.</li><li>Therapist and treatment room allocation are completed.</li><li>Notification and reminder are generated.</li><li>Audit logs are stored.</li></ul></th></tr><tr><th><p>Normal Flow:</p></th><th colspan="3"><ol><li>Guest opens Therapy Scheduling.</li><li>System displays available therapy services.</li><li>Guest selects therapy type.</li><li>Guest selects preferred date and time.</li><li>System validates service eligibility based on purchased package (BR-05).</li><li>System checks therapist availability.</li><li>System checks treatment room availability.</li><li>System prevents resource collision and double booking (BR-04).</li><li>System allocates therapist and treatment room.</li><li>System creates therapy reservation.</li><li>System synchronizes reminder notification.</li><li>System records audit activity (BR-15).</li><li>System displays MSG-08.</li></ol></th></tr><tr><th><p>Alternative Flows:</p></th><th colspan="3"><ul><li>A1. Guest purchases additional therapy service.<br>→ System allows reservation beyond package scope.</li><li>A2. Guest modifies session time.<br>→ System recalculates availability.</li></ul></th></tr><tr><th><p>Exceptions:</p></th><th colspan="3"><ul><li>E1. No therapist or room available.<br>→ System displays MSG-09.</li><li>E2. Booking validation failed.<br>→ System rejects scheduling request.</li></ul><p></p></th></tr><tr><th><p>Priority:</p></th><th colspan="3"><p>High</p></th></tr><tr><th><p>Frequency of Use:</p></th><th colspan="3"><p>High</p></th></tr><tr><th><p>Business Rules:</p></th><th colspan="3"><p>BR-04 – Two-dimensional Spa Scheduling.</p><p>BR-05 – Spa service scope restriction.</p><p>BR-15 – Audit Trail.</p></th></tr><tr><th><p>Other Information:</p></th><th colspan="3"><ul><li>Only assigned therapists are allowed to update session status.</li></ul></th></tr><tr><th><p>Assumptions:</p></th><th colspan="3"><ul><li>Availability information is synchronized in real time.</li></ul></th></tr></thead></table></div>

### 2.4 Dietary & Food Service Management.

<div class="joplin-table-wrapper"><table><thead><tr><th><p>ID and Name:</p></th><th colspan="3"><p><strong>UC17 – View Daily Meal Preparation Dashboard</strong></p></th></tr><tr><th><p>Primary Actor:</p></th><th><p>F&amp;B Staff / Chef</p></th><th><p>Secondary Actors:</p></th><th><p>None</p></th></tr><tr><th><p>Description:</p></th><th colspan="3"><p>This use case allows F&amp;B staff to view meal requests and dietary constraints for guests in order to prepare meals that satisfy health restrictions and dietary requirements.</p></th></tr><tr><th><p>Trigger:</p></th><th colspan="3"><p>Chef opens Daily Meal Dashboard.</p></th></tr><tr><th><p>Preconditions:</p></th><th colspan="3"><ul><li>Guest meal selection exists.</li><li>Guest has active accommodation.</li></ul></th></tr><tr><th><p>Postconditions:</p></th><th colspan="3"><ul><li>Meal preparation dashboard is displayed.</li><li>Restricted information remains hidden.</li></ul></th></tr><tr><th><p>Normal Flow:</p></th><th colspan="3"><ol><li>Chef opens Daily Meal Dashboard.</li><li>System retrieves meal orders.</li><li>System retrieves dietary preferences.</li><li>System retrieves allergy information only (BR-07).</li><li>System automatically filters incompatible menu items (BR-06).</li><li>System groups meal preparation requests.</li><li>System displays preparation instructions.</li><li>Chef confirms preparation readiness.</li></ol></th></tr><tr><th><p>Alternative Flows:</p></th><th colspan="3"><ul><li>A1. Guest updates meal preference.<br>→ Dashboard refreshes automatically.</li><li>A2. Menu becomes unavailable.<br>→ System recommends replacement dishes.</li></ul></th></tr><tr><th><p>Exceptions:</p></th><th colspan="3"><ul><li>E1. Unauthorized access attempt.<br>→ System rejects access and displays MSG-14.</li><li>E2. Missing dietary profile.<br>→ Dashboard shows warning.</li></ul><p></p></th></tr><tr><th><p>Priority:</p></th><th colspan="3"><p>Medium</p></th></tr><tr><th><p>Frequency of Use:</p></th><th colspan="3"><p>High</p></th></tr><tr><th><p>Business Rules:</p></th><th colspan="3"><p>BR-06 – Automatic Menu Filtering.</p><p>BR-07 – RBAC and Data Minimization.</p></th></tr><tr><th><p>Other Information:</p></th><th colspan="3"><ul><li>Medical conditions are never shown to Chef.</li></ul></th></tr><tr><th><p>Assumptions:</p></th><th colspan="3"><ul><li>Guest dietary profile exists before meal service.</li></ul></th></tr></thead></table></div>

### 2.5 Consolidated Billing & Checkout.

#### 2.5.1 UC21 – Generate Consolidated Invoice

### 

<div class="joplin-table-wrapper"><table><thead><tr><th><p>ID and Name:</p></th><th colspan="3"><p><strong>UC21 – Generate Consolidated Invoice</strong></p></th></tr><tr><th><p>Primary Actor:</p></th><th><p>Receptionist</p></th><th><p>Secondary Actors:</p></th><th><p>Payment Gateway API</p></th></tr><tr><th><p>Description:</p></th><th colspan="3"><p>This use case allows the receptionist to consolidate all eligible charges into a final invoice before checkout.</p></th></tr><tr><th><p>Trigger:</p></th><th colspan="3"><p>Receptionist initiates checkout.</p></th></tr><tr><th><p>Preconditions:</p></th><th colspan="3"><ul><li>Guest booking exists.</li><li>Guest Folio exists.</li><li>Charges are posted.</li></ul></th></tr><tr><th><p>Postconditions:</p></th><th colspan="3"><ul><li>Consolidated invoice is generated.</li><li>Outstanding balance is calculated.</li><li>Audit logs are recorded.</li></ul></th></tr><tr><th><p>Normal Flow:</p></th><th colspan="3"><ol><li>Receptionist opens Checkout.</li><li>System retrieves Guest Folio.</li><li>System retrieves Package charges.</li><li>System retrieves Spa charges.</li><li>System retrieves F&amp;B charges.</li><li>System aggregates all charges using Room_Booking_ID (BR-11).</li><li>System deducts deposit amount (BR-12).</li><li>System calculates final payable amount.</li><li>System generates invoice.</li><li>System stores invoice record.</li><li>System records audit logs.</li></ol></th></tr><tr><th><p>Alternative Flows:</p></th><th colspan="3"><ul><li>A1. Additional services detected.<br>→ Invoice recalculation occurs.</li><li>A2. Promotion applies.<br>→ Final amount is adjusted.</li></ul></th></tr><tr><th><p>Exceptions:</p></th><th colspan="3"><ul><li>E1. Transaction data missing.</li></ul><p>→ System displays MSG-15.</p><p></p></th></tr><tr><th><p>Priority:</p></th><th colspan="3"><p>High</p></th></tr><tr><th><p>Frequency of Use:</p></th><th colspan="3"><p>High</p></th></tr><tr><th><p>Business Rules:</p></th><th colspan="3"><p>BR-11 – Guest Folio Consolidation.</p><p>BR-12 – Checkout Constraint.</p><p>BR-15 – Audit Trail.</p></th></tr><tr><th><p>Other Information:</p></th><th colspan="3"><ul><li>Only posted transactions appear in invoice.</li></ul></th></tr><tr><th><p>Assumptions:</p></th><th colspan="3"><ul><li>All service usage has been synchronized.</li></ul></th></tr></thead></table></div>

### 

#### _2.5.2_ UC22 – Process Final Payment

### 

<div class="joplin-table-wrapper"><table><thead><tr><th><p>ID and Name:</p></th><th colspan="3"><p><strong>UC22 – Process Final Payment</strong></p></th></tr><tr><th><p>Primary Actor:</p></th><th><p>Receptionist</p></th><th><p>Secondary Actors:</p></th><th><p>Payment Gateway API</p></th></tr><tr><th><p>Description:</p></th><th colspan="3"><p>This use case allows the receptionist to collect and process the final payment after all eligible charges have been consolidated into the final invoice. The payment process finalizes the guest stay and prepares checkout completion.</p></th></tr><tr><th><p>Trigger:</p></th><th colspan="3"><p>Receptionist selects “Process Final Payment” after invoice confirmation.</p></th></tr><tr><th><p>Preconditions:</p></th><th colspan="3"><ul><li>Consolidated invoice has been generated.</li><li>Guest booking status is Active.</li><li>Outstanding balance exists.</li><li>Payment service is available.</li></ul></th></tr><tr><th><p>Postconditions:</p></th><th colspan="3"><ul><li>Final payment is completed successfully.</li><li>Invoice status becomes Paid.</li><li>Guest booking status becomes Completed.</li><li>Checkout becomes available.</li><li>Audit logs are stored.</li></ul></th></tr><tr><th><p>Normal Flow:</p></th><th colspan="3"><ol><li>Receptionist opens Final Payment screen.</li><li>System displays invoice summary.</li><li>System calculates remaining balance after deposit deduction (BR-12).</li><li>Receptionist confirms payment amount.</li><li>Guest selects payment method.</li><li>System redirects transaction to Payment Gateway.</li><li>Gateway validates payment.</li><li>System receives successful response.</li><li>System records transaction.</li><li>System updates invoice status to Paid.</li><li>System updates booking status to Completed.</li><li>System records audit logs (BR-15).</li><li>System displays payment success confirmation.</li></ol></th></tr><tr><th><p>Alternative Flows:</p></th><th colspan="3"><ul><li>A1. Guest changes payment method.<br>→ System regenerates payment request.</li><li>A2. Payment requires additional verification.<br>→ System waits for asynchronous confirmation.</li></ul></th></tr><tr><th><p>Exceptions:</p></th><th colspan="3"><ul><li>E1. Outstanding Spa/F&amp;B fees still exist.<br>→ System rejects checkout and displays MSG-11.</li><li>E2. Payment failed.<br>→ Invoice remains unpaid.</li><li>E3. Gateway timeout occurs.<br>→ Transaction status becomes Pending.</li><li>E4. Unexpected payment exception.<br>→ System displays MSG-15.</li></ul><p></p></th></tr><tr><th><p>Priority:</p></th><th colspan="3"><p>High</p></th></tr><tr><th><p>Frequency of Use:</p></th><th colspan="3"><p>High</p></th></tr><tr><th><p>Business Rules:</p></th><th colspan="3"><p>BR-12 – Checkout Constraint.</p><p>BR-15 – Audit Trail.</p></th></tr><tr><th><p>Other Information:</p></th><th colspan="3"><ul><li>Checkout cannot be completed until payment is fully confirmed.</li></ul></th></tr><tr><th><p>Assumptions:</p></th><th colspan="3"><ul><li>Payment gateway remains available.</li></ul></th></tr></thead></table></div>

### 2.6 Revenue Analytics & Reporting

#### 2.6.1 **UC24 – View Revenue Dashboard**

### 

<div class="joplin-table-wrapper"><table><thead><tr><th><p>ID and Name:</p></th><th colspan="3"><p><strong>UC24 – View Revenue Dashboard</strong></p></th></tr><tr><th><p>Primary Actor:</p></th><th><p>Manager</p></th><th><p>Secondary Actors:</p></th><th><p>System Administrator</p></th></tr><tr><th><p>Description:</p></th><th colspan="3"><p>This use case allows managers to monitor business performance through analytical dashboards that summarize retreat revenue, occupancy indicators, therapist utilization, and food service performance.</p></th></tr><tr><th><p>Trigger:</p></th><th colspan="3"><p>Manager selects “Revenue Dashboard”.</p></th></tr><tr><th><p>Preconditions:</p></th><th colspan="3"><ul><li>Manager account is active.</li><li>Reporting data exists.</li><li>Data synchronization has completed.</li></ul></th></tr><tr><th><p>Postconditions:</p></th><th colspan="3"><ul><li>Dashboard information is displayed.</li><li>Reporting indicators become available for business decisions.</li></ul></th></tr><tr><th><p>Normal Flow:</p></th><th colspan="3"><ol><li>Manager opens Revenue Dashboard.</li><li>System retrieves completed transaction records only (BR-13).</li><li>System retrieves retreat package revenue.</li><li>System retrieves Spa revenue.</li><li>System retrieves F&amp;B revenue.</li><li>System calculates occupancy indicators.</li><li>System calculates therapist utilization.</li><li>System generates analytical charts.</li><li>Manager filters reporting period.</li><li>System refreshes dashboard.</li><li>Manager exports report if required.</li></ol></th></tr><tr><th><p>Alternative Flows:</p></th><th colspan="3"><ul><li>A1. Manager changes reporting dimensions.<br>→ Dashboard recalculates indicators.</li><li>A2. Manager exports dashboard.<br>→ System generates downloadable report.</li></ul></th></tr><tr><th><p>Exceptions:</p></th><th colspan="3"><ul><li>E1. Reporting data unavailable.<br>→ System displays empty dashboard.</li><li>E2. Aggregation calculation failed.<br>→ System displays MSG-15.</li></ul><p></p></th></tr><tr><th><p>Priority:</p></th><th colspan="3"><p>Medium</p></th></tr><tr><th><p>Frequency of Use:</p></th><th colspan="3"><p>Medium</p></th></tr><tr><th><p>Business Rules:</p></th><th colspan="3"><p>BR-13 – Reporting and Review Logic.</p></th></tr><tr><th><p>Other Information:</p></th><th colspan="3"><ul><li>Dashboard is read-only and cannot modify operational data.</li></ul></th></tr><tr><th><p>Assumptions:</p></th><th colspan="3"><ul><li>Operational data synchronization runs successfully.</li></ul></th></tr></thead></table></div>

### 

#### 2.6.2 UC23 – Submit Retreat Review

<div class="joplin-table-wrapper"><table><thead><tr><th><p>ID and Name:</p></th><th colspan="3"><p><strong>UC23 – Submit Retreat Review</strong></p></th></tr><tr><th><p>Primary Actor:</p></th><th><p>Guest</p></th><th><p>Secondary Actors:</p></th><th><p>None</p></th></tr><tr><th><p>Description:</p></th><th colspan="3"><p>This use case allows guests to submit ratings and reviews after completing their retreat experience.</p></th></tr><tr><th><p>Trigger:</p></th><th colspan="3"><p>Guest selects “Submit Review”.</p></th></tr><tr><th><p>Preconditions:</p></th><th colspan="3"><ul><li>Retreat booking status is Completed.</li><li>Checkout process has finished.</li></ul></th></tr><tr><th><p>Postconditions:</p></th><th colspan="3"><ul><li>Review is stored successfully.</li><li>Rating becomes available in reporting.</li></ul></th></tr><tr><th><p>Normal Flow:</p></th><th colspan="3"><ol><li>Guest opens Review page.</li><li>System validates retreat completion status.</li><li>System displays review form.</li><li>Guest enters rating score.</li><li>Guest enters textual review.</li><li>Guest submits review.</li><li>System validates submission.</li><li>System stores review.</li><li>System displays successful confirmation.</li></ol></th></tr><tr><th><p>Alternative Flows:</p></th><th colspan="3"><ul><li>A1. Guest edits review.</li></ul><p>→ System updates review.</p></th></tr><tr><th><p>Exceptions:</p></th><th colspan="3"><ul><li>E1. Guest has not completed retreat.<br>→ System rejects review submission.</li><li>E2. Unexpected system error.<br>→ System displays MSG-15.</li></ul><p></p></th></tr><tr><th><p>Priority:</p></th><th colspan="3"><p>Low</p></th></tr><tr><th><p>Frequency of Use:</p></th><th colspan="3"><p>Medium</p></th></tr><tr><th><p>Business Rules:</p></th><th colspan="3"><p>BR-13 – Only completed stays may submit reviews.</p></th></tr><tr><th><p>Other Information:</p></th><th colspan="3"><ul><li>Reviews contribute to analytics and quality monitoring</li></ul></th></tr><tr><th><p>Assumptions:</p></th><th colspan="3"><ul><li>Guests provide honest feedback.</li></ul></th></tr></thead></table></div>

## 3\. Functional Requirements - Hải

### 3.1 Core Feature

#### 3.1.1 Health & Dietary Form Screen

**\[Content #1\]**

#### 3.1.13 Manager Revenue Dashboard Screen

**\[Content #1\]**

- The screen layout utilizes a standard dashboard structure with a persistent left navigation sidebar.
- The main content area features a top filter bar, a mid-section containing two large data visualization widgets (a Donut chart and a Line/Bar chart), and a bottom section displaying a detailed data grid of financial transactions.

**\[Content #2\]**

- **Description:** This high-level dashboard provides the Management team with a comprehensive overview of the resort's financial performance. The system dynamically extracts and aggregates data from completed Consolidated Folios (Check-outs) to render real-time business intelligence metrics.
- **Mapped Use Case:** UC24 - As a Manager, I want to view a Revenue Dashboard (Pie/Bar charts) breaking down income by Retreat Packages, Spa, and F&B.

**\[Content #3\]**

<div class="joplin-table-wrapper"><table><thead><tr><th><p><strong>Field Name</strong></p></th><th><p><strong>Description</strong></p></th></tr><tr><th colspan="2"><p><strong>Field Group: Dashboard Filters</strong></p></th></tr><tr><th><p>(1) Filters</p></th><th><p>Data type: Dropdown lists.</p><p>- Time period: This Month, This Quarter, This Year.</p><p>- Category: All Services, Retreat Packages, Spa, F&amp;B.</p><p>Action: Changing these values triggers a backend API call to re-query the financial data and dynamically re-render the associated chThe screen is divided into two main sections: "Diet &amp; Allergies" on the left and "Physical Health Status" on the right.</p><ul><li>At the bottom of the form, there is a consent confirmation area and two action buttons.</li></ul><p></p><p><img src=""></p><p></p><p><strong><em>[Content #2]</em></strong></p><ul><li><strong><em>Description:</em></strong><em> This screen allows the Guest to input their personal health and dietary profile. This sensitive information is strictly confidential and will be segregated by the system: Chefs will only have access to dietary restrictions, while Spa Therapists will only view physical conditions.</em></li><li><strong><em>Mapped Use Case:</em></strong><em> UC02 - As a Guest, I want to complete my "Health &amp; Dietary Profile".</em></li></ul><p><strong><em>[Content #3 ]</em></strong></p><table><thead><tr><th><p><strong>Field Name</strong></p></th><th><p><strong>Description</strong></p></th></tr><tr><th colspan="2"><p><strong>Field Group: Dietary &amp; Allergies</strong></p></th></tr><tr><th><p>(1) Food Allergies Checkboxes</p></th><th><p>Data type: Array of Booleans (Options: Peanuts, Shellfish, Dairy, Gluten). Optional. Checked values will be routed to F&amp;B Staff</p></th></tr><tr><th><p>(2) Other Allergies Input</p></th><th><p>Data type: String, max length of 255 characters. Optional text field for unlisted allergies.</p></th></tr><tr><th><p>(3) Diet Type Buttons</p></th><th><p>Data type: Enum/String (Options: Vegan, Vegetarian, Keto, Halal). Single selection. Required field for automatic menu filtering.</p></th></tr><tr><th><p>(4) Additional Notes</p></th><th><p>Data type: String (Text area), max length of 500 characters. Optional for specific taste preferences.</p></th></tr><tr><th colspan="2"><p><strong><em>Field Group: Physical Health Status</em></strong></p></th></tr><tr><th><p>(5) Current Medical Conditions</p></th><th><p>Data type: String (Text area), max length of 500 characters. Optional. (e.g., High blood pressure, diabetes).</p></th></tr><tr><th><p>(6) Current Medications</p></th><th><p>Data type: String (Text area), max length of 500 characters. Optional.</p></th></tr><tr><th><p>(7) Recent Injuries or Issues</p></th><th><p>Data type: String (Text area), max length of 500 characters. Optional. (e.g., Back pain, joint issues). This specific data will be visible to Spa Therapists.</p></th></tr><tr><th colspan="2"><p><strong><em>Field Group: Confirmation &amp; Actions</em></strong></p></th></tr><tr><th><p>(8) Consent Checkbox</p></th><th><p>Data type: Boolean.</p><p><strong>Strict Constraint:</strong> The initial value MUST be Unchecked (False) to comply with Personal Data Protection regulations (Decree 356/2025). The system must disable the Save button if this is not checked.</p></th></tr><tr><th><p>(9) "Save &amp; Continue" Button</p></th><th><p>Action: Triggers data submission. Backend must encrypt sensitive health data fields before inserting them into the Database. Redirects to the next step.</p></th></tr><tr><th><p>(10) "Review Itinerary" Button</p></th><th><p>Action: Cancels the current input and navigates back to the Guest's itinerary dashboard.</p></th></tr></thead></table><p></p><p></p><p></p><p></p><p></p><p></p><p></p><p></p><h4><a id="_heading=h.5wplhwij9haa"></a>3.1.2 Data Erasure Request Screen</h4><p><strong>[Content #1]</strong></p><ul><li>The UI is presented as a warning Modal/Dialog with prominent red text alerts.</li><li>It contains static warning text indicating that data cannot be recovered after deletion and a legal compliance note regarding personal data protection.</li></ul><p><strong><img src=""></strong></p><p><strong>[Content #2]</strong></p><ul><li><strong>Description:</strong> This screen allows the Guest to exercise their "Right to Erasure" in compliance with data privacy regulations. The user is strictly required to re-authenticate via their password before the system executes the permanent deletion of their medical profiles, allergies, and itinerary records.</li><li><strong>Mapped Use Case:</strong> UC05 - As a Guest, I want to exercise my "Right to Erasure", permanently deleting my health and allergy data from the system after the retreat ends.</li></ul><p><strong>[Content #3]</strong></p><table><thead><tr><th><p><strong>Field Name</strong></p></th><th><p><strong>Description</strong></p></th></tr><tr><th colspan="2"><p><strong>Field Group: Verification</strong></p></th></tr><tr><th><p>(1) Password Input</p></th><th><p>Data type: String. Required field. Input is masked by default (e.g., ***). Used to verify the user's identity before executing a destructive action.</p></th></tr><tr><th><p>(2) Toggle Visibility (Eye Icon)</p></th><th><p>Action: Toggles the input field (1) state between masked (hidden) and plain text (visible).</p></th></tr><tr><th colspan="2"><p><strong><em>Field Group: Actions</em></strong></p></th></tr><tr><th><p>(3) "Hủy" (Cancel) Button</p></th><th><p>Action: Closes the warning dialog, aborts the operation, and returns the user to the previous screen.</p></th></tr><tr><th><p>(4) "Xóa vĩnh viễn" (Permanent Delete) Button</p></th><th><p>Action: Submits the deletion request.</p><p>- Validation: Validates the entered Password (1) against the database. If incorrect, display an inline error message.</p><p>- Execution: If correct, execute a hard delete query to permanently remove sensitive health data from the database, terminate the active session, and log the user out.</p></th></tr></thead></table><h4><a id="_heading=h.nakfpejumfxl"></a></h4><h4><a id="_heading=h.ywz7izz0uddc"></a></h4><h4><a id="_heading=h.so5wwwnweg8h"></a>3.1.3 Retreat Package List Screen</h4><p><strong>[Content #1]</strong></p><ul><li>The UI features a horizontal search and multi-criteria filter bar (Duration, Goal, Price) fixed at the top of the section.</li><li>The main content area displays the available Retreat Packages in a responsive grid layout. Each package is contained within an individual card component featuring a thumbnail image, title, brief text description, starting price, and a call-to-action button.</li></ul><p></p><p><strong><img src=""></strong></p><p><strong>[Content #2: Brief descriptions of the screen/function, mapped to the relevant use cases]</strong></p><ul><li><strong>Description:</strong> This screen enables Guests to explore available Retreat Packages. Users can utilize multi-criteria filters to find itineraries that align with their personal wellness objectives.</li><li><strong>Mapped Use Case:</strong> UC06 - As a Guest, I want to browse available "Retreat Packages" and filter them by goals (e.g., Weight Loss, Stress Relief, Yoga).</li></ul><p></p><p><strong><em>[Content #3]</em></strong></p><table><thead><tr><th><p><strong>Field Name</strong></p></th><th><p><strong>Description</strong></p></th></tr><tr><th colspan="2"><p><strong>Field Group: Verification</strong></p></th></tr><tr><th><p>(1) Password Input</p></th><th><p>Data type: String. Required field. Input is masked by default (e.g., ***). Used to verify the user's identity before executing a destructive action.</p></th></tr><tr><th><p>(2) Toggle Visibility (Eye Icon)</p></th><th><p>Action: Toggles the input field (1) state between masked (hidden) and plain text (visible).</p></th></tr><tr><th colspan="2"><p><strong><em>Field Group: Actions</em></strong></p></th></tr><tr><th><p>(3) "Hủy" (Cancel) Button</p></th><th><p>Action: Closes the warning dialog, aborts the operation, and returns the user to the previous screen.</p></th></tr><tr><th><p>(4) "Xóa vĩnh viễn" (Permanent Delete) Button</p></th><th><p>Action: Submits the deletion request.</p><p>- Validation: Validates the entered Password (1) against the database. If incorrect, display an inline error message.</p><p>- Execution: If correct, execute a hard delete query to permanently remove sensitive health data from the database, terminate the active session, and log the user out.</p></th></tr></thead></table><p></p><h4><a id="_heading=h.k91wv6wcyorn"></a>3.1.4 Package Detail &amp; Checkout Screen</h4><p><strong>[Content #1: UI Layout (Mockup screen prototype)]</strong></p><ul><li>The layout is divided into two main columns to optimize the user experience.</li><li>The left column displays the comprehensive details of the selected Retreat Package, including a large hero image, a descriptive text block outlining the itinerary, and highlighted included experiences.</li><li>The right column features a persistent booking form containing date pickers, a Villa type dropdown, guest information input fields, a dynamic cost summary block, and a prominent checkout button at the bottom.</li></ul><p><img src=""></p><p><strong>[Content #2]</strong></p><ul><li><strong>Description:</strong> This screen displays in-depth details of a selected package. Here, the Guest finalizes their itinerary dates, selects their accommodation, and securely pays the deposit.</li><li><strong>Mapped Use Case:</strong> UC07 - As a Guest, I want to select a package, pick travel dates, choose a Villa type, and securely pay the deposit.</li></ul><p></p><p><strong>[Content #3]</strong></p><p></p><table><thead><tr><th><p><strong>Field Name</strong></p></th><th><p><strong>Description</strong></p></th></tr><tr><th colspan="2"><p><strong>Field Group: Booking Form</strong></p></th></tr><tr><th><p>(1) Check-in/Check-out Dates</p></th><th><p>Data type: LocalDate.</p><p><strong>Constraint:</strong> Past dates are disabled. Check-out date must strictly follow the Check-in date based on the package's predefined duration.</p></th></tr><tr><th><p>(2) Loại Villa (Villa Type)</p></th><th><p>Data type: Dropdown list.</p><p><strong>Constraint:</strong> The system must dynamically filter this list to only show Villa types that have actual availability during the selected date range (1).</p></th></tr><tr><th><p>(3) Guest Info (Name &amp; Email)</p></th><th><p>Data type: String. Should be auto-populated if the Guest is fully authenticated via SSO/Login.</p></th></tr><tr><th colspan="2"><p><strong><em>Field Group: Payment &amp; Checkout</em></strong></p></th></tr><tr><th><p>(4) Price Summary</p></th><th><p>Read-only calculation block. Dynamically updates based on the selected Villa Type. Displays Base Price, Tax (10%), Total, and the required 30% Deposit amount.</p></th></tr><tr><th><p>(5) "Thanh toán" (Pay Deposit) Button</p></th><th><p>Action:</p><p>- Integrates with and triggers the external Payment Gateway API (Stripe / VNPay Sandbox) to handle secure transactions.</p><p>- On Success: Generates a new Room_Booking_ID in the database and sets the booking status.</p></th></tr></thead></table><p></p><h4><a id="_heading=h.zav3kqbmwnll"></a>3.1.5 Arrivals &amp; Check-in Dashboard Screen</h4><p><strong>[Content #1]</strong></p><ul><li>The layout is divided into a fixed left navigation sidebar and a primary main content area on the right.</li><li>The main content area features a date header, a detailed Data Table listing all expected arrivals for the current date, and summary metric cards at the bottom.</li></ul><p></p><p><img src=""></p><p></p><p></p><p><strong>[Content #2]</strong></p><ul><li><strong>Description:</strong> This is the primary operational dashboard for Receptionists. The system automatically fetches and displays all reservations scheduled for arrival on the current day. Receptionists use this interface to assign physical room numbers and process the check-in workflow.</li><li><strong>Mapped Use Case:</strong> UC08 - As a Receptionist, I want to view a dashboard of expected arrivals and perform Check-In (assign a specific Villa room number).</li></ul><p></p><p></p><p></p><p></p><p><strong>[Content #3]</strong></p><p></p><table><thead><tr><th><p><strong>Field Name</strong></p></th><th><p><strong>Description</strong></p></th></tr><tr><th colspan="2"><p><strong>Field Group: Dashboard Overview</strong></p></th></tr><tr><th><p>(1) Sidebar Navigation</p></th><th><p>Navigation links for front-desk operations (e.g., Arrivals, Departures, Room Management).</p></th></tr><tr><th><p>(6) Summary Cards</p></th><th><p>Read-only numeric data. Dynamically counts and displays the number of Pending Check-ins, Completed Check-ins, and Ready Villas.</p></th></tr><tr><th colspan="2"><p><strong><em>Field Group: Arrivals Data Table</em></strong></p></th></tr><tr><th><p>(2) Guest &amp; Package Info</p></th><th><p>Data type: Text. Displays Guest Name, Booking ID, Retreat Package, and booked Villa Category.</p><p><strong>Strict RBAC Constraint:</strong> For the "Special Requests" column, the Backend query MUST mask/hide any Health or Dietary Allergy data for the Receptionist role. Only general service requests (e.g., Wheelchair, Extra pillow) are permitted to be shown.</p></th></tr><tr><th><p>(3) Số phòng (Room Assignment)</p></th><th><p>Data type: Dropdown List.</p><p><strong>Constraint:</strong> Upon clicking, the system must dynamically populate this list with specific room numbers that meet two conditions simultaneously: The room matches the booked Villa Category AND its physical status is currently "Vacant/Ready".</p></th></tr><tr><th><p>(4) Trạng thái (Status Badge)</p></th><th><p>Visual indicator of the booking status (e.g., Not Arrived, Waiting, Checked-in).</p></th></tr><tr><th><p>(5) "Check-in" Action Button</p></th><th><p>Action: Triggers the check-in execution.</p><p>- Validation: The Receptionist must select a Room Number (3) before submission.</p><p>- Execution: Updates the Booking status to "Checked-in" and concurrently updates the physical Villa status (UC09) to "Occupied" in the database</p></th></tr></thead></table><h4><a id="_heading=h.y234bqbs4y0c"></a>3.1.6 Guest Itinerary Timeline Screen</h4><p><strong>[Content #1]</strong></p><ul><li>The UI is structured as a vertical Timeline layout.</li><li>The top section contains the header and date navigation. The main body is split into a fixed time axis on the left and corresponding event detail cards on the right.</li></ul><p><img src=""></p><p><strong>[Content #2]</strong></p><ul><li><strong>Description:</strong> This screen provides Guests with a visual and detailed overview of all scheduled daily activities at the resort. It seamlessly aggregates data from various sub-systems (such as F&amp;B meal plans, Yoga classes, and Spa therapy sessions) into a single, continuous chronological flow.</li><li><strong>Mapped Use Case:</strong> UC10 - As a Guest, I want to view my full booking details and itinerary timeline.</li></ul><p><strong>[Content #3]</strong></p><p></p><table><thead><tr><th><p><strong>Field Name</strong></p></th><th><p><strong>Description</strong></p></th></tr><tr><th colspan="2"><p><strong>Field Group: Date Navigation</strong></p></th></tr><tr><th><p>(1) Date Selector / Display</p></th><th><p>Data type: LocalDate.</p><p>Action: Displays the current itinerary date (e.g., Monday, May 24). Allows users to click and navigate between different days within the duration of their booked Retreat Package.</p></th></tr><tr><th colspan="2"><p><strong><em>Field Group: Timeline Events</em></strong></p></th></tr><tr><th><p>(2) Time Milestone</p></th><th><p>Data type: LocalTime (e.g., 07:00, 09:30). Displays the start time of each scheduled event.</p></th></tr><tr><th><p>(3) Activity Card</p></th><th><p>Read-only container. Dynamically queries and aggregates cross-module data from the Spa scheduling (Module 3) and F&amp;B planning (Module 4) tables.</p></th></tr><tr><th><p>(4) Activity Title &amp; Icon</p></th><th><p>Data type: String &amp; Icon Asset.</p><p>Displays the title of the event (e.g., "Ăn sáng Detox", "Massage Thụy Điển") accompanied by a context-specific icon in the top right corner of the card.</p></th></tr><tr><th><p>(5) Activity Description</p></th><th><p>Data type: String. A brief descriptive text outlining the wellness activity or its benefits.</p></th></tr><tr><th><p>(6) Location</p></th><th><p>Data type: String.</p><p>Displays the physical venue retrieved from the master data (e.g., "Nhà hàng Thực dưỡng", "Shala Thiền", "Aura Spa").</p></th></tr></thead></table><p></p><h4><a id="_heading=h.t68b1hnepq1a"></a>3.1.7 Guest Spa Scheduler Screen</h4><p><strong>[Content #1]</strong></p><ul><li>The layout is organized into two distinct columns. The left column displays the selected service details and an interactive monthly Calendar component.</li><li>The right column features a responsive grid of available time slots and a fixed Booking Summary block at the bottom, containing the primary call-to-action button.</li></ul><p><img src=""></p><p><strong>[Content #2]</strong></p><ul><li><strong>Description:</strong> This interface empowers Guests to schedule their included Spa therapies. Crucially, the system acts as a background orchestrator, computing real-time resource availability (rooms and staff) to dynamically render valid time slots, thereby completely preventing overbooking scenarios.</li><li><strong>Mapped Use Case:</strong><ul><li>UC11 - As a Guest, I want to schedule included Spa sessions by selecting a date and time slot.</li><li>UC12 - As a System, I must automatically find an available slot by simultaneously matching ONE free Therapist AND ONE empty Treatment Room.</li></ul></li></ul><p></p><p><strong>[Content #3]</strong></p><table><thead><tr><th><p><strong>Field Name</strong></p></th><th><p><strong>Description</strong></p></th></tr><tr><th colspan="2"><p><strong>Field Group: Scheduling Inputs</strong></p></th></tr><tr><th><p>(1) Service Info</p></th><th><p>Read-only container. Displays the Service Name, Duration (e.g., 60 mins), and Price.</p></th></tr><tr><th><p>(2) Date Picker</p></th><th><p>Data type: LocalDate.</p><p><strong>Constraint:</strong> Past dates are strictly disabled. Selecting a valid date triggers an API request to the backend to fetch the corresponding availability for the Time Slot Grid (3).</p></th></tr><tr><th><p>(3) Time Slot Grid</p></th><th><p>Data type: LocalTime.</p><p><strong>Strict Business Rule:</strong> A time slot button is "Clickable/Available" IF AND ONLY IF the Backend query verifies the availability of (Count Room_ID &gt; 0) <strong>AND</strong> (Count Therapist_ID &gt; 0) for that exact duration. Otherwise, the slot is rendered as Disabled/Grayed-out (e.g., "19:00 Hết chỗ") to enforce 2-Dimensional Double-Booking Prevention.</p></th></tr><tr><th colspan="2"><p><strong><em>Field Group: Confirmation</em></strong></p></th></tr><tr><th><p>(4) Booking Summary</p></th><th><p>Read-only block. Dynamically updates based on inputs (2) and (3).</p><p><strong>Logic Note:</strong> The "Room" field (e.g., Lotus Suite 02) is auto-assigned by the system's matching algorithm (UC12); the guest does not manually select the room.</p></th></tr><tr><th><p>(5) "Xác nhận đặt lịch" (Confirm Booking) Button</p></th><th><p>Action: Submits the booking request.</p><p><strong>Backend Constraint:</strong> The execution must be encapsulated within a Database Transaction. The system must simultaneously lock both the allocated Therapist and Room resources to strictly prevent concurrency issues (Double-booking) during the write operation.</p></th></tr><tr><th><p>(5) Activity Description</p></th><th><p>Data type: String. A brief descriptive text outlining the wellness activity or its benefits.</p></th></tr></thead></table><p></p><h4><a id="_heading=h.9iqpsjuxqw1j"></a>3.1.8 Therapist Daily Schedule Screen</h4><p><strong>[Content #1]</strong></p><ul><li>The screen layout consists of a Header displaying the current operational date (e.g., Tuesday, June 2, 2026) alongside secondary action buttons.</li><li>Below the header are three prominent summary KPI cards.</li><li>The core element is a chronological Data Table detailing the therapist's assigned treatment sessions for the day.</li></ul><p><img src=""></p><p><strong>[Content #2]</strong></p><ul><li><strong>Description:</strong> This specialized dashboard empowers Spa Therapists to manage their daily shifts. It provides a structured view of upcoming appointments, grants secure access to permitted physical health records for treatment preparation, and allows therapists to update real-time session statuses.</li><li><strong>Mapped Use Case:</strong><ul><li>UC13 - As a Spa Therapist, I want to view my daily schedule and access specific medical notes of assigned guests.</li><li>UC14 - As a Spa Therapist, I want to mark a session as "Completed" or "No-Show".</li></ul></li></ul><p><strong>[Content #3]</strong></p><table><thead><tr><th><p><strong>Field Name</strong></p></th><th><p><strong>Description</strong></p></th></tr><tr><th colspan="2"><p><strong>Field Group: Dashboard Summary</strong></p></th></tr><tr><th><p>(1) Date Header</p></th><th><p>Data type: LocalDate. Defaults to the current system date.</p></th></tr><tr><th><p>(2) Summary Cards</p></th><th><p>Read-only dynamic metrics (Total sessions, Completed sessions, and currently Vacant rooms) calculated from the active dataset.</p></th></tr><tr><th colspan="2"><p><strong><em>Field Group: Schedule Data Table</em></strong></p></th></tr><tr><th><p>(3) Session Details</p></th><th><p>Read-only text. Displays Time Range, Guest Name, Treatment Name, and Assigned Room. This data is the direct output of the automated scheduling algorithm (UC12).</p></th></tr><tr><th><p>(4) "Xem Ghi Chú" (View Notes) Button</p></th><th><p>Action: Triggers a Modal dialog displaying the guest's medical notes.</p><p><strong>Strict RBAC Constraint:</strong> The Backend API serving this endpoint MUST implement Data Minimization. It is strictly restricted to returning physical treatment-related data (e.g., injuries, back pain) and MUST completely mask/exclude any dietary allergy information.</p></th></tr><tr><th><p>(5) Trạng thái (Status Dropdown)</p></th><th><p>Data type: Enum.</p><p>Allowed values: Đang chờ (Pending), Hoàn thành (Completed), Vắng (No-Show).</p><p>Action: Updating this field commits the state change to the database. Marking a session as "Completed" triggers the billing logic for the guest's Consolidated Folio (Module 5).</p></th></tr></thead></table><p></p><p></p><p></p><h4><a id="_heading=h.yqw9p14jua3z"></a>3.1.9 Manual Spa Booking Modal</h4><p></p><p><strong>[Content #1]</strong></p><ul><li>The interface is rendered as an overlay dialog (Modal) to maintain the user's current context, featuring a close (X) icon at the top right.</li><li>It contains sequential input fields for guest identification, service selection, scheduling, and a dynamic price summary block.</li></ul><p><img src=""></p><p><strong>[Content #2]</strong></p><ul><li><strong>Description:</strong> This operational function enables Receptionists to manually book additional, a-la-carte Spa therapies for in-house guests. Utilizing standard hospitality accounting mechanics, the system bypasses immediate point-of-sale payment and routes the charges directly to the guest's centralized room account.</li><li><strong>Mapped Use Case:</strong> UC15 - As a Receptionist, I want to manually book additional Spa services for guests and charge them to their Villa folio.</li></ul><p></p><p></p><p><strong>[Content #3]</strong></p><table><thead><tr><th><p><strong>Field Name</strong></p></th><th><p><strong>Description</strong></p></th></tr><tr><th colspan="2"><p><strong>Field Group: Guest &amp; Service Selection</strong></p></th></tr><tr><th><p>(1) Guest/Room Search Input</p></th><th><p>Data type: String (Autocomplete).</p><p><strong>Constraint:</strong> The backend query must filter and return only guests whose current reservation status is actively "Checked-in".</p></th></tr><tr><th><p>(2) Spa Service</p></th><th><p>Data type: Dropdown List. Populated from the Spa Master Data.</p></th></tr><tr><th colspan="2"><p><strong><em>Field Group: Scheduling</em></strong></p></th></tr><tr><th><p>(3) Date</p></th><th><p>Data type: LocalDate. Defaults to the current system date.</p></th></tr><tr><th><p>(4) Available Time Slots</p></th><th><p>Data type: LocalTime grid.</p><p><strong>Logic:</strong> Reuses the backend validation algorithm to render only time slots where both a Treatment Room and a Therapist are concurrently available</p></th></tr><tr><th colspan="2"><p><strong><em>Field Group: Folio Integration</em></strong></p></th></tr><tr><th><p>(5) Price Summary</p></th><th><p>Read-only calculation block. Displays Base Price, Applicable Taxes/Service Fees, and the Grand Total.</p></th></tr><tr><th><p>(6) "Xác nhận &amp; Ghi nợ vào Folio" (Confirm &amp; Post to Folio) Button</p></th><th><p>Action: Form submission.</p><p><strong>Strict Backend Constraint:</strong> This action must execute a dual-operation database transaction:</p><p>1. Insert a new Spa appointment record.</p><p>2. Post the financial charge to the central Guest Folio, utilizing the Room_Booking_ID as the primary foreign key linkage. This charge remains "Pending" until the Consolidated Billing process during Check-out (UC21).</p></th></tr></thead></table><p></p><p></p><h4><a id="_heading=h.3r22dhda72y7"></a>3.1.10 Personalized Menu &amp; A-la-carte Screen</h4><p><strong>[Content #1]</strong></p><ul><li>The layout is split into a main catalog area on the left and a persistent floating Cart/Summary block on the right.</li><li>The catalog area features top navigation tabs to switch between included package meals and extra a-la-carte options. Food items are displayed as detailed cards containing nutritional metrics.</li></ul><p><img src=""></p><p><strong>[Content #2]</strong></p><ul><li><strong>Description:</strong> This interface delivers a smart dining experience. The system automatically cross-references dish ingredients against the user's health profile to issue warnings and prevent the ordering of allergens. Furthermore, guests can seamlessly order premium items outside their standard package via the a-la-carte tab.</li><li><strong>Mapped Use Case:</strong><ul><li>UC16 - As a Guest, I want to pre-select my daily meals from a menu automatically filtered by the system based on my allergy/dietary profile.</li><li>UC19 - As a Guest, I want to order extra a-la-carte food/drinks outside my package and charge it to the Villa.</li></ul></li></ul><p></p><p><strong>[Content #3]</strong></p><table><thead><tr><th><p><strong>Field Name</strong></p></th><th><p><strong>Description</strong></p></th></tr><tr><th colspan="2"><p><strong>Field Group: Menu Navigation</strong></p></th></tr><tr><th><p>(1) Category Tabs</p></th><th><p>Action: Toggles the active dataset.</p><p>- Thực đơn của bạn (Your Menu): Renders items included in the Retreat Package (Zero cost).</p><p>- Gọi món ngoài (A-la-carte): Renders the premium menu with associated monetary values.</p></th></tr><tr><th colspan="2"><p><strong><em>Field Group: Food Items</em></strong></p></th></tr><tr><th><p>(2) Standard Food Card</p></th><th><p>Read-only elements: Image, Dish Name, Ingredients Description, Nutritional Facts (Calories, Macros), and an active "Chọn món" (Add to Cart) button.</p></th></tr><tr><th><p>(3) Allergy Warning Card</p></th><th><p><strong>Strict Logic Constraint:</strong> During data retrieval, the Backend must perform an intersection check between the dish's Ingredients array and the guest's Food_Allergies array. If a match occurs, the Frontend must render an alert overlay (e.g., "Cảnh báo dị ứng") and strictly disable the selection button to prevent the item from being added to the cart.</p></th></tr><tr><th colspan="2"><p><strong><em>Field Group: Cart &amp; Checkout</em></strong></p></th></tr><tr><th><p>(4) Cart Summary</p></th><th><p>Dynamic calculation block. Orders from the included menu calculate to 0 VND. Orders from the A-la-carte tab dynamically sum the Base Price + Service Fees.</p></th></tr><tr><th><p>(5) "Xác nhận đặt bàn" (Confirm Order) Button</p></th><th><p>Action: Submits the Order payload to the database.</p><p><strong>Folio Constraint:</strong> For any items sourced from the A-la-carte tab, the system must automatically post the calculated financial charge directly to the guest's centralized Folio account associated with their Room_Booking_ID</p></th></tr></thead></table><p></p><h4><a id="_heading=h.wecavgnffwd6"></a>3.1.11 Chef Dashboard (Kitchen Display System)</h4><p><strong>[Content #1]</strong></p><ul><li>The interface employs a Kanban board layout, segmented into three primary operational columns: Pending, In Progress, and Completed.</li><li>Each culinary order is represented as a dynamic Ticket/Card. These cards display room assignments, wait timers, itemized dish lists, and quick-action buttons. Critical allergy alerts are prominently anchored at the top of applicable cards in high-contrast red.</li></ul><p><img src=""></p><p><strong>[Content #2]</strong></p><ul><li><strong>Description:</strong> This real-time operational dashboard is utilized by Chefs and F&amp;B staff. It digitizes the ticket routing process, enabling the kitchen to monitor preparation workflows and strictly adhere to food safety constraints without violating patient medical privacy.</li><li><strong>Mapped Use Case:</strong><ul><li>UC17 - As a Chef, I want to view a "Daily Meal Prep Dashboard" aggregating orders and specific allergy alerts.</li><li>UC18 - As a Chef, I want to update the status of an order ticket (e.g., Preparing -&gt; Ready).</li><li>UC20 - As a System, I must mask the Guest's entire medical history from the Chef, displaying ONLY relevant "Food Allergies".</li></ul></li></ul><p></p><p><strong>[Content #3]</strong></p><table><thead><tr><th><p><strong>Field Name</strong></p></th><th><p><strong>Description</strong></p></th></tr><tr><th colspan="2"><p><strong>Field Group: Kanban Columns</strong></p></th></tr><tr><th><p>(1) Order Status Columns</p></th><th><p>Layout containers that group tickets by their current state: Pending, In_Progress, and Completed</p></th></tr><tr><th colspan="2"><p><strong><em>Field Group: Order Ticket Details</em></strong></p></th></tr><tr><th><p>(2) Allergy Alert Label</p></th><th><p>Data type: String/Alert Banner.</p><p><strong>Strict RBAC / Data Minimization Constraint:</strong> The Backend query servicing this UI MUST restrict the payload to "Dietary Allergies" only (e.g., peanuts, seafood). Physical medical records (e.g., back pain, hypertension) MUST be completely masked and excluded from this view.</p></th></tr><tr><th><p>(3) Order Meta Info</p></th><th><p>Read-only text. Displays Room Number, Guest Name, and a dynamic wait Timer tracking ticket aging (e.g., "Trễ 5p")</p></th></tr><tr><th><p>(4) Itemized List</p></th><th><p>Data type: Array of Objects. Displays dish names, quantities, and specific dietary modifications/notes associated with the items.</p></th></tr><tr><th><p>(5) State Mutation Buttons</p></th><th><p>Action Buttons ("Bắt đầu" / Start and "Hoàn thành" / Complete).</p><p>Action: Triggers a state update query in the database, subsequently migrating the ticket to the next logical Kanban column on the UI</p></th></tr></thead></table><p></p><p></p><h4><a id="_heading=h.290s0v2g88vy"></a>3.1.12 Consolidated Billing &amp; Check-out Screen</h4><p><strong>[Content #1]</strong></p><ul><li>The layout utilizes a 7:3 two-column structure. The wider left column provides a granular breakdown of all incurred charges, categorized logically by operational departments (Room, Spa, F&amp;B).</li><li>The narrower right column is a sticky sidebar displaying the grand total summary, payment method selectors, and the primary check-out execution button.</li></ul><p><img src=""></p><p><strong>[Content #2]</strong></p><ul><li><strong>Description:</strong> This screen digitizes the hotel night audit and central accounting processes. Functioning as a centralized Guest Folio, the system automatically aggregates all pending financial liabilities from various Point-of-Sale (POS) terminals across the resort into a single, comprehensive final invoice.</li><li><strong>Mapped Use Case:</strong><ul><li>UC21 - As a Receptionist, during Check-Out, I want to generate a Consolidated Bill summarizing the remaining Package Cost, extra Spa services, and extra F&amp;B orders.</li><li>UC22 - As a Receptionist, I want to process the final payment and change the Villa status to Vacant/Needs Cleaning.</li></ul></li></ul><p><strong>[Content #3]</strong></p><table><thead><tr><th><p><strong>Field Name</strong></p></th><th><p><strong>Description</strong></p></th></tr><tr><th colspan="2"><p><strong>Field Group: Consolidated Folio Details</strong></p></th></tr><tr><th><p>(1), (2), (3) Departmental Charge Blocks</p></th><th><p>Read-only Data Grids.</p><p><strong>Backend Logic:</strong> The system must automatically aggregate these records by executing cross-module queries (Modules 2, 3, and 4), strictly utilizing the Room_Booking_ID as the primary relational linkage.</p></th></tr><tr><th colspan="2"><p><strong><em>Field Group: Payment Processing</em></strong></p></th></tr><tr><th><p>(4) Payment Summary</p></th><th><p>Read-only calculation block. Displays the algebraic sum of the remaining Room balance, Spa charges, F&amp;B charges, and applicable Taxes/Fees</p></th></tr><tr><th><p>(5) Payment Method</p></th><th><p>Data type: Toggle/Radio selection (e.g., Credit Card, Bank Transfer).</p></th></tr><tr><th><p>(6) "Thanh toán &amp; Check-out" (Pay &amp; Check-out) Button</p></th><th><p>Action: Executes the final transaction and terminates the stay.</p><p><strong>Strict Business Constraint:</strong> The system must validate the status of all associated sub-orders. Guests CANNOT check-out if they have pending/unprocessed Spa or F&amp;B orders (the button must be disabled/throw an error).</p><p><strong>Post-execution:</strong> Upon successful payment, the system transitions the booking status to "Checked-out" and automatically updates the physical Villa status to Vacant/Needs Cleaning.</p></th></tr></thead></table><p></p><p>arts.</p></th></tr><tr><th colspan="2"><p><strong><em>Field Group: Data Visualizations</em></strong></p></th></tr><tr><th><p>(2) Revenue Breakdown</p></th><th><p>Display type: Donut / Pie Chart widget.</p><p>Display Logic: Visually represents the percentage (%) and absolute monetary value segmented by the primary revenue streams: Retreat Packages, Spa &amp; Therapy, and F&amp;B.</p></th></tr><tr><th><p>(3) Revenue Trend</p></th><th><p>Display type: Line / Bar Chart widget.</p><p>Display Logic: Illustrates the fluctuation of total revenue across a timeline (e.g., months). It also includes static KPI summaries such as Best Month and Monthly Average.</p></th></tr><tr><th colspan="2"><p><strong><em>Field Group: Transaction Data</em></strong></p></th></tr><tr><th><p>(4) Recent Transactions Table</p></th><th><p>Read-only Data Grid. Displays a paginated list of the most recent completed check-out transactions, detailing Guest Name, Service, Date, Status, and Amount.</p></th></tr><tr><th><p>(5) "Tải báo cáo CSV" (Export CSV) Button</p></th><th><p>Action: Triggers a backend service to export the dataset from table (4), based on the active filters, and downloads it to the user's device in .csv or .xlsx format.</p></th></tr></thead></table></div>

### 3.2 User Authentication

#### 3.2.1 Authentication & Login Screen

**\[Content #1\]**

- The user interface features a clean, centrally-aligned authentication card utilizing elegant serif typography.
- It accommodates a traditional credential-based input form, visually separated by an "OR" divider, followed by a seamless Single Sign-On (SSO) option via Google. A directional link for new user registration is anchored at the bottom.

**\[Content #2\]**

- **Description:** This screen serves as the primary security gateway to the application. It securely validates user identities and establishes active sessions. Users are provided the flexibility to authenticate via standard credentials or leverage Google's OAuth2 service for expedited access.
- **Mapped Use Case:** UC01 - Log in to the system (Includes traditional and SSO authentication).

**\[Content #3\]**

| **Field Name** | **Description** |
| --- | --- |
| **Field Group: Traditional Login** |     |
| --- |     | --- |
| (1) EMAIL | Data type: String. Required field with strict email format validation. |
| --- | --- |
| (2) MẬT KHẨU (Password) | Data type: String. Required field. User input is securely masked by default. |
| --- | --- |
| (3) "Quên mật khẩu?" (Forgot Password) Link | Action: Navigates the user to the Password Recovery workflow interface (UC04). |
| --- | --- |
| (4) "Đăng nhập" (Login) Button | Action: Submits the credential payload to the authentication endpoint.<br><br>**Security Constraint:** The backend must utilize secure hashing algorithms (e.g., BCrypt) for password verification. Upon successful authentication, the server provisions a valid JSON Web Token to the client. |
| --- | --- |
| **_Field Group: Alternative Authentication_** |     |
| --- |     | --- |
| (5) "Google" SSO Button | Action: Initializes the external OAuth2 authentication flow with Google.<br><br>**Logic:** Bypasses manual password entry. The backend securely decodes the received Google token to extract the unique Email identifier and seamlessly authorize system access |
| --- | --- |
| (6) "Đăng ký ngay" (Register Now) Link | Action: Directs unauthenticated users to the New Account Registration screen (UC03). |
| --- | --- |

#### 3.2.2 New Account Registration Screen

#### \[Content #1\]

#### The registration form interface is minimalist, maintaining visual and structural consistency with the Authentication screen.

#### Input fields employ a clean, stacked layout with borderless underlines. The primary call-to-action button is prominently displayed, followed by secondary navigation links at the footer.

#### \[Content #2\]

#### Description: This interface enables new guests to establish their secure identity profile within the resort's ecosystem. The registration payload undergoes strict data integrity validations to ensure account uniqueness and robust security standards before database persistence.

#### Mapped Use Case: UC03 - New account registration and basic profile management.

**\[Content #3\]**

| **Field Name** | **Description** |
| --- | --- |
| **Field Group: Header & Basic Information** |     |
| --- |     | --- |
| (1) Header & Message | Read-only text block. Displays the "Đăng ký" (Register) title and community onboarding subtext. |
| --- | --- |
| (2) HỌ VÀ TÊN (Full Name) | Data type: String. Status: Required. |
| --- | --- |
| (3) EMAIL | Data type: String. Status: Required. Frontend must enforce standard Regex validation.<br><br>**SQL Constraint:** The backend must handle database-level UNIQUE constraints. If a duplicate exists, the API must return a standardized 400 Bad Request error. |
| --- | --- |
| **_Field Group: Security Configuration_** |     |
| --- |     | --- |
| (4) MẬT KHẨU (Password) | Data type: String. Input is masked by default. Incorporates an interactive eye icon to toggle visibility state (plaintext/masked). |
| --- | --- |
| (5) XÁC NHẬN MẬT KHẨU (Confirm Password) | Data type: String.<br><br>**Frontend Logic:** Strict real-time validation is required. The string value must match field (4) exactly; otherwise, form submission is disabled. |
| --- | --- |
| **_Field Group: Actions & Navigation_** |     |
| --- |     | --- |
| (6) "ĐĂNG KÝ" (Register) Button | Action: Submits the user creation payload to the backend API.<br><br>**Backend Logic:** The server must execute one-way cryptographic hashing (e.g., BCrypt) on the plaintext password prior to executing the SQL INSERT statement. |
| --- | --- |
| (7) "Đăng nhập ngay" (Login Now) Link | Action: Navigates existing users back to the primary Authentication Screen (UC01). |
| --- | --- |

#### 

#### 3.2.3 Password Recovery Screens

**\[Content #1\]**

- Phase 1 (Request): Annotate (1) Header & Instructions, (2) Email Input, (3) "Gửi yêu cầu" Button, (4) "Quay lại đăng nhập" Link.
- Phase 2 (Reset): Annotate (5) Header & Instructions, (6) New Password Input, (7) Confirm Password Input, (8) "Cập nhật mật khẩu" Button, (9) "Quay lại đăng nhập" Link.
- The UI maintains visual consistency with the Authentication module, utilizing centered cards, elegant serif typography, and minimalist input fields.

**\[Content #2:\]**

- Description: This feature provides a secure, two-step workflow for account recovery. The system never exposes original credentials; instead, it dispatches a secure, single-use token via email (Screen 1), authorizing the user to define a new password (Screen 2).
- Mapped Use Case: UC04 - Forgot password, reset password, and security configuration.

**\[Content #3\]**

| **Field Name** | **Description** |
| --- | --- |
| **Field Group: Phase 1 - Request Reset Link** |     |
| --- |     | --- |
| (1) Header & Instructions | Read-only text. Displays "Khôi phục mật khẩu" (Password Recovery) and instructions. |
| --- | --- |
| (2) Email Input | Data type: String. Status: Required field. |
| --- | --- |
| (3) "Gửi yêu cầu" (Send Request) Button | Action: Submits the recovery request to the API.<br><br>**Core Backend Logic:** The system verifies the email existence. If valid, it generates a cryptographically secure Reset_Token (with a strict expiration, e.g., 15 minutes) and triggers an asynchronous email service containing the tokenized URI. |
| --- | --- |
| (4) "Quay lại..." (Back to Login) Link | Action: Navigates back to the Login Screen (UC01). |
| --- | --- |
| **_Field Group: Phase 2 - Set New Password_** |     |
| --- |     | --- |
| (5) Header & Instructions | Read-only text. Displays "Thiết lập mật khẩu mới" (Set New Password). This interface is exclusively accessed via the emailed URI. |
| --- | --- |
| (6) Mật khẩu mới (New Password) | Data type: String. Masked input with a toggle visibility icon. Enforces strict Password Policy configurations. |
| --- | --- |
| (7) Xác nhận mật khẩu (Confirm Password) | Data type: String. Masked input.<br><br>**Frontend Logic:** Must exactly match field (6) before form submission is enabled. |
| --- | --- |
| (8) "Cập nhật..." (Update Password) Button | Action: Submits the new password payload alongside the URL-extracted Token.<br><br>**Database Security:** The backend validates the Token's integrity. Upon success, it executes a secure hash of the new password, updates the record, and immediately **revokes the Token** to prevent Replay Attacks. |
| --- | --- |
| (9) "Quay lại..." (Back to Login) Link | Action: Navigates back to the Login Screen (UC01). |
| --- | --- |

### 3.3 Master Data

#### 3.3.1 Villa Status Management Screen

**\[Content #1\]**

- The interface employs a modern Dashboard layout utilizing interactive Cards to represent individual Villas.
- The top section highlights aggregate KPI counters for room states (Clean, Dirty, Maintenance) utilizing strict traffic-light color coding. The underlying grid renders detailed Villa cards that seamlessly merge interactive state mutation capabilities (dropdowns) with cross-departmental occupancy data (Guest details, timelines).

**\[Content #2\]**

- **Description:** This operational hub synchronizes workflows between the Front Desk and Housekeeping departments. By providing real-time physical status monitoring, the system acts as a strict guardrail, completely preventing operational anomalies such as allocating arriving guests to unserviced or out-of-order accommodations.
- **Mapped Use Case:** UC09 - Manage and update the physical status of Villas (Vacant, Occupied, Dirty/Needs Cleaning, Maintenance).

**\[Content #3\]**

**Field Description**

| **Field Name** | **Description** |
| --- | --- |
| **_Field Group: Dashboard Overview & Filters_** |     |
| --- |     | --- |
| (1) KPI Status Summary | IRead-only dynamic metrics. The backend executes COUNT queries to dynamically aggregate the volume of Villas in SẠCH (Clean), BẨN (Dirty), and BẢO TRÌ (Maintenance) states. |
| --- | --- |
| (2) Filters | Data type: Dropdowns. Enables list segmentation by Floor (TẦNG) or Villa Category (LOẠI VILLA). Mutating these parameters instantly triggers a backend API request to re-fetch the grid payload. |
| --- | --- |
| **_Field Group: Villa Card Details_** |     |
| --- |     | --- |
| (3) Villa Identification | Read-only text. Displays the primary key identifier (e.g., V01) and associated Room Category. |
| --- | --- |
| (4) Housekeeping Status Dropdown | Data type: Enum.<br><br>**Logic:** Empowers staff to mutate the physical state directly on the card. Transitioning a state from BẨN (Dirty) to SẠCH (Clean) commits the update to the DB and concurrently dispatches an event to unlock the room's availability for Front Desk Check-in operations. |
| --- | --- |
| (5) Occupancy Info & Alerts | Dynamic data block requiring cross-module relational queries (e.g., JOIN with Room_Booking).<br><br>**Business Rules:**<br><br>\- Active Booking: Renders "Đang ở" (Occupied), Guest Name, and Checkout timeline.<br><br>\- Impending Arrival: Renders "Khách sắp đến" (Arrival Expected).<br><br>\- No active/pending linkage: Renders "Trống" (Vacant).<br><br>\- Maintenance state active: Prominently renders technical fault notes (e.g., "Sửa máy lạnh" / AC Repair) in red. |
| --- | --- |

#### 3.3.2 Performance Reports & Export Screen

**\[Content #1\]**

- The interface utilizes a highly professional Admin Dashboard layout, leveraging adequate white space for optimal data readability.
- The screen is vertically divided into three functional zones: Report parameter configuration, high-level KPI summaries featuring Month-over-Month (MoM) growth indicators, and a granular, paginated data grid highlighting status alerts.

**\[Content #2\]**

- Description: This functionality provides Management with a robust analytics engine. The system automatically aggregates transactional data across all operational modules (Front Desk, Spa, F&B) to calculate room occupancy, total revenue, and service utilization rates. Users can preview these metrics dynamically or export them into document formats (PDF/Excel) for strategic planning.
- Mapped Use Case: UC25 - As a Manager, I want to export monthly Room Occupancy & Therapist Utilization reports to a file.

**\[Content #3\]**

**Field Description**

| **Field Name** | **Description** |
| --- | --- |
| **_Field Group: Report Configuration & Export_** |     |
| --- |     | --- |
| (1) "Xuất PDF" (Export PDF) Button | Action: Initiates the document generation pipeline.<br><br>**Core Backend Logic:** The server utilizes the currently filtered dataset and applies a document rendering library (e.g., iTextPDF in Java) to construct a branded table format. It returns a Byte Stream prompting an automatic .pdf download in the browser. |
| --- | --- |
| (2) "Tạo báo cáo mới" (Generate Report) Button | Action: Dispatches a GET request with the active parameters to the backend, triggering a recalculation and re-rendering of both the KPI Cards and the Data Grid below. |
| --- | --- |
| (2) Data Filters | Data type: Dropdown Lists.<br><br>\- Loại dữ liệu (Data Type): e.g., Total Revenue, Spa Efficiency, Occupancy Rate.<br><br>\- Khoảng thời gian (Timeframe): This Month, This Quarter, This Year.<br><br>\- Phân khúc (Segment): All Services, Retreat Packages, A-la-carte.<br><br>**Logic:** These selections formulate the exact parameter payload sent to the backend SQL queries. |
| --- | --- |
| **_Field Group: Performance Dashboard_** |     |
| --- |     | --- |
| (3) KPI Summary Cards | Read-only dynamic widgets. Displays core metrics: Revenue, Occupancy Rate (%), and Spa Utilization Count.<br><br>**Calculation Logic:** Incorporates a Trend Analysis indicator comparing current data against the previous month (MoM). Green denotes positive growth; Red denotes decline. |
| --- | --- |
| **_Field Group: Detailed Data Grid_** |     |
| --- |     | --- |
| (4) Data Preview Grid | Read-only Data Grid rendering granular record details.<br><br>Columns include: Date, Metric Name, Actual Value, Target, Trend, and Status. The "Trạng thái" (Status) column employs color-coded badging (e.g., TỐT/Good in Green, CẦN CHÚ Ý/Needs Attention in Red) for rapid cognitive processing. |
| --- | --- |
| (5) Pagination Controls | Navigation component.<br><br>**Logic:** Implements Server-side pagination (utilizing SQL Limit/Offset) to ensure memory optimization and performance when handling large datasets. Displays total record counts and current page location (e.g., "Page 1 / 8"). |
| --- | --- |

#### 

## 4\. Non-Functional Requirements - Đắc

### 4.1 External Interfaces

_\[This section provides information to ensure that the system will communicate properly with users and with external hardware or software/system elements.\]_

**ID**

**Interface**

**Requirements**

**EI-01**

**User Interface**

**The system shall provide responsive web-based interfaces for desktop and tablet devices.**

**EI-02**

**Navigation**

**Screen flow shall follow Left → Right and Top → Bottom principles.**

**EI-03**

**Consent Interface**

**Sensitive health-related screens shall display consent notices before data collection.**

**EI-04**

**Dashboard Interface**

**Dashboard shall support filtering, sorting, searching, and exporting.**

**EI-05**

**Validation Interface**

**Input validation messages shall appear immediately.**

**EI-06**

**System Messages**

**System shall follow predefined messages MSG-01 → MSG-15.**

**EI-07**

**Payment Gateway**

**Support deposit payment and final payment integration.**

**EI-08**

**Notification Service**

**Support email verification and appointment reminders.**

**EI-09**

**Reporting Interface**

**Support exporting reports and invoice generation.**

### 4.2 Quality Attributes

_\[List all the required system characteristics (quality attributes) specification. Some of the possible attributes are provided with the guide/descriptions are mentioned here\]_

#### 4.2.1 Usability

_\[This section includes all those requirements that affect usability. For example, specify the required training time for a normal user and a power user to become productive at particular operations specify measurable task times for typical tasks or base the new system’s usability requirements on other systems that the users know and like specify requirement to conform to common usability standards, such as IBM’s CUA standards Microsoft’s GUI standards\]_

| **ID** | **Requirement** | **Measurement** |
| --- | --- | --- |
| US-01 | Guest registration shall be simple and intuitive | ≤ 3 minutes |
| --- | --- | --- |
| US-02 | Retreat package booking shall be completed efficiently | ≤ 5 minutes |
| --- | --- | --- |
| US-03 | Receptionist shall complete check-in quickly | ≤ 3 minutes |
| --- | --- | --- |
| US-04 | Therapist shall update treatment status efficiently | ≤ 30 seconds |
| --- | --- | --- |
| US-05 | Chef shall locate dietary information quickly | ≤ 15 seconds |
| --- | --- | --- |
| US-06 | Manager shall access reports efficiently | ≤ 10 seconds |
| --- | --- | --- |
| US-07 | User interfaces shall remain visually consistent | Across all modules |
| --- | --- | --- |
| US-08 | Consent requirements shall be clearly displayed | Mandatory |
| --- | --- | --- |

#### 4.2.1.1 Training Requirements

| **User Role** | **Training Time** |
| --- | --- |
| Guest | No training required |
| --- | --- |
| Receptionist | ≤ 2 hours |
| --- | --- |
| Therapist | ≤ 1 hour |
| --- | --- |
| Administrator | ≤ 4 hours |
| --- | --- |

#### 

#### 4.2.1.2 Usability Standards

| **Standard Type** | **Requirement** |
| --- | --- |
| Accessibility | WCAG principles |
| --- | --- |
| Responsive Design | Supported |
| --- | --- |
| Navigation Consistency | Required |
| --- | --- |

#### 

#### 4.2.2.1 Performance

_\[The system’s performance characteristics are outlined in this section. Include specific response times. Where applicable, reference related Use Cases by name._

_Response time for a transaction (average, maximum)_

_Throughput, for example, transactions per second_

_Capacity, for example, the number of customers or transactions the system can accommodate_

_Resource utilization, such as memory, disk, communications, and so forth.\]_

| **ID** | **Requirement** | **Target** |
| --- | --- | --- |
| PF-01 | Average response time | ≤ 2 seconds |
| --- | --- | --- |
| PF-02 | Maximum response time | ≤ 5 seconds |
| --- | --- | --- |
| PF-03 | Booking confirmation (UC07) | ≤ 10 seconds |
| --- | --- | --- |
| PF-04 | Final payment processing (UC22) | ≤ 15 seconds |
| --- | --- | --- |
| PF-05 | Revenue dashboard loading (UC24) | ≤ 8 seconds |
| --- | --- | --- |
| PF-06 | Concurrent active users | 100 users |
| --- | --- | --- |
| PF-07 | Booking requests | 30 requests/minute |
| --- | --- | --- |
| PF-08 | Dashboard requests | 50 requests/minute |
| --- | --- | --- |
| PF-09 | Guest Accounts | 100,000 |
| --- | --- | --- |
| PF-10 | Bookings | 50,000 |
| --- | --- | --- |
| PF-11 | Audit Logs | 1,000,000 |
| --- | --- | --- |
| PF-12 | Payment Records | 500,000 |
| --- | --- | --- |
| PF-13 | CPU Utilization | ≤ 70% |
| --- | --- | --- |
| PF-14 | Memory Utilization | ≤ 8 GB |
| --- | --- | --- |
| PF-15 | Database Query | ≤ 1 second |
| --- | --- | --- |
| PF-16 | Encryption Overhead | ≤ 10% |
| --- | --- | --- |
| PF-17 | Monthly System Uptime | ≥ 99.5% |
| --- | --- | --- |
| PF-18 | Recovery Time | ≤ 30 minutes |
| --- | --- | --- |
| PF-19 | Transaction Data Loss | Not allowed |
| --- | --- | --- |

#### 4.2.2.2 Related Business Rules

| **Rule ID** | **Description** |
| --- | --- |
| BR-09 | Sensitive Data Encryption |
| --- | --- |
| BR-15 | Audit Trail |
| --- | --- |

#### 

## 5\. Requirement Appendix - My

### 5.1 Business Rules:

| **ID** | **Business Rule Name** | **Detailed Description (Constraints & Logic)** | **Reference (UC/Source)** |
| --- | --- | --- | --- |
| BR-01 | Booking Confirmation and Deposit Payment | A Retreat Package booking shall only be confirmed after the system receives a successful deposit payment result from the payment gateway. | UC07 |
| --- | --- | --- | --- |
| BR-02 | Villa Management and Allocation | Guests may only select a Villa Type when making a booking. The system shall only confirm a booking if sufficient capacity is available for the selected Villa Type during the requested period. A specific Villa shall be assigned by the Receptionist during the Check-in process based on actual Villa availability. | UC07, UC08 |
| --- | --- | --- | --- |
| BR-03 | Villa Status and Allocation Constraints | A Villa shall not be assigned to more than one active booking during the same period. Villas with a status of Maintenance or Out of Service shall not be allocated to guests. | UC08, UC09 |
| --- | --- | --- | --- |
| BR-04 | Dual Resource Spa Scheduling | A Spa appointment shall only be valid when both an available Therapist and an available Therapy Room exist at the requested time. The system shall prevent double booking of resources. | UC12 |
| --- | --- | --- | --- |
| BR-05 | Spa Service Eligibility and Update Control | Guests may only book Spa services included in their purchased Retreat Package. Only the assigned Therapist may update the treatment session status. Additional Spa services outside the package may only be added by a Receptionist and must be recorded in the Guest Folio. | UC11, UC14, UC15 |
| --- | --- | --- | --- |
| BR-06 | Automatic F&B Menu Filtering | The system shall automatically exclude menu items that contain allergens or conflict with the guest’s declared dietary preferences. Chefs and F&B Staff shall only have access to allergy and dietary information necessary for their work. | UC16, UC17, UC20 |
| --- | --- | --- | --- |
| BR-07 | Role-Based Access Control (RBAC) and Data Minimization | Therapists may only access health information required for treatment purposes. Chefs may only access food allergy and dietary preference information. Receptionists shall not have access to guest health records. Access control shall be enforced at the backend level. | UC03, UC13, UC17, UC20 |
| --- | --- | --- | --- |
| BR-08 | Consent for Sensitive Data Collection | The system shall obtain explicit consent from guests before collecting health-related or allergy information. Consent checkboxes shall be unchecked by default. | UC02 |
| --- | --- | --- | --- |
| BR-09 | Sensitive Data Encryption | Health information, allergies, dietary preferences, and personal identification data shall be encrypted when stored in the database. | UC02, UC08 |
| --- | --- | --- | --- |
| BR-10 | Right to Data Erasure | Guests may request the permanent deletion of their health and allergy information after their retreat stay has been completed. | UC05 |
| --- | --- | --- | --- |
| BR-11 | Guest Folio and Consolidated Billing | All Spa and F&B charges shall be recorded in the Guest Folio using the corresponding Booking_ID and included in the final bill. | UC15, UC19, UC21 |
| --- | --- | --- | --- |
| BR-12 | Check-out Constraints | Guests shall not be allowed to complete the Check-out process if any Spa or F&B charges remain unpaid. Any previously paid deposit shall be deducted from the final invoice. | UC21, UC22 |
| --- | --- | --- | --- |
| BR-13 | Reporting and Review Logic | Revenue and occupancy reports shall only include completed transactions. Only guests who have completed their retreat stay may submit reviews and ratings. Each booking may submit only one review. | UC23, UC24, UC25 |
| --- | --- | --- | --- |
| BR-14 | Guest Stay Registration Information | During Check-in, the system shall collect and store guest identification information to comply with accommodation registration regulations. | UC08, Residence Law 2020 |
| --- | --- | --- | --- |
| BR-15 | Audit Trail Management | The system shall maintain audit logs for critical activities such as login, health data access, booking, payment, and Check-out to support monitoring and traceability. | UC07, UC11, UC21, UC22 |
| --- | --- | --- | --- |
| BR-16 | Meal Order Status Workflow | Meal Order status shall only progress in the following sequence: Pending → Preparing → Ready for Delivery. Status reversal shall not be permitted. Only Chefs or F&B Staff may update Meal Order status. | UC18 |
| --- | --- | --- | --- |
| BR-17 | Spa Appointment Notification and Synchronization | After a Spa appointment is successfully booked, the system shall send confirmation and reminder notifications to the guest. Notification failures shall not invalidate a confirmed appointment. | UC11 |
| --- | --- | --- | --- |
| BR-18 | Authentication and Single Sign-On (SSO) | The system shall support authentication through Google and Facebook. Accounts registered via SSO must complete email verification before being allowed to book a Retreat Package. The system shall prevent duplicate account creation. | UC01 |
| --- | --- | --- | --- |

### 5.2 System Messages

| **#** | **Code** | **Type** | **Context** | **Message** |
| --- | --- | --- | --- | --- |
| 1   | MSG-01 | Success | Account registration successful | Registration successful. Please verify your email address. |
| --- | --- | --- | --- | --- |
| 2   | MSG-02 | Error | Login failed | Invalid email or password. |
| --- | --- | --- | --- | --- |
| 3   | MSG-03 | Success | Email verification successful | Your email has been verified. You may now log in. |
| --- | --- | --- | --- | --- |
| 4   | MSG-04 | Warning | Health data consent not provided | You must provide consent before submitting health information. |
| --- | --- | --- | --- | --- |
| 5   | MSG-05 | Success | Retreat Package booking successful | Retreat Package booking created successfully. |
| --- | --- | --- | --- | --- |
| 6   | MSG-06 | Error | Deposit payment failed | Deposit payment failed. Please try again. |
| --- | --- | --- | --- | --- |
| 7   | MSG-07 | Success | Check-in successful | Check-in completed successfully. |
| --- | --- | --- | --- | --- |
| 8   | MSG-08 | Error | No suitable Villa available | No available Villa could be found for allocation. |
| --- | --- | --- | --- | --- |
| 9   | MSG-09 | Success | Spa appointment booked successfully | Spa appointment booked successfully. |
| --- | --- | --- | --- | --- |
| 10  | MSG-10 | Error | Therapist or therapy room unavailable | No available Therapist or Therapy Room could be found. |
| --- | --- | --- | --- | --- |
| 11  | MSG-11 | Success | Meal order placed successfully | Meal order has been recorded successfully. |
| --- | --- | --- | --- | --- |
| 12  | MSG-12 | Warning | Meal contains allergens | Warning: This meal contains ingredients listed in the guest’s allergy profile. |
| --- | --- | --- | --- | --- |
| 13  | MSG-13 | Warning | Outstanding charges during check-out | Please settle all outstanding charges before checking out. |
| --- | --- | --- | --- | --- |
| 14  | MSG-14 | Success | Check-out completed successfully | Check-out completed successfully. |
| --- | --- | --- | --- | --- |
| 15  | MSG-15 | Success | Health data deleted successfully | Health data has been deleted successfully. |
| --- | --- | --- | --- | --- |
| 16  | MSG-16 | Success | Review submitted successfully | Thank you for submitting your review. |
| --- | --- | --- | --- | --- |
| 17  | MSG-17 | Success | Excel report exported successfully | Report exported successfully. |
| --- | --- | --- | --- | --- |
| 18  | MSG-18 | Error | Unauthorized access | You do not have permission to access this function. |
| --- | --- | --- | --- | --- |
| 19  | MSG-19 | Error | Unexpected system error | An unexpected system error has occurred. Please try again later. |
| --- | --- | --- | --- | --- |

###