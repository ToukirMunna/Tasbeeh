# 📜 Project History & Audit Log

> **Protocol**: Prepend new entries directly below the separator line (reverse chronological).

### Format Standard
```markdown
### [YYYY-MM-DD HH:MM AM/PM] - Short Feature / Fix Title
* **Files Modified**: `app/src/.../File.kt`, `app/src/main/res/values/strings.xml`
* **Changes Made**: Bullet-point breakdown of architectural, UI, or bug fix changes.
* **Rationale & Impact**: Why this change was made and how it affects the system.
```

---

<!-- PREPEND NEW ENTRIES BELOW THIS LINE -->

### [2026-09-09 01:54 AM] - Updated google-services.json with Debug SHA-1 OAuth Client
* **Files Modified**: `app/google-services.json`
* **Changes Made**:
  - Replaced `google-services.json` with updated configuration from Firebase Console containing the debug certificate SHA-1 (`59:67:11:DD:70:23:DC:1E:51:7B:75:B9:99:CF:9F:31:AC:C5:D7:81`) for `com.toukir.tasbeeh.pro`.
  - Rebuilt and deployed debug APK to physical device `39c507c2`.
* **Rationale & Impact**: Enables Google Sign-In and Cloud Sync authentication to function in local debug builds without OAuth API 10 errors.

### [2026-09-09 01:48 AM] - Phase 1 Refactoring: Decomposed Dialogs.kt and SettingsComponents.kt
* **Files Modified**: `res/values/strings.xml`, `ui/ProfileHeader.kt`, `ui/dialogs/DurationSelector.kt`, `ui/dialogs/AddToGoalDialog.kt`, `ui/dialogs/AddGoalDialog.kt`, `ui/dialogs/EditGoalDialog.kt`, `ui/dialogs/ManageGoalsDialog.kt`, `ui/dialogs/ManageGoalRowItem.kt`, `ui/dialogs/EditTasbeehDetailsDialog.kt`, `ui/dialogs/NameInputDialog.kt`, `ui/settings/SettingsSharedComponents.kt`, `ui/settings/LanguageSettingsSection.kt`, `ui/settings/AppearanceSettingsSection.kt`, `ui/settings/SoundSettingsSection.kt`, `ui/settings/ToastReminderSettingsSection.kt`, `ui/settings/BackupRestoreSettingsSection.kt`, `ui/settings/LeaderboardSettingsSection.kt`, `ui/settings/AboutSettingsSection.kt`, `ui/settings/SettingsScreenContent.kt`, `ui/Dialogs.kt` (deleted), `ui/SettingsComponents.kt` (deleted)
* **Changes Made**:
  - Fully retired `Dialogs.kt` (678 lines) and decomposed into 7 focused files under `ui/dialogs/`, keeping all composables under 50 lines and files under 210 lines.
  - Extracted `NameInputDialog.kt` from `ProfileHeader.kt`, curing `ProfileHeader.kt`'s line ceiling violation (reduced from 252 lines to 144 lines).
  - Fully retired `SettingsComponents.kt` (827 lines) and decomposed into 9 focused files under `ui/settings/` (all < 220 lines each).
  - Maintained `package com.toukir.tasbeeh.ui` across all new files for zero caller breakage in `MainAppContent.kt` and `SettingsDialog.kt`.
  - Added missing string resources in `res/values/strings.xml` to eliminate hardcoded user strings per Invariant 5.
* **Rationale & Impact**: Eliminates the two largest monolithic UI files in the project, restoring architectural purity, modular maintainability, and strict adherence to the 250-line file ceiling and 50-line Composable limit without any runtime regression.


### [2026-09-09 01:32 AM] - Added Dynamic Accent Color Selection System in Settings
* **Files Modified**: `ui/theme/Theme.kt`, `ui/theme/Color.kt`, `ui/MainViewModel.kt`, `ui/SettingsComponents.kt`, `ui/SettingsDialog.kt`, `ui/MainAppContent.kt`, `MainActivity.kt`, `res/values/strings.xml`
* **Changes Made**:
  - Defined `AppColorTheme` enum with 5 curated themes: Sacred Gold (`#F59E0B`), Toukir Mint (`#14B8A6`), Royal Sapphire (`#3B82F6`), Velvet Amethyst (`#A855F7`), and Rose Coral (`#FB7185`).
  - Added DataStore persistence under `app_color_theme` in `MainViewModel`.
  - Created `ColorSwatch` selector inside Appearance settings with tactile active rings and high-contrast checkmarks.
  - Dynamically updates `MaterialTheme.colorScheme` across the entire app in real time.
* **Rationale & Impact**: Allows users to personalize their spiritual recitation mood directly from Settings while keeping background and elevation neutrals rock solid.

### [2026-09-09 01:27 AM] - Implemented Sacred Gold & Velvet Charcoal Dark Mode
* **Files Modified**: `ui/theme/Color.kt`, `ui/theme/Theme.kt`
* **Changes Made**:
  - Replaced obsidian mint dark scheme with Sacred Gold / Illuminated Amber (`#F59E0B`) accent on Deep Velvet Charcoal (`#0C0D0E`) canvas.
  - Upgraded card surfaces (`#16181B`), tactile hairline rims (`#2E3137`), and luminous warm text tokens.
  - Preserved light mode completely intact.
* **Rationale & Impact**: Delivers a warm, illuminated, sacred aesthetic for evening recitation and reduces eye strain.

### [2026-09-09 01:00 AM] - Migrated to Bespoke Phosphor Vector Icon Pack & Purged Emojis
* **Files Modified**: `ui/theme/StudioIcons.kt`, `ui/common/StudioIcon.kt`, `app/build.gradle.kts`, `res/drawable/ic_lexi_*`, across all UI screens
* **Changes Made**:
  - Integrated 62 bespoke vector drawables into `StudioIcons` object.
  - Created `StudioIcon()` wrapper with tinted vector rendering.
  - Replaced all Material Icons across 30+ UI files.
  - Completely purged raw emojis (`🔥`, `👋`, etc.) and replaced them with vector drawables.
  - Removed `androidx.compose.material:material-icons-extended` dependency, slimming down APK and compilation footprint.
* **Rationale & Impact**: Unifies the icon design language with Toukir Studio apps, eliminates font-dependent emoji rendering inconsistencies, and reduces memory overhead.
