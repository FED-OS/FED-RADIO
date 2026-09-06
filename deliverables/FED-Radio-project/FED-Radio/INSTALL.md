# Installing FED-Radio

From APK to car speakers in about five minutes.

## What you need

- An Android phone, Android 10 (API 29) or newer
- The FED-Radio APK — from CI (Actions artifact), Android Studio, or `./gradlew assembleDebug` (see [BUILD.md](BUILD.md))
- A car that supports **Android Auto** (wired or wireless)
- A USB cable (for wired projection; wireless works the same once paired)

## Step 1 — Install the APK on your phone

Any of these:

```bash
adb install -r app-debug.apk        # with USB debugging on
```

- Or copy the APK to your phone (Drive/USB/whatever) and open it — allow
  "install unknown apps" for your file manager when prompted.
- Or run it straight from Android Studio with the phone attached.

## Step 2 — Unlock sideloaded apps in Android Auto (one-time)

This is the **only** manual step, and it happens on the phone, not the car:

1. Open the **Android Auto** app on your phone.
2. Scroll to the bottom and tap **Version** about ten times, until it says
   *"You are now a developer!"*
3. Tap the **⋮** menu (top right) → **Developer settings**.
4. Check **Unknown sources**.

That's the whole ceremony. Google hides sideloaded apps from the car screen by
default; this toggle tells the phone's Android Auto to trust your build. You
never touch the car's own settings — the head unit just displays whatever the
phone projects.

## Step 3 — Plug in and play

1. Connect the phone to the car (USB or wireless Android Auto).
2. On the dashboard, open the **media/apps** list — FED-Radio appears alongside
   Spotify and YouTube Music.
3. Tap a station. Audio routes through the car speakers; the steering-wheel
   next/previous buttons work; play/pause lives on the dash.

First station tap plays the bundled **FED-Radio Station ID** jingle until you
add per-station audio (see [usage.md](usage.md)).

## Step 4 — Make it yours

1. Replace `VIDEO_ID_1..3` in `app/src/main/assets/index.html` with your real
   YouTube video ids (phone view opens them; keep or drop `data-yt` as you like).
2. For offline car audio, drop `.wav`/`.mp3`/`.m4a`/`.ogg` files named after
   each `data-id` into `app/src/main/assets/audio/`.
3. Rebuild, reinstall (`adb install -r`), replug. New stations appear instantly.

## Uninstall

Settings → Apps → FED-Radio → Uninstall. The car needs no cleanup — when the
phone stops broadcasting the app, the dashboard forgets it immediately.

## No Android Auto?

- **Android Automotive OS car (AAOS):** the same APK installs directly on the
  car's system (it's Android). Sideload via the car's own unknown-sources flow.
- **Older/other head units:** no projection system, no app. The phone/tablet/
  desktop dashboard still works everywhere else.
