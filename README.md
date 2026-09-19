# USIU-Africa Community Service Management System (CMS 3700 M)

An Android mobile application built for United States International University - Africa (USIU-Africa) to manage and streamline community service placements, attendance tracking, supervisory evaluations, report reviews, and administrative oversight.

---

## Features & Modules

### 1. 🎓 Student Module
- **Browse Opportunities:** View active approved placement organizations with real-time capacity and location details.
- **Service Track Eligibility:** 
  - *Hands-on track:* Full-time students (90 service hours, 3 days/week, 3 hrs/day, 10 weeks).
  - *Project-based track:* Part-time/working students (requires proof of employment and coordinator approval).
- **Application & Registration:** Submit applications to partner organizations with automated capacity checks; complete site registrations within the university 3-week deadline.
- **Service Diary & Hours Tracking:** Log daily service hours (0.5 to 12.0 hours) with supervisor verification. Clear distinction between *recorded hours* and *supervisor-verified hours*.
- **Absence Monitoring:** Proactive alerts when approaching the absence threshold (2 site / 2 class absences) without automated non-completion, ensuring academic due process.
- **Report Submissions:** Upload mid-semester and final reports directly from device storage.

### 2. 👷 Community Supervisor Module
- **Placement Review:** Approve or decline student applications with atomic capacity locking.
- **Diary Entry Verification:** Approve logged hours (crediting verified hours atomically) or flag entries with notes for student correction.
- **Attendance Management:** Record student attendance per visit.
- **Issue Reporting:** Escalate incidents directly to the course coordinator.

### 3. 🗂️ Course Coordinator Module
- **Partner Organization Directory:** Manage and approve community partner organizations and placement opportunities.
- **Eligibility Screening:** Review student track selection, registration deadlines, and supporting documents.
- **Site Registration Approvals:** Authorize student community service placements.
- **Supervision History:** Log and track on-site supervisory visits.
- **Course Configuration:** Dynamically configure course deadlines, minimum required hours, and absence thresholds.
- **Audit Logs:** View an immutable audit trail of critical system actions.

### 4. 📚 Academic Lecturer Module
- **Student Progress Dashboard:** Track student completion percentages, verified hours, and absence counts.
- **Report Evaluation & Grading:** Review submitted academic reports (mid/final), assign grades (0–100), and provide feedback.

### 5. 🔐 System Administrator Module
- **User Management:** Search, view, and manage user accounts and assign staff roles (`COORDINATOR`, `LECTURER`, `SUPERVISOR`, `ADMIN`).
- **Account Controls:** Activate or suspend accounts with audit reasons.
- **System-wide Audit Trail:** Access complete chronological logs of sensitive actions and permissions changes.

---

## Architecture & Tech Stack

- **Platform:** Android (Java 17 / compileSdk 34 / minSdk 24)
- **UI Framework:** AndroidX, Material Design Components (MDC 1.11.0)
- **Backend / Cloud Services:** Google Firebase (BOM 32.8.0)
  - Firebase Authentication (Email/Password with institutional `@usiu.ac.ke` domain validation)
  - Cloud Firestore (Offline persistence enabled, atomic transactions, security rules)
  - Firebase Cloud Storage (PDF/Image uploads capped at 5 MB)
- **Security & Privacy:**
  - Zero-trust public registration (students only; staff accounts elevated by admin)
  - Immutable audit logs (`auditLogs` collection protected by Firestore security rules)
  - Cleartext HTTP traffic blocked via `network_security_config.xml`
  - Backup and device-to-device transfers disabled via `data_extraction_rules.xml`

---

## Setup & Running

### Prerequisites
- [Android Studio Iguana | Hedgehog or newer](https://developer.android.com/studio)
- JDK 17 or newer
- A Google Firebase account (Spark free tier is fully supported)

### 1. Firebase Configuration
1. Open the [Firebase Console](https://console.firebase.google.com/) and create a project.
2. Add an Android app with package name:
   ```
   com.usiu.communityservice
   ```
3. Enable **Authentication** (Email/Password provider).
4. Create a **Cloud Firestore** database (Production mode).
5. Enable **Cloud Storage**.
6. Download `google-services.json` and place it at:
   ```
   app/google-services.json
   ```
7. Deploy security rules using the Firebase CLI:
   ```bash
   firebase deploy --only firestore:rules,storage
   ```

### 2. Opening in Android Studio
1. Open Android Studio.
2. Click **File > Open...** and select this project directory.
3. Allow Gradle to sync and download necessary dependencies.
4. Select a connected device or an Android Virtual Device (API 24+).
5. Click **Run** (`Shift + F10`).

### 3. Provisioning the Initial Admin Account
1. Register an account within the app using an `@usiu.ac.ke` email address.
2. In the Firebase Console, navigate to the `users` collection in Firestore.
3. Edit the user document and set:
   - `role`: `"ADMIN"`
   - `accountStatus`: `"ACTIVE"`
4. Log back in to access the Administrator Dashboard and provision other coordinators, lecturers, and supervisors.

---

## License

This project is developed for USIU-Africa academic purposes under CMS 3700 M.
