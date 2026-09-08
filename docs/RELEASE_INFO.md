# 🔑 Universal Release Signing Reference

---

## 🤖 Agent Instructions: How to Use This Keystore

1. **Keystore Location**: The universal release keystore is stored at `release-key-TKR.jks` in the project root.
2. **Release Signing Configuration**: Configure `app/build.gradle.kts` release signing with:
   * **File**: `file("../release-key-TKR.jks")` or `file("release-key-TKR.jks")`
   * **Alias**: `toukir_ahmed`
   * **Password**: Loaded from `local.properties` (see below)
3. **Cloud / OAuth Integration**: When configuring Google Cloud Console, Firebase, or Google Sign-In, **always use the exact Machine Debug and Universal Release SHA fingerprints listed below**.

---

## 1. Keystore Details
* **Keystore File**: `release-key-TKR.jks` *(gitignored)*
* **Key Alias**: Configured in `local.properties` as `RELEASE_KEY_ALIAS`
* **Keystore Password**: Configured in `local.properties` as `RELEASE_STORE_PASSWORD`
* **Key Password**: Configured in `local.properties` as `RELEASE_KEY_PASSWORD`

## 1.1 Setting Up Signing Credentials
Add the following entries to your project's `local.properties` file (gitignored, never committed):
```properties
RELEASE_STORE_PASSWORD=your_keystore_password
RELEASE_KEY_ALIAS=your_key_alias
RELEASE_KEY_PASSWORD=your_key_password
```

---

## 2. Certificate SHA Fingerprints

| Variant | Keystore Source | SHA-1 Certificate Fingerprint | SHA-256 Certificate Fingerprint |
| :--- | :--- | :--- | :--- |
| **Debug** | Machine `debug.keystore` | `59:67:11:DD:70:23:DC:1E:51:7B:75:B9:99:CF:9F:31:AC:C5:D7:81` | `23:DE:3A:59:47:56:60:F8:E6:D4:9E:07:1D:38:6D:6B:30:6E:8F:6C:32:A3:87:1D:6C:A9:27:77:DB:8C:E8:5B` |
| **Release** | `release-key-TKR.jks` | `6A:A9:0D:AC:A5:70:C7:71:D9:25:35:30:99:41:1A:36:2D:08:02:37` | `62:97:62:E4:BC:FB:DA:F5:49:9F:F1:81:20:1A:FB:D2:D2:8B:5C:58:BC:80:07:26:D1:50:6E:18:5E:EA:17:CF` |

---

## 3. Google Cloud / OAuth 2.0 Client IDs (Optional)
* **Google Cloud Project**: `[PROJECT_NAME]`
* **Debug Client ID**: Package `[PACKAGE_NAME].debug` + Machine Debug SHA-1
* **Release Client ID**: Package `[PACKAGE_NAME]` + Universal Release SHA-1
* **OAuth Scopes**:
  * `https://www.googleapis.com/auth/drive.appdata` *(Private sandboxed app storage)*
  * `https://www.googleapis.com/auth/userinfo.profile` *(User display name & avatar)*
  * `https://www.googleapis.com/auth/userinfo.email` *(User email)*



