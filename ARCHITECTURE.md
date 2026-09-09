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
│   ├── AchievementsData.kt        <-- Milestone & Streak Achievements
│   ├── AdhkarData.kt              <-- Curated Islamic Adhkar Library
│   ├── DataStoreProvider.kt       <-- Jetpack DataStore Instances
│   ├── FirebaseManager.kt         <-- Firebase Auth & Orchestrator Facade
│   ├── LeaderboardEntry.kt        <-- Community Leaderboard Model
│   ├── ReminderManager.kt         <-- Periodic Toast / Worker Reminders
│   ├── TasbeehHistory.kt          <-- Historical Recitation Entity
│   ├── TasbeehRepository.kt       <-- Facade delegating to specialized data stores
│   ├── repository/
│   │   ├── GoalsDataStore.kt      <-- Goals persistence, defaults & distinct aggregation
│   │   ├── HistoryDataStore.kt    <-- Daily history records, streaks, period totals & migration
│   │   ├── BackupRestoreManager.kt<-- JSON Export/Import & Non-destructive backup merging
│   │   ├── AchievementManager.kt  <-- Achievement criteria evaluation & unlock storage
│   │   └── SettingsDataStore.kt   <-- DataStore preferences facade
│   └── cloud/
│       └── FirebaseLeaderboardManager.kt <-- Firestore leaderboard queries & cloud submissions
├── utils/
│   ├── LanguageUtils.kt           <-- Language & Localization Helpers
│   └── NetworkUtils.kt            <-- Connectivity Verification
├── ui/
│   ├── common/                    <-- Shared StudioIcon, Cards, Rims
│   ├── theme/                     <-- Color.kt, Theme.kt, Type.kt, StudioIcons.kt
│   ├── dialogs/                   <-- Modularized dialog components (<250 lines)
│   ├── AppNavHost.kt              <-- Navigation graph orchestrator
│   ├── AppNavTransitions.kt       <-- Nav animated slide/fade transition specs
│   ├── NavScreens.kt              <-- Top-level navigation destination routers
│   ├── NavWrappers.kt             <-- Nav destination wrappers & tab containers
│   ├── TasbeehScaffoldContent.kt  <-- Scaffold layout & inner content sub-composables
│   ├── TasbeehAppState.kt         <-- Navigation & lifecycle state containers
│   └── (Feature Screens)          <-- All screens decomposed into surgical sub-composables
├── MainActivity.kt                <-- Single-Activity Host
└── (test) domain/
    ├── GoalsCalculationTest.kt    <-- Distinct dhikr aggregation unit tests
    ├── StreakCalculationLogicTest.kt <-- Daily streak calculation test suite
    ├── PeriodAggregationLogicTest.kt <-- Daily/Weekly/Monthly interval aggregation tests
    └── BackupMergeLogicTest.kt    <-- Non-destructive backup/restore merge invariant tests
```
