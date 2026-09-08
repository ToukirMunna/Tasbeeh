# 💻 New Machine Environment Setup Guide

A quick reference guide for setting up a brand-new Windows machine to work seamlessly with Antigravity and Android development.

---

## ⚡ Step 1: Core Prerequisites

1. **Install Android Studio (Recommended)**:
   * Download and install Android Studio to automatically set up:
     * Android SDK: `%LOCALAPPDATA%\Android\Sdk`
     * Java / JBR: `C:\Program Files\Android\Android Studio\jbr`
     * Platform Tools & ADB: `%LOCALAPPDATA%\Android\Sdk\platform-tools`
2. **Alternative (Headless / Without Android Studio)**:
   * Install OpenJDK 21+ and Android Command-Line Tools.
   * Set environment variable `ANDROID_HOME` to your SDK folder.

---

## 🛠️ Step 2: Install Google `android` CLI (If Missing)

If Antigravity or the `android-cli` plugin is missing the `android.exe` binary on a fresh Windows machine:

Run this 1-line command in PowerShell:
```powershell
curl.exe -fsSL "https://dl.google.com/android/cli/latest/windows_x86_64/android.exe" -o "$env:USERPROFILE\.gemini\antigravity\bin\android.exe"
```

---

## 🔍 Step 3: Verify the Environment

Open PowerShell and run:
```powershell
android info
```

Expected output:
```text
sdk: C:\Users\<Username>\AppData\Local\Android\Sdk
version: 1.0.x
launcher_version: 1.0.x
```

---

## 🗝️ Step 4: Restore Universal Keystore

1. Unzip your password-protected backup of `release-key-TKR.jks`.
2. Place `release-key-TKR.jks` into your project root or master template.
3. Add your signing credentials to `local.properties` (see `docs/RELEASE_INFO.md` Section 1.1).
