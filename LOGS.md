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

### [2026-10-30 08:35 PM] - Production Release Build Assembly
* **Files Modified**: `app/proguard-rules.pro`, `app/build/outputs/apk/release/app-release.apk`
* **Changes Made**:
  - Configured fast R8 shrinking without optimization/obfuscation passes (`-dontoptimize`, `-dontobfuscate`) to maintain small APK footprint while eliminating bytecode rewrite overhead.
  - Successfully built signed release APK using keystore credentials from `local.properties`.
  - Verified APK signature (v2 scheme valid) and confirmed package identifier `com.toukirstudio.tasbeeh` with a compact ~11.6 MB file size.
* **Rationale & Impact**: Produces a production-ready, signed release APK with the new package identifier, streamlined app assets, and zero obfuscation overhead.

### [2026-10-30 08:26 PM] - Solidified Fingertip Stroke & Splash Screen Polish
* **Files Modified**: `app/src/main/res/drawable/ic_splash_tasbeeh_hand.png`, `app/src/main/res/mipmap-*/ic_launcher_foreground.png`, `app/src/main/res/mipmap-*/ic_launcher.png`, `app/src/main/res/mipmap-*/ic_launcher_round.png`
* **Changes Made**:
  - **Fortified Fingertip Arch**: Reinforced the stroke thickness at the apex of the middle fingertip with a solid rounded contour, preventing anti-aliasing subpixel dropout at lower icon resolutions.
  - **Asset Regeneration**: Updated both launcher icons across all density buckets and the high-res splash screen graphic (`ic_splash_tasbeeh_hand.png`).
  - **Verification & Deployment**: Executed unit tests and `assembleDebug`, followed by successful stream installation to active device (`192.168.1.106:5555`).
* **Rationale & Impact**: Ensures the praying hand silhouette remains fully connected, closed, and crisp across all Android launcher icon sizes and system App Info dialogs.

### [2026-10-30 08:18 PM] - Middle Finger Contour Closure & Unified Splash Screen Redesign
* **Files Modified**: `app/src/main/res/drawable/ic_splash_logo.xml`, `app/src/main/res/drawable/ic_splash_tasbeeh_hand.png`, `app/src/main/java/com/toukirstudio/tasbeeh/ui/splash/SplashScreen.kt`, `app/src/main/res/mipmap-*/ic_launcher_foreground.png`, `app/src/main/res/mipmap-*/ic_launcher.png`, `app/src/main/res/mipmap-*/ic_launcher_round.png`
* **Changes Made**:
  - **Closed Finger Contour**: Seamlessly bridged the open arch gap at the tip of the middle finger on the praying hand artwork across all launcher density buckets.
  - **Unified Splash Screen**: Integrated the identical handcrafted Tasbeeh hand graphic into Android 12+ system splash (`ic_splash_logo.xml`) and the Compose post-splash animated screen (`SplashScreen.kt`) with smooth breathing pulse animations and dynamic theme primary color tinting.
  - **Deployment**: Stream installed via wireless ADB to device (`192.168.1.106:5555`) and launched.
* **Rationale & Impact**: Resolves the open fingertip visual defect and creates end-to-end aesthetic consistency from launcher icon to splash screen to home dashboard.

### [2026-10-30 11:42 AM] - Light Luminous Canvas Icon with Warm Bronze Accent
* **Files Modified**: `app/src/main/res/drawable/ic_launcher_background.xml`, `app/src/main/res/mipmap-*/ic_launcher_foreground.png`, `app/src/main/res/mipmap-*/ic_launcher.png`, `app/src/main/res/mipmap-*/ic_launcher_round.png`
* **Changes Made**:
  - **Light Background**: Transitioned background from dark charcoal to a luminous TDS Cool Slate gradient (`#FFFFFF` &rarr; `#F0F2F5` &rarr; `#E4E8EE`) with a subtle `#D2D8E2` tactile rim.
  - **High-Contrast Warm Gold/Bronze Stroke**: Shifted icon stroke color to burnished bronze-gold (`#92400E`), achieving WCAG AAA contrast against the bright surface.
  - **Extended Breathing Room**: Scaled graphic footprint down to ~45% coverage for maximum padding and zero border clipping.
  - **Deployment**: Verified with `assembleDebug` and stream-installed to device.
* **Rationale & Impact**: Delivers a crisp, lightweight icon aesthetic that pops brightly on both light and dark Android wallpapers.

### [2026-10-30 11:40 AM] - Icon Background Enhancement & Optical Scale Adjustment
* **Files Modified**: `app/src/main/res/drawable/ic_launcher_background.xml`, `app/src/main/res/mipmap-*/ic_launcher_foreground.png`, `app/src/main/res/mipmap-*/ic_launcher.png`, `app/src/main/res/mipmap-*/ic_launcher_round.png`
* **Changes Made**:
  - **Optical Scale**: Scaled down graphic by ~20% (from 68% to 54% viewport coverage) providing balanced breathing room and eliminating edge crowding.
  - **Elevated Gradient Background**: Replaced flat black with a linear slate/charcoal gradient (`#232730` &rarr; `#181B21` &rarr; `#111317`) accented by a `#323744` hairline perimeter rim.
  - **Device Verification**: Built with `assembleDebug` and stream-installed to device.
* **Rationale & Impact**: Refines launcher icon visual hierarchy with proper margins and depth contrast against diverse home screen wallpapers.

### [2026-10-30 11:37 AM] - High-Fidelity Golden App Icon Integration
* **Files Modified**: `app/src/main/res/mipmap-*/ic_launcher_foreground.png`, `app/src/main/res/mipmap-*/ic_launcher.png`, `app/src/main/res/mipmap-*/ic_launcher_round.png`, `app/src/main/res/mipmap-*/ic_launcher_monochrome.png`, `app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml`, `app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml`
* **Changes Made**:
  - Replaced vector approximation with direct high-fidelity extraction of the user's reference graphic (praying hand, tasbeeh bead loop, cuff with button, tassel).
  - Scaled and centered with optical padding across all density buckets (`mdpi`, `hdpi`, `xhdpi`, `xxhdpi`, `xxxhdpi`) in the app's Sacred Gold (`#F59E0B`) accent on Velvet Obsidian (`#0C0D0E`).
  - Successfully built and stream-installed to connected device via wireless ADB.
* **Rationale & Impact**: Guarantees pixel-accurate rendering of the requested icon on physical device home screens and app drawers without clipping or black box artifacts.

### [2026-10-30 11:32 AM] - Pure Vector Math App Icon Redesign with Sacred Gold Theme
* **Files Modified**: `app/src/main/res/drawable/ic_launcher_background.xml`, `app/src/main/res/drawable/ic_launcher_foreground.xml`, `app/src/main/res/drawable/ic_launcher_monochrome.xml`
* **Changes Made**:
  - **Vector Icon Geometry**: Recreated the praying hand, angled sleeve cuff with button, and hanging Tasbeeh loop with 13 distinct circular beads and minaret tassel using pure Android Vector Drawable paths.
  - **Color Palette Alignment**: Configured foreground strokes to the app's Sacred Gold (`#F59E0B`) accent and the background to TDS Deep Velvet Obsidian (`#0C0D0E`) with a fine 1.dp hairline perimeter rim (`#2E3137`).
  - **Android 13+ Monochrome Themed Icon Support**: Implemented `ic_launcher_monochrome.xml` for dynamic Material You wallpaper matching.
  - **Verification**: Built with Gradle daemon via `assembleDebug` with 0 resource errors.
* **Rationale & Impact**: Replaces previous icon with an authentic, handcrafted vector Tasbeeh illustration harmonized with the app's dark velvet and golden design identity.

### [2026-10-30 11:15 AM] - Refactor Package Name to `com.toukirstudio.tasbeeh` & Configure Dedicated Debug Flavor
* **Files Modified**: `app/build.gradle.kts`, `app/src/main/AndroidManifest.xml`, `app/proguard-rules.pro`, `app/google-services.json`, `app/src/main/res/values/strings.xml`, `app/src/main/res/values-bn/strings.xml`, `ARCHITECTURE.md`, `AGENTS.md`, and all 85+ production and test Kotlin source files moved from `com.toukir.tasbeeh` to `com.toukirstudio.tasbeeh`
* **Changes Made**:
  - **Namespace & Package Migration**: Refactored root package name and namespace from `com.toukir.tasbeeh` to `com.toukirstudio.tasbeeh` across main, test, and androidTest source roots. Batch-migrated all package declarations and imports.
  - **Dedicated Debug Flavor (`.debug`)**: Configured `applicationIdSuffix = ".debug"` and `versionNameSuffix = "-debug"` in `buildTypes.debug`. Production builds retain root `com.toukirstudio.tasbeeh`.
  - **Dynamic Application Labels**: Added manifest placeholders so debug builds are labeled `Tasbeeh (Debug)` / `তাসবীহ (ডিবাগ)` and release builds are labeled `Tasbeeh` / `তাসবীহ`.
  - **ProGuard & Google Services Preparedness**: Updated `-keep class com.toukirstudio.tasbeeh.**` in `proguard-rules.pro`, and registered interim client identifiers in `google-services.json` so both `debug` and `release` compile cleanly.
  - **Verification**: Attached to warm Gradle daemon and ran `testDebugUnitTest` and `assembleDebug` with 100% pass rate.
* **Rationale & Impact**: Establishes proper namespace branding under `com.toukirstudio.tasbeeh`, enables simultaneous side-by-side device installations of debug and production builds without APK package collision, and sets up clean separation for Firebase project integration.

### [2026-09-09 12:25 PM] - Dynamic Language Switching, Zero-Leak Bengali Localization & Theme-Adaptive Vector Avatars
* **Files Modified**: `app/src/main/java/com/toukir/tasbeeh/MainActivity.kt`, `app/src/main/java/com/toukir/tasbeeh/utils/LanguageUtils.kt`, `app/src/main/java/com/toukir/tasbeeh/ui/common/UserAvatar.kt`, `app/src/main/java/com/toukir/tasbeeh/ui/ProfileHeader.kt`, `app/src/main/java/com/toukir/tasbeeh/ui/LeaderboardComponents.kt`, `app/src/main/java/com/toukir/tasbeeh/ui/dialogs/NameInputDialog.kt`, `app/src/main/java/com/toukir/tasbeeh/ui/HomeScreen.kt`, `app/src/main/java/com/toukir/tasbeeh/ui/HomeComponents.kt`, `app/src/main/java/com/toukir/tasbeeh/ui/LongTermGoals.kt`, `app/src/main/java/com/toukir/tasbeeh/ui/GoalProgressCard.kt`, `app/src/main/java/com/toukir/tasbeeh/ui/TasbeehsListScreen.kt`, `app/src/main/java/com/toukir/tasbeeh/ui/TasbeehListCard.kt`, `app/src/main/java/com/toukir/tasbeeh/ui/TasbeehDetailsScreen.kt`, `app/src/main/java/com/toukir/tasbeeh/ui/DailyHistoryComponents.kt`, `app/src/main/java/com/toukir/tasbeeh/ui/DetailStatsScreen.kt`, `app/src/main/java/com/toukir/tasbeeh/ui/ProfileHistory.kt`, `app/src/main/java/com/toukir/tasbeeh/ui/YearExpandableGroup.kt`, `app/src/main/java/com/toukir/tasbeeh/ui/CounterScreen.kt`, `app/src/main/java/com/toukir/tasbeeh/ui/NavScreens.kt`, `app/src/main/res/drawable/female.jpg`, `app/src/main/res/drawable/male.jpg`
* **Changes Made**:
  - **Dynamic 0ms Language Switching**: Wrapped root composition in `MainActivity.kt` with `CompositionLocalProvider(LocalConfiguration provides localizedConfiguration, LocalContext provides localizedContext)` and added dynamic `AppCompatDelegate.setApplicationLocales(...)` synchronization on `settings.language` changes, allowing instant language switching without requiring an app restart.
  - **Bengali Locale & Numeral Leak Elimination**:
    - Propagated `language` to `HomeComponents.kt`, `HomeScreen.kt`, `LongTermGoals.kt`, `GoalProgressCard.kt`, `TasbeehListCard.kt`, `TasbeehDetailsScreen.kt`, `TasbeehsListScreen.kt`, and `YearExpandableGroup.kt`.
    - Converted all remaining English numerals (streaks, years, days active, goal totals) to localized numerals via `formatNumber(..., language)`.
    - Updated `DateTimeFormatter` instances across `DailyHistoryComponents.kt`, `DetailStatsScreen.kt`, and `ProfileHistory.kt` to dynamically use `getLocaleForLanguage(language)`.
    - Added reactive `remember` keys `(goal.name, language, context)` to `AdhkarLibrary.getLocalizedName` lookups to eliminate stale cache across language switches.
  - **Theme-Adaptive Pure Vector Avatars (`UserAvatar.kt`)**:
    - Created high-fidelity Compose `Canvas` vector avatar system for male (taqiyah prayer cap, beard, kurta) and female (flowing draped hijab, underscarf band, abaya) profiles.
    - Dynamically takes colors from active `MaterialTheme.colorScheme` tokens (`primary`, `primaryContainer`, `onPrimaryContainer`), adapting in real time to the selected color theme (Gold, Mint, Sapphire, Rose, Amethyst).
    - Integrated `UserAvatar` into `ProfileHeader.kt`, `LeaderboardComponents.kt`, and `NameInputDialog.kt`.
    - Deleted legacy raster files `female.jpg` and `male.jpg`.
* **Rationale & Impact**: Guarantees zero latency on language toggles, ensures complete Bengali localization across all greetings, dates, and numerals without app restarts, and introduces a crisp, scalable, theme-harmonized vector avatar system.

### [2026-09-09 11:53 AM] - Data Integrity, Cloud Persistence & WCAG AA Color Contrast Remediation
* **Files Modified**: `app/src/main/java/com/toukir/tasbeeh/data/repository/HistoryDataStore.kt`, `app/src/main/java/com/toukir/tasbeeh/data/repository/BackupRestoreManager.kt`, `app/src/main/java/com/toukir/tasbeeh/data/FirebaseManager.kt`, `app/src/main/java/com/toukir/tasbeeh/data/cloud/FirebaseLeaderboardManager.kt`, `app/src/main/java/com/toukir/tasbeeh/ui/theme/Theme.kt`, `app/src/test/java/com/toukir/tasbeeh/domain/BackupMergeLogicTest.kt`
* **Changes Made**:
  - **Non-Destructive History Reset Merge**: Fixed history collision logic in `HistoryDataStore.kt` where an existing entry on `dateForHistory` was previously replaced; it now non-destructively merges details and takes `maxOf` totals.
  - **Calendar Boundary Goal Resets**: Updated `HistoryDataStore.kt` duration reset checks from `ChronoUnit.MONTHS` to `YearMonth` boundary transitions to prevent short-month goal reset failures (e.g. Feb 28).
  - **Non-Destructive Achievement & Date Merge**: Replaced shallow map `+` in `BackupRestoreManager.kt` with a `maxOf` count merge across local and remote achievements, and preserved the latest `LAST_RESET_DATE_KEY` when restoring older backups.
  - **Cloud Sync Leaderboard Configuration**: Added `leaderboardUsername`, `isAnonymous`, and `isLeaderboardEnabled` to Firestore root document sync and cloud restore in `FirebaseManager.kt`.
  - **Leaderboard Duplicate Counting Prevention**: Switched from `goals.sumOf { it.dailyCount }` to `distinctDailyTotal(goals)` in `FirebaseLeaderboardManager.kt`.
  - **WCAG AA Contrast Remediation**: Darkened `AppColorTheme.Gold.primaryLight` to `#92400E` (6.32:1 on canvas, 7.09:1 on surface) and `Rose.primaryLight` to `#BE123C` (5.60:1 on canvas, 6.29:1 on surface), fully complying with WCAG AA/AAA.
  - **Unit Test Coverage**: Added automated test coverage in `BackupMergeLogicTest.kt` for achievement count max-merging and reset date reconciliation.
* **Rationale & Impact**: Guarantees zero user data loss across resets and backup restores, ensures full cloud persistence for leaderboard settings, eliminates double-counting on the leaderboard, and brings light theme color accents into full compliance with WCAG AA contrast standards.

### [2026-09-09 02:47 AM] - Complete 8-Pillar Architectural Audit, Remediation & Automated Domain Test Suites
* **Files Modified**: `app/src/main/java/com/toukir/tasbeeh/data/TasbeehRepository.kt`, `data/FirebaseManager.kt`, `data/ReminderManager.kt`, `data/repository/GoalsDataStore.kt`, `data/repository/HistoryDataStore.kt`, `data/repository/BackupRestoreManager.kt`, `data/repository/AchievementManager.kt`, `data/cloud/FirebaseLeaderboardManager.kt`, `ui/MainAppContent.kt`, `ui/TasbeehScaffoldContent.kt`, `ui/TasbeehAppState.kt`, `ui/AppNavHost.kt`, `ui/AppNavTransitions.kt`, `ui/NavScreens.kt`, `ui/NavWrappers.kt`, `ui/CounterScreen.kt`, `ui/CounterEffects.kt`, `ui/HomeScreen.kt`, `ui/HomeComponents.kt`, `ui/LeaderboardScreen.kt`, `ui/LeaderboardViews.kt`, `ui/TasbeehDetailsScreen.kt`, `ui/TasbeehDetailsComponents.kt`, `ui/TasbeehsListScreen.kt`, `ui/TasbeehListCard.kt`, `ui/StatisticsScreen.kt`, `ui/StatisticsComponents.kt`, `ui/HistoryScreen.kt`, `ui/DailyHistoryComponents.kt`, `ui/ProfileHeader.kt`, `ui/ProfileHistory.kt`, `ui/ProfileCharts.kt`, `ui/Components.kt`, `ui/theme/Theme.kt`, `res/values/strings.xml`, `res/values-bn/strings.xml`, `app/src/test/java/com/toukir/tasbeeh/domain/GoalsCalculationTest.kt`, `StreakCalculationLogicTest.kt`, `PeriodAggregationLogicTest.kt`, `BackupMergeLogicTest.kt`, `ARCHITECTURE.md`
* **Changes Made**:
  - **Pillar 1 (250-Line Ceiling)**: Audited all 85 production `.kt` files. Decomposed all 7 monolithic violators (`TasbeehRepository.kt`, `MainAppContent.kt`, `CounterScreen.kt`, `HomeScreen.kt`, `LeaderboardScreen.kt`, `TasbeehDetailsScreen.kt`, `FirebaseManager.kt`) into modular single-responsibility units. 0 files now exceed 250 lines.
  - **Pillar 2 (50-Line Composable Rule)**: Audited every composable across all screens. Surgically extracted 18 oversized composables into sub-composables (`ManageGoalsLazyList`, `AddGoalDialogContainer`, `TasbeehNavItem`, `appNavTransitionSpec`, `TasbeehScaffoldLayout`, `MonthItemExpandedDetails`, `WeeklyBarChartCanvas`, `CircularProgressTrackAndGlow`, `LeaderboardScreenBody`, etc.). 0 composables now exceed 50 lines.
  - **Pillar 3 (Localization Parity & Zero Hardcoded Strings)**: Added missing resource keys (`leaderboard_you`, `error_something_went_wrong`, `action_add_to_goals`, `badge_daily/weekly/monthly/yearly`). Replaced all raw strings in UI cards and dialogs. Achieved 100% 1-to-1 key parity (290 keys) between `values/strings.xml` and `values-bn/strings.xml`.
  - **Pillar 4 (Design System & Contrast Invariants)**: Adjusted Light Gold (`#8A5400`) and Mint (`#0D766E`) accent tones to exceed WCAG AA contrast ratio (>4.5:1). Preserved dynamic system bar icon synchronization on every theme change. Zero raw emojis throughout the app.
  - **Pillar 5 (Compose UDF Purity & Performance)**: Verified and assigned explicit unique `key = { ... }` lambdas to all Lazy layouts. Standardized state collection with `collectAsStateWithLifecycle()`.
  - **Pillar 6 (Data Layer Safety & Cloud Sync Invariants)**: Rewrote `applyFullBackupData` in `BackupRestoreManager` to perform non-destructive merges (preserves existing higher counts, unions all goal items, merges history details by date, and never deletes un-backed up items).
  - **Pillar 7 (Dead Code & Asset Cleanup)**: Deleted dead files (`ui/AdhkarData.kt`, `ui/ProfileComponents.kt`, `ui/Screens.kt`) and unused legacy raster/vector assets (`dmosque.png`, `ic_vibration.xml`, `wood_background.xml`).
  - **Pillar 8 (Automated Domain Unit Testing)**: Created 4 domain-specific unit test suites under `app/src/test/java/com/toukir/tasbeeh/domain/` (`GoalsCalculationTest.kt`, `StreakCalculationLogicTest.kt`, `PeriodAggregationLogicTest.kt`, `BackupMergeLogicTest.kt`). All unit tests compile and pass via Gradle daemon in under 6 seconds.
* **Rationale & Impact**: Fully restores the entire codebase to compliance with all 8 AGENTS.md mandates and invariants, eliminating technical debt, safeguarding user data integrity, and establishing automated test coverage for core business logic without any regressions.

### [2026-09-09 02:05 AM] - Dashboard Reorganization, Vector Avatar & Salam Localization
* **Files Modified**: `ui/HomeComponents.kt`, `ui/ProfileHeader.kt`, `ui/ProfileScreen.kt`, `ui/ProfileStats.kt`, `ui/ProfileItems.kt`, `res/values/strings.xml`, `res/values-bn/strings.xml`
* **Changes Made**:
  - Replaced hardcoded English greeting in `HomeComponents.kt` with `stringResource(R.string.greeting)` so it properly displays in Bangla.
  - Replaced static raster photos (`male`/`female`) with a theme-adaptive vector avatar badge (`StudioIcons.AccountCircle`) in `ProfileHeader.kt`.
  - Fixed streak text visibility: Applied solid 100% opacity, `FontWeight.Bold`, and `onPrimary` color so "একটানা" is sharp and high-contrast against the Gold card.
  - Reorganized Dashboard navigation buttons into a clean, balanced 2x2 grid (Statistics & History / Leaderboard & Settings).
  - Replaced duplicate top tasbeeh stat card with Active Days ("সক্রিয় দিন").
  - Localized the "total recitations" subtitle in `ProfileItems.kt`.
* **Rationale & Impact**: Elevates visual polish and readability, achieves strict WCAG AA contrast, eliminates duplicate information, and ensures complete language localization across the app.

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
