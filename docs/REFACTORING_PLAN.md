# 🏛️ Master Refactoring Plan: Modernizing Tasbeeh to MasterTemplate Architecture

## Executive Summary
This implementation plan establishes a non-destructive, phased refactoring roadmap to bring **Tasbeeh Pro** into full alignment with the **MasterTemplate architecture** and **Toukir Studio Coding Invariants** (`AGENTS.md`).

### Non-Negotiable Invariants
1. **Zero Runtime Regressions**: 100% of existing behavior, gestures, haptics, sounds, and UI interactions remain identical.
2. **Zero Data Loss**: Sacred user data (`tasbeeh_goals.json`, `tasbeeh_history.json`, and Jetpack DataStore preferences) will not be touched or altered.
3. **The 250-Line Ceiling**: Every `.kt` file in `app/src/main/` must stay under 250 lines.
4. **The 50-Line Composable Rule**: Composable function bodies must not exceed 50 lines (extracting sections into sub-composables).
5. **Feature-First Packaging**: Transition from flat `com.toukir.tasbeeh.ui.*` into clean, modular feature pods.

---

## 1. Current State vs. Target State Audit

### Files Currently Violating the 250-Line Ceiling (13 Files)
| File | Current Lines | Target Decomposition |
| :--- | :---: | :--- |
| **`SettingsComponents.kt`** | **788** | Split into 6 dedicated section files (under 120 lines each). |
| **`Dialogs.kt`** | **657** | Split into 5 single-dialog files (under 130 lines each). |
| **`TasbeehRepository.kt`** | **614** | Extract JSON file persistence engines (`GoalStorage.kt`, `HistoryStorage.kt`). |
| **`MainAppContent.kt`** | **585** | Extract `AppDialogHost.kt` (dialog router) and `AppNavigationScaffold.kt`. |
| **`MainViewModel.kt`** | **352** | Extract state delegates (`SettingsDelegate.kt`, `SyncDelegate.kt`). |
| **`TasbeehDetailsScreen.kt`** | **334** | Extract `TasbeehGoalCard.kt` and `TasbeehHistoryList.kt`. |
| **`HistoryScreen.kt`** | **303** | Extract `MonthCalendarPicker.kt` and `DailyHistoryCard.kt`. |
| **`HomeComponents.kt`** | **292** | Extract `HomeCounterRing.kt` and `HomeActiveGoalCard.kt`. |
| **`StatisticsScreen.kt`** | **289** | Extract `WeeklyMonthlyBarChart.kt` and `StatsSummaryGrid.kt`. |
| **`FirebaseManager.kt`** | **286** | Extract `FirestoreSyncEngine.kt` and `FirebaseAuthHandler.kt`. |
| **`CounterComponents.kt`** | **284** | Extract `CounterTapZone.kt` and `CounterProgressRing.kt`. |
| **`CounterScreen.kt`** | **271** | Extract `CounterFullscreenHost.kt` and volume key listener. |
| **`TasbeehsListScreen.kt`** | **250** | Extract `AdhkarLibraryTab.kt` and `UserTasbeehsTab.kt`. |

### Duplicate Code Cleanup
- `com.toukir.tasbeeh.ui.AdhkarData.kt` (138L) vs `com.toukir.tasbeeh.data.AdhkarData.kt` (91L): Consolidate into single authoritative source in `data/AdhkarData.kt`.

---

## 2. Target Package & Feature Pod Hierarchy

```text
com.toukir.tasbeeh/
├── data/
│   ├── local/                      <-- JSON storage engines (GoalStorage, HistoryStorage)
│   ├── preferences/                <-- DataStoreProvider
│   ├── remote/                     <-- FirebaseManager, LeaderboardSync
│   ├── model/                      <-- TasbeehGoal, TasbeehHistory, LeaderboardEntry
│   └── TasbeehRepository.kt        <-- Public facade API (<250L)
├── di/
│   └── AppContainer.kt             <-- Lightweight manual DI (if needed)
├── ui/
│   ├── common/                     <-- StudioIcon, TactileCard, HairlineDivider
│   ├── theme/                      <-- Color.kt, Theme.kt, Type.kt, StudioIcons.kt
│   └── features/
│       ├── dialogs/                <-- AddGoalDialog, CustomTasbeehDialog, TargetCountDialog, etc.
│       ├── settings/               <-- SettingsDialog, AppearanceSection, SoundSection, BackupSection
│       ├── home/                   <-- HomeScreen, HomeCounterRing, GoalCards
│       ├── counter/                <-- CounterScreen, TapZone, RecitationHost
│       ├── library/                <-- TasbeehsListScreen, TasbeehDetailsScreen
│       ├── history/                <-- HistoryScreen, MonthCalendarPicker
│       ├── statistics/             <-- StatisticsScreen, RecitationCharts
│       ├── profile/                <-- ProfileScreen, ProfileHeader, ProfileStats
│       └── leaderboard/            <-- LeaderboardScreen, LeaderboardSettingsDialog
└── MainActivity.kt                 <-- Single Activity edge-to-edge host (<150L)
```

---

## 3. Step-by-Step Phase Roadmap

Each phase is self-contained, completely verified with `./gradlew assembleDebug`, installed on device `39c507c2`, and committed atomically.

### Phase 1: High-Impact UI Giants (Zero Package Change)
*Goal: Retire the two biggest files (`Dialogs.kt` and `SettingsComponents.kt`) without changing imports for existing callers.*

1. **Decompose `Dialogs.kt` (657 lines)**:
   - `ui/dialogs/AddGoalDialog.kt` (~110L): Dialog for setting a new daily goal.
   - `ui/dialogs/CustomTasbeehDialog.kt` (~120L): Dialog for adding a custom tasbeeh.
   - `ui/dialogs/TargetCountDialog.kt` (~90L): Dialog for modifying target count.
   - `ui/dialogs/NameInputDialog.kt` (~80L): Dialog for updating user profile name.
   - `ui/dialogs/GoalDetailsDialog.kt` (~130L): Read/edit details modal.
   - Retire `Dialogs.kt`.
2. **Decompose `SettingsComponents.kt` (788 lines)**:
   - `ui/settings/SettingsSharedComponents.kt` (~70L): `SettingsItem`, `SettingsGroup`, `SettingsDivider`, `SettingsSwitchItem`.
   - `ui/settings/AppearanceSettingsSection.kt` (~85L): Light/Dark chip selector + `ColorSwatch` accent picker.
   - `ui/settings/SoundSettingsSection.kt` (~70L): Audio & vibration switches.
   - `ui/settings/ToastReminderSettingsSection.kt` (~90L): Periodic toast reminder controls.
   - `ui/settings/BackupRestoreSettingsSection.kt` (~110L): JSON export/import & cloud auth.
   - `ui/settings/AboutSettingsSection.kt` (~60L): App version, developer bio, credits.
   - Retire monolithic `SettingsComponents.kt`.

### Phase 2: Screen Modularization (Under 250 Lines Each)
*Goal: Bring all remaining screens and component files below 250 lines.*

1. **`MainAppContent.kt` (585 lines $\rightarrow$ 2 files < 250L)**:
   - Extract `AppDialogRouter.kt`: Handles showing the 10+ modal dialog states.
   - `MainAppContent.kt`: Retains only Scaffold, BottomNavigationBar, and Tab navigation switching.
2. **`CounterScreen.kt` & `CounterComponents.kt` (555 lines $\rightarrow$ 3 files < 200L)**:
   - `CounterGestureZone.kt`: Fullscreen tap zone and count animations.
   - `CounterToolbar.kt`: Pure black mode, audio toggle, reset.
   - `CounterScreen.kt`: Volume key dispatcher and screen lifecycle.
3. **`TasbeehDetailsScreen.kt` & `TasbeehsListScreen.kt` (584 lines $\rightarrow$ 3 files < 200L)**:
   - `TasbeehListItemCard.kt`: Reusable card for library and custom tabs.
   - `TasbeehGoalSection.kt`: Progression bars and daily milestones.
4. **`HistoryScreen.kt` & `StatisticsScreen.kt` (592 lines $\rightarrow$ 3 files < 200L)**:
   - `HistoryCalendarMonthPicker.kt`: Compact monthly grid.
   - `StatisticsBarChart.kt`: Canvas-drawn weekly and monthly activity bars.

### Phase 3: Data & Repository Layer Clean-Up
*Goal: Decouple `TasbeehRepository` without altering file storage or DataStore schemas.*

1. **Storage Decoupling**:
   - `data/local/GoalFileStorage.kt`: Reads and writes `tasbeeh_goals.json`.
   - `data/local/HistoryFileStorage.kt`: Reads and writes `tasbeeh_history.json`.
   - `TasbeehRepository.kt`: Remains the sole public orchestrator, delegating file I/O to the storage classes. Line count drops from 614L to ~200L.
2. **Remove Redundant Code**:
   - Consolidate `ui/AdhkarData.kt` into `data/AdhkarData.kt`.
3. **`MainViewModel.kt` (352 lines $\rightarrow$ <220L)**:
   - Extract `SyncManagerDelegate.kt` for cloud sync scheduling.

### Phase 4: Final Polishing & Verification
1. Full line count audit across all `.kt` files in `app/src/main/` confirming **0 files > 250 lines**.
2. Run full test suite: `./gradlew testDebugUnitTest`.
3. Build and verify APK: `./gradlew assembleDebug`.
4. Deploy and verify on connected physical device `39c507c2`.
5. Update `ARCHITECTURE.md` and `LOGS.md`.

---

## 4. Execution Readiness

To execute this smoothly in a **fresh conversation**:
1. Start the new conversation.
2. The agent will read `AGENTS.md`, `ARCHITECTURE.md`, and this `implementation_plan.md`.
3. Give the imperative command: `"Execute Phase 1 of the implementation plan"`.
