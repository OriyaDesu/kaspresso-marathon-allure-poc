# Android UI Testing with Kaspresso, Marathon & Allure

A sample Android project demonstrating a complete UI testing pipeline using **Kaspresso**, **Marathon**, and **Allure**.

The repository contains a simple Jetpack Compose application, native Android UI tests, Marathon configuration, and a utility for merging Kaspresso and Marathon Allure results into a single report.

---

## ✨ Features

- ✅ Sample Jetpack Compose application (Habit Tracker)
- ✅ Native Android UI tests written with Kaspresso
- ✅ Marathon test execution
- ✅ Unified Allure report
- ✅ Kaspresso step reporting
- ✅ Failure-only screenshots
- ✅ Failure-only Marathon video recordings
- ✅ Automatic Allure results merge
- ✅ Marathon logcat collection

---

## 📱 Screenshots

### Habit Tracker application

<img width="277" height="297" alt="Снимок экрана — 2026-07-15 в 23 35 22" src="https://github.com/user-attachments/assets/1bf01ad9-cdb6-4e8c-9a89-8c9e3e15f13f" />


### Merged Allure report (Kaspresso + Marathon)

<img width="632" height="397" alt="Снимок экрана — 2026-07-21 в 13 33 04" src="https://github.com/user-attachments/assets/b14b3b90-7a8d-4294-846f-333f4aab72b1" />

<img width="632" height="624" alt="image" src="https://github.com/user-attachments/assets/f9264f82-ee7b-4f69-9e88-90f1ebaf02c4" />




---

## 📂 Project overview

The repository consists of:

- **Habit Tracker** — a simple Jetpack Compose application used as a target for UI testing.
- **Kaspresso UI tests** — native Android UI tests with readable step-based DSL.
- **Marathon** — Android test execution with failure-only video recording, logcat collection and execution metadata.
- **Allure Merge** — a small Kotlin utility that combines device-side Kaspresso Allure results with Marathon runner-side artifacts into a single Allure report.

---

## 📈 Reporting pipeline

```text
Kaspresso
        │
        ├── Steps
        ├── Failure screenshots
        │
        ▼
Device Allure Results
        │
        ▼
Allure Merge
        ▲
        │
Marathon
        ├── Execution metadata
        ├── Failure videos
        └── Logcat
        │
        ▼
Merged Allure Report
```

## 📊 Final Allure report

The generated report contains:

- ✅ Kaspresso steps
- ✅ Failure screenshots
- ✅ Failure video recordings
- ✅ Marathon logcat
- ✅ Execution metadata

---

## 🏗 Project structure

```text
.
├── app/                    # Sample Jetpack Compose application
├── allure-merge/           # Utility for merging Allure results
├── Marathonfile            # Marathon configuration
└── README.md
```

---

## 🚀 Running

Run the complete pipeline:

```bash
./gradlew runMarathonWithMergedAllure
```

Open the report:

```bash
allure serve build/reports/marathon/merged-allure-results
```

---

## 🛠 Tech stack

- Kotlin
- Jetpack Compose
- Kaspresso
- Marathon
- Allure
- Gradle
- JUnit4

---

## 💡 Motivation
Kaspresso and Marathon generate complementary Allure artifacts. Kaspresso produces detailed step-based reports, while Marathon provides execution metadata, screen recordings and logcat. This project demonstrates how these artifacts can be merged into a single Allure report while avoiding redundant attachments by keeping screenshots and videos only for failed tests.
