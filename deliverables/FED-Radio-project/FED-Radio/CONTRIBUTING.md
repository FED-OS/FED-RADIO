# Contributing to FED-Radio

Thanks for wanting to make the broadcast better. This project is deliberately
small and in-house — keep it that way.

## The three ground rules

1. **Zero third-party.** No new SDKs, no analytics, no remote services, no
   cloud calls. First-party AndroidX libraries are the only allowed dependencies
   (see `app/build.gradle.kts`). If a feature needs an external service, it
   belongs on the [ROADMAP.md](ROADMAP.md) "rejected" list, not in a PR.
2. **One brain.** The station catalog lives in
   `app/src/main/assets/index.html` and its web twin at the repo root
   `index.html`. **If you change stations, change both in the same PR** —
   the PR checklist enforces it.
3. **Don't break the parser contract.** The Android Auto bridge parses station
   blocks with this exact pattern:

   ```html
   <div class="station" data-yt="..." data-id="track-01">Title here</div>
   ```

   `data-id` must remain the **last attribute** before `>`. If you need to add
   attributes, add them **before** `data-id` and update the regex in
   `CarAudioEngine.kt` + its unit test in the same PR.

## Adding a station (the 60-second path)

1. Open `app/src/main/assets/index.html` and the root `index.html`.
2. Copy an existing station block; give it a fresh `data-id` (letters and dashes).
3. Optional: set `data-yt` to a real YouTube video id for the phone/web view.
4. Optional: drop offline audio at `app/src/main/assets/audio/<data-id>.wav`
   (`.mp3`, `.m4a`, `.ogg` all work). No file = the station-ID jingle plays.
5. Build, tap, listen.

## Adding code

- **Kotlin style:** standard Kotlin idioms, no new dependencies.
- **Audio handling:** `MediaPlayer` via `AssetFileDescriptor` only — that's how
  the bundled audio stays playable in the car. Never stream in the car bridge.
- **Testing:** `./gradlew testDebugUnitTest` must pass. Parser changes require
  an updated unit test.

## Workflow

1. Fork, branch (`feat/my-thing` or `fix/my-thing`).
2. Commit with clear messages.
3. PR against `main` using the template in `.github/PULL_REQUEST_TEMPLATE.md`.
4. CI (`.github/workflows/build.yml`) compiles the APK and runs tests on every PR.

## Reporting bugs

Use the bug report template (`.github/ISSUE_TEMPLATE/bug_report.md`). For
security issues, follow [SECURITY.md](SECURITY.md) — do not open public issues.

## Code of conduct

[CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md) applies to everything: issues, PRs,
discussions, and the garage.
