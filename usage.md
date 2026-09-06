# Using FED-Radio

Day-to-day operation of the broadcast, for the person running it.

## The two faces of one file

The same `index.html` catalog powers every screen:

| Screen | How it reads the brain | What tapping does |
|---|---|---|
| Phone / tablet (app) | WebView renders it | YouTube link → OS app; bundled audio → speaker |
| Desktop / any browser (web build) | GitHub Pages serves it | Same behavior, audio from the repo |
| Car (Android Auto) | `CarAudioEngine` parses it as text | Bundled audio through the car speakers |

Change the file once, every screen updates on next build/deploy.

## Managing stations

The catalog lives in `app/src/main/assets/index.html` (and its web twin at the
repo root). One station = one line:

```html
<div class="station" data-yt="YOUTUBE_ID" data-id="track-04">Track Four: Your Title</div>
```

**Add a station:** copy a block → new `data-id` (letters/dashes) → save → rebuild.
**Retire a station:** delete the block (and its audio file if any).
**Reorder:** move blocks around — the car list follows the file order.

Rules that keep the car parser happy:

- `data-id` stays the **last attribute** before `>`.
- One station per line, title between `>` and `</div>`.
- Ids are unique — the car uses them as track keys.

## Managing audio

Per-station audio goes to `app/src/main/assets/audio/<data-id>.<ext>`:

- Supported: `.wav`, `.mp3`, `.m4a`, `.ogg` — matched in that order.
- No file for a station? The bundled **Station ID** jingle plays instead, so
  the car always has something real.
- When a track finishes, the station **auto-advances** to the next one —
  radio behavior, not playlist behavior.

Keep bundled audio lean: `.wav` is uncompressed (~10 MB/minute). For long
shows prefer `.m4a`/`.mp3`.

## In the car

- The dashboard shows stations as a media list with the FED-Radio art.
- Play/pause/next/previous work from **both** the touchscreen and the
  **steering-wheel** controls.
- If a call comes in, audio focus pauses the station; it resumes when the call
  ends (audio-focus handling is built into `CarAudioEngine`).
- Navigation prompts duck the volume automatically — standard media behavior.

## On the phone / desktop

- The app opens straight into the dark dashboard.
- YouTube-linked stations hand off to the YouTube app/browser.
- In-house stations play bundled audio right in the WebView page.
- The web build (GitHub Pages) behaves identically — share the link, anyone
  can listen without installing anything.

## Daily workflow cheat-sheet

```bash
# edit stations
$EDITOR app/src/main/assets/index.html   # and root index.html if web matters

# drop audio
cp ~/Music/episode-7.wav app/src/main/assets/audio/track-04.wav

# rebuild + reinstall
./gradlew assembleDebug && adb install -r app/build/outputs/apk/debug/app-debug.apk

# replug phone in car — new lineup is live
```

## Tips

- **Testing without the car:** enable the Desktop Head Unit (DHU) from Android
  Auto's developer settings, or just use the app on the phone — the station
  list is identical.
- **The "now playing" notification** is the media-style card; tapping it opens
  the app dashboard.
- **Version bumps:** bump `versionCode` in `app/build.gradle.kts` whenever you
  want `adb install -r` to cleanly replace the previous build.

Anything confusing? [FAQ.md](FAQ.md) covers the common surprises.
