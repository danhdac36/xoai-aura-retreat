

![](images/media/image17.png)

**`<span class="smallcaps">`SOFTWARE REQUIREMENT SPECIFICATION**

**Aura Moon (HOS-03)**

> ? Hanoi, Jan 2024 ?

**Table of Contents**

[I. Record of Changes 3](#i.-record-of-changes)

[II. Software Requirement Specification
4](#ii.-software-requirement-specification)

> [1. Overall Requirements 4](#overall-requirements)
>
> [1.1 Context Diagram 4](#context-diagram---my)
>
> [1.2 Main Business Processes 5](#main-business-processes---h?i)
>
> [1.3 User Requirements 5](#user-requirements---d?c)
>
> [1.4 System Functionalities 7](#system-functionalities---duong)
>
> [1.5 Entity Relationship Diagram
> 7](#entity-relationship-diagram---ng?c)
>
> [2. Use Case Specifications 8](#use-case-specifications---d?c)
>
> [2.1 \<\<Feature Name1\>\>
> 8](#authentication-sensitive-health-profile)
>
> [2.2 Xyz Feature 10](#_heading=h.ngzmqu5gb7v4)
>
> [3. Functional Requirements 11](#functional-requirements---h?i)
>
> [3.1 Feature Name1 11](#core-feature)
>
> [3.2 User Authentication 11](#user-authentication)
>
> [3.3 System Administration 12](#master-data)
>
> [4. Non-Functional Requirements
> 13](#non-functional-requirements---d?c)
>
> [3.1 External Interfaces 13](#external-interfaces)
>
> [3.2 Quality Attributes 13](#quality-attributes)
>
> [5. Requirement Appendix 14](#requirement-appendix---my)
>
> [5.1 Business Rules 14](#business-rules)
>
> [5.2 System Messages 14](#system-messages)
>
> [5.3 Other Requirements? 15](#section-17)

# I. Record of Changes

<table style="width:94%;">
<colgroup>
<col style="width: 10%" />
<col style="width: 8%" />
<col style="width: 13%" />
<col style="width: 61%" />
</colgroup>
<tbody>
<tr>
<td style="text-align: center;"><strong>Date</strong></td>
<td style="text-align: center;"><strong>A*<br />
M, D</strong></td>
<td style="text-align: center;"><strong>In charge</strong></td>
<td style="text-align: center;"><strong>Change Description</strong></td>
</tr>
<tr>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
</tr>
<tr>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
<td style="text-align: center;"></td>
</tr>
</tbody>
</table>

\*A - Added M - Modified D - Deleted

# II. Software Requirement Specification

## 1. Overall Requirements

### 1.1 Context Diagram - My

The Xoai Aura Retreat Management System is a wellness resort management
platform designed to support retreat package booking, villa
accommodation management, spa scheduling, dietary planning, billing, and
business reporting.

The context diagram below illustrates the system boundary and its
interactions with external entities, including Guests, Receptionists,
Spa Therapists/Yoga Instructors, F&B Staff, Administrators, and
Managers. The system also integrates with external services such as
Payment Gateway APIs, Authentication APIs, and Notification & Calendar
APIs.

These interactions enable the efficient management of retreat
operations, guest services, payment processing, and business analytics.

![](images/media/image24.png)

[`<u>`Context
Diagram `</u>`](https://drive.google.com/file/d/11_WfkEVhWSdekAlrh9CIz8mSRiV3k7bq/view?usp=sharing)

### 1.2 Main Business Processes - H?i

![](images/media/image35.png)

[`<u>`Main Business
Processes `</u>`](https://drive.google.com/file/d/1PoYklOGFGtD73upbl_p68I7gUDY_4eDJ/view?usp=drive_link)

<table>
<colgroup>
<col style="width: 8%" />
<col style="width: 15%" />
<col style="width: 44%" />
<col style="width: 10%" />
<col style="width: 21%" />
</colgroup>
<tbody>
<tr>
<td style="text-align: center;"><strong>Step #</strong></td>
<td style="text-align: center;"><strong>Step Name</strong></td>
<td style="text-align: center;"><strong>Detailed
Description</strong></td>
<td style="text-align: center;"><strong>Role</strong></td>
<td style="text-align: center;"><strong>Note</strong></td>
</tr>
<tr>
<td style="text-align: center;"><strong>1</strong></td>
<td style="text-align: center;">Publish driving course enrollment</td>
<td style="text-align: center;"><strong>1. Activity:</strong>
- Guest creates an account and fills out the "Health & Dietary
Profile" (medical conditions, allergies).
- System checks the UI to ensure NO consent checkboxes are
pre-checked.
<strong>2. Input:</strong> Email, Password, Sensitive medical/allergy
data.
<strong>3. Output:</strong> User account, Health profile (encrypted
in DB).</td>
<td style="text-align: center;">Guest, System</td>
<td style="text-align: center;"><strong>Crucial:</strong> Must strictly
comply with Decree 356/2025 regarding sensitive data.</td>
</tr>
<tr>
<td style="text-align: center;"><strong>2</strong></td>
<td style="text-align: center;">Retreat Package Booking &
Deposit</td>
<td style="text-align: center;"><strong>1. Activity:</strong>
- Guest filters and selects a Retreat Package by goal, chooses dates
and Villa type.
- Redirects to Payment Gateway (Stripe/VNPay) to pay the deposit.
<strong>2. Input:</strong> Retreat_Package_ID, Arrival/Departure
Dates, Villa Type, Card details.
<strong>3. Output:</strong> Booking Record, Successful deposit
payment status.</td>
<td style="text-align: center;">Guest, System</td>
<td style="text-align: center;">When clicking the "register for course"
button, user authentication is required</td>
</tr>
<tr>
<td style="text-align: center;"><strong>3</strong></td>
<td style="text-align: center;">Check-in & Room Assignment</td>
<td style="text-align: center;"><strong>1. Activity:</strong>
- Receptionist views the expected guest list, assigns a physical
Villa number.
- Collects guest's ID/CCCD info for temporary residence
declaration.
- System encrypts the ID and changes room status.
<strong>2. Input:</strong> Booking ID, ID/Passport details, Physical
Villa number.
<strong>3. Output:</strong> Villa Status: "Occupied", ID data
securely stored.</td>
<td style="text-align: center;">Receptionist, System</td>
<td style="text-align: center;">Complies with Residence Law 2020.
Receptionist MUST NOT view the guest's medical data.</td>
</tr>
<tr>
<td style="text-align: center;"><strong>4</strong></td>
<td style="text-align: center;">Spa Scheduling</td>
<td style="text-align: center;">1. <strong>Activity</strong>:
- Guest selects date/time for a Spa session.
- System runs a DB locking transaction: Finds ONE available therapist
AND ONE available room simultaneously. If available, confirms the
schedule and calls Google Calendar API to notify the guest.
2. <strong>Input</strong>: Booking ID, Spa_Service_ID, Desired time
slot.
3. <strong>Output</strong>: Confirmed Spa schedule, Reminder email
(sent 1h prior).</td>
<td style="text-align: center;">Guest, System</td>
<td style="text-align: center;"><strong>Complex Logic:</strong>
2-Dimensional Double-Booking prevention constraint</td>
</tr>
<tr>
<td style="text-align: center;"><strong>5</strong></td>
<td style="text-align: center;">Spa Treatment Execution</td>
<td style="text-align: center;"><strong>1. Activity:</strong>
- Therapist views their work schedule and reads physical health notes
(other data is hidden).
- Performs treatment and updates the session status.
- System automatically accumulates charges (if the guest uses extra
off-package services) into the Guest Folio.
<strong>2. Input:</strong> Spa Schedule, Physical therapy notes.
<strong>3. Output:</strong> Status: "Completed/No-Show", Updated
Guest Folio.</td>
<td style="text-align: center;">Spa Therapist, System</td>
<td style="text-align: center;"><strong>RBAC:</strong> Therapist MUST
NOT view the guest's dietary/allergy information.</td>
</tr>
<tr>
<td style="text-align: center;"><strong>6</strong></td>
<td style="text-align: center;">F&B Meal Ordering</td>
<td style="text-align: center;"><strong>1. Activity:</strong>
- System cross-references the "Dietary Profile" to filter out menu
items containing allergens.
- Guest selects daily meals from this pre-filtered safe menu.
<strong>2. Input:</strong> Original Menu, Guest's allergy data.
<strong>3. Output:</strong> Safe Menu, F&B Order.</td>
<td style="text-align: center;">Guest, System</td>
<td style="text-align: center;">Applies the Data Minimization
principle</td>
</tr>
<tr>
<td style="text-align: center;"><strong>7</strong></td>
<td style="text-align: center;">Culinary Preparation</td>
<td style="text-align: center;"><strong>1. Activity:</strong>
- Chef opens the Dashboard to view aggregated meal orders and
"allergy alerts".
- Chef prepares the dish and updates the status (Preparing ->
Ready to deliver).
<strong>2. Input:</strong> F&B Order, Food allergy alerts.
<strong>3. Output:</strong> Updated meal status, A-la-carte charges
billed to Guest Folio.</td>
<td style="text-align: center;">Chef / F&B, System</td>
<td style="text-align: center;"><strong>RBAC:</strong> Chef MUST NOT
view the guest's physical medical records</td>
</tr>
<tr>
<td style="text-align: center;"><strong>8</strong></td>
<td style="text-align: center;">Check-out & Consolidated Bill</td>
<td style="text-align: center;"><strong>1. Activity:</strong>
- Receptionist clicks Check-out. System scans to check if there are
any pending/unpaid Spa/F&B orders.
- If clear, the system aggregates: Remaining Package fee + Extra
services into 1 Consolidated Bill.
- Receptionist collects payment and releases the room.
<strong>2. Input:</strong> Guest Folio (Room + Spa + F&B).
<strong>3. Output:</strong> Final Bill, Villa Status: "Needs
Cleaning".</td>
<td style="text-align: center;">Receptionist, System</td>
<td style="text-align: center;"><strong>Constraint:</strong> Guest
CANNOT check out if there are pending orders.</td>
</tr>
<tr>
<td style="text-align: center;"><strong>9</strong></td>
<td style="text-align: center;">Review & Data Deletion</td>
<td style="text-align: center;"><strong>1. Activity:</strong>
- Guest submits a Retreat quality review form.
- Guest exercises their "Right to be forgotten". System executes a
command to permanently wipe medical and allergy records from the DB.
<strong>2. Input:</strong> Rating/Review, Data deletion request.
<strong>3. Output:</strong> System review record, User profile
"cleansed" of sensitive data</td>
<td style="text-align: center;">Guest, System</td>
<td style="text-align: center;">Ensures absolute privacy after the
retreat concludes.</td>
</tr>
</tbody>
</table>

### 1.3 User Requirements - ??c

#### 1.3.1 Actors

*\[An actor is someone/something that interacts with the system.*

- *The only external entities that interact with the system*
- *?Actors are outside the system and not part of it*
- *?A user is an individual, whereas an actor represents the role played
  by all users of the same type*
- *There are other types of actors in addition to or in place of human
  actors: external systems, I/O devices, or timers*

*Following are some questions you might ask to help user representatives
identify actors*

- *Who (or what) is notified when something occurs within the system?*
- *Who (or what) provides information or services to the system?*
- *Who (or what) helps the system respond to and complete a task?*

*This part gives the description of system actors, you can follow the
table form as below\]*

<table style="width:96%;">
<colgroup>
<col style="width: 4%" />
<col style="width: 18%" />
<col style="width: 72%" />
</colgroup>
<tbody>
<tr>
<td style="text-align: center;"><strong>#</strong></td>
<td><strong>Actor</strong></td>
<td><strong>Description</strong></td>
</tr>
<tr>
<td style="text-align: center;"><blockquote>
1
</blockquote></td>
<td>Guest / Customer</td>
<td>A customer who uses the system to register, log in, complete health
and dietary profiles, browse wellness packages, book villas, schedule
spa/treatment sessions, pre-select meals, view itinerary, make payments,
and submit reviews.</td>
</tr>
<tr>
<td style="text-align: center;"><blockquote>
2
</blockquote></td>
<td>Receptionist</td>
<td>A front-desk staff member who manages guest check-in/check-out,
assigns physical villas, manages villa status, books additional spa
services for guests, and processes consolidated invoices and final
payments.</td>
</tr>
<tr>
<td style="text-align: center;"><blockquote>
3
</blockquote></td>
<td>Spa Therapist / Yoga Trainer</td>
<td>A service provider who views daily treatment schedules, checks
treatment-relevant health notes, and updates treatment session status
such as Completed or No-Show.</td>
</tr>
<tr>
<td style="text-align: center;"><blockquote>
4
</blockquote></td>
<td>Chef / F&B Staff</td>
<td>A food and beverage staff member who views daily meal preparation
dashboards, checks food allergy alerts, prepares personalized meals, and
updates meal order status.</td>
</tr>
<tr>
<td style="text-align: center;"><blockquote>
5
</blockquote></td>
<td>Administrator</td>
<td>A system administrator who manages staff accounts, assigns user
roles, configures role-based access control, and manages master data
such as villa types, spa services, retreat packages, and staff
records.</td>
</tr>
<tr>
<td style="text-align: center;"><blockquote>
6
</blockquote></td>
<td>Resort Manager</td>
<td>A management user who monitors revenue dashboards, reviews business
performance, and exports monthly reports on room occupancy and therapist
utilization.</td>
</tr>
<tr>
<td style="text-align: center;"><blockquote>
7
</blockquote></td>
<td>Payment Gateway</td>
<td>An external payment service such as Stripe, VNPay, or PayPal that
processes deposit payments and final payments securely.</td>
</tr>
<tr>
<td style="text-align: center;"><blockquote>
8
</blockquote></td>
<td>Calendar / Notification Service</td>
<td>An external service such as Google Calendar or SendGrid that
synchronizes spa/yoga schedules and sends reminder notifications to
guests.</td>
</tr>
<tr>
<td style="text-align: center;"><blockquote>
9
</blockquote></td>
<td>SSO Provider</td>
<td>An external authentication provider such as Google Identity or
Facebook Login that supports secure single sign-on for guests.</td>
</tr>
</tbody>
</table>

#### 1.3.2 Use Cases (UC)

*\[A use case (UC) describes a sequence of interactions between a system
and an external actor that results in the actor being able to achieve
some outcome of value. The names of use cases are always written in the
form of a verb followed by an object. Select strong, descriptive names
to make it evident from the name that the use case will deliver
something valuable for some user.*

*Following are some questions you might ask to help user representatives
identify use cases*

- *What will the actor use the system for?*
- *Will the actor create, store, change, remove, or read data in the
  system?*
- *Will the actor need to inform the system about external events or
  changes?*
- *Will the actor need to be informed about certain occurrences in the
  system?*

*This part describes the use cases you could define, you can follow the
table form as below\]*

|              |                                                           |                               |                                                                                                                                      |
| :----------: | --------------------------------------------------------- | ----------------------------- | ------------------------------------------------------------------------------------------------------------------------------------ |
| **ID** | **Use Case**                                        | **Feature**             | **Use Case Description**                                                                                                       |
|      01      | Register, Verify Email and Log In                         | Authentication1               | The Guest creates an account, verifies email, and logs in securely using system credentials or SSO.                                  |
|      02      | Complete Health and Dietary Profile                       | Health Profile Management     | The Guest provides health conditions, allergies, and dietary preferences with explicit consent.                                      |
|      03      | Manage Staff Accounts and Assign Roles                    | User and Role Management      | The Administrator creates staff accounts and assigns strict roles such as Therapist, Chef, and Receptionist.                         |
|      04      | Manage Master Data                                        | Master Data Management        | The Administrator manages villa types, spa services, retreat packages, and staff records.                                            |
|      05      | Delete Sensitive Health Data                              | Data Privacy Management       | The Guest requests permanent deletion of sensitive health and allergy data after the stay.                                           |
|      06      | Browse Wellness Packages                                  | Package Browsing              | The Guest browses available wellness packages and filters them by goals such as Detoxification, Yoga, Stress Relief, or Weight Loss. |
|      07      | Book Wellness Package and Pay Deposit                     | Booking and Payment           | The Guest selects a package, arrival date, villa type, and pays a secure deposit.                                                    |
|      08      | Check In Guest                                            | Reception Management          | The Receptionist views expected arrivals, checks in guests, assigns a specific villa, and collects required identity information.    |
|      09      | Manage Villa Status                                       | Villa Management              | The Receptionist updates villa status such as Available, Occupied, Under Maintenance, or Cleaning Required.                          |
|      10      | View Booking Details and Itinerary Timeline               | Booking Tracking              | The Guest views booking details, villa information, spa schedule, meal plan, and billing timeline.                                   |
|      11      | Schedule Spa/Treatment Session                            | Spa Scheduling                | The Guest schedules included spa or therapy sessions by selecting a date and time slot.                                              |
|      12      | Find Available Therapist and Treatment Room               | Automatic Scheduling          | The System automatically checks both therapist availability and treatment room availability before confirming a session.             |
|      13      | View Daily Work Schedule                                  | Therapist Schedule Management | The Spa Therapist or Yoga Trainer views assigned daily sessions and treatment-relevant health notes.                                 |
|      14      | Update Treatment Session Status                           | Treatment Management          | The Spa Therapist marks a session as Completed or No-Show.                                                                           |
|      15      | Book Additional Spa Service                               | Additional Service Booking    | The Receptionist manually books extra spa services for guests and posts the charge to the guest folio.                               |
|      16      | Pre-select Daily Meals                                    | Meal Management               | The Guest selects daily meals from a personalized menu filtered by allergies and dietary restrictions.                               |
|      17      | View Daily Meal Preparation Dashboard                     | F&B Dashboard                 | The Chef or F&B Staff views aggregated meal orders and relevant food allergy alerts for the selected date.                           |
|      18      | Update Meal Order Status                                  | Meal Order Management         | The Chef updates meal order status from Preparing to Ready for Delivery.                                                             |
|      19      | Order A-la-carte Food and Beverage                        | Additional F&B Service        | The Guest orders additional food or beverages outside the package and the system posts the charge to the folio.                      |
|      20      | Enforce Data Minimization for F&B Staff                   | Data Privacy and RBAC         | The System restricts kitchen staff from viewing medical history and only displays food allergies and dietary restrictions.           |
|      21      | Generate Consolidated Invoice                             | Billing Management            | The Receptionist generates a consolidated invoice including remaining package charges, additional spa services, and F&B orders.      |
|      22      | Process Final Payment and Complete Check-out              | Checkout and Payment          | The Receptionist processes the final payment and updates villa status after successful check-out.                                    |
|      23      | Submit Post-stay Review and Rating                        | Feedback Management           | The Guest submits a review and rating after completing the stay.                                                                     |
|      24      | View Revenue Analytics Dashboard                          | Analytics and Reporting       | The Resort Manager views revenue charts categorized by package, spa, and F&B income.                                                 |
|      25      | Export Monthly Occupancy and Therapist Utilization Report | Report Export                 | The Resort Manager exports monthly reports on room occupancy and therapist utilization to Excel.                                     |

#### 1.3.2 Use Case Diagrams

*In this section, you need to provide the UC diagram(s) to show the
actor-UCs and UC-UC relationships like the sample below. You can have
multiple UC diagrams for the system, each diagram is for one actor or
one workflow\]*

##### 1.3.2.1 UCs for Guest

![](images/media/image13.png)

##### 1.3.2.2 UCs for User

![](images/media/image18.png)

##### 1.3.2.3 UCs for Receptionist

![](images/media/image14.png)

##### 1.3.2.4 UCs for Spa Therapist / Yoga Trainer

![](images/media/image19.png)

##### 1.3.2.5 UCs for F&B Staff / Chef

![](images/media/image20.png)

##### 1.3.2.6 UCs for Administrator / Manager

![](images/media/image21.png)

##### [`<u>`Link `</u>`](https://app.diagrams.net/#G1YncpWgVmU244V3KrwqgE1e0nYMXlNedF#%7B%22pageId%22%3A%22eFBbMIPbn18NP5xC_-qE%22%7D)

### 1.4 System Functionalities - Duong

*\[Provide functionality overview of software system: screen flow,
screen descriptions, system user roles, screen authorization, non-screen
functions, ERD\]*

#### 1.4.1 Screens Flow

*\[This part shows the system screens and the relationship among
screens. You can draw the Screens Flow for the system in the form of
diagram as below.\]*

##### 1.4.1.1 User Screen

![](images/media/image6.png)

[`<u>`Link `</u>`](https://drive.google.com/file/d/1U4d7F3pLs_-YoxhPNGxx9ETU0EttOFbf/view?usp=sharing)

##### 1.4.1.2 Spa Therapist Screen

![](images/media/image25.png)

[`<u>`Link `</u>`](https://drive.google.com/file/d/1AnkOjnU7ZvZUiBOPnfWBZjzMf3UO-xXm/view?usp=sharing)

##### 1.4.1.3 F&B Screen

![](images/media/image22.png)

[`<u>`Link `</u>`](https://drive.google.com/file/d/1mJMnmUsTXdhT-t4qXD-jXv278YJXzzHM/view?usp=sharing)

##### 1.4.1.4 Receptionist Screen

![](images/media/image28.png)

[`<u>`Link `</u>`](https://drive.google.com/file/d/1wuPfRU24YecTAOEjnpYzZbvMYWqMera_/view?usp=sharing)

##### 1.4.1.5 Admin Screen

![](images/media/image12.png)

[`<u>`Link `</u>`](https://drive.google.com/file/d/1dZef4KxB4NK_VIhdDVCAE3O1F0CnfmUC/view?usp=sharing)

##### 1.4.1.6 All System

![](images/media/image26.png)

[`<u>`Screen
Flow `</u>`](https://drive.google.com/file/d/1IXyWIHj3ku43vv0RJHPECeEu1_UzhsSk/view?usp=sharing)

#### 1.4.2 Screen Authorization

*\[Provide the system roles authorization to the system features (down
to screens, and event to the screen activities if applicable) in the
table form as below ? replace Role-Name1, Role-Name2,? with your
specific system user role names\]*

|                                 |                |                        |                    |                      |                |
| :------------------------------ | :-------------: | :--------------------: | :-----------------: | :------------------: | :-------------: |
| **Screen**                | **Guest** | **Receptionist** | **Therapist** | **F&B / Chef** | **Admin** |
| Home Page                       |        X        |           X           |          X          |          X          |        X        |
| About Us                        |        X        |           X           |          X          |          X          |        X        |
| Contact Us                      |        X        |           X           |          X          |          X          |        X        |
| Packages List                   |        X        |           X           |          X          |          X          |        X        |
| Package Detail                  |        X        |           X           |          X          |          X          |        X        |
| Login Screen                    |        X        |           X           |          X          |          X          |        X        |
| Register Screen                 |        X        |                        |                    |                      |                |
| Book Now\[Action\]              |        X        |                        |                    |                      |                |
| Health & Dietary Profile        |        X        |                        |                    |                      |                |
| Payment                         |        X        |                        |                    |                      |                |
| Success Page                    |        X        |                        |                    |                      |                |
| Guest Dashboard                 |        X        |                        |                    |                      |                |
| Itinerary Timeline              |        X        |                        |                    |                      |                |
| Spa Scheduling                  |        X        |                        |                    |                      |                |
| A la-carte Menu                 |        X        |                        |                    |                      |                |
| Dietary Menu                    |        X        |                        |                    |                      |                |
| My Profile                      |        X        |                        |                    |                      |                |
| Update/Delete Profile           |        X        |                        |                    |                      |                |
| Review & Rating                 |        X        |                        |                    |                      |                |
| Receptionist Dashboard          |                |           X           |                    |                      |                |
| Expected Arrival List           |                |           X           |                    |                      |                |
| Check-in Form                   |                |           X           |                    |                      |                |
| Villa Status Management         |                |           X           |                    |                      |                |
| Update Villa Status\[Action\]   |                |           X           |                    |                      |                |
| Manual Spa Booking\[Modal\]     |                |           X           |                    |                      |                |
| Checkout Management             |                |           X           |                    |                      |                |
| Invoice Detail                  |                |           X           |                    |                      |                |
| Process Payment                 |                |           X           |                    |                      |                |
| Therapist Dashboard             |                |                        |          X          |                      |                |
| Daily Schedule                  |                |                        |          X          |                      |                |
| Session Detail                  |                |                        |          X          |                      |                |
| View Health Notes\[Modal\]      |                |                        |          X          |                      |                |
| Update Session Status\[Action\] |                |                        |          X          |                      |                |
| History                         |                |                        |          X          |                      |                |
| F&B / Chef Dashboard            |                |                        |                    |          X          |                |
| Daily Meal Prep Board           |                |                        |                    |          X          |                |
| A-la-carte Orders Board         |                |                        |                    |          X          |                |
| Update Prep Status\[Action\]    |                |                        |                    |          X          |                |
| Admin Dashboard                 |                |                        |                    |                      |        X        |
| Staff Accounts Management       |                |                        |                    |                      |        X        |
| Assign Roles\[Modal\]           |                |                        |                    |                      |        X        |
| Master Data Management          |                |                        |                    |                      |        X        |
| Villas Management               |                |                        |                    |                      |        X        |
| Packages Management             |                |                        |                    |                      |        X        |
| Spa Services Management         |                |                        |                    |                      |        X        |
| Revenue Analytics               |                |                        |                    |                      |        X        |
| Package Revenue Chart           |                |                        |                    |                      |        X        |
| Spa Revenue Chart               |                |                        |                    |                      |        X        |
| F&B Revenue Chart               |                |                        |                    |                      |        X        |
| Export Center                   |                |                        |                    |                      |        X        |
| Daily ID Report                 |                |                        |                    |                      |        X        |
| Excel Reports                   |                |                        |                    |                      |        X        |

#### 1.4.3 Non-UI Functions

*\[Provide the descriptions for the non-screen system functions, i.e
batch/cron job, service, API, etc.\]*

|              |                  |                              |                                                          |
| :----------: | :---------------: | :--------------------------: | :------------------------------------------------------: |
| **\#** | **Feature** |  **System Function**  |                  **Description**                  |
|      1      |  Authentication  |       Google OAuth API       |      Allows users to sign in using Google accounts.      |
|      2      |      Payment      |  VNPay Payment Gateway API  |         Processes online payments through VNPay.         |
|      3      |      Payment      | Payment Verification Service | Verifies transaction status and updates booking records. |
|      4      |  Data Management  |   Database Backup Service   |           Performs scheduled database backups.           |

### 1.5 Entity Relationship Diagram - Ng?c

***1.5.1 Entity Relationship Diagram***

*\[ the **ERD** using the Crow-Foot notation\]*

![](images/media/image1.png)

[`<u>`Figure x -
ERD `</u>`](https://drive.google.com/file/d/1oWAww-BpAlODydsSWfGnFlNlPFawkdOB/view?usp=sharing)

***1.5.2 Entities Description***

| **\#** | **Entity**        | **Description**                                                                 |
| ------------ | ----------------------- | ------------------------------------------------------------------------------------- |
| 1            | User                    | Stores personal information and account details of users in the system.               |
| 2            | Role                    | Defines user roles (e.g., guest, staff, administrator).                               |
| 3            | Consent                 | Stores the status and version of user consent.                                        |
| 4            | Physical_Health_Profile | Stores physical health information of users, such as medical conditions and injuries. |
| 5            | Dietary_Profile         | Stores information regarding dietary preferences and food allergies of users.         |
| 6            | Retreat_Package         | Contains the catalog of retreat packages offered by the facility.                     |
| 7            | Villa_Type              | Categorizes villa types, including capacity and pricing information.                  |
| 8            | Villa                   | Lists specific villa units associated with different types.                           |
| 9            | Booking                 | Manages guest reservation details.                                                    |
| 10           | Review                  | Stores feedback and ratings provided by guests after their stay.                      |
| 11           | Guest_Folio             | A summary table of guest expenses and costs during a booking period.                  |
| 12           | Folio_Item              | Contains detailed line items (charges) associated with a specific folio.              |
| 13           | Payment                 | Records detailed information regarding guest payment transactions.                    |
| 14           | Spa_Booking             | Manages individual spa service appointments for guests.                               |
| 15           | Spa_Service             | Catalog of spa services available for booking.                                        |
| 16           | Treatment_Room          | Manages the treatment rooms used for spa services.                                    |
| 17           | Therapist               | Store information and status of therapists when they are at work.                     |
| 18           | Schedule                | Store schedule and slot to manage session of treatment room and therapist             |
| 19           | Meal_Order              | Manages food orders placed by guests.                                                 |
| 20           | Meal_Order_Item         | Details the specific food items included in a meal order.                             |
| 21           | Menu_Item               | Catalog of food items available on the menu.                                          |

## 2. Use Case Specifications - ??c

*\[Provide specifications for the use cases (UCs) those are covered in
the system. The UCs are grouped by the system features and even sub
features. **You just need to provide UC specifications for complex UCs
involving in the main workflows (business processes)**. Other UCs (i.e
CRUD or data-viewing UCs) are simple, and you just need to refer the
descriptions in the Functional Requirement (part 3) below)\]*

### 2.1 Authentication & Sensitive Health Profile

<table>
<colgroup>
<col style="width: 19%" />
<col style="width: 29%" />
<col style="width: 22%" />
<col style="width: 27%" />
</colgroup>
<tbody>
<tr>
<td style="text-align: right;">ID and Name:</td>
<td colspan="3"><strong>UC-02 ? Complete Health & Dietary
Profile</strong></td>
</tr>
<tr>
<td style="text-align: right;">Primary Actor:</td>
<td>Guest</td>
<td style="text-align: right;">Secondary Actors:</td>
<td>System Administrator</td>
</tr>
<tr>
<td style="text-align: right;">Description:</td>
<td colspan="3">This use case allows a guest to create and maintain a
sensitive Health & Dietary Profile containing health conditions,
food allergies, and dietary preferences. The profile is used to
personalize retreat experiences while ensuring privacy, consent
management, and strict role-based access control.</td>
</tr>
<tr>
<td style="text-align: right;">Trigger:</td>
<td colspan="3">Guest selects ?Health & Dietary Profile? after
successful authentication.</td>
</tr>
<tr>
<td style="text-align: right;">Preconditions:</td>
<td colspan="3">- Guest account exists and is authenticated.
- Guest account status is Active.
- Guest has accepted Privacy Policy.</td>
</tr>
<tr>
<td style="text-align: right;">Postconditions:</td>
<td colspan="3">- Health and dietary profile is encrypted and stored.
- Authorized services may access only relevant
information.
- Audit logs are recorded.</td>
</tr>
<tr>
<td style="text-align: right;">Normal Flow:</td>
<td colspan="3"><ol type="1">
- Guest opens Health & Dietary Profile.
- System displays profile form.
- System displays consent statement with unchecked consent box by
default (BR-08).
- Guest enters dietary preferences.
- Guest enters allergy information.
- Guest enters health conditions.
- Guest grants explicit consent.
- Guest submits profile.
- System validates required information.
- System encrypts sensitive information before persistence
(BR-09).
- System stores profile.
- System records audit log (BR-15).
- System displays confirmation.
</ol></td>
</tr>
<tr>
<td style="text-align: right;">Alternative Flows:</td>
<td colspan="3">- A1. Guest provides dietary profile only.<br />
? Health information remains empty.
- A2. Guest updates existing profile.<br />
? System overwrites current values and records update history.</td>
</tr>
<tr>
<td style="text-align: right;">Exceptions:</td>
<td colspan="3">- E1. Consent not granted.<br />
? System rejects submission and displays MSG-03.
- E2. Unexpected storage failure.<br />
? System displays MSG-15.</td>
</tr>
<tr>
<td style="text-align: right;">Priority:</td>
<td colspan="3">High</td>
</tr>
<tr>
<td style="text-align: right;">Frequency of Use:</td>
<td colspan="3">Medium</td>
</tr>
<tr>
<td style="text-align: right;">Business Rules:</td>
<td colspan="3">BR-07 ? Role-Based Access Control and Data
Minimization.
BR-08 ? Explicit consent is mandatory.
BR-09 ? Sensitive data encryption is required.
BR-10 ? Guest has the right to request permanent deletion.
BR-15 ? System audit logging is mandatory.</td>
</tr>
<tr>
<td style="text-align: right;">Other Information:</td>
<td colspan="3">- Health information is classified as Sensitive Personal
Data.</td>
</tr>
<tr>
<td style="text-align: right;">Assumptions:</td>
<td colspan="3">- Guests provide accurate and updated information.</td>
</tr>
</tbody>
</table>

### 2.2 Retreat Package & Accommodation Booking

<table>
<colgroup>
<col style="width: 19%" />
<col style="width: 29%" />
<col style="width: 22%" />
<col style="width: 27%" />
</colgroup>
<tbody>
<tr>
<td style="text-align: right;">ID and Name:</td>
<td colspan="3"><strong>UC07 ? Book Retreat Package</strong></td>
</tr>
<tr>
<td style="text-align: right;">Primary Actor:</td>
<td>Guest</td>
<td style="text-align: right;">Secondary Actors:</td>
<td>Payment Gateway API</td>
</tr>
<tr>
<td style="text-align: right;">Description:</td>
<td colspan="3">This use case allows a guest to browse retreat packages,
select stay dates, choose a preferred Villa Type, and secure the
reservation through a deposit payment.</td>
</tr>
<tr>
<td style="text-align: right;">Trigger:</td>
<td colspan="3">Guest selects ?Book Retreat Package?.</td>
</tr>
<tr>
<td style="text-align: right;">Preconditions:</td>
<td colspan="3">- Guest is authenticated.
- Retreat package exists and is active.
- Villa inventory exists.</td>
</tr>
<tr>
<td style="text-align: right;">Postconditions:</td>
<td colspan="3">- Booking is created successfully.
- Deposit transaction is stored.
- Confirmation is generated.
- Audit records are created.</td>
</tr>
<tr>
<td style="text-align: right;">Normal Flow:</td>
<td colspan="3"><ol start="14" type="1">
- Guest opens Retreat Package page.
- System displays available packages.
- Guest selects package.
- Guest selects stay dates.
- Guest selects Villa Type only (BR-02).
- System checks inventory.
- System calculates package cost and deposit.
- Guest confirms booking.
- System redirects to Payment Gateway.
- Guest completes deposit payment.
- Gateway returns success result.
- System confirms reservation (BR-01).
- System generates itinerary.
- System records booking logs (BR-15).
- System displays MSG-04.
</ol></td>
</tr>
<tr>
<td style="text-align: right;">Alternative Flows:</td>
<td colspan="3">- A1. Guest changes package.<br />
? System recalculates pricing.
- A2. Villa inventory changes.<br />
? System refreshes availability.</td>
</tr>
<tr>
<td style="text-align: right;">Exceptions:</td>
<td colspan="3">- E1. Deposit payment failed.<br />
? System displays MSG-05.
- E2. Booking confirmation timeout.<br />
? Reservation remains Pending.</td>
</tr>
<tr>
<td style="text-align: right;">Priority:</td>
<td colspan="3">High</td>
</tr>
<tr>
<td style="text-align: right;">Frequency of Use:</td>
<td colspan="3">High</td>
</tr>
<tr>
<td style="text-align: right;">Business Rules:</td>
<td colspan="3">BR-01 ? Booking requires successful deposit.
BR-02 ? Guest selects Villa Type only.
BR-15 ? Audit Trail is mandatory.</td>
</tr>
<tr>
<td style="text-align: left;">Other Information:</td>
<td colspan="3">- Actual Villa assignment occurs during Check-in.</td>
</tr>
<tr>
<td style="text-align: right;">Assumptions:</td>
<td colspan="3">- Payment services are available.</td>
</tr>
</tbody>
</table>

### 2.3 Spa & Therapy Scheduling Engine

<table>
<colgroup>
<col style="width: 19%" />
<col style="width: 29%" />
<col style="width: 22%" />
<col style="width: 27%" />
</colgroup>
<tbody>
<tr>
<td style="text-align: right;">ID and Name:</td>
<td colspan="3"><strong>UC11 ? Schedule Therapy Session</strong></td>
</tr>
<tr>
<td style="text-align: right;">Primary Actor:</td>
<td>Guest</td>
<td style="text-align: right;">Secondary Actors:</td>
<td>Spa Therapist / Yoga Trainer
Calendar & Notification Service</td>
</tr>
<tr>
<td style="text-align: right;">Description:</td>
<td colspan="3">This use case allows a guest with an active retreat
booking to schedule therapy or wellness sessions included in the
purchased retreat package. The system automatically coordinates
therapist availability and treatment room availability to prevent
resource conflicts.</td>
</tr>
<tr>
<td style="text-align: right;">Trigger:</td>
<td colspan="3">Guest selects ?Schedule Therapy Session?.</td>
</tr>
<tr>
<td style="text-align: right;">Preconditions:</td>
<td colspan="3">- Guest has an active retreat booking.
- Guest has available therapy sessions included in the
package.
- Therapist schedule exists.
- Treatment room availability exists.</td>
</tr>
<tr>
<td style="text-align: right;">Postconditions:</td>
<td colspan="3">- Therapy session reservation is successfully created.
- Therapist and treatment room allocation are completed.
- Notification and reminder are generated.
- Audit logs are stored.</td>
</tr>
<tr>
<td style="text-align: right;">Normal Flow:</td>
<td colspan="3"><ol start="29" type="1">
- Guest opens Therapy Scheduling.
- System displays available therapy services.
- Guest selects therapy type.
- Guest selects preferred date and time.
- System validates service eligibility based on purchased package
(BR-05).
- System checks therapist availability.
- System checks treatment room availability.
- System prevents resource collision and double booking
(BR-04).
- System allocates therapist and treatment room.
- System creates therapy reservation.
- System synchronizes reminder notification.
- System records audit activity (BR-15).
- System displays MSG-08.
</ol></td>
</tr>
<tr>
<td style="text-align: right;">Alternative Flows:</td>
<td colspan="3">- A1. Guest purchases additional therapy service.<br />
? System allows reservation beyond package scope.
- A2. Guest modifies session time.<br />
? System recalculates availability.</td>
</tr>
<tr>
<td style="text-align: right;">Exceptions:</td>
<td colspan="3">- E1. No therapist or room available.<br />
? System displays MSG-09.
- E2. Booking validation failed.<br />
? System rejects scheduling request.</td>
</tr>
<tr>
<td style="text-align: right;">Priority:</td>
<td colspan="3">High</td>
</tr>
<tr>
<td style="text-align: right;">Frequency of Use:</td>
<td colspan="3">High</td>
</tr>
<tr>
<td style="text-align: right;">Business Rules:</td>
<td colspan="3">BR-04 ? Two-dimensional Spa Scheduling.
BR-05 ? Spa service scope restriction.
BR-15 ? Audit Trail.</td>
</tr>
<tr>
<td style="text-align: right;">Other Information:</td>
<td colspan="3">- Only assigned therapists are allowed to update session
status.</td>
</tr>
<tr>
<td style="text-align: right;">Assumptions:</td>
<td colspan="3">- Availability information is synchronized in real time.</td>
</tr>
</tbody>
</table>

### 2.4 Dietary & Food Service Management.

<table>
<colgroup>
<col style="width: 19%" />
<col style="width: 29%" />
<col style="width: 22%" />
<col style="width: 27%" />
</colgroup>
<tbody>
<tr>
<td style="text-align: right;">ID and Name:</td>
<td colspan="3"><strong>UC17 ? View Daily Meal Preparation
Dashboard</strong></td>
</tr>
<tr>
<td style="text-align: right;">Primary Actor:</td>
<td>F&B Staff / Chef</td>
<td style="text-align: right;">Secondary Actors:</td>
<td>None</td>
</tr>
<tr>
<td style="text-align: right;">Description:</td>
<td colspan="3">This use case allows F&B staff to view meal requests
and dietary constraints for guests in order to prepare meals that
satisfy health restrictions and dietary requirements.</td>
</tr>
<tr>
<td style="text-align: right;">Trigger:</td>
<td colspan="3">Chef opens Daily Meal Dashboard.</td>
</tr>
<tr>
<td style="text-align: right;">Preconditions:</td>
<td colspan="3">- Guest meal selection exists.
- Guest has active accommodation.</td>
</tr>
<tr>
<td style="text-align: right;">Postconditions:</td>
<td colspan="3">- Meal preparation dashboard is displayed.
- Restricted information remains hidden.</td>
</tr>
<tr>
<td style="text-align: right;">Normal Flow:</td>
<td colspan="3"><ol start="42" type="1">
- Chef opens Daily Meal Dashboard.
- System retrieves meal orders.
- System retrieves dietary preferences.
- System retrieves allergy information only (BR-07).
- System automatically filters incompatible menu items
(BR-06).
- System groups meal preparation requests.
- System displays preparation instructions.
- Chef confirms preparation readiness.
</ol></td>
</tr>
<tr>
<td style="text-align: right;">Alternative Flows:</td>
<td colspan="3">- A1. Guest updates meal preference.<br />
? Dashboard refreshes automatically.
- A2. Menu becomes unavailable.<br />
? System recommends replacement dishes.</td>
</tr>
<tr>
<td style="text-align: right;">Exceptions:</td>
<td colspan="3">- E1. Unauthorized access attempt.<br />
? System rejects access and displays MSG-14.
- E2. Missing dietary profile.<br />
? Dashboard shows warning.</td>
</tr>
<tr>
<td style="text-align: right;">Priority:</td>
<td colspan="3">Medium</td>
</tr>
<tr>
<td style="text-align: right;">Frequency of Use:</td>
<td colspan="3">High</td>
</tr>
<tr>
<td style="text-align: right;">Business Rules:</td>
<td colspan="3">BR-06 ? Automatic Menu Filtering.
BR-07 ? RBAC and Data Minimization.</td>
</tr>
<tr>
<td style="text-align: right;">Other Information:</td>
<td colspan="3">- Medical conditions are never shown to Chef.</td>
</tr>
<tr>
<td style="text-align: right;">Assumptions:</td>
<td colspan="3">- Guest dietary profile exists before meal service.</td>
</tr>
</tbody>
</table>

### 2.5 Consolidated Billing & Checkout.

#### 2.5.1 UC21 ? Generate Consolidated Invoice

<table>
<colgroup>
<col style="width: 19%" />
<col style="width: 29%" />
<col style="width: 22%" />
<col style="width: 27%" />
</colgroup>
<tbody>
<tr>
<td style="text-align: right;">ID and Name:</td>
<td colspan="3"><strong>UC21 ? Generate Consolidated
Invoice</strong></td>
</tr>
<tr>
<td style="text-align: right;">Primary Actor:</td>
<td>Receptionist</td>
<td style="text-align: right;">Secondary Actors:</td>
<td>Payment Gateway API</td>
</tr>
<tr>
<td style="text-align: right;">Description:</td>
<td colspan="3">This use case allows the receptionist to consolidate all
eligible charges into a final invoice before checkout.</td>
</tr>
<tr>
<td style="text-align: right;">Trigger:</td>
<td colspan="3">Receptionist initiates checkout.</td>
</tr>
<tr>
<td style="text-align: right;">Preconditions:</td>
<td colspan="3">- Guest booking exists.
- Guest Folio exists.
- Charges are posted.</td>
</tr>
<tr>
<td style="text-align: right;">Postconditions:</td>
<td colspan="3">- Consolidated invoice is generated.
- Outstanding balance is calculated.
- Audit logs are recorded.</td>
</tr>
<tr>
<td style="text-align: right;">Normal Flow:</td>
<td colspan="3"><ol start="50" type="1">
- Receptionist opens Checkout.
- System retrieves Guest Folio.
- System retrieves Package charges.
- System retrieves Spa charges.
- System retrieves F&B charges.
- System aggregates all charges using Room_Booking_ID
(BR-11).
- System deducts deposit amount (BR-12).
- System calculates final payable amount.
- System generates invoice.
- System stores invoice record.
- System records audit logs.
</ol></td>
</tr>
<tr>
<td style="text-align: right;">Alternative Flows:</td>
<td colspan="3">- A1. Additional services detected.<br />
? Invoice recalculation occurs.
- A2. Promotion applies.<br />
? Final amount is adjusted.</td>
</tr>
<tr>
<td style="text-align: right;">Exceptions:</td>
<td colspan="3">- E1. Transaction data missing.
<blockquote>
? System displays MSG-15.
</blockquote></td>
</tr>
<tr>
<td style="text-align: right;">Priority:</td>
<td colspan="3">High</td>
</tr>
<tr>
<td style="text-align: right;">Frequency of Use:</td>
<td colspan="3">High</td>
</tr>
<tr>
<td style="text-align: right;">Business Rules:</td>
<td colspan="3">BR-11 ? Guest Folio Consolidation.
BR-12 ? Checkout Constraint.
BR-15 ? Audit Trail.</td>
</tr>
<tr>
<td style="text-align: right;">Other Information:</td>
<td colspan="3">- Only posted transactions appear in invoice.</td>
</tr>
<tr>
<td style="text-align: right;">Assumptions:</td>
<td colspan="3">- All service usage has been synchronized.</td>
</tr>
</tbody>
</table>

#### *2.5.2* UC22 ? Process Final Payment

<table>
<colgroup>
<col style="width: 19%" />
<col style="width: 29%" />
<col style="width: 22%" />
<col style="width: 27%" />
</colgroup>
<tbody>
<tr>
<td style="text-align: right;">ID and Name:</td>
<td colspan="3"><strong>UC22 ? Process Final Payment</strong></td>
</tr>
<tr>
<td style="text-align: right;">Primary Actor:</td>
<td>Receptionist</td>
<td style="text-align: right;">Secondary Actors:</td>
<td>Payment Gateway API</td>
</tr>
<tr>
<td style="text-align: right;">Description:</td>
<td colspan="3">This use case allows the receptionist to collect and
process the final payment after all eligible charges have been
consolidated into the final invoice. The payment process finalizes the
guest stay and prepares checkout completion.</td>
</tr>
<tr>
<td style="text-align: right;">Trigger:</td>
<td colspan="3">Receptionist selects ?Process Final Payment? after
invoice confirmation.</td>
</tr>
<tr>
<td style="text-align: right;">Preconditions:</td>
<td colspan="3">- Consolidated invoice has been generated.
- Guest booking status is Active.
- Outstanding balance exists.
- Payment service is available.</td>
</tr>
<tr>
<td style="text-align: right;">Postconditions:</td>
<td colspan="3">- Final payment is completed successfully.
- Invoice status becomes Paid.
- Guest booking status becomes Completed.
- Checkout becomes available.
- Audit logs are stored.</td>
</tr>
<tr>
<td style="text-align: right;">Normal Flow:</td>
<td colspan="3"><ol start="61" type="1">
- Receptionist opens Final Payment screen.
- System displays invoice summary.
- System calculates remaining balance after deposit deduction
(BR-12).
- Receptionist confirms payment amount.
- Guest selects payment method.
- System redirects transaction to Payment Gateway.
- Gateway validates payment.
- System receives successful response.
- System records transaction.
- System updates invoice status to Paid.
- System updates booking status to Completed.
- System records audit logs (BR-15).
- System displays payment success confirmation.
</ol></td>
</tr>
<tr>
<td style="text-align: right;">Alternative Flows:</td>
<td colspan="3">- A1. Guest changes payment method.<br />
? System regenerates payment request.
- A2. Payment requires additional verification.<br />
? System waits for asynchronous confirmation.</td>
</tr>
<tr>
<td style="text-align: right;">Exceptions:</td>
<td colspan="3">- E1. Outstanding Spa/F&B fees still exist.<br />
? System rejects checkout and displays MSG-11.
- E2. Payment failed.<br />
? Invoice remains unpaid.
- E3. Gateway timeout occurs.<br />
? Transaction status becomes Pending.
- E4. Unexpected payment exception.<br />
? System displays MSG-15.</td>
</tr>
<tr>
<td style="text-align: right;">Priority:</td>
<td colspan="3">High</td>
</tr>
<tr>
<td style="text-align: right;">Frequency of Use:</td>
<td colspan="3">High</td>
</tr>
<tr>
<td style="text-align: right;">Business Rules:</td>
<td colspan="3">BR-12 ? Checkout Constraint.
BR-15 ? Audit Trail.</td>
</tr>
<tr>
<td style="text-align: right;">Other Information:</td>
<td colspan="3">- Checkout cannot be completed until payment is fully
confirmed.</td>
</tr>
<tr>
<td style="text-align: right;">Assumptions:</td>
<td colspan="3">- Payment gateway remains available.</td>
</tr>
</tbody>
</table>

### 2.6 Revenue Analytics & Reporting

#### 2.6.1 **UC24 ? View Revenue Dashboard**

<table>
<colgroup>
<col style="width: 19%" />
<col style="width: 29%" />
<col style="width: 22%" />
<col style="width: 27%" />
</colgroup>
<tbody>
<tr>
<td style="text-align: right;">ID and Name:</td>
<td colspan="3"><strong>UC24 ? View Revenue Dashboard</strong></td>
</tr>
<tr>
<td style="text-align: right;">Primary Actor:</td>
<td>Manager</td>
<td style="text-align: right;">Secondary Actors:</td>
<td>System Administrator</td>
</tr>
<tr>
<td style="text-align: right;">Description:</td>
<td colspan="3">This use case allows managers to monitor business
performance through analytical dashboards that summarize retreat
revenue, occupancy indicators, therapist utilization, and food service
performance.</td>
</tr>
<tr>
<td style="text-align: right;">Trigger:</td>
<td colspan="3">Manager selects ?Revenue Dashboard?.</td>
</tr>
<tr>
<td style="text-align: right;">Preconditions:</td>
<td colspan="3">- Manager account is active.
- Reporting data exists.
- Data synchronization has completed.</td>
</tr>
<tr>
<td style="text-align: right;">Postconditions:</td>
<td colspan="3">- Dashboard information is displayed.
- Reporting indicators become available for business
decisions.</td>
</tr>
<tr>
<td style="text-align: right;">Normal Flow:</td>
<td colspan="3"><ol start="74" type="1">
- Manager opens Revenue Dashboard.
- System retrieves completed transaction records only
(BR-13).
- System retrieves retreat package revenue.
- System retrieves Spa revenue.
- System retrieves F&B revenue.
- System calculates occupancy indicators.
- System calculates therapist utilization.
- System generates analytical charts.
- Manager filters reporting period.
- System refreshes dashboard.
- Manager exports report if required.
</ol></td>
</tr>
<tr>
<td style="text-align: right;">Alternative Flows:</td>
<td colspan="3">- A1. Manager changes reporting dimensions.<br />
? Dashboard recalculates indicators.
- A2. Manager exports dashboard.<br />
? System generates downloadable report.</td>
</tr>
<tr>
<td style="text-align: right;">Exceptions:</td>
<td colspan="3">- E1. Reporting data unavailable.<br />
? System displays empty dashboard.
- E2. Aggregation calculation failed.<br />
? System displays MSG-15.</td>
</tr>
<tr>
<td style="text-align: right;">Priority:</td>
<td colspan="3">Medium</td>
</tr>
<tr>
<td style="text-align: right;">Frequency of Use:</td>
<td colspan="3">Medium</td>
</tr>
<tr>
<td style="text-align: right;">Business Rules:</td>
<td colspan="3">BR-13 ? Reporting and Review Logic.</td>
</tr>
<tr>
<td style="text-align: right;">Other Information:</td>
<td colspan="3">- Dashboard is read-only and cannot modify operational
data.</td>
</tr>
<tr>
<td style="text-align: right;">Assumptions:</td>
<td colspan="3">- Operational data synchronization runs successfully.</td>
</tr>
</tbody>
</table>

#### 2.6.2 UC23 ? Submit Retreat Review

<table>
<colgroup>
<col style="width: 19%" />
<col style="width: 29%" />
<col style="width: 22%" />
<col style="width: 27%" />
</colgroup>
<tbody>
<tr>
<td style="text-align: right;">ID and Name:</td>
<td colspan="3"><strong>UC23 ? Submit Retreat Review</strong></td>
</tr>
<tr>
<td style="text-align: right;">Primary Actor:</td>
<td>Guest</td>
<td style="text-align: right;">Secondary Actors:</td>
<td>None</td>
</tr>
<tr>
<td style="text-align: right;">Description:</td>
<td colspan="3">This use case allows guests to submit ratings and
reviews after completing their retreat experience.</td>
</tr>
<tr>
<td style="text-align: right;">Trigger:</td>
<td colspan="3">Guest selects ?Submit Review?.</td>
</tr>
<tr>
<td style="text-align: right;">Preconditions:</td>
<td colspan="3">- Retreat booking status is Completed.
- Checkout process has finished.</td>
</tr>
<tr>
<td style="text-align: right;">Postconditions:</td>
<td colspan="3">- Review is stored successfully.
- Rating becomes available in reporting.</td>
</tr>
<tr>
<td style="text-align: right;">Normal Flow:</td>
<td colspan="3"><ol start="85" type="1">
- Guest opens Review page.
- System validates retreat completion status.
- System displays review form.
- Guest enters rating score.
- Guest enters textual review.
- Guest submits review.
- System validates submission.
- System stores review.
- System displays successful confirmation.
</ol></td>
</tr>
<tr>
<td style="text-align: right;">Alternative Flows:</td>
<td colspan="3">- A1. Guest edits review.
<blockquote>
? System updates review.
</blockquote></td>
</tr>
<tr>
<td style="text-align: right;">Exceptions:</td>
<td colspan="3">- E1. Guest has not completed retreat.<br />
? System rejects review submission.
- E2. Unexpected system error.<br />
? System displays MSG-15.</td>
</tr>
<tr>
<td style="text-align: right;">Priority:</td>
<td colspan="3">Low</td>
</tr>
<tr>
<td style="text-align: right;">Frequency of Use:</td>
<td colspan="3">Medium</td>
</tr>
<tr>
<td style="text-align: right;">Business Rules:</td>
<td colspan="3">BR-13 ? Only completed stays may submit reviews.</td>
</tr>
<tr>
<td style="text-align: right;">Other Information:</td>
<td colspan="3">- Reviews contribute to analytics and quality monitoring</td>
</tr>
<tr>
<td style="text-align: right;">Assumptions:</td>
<td colspan="3">- Guests provide honest feedback.</td>
</tr>
</tbody>
</table>

## 3. Functional Requirements - H?i

### 3.1 Core Feature

#### 3.1.1 Health & Dietary Form Screen

**\[Content \#1\]**

- The screen
is divided into two main sections: "Diet & Allergies" on the left
and "Physical Health Status" on the right.
- At the bottom of the form, there is a consent confirmation area
and two action buttons.

<img src="images/media/image16.png" />

**\[Content #2\]**

- <em><strong>Description:</strong> This screen allows the Guest to
input their personal health and dietary profile. This sensitive
information is strictly confidential and will be segregated by the
system: Chefs will only have access to dietary restrictions, while Spa
Therapists will only view physical conditions.</em>
- <em><strong>Mapped Use Case:</strong> UC02 - As a Guest, I want
to complete my "Health & Dietary Profile".</em>

**\[Content #3\]**

<table style="width:97%;">
<colgroup>
<col style="width: 21%" />
<col style="width: 76%" />
</colgroup>
<tbody>
<tr>
<td><strong>Field Name</strong></td>
<td><strong>Description</strong></td>
</tr>
<tr>
<td colspan="2"><strong>Field Group: Dietary &
Allergies</strong></td>
</tr>
<tr>
<td>(1) Food Allergies Checkboxes</td>
<td>Data type: Array of Booleans (Options: Peanuts, Shellfish, Dairy,
Gluten). Optional. Checked values will be routed to F&B Staff</td>
</tr>
<tr>
<td>(2) Other Allergies Input</td>
<td>Data type: String, max length of 255 characters. Optional text field
for unlisted allergies.</td>
</tr>
<tr>
<td>(3) Diet Type Buttons</td>
<td>Data type: Enum/String (Options: Vegan, Vegetarian, Keto, Halal).
Single selection. Required field for automatic menu filtering.</td>
</tr>
<tr>
<td>(4) Additional Notes</td>
<td>Data type: String (Text area), max length of 500 characters.
Optional for specific taste preferences.</td>
</tr>
<tr>
<td colspan="2"><em><strong>Field Group: Physical Health
Status</strong></em></td>
</tr>
<tr>
<td>(5) Current Medical Conditions</td>
<td>Data type: String (Text area), max length of 500 characters.
Optional. (e.g., High blood pressure, diabetes).</td>
</tr>
<tr>
<td>(6) Current Medications</td>
<td>Data type: String (Text area), max length of 500 characters.
Optional.</td>
</tr>
<tr>
<td>(7) Recent Injuries or Issues</td>
<td>Data type: String (Text area), max length of 500 characters.
Optional. (e.g., Back pain, joint issues). This specific data will be
visible to Spa Therapists.</td>
</tr>
<tr>
<td colspan="2"><em><strong>Field Group: Confirmation &
Actions</strong></em></td>
</tr>
<tr>
<td>(8) Consent Checkbox</td>
<td>Data type: Boolean.
<strong>Strict Constraint:</strong> The initial value MUST be
Unchecked (False) to comply with Personal Data Protection regulations
(Decree 356/2025). The system must disable the Save button if this is
not checked.</td>
</tr>
<tr>
<td>(9) "Save & Continue" Button</td>
<td>Action: Triggers data submission. Backend must encrypt sensitive
health data fields before inserting them into the Database. Redirects to
the next step.</td>
</tr>
<tr>
<td>(10) "Review Itinerary" Button</td>
<td>Action: Cancels the current input and navigates back to the Guest's
itinerary dashboard.</td>
</tr>
</tbody>
</table>

#### 3.1.2 Data Erasure Request Screen

**\[Content #1\]**

- The UI is presented as a warning Modal/Dialog with prominent red
text alerts.
- It contains static warning text indicating that data cannot be
recovered after deletion and a legal compliance note regarding personal
data protection.

![](images/media/image4.png)

**\[Content #2\]**

- <strong>Description:</strong> This screen allows the Guest to
exercise their "Right to Erasure" in compliance with data privacy
regulations. The user is strictly required to re-authenticate via their
password before the system executes the permanent deletion of their
medical profiles, allergies, and itinerary records.
- <strong>Mapped Use Case:</strong> UC05 - As a Guest, I want to
exercise my "Right to Erasure", permanently deleting my health and
allergy data from the system after the retreat ends.

**\[Content #3\]**

<table style="width:97%;">
<colgroup>
<col style="width: 21%" />
<col style="width: 76%" />
</colgroup>
<tbody>
<tr>
<td><strong>Field Name</strong></td>
<td><strong>Description</strong></td>
</tr>
<tr>
<td colspan="2"><strong>Field Group: Verification</strong></td>
</tr>
<tr>
<td>(1) Password Input</td>
<td>Data type: String. Required field. Input is masked by default (e.g.,
***). Used to verify the user's identity before executing a destructive
action.</td>
</tr>
<tr>
<td>(2) Toggle Visibility (Eye Icon)</td>
<td>Action: Toggles the input field (1) state between masked (hidden)
and plain text (visible).</td>
</tr>
<tr>
<td colspan="2"><em><strong>Field Group: Actions</strong></em></td>
</tr>
<tr>
<td>(3) "H?y" (Cancel) Button</td>
<td>Action: Closes the warning dialog, aborts the operation, and returns
the user to the previous screen.</td>
</tr>
<tr>
<td>(4) "X?a vinh vi?n" (Permanent Delete) Button</td>
<td>Action: Submits the deletion request.
- Validation: Validates the entered Password (1) against the
database. If incorrect, display an inline error message.
- Execution: If correct, execute a hard delete query to permanently
remove sensitive health data from the database, terminate the active
session, and log the user out.</td>
</tr>
</tbody>
</table>

#### 

#### 

#### 3.1.3 Retreat Package List Screen

**\[Content #1\]**

- The UI features a horizontal search and multi-criteria filter bar
(Duration, Goal, Price) fixed at the top of the section.
- The main content area displays the available Retreat Packages in
a responsive grid layout. Each package is contained within an individual
card component featuring a thumbnail image, title, brief text
description, starting price, and a call-to-action button.

![](images/media/image30.png)
<strong>[Content #2: Brief descriptions of the screen/function,
mapped to the relevant use cases]</strong>
- <strong>Description:</strong> This screen enables Guests to
explore available Retreat Packages. Users can utilize multi-criteria
filters to find itineraries that align with their personal wellness
objectives.
- <strong>Mapped Use Case:</strong> UC06 - As a Guest, I want to
browse available "Retreat Packages" and filter them by goals (e.g.,
Weight Loss, Stress Relief, Yoga).

**\[Content #3\]**

<table style="width:97%;">
<colgroup>
<col style="width: 21%" />
<col style="width: 76%" />
</colgroup>
<tbody>
<tr>
<td><strong>Field Name</strong></td>
<td><strong>Description</strong></td>
</tr>
<tr>
<td colspan="2"><strong>Field Group: Verification</strong></td>
</tr>
<tr>
<td>(1) Password Input</td>
<td>Data type: String. Required field. Input is masked by default (e.g.,
***). Used to verify the user's identity before executing a destructive
action.</td>
</tr>
<tr>
<td>(2) Toggle Visibility (Eye Icon)</td>
<td>Action: Toggles the input field (1) state between masked (hidden)
and plain text (visible).</td>
</tr>
<tr>
<td colspan="2"><em><strong>Field Group: Actions</strong></em></td>
</tr>
<tr>
<td>(3) "H?y" (Cancel) Button</td>
<td>Action: Closes the warning dialog, aborts the operation, and returns
the user to the previous screen.</td>
</tr>
<tr>
<td>(4) "X?a vinh vi?n" (Permanent Delete) Button</td>
<td>Action: Submits the deletion request.
- Validation: Validates the entered Password (1) against the
database. If incorrect, display an inline error message.
- Execution: If correct, execute a hard delete query to permanently
remove sensitive health data from the database, terminate the active
session, and log the user out.</td>
</tr>
</tbody>
</table>

#### 3.1.4 Package Detail & Checkout Screen
<strong>[Content #1: UI Layout (Mockup screen
prototype)]</strong>
- The layout is divided into two main columns to optimize the user
experience.
- The left column displays the comprehensive details of the
selected Retreat Package, including a large hero image, a descriptive
text block outlining the itinerary, and highlighted included
experiences.
- The right column features a persistent booking form containing
date pickers, a Villa type dropdown, guest information input fields, a
dynamic cost summary block, and a prominent checkout button at the
bottom.

![](images/media/image33.png)

**\[Content #2\]**

- <strong>Description:</strong> This screen displays in-depth
details of a selected package. Here, the Guest finalizes their itinerary
dates, selects their accommodation, and securely pays the
deposit.
- <strong>Mapped Use Case:</strong> UC07 - As a Guest, I want to
select a package, pick travel dates, choose a Villa type, and securely
pay the deposit.

**\[Content #3\]**

<table style="width:97%;">
<colgroup>
<col style="width: 21%" />
<col style="width: 76%" />
</colgroup>
<tbody>
<tr>
<td><strong>Field Name</strong></td>
<td><strong>Description</strong></td>
</tr>
<tr>
<td colspan="2"><strong>Field Group: Booking Form</strong></td>
</tr>
<tr>
<td>(1) Check-in/Check-out Dates</td>
<td>Data type: LocalDate.
<strong>Constraint:</strong> Past dates are disabled. Check-out date
must strictly follow the Check-in date based on the package's predefined
duration.</td>
</tr>
<tr>
<td>(2) Lo?i Villa (Villa Type)</td>
<td>Data type: Dropdown list.
<strong>Constraint:</strong> The system must dynamically filter this
list to only show Villa types that have actual availability during the
selected date range (1).</td>
</tr>
<tr>
<td>(3) Guest Info (Name & Email)</td>
<td>Data type: String. Should be auto-populated if the Guest is fully
authenticated via SSO/Login.</td>
</tr>
<tr>
<td colspan="2"><em><strong>Field Group: Payment &
Checkout</strong></em></td>
</tr>
<tr>
<td>(4) Price Summary</td>
<td>Read-only calculation block. Dynamically updates based on the
selected Villa Type. Displays Base Price, Tax (10%), Total, and the
required 30% Deposit amount.</td>
</tr>
<tr>
<td>(5) "Thanh to?n" (Pay Deposit) Button</td>
<td>Action:
- Integrates with and triggers the external Payment Gateway API
(Stripe / VNPay Sandbox) to handle secure transactions.
- On Success: Generates a new Room_Booking_ID in the database and
sets the booking status.</td>
</tr>
</tbody>
</table>

#### 3.1.5 Arrivals & Check-in Dashboard Screen

**\[Content #1\]**

- The layout is divided into a fixed left navigation sidebar and a
primary main content area on the right.
- The main content area features a date header, a detailed Data
Table listing all expected arrivals for the current date, and summary
metric cards at the bottom.

![](images/media/image5.png)

**\[Content #2\]**

- <strong>Description:</strong> This is the primary operational
dashboard for Receptionists. The system automatically fetches and
displays all reservations scheduled for arrival on the current day.
Receptionists use this interface to assign physical room numbers and
process the check-in workflow.
- <strong>Mapped Use Case:</strong> UC08 - As a Receptionist, I
want to view a dashboard of expected arrivals and perform Check-In
(assign a specific Villa room number).

**\[Content #3\]**

<table style="width:97%;">
<colgroup>
<col style="width: 21%" />
<col style="width: 76%" />
</colgroup>
<tbody>
<tr>
<td><strong>Field Name</strong></td>
<td><strong>Description</strong></td>
</tr>
<tr>
<td colspan="2"><strong>Field Group: Dashboard Overview</strong></td>
</tr>
<tr>
<td>(1) Sidebar Navigation</td>
<td>Navigation links for front-desk operations (e.g., Arrivals,
Departures, Room Management).</td>
</tr>
<tr>
<td>(6) Summary Cards</td>
<td>Read-only numeric data. Dynamically counts and displays the number
of Pending Check-ins, Completed Check-ins, and Ready Villas.</td>
</tr>
<tr>
<td colspan="2"><em><strong>Field Group: Arrivals Data
Table</strong></em></td>
</tr>
<tr>
<td>(2) Guest & Package Info</td>
<td>Data type: Text. Displays Guest Name, Booking ID, Retreat
Package, and booked Villa Category.
<strong>Strict RBAC Constraint:</strong> For the "Special Requests"
column, the Backend query MUST mask/hide any Health or Dietary Allergy
data for the Receptionist role. Only general service requests (e.g.,
Wheelchair, Extra pillow) are permitted to be shown.</td>
</tr>
<tr>
<td>(3) S? ph?ng (Room Assignment)</td>
<td>Data type: Dropdown List.
<strong>Constraint:</strong> Upon clicking, the system must
dynamically populate this list with specific room numbers that meet two
conditions simultaneously: The room matches the booked Villa Category
AND its physical status is currently "Vacant/Ready".</td>
</tr>
<tr>
<td>(4) Tr?ng th?i (Status Badge)</td>
<td>Visual indicator of the booking status (e.g., Not Arrived, Waiting,
Checked-in).</td>
</tr>
<tr>
<td>(5) "Check-in" Action Button</td>
<td>Action: Triggers the check-in execution.
- Validation: The Receptionist must select a Room Number (3) before
submission.
- Execution: Updates the Booking status to "Checked-in" and
concurrently updates the physical Villa status (UC09) to "Occupied" in
the database</td>
</tr>
</tbody>
</table>

#### 3.1.6 Guest Itinerary Timeline Screen

**\[Content #1\]**

- The UI is structured as a vertical Timeline layout.
- The top section contains the header and date navigation. The main
body is split into a fixed time axis on the left and corresponding event
detail cards on the right.

![](images/media/image23.png)

**\[Content #2\]**

- <strong>Description:</strong> This screen provides Guests with a
visual and detailed overview of all scheduled daily activities at the
resort. It seamlessly aggregates data from various sub-systems (such as
F&B meal plans, Yoga classes, and Spa therapy sessions) into a
single, continuous chronological flow.
- <strong>Mapped Use Case:</strong> UC10 - As a Guest, I want to
view my full booking details and itinerary timeline.

**\[Content #3\]**

<table style="width:97%;">
<colgroup>
<col style="width: 21%" />
<col style="width: 76%" />
</colgroup>
<tbody>
<tr>
<td><strong>Field Name</strong></td>
<td><strong>Description</strong></td>
</tr>
<tr>
<td colspan="2"><strong>Field Group: Date Navigation</strong></td>
</tr>
<tr>
<td>(1) Date Selector / Display</td>
<td>Data type: LocalDate.
Action: Displays the current itinerary date (e.g., Monday, May 24).
Allows users to click and navigate between different days within the
duration of their booked Retreat Package.</td>
</tr>
<tr>
<td colspan="2"><em><strong>Field Group: Timeline
Events</strong></em></td>
</tr>
<tr>
<td>(2) Time Milestone</td>
<td>Data type: LocalTime (e.g., 07:00, 09:30). Displays the start time
of each scheduled event.</td>
</tr>
<tr>
<td>(3) Activity Card</td>
<td>Read-only container. Dynamically queries and aggregates cross-module
data from the Spa scheduling (Module 3) and F&B planning (Module 4)
tables.</td>
</tr>
<tr>
<td>(4) Activity Title & Icon</td>
<td>Data type: String & Icon Asset.
Displays the title of the event (e.g., "An s?ng Detox", "Massage Th?y
?i?n") accompanied by a context-specific icon in the top right corner of
the card.</td>
</tr>
<tr>
<td>(5) Activity Description</td>
<td>Data type: String. A brief descriptive text outlining the wellness
activity or its benefits.</td>
</tr>
<tr>
<td>(6) Location</td>
<td>Data type: String.
Displays the physical venue retrieved from the master data (e.g.,
"Nh? h?ng Th?c du?ng", "Shala Thi?n", "Aura Spa").</td>
</tr>
</tbody>
</table>

#### 3.1.7 Guest Spa Scheduler Screen

**\[Content #1\]**

- The layout is organized into two distinct columns. The left
column displays the selected service details and an interactive monthly
Calendar component.
- The right column features a responsive grid of available time
slots and a fixed Booking Summary block at the bottom, containing the
primary call-to-action button.

![](images/media/image34.png)

**\[Content #2\]**

- <strong>Description:</strong> This interface empowers Guests to
schedule their included Spa therapies. Crucially, the system acts as a
background orchestrator, computing real-time resource availability
(rooms and staff) to dynamically render valid time slots, thereby
completely preventing overbooking scenarios.
- <strong>Mapped Use Case:</strong>
- UC11 - As a Guest, I want to schedule included Spa sessions by
selecting a date and time slot.
- UC12 - As a System, I must automatically find an available slot
by simultaneously matching ONE free Therapist AND ONE empty Treatment
Room.

**\[Content #3\]**

<table style="width:97%;">
<colgroup>
<col style="width: 21%" />
<col style="width: 76%" />
</colgroup>
<tbody>
<tr>
<td><strong>Field Name</strong></td>
<td><strong>Description</strong></td>
</tr>
<tr>
<td colspan="2"><strong>Field Group: Scheduling Inputs</strong></td>
</tr>
<tr>
<td>(1) Service Info</td>
<td>Read-only container. Displays the Service Name, Duration (e.g., 60
mins), and Price.</td>
</tr>
<tr>
<td>(2) Date Picker</td>
<td>Data type: LocalDate.
<strong>Constraint:</strong> Past dates are strictly disabled.
Selecting a valid date triggers an API request to the backend to fetch
the corresponding availability for the Time Slot Grid (3).</td>
</tr>
<tr>
<td>(3) Time Slot Grid</td>
<td>Data type: LocalTime.
<strong>Strict Business Rule:</strong> A time slot button is
"Clickable/Available" IF AND ONLY IF the Backend query verifies the
availability of (Count Room_ID > 0) <strong>AND</strong> (Count
Therapist_ID > 0) for that exact duration. Otherwise, the slot is
rendered as Disabled/Grayed-out (e.g., "19:00 H?t ch?") to enforce
2-Dimensional Double-Booking Prevention.</td>
</tr>
<tr>
<td colspan="2"><em><strong>Field Group: Confirmation</strong></em></td>
</tr>
<tr>
<td>(4) Booking Summary</td>
<td>Read-only block. Dynamically updates based on inputs (2) and
(3).
<strong>Logic Note:</strong> The "Room" field (e.g., Lotus Suite 02)
is auto-assigned by the system's matching algorithm (UC12); the guest
does not manually select the room.</td>
</tr>
<tr>
<td>(5) "X?c nh?n d?t l?ch" (Confirm Booking) Button</td>
<td>Action: Submits the booking request.
<strong>Backend Constraint:</strong> The execution must be
encapsulated within a Database Transaction. The system must
simultaneously lock both the allocated Therapist and Room resources to
strictly prevent concurrency issues (Double-booking) during the write
operation.</td>
</tr>
<tr>
<td>(5) Activity Description</td>
<td>Data type: String. A brief descriptive text outlining the wellness
activity or its benefits.</td>
</tr>
</tbody>
</table>

#### 3.1.8 Therapist Daily Schedule Screen

**\[Content #1\]**

- The screen layout consists of a Header displaying the current
operational date (e.g., Tuesday, June 2, 2026) alongside secondary
action buttons.
- Below the header are three prominent summary KPI cards.
- The core element is a chronological Data Table detailing the
therapist's assigned treatment sessions for the day.

![](images/media/image9.png)

**\[Content #2\]**

- <strong>Description:</strong> This specialized dashboard empowers
Spa Therapists to manage their daily shifts. It provides a structured
view of upcoming appointments, grants secure access to permitted
physical health records for treatment preparation, and allows therapists
to update real-time session statuses.
- <strong>Mapped Use Case:</strong>
- UC13 - As a Spa Therapist, I want to view my daily schedule and
access specific medical notes of assigned guests.
- UC14 - As a Spa Therapist, I want to mark a session as
"Completed" or "No-Show".

**\[Content #3\]**

<table style="width:97%;">
<colgroup>
<col style="width: 21%" />
<col style="width: 76%" />
</colgroup>
<tbody>
<tr>
<td><strong>Field Name</strong></td>
<td><strong>Description</strong></td>
</tr>
<tr>
<td colspan="2"><strong>Field Group: Dashboard Summary</strong></td>
</tr>
<tr>
<td>(1) Date Header</td>
<td>Data type: LocalDate. Defaults to the current system date.</td>
</tr>
<tr>
<td>(2) Summary Cards</td>
<td>Read-only dynamic metrics (Total sessions, Completed sessions, and
currently Vacant rooms) calculated from the active dataset.</td>
</tr>
<tr>
<td colspan="2"><em><strong>Field Group: Schedule Data
Table</strong></em></td>
</tr>
<tr>
<td>(3) Session Details</td>
<td>Read-only text. Displays Time Range, Guest Name, Treatment Name, and
Assigned Room. This data is the direct output of the automated
scheduling algorithm (UC12).</td>
</tr>
<tr>
<td>(4) "Xem Ghi Ch?" (View Notes) Button</td>
<td>Action: Triggers a Modal dialog displaying the guest's medical
notes.
<strong>Strict RBAC Constraint:</strong> The Backend API serving this
endpoint MUST implement Data Minimization. It is strictly restricted to
returning physical treatment-related data (e.g., injuries, back pain)
and MUST completely mask/exclude any dietary allergy
information.</td>
</tr>
<tr>
<td>(5) Tr?ng th?i (Status Dropdown)</td>
<td>Data type: Enum.
Allowed values: ?ang ch? (Pending), Ho?n th?nh (Completed), V?ng
(No-Show).
Action: Updating this field commits the state change to the database.
Marking a session as "Completed" triggers the billing logic for the
guest's Consolidated Folio (Module 5).</td>
</tr>
</tbody>
</table>

#### 3.1.9 Manual Spa Booking Modal

**\[Content #1\]**

- The interface is rendered as an overlay dialog (Modal) to
maintain the user's current context, featuring a close (X) icon at the
top right.
- It contains sequential input fields for guest identification,
service selection, scheduling, and a dynamic price summary
block.

![](images/media/image15.png)

**\[Content #2\]**

- <strong>Description:</strong> This operational function enables
Receptionists to manually book additional, a-la-carte Spa therapies for
in-house guests. Utilizing standard hospitality accounting mechanics,
the system bypasses immediate point-of-sale payment and routes the
charges directly to the guest's centralized room account.
- <strong>Mapped Use Case:</strong> UC15 - As a Receptionist, I
want to manually book additional Spa services for guests and charge them
to their Villa folio.

**\[Content #3\]**

<table style="width:97%;">
<colgroup>
<col style="width: 21%" />
<col style="width: 76%" />
</colgroup>
<tbody>
<tr>
<td><strong>Field Name</strong></td>
<td><strong>Description</strong></td>
</tr>
<tr>
<td colspan="2"><strong>Field Group: Guest & Service
Selection</strong></td>
</tr>
<tr>
<td>(1) Guest/Room Search Input</td>
<td>Data type: String (Autocomplete).
<strong>Constraint:</strong> The backend query must filter and return
only guests whose current reservation status is actively
"Checked-in".</td>
</tr>
<tr>
<td>(2) Spa Service</td>
<td>Data type: Dropdown List. Populated from the Spa Master Data.</td>
</tr>
<tr>
<td colspan="2"><em><strong>Field Group: Scheduling</strong></em></td>
</tr>
<tr>
<td>(3) Date</td>
<td>Data type: LocalDate. Defaults to the current system date.</td>
</tr>
<tr>
<td>(4) Available Time Slots</td>
<td>Data type: LocalTime grid.
<strong>Logic:</strong> Reuses the backend validation algorithm to
render only time slots where both a Treatment Room and a Therapist are
concurrently available</td>
</tr>
<tr>
<td colspan="2"><em><strong>Field Group: Folio
Integration</strong></em></td>
</tr>
<tr>
<td>(5) Price Summary</td>
<td>Read-only calculation block. Displays Base Price, Applicable
Taxes/Service Fees, and the Grand Total.</td>
</tr>
<tr>
<td>(6) "X?c nh?n & Ghi n? v?o Folio" (Confirm & Post to Folio)
Button</td>
<td>Action: Form submission.
<strong>Strict Backend Constraint:</strong> This action must execute
a dual-operation database transaction:
1. Insert a new Spa appointment record.
2. Post the financial charge to the central Guest Folio, utilizing
the Room_Booking_ID as the primary foreign key linkage. This charge
remains "Pending" until the Consolidated Billing process during
Check-out (UC21).</td>
</tr>
</tbody>
</table>

#### 3.1.10 Personalized Menu & A-la-carte Screen

**\[Content #1\]**

- The layout is split into a main catalog area on the left and a
persistent floating Cart/Summary block on the right.
- The catalog area features top navigation tabs to switch between
included package meals and extra a-la-carte options. Food items are
displayed as detailed cards containing nutritional metrics.

![](images/media/image32.png)

**\[Content #2\]**

- <strong>Description:</strong> This interface delivers a smart
dining experience. The system automatically cross-references dish
ingredients against the user's health profile to issue warnings and
prevent the ordering of allergens. Furthermore, guests can seamlessly
order premium items outside their standard package via the a-la-carte
tab.
- <strong>Mapped Use Case:</strong>
- UC16 - As a Guest, I want to pre-select my daily meals from a
menu automatically filtered by the system based on my allergy/dietary
profile.
- UC19 - As a Guest, I want to order extra a-la-carte food/drinks
outside my package and charge it to the Villa.

**\[Content #3\]**

<table style="width:97%;">
<colgroup>
<col style="width: 21%" />
<col style="width: 76%" />
</colgroup>
<tbody>
<tr>
<td><strong>Field Name</strong></td>
<td><strong>Description</strong></td>
</tr>
<tr>
<td colspan="2"><strong>Field Group: Menu Navigation</strong></td>
</tr>
<tr>
<td>(1) Category Tabs</td>
<td>Action: Toggles the active dataset.
- Th?c don c?a b?n (Your Menu): Renders items included in the Retreat
Package (Zero cost).
- G?i m?n ngo?i (A-la-carte): Renders the premium menu with
associated monetary values.</td>
</tr>
<tr>
<td colspan="2"><em><strong>Field Group: Food Items</strong></em></td>
</tr>
<tr>
<td>(2) Standard Food Card</td>
<td>Read-only elements: Image, Dish Name, Ingredients Description,
Nutritional Facts (Calories, Macros), and an active "Ch?n m?n" (Add to
Cart) button.</td>
</tr>
<tr>
<td>(3) Allergy Warning Card</td>
<td><strong>Strict Logic Constraint:</strong> During data retrieval, the
Backend must perform an intersection check between the dish's
Ingredients array and the guest's Food_Allergies array. If a match
occurs, the Frontend must render an alert overlay (e.g., "C?nh b?o d?
?ng") and strictly disable the selection button to prevent the item from
being added to the cart.</td>
</tr>
<tr>
<td colspan="2"><em><strong>Field Group: Cart &
Checkout</strong></em></td>
</tr>
<tr>
<td>(4) Cart Summary</td>
<td>Dynamic calculation block. Orders from the included menu calculate
to 0 VND. Orders from the A-la-carte tab dynamically sum the Base Price
+ Service Fees.</td>
</tr>
<tr>
<td>(5) "X?c nh?n d?t b?n" (Confirm Order) Button</td>
<td>Action: Submits the Order payload to the database.
<strong>Folio Constraint:</strong> For any items sourced from the
A-la-carte tab, the system must automatically post the calculated
financial charge directly to the guest's centralized Folio account
associated with their Room_Booking_ID</td>
</tr>
</tbody>
</table>

#### 3.1.11 Chef Dashboard (Kitchen Display System)

**\[Content #1\]**

- The interface employs a Kanban board layout, segmented into three
primary operational columns: Pending, In Progress, and
Completed.
- Each culinary order is represented as a dynamic Ticket/Card.
These cards display room assignments, wait timers, itemized dish lists,
and quick-action buttons. Critical allergy alerts are prominently
anchored at the top of applicable cards in high-contrast red.

![](images/media/image11.png)

**\[Content #2\]**

- <strong>Description:</strong> This real-time operational
dashboard is utilized by Chefs and F&B staff. It digitizes the
ticket routing process, enabling the kitchen to monitor preparation
workflows and strictly adhere to food safety constraints without
violating patient medical privacy.
- <strong>Mapped Use Case:</strong>
- UC17 - As a Chef, I want to view a "Daily Meal Prep Dashboard"
aggregating orders and specific allergy alerts.
- UC18 - As a Chef, I want to update the status of an order ticket
(e.g., Preparing -> Ready).
- UC20 - As a System, I must mask the Guest's entire medical
history from the Chef, displaying ONLY relevant "Food
Allergies".

**\[Content #3\]**

<table style="width:97%;">
<colgroup>
<col style="width: 21%" />
<col style="width: 76%" />
</colgroup>
<tbody>
<tr>
<td><strong>Field Name</strong></td>
<td><strong>Description</strong></td>
</tr>
<tr>
<td colspan="2"><strong>Field Group: Kanban Columns</strong></td>
</tr>
<tr>
<td>(1) Order Status Columns</td>
<td>Layout containers that group tickets by their current state:
Pending, In_Progress, and Completed</td>
</tr>
<tr>
<td colspan="2"><em><strong>Field Group: Order Ticket
Details</strong></em></td>
</tr>
<tr>
<td>(2) Allergy Alert Label</td>
<td>Data type: String/Alert Banner.
<strong>Strict RBAC / Data Minimization Constraint:</strong> The
Backend query servicing this UI MUST restrict the payload to "Dietary
Allergies" only (e.g., peanuts, seafood). Physical medical records
(e.g., back pain, hypertension) MUST be completely masked and excluded
from this view.</td>
</tr>
<tr>
<td>(3) Order Meta Info</td>
<td>Read-only text. Displays Room Number, Guest Name, and a dynamic wait
Timer tracking ticket aging (e.g., "Tr? 5p")</td>
</tr>
<tr>
<td>(4) Itemized List</td>
<td>Data type: Array of Objects. Displays dish names, quantities, and
specific dietary modifications/notes associated with the items.</td>
</tr>
<tr>
<td>(5) State Mutation Buttons</td>
<td>Action Buttons ("B?t d?u" / Start and "Ho?n th?nh" /
Complete).
Action: Triggers a state update query in the database, subsequently
migrating the ticket to the next logical Kanban column on the
UI</td>
</tr>
</tbody>
</table>

#### 3.1.12 Consolidated Billing & Check-out Screen

**\[Content #1\]**

- The layout utilizes a 7:3 two-column structure. The wider left
column provides a granular breakdown of all incurred charges,
categorized logically by operational departments (Room, Spa,
F&B).
- The narrower right column is a sticky sidebar displaying the
grand total summary, payment method selectors, and the primary check-out
execution button.

![](images/media/image29.png)

**\[Content #2\]**

- <strong>Description:</strong> This screen digitizes the hotel
night audit and central accounting processes. Functioning as a
centralized Guest Folio, the system automatically aggregates all pending
financial liabilities from various Point-of-Sale (POS) terminals across
the resort into a single, comprehensive final invoice.
- <strong>Mapped Use Case:</strong>
- UC21 - As a Receptionist, during Check-Out, I want to generate a
Consolidated Bill summarizing the remaining Package Cost, extra Spa
services, and extra F&B orders.
- UC22 - As a Receptionist, I want to process the final payment and
change the Villa status to Vacant/Needs Cleaning.

**\[Content #3\]**

<table style="width:97%;">
<colgroup>
<col style="width: 21%" />
<col style="width: 76%" />
</colgroup>
<tbody>
<tr>
<td><strong>Field Name</strong></td>
<td><strong>Description</strong></td>
</tr>
<tr>
<td colspan="2"><strong>Field Group: Consolidated Folio
Details</strong></td>
</tr>
<tr>
<td>(1), (2), (3) Departmental Charge Blocks</td>
<td>Read-only Data Grids.
<strong>Backend Logic:</strong> The system must automatically
aggregate these records by executing cross-module queries (Modules 2, 3,
and 4), strictly utilizing the Room_Booking_ID as the primary relational
linkage.</td>
</tr>
<tr>
<td colspan="2"><em><strong>Field Group: Payment
Processing</strong></em></td>
</tr>
<tr>
<td>(4) Payment Summary</td>
<td>Read-only calculation block. Displays the algebraic sum of the
remaining Room balance, Spa charges, F&B charges, and applicable
Taxes/Fees</td>
</tr>
<tr>
<td>(5) Payment Method</td>
<td>Data type: Toggle/Radio selection (e.g., Credit Card, Bank
Transfer).</td>
</tr>
<tr>
<td>(6) "Thanh to?n & Check-out" (Pay & Check-out) Button</td>
<td>Action: Executes the final transaction and terminates the
stay.
<strong>Strict Business Constraint:</strong> The system must validate
the status of all associated sub-orders. Guests CANNOT check-out if they
have pending/unprocessed Spa or F&B orders (the button must be
disabled/throw an error).
<strong>Post-execution:</strong> Upon successful payment, the system
transitions the booking status to "Checked-out" and automatically
updates the physical Villa status to Vacant/Needs Cleaning.</td>
</tr>
</tbody>
</table>

#### 3.1.13 Manager Revenue Dashboard Screen

**\[Content \#1\]**

- The screen layout utilizes a standard dashboard structure with a
  persistent left navigation sidebar.
- The main content area features a top filter bar, a mid-section
  containing two large data visualization widgets (a Donut chart and a
  Line/Bar chart), and a bottom section displaying a detailed data grid
  of financial transactions.

![](images/media/image35.png)

**\[Content \#2\]**

- **Description:** This high-level dashboard provides the Management
  team with a comprehensive overview of the resort's financial
  performance. The system dynamically extracts and aggregates data from
  completed Consolidated Folios (Check-outs) to render real-time
  business intelligence metrics.
- **Mapped Use Case:** UC24 - As a Manager, I want to view a Revenue
  Dashboard (Pie/Bar charts) breaking down income by Retreat Packages,
  Spa, and F&B.

**\[Content \#3\]**

<table style="width:97%;">
<colgroup>
<col style="width: 21%" />
<col style="width: 76%" />
</colgroup>
<tbody>
<tr>
<td><strong>Field Name</strong></td>
<td><strong>Description</strong></td>
</tr>
<tr>
<td colspan="2"><strong>Field Group: Dashboard Filters</strong></td>
</tr>
<tr>
<td>(1) Filters</td>
<td>Data type: Dropdown lists.
- Time period: This Month, This Quarter, This Year.
- Category: All Services, Retreat Packages, Spa, F&B.
Action: Changing these values triggers a backend API call to re-query
the financial data and dynamically re-render the associated charts.</td>
</tr>
<tr>
<td colspan="2"><em><strong>Field Group: Data
Visualizations</strong></em></td>
</tr>
<tr>
<td>(2) Revenue Breakdown</td>
<td>Display type: Donut / Pie Chart widget.
Display Logic: Visually represents the percentage (%) and absolute
monetary value segmented by the primary revenue streams: Retreat
Packages, Spa & Therapy, and F&B.</td>
</tr>
<tr>
<td>(3) Revenue Trend</td>
<td>Display type: Line / Bar Chart widget.
Display Logic: Illustrates the fluctuation of total revenue across a
timeline (e.g., months). It also includes static KPI summaries such as
Best Month and Monthly Average.</td>
</tr>
<tr>
<td colspan="2"><em><strong>Field Group: Transaction
Data</strong></em></td>
</tr>
<tr>
<td>(4) Recent Transactions Table</td>
<td>Read-only Data Grid. Displays a paginated list of the most recent
completed check-out transactions, detailing Guest Name, Service, Date,
Status, and Amount.</td>
</tr>
<tr>
<td>(5) "T?i b?o c?o CSV" (Export CSV) Button</td>
<td>Action: Triggers a backend service to export the dataset from table
(4), based on the active filters, and downloads it to the user's device
in .csv or .xlsx format.</td>
</tr>
</tbody>
</table>

### 3.2 User Authentication

#### 3.2.1 Authentication & Login Screen

**\[Content \#1\]**

- The user interface features a clean, centrally-aligned authentication
  card utilizing elegant serif typography.
- It accommodates a traditional credential-based input form, visually
  separated by an "OR" divider, followed by a seamless Single Sign-On
  (SSO) option via Google. A directional link for new user registration
  is anchored at the bottom.

![](images/media/image2.png)

**\[Content \#2\]**

- **Description:** This screen serves as the primary security gateway to
  the application. It securely validates user identities and establishes
  active sessions. Users are provided the flexibility to authenticate
  via standard credentials or leverage Google's OAuth2 service for
  expedited access.
- **Mapped Use Case:** UC01 - Log in to the system (Includes traditional
  and SSO authentication).

**\[Content \#3\]**

<table style="width:97%;">
<colgroup>
<col style="width: 21%" />
<col style="width: 76%" />
</colgroup>
<tbody>
<tr>
<td><strong>Field Name</strong></td>
<td><strong>Description</strong></td>
</tr>
<tr>
<td colspan="2"><strong>Field Group: Traditional Login</strong></td>
</tr>
<tr>
<td>(1) EMAIL</td>
<td>Data type: String. Required field with strict email format
validation.</td>
</tr>
<tr>
<td>(2) M?T KH?U (Password)</td>
<td>Data type: String. Required field. User input is securely masked by
default.</td>
</tr>
<tr>
<td>(3) "Qu?n m?t kh?u?" (Forgot Password) Link</td>
<td>Action: Navigates the user to the Password Recovery workflow
interface (UC04).</td>
</tr>
<tr>
<td>(4) "?ang nh?p" (Login) Button</td>
<td>Action: Submits the credential payload to the authentication
endpoint.
<strong>Security Constraint:</strong> The backend must utilize secure
hashing algorithms (e.g., BCrypt) for password verification. Upon
successful authentication, the server provisions a valid JSON Web Token
to the client.</td>
</tr>
<tr>
<td colspan="2"><em><strong>Field Group: Alternative
Authentication</strong></em></td>
</tr>
<tr>
<td>(5) "Google" SSO Button</td>
<td>Action: Initializes the external OAuth2 authentication flow with
Google.
<strong>Logic:</strong> Bypasses manual password entry. The backend
securely decodes the received Google token to extract the unique Email
identifier and seamlessly authorize system access</td>
</tr>
<tr>
<td>(6) "?ang k? ngay" (Register Now) Link</td>
<td>Action: Directs unauthenticated users to the New Account
Registration screen (UC03).</td>
</tr>
</tbody>
</table>

#### 3.2.2 New Account Registration Screen

**\[Content \#1\]**

- The registration form interface is minimalist, maintaining visual and structural consistency with the Authentication screen.

- Input fields employ a clean, stacked layout with borderless underlines. The primary call-to-action button is prominently displayed, followed by secondary navigation links at the footer.

![](images/media/image8.png)

**\[Content \#2\]**

- **Description:** This interface enables new guests to establish their secure identity profile within the resort's ecosystem. The registration payload undergoes strict data integrity validations to ensure account uniqueness and robust security standards before database persistence.

- **Mapped Use Case:** UC03 - New account registration and basic profile management.

**\[Content \#3\]**

<table style="width:97%;">
<colgroup>
<col style="width: 21%" />
<col style="width: 76%" />
</colgroup>
<tbody>
<tr>
<td><strong>Field Name</strong></td>
<td><strong>Description</strong></td>
</tr>
<tr>
<td colspan="2"><strong>Field Group: Header & Basic
Information</strong></td>
</tr>
<tr>
<td>(1) Header & Message</td>
<td>Read-only text block. Displays the "?ang k?" (Register) title and
community onboarding subtext.</td>
</tr>
<tr>
<td>(2) H? V? T?N (Full Name)</td>
<td>Data type: String. Status: Required.</td>
</tr>
<tr>
<td>(3) EMAIL</td>
<td>Data type: String. Status: Required. Frontend must enforce
standard Regex validation.
<strong>SQL Constraint:</strong> The backend must handle
database-level UNIQUE constraints. If a duplicate exists, the API must
return a standardized 400 Bad Request error.</td>
</tr>
<tr>
<td colspan="2"><em><strong>Field Group: Security
Configuration</strong></em></td>
</tr>
<tr>
<td>(4) M?T KH?U (Password)</td>
<td>Data type: String. Input is masked by default. Incorporates an
interactive eye icon to toggle visibility state (plaintext/masked).</td>
</tr>
<tr>
<td>(5) X?C NH?N M?T KH?U (Confirm Password)</td>
<td>Data type: String.
<strong>Frontend Logic:</strong> Strict real-time validation is
required. The string value must match field (4) exactly; otherwise, form
submission is disabled.</td>
</tr>
<tr>
<td colspan="2"><em><strong>Field Group: Actions &
Navigation</strong></em></td>
</tr>
<tr>
<td>(6) "?ANG K?" (Register) Button</td>
<td>Action: Submits the user creation payload to the backend API.
<strong>Backend Logic:</strong> The server must execute one-way
cryptographic hashing (e.g., BCrypt) on the plaintext password prior to
executing the SQL INSERT statement.</td>
</tr>
<tr>
<td>(7) "?ang nh?p ngay" (Login Now) Link</td>
<td>Action: Navigates existing users back to the primary Authentication
Screen (UC01).</td>
</tr>
</tbody>
</table>

#### 3.2.3 Password Recovery Screens

**\[Content \#1\]**

- Phase 1 (Request): Annotate (1) Header & Instructions, (2) Email
  Input, (3) "G?i y?u c?u" Button, (4) "Quay l?i dang nh?p" Link.
- Phase 2 (Reset): Annotate (5) Header & Instructions, (6) New Password
  Input, (7) Confirm Password Input, (8) "C?p nh?t m?t kh?u" Button, (9)
  "Quay l?i dang nh?p" Link.
- The UI maintains visual consistency with the Authentication module,
  utilizing centered cards, elegant serif typography, and minimalist
  input fields.

![](images/media/image27.png)

![](images/media/image10.png)

**\[Content \#2:\]**

- Description: This feature provides a secure, two-step workflow for
  account recovery. The system never exposes original credentials;
  instead, it dispatches a secure, single-use token via email (Screen
  1), authorizing the user to define a new password (Screen 2).
- Mapped Use Case: UC04 - Forgot password, reset password, and security
  configuration.

**\[Content \#3\]**

<table style="width:97%;">
<colgroup>
<col style="width: 21%" />
<col style="width: 76%" />
</colgroup>
<tbody>
<tr>
<td><strong>Field Name</strong></td>
<td><strong>Description</strong></td>
</tr>
<tr>
<td colspan="2"><strong>Field Group: Phase 1 - Request Reset
Link</strong></td>
</tr>
<tr>
<td>(1) Header & Instructions</td>
<td>Read-only text. Displays "Kh?i ph?c m?t kh?u" (Password Recovery)
and instructions.</td>
</tr>
<tr>
<td>(2) Email Input</td>
<td>Data type: String. Status: Required field.</td>
</tr>
<tr>
<td>(3) "G?i y?u c?u" (Send Request) Button</td>
<td>Action: Submits the recovery request to the API.
<strong>Core Backend Logic:</strong> The system verifies the email
existence. If valid, it generates a cryptographically secure Reset_Token
(with a strict expiration, e.g., 15 minutes) and triggers an
asynchronous email service containing the tokenized URI.</td>
</tr>
<tr>
<td>(4) "Quay l?i..." (Back to Login) Link</td>
<td>Action: Navigates back to the Login Screen (UC01).</td>
</tr>
<tr>
<td colspan="2"><em><strong>Field Group: Phase 2 - Set New
Password</strong></em></td>
</tr>
<tr>
<td>(5) Header & Instructions</td>
<td>Read-only text. Displays "Thi?t l?p m?t kh?u m?i" (Set New
Password). This interface is exclusively accessed via the emailed
URI.</td>
</tr>
<tr>
<td>(6) M?t kh?u m?i (New Password)</td>
<td>Data type: String. Masked input with a toggle visibility icon.
Enforces strict Password Policy configurations.</td>
</tr>
<tr>
<td>(7) X?c nh?n m?t kh?u (Confirm Password)</td>
<td>Data type: String. Masked input.
<strong>Frontend Logic:</strong> Must exactly match field (6) before
form submission is enabled.</td>
</tr>
<tr>
<td>(8) "C?p nh?t..." (Update Password) Button</td>
<td>Action: Submits the new password payload alongside the
URL-extracted Token.
<strong>Database Security:</strong> The backend validates the Token's
integrity. Upon success, it executes a secure hash of the new password,
updates the record, and immediately <strong>revokes the Token</strong>
to prevent Replay Attacks.</td>
</tr>
<tr>
<td>(9) "Quay l?i..." (Back to Login) Link</td>
<td>Action: Navigates back to the Login Screen (UC01).</td>
</tr>
</tbody>
</table>

### 3.3 Master Data

#### 3.3.1 Villa Status Management Screen

**\[Content \#1\]**

- The interface employs a modern Dashboard layout utilizing interactive
  Cards to represent individual Villas.
- The top section highlights aggregate KPI counters for room states
  (Clean, Dirty, Maintenance) utilizing strict traffic-light color
  coding. The underlying grid renders detailed Villa cards that
  seamlessly merge interactive state mutation capabilities (dropdowns)
  with cross-departmental occupancy data (Guest details, timelines).

![](images/media/image31.png)

**\[Content \#2\]**

- **Description:** This operational hub synchronizes workflows between
  the Front Desk and Housekeeping departments. By providing real-time
  physical status monitoring, the system acts as a strict guardrail,
  completely preventing operational anomalies such as allocating
  arriving guests to unserviced or out-of-order accommodations.
- **Mapped Use Case:** UC09 - Manage and update the physical status of
  Villas (Vacant, Occupied, Dirty/Needs Cleaning, Maintenance).

**\[Content \#3\]**

**Field Description**

<table style="width:97%;">
<colgroup>
<col style="width: 22%" />
<col style="width: 74%" />
</colgroup>
<tbody>
<tr>
<td><strong>Field Name</strong></td>
<td><strong>Description</strong></td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"><em><strong>Field Group:
Dashboard Overview & Filters</strong></em></td>
</tr>
<tr>
<td style="text-align: center;">(1) KPI Status Summary</td>
<td>IRead-only dynamic metrics. The backend executes COUNT queries to
dynamically aggregate the volume of Villas in S?CH (Clean), B?N (Dirty),
and B?O TR? (Maintenance) states.</td>
</tr>
<tr>
<td style="text-align: center;">(2) Filters</td>
<td>Data type: Dropdowns. Enables list segmentation by Floor (T?NG) or
Villa Category (LO?I VILLA). Mutating these parameters instantly
triggers a backend API request to re-fetch the grid payload.</td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"><em><strong>Field Group: Villa
Card Details</strong></em></td>
</tr>
<tr>
<td style="text-align: center;">(3) Villa Identification</td>
<td>Read-only text. Displays the primary key identifier (e.g., V01) and
associated Room Category.</td>
</tr>
<tr>
<td style="text-align: center;">(4) Housekeeping Status Dropdown</td>
<td>Data type: Enum.
<strong>Logic:</strong> Empowers staff to mutate the physical state
directly on the card. Transitioning a state from B?N (Dirty) to S?CH
(Clean) commits the update to the DB and concurrently dispatches an
event to unlock the room's availability for Front Desk Check-in
operations.</td>
</tr>
<tr>
<td style="text-align: center;">(5) Occupancy Info & Alerts</td>
<td>Dynamic data block requiring cross-module relational queries
(e.g., JOIN with Room_Booking).
<strong>Business Rules:</strong>
- Active Booking: Renders "?ang ?" (Occupied), Guest Name, and
Checkout timeline.
- Impending Arrival: Renders "Kh?ch s?p d?n" (Arrival Expected).
- No active/pending linkage: Renders "Tr?ng" (Vacant).
- Maintenance state active: Prominently renders technical fault notes
(e.g., "S?a m?y l?nh" / AC Repair) in red.</td>
</tr>
</tbody>
</table>

#### 3.3.2 Performance Reports & Export Screen

**\[Content \#1\]**

- The interface utilizes a highly professional Admin Dashboard layout,
  leveraging adequate white space for optimal data readability.
- The screen is vertically divided into three functional zones: Report
  parameter configuration, high-level KPI summaries featuring
  Month-over-Month (MoM) growth indicators, and a granular, paginated
  data grid highlighting status alerts.

![](images/media/image7.png)

**\[Content \#2\]**

- Description: This functionality provides Management with a robust
  analytics engine. The system automatically aggregates transactional
  data across all operational modules (Front Desk, Spa, F&B) to
  calculate room occupancy, total revenue, and service utilization
  rates. Users can preview these metrics dynamically or export them into
  document formats (PDF/Excel) for strategic planning.
- Mapped Use Case: UC25 - As a Manager, I want to export monthly Room
  Occupancy & Therapist Utilization reports to a file.

**\[Content \#3\]**

**Field Description**

<table style="width:97%;">
<colgroup>
<col style="width: 22%" />
<col style="width: 74%" />
</colgroup>
<tbody>
<tr>
<td><strong>Field Name</strong></td>
<td><strong>Description</strong></td>
</tr>
<tr>
<td colspan="2"><em><strong>Field Group: Report Configuration &
Export</strong></em></td>
</tr>
<tr>
<td style="text-align: center;">(1) "Xu?t PDF" (Export PDF) Button</td>
<td>Action: Initiates the document generation pipeline.
<strong>Core Backend Logic:</strong> The server utilizes the
currently filtered dataset and applies a document rendering library
(e.g., iTextPDF in Java) to construct a branded table format. It returns
a Byte Stream prompting an automatic .pdf download in the
browser.</td>
</tr>
<tr>
<td style="text-align: center;">(2) "T?o b?o c?o m?i" (Generate Report)
Button</td>
<td>Action: Dispatches a GET request with the active parameters to the
backend, triggering a recalculation and re-rendering of both the KPI
Cards and the Data Grid below.</td>
</tr>
<tr>
<td style="text-align: center;">(2) Data Filters</td>
<td>Data type: Dropdown Lists.
- Lo?i d? li?u (Data Type): e.g., Total Revenue, Spa Efficiency,
Occupancy Rate.
- Kho?ng th?i gian (Timeframe): This Month, This Quarter, This
Year.
- Ph?n kh?c (Segment): All Services, Retreat Packages,
A-la-carte.
<strong>Logic:</strong> These selections formulate the exact
parameter payload sent to the backend SQL queries.</td>
</tr>
<tr>
<td colspan="2"><em><strong>Field Group: Performance
Dashboard</strong></em></td>
</tr>
<tr>
<td style="text-align: center;">(3) KPI Summary Cards</td>
<td>Read-only dynamic widgets. Displays core metrics: Revenue,
Occupancy Rate (%), and Spa Utilization Count.
<strong>Calculation Logic:</strong> Incorporates a Trend Analysis
indicator comparing current data against the previous month (MoM). Green
denotes positive growth; Red denotes decline.</td>
</tr>
<tr>
<td colspan="2" style="text-align: left;"><em><strong>Field Group:
Detailed Data Grid</strong></em></td>
</tr>
<tr>
<td style="text-align: center;">(4) Data Preview Grid</td>
<td>Read-only Data Grid rendering granular record details.
Columns include: Date, Metric Name, Actual Value, Target, Trend, and
Status. The "Tr?ng th?i" (Status) column employs color-coded badging
(e.g., T?T/Good in Green, C?N CH? ?/Needs Attention in Red) for rapid
cognitive processing.</td>
</tr>
<tr>
<td style="text-align: center;">(5) Pagination Controls</td>
<td>Navigation component.
<strong>Logic:</strong> Implements Server-side pagination (utilizing
SQL Limit/Offset) to ensure memory optimization and performance when
handling large datasets. Displays total record counts and current page
location (e.g., "Page 1 / 8").</td>
</tr>
</tbody>
</table>

## 4. Non-Functional Requirements - ??c

### 4.1 External Interfaces

*\[This section provides information to ensure that the system will
communicate properly with users and with external hardware or
software/system elements.\]*

<table>
<colgroup>
<col style="width: 18%" />
<col style="width: 25%" />
<col style="width: 55%" />
</colgroup>
<tbody>
<tr>
<td style="text-align: center;"><strong><mark>ID</mark></strong></td>
<td
style="text-align: center;"><strong><mark>Interface</mark></strong></td>
<td
style="text-align: center;"><strong><mark>Requirements</mark></strong></td>
</tr>
<tr>
<td style="text-align: center;"><strong><mark>EI-01</mark></strong></td>
<td style="text-align: center;"><strong><mark>User
Interface</mark></strong></td>
<td style="text-align: center;"><strong><mark>The system shall provide
responsive web-based interfaces for desktop and tablet
devices.</mark></strong></td>
</tr>
<tr>
<td style="text-align: center;"><strong><mark>EI-02</mark></strong></td>
<td
style="text-align: center;"><strong><mark>Navigation</mark></strong></td>
<td style="text-align: center;"><table style="width:2%;">
<colgroup>
<col style="width: 2%" />
</colgroup>
<tbody>
<tr>
<td></td>
</tr>
</tbody>
</table>

<table style="width:59%;">
<colgroup>
<col style="width: 59%" />
</colgroup>
<tbody>
<tr>
<td><strong><mark>Screen flow shall follow Left ? Right and Top ? Bottom
principles.</mark></strong></td>
</tr>
</tbody>
</table>

</td>
</tr>
<tr>
<td style="text-align: center;"><strong><mark>EI-03</mark></strong></td>
<td style="text-align: center;"><strong><mark>Consent
Interface</mark></strong></td>
<td style="text-align: center;"><strong><mark>Sensitive health-related
screens shall display consent notices before data
collection.</mark></strong></td>
</tr>
<tr>
<td style="text-align: center;"><strong><mark>EI-04</mark></strong></td>
<td style="text-align: center;"><strong><mark>Dashboard
Interface</mark></strong></td>
<td style="text-align: center;"><strong><mark>Dashboard shall support
filtering, sorting, searching, and exporting.</mark></strong></td>
</tr>
<tr>
<td style="text-align: center;"><strong><mark>EI-05</mark></strong></td>
<td style="text-align: center;"><strong><mark>Validation
Interface</mark></strong></td>
<td style="text-align: center;"><strong><mark>Input validation messages
shall appear immediately.</mark></strong></td>
</tr>
<tr>
<td style="text-align: center;"><strong><mark>EI-06</mark></strong></td>
<td style="text-align: center;"><strong><mark>System
Messages</mark></strong></td>
<td style="text-align: center;"><strong><mark>System shall follow
predefined messages MSG-01 ? MSG-15.</mark></strong></td>
</tr>
<tr>
<td style="text-align: center;"><strong><mark>EI-07</mark></strong></td>
<td style="text-align: center;"><strong><mark>Payment
Gateway</mark></strong></td>
<td style="text-align: center;"><strong><mark>Support deposit payment
and final payment integration.</mark></strong></td>
</tr>
<tr>
<td style="text-align: center;"><strong><mark>EI-08</mark></strong></td>
<td style="text-align: center;"><strong><mark>Notification
Service</mark></strong></td>
<td style="text-align: center;"><strong><mark>Support email verification
and appointment reminders.</mark></strong></td>
</tr>
<tr>
<td style="text-align: center;"><strong><mark>EI-09</mark></strong></td>
<td style="text-align: center;"><strong><mark>Reporting
Interface</mark></strong></td>
<td style="text-align: center;"><strong><mark>Support exporting reports
and invoice generation.</mark></strong></td>
</tr>
</tbody>
</table>

### 4.2 Quality Attributes

*\[List all the required system characteristics (quality attributes)
specification. Some of the possible attributes are provided with the
guide/descriptions are mentioned here\]*

#### 4.2.1 Usability

*\[This section includes all those requirements that affect usability.
For example, specify the required training time for a normal user and a
power user to become productive at particular operations specify
measurable task times for typical tasks or base the new system?s
usability requirements on other systems that the users know and like
specify requirement to conform to common usability standards, such as
IBM?s CUA standards Microsoft?s GUI standards\]*

|              |                                                        |                      |
| :----------: | :----------------------------------------------------: | :-------------------: |
| **ID** |                 **Requirement**                 | **Measurement** |
|    US-01    |    Guest registration shall be simple and intuitive    |     = 3 minutes     |
|    US-02    | Retreat package booking shall be completed efficiently |     = 5 minutes     |
|    US-03    |      Receptionist shall complete check-in quickly      |     = 3 minutes     |
|    US-04    |  Therapist shall update treatment status efficiently  |     = 30 seconds     |
|    US-05    |     Chef shall locate dietary information quickly     |     = 15 seconds     |
|    US-06    |        Manager shall access reports efficiently        |     = 10 seconds     |
|    US-07    |    User interfaces shall remain visually consistent    |  Across all modules  |
|    US-08    |    Consent requirements shall be clearly displayed    |       Mandatory       |

#### 4.2.1.1 Training Requirements

|                    |                        |
| :-----------------: | :---------------------: |
| **User Role** | **Training Time** |
|        Guest        |  No training required  |
|    Receptionist    |       = 2 hours       |
|      Therapist      |        = 1 hour        |
|    Administrator    |       = 4 hours       |

#### 4.2.1.2 Usability Standards

|                        |                      |
| :---------------------: | :-------------------: |
| **Standard Type** | **Requirement** |
|      Accessibility      |    WCAG principles    |
|    Responsive Design    |       Supported       |
| Navigation Consistency |       Required       |

#### 4.2.2.1 Performance

*\[The system?s performance characteristics are outlined in this
section. Include specific response times. Where applicable, reference
related Use Cases by name.*

*Response time for a transaction (average, maximum)*

*Throughput, for example, transactions per second*

*Capacity, for example, the number of customers or transactions the
system can accommodate*

*Resource utilization, such as memory, disk, communications, and so
forth.\]*

|              |                                  |                    |
| :----------: | :------------------------------: | :----------------: |
| **ID** |      **Requirement**      |  **Target**  |
|    PF-01    |      Average response time      |    = 2 seconds    |
|    PF-02    |      Maximum response time      |    = 5 seconds    |
|    PF-03    |   Booking confirmation (UC07)   |   = 10 seconds   |
|    PF-04    | Final payment processing (UC22) |   = 15 seconds   |
|    PF-05    | Revenue dashboard loading (UC24) |    = 8 seconds    |
|    PF-06    |     Concurrent active users     |     100 users     |
|    PF-07    |         Booking requests         | 30 requests/minute |
|    PF-08    |        Dashboard requests        | 50 requests/minute |
|    PF-09    |          Guest Accounts          |      100,000      |
|    PF-10    |             Bookings             |       50,000       |
|    PF-11    |            Audit Logs            |     1,000,000     |
|    PF-12    |         Payment Records         |      500,000      |
|    PF-13    |         CPU Utilization         |       = 70%       |
|    PF-14    |        Memory Utilization        |      = 8 GB      |
|    PF-15    |          Database Query          |    = 1 second    |
|    PF-16    |       Encryption Overhead       |       = 10%       |
|    PF-17    |      Monthly System Uptime      |      = 99.5%      |
|    PF-18    |          Recovery Time          |   = 30 minutes   |
|    PF-19    |      Transaction Data Loss      |    Not allowed    |

#### 4.2.2.2 Related Business Rules

|                  |                          |
| :---------------: | :-----------------------: |
| **Rule ID** |   **Description**   |
|       BR-09       | Sensitive Data Encryption |
|       BR-15       |        Audit Trail        |

## 5. Requirement Appendix - My

### 5.1 Business Rules:

|              |                                                        |                                                                                                                                                                                                                                                                                                                        |                                |
| :----------: | :----------------------------------------------------: | :--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------: | :-----------------------------: |
| **ID** |              **Business Rule Name**              |                                                                                                                                  **Detailed Description (Constraints & Logic)**                                                                                                                                  | **Reference (UC/Source)** |
|    BR-01    |        Booking Confirmation and Deposit Payment        |                                                                                       A Retreat Package booking shall only be confirmed after the system receives a successful deposit payment result from the payment gateway.                                                                                       |              UC07              |
|    BR-02    |            Villa Management and Allocation            | Guests may only select a Villa Type when making a booking. The system shall only confirm a booking if sufficient capacity is available for the selected Villa Type during the requested period. A specific Villa shall be assigned by the Receptionist during the Check-in process based on actual Villa availability. |           UC07, UC08           |
|    BR-03    |        Villa Status and Allocation Constraints        |                                                                     A Villa shall not be assigned to more than one active booking during the same period. Villas with a status of Maintenance or Out of Service shall not be allocated to guests.                                                                     |           UC08, UC09           |
|    BR-04    |              Dual Resource Spa Scheduling              |                                                                A Spa appointment shall only be valid when both an available Therapist and an available Therapy Room exist at the requested time. The system shall prevent double booking of resources.                                                                |              UC12              |
|    BR-05    |       Spa Service Eligibility and Update Control       |                      Guests may only book Spa services included in their purchased Retreat Package. Only the assigned Therapist may update the treatment session status. Additional Spa services outside the package may only be added by a Receptionist and must be recorded in the Guest Folio.                      |        UC11, UC14, UC15        |
|    BR-06    |              Automatic F&B Menu Filtering              |                                      The system shall automatically exclude menu items that contain allergens or conflict with the guest?s declared dietary preferences. Chefs and F&B Staff shall only have access to allergy and dietary information necessary for their work.                                      |        UC16, UC17, UC20        |
|    BR-07    | Role-Based Access Control (RBAC) and Data Minimization |                       Therapists may only access health information required for treatment purposes. Chefs may only access food allergy and dietary preference information. Receptionists shall not have access to guest health records. Access control shall be enforced at the backend level.                       |     UC03, UC13, UC17, UC20     |
|    BR-08    |         Consent for Sensitive Data Collection         |                                                                            The system shall obtain explicit consent from guests before collecting health-related or allergy information. Consent checkboxes shall be unchecked by default.                                                                            |              UC02              |
|    BR-09    |               Sensitive Data Encryption               |                                                                                          Health information, allergies, dietary preferences, and personal identification data shall be encrypted when stored in the database.                                                                                          |           UC02, UC08           |
|    BR-10    |                 Right to Data Erasure                 |                                                                                             Guests may request the permanent deletion of their health and allergy information after their retreat stay has been completed.                                                                                             |              UC05              |
|    BR-11    |          Guest Folio and Consolidated Billing          |                                                                                            All Spa and F&B charges shall be recorded in the Guest Folio using the corresponding Booking_ID and included in the final bill.                                                                                            |        UC15, UC19, UC21        |
|    BR-12    |                 Check-out Constraints                 |                                                                      Guests shall not be allowed to complete the Check-out process if any Spa or F&B charges remain unpaid. Any previously paid deposit shall be deducted from the final invoice.                                                                      |           UC21, UC22           |
|    BR-13    |               Reporting and Review Logic               |                                                          Revenue and occupancy reports shall only include completed transactions. Only guests who have completed their retreat stay may submit reviews and ratings. Each booking may submit only one review.                                                          |        UC23, UC24, UC25        |
|    BR-14    |          Guest Stay Registration Information          |                                                                                      During Check-in, the system shall collect and store guest identification information to comply with accommodation registration regulations.                                                                                      |    UC08, Residence Law 2020    |
|    BR-15    |                 Audit Trail Management                 |                                                                        The system shall maintain audit logs for critical activities such as login, health data access, booking, payment, and Check-out to support monitoring and traceability.                                                                        |     UC07, UC11, UC21, UC22     |
|    BR-16    |               Meal Order Status Workflow               |                                                       Meal Order status shall only progress in the following sequence: Pending ? Preparing ? Ready for Delivery. Status reversal shall not be permitted. Only Chefs or F&B Staff may update Meal Order status.                                                       |              UC18              |
|    BR-17    |    Spa Appointment Notification and Synchronization    |                                                            After a Spa appointment is successfully booked, the system shall send confirmation and reminder notifications to the guest. Notification failures shall not invalidate a confirmed appointment.                                                            |              UC11              |
|    BR-18    |        Authentication and Single Sign-On (SSO)        |                                         The system shall support authentication through Google and Facebook. Accounts registered via SSO must complete email verification before being allowed to book a Retreat Package. The system shall prevent duplicate account creation.                                         |              UC01              |
<<<<<<< HEAD
|    BR-19    |                 Zero Balance Bypass                   |                                         If a guest's total balance due is exactly 0 VND (e.g., fully pre-paid), the checkout process shall automatically bypass the payment gateway selection and complete the checkout immediately without generating a pending payment transaction.                                         |              UC22              |
=======
>>>>>>> origin/SourceCode

### 5.2 System Messages

|              |                |                |                                      |                                                                                |
| :----------: | :------------: | :------------: | :-----------------------------------: | :-----------------------------------------------------------------------------: |
| **\#** | **Code** | **Type** |           **Context**           |                                **Message**                                |
|      1      |     MSG-01     |    Success    |    Account registration successful    |           Registration successful. Please verify your email address.           |
|      2      |     MSG-02     |     Error     |             Login failed             |                           Invalid email or password.                           |
|      3      |     MSG-03     |    Success    |     Email verification successful     |                Your email has been verified. You may now log in.                |
|      4      |     MSG-04     |    Warning    |   Health data consent not provided   |         You must provide consent before submitting health information.         |
|      5      |     MSG-05     |    Success    |  Retreat Package booking successful  |                  Retreat Package booking created successfully.                  |
|      6      |     MSG-06     |     Error     |        Deposit payment failed        |                    Deposit payment failed. Please try again.                    |
|      7      |     MSG-07     |    Success    |          Check-in successful          |                        Check-in completed successfully.                        |
|      8      |     MSG-08     |     Error     |      No suitable Villa available      |                No available Villa could be found for allocation.                |
|      9      |     MSG-09     |    Success    |  Spa appointment booked successfully  |                      Spa appointment booked successfully.                      |
|      10      |     MSG-10     |     Error     | Therapist or therapy room unavailable |             No available Therapist or Therapy Room could be found.             |
|      11      |     MSG-11     |    Success    |    Meal order placed successfully    |                   Meal order has been recorded successfully.                   |
|      12      |     MSG-12     |    Warning    |        Meal contains allergens        | Warning: This meal contains ingredients listed in the guest?s allergy profile. |
|      13      |     MSG-13     |    Warning    | Outstanding charges during check-out |           Please settle all outstanding charges before checking out.           |
|      14      |     MSG-14     |    Success    |   Check-out completed successfully   |                        Check-out completed successfully.                        |
|      15      |     MSG-15     |    Success    |   Health data deleted successfully   |                   Health data has been deleted successfully.                   |
|      16      |     MSG-16     |    Success    |     Review submitted successfully     |                      Thank you for submitting your review.                      |
|      17      |     MSG-17     |    Success    |  Excel report exported successfully  |                          Report exported successfully.                          |
|      18      |     MSG-18     |     Error     |          Unauthorized access          |               You do not have permission to access this function.               |
|      19      |     MSG-19     |     Error     |        Unexpected system error        |        An unexpected system error has occurred. Please try again later.        |



