# AGENT MANDATES & CODING INVARIANTS

### 1. Discussion Gate
- Q&A/Discussion: NEVER edit project source files or run modifying commands. Chat first. Planning artifacts (`implementation_plan.md`, `walkthrough.md`) are permitted.
- Edit project code ONLY on explicit imperative instructions (e.g. "implement", "apply") or approved `implementation_plan.md`.
- Pre-existing Work Inviolability: Inspect git status before editing. NEVER reset, overwrite, stash, or discard pre-existing user changes.

### 2. Context Updates
- `ARCHITECTURE.md`: Update ONLY on structural changes (new packages, schemas, tech stack, or DI changes).
- `LOGS.md`: Update on EVERY implemented change or fix (never on pure Q&A/discussion). Prepend below the separator using standard schema:
  `### [YYYY-MM-DD HH:MM AM/PM] - Title`
  `* **Files Modified**: <comma-separated file list>`
  `* **Changes Made**: <bulleted list of changes>`
  `* **Rationale & Impact**: <short rationale>`

### 3. Git & GitHub Protocol
- NEVER commit/push automatically. Run git only when explicitly ordered (e.g. "commit", "push").
- Author: `Toukir Munna <toukirahmhedmunna@gmail.com>`, default branch: `main`.

### 4. Architecture & Line Ceilings
- Max 50 lines per `@Composable` function body (excluding parameter signatures).
- Max 250 lines per production kt file (`app/src/main/`). Never exceed this limit.
- If a Composable exceeds 50 lines, extract logical sections into `private` sub-composables or separate component files.
- Test suites (`app/src/test/`): If approaching 250 lines, split into domain-specific test classes rather than reducing test coverage.
- UDF Layer Purity:
  - `ui/`: Rendering & state collection only. Zero business logic. Always collect via lifecycle-aware flows.
  - `ViewModel`: Exposes immutable `StateFlow<UiState>`. Zero Android View/Context references in UI composables.
  - `domain/`: Pure Kotlin. Zero Android dependencies.
- Lazy Layouts: Explicit, unique `key = { ... }` mandatory for all `items()` in `LazyColumn`/`LazyRow`.

### 5. Zero Hardcoded User-Facing Strings
- 100% of user-facing UI strings in `res/values/strings.xml` via `stringResource(R.string.x, ...)` or `context.getString(R.string.x, ...)`. Zero raw string literals in UI. Technical strings (routes, DB keys, log tags) are exempt.

### 6. Design System & Semantic Colors
- Use only semantic `MaterialTheme.colorScheme` tokens or `ui/theme/Color.kt`.
- Themes: Supported `AppTheme` (Light, Dark) paired with dynamic `AppColorTheme` accents (Gold, Mint, Sapphire, Amethyst, Rose).
- No pure #000000 / #FFFFFF:
  - Light Canvas: `#F0F2F5`, Light Surface: `#FFFFFF`, Light Inset: `#E4E8EE`.
  - Dark Canvas: `#0C0D0E`, Dark Surface: `#16181B`, Dark Inset: `#212328`.
- WCAG AA contrast minimum. 1.dp `outlineVariant` tactile borders on cards and containers (not plain text/buttons).
- System Bar Contrast Invariant: Status bar and navigation bar icon contrast (`isAppearanceLightStatusBars`/`NavigationBars`) MUST dynamically synchronize with the active theme on every theme change. Zero invisible or low-contrast status bar icons in light mode.

### 7. Zero Emojis & Bespoke Phosphor Vector Icons
- Bespoke Phosphor vector drawables via `StudioIcons.*` and `StudioIcon()` only.
- Raw emojis strictly banned in UI, code, drawables, strings, and layouts.

### 8. Single Warm Daemon Protocol
- User starts daemon via `start-daemon.bat` (or Mission Control GUI).
- Agent attaches for `testDebugUnitTest` and `assembleDebug`:
  `$env:JAVA_HOME="C:\Java\jdk-21"; $env:PATH="$env:JAVA_HOME\bin;$env:PATH"; .\gradlew.bat --daemon <task>`
- Never pass CLI JVM/heap flags (strictly in `gradle.properties`).
- Daemon check command: `@(Get-CimInstance Win32_Process -Filter "CommandLine like '%GradleDaemon%'").Count -gt 0`
- Release builds (`assembleRelease`, `bundleRelease`): user-triggered only.

### 9. Data Safety & Persistence
- Local user data (`tasbeeh_goals.json`, `tasbeeh_history.json`, and Jetpack DataStore preferences) are sacred.
- All schema/data migrations must be strictly non-destructive. Never reset or overwrite user goals, streaks, or lifetime count data.

### 10. Wireless ADB & Deployment
- ADB binary: `$adb = "C:\Android\Sdk\platform-tools\adb.exe"`
- Device Selector (ignores offline/ghost entries):
  `$dev = (& $adb devices | Where-Object { $_ -match '\tdevice$' } | ForEach-Object { ($_ -split '\t')[0] } | Select-Object -First 1)`
- Deploy & Launch Pipeline:
  1. Device Check: If `$dev` is empty: report "No active ADB device detected" immediately and STOP. No retry loops.
  2. Build APK: `.\gradlew.bat --daemon assembleDebug`
  3. Install: `& $adb -s $dev install -r app\build\outputs\apk\debug\app-debug.apk`
  4. Launch: `& $adb -s $dev shell am start -n com.toukir.tasbeeh.pro/com.toukir.tasbeeh.MainActivity`
- Zero Polling Loops: Never poll progress, loop, or sleep on background tasks/builds/subagents. Stop tools and await reactive notifications.

### 11. Pinned Toolchain & Application Identifiers
- Target Hardware: Physical devices via wireless ADB (ARM64 target).
- Package Name: `com.toukir.tasbeeh`
- ApplicationId: `com.toukir.tasbeeh.pro`
- Main Activity: `com.toukir.tasbeeh.MainActivity`
- Java Target: `JavaVersion.VERSION_17` (using OpenJDK 21 at `C:\Java\jdk-21`).
- Dependencies: Do not add or replace dependencies without explicit instructions.

### 12. Verification Discipline
- `testDebugUnitTest`: Mandatory for logic, algorithms, repository calculations.
- `assembleDebug`: For Compose UI, `res/`, manifest, and Gradle changes. Fix test failures immediately.
- Test Inviolability: Existing tests are immutable. NEVER delete, weaken, or comment out existing tests/assertions to make builds pass. Fix implementation only.
- Minimal Blast Radius: Surgical edits only. Touch ONLY lines strictly required for the prompt. Never reformat, reorder, or refactor untouched code.
