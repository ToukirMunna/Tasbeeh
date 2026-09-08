# 🏗️ System Architecture & Technical Reference

**Project Name**: Tasbeeh Pro  
**Package Name**: `com.toukir.tasbeeh`  
**Application ID**: `com.toukir.tasbeeh.pro`  

---

## 1. Core Tech Stack
* **Platform**: Android (Min SDK: 29 / Android 10+, Target SDK: 36, Compile SDK: 36)
* **Language & Tooling**: Kotlin, Jetpack Compose, Material 3, Gradle Kotlin DSL
* **Architecture**: Clean MVVM + Unidirectional Data Flow (UDF)
* **Design System**: Toukir Studio Unified Design System (TDS)
  - 3-tier elevation (Canvas -> Surface -> Inset) + 1dp tactile hairline rims
  - Dynamic `AppColorTheme` accents (Sacred Gold, Toukir Mint, Royal Sapphire, Velvet Amethyst, Rose Coral)
  - Light & Dark mode support with dynamic system bar synchronization
* **Icons**: Bespoke Phosphor vector icons via `StudioIcons.*` and `StudioIcon()` (Zero raw emojis, zero material-icons-extended bloat)
* **Persistence**:
  - Jetpack DataStore: User settings (`app_theme`, `app_color_theme`, audio, haptic, reminders)
  - Local JSON persistence: `tasbeeh_goals.json`, `tasbeeh_history.json`
* **Cloud & Networking**:
  - `FirebaseManager`: Firebase Auth + Cloud Firestore automatic data sync and community leaderboard
* **Audio & Haptics**:
  - Android MediaPlayer & VibrationEffect triggers on recitation increments and milestone reaches (100 counts)

---

## 2. Data Flow & State Hierarchy

```text
[ User Interaction ] ──> [ Composable UI ]
                                │
                                ▼  (events)
                       [ MainViewModel ]
                                │
                                ▼
                     [ TasbeehRepository ]
                     ┌──────────┴──────────┐
                     ▼                     ▼
          [ Local JSON & DataStore ]  [ FirebaseManager ]
```

---

## 3. Target Package Hierarchy (Decomposition Roadmap)

```text
com.toukir.tasbeeh/
├── data/
│   ├── AdhkarData.kt              <-- Curated Islamic Adhkar Library
│   ├── AchievementsData.kt        <-- Milestone & Streak Achievements
│   ├── DataStoreProvider.kt       <-- Jetpack DataStore Instance
│   ├── FirebaseManager.kt         <-- Firebase Auth & Firestore Sync
│   ├── LeaderboardEntry.kt        <-- Community Leaderboard Model
│   ├── ReminderManager.kt         <-- Periodic Toast / Worker Reminders
│   ├── TasbeehHistory.kt          <-- Historical Recitation Entity
│   └── TasbeehRepository.kt       <-- Core Persistence & Goal Orchestrator
├── utils/
│   ├── LanguageUtils.kt           <-- Language & Localization Helpers
│   └── NetworkUtils.kt            <-- Connectivity Verification
├── ui/
│   ├── common/                    <-- Shared StudioIcon, Cards, Rims
│   ├── theme/                     <-- Color.kt, Theme.kt, Type.kt, StudioIcons.kt
│   └── features/
│       ├── home/                  <-- HomeScreen, CounterRing, GoalCards
│       ├── counter/               <-- CounterScreen, TapZone, FullscreenHost
│       ├── library/               <-- TasbeehsListScreen, TasbeehDetailsScreen
│       ├── profile/               <-- ProfileScreen, ProfileHeader, Stats
│       ├── history/               <-- HistoryScreen, CalendarMonthView
│       ├── statistics/            <-- StatisticsScreen, RecitationCharts
│       ├── leaderboard/           <-- LeaderboardScreen, LeaderboardSettingsDialog
│       ├── settings/              <-- SettingsDialog, Appearance, Sound, Backup
│       └── dialogs/               <-- AddGoalDialog, CustomTasbeehDialog, etc.
└── MainActivity.kt                <-- Single-Activity Host
```
