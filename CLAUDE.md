# CLAUDE.md — working instructions for Claude in this repo

Guidance for Claude (claude.ai / Claude Code) when assisting with FED-Radio.

## Project in one paragraph

FED-Radio is a private, in-house radio app. A single local HTML file
(`app/src/main/assets/index.html`, "the brain") holds the station catalog.
Phone/tablet render it in a WebView (`MainActivity.kt`); any browser serves
the web twin at the repo root (`index.html` + `styles.css`, GitHub
Pages-ready); and the Android Auto bridge (`CarAudioEngine.kt`, a
`MediaBrowserServiceCompat`) parses the same file as raw text and publishes
stations to the car dashboard, playing bundled audio from
`app/src/main/assets/audio/`.

## Non-negotiables (verify before suggesting changes)

1. **Zero third-party.** Never suggest adding SDKs, analytics, streaming
   services, or any dependency outside first-party AndroidX
   (`core-ktx`, `appcompat`, `media`). If asked for a feature that requires
   one, explain it's rejected per [ROADMAP.md](ROADMAP.md) and offer an
   in-house alternative.
2. **One brain, two files.** Any station edit must touch
   `app/src/main/assets/index.html` AND root `index.html` together.
3. **Parser contract.** Station blocks must match
   `data-id="...">TITLE` with `data-id` as the **last attribute**. Changes to
   the pattern require updating the regex in `CarAudioEngine.kt` and the unit
   test `CarAudioEngineUnitTest.kt` in the same change.

## Where things are

- Car bridge: `app/src/main/java/com/fedradio/CarAudioEngine.kt`
- Phone view: `app/src/main/java/com/fedradio/MainActivity.kt`
- Brain: `app/src/main/assets/index.html`; audio: `app/src/main/assets/audio/`
- Build config: `app/build.gradle.kts` — note `androidResources.noCompress`
  includes audio extensions; it is load-bearing for `MediaPlayer.openFd()`.
- Docs: see [SUMMARY.md](SUMMARY.md) for the full index.

## Task conventions

- Kotlin, standard idioms, no new dependencies.
- Keep car playback **offline-only**: `MediaPlayer` + `AssetFileDescriptor`
  from bundled assets; fallback jingle is `audio/station_id.wav`.
- After code changes: `./gradlew assembleDebug testDebugUnitTest`.
- Keep docs in sync — a change isn't done until usage/FAQ/CHANGELOG reflect it.
- Tone of the project docs: practical, garage-built, a little radio-flavored,
  never corporate.

## Verification habits

- Station HTML: check the pattern matches with a quick
  `grep -o 'data-id="[^"]*">[^<]*'` on the brain file.
- Audio: `file` the WAV/MP3 to sanity-check format; remember `.wav` is
  ~10 MB/min, so recommend `.m4a` for long content.
- Builds: trust CI (`.github/workflows/build.yml`) as the source of truth
  for "does it compile".
