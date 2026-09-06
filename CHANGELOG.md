# Changelog — FED-Radio

All notable changes to this project are documented here.
The format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/).

## [2.1.0] — 2026-09-06 — The Overnight Evolution

Radio comes alive at night: the dial keeps rolling even when nobody is watching.

### Added
- **Auto-advance (real-radio behavior).** When a broadcast ends, the station
  plate flashes `SWITCHING`, a sign-off line appears, and after a beat the
  dial rolls to the next frequency on its own — mirroring the auto-advance
  the car bridge already performs on Android Auto. Web and car now behave
  the same at 2 a.m.
- **Desktop keyboard tuning.** `←` / `→` step through frequencies, `Space`
  (or `Enter`) toggles the current station on/off. A keyboard hint on the
  dial caption is shown only on devices with a real pointer.
- **Installable web build (PWA).** Own `manifest.webmanifest`, locally drawn
  `icon-192.png` / `icon-512.png` in the print palette, and a theme-color
  meta — all first-party files, still zero third-party. Installable from
  the browser menu on desktop and Android.
- **`tools/make_web_twin.py`** — deterministic web-twin generator committed
  to the repo: change the brain, run the script, the twin can never drift.
  It also defensively strips any stray third-party script tags.

### Changed
- Web twin regenerated from the brain — now carries auto-advance, keyboard
  tuning, and the PWA links.
- `versionCode 4 / versionName 2.1.0`.

## [2.0.0] — 2026-09-06 — The Print Edition

A complete recomposition. Nothing reused: new theme, new layout, new identity.

### Changed
- **Complete redesign — "The Broadcast Journal" broadsheet.** The dashboard is
  now a light print-edition paper layout: cream paper, ink typography, editorial
  red, and a newspaper masthead ("Est. 2026 · Vol. II · Issue No. 1").
- **New composition, top to bottom:** scrolling ticker tape; a 4:3 broadcast
  monitor with an SMPTE color-bars test pattern and VU meter standby cue;
  a live broadcast clock (real time/date); and a horizontal FM tuning dial with
  tick marks and an animated red needle — tuning is now the primary navigation.
- **Native theme to match:** status bar, navigation bar, and window background
  now use the paper palette (`paper`, `ink`, `accent_red`, `dial_amber`);
  light status bar icons; WebView background matches the paper tone.
- **Web twin generated deterministically** from the app brain (script-derived
  so the two files can never drift), keeping the Pages meta tags and
  repo-relative audio path.
- `versionCode 3 / versionName 2.0.0`.

### Fixed
- `activity_main.xml` referenced a removed legacy color after the palette
  swap — caught by the compile and corrected (`@color/ink`).

## [1.1.0] — 2026-09-06 — The Studio Update

The phone dashboard becomes a tech radio studio. 🎙️

### Added
- **Studio stage** — embedded YouTube player (privacy-enhanced
  `youtube-nocookie.com`) as the top screen: tap a channel, the episode plays
  right inside the app. No more bouncing out to the YouTube app.
- **Now-playing strip** — channel number, show title, and ON AIR / STANDBY
  state under the stage, styled like broadcast hardware.
- **Animated VU-meter standby screen** — animated level bars while a pure
  in-house audio channel plays (no video source), like a radio station idling.
- **Runtime card decoration** — station blocks are now one line of plain text;
  JavaScript decorates them into styled cards. The raw HTML the car parses
  stays minimal and untouchable.

### Fixed
- **Car station list was empty in practice** — titles wrapped in `<span>` tags
  meant the car regex captured whitespace, not titles. All four channels now
  carry plain-text titles directly after the `data-id` attribute.
- **Phantom stations from comments** — example station tags written inside
  HTML comments were indistinguishable from real stations to the raw-text
  parser, adding fake channels to the car list. Comments no longer embed
  example tags, and a new unit test (`car station count matches station
  block count`) fails the build if they ever return.

### Changed
- Unit tests now parse the real `index.html` (not sample strings), so any
  brain edit that breaks the Android Auto list fails CI before it ships.
- `versionCode 2 / versionName 1.1.0`; footer marks the Studio Build.

## [1.0.0] — 2026-09-06

The first working broadcast. 📻

### Added
- **Car bridge** — `CarAudioEngine` (`MediaBrowserServiceCompat`) publishes the
  station list to Android Auto and plays audio through the car speakers:
  play/pause, next/previous (incl. steering wheel), seek, audio-focus handling,
  and a media-style foreground notification.
- **The brain** — `app/src/main/assets/index.html`: single-file station catalog
  with the parser contract (`data-id` last attribute), parsed natively via regex.
- **Bundled station-ID jingle** — `assets/audio/station_id.wav`, synthesized
  from scratch (pure Python, no third-party audio tooling), so the car has
  real audio out of the box.


- **Per-station audio** — drop `<data-id>.wav|.mp3|.m4a|.ogg` into
  `assets/audio/`; missing files fall back to the station ID. Radio-style
  auto-advance rolls to the next station when a track ends.
- **Phone/tablet/desktop app** — `MainActivity` renders the brain in a
  full-bleed dark WebView; YouTube links hand off to the OS.
- **Web build** — root `index.html` + shared `styles.css`; GitHub Pages-ready,
  plays bundled audio from the repo path.
- **Brand kit** — app icon (adaptive + all densities), white notification
  glyph, car album art, social preview image. Generated + committed.
- **CI** — `.github/workflows/build.yml` compiles the debug APK and uploads it
  as an artifact on every push/PR; `dependency-review.yml` scans dependency
  changes.
- **Governance & docs set** — README, INSTALL, BUILD, DEPLOYMENT, usage, FAQ,
  SUPPORT, SECURITY, CONTRIBUTING, CODE_OF_CONDUCT, CHANGELOG, AUTHORS,
  MAINTAINERS, GOVERNANCE, ROADMAP, ADR, SUMMARY, NOTICE, COPYING, CITATIONS,
  PRICING, plus issue/PR/discussion templates and root fallbacks.
- **Placeholder tests** — parser-contract unit test + package sanity
  instrumented test (source sets wired for real tests later).

### Decisions
See [ADR.md](ADR.md): HTML-as-database, raw-text regex parsing, jingle-first
fallback, AndroidX-only dependencies, sideloaded private distribution.

[1.0.0]: https://github.com/YOUR_USER/FED-Radio/releases/tag/v1.0.0
