Xoai Aura Retreat - Full-Stack Wellness Resort & Spa Management System
📌 Project Context & Overview
Course: Software Development Project (SWP391)  
Major: Software Engineering (SE) - Java Full-Stack specialization
Team Size: 5 Students (Role: Full-Stack Developer)  
Project Code: SWP391-HOS-03  
Problem Statement
Traditional Property Management Systems (PMS) only handle standard hotel room bookings. For Wellness Resorts, guests book comprehensive "Retreat Packages" (e.g., 5-Day Detox, 3-Day Mindfulness Yoga) that bundle luxury villas, specialized medical diets, and strict therapy schedules.  
This project implements a secure, integrated full-stack solution to automate complex multi-resource spa scheduling (matching available therapist + available room concurrently), dietary restrictions, and dynamic unified billing (Folio accounting).  
📂 Project Structure (Software Engineering Standards)
Following standard SE project lifecycles, our repository is strictly structured into separate phases as seen in our codebase:  
01_Planning/ - Sprint backlogs, timeline management, and project plans.  
02_Requirement/ - Business rules, software requirements specification (SRS), and Traceability Matrix.  
03_Design/ - Database ERD, UML Class diagrams, and Architecture Decision Records (ADR).  
04_Implement/ - Refactoring reports and Test-Driven Development (TDD) module templates.  
05_Development/auramoon/ - The core Spring Boot Maven source code directory.  
07_Reports/ & 08_Document_References/ - Academic deliverables and industry research materials.  
🛠️ Technology Stack & Architecture
Backend: Java 17, Spring Boot 3.x, Spring Security (RBAC implementation), Spring Data JPA.  
Database: MySQL / PostgreSQL utilizing ACID transaction isolation and pessimistic locks.  
Frontend UI: ReactJS / Thymeleaf, Bootstrap, Axios.  
Third-Party Integration Core:
VNPay Sandbox API: For handling secure reservation down-payments.  
Google Calendar API & SendGrid: Synchronizing real-time treatment appointments and sending 1-hour automated reminders.  
Google Identity: Single Sign-On (SSO) for smooth guest registration.  
⚙️ Core Modules & Allocation (Full-Stack Implementation)
The system is divided into 5 highly cohesive, decoupled modules. Each team member is responsible for a complete vertical slice (Database $\rightarrow$ Repository $\rightarrow$ Controller $\rightarrow$ Frontend UI).  
Module 1: Authentication & Sensitive Health Profiles (Data Privacy)
Implements secure onboarding with OAuth2 and email validation.  
Data Protection Compliance: Implements explicit consent checkboxes (non-pre-ticked) for collecting medical/allergy profiles to comply with Vietnam Decree 356/2025/ND-CP.  
Enforces the "Right to be Forgotten" allowing full database erasure of sensitive guest data upon request.  
Module 2: Villa & Retreat Package Booking
Enables customers to browse and filter holistic retreat packages based on wellness goals (e.g., Weight Loss, Stress Relief).  
Receptionist dashboard to manage live villa room statuses (Vacant, Occupied, Maintenance).  
Module 3: Dual-Resource Spa & Therapy Scheduler (High Complexity Core)
Simultaneous Resource Allocation: An appointment is only valid if both a specific Therapist AND a specialized Treatment Room are available at the chosen timeslot.  
Uses backend transaction locks to prevent race conditions and double-bookings.  
Module 4: F&B Operations & Dietary Guardrails
Automated filtering engine that completely hides dishes from the guest menu if they contain ingredients conflicting with the guest's allergy profile.  
Data Minimization: Kitchen staff can view summary allergy counts but are strictly restricted from viewing private customer medical histories via backend controller filters.  
Module 5: Central Guest Folio & Revenue Analytics
AHLEI Night Audit Standard: Uses the central RoomBooking_ID to dynamically calculate and aggregate package fees, add-on spa treatments, and a-la-carte restaurant bills into one consolidated invoice.  
Provides analytical charts (revenue breakdown) and monthly Apache POI Excel report exports for administrators.   
