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
- ✅ Automatic screenshots
- ✅ Marathon video recordings
- ✅ Marathon logcat collection

---

## 📱 Screenshots

### Habit Tracker application

<img width="277" height="297" alt="Снимок экрана — 2026-07-15 в 23 35 22" src="https://github.com/user-attachments/assets/1bf01ad9-cdb6-4e8c-9a89-8c9e3e15f13f" />


### Merged Allure report (Kaspresso + Marathon)

![Merged Allure report](https://github.com/user-attachments/assets/bfa8f932-8a47-4090-98e7-a47fd3148724)


---

## 📂 Project overview

The repository consists of:

- **Habit Tracker** — a simple Jetpack Compose application used as a target for UI testing.
- **Kaspresso UI tests** — native Android UI tests with readable step-based DSL.
- **Marathon** — parallel test execution with video recording and logcat collection.
- **Allure Merge** — a small Kotlin utility that combines device-side Kaspresso Allure results with Marathon runner-side artifacts into a single Allure report.

---

## 📊 Final Allure report

The generated report contains:

- ✅ Kaspresso steps
- ✅ Kaspresso screenshots
- ✅ Marathon videos
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

Build the application and test APKs

```bash
./gradlew app:assembleDebug app:assembleDebugAndroidTest
```

Run tests with Marathon

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
Kaspresso and Marathon generate different Allure results.
Kaspresso provides rich step-based reporting and screenshots, while Marathon generates execution metadata, videos, and logcat.
This project demonstrates one approach to combining both outputs into a single Allure report while preserving the strengths of each tool.
