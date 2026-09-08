# ==============================================================================
#  Android App Initializer & Rebrander Engine
# ==============================================================================
[CmdletBinding()]
param()

$ErrorActionPreference = "Stop"
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

$rootDir = (Resolve-Path "$PSScriptRoot\..").Path

Write-Host ""
Write-Host "========================================================" -ForegroundColor Cyan
Write-Host "   Android App Initializer & Rebrander" -ForegroundColor Green
Write-Host "========================================================" -ForegroundColor Cyan
Write-Host ""

# 1. Prompt for App Display Name
$appName = Read-Host "Enter App Display Name (e.g. Habit Tracker)"
$appName = $appName.Trim()
if ([string]::IsNullOrWhiteSpace($appName)) {
    Write-Host ""
    Write-Host "[ERROR] App display name cannot be empty." -ForegroundColor Red
    exit 1
}

# 2. Prompt for Package Name / Suffix
Write-Host ""
$pkgInput = Read-Host "Enter Package Suffix (e.g. habittracker or com.toukirstudio.habittracker)"
$pkgInput = $pkgInput.Trim().ToLower()
if ([string]::IsNullOrWhiteSpace($pkgInput)) {
    Write-Host ""
    Write-Host "[ERROR] Package suffix cannot be empty." -ForegroundColor Red
    exit 1
}

# Normalize: accept either 'narra', 'com.toukirstudio.narra', or 'com.toukir.narra'
if ($pkgInput -match '\.([a-z0-9_]+)$') {
    $pkgSuffix = $matches[1]
} else {
    $pkgSuffix = $pkgInput
}

# Validate format: letters, numbers, underscores only; must start with a letter
if ($pkgSuffix -notmatch '^[a-z][a-z0-9_]*$') {
    Write-Host ""
    Write-Host "[ERROR] Invalid package suffix '$pkgSuffix'. Must start with a letter and contain only lowercase letters, digits, or underscores." -ForegroundColor Red
    exit 1
}

$fullPackage = "com.toukirstudio.$pkgSuffix"

# 3. Detect current package from app/build.gradle.kts
$buildGradlePath = Join-Path $rootDir "app\build.gradle.kts"
if (-not (Test-Path $buildGradlePath)) {
    Write-Host ""
    Write-Host "[ERROR] app/build.gradle.kts not found at: $buildGradlePath" -ForegroundColor Red
    exit 1
}

$buildGradleText = [System.IO.File]::ReadAllText($buildGradlePath)
if ($buildGradleText -match 'namespace\s*=\s*"([^"]+)"') {
    $currentPackage = $matches[1]
} else {
    $currentPackage = "com.toukirstudio.template"
}
$currentSuffix = $currentPackage.Substring($currentPackage.LastIndexOf('.') + 1)

# 4. Show Confirmation Summary
Write-Host ""
Write-Host "--------------------------------------------------------" -ForegroundColor Yellow
Write-Host "  CONFIGURATION SUMMARY" -ForegroundColor Yellow
Write-Host "--------------------------------------------------------" -ForegroundColor Yellow
Write-Host "  App Display Name : $appName" -ForegroundColor White
Write-Host "  Current Package  : $currentPackage" -ForegroundColor Gray
Write-Host "  Target Package   : $fullPackage" -ForegroundColor Cyan
Write-Host "  Root Project Name: $appName" -ForegroundColor White
Write-Host "  Source Directory : com/toukirstudio/$pkgSuffix" -ForegroundColor White
Write-Host "--------------------------------------------------------" -ForegroundColor Yellow
Write-Host ""

$confirm = Read-Host "Proceed with initialization and rebranding? (Y/N)"
if ($confirm.Trim().ToUpper() -ne "Y") {
    Write-Host ""
    Write-Host "[ABORTED] Initialization cancelled. No files were modified." -ForegroundColor Yellow
    exit 0
}

Write-Host ""
Write-Host "[1/6] Updating settings.gradle.kts..." -ForegroundColor Cyan
$settingsPath = Join-Path $rootDir "settings.gradle.kts"
if (Test-Path $settingsPath) {
    $settingsContent = [System.IO.File]::ReadAllText($settingsPath)
    $settingsContent = [System.Text.RegularExpressions.Regex]::Replace($settingsContent, 'rootProject\.name\s*=\s*"[^"]+"', "rootProject.name = `"$appName`"")
    [System.IO.File]::WriteAllText($settingsPath, $settingsContent, [System.Text.Encoding]::UTF8)
}

Write-Host "[2/6] Updating app/build.gradle.kts..." -ForegroundColor Cyan
$buildGradleText = [System.Text.RegularExpressions.Regex]::Replace($buildGradleText, 'namespace\s*=\s*"[^"]+"', "namespace = `"$fullPackage`"")
$buildGradleText = [System.Text.RegularExpressions.Regex]::Replace($buildGradleText, 'applicationId\s*=\s*"[^"]+"', "applicationId = `"$fullPackage`"")
[System.IO.File]::WriteAllText($buildGradlePath, $buildGradleText, [System.Text.Encoding]::UTF8)

Write-Host "[3/6] Updating res/values/strings.xml..." -ForegroundColor Cyan
$stringsPath = Join-Path $rootDir "app\src\main\res\values\strings.xml"
if (Test-Path $stringsPath) {
    $stringsContent = [System.IO.File]::ReadAllText($stringsPath)
    $stringsContent = [System.Text.RegularExpressions.Regex]::Replace($stringsContent, '<string name="app_name">.*?</string>', "<string name=`"app_name`">$appName</string>")
    [System.IO.File]::WriteAllText($stringsPath, $stringsContent, [System.Text.Encoding]::UTF8)
}

Write-Host "[4/6] Updating ARCHITECTURE.md metadata..." -ForegroundColor Cyan
$archPath = Join-Path $rootDir "ARCHITECTURE.md"
if (Test-Path $archPath) {
    $archContent = [System.IO.File]::ReadAllText($archPath)
    $archContent = [System.Text.RegularExpressions.Regex]::Replace($archContent, '\*\*Project Name\*\*:\s*.*', "**Project Name**: $appName")
    $archContent = [System.Text.RegularExpressions.Regex]::Replace($archContent, '\*\*Package Name\*\*:\s*`[^`]+`', "**Package Name**: ``$fullPackage``")
    $archContent = [System.Text.RegularExpressions.Regex]::Replace($archContent, '\*\*Application ID\*\*:\s*`[^`]+`', "**Application ID**: ``$fullPackage``")
    [System.IO.File]::WriteAllText($archPath, $archContent, [System.Text.Encoding]::UTF8)
}

Write-Host "[5/6] Renaming Kotlin source directories..." -ForegroundColor Cyan
$sourceSets = @(
    "app\src\main\java\com\toukirstudio",
    "app\src\test\java\com\toukirstudio",
    "app\src\androidTest\java\com\toukirstudio"
)

foreach ($set in $sourceSets) {
    $basePath = Join-Path $rootDir $set
    $sourceDir = Join-Path $basePath $currentSuffix
    $targetDir = Join-Path $basePath $pkgSuffix

    if ((Test-Path $sourceDir) -and ($sourceDir -ne $targetDir)) {
        if (Test-Path $targetDir) {
            Remove-Item -Path $targetDir -Recurse -Force
        }
        Move-Item -Path $sourceDir -Destination $targetDir -Force
        Write-Host "      Moved: $set\$currentSuffix -> $set\$pkgSuffix" -ForegroundColor Gray
    }
}

Write-Host "[6/6] Replacing package declarations and imports in Kotlin files..." -ForegroundColor Cyan
$ktFiles = Get-ChildItem -Path (Join-Path $rootDir "app\src") -Filter "*.kt" -Recurse
$filesUpdated = 0

foreach ($file in $ktFiles) {
    $content = [System.IO.File]::ReadAllText($file.FullName)
    $updatedContent = $content.Replace("package $currentPackage", "package $fullPackage")
    $updatedContent = $updatedContent.Replace("import $currentPackage.", "import $fullPackage.")
    $updatedContent = $updatedContent.Replace("$currentPackage.R", "$fullPackage.R")

    if ($content -ne $updatedContent) {
        [System.IO.File]::WriteAllText($file.FullName, $updatedContent, [System.Text.Encoding]::UTF8)
        $filesUpdated++
    }
}
Write-Host "      Updated $filesUpdated Kotlin source files." -ForegroundColor Gray

# Clean build caches if present
$buildDirs = @(
    "$rootDir\app\build",
    "$rootDir\build"
)
foreach ($bDir in $buildDirs) {
    if (Test-Path $bDir) {
        Remove-Item -Path $bDir -Recurse -Force -ErrorAction SilentlyContinue
    }
}

Write-Host ""
Write-Host "========================================================" -ForegroundColor Green
Write-Host "   SUCCESS: App initialized and rebranded to '$appName'!" -ForegroundColor Green
Write-Host "   Package: $fullPackage" -ForegroundColor Green
Write-Host "========================================================" -ForegroundColor Green
Write-Host ""
Write-Host "Recommended Next Steps:" -ForegroundColor Cyan
Write-Host "1. Run 'scripts\start-daemon.bat' to spin up the Gradle daemon." -ForegroundColor White
Write-Host "2. Instruct your Antigravity agent to implement your app features!" -ForegroundColor White
Write-Host ""
