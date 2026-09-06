# Building FED-Radio

Three ways to get an APK, from easiest to most control.

## Option A — Let GitHub build it (zero setup)

1. Push (or fork) this repo to GitHub.
2. Open the **Actions** tab → **Build APK** workflow → the latest run.
3. Download the **FED-Radio-debug-APK** artifact and unzip it.
4. You now have `app-debug.apk` — skip to [INSTALL.md](INSTALL.md).

That's it. CI runs `.github/workflows/build.yml`: JDK 17, Gradle, `assembleDebug`, unit tests, artifact upload.

## Option B — Android Studio (recommended for development)

1. Install [Android Studio](https://developer.android.com/studio) (any recent version).
2. **File → Open** → select the `FED-Radio` folder (the one with `settings.gradle.kts`).
3. Let Gradle sync (first sync downloads the AndroidX dependencies — that's expected; they're first-party).
4. Press **▶ Run** with a phone attached, or **Build → Build Bundle(s)/APK(s) → Build APK(s)**.
5. Output: `app/build/outputs/apk/debug/app-debug.apk`.

## Option C — Command line (no IDE)

Requirements: JDK 17, Android SDK (platform 34 + build-tools 34.0.0), and the
Gradle wrapper included in this repo.

```bash
# Linux/macOS
./gradlew assembleDebug

# Windows
gradlew.bat assembleDebug
```

The APK lands at `app/build/outputs/apk/debug/app-debug.apk`.

Run the unit tests:

```bash
./gradlew testDebugUnitTest
```

## Verifying your build (30 seconds)

1. Install on your phone: `adb install -r app-debug.apk`
2. Open **FED-Radio** — you should see the dark dashboard with four station cards.
3. Tap **CH·00 Station ID** — the jingle plays (phone speaker).
4. Plug into the car — the station list appears under the media apps; tapping a
   station plays through the car speakers.

If step 3 is silent, the audio asset didn't package — check that
`app/src/main/assets/audio/station_id.wav` exists and that
`noCompress` in `app/build.gradle.kts` still lists `wav`.

## Build configuration notes

- `minSdk 29` (Android 10) per the FED-Radio spec; `targetSdk 34`.
- Audio extensions are added to `androidResources.noCompress` — **required**
  for `MediaPlayer.openFd()` to seek bundled audio. Don't remove it.
- Minification is off by default. If you enable `isMinifyEnabled`, the keeps in
  `app/proguard-rules.pro` already protect the car bridge.
- Debug builds are signed with the auto-generated debug key — fine for private
  sideloading, not for distribution. For a signed release build see
  [DEPLOYMENT.md](DEPLOYMENT.md).

## Troubleshooting

| Symptom | Fix |
|---|---|
| Gradle sync fails on dependencies | Check internet; ensure `google()`/`mavenCentral()` remain in `settings.gradle.kts` |
| `JAVA_HOME` mismatch | Set JDK 17 (Android Studio: Settings → Build → Gradle JDK) |
| APK installs but car doesn't list it | Unknown sources toggle — see [INSTALL.md](INSTALL.md) step 2 |
| Audio silent in car | Confirm `noCompress` includes your extension; confirm file is `assets/audio/<data-id>.<ext>` |
