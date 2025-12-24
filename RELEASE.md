# Release Guide

This document explains how to set up and trigger production APK releases for the Facial Photo Management app.

---

## Changelog

### v1.1.0 (December 2024)

**Bug Fixes:**
- Fixed photo viewer swiping between photos
- Fixed deselect functionality when multiple photos are selected (previously only worked with single selection)

**UI Improvements:**
- Replaced generic icons with the app icon in homepage and settings headers

---

## Prerequisites

- Git installed and configured
- GitHub repository with push access
- Java JDK 17 (for local builds)

## Setting Up Signing (One-Time)

### 1. Generate a Signing Keystore

Run the following command in the project root directory:

```bash
keytool -genkeypair -v -keystore release.keystore -alias release -keyalg RSA -keysize 2048 -validity 10000
```

You'll be prompted to enter:
- **Keystore password**: Choose a strong password (remember this!)
- **Key password**: Can be the same as keystore password
- **Your name and organization details**: Fill in as appropriate

> ⚠️ **Important**: Keep your keystore file and passwords safe! If you lose them, you cannot update your app.

### 2. Convert Keystore to Base64

**On Windows (PowerShell):**
```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("release.keystore")) | Set-Clipboard
```

**On Linux/Mac:**
```bash
base64 -i release.keystore | pbcopy  # Mac
base64 release.keystore | xclip -selection clipboard  # Linux
```

### 3. Configure GitHub Secrets

Go to your GitHub repository → **Settings** → **Secrets and variables** → **Actions** → **New repository secret**

Add these four secrets:

| Secret Name | Value |
|-------------|-------|
| `KEYSTORE_BASE64` | The base64 string from step 2 |
| `KEYSTORE_PASSWORD` | Your keystore password |
| `KEY_ALIAS` | `release` (or your chosen alias) |
| `KEY_PASSWORD` | Your key password |

## Triggering Releases

### Automatic Release (Push to Main)

Every push to the `main` branch automatically:
1. Builds a signed release APK
2. Creates a GitHub Release with the APK attached
3. Auto-increments version based on commit count

### Manual Release

1. Go to **Actions** tab in GitHub
2. Select **Build and Release APK** workflow
3. Click **Run workflow**
4. Optionally specify a custom version name (e.g., `2.0.0`)
5. Click **Run workflow**

## Building Locally

### Debug Build (No Signing Required)
```bash
./gradlew assembleDebug
```
APK location: `app/build/outputs/apk/debug/app-debug.apk`

### Release Build (Signing Required)

Set environment variables and build:

**Windows (PowerShell):**
```powershell
$env:KEYSTORE_PATH = "C:\path\to\release.keystore"
$env:KEYSTORE_PASSWORD = "your_password"
$env:KEY_ALIAS = "release"
$env:KEY_PASSWORD = "your_password"
./gradlew assembleRelease
```

**Linux/Mac:**
```bash
export KEYSTORE_PATH=/path/to/release.keystore
export KEYSTORE_PASSWORD=your_password
export KEY_ALIAS=release
export KEY_PASSWORD=your_password
./gradlew assembleRelease
```

APK location: `app/build/outputs/apk/release/app-release.apk`

## Version Numbering

- **Version Code**: Auto-incremented based on total commit count
- **Version Name**: Format `1.0.{commit_count}` by default, can be overridden in manual releases

## Troubleshooting

### Build fails with signing error
- Ensure all 4 secrets are configured correctly in GitHub
- Verify the keystore was converted to base64 correctly

### APK won't install
- Make sure "Install from unknown sources" is enabled on the device
- Check that minSdk (26) matches your device's Android version

### ProGuard errors
- Check `proguard-rules.pro` for missing keep rules
- Add specific keep rules for any classes being incorrectly obfuscated
