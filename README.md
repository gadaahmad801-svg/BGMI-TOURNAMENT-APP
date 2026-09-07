# BGMI ARENA - Esports Tournament & Scrims Platform

BGMI ARENA is a modern, mobile-first Android application designed for BGMI (Battlegrounds Mobile India) esports teams, solo gladiators, and tournament organizers. Built with Kotlin and Jetpack Compose (Material Design 3), it delivers real-time tournament discovery, automated bracket management, secure room credential delivery, live standings, and an engagement-based virtual coin economy.

---

## Key Features

1. **Tournament Discovery & Registration**
   - Filter by Solo, Duo, Squad, or Clan Showdowns.
   - Real-time slot counters with atomic capacity enforcement.
   - Trusted registration deadlines to prevent late entries.
   - Multi-player roster validation with character UID checks.

2. **Protected Match Room Hub**
   - In-game Room ID and Password distribution secured by role and participant authorization.
   - Synchronized live countdown timer (15-minute credential unlock window).
   - One-tap clipboard copy for rapid in-game joining.

3. **Virtual Coin Economy & Fair Play Disclosures**
   - 100% virtual, non-monetary coin reward system.
   - **Zero Real-Money Gambling or Cash-Out**: Coins cannot be purchased with fiat money, converted to cash, or transferred to external accounts.
   - Strict mathematical constraint: User virtual coin balances **can never drop below 5 coins**.
   - Transparent, auditable transaction ledger for every coin addition and deduction.

4. **Live Leaderboards & Deterministic Rankings**
   - Server-computed standings calculated from verified referee placement points and kill points.
   - Client-side write protection ensures competitive integrity.

5. **Admin Referee & Control Center**
   - Create and schedule tournaments across official maps (Erangel, Miramar, Sanhok, Vikendi, Livik).
   - Release encrypted room credentials.
   - Review match scores and award tournament points.
   - Player moderation (active, suspended) and tamper-resistant audit logs.

6. **Notification & Support System**
   - Real-time alerts for room details, match reminders, and tournament announcements.
   - Interactive helpdesk for submitting and tracking support inquiries.

---

## Architecture & Tech Stack

- **Language:** 100% Kotlin
- **UI Framework:** Jetpack Compose with Material Design 3 (M3)
- **Architecture Pattern:** MVVM (Model-View-ViewModel) + Single-Source-of-Truth Repository
- **State Management:** Kotlin Coroutines `StateFlow` and `asStateFlow` with lifecycle collection
- **Cloud Backend:** Firebase Auth (Email/Password & Google Sign-In with Credential Manager) + Cloud Firestore
- **Security:** Strict `firestore.rules` with RBAC, field-level diff restrictions, and timestamp validations
- **Indexing:** `firestore.indexes.json` for optimized composite queries

---

## Firebase Setup Instructions

BGMI ARENA includes full offline-first caching and built-in fallback data. To connect your live Firebase project:

1. **Create a Firebase Project:**
   - Go to the [Firebase Console](https://console.firebase.google.com/).
   - Add an Android App with package name `com.example` (or the `applicationId` defined in `app/build.gradle.kts`).
   - Download the generated `google-services.json` and place it in the `/app` directory:
     ```
     app/google-services.json
     ```

2. **Deploy Firestore Security Rules:**
   - Install the Firebase CLI (`npm install -g firebase-tools`).
   - Run:
     ```bash
     firebase deploy --only firestore:rules
     ```
   - This applies the strict rules defined in `firestore.rules` protecting user roles, coin balances, private room passwords, and audit logs.

3. **Deploy Firestore Composite Indexes:**
   - Run:
     ```bash
     firebase deploy --only firestore:indexes
     ```
   - This indexes compound queries for tournaments, matches, leaderboard standings, and notifications.

---

## Building and Running

### Prerequisites
- Android Studio Ladybug or newer
- JDK 17 or higher
- Android SDK 35 (`compileSdk 35`, `minSdk 24`)

### Build Debug APK
```bash
gradle assembleDebug
```

### Run Unit and Robolectric Tests
```bash
gradle :app:testDebugUnitTest
```

---

## Security Audit Summary

| Checkpoint | Status | Notes |
| :--- | :--- | :--- |
| Authentication & Session Flow | **PASS** | Complete login/registration with secure token session management |
| Role-Based Access Control (RBAC) | **PASS** | Server-authorized admin checks; normal users cannot become admins |
| Coin Balance Floor (>= 5) | **PASS** | Strictly enforced in Repository, ViewModel, and Firestore rules |
| Duplicate Registration Prevention | **PASS** | Guarded by UID matching and team membership checks |
| Protected Match Room Credentials | **PASS** | Only accessible by authorized participants and match referees |
| Client-Side Write Protection | **PASS** | Leaderboard, official match results, and audit logs are server-computed |
| Zero Real-Money Payments | **PASS** | No payment gateways, no fake payment screens, purely virtual engagement |
| No BGMI Password Collection | **PASS** | Only public Character Name and BGMI UID collected for matchmaking |
