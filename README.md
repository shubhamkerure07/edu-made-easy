<div align="center">

# 📚 EduClass (Edu Made Easy)

**Modern Interactive Online Tuition & Virtual Classroom Platform for Android**  
*Empowering educators and students with live virtual lectures, assignment pipelines, interactive quizzes, and premium 1-on-1 tutoring.*

[![Platform: Android](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://www.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-Material_3-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Room Database](https://img.shields.io/badge/Room_DB-SQLite-0284C7?style=for-the-badge&logo=sqlite&logoColor=white)](https://developer.android.com/training/data-storage/room)
[![Google Gemini](https://img.shields.io/badge/Google_Gemini-AI_Tutor-8E75C2?style=for-the-badge&logo=google&logoColor=white)](https://ai.google.dev/)
[![Deploy with Vercel](https://vercel.com/button)](https://vercel.com/new/clone?repository-url=https%3A%2F%2Fgithub.com%2Fshubhamkerure07%2Fedu-made-easy)
[![License: MIT](https://img.shields.io/badge/License-MIT-F59E0B?style=for-the-badge)](LICENSE)

</div>

---

## 🌐 Instant Live Web Simulator & Vercel Deploy

Even though EduClass is a native Kotlin Android application, the repository includes a responsive **interactive web simulator** (`web-preview/`) preconfigured with [`vercel.json`](vercel.json) for instant cloud deployment:

<div align="center">

[![Deploy with Vercel](https://vercel.com/button)](https://vercel.com/new/clone?repository-url=https%3A%2F%2Fgithub.com%2Fshubhamkerure07%2Fedu-made-easy)

</div>

---

## 📖 Overview

**EduClass (Edu Made Easy)** is a high-performance native Android application engineered with **Kotlin** and **Jetpack Compose**. It bridges the gap between students and educators by providing an all-in-one digital classroom experience. 

From streaming live interactive sessions and managing deadlines to taking timed quizzes and booking personalized 1-on-1 tutoring slots, EduClass eliminates educational friction through clean **Material 3** design and an offline-first **Room database** architecture.

---

## 🌟 Key Capabilities & Features

### 🎥 1. Live Virtual Session Hub (`LiveSessionScreen.kt`)
- **Upcoming Lecture Scheduling:** Clear countdown timers and schedules for upcoming live lectures.
- **Instant Meeting Launch:** One-tap join for virtual video lectures with low-latency connection.
- **Tutor Credentials & Syllabus:** View instructor qualifications, lesson agendas, and required prerequisites before class starts.

### 📝 2. Assignment Tracker & Submission Pipeline (`AssignmentScreen.kt`)
- **Deadline Monitoring:** Categorized views for *Pending*, *Submitted*, and *Graded* coursework.
- **File Attachment & Uploads:** Seamlessly attach homework, laboratory reports, and solution PDFs.
- **Feedback & Scores:** Direct teacher grading metrics, rubric breakdowns, and personalized correction remarks.

### 🧠 3. Interactive Timed Quizzes (`QuizScreen.kt`)
- **Real-Time Evaluation:** Multiple-choice question (MCQ) engine with dynamic countdown timers.
- **Immediate Result Breakdown:** Instant score computation with comprehensive answer explanations.
- **Performance Analytics:** Historical quiz tracking to pinpoint weak syllabus topics.

### 💎 4. Premium 1-on-1 Mentorship Booking (`PremiumScreen.kt`)
- **Personalized Tutoring:** Book dedicated 1-on-1 private tuition sessions tailored to specific subject doubts.
- **Flexible Time-Slot Calendar:** Select dates and times directly with preferred mentors.
- **Direct Doubt Clarification:** Private attention for competitive exams, laboratory experiments, or difficult coursework.

### 🤖 5. Gemini AI Study Copilot
- **Automated Doubt Assistance:** Inquire about complex topics and receive instantaneous, contextual breakdowns.
- **Revision Summaries:** Generate concise chapter summaries and flashcard-style key takeaways.

### 🗄️ 6. Offline-First Room Database (`EduDatabase.kt`)
- **Zero-Latency Data Access:** Course lists, downloaded lecture materials, and assignment history remain 100% accessible offline.
- **Automatic Sync:** Seamless background synchronization when an active internet connection is detected.

---

## 🏛️ System Architecture

The application is structured following official **Android Architecture Guidelines** using **MVVM (Model-View-ViewModel)** with reactive **StateFlow**:

```
app/src/main/java/com/example/
├── MainActivity.kt               # Single-activity container with Compose Navigation
├── database/                     # Data Layer (Room ORM)
│   ├── EduDatabase.kt            # Room Database singleton
│   ├── EduDao.kt                 # Data Access Object with Coroutine Flow queries
│   ├── EduRepository.kt          # Repository pattern mediating network & local cache
│   └── Entities.kt               # Room entities (Session, Assignment, Quiz, Booking)
├── ui/                           # Presentation Layer (Jetpack Compose)
│   ├── EduViewModel.kt           # Central ViewModel coordinating UI state flows
│   ├── screens/                  # Feature Screens
│   │   ├── MainAppScreen.kt      # Scaffold, TopAppBar & animated Navigation Bar
│   │   ├── LiveSessionScreen.kt  # Virtual live classes & session scheduler
│   │   ├── AssignmentScreen.kt   # Assignment submissions & grading
│   │   ├── QuizScreen.kt         # Timed MCQ quizzes & scoring engine
│   │   └── PremiumScreen.kt      # 1-on-1 premium mentor booking
│   └── theme/                    # Material 3 Styling (Color, Theme, Typography)
```

---

## 🛠️ Tech Stack & Dependencies

- **Language:** Kotlin 2.0+
- **UI Toolkit:** Jetpack Compose + Material Design 3
- **Local Persistence:** Android Room SQLite ORM
- **Concurrency & State:** Kotlin Coroutines & `StateFlow`
- **Architecture:** Android Architecture Components (ViewModel, Repository pattern)
- **AI Integration:** Google Gemini API
- **Build System:** Gradle (Kotlin DSL - `build.gradle.kts`)

---

## 🚀 Getting Started & Local Setup

### Prerequisites
- **Android Studio:** Ladybug (2024.2.1) / Koala or newer
- **JDK:** Java 17 or higher
- **Android SDK:** Compile SDK 34+, Min SDK 24 (Android 7.0 Nougat+)

### Installation & Run

```bash
# 1. Clone the repository
git clone https://github.com/shubhamkerure07/edu-made-easy.git
cd edu-made-easy

# 2. Open project in Android Studio
# Open Android Studio -> File -> Open... -> Select the 'edu-made-easy' folder

# 3. Build debug APK using Gradle CLI
./gradlew assembleDebug

# 4. Install and run directly on an emulator or connected device
./gradlew installDebug
```

---

## 👨‍💻 Author

**Shubham Kerure**  
*Mechatronics Engineering Student @ Mangalore Institute of Technology & Engineering (MITE)*  
- **GitHub:** [@shubhamkerure07](https://github.com/shubhamkerure07)  
- **Portfolio:** [portfolio-lac-two-76.vercel.app](https://portfolio-lac-two-76.vercel.app/)  
- **LinkedIn:** [Shubham Kerure](https://www.linkedin.com/in/shubham-kerure-23350938b)  
- **Email:** [shubhamkerure13@gmail.com](mailto:shubhamkerure13@gmail.com)

---

<div align="center">
<sub>Licensed under the MIT License • Built with Jetpack Compose & Material 3</sub>
</div>