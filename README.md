# Kaspresso + Marathon + Allure

Sample Android project demonstrating how to combine **Kaspresso**, **Marathon**, and **Allure** into a single reporting pipeline.

The repository includes a simple Jetpack Compose application, UI tests, Marathon configuration, and a utility for merging Allure results.

---

## Project overview

The project consists of:

- 📱 A sample Android application (Habit Tracker)
- ✅ UI tests written with Kaspresso
- 🚀 Marathon test execution
- 📊 Unified Allure reporting

The goal is to preserve Kaspresso step reporting while enriching the final report with Marathon artifacts such as videos and logcat.

---

## Application

A simple Jetpack Compose Habit Tracker application.

Features:

- Add a habit
- Display habits in a list
- Mark habits as completed

The application exists only as a target for automated UI testing.

---

## Test infrastructure

### Kaspresso

Used for:

- readable test DSL
- step-based reporting
- automatic screenshots
- assertions

### Marathon

Used for:

- test execution
- video recording
- logcat collection
- device orchestration

### Allure

Final reports include:

- Kaspresso steps
- screenshots
- Marathon videos
- Marathon logcat
- execution metadata

---

## Architecture

```
                 UI Tests (Kaspresso)
                          │
             ┌────────────┴────────────┐
             │                         │
             ▼                         ▼
   Device-side Allure          Marathon execution
        results                    artifacts
             │                         │
             └────────────┬────────────┘
                          ▼
                 Allure Merge Utility
                          ▼
                Merged Allure Report
```

---

## Project structure

```
.
├── app/                    # Sample Compose application
├── allure-merge/           # Utility for merging Allure results
├── Marathonfile            # Marathon configuration
└── README.md
```

---

## Running

Build APKs

```bash
./gradlew app:assembleDebug app:assembleDebugAndroidTest
```

Run Marathon

```bash
marathon
```

Merge Allure results

```bash
./gradlew :allure-merge:run
```

Open the report

```bash
allure serve build/reports/marathon/merged-allure-results
```

---

## Tech stack

- Kotlin
- Jetpack Compose
- Kaspresso
- Marathon
- Allure
- Gradle
- JUnit4

---

## Why this project?

Marathon and Kaspresso generate different Allure results.

This project demonstrates how to combine them into a single report while preserving:

- Kaspresso steps
- screenshots
- Marathon videos
- logcat
- execution metadata
