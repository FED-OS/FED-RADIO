# Roadmap — FED-Radio

Where the broadcast goes next. Rough quarters, honest priorities.

## Now (v1.x line)

- [ ] **Real station lineup** — replace the three placeholder YouTube ids in
  `index.html` with the actual show catalog (phone view), and export first
  batch of episode audio (`.m4a`) into `assets/audio/`.
- [ ] **Release signing** — create the keystore + GitHub Actions secrets and
  enable `assembleRelease` in CI (see [DEPLOYMENT.md](DEPLOYMENT.md)).
- [ ] **Parser extraction** — move the HTML→stations regex out of
  `CarAudioEngine.kt` into a pure `StationParser` object with a real unit
  test suite (fixtures: every current station block + malformed inputs).

## Next (v1.1–1.2)

- [ ] **Now-playing on the phone** — mirror car playback state into the
  WebView dashboard (JS bridge) so the phone shows what the car is hearing.
- [ ] **Station ordering & pinned favorites** — a `data-pin` attribute and
  stable custom ordering shared by both `index.html` files.
- [ ] **Desktop Head Unit test script** — documented DHU smoke test in CI
  docs so contributors can fake a car without a car.
- [ ] **Localization pass** — pull user-facing strings into `strings.xml`
  (they mostly are already) and add a second locale.

## Later (v2 ideas)

- [ ] **Playlist mode per station** — a station expands into a nested browse
  tree (`onLoadChildren` already supports hierarchy; needs a
  `data-playlist="id1,id2"` attribute and an audio convention).
- [ ] **Live input** — microphone-to-PCM capture for a genuinely live
  in-house broadcast (stays local: USB/BT mic → encoder → MediaPlayer-side
  pipe). Design sketch required first (ADR candidate).
- [ ] **Wear OS companion** — station skip from the wrist; same
  `MediaBrowserServiceCompat`, different client.

## Rejected (and why)

| Idea | Why not |
|---|---|
| Play Store distribution | The whole point is store-free, in-house operation |
| YouTube streaming inside the car bridge | Would violate the offline-first, zero third-party rule — the car plays bundled audio only |
| Analytics / crash reporting SDK | External service; banned by ground rule 1 |
| Cloud station sync | Same — the brain is a local file, and it stays that way |
| Custom car UI / video on the dashboard | Android Auto forbids it for safety; we ride the templates instead |

## Have an idea?

Open a Discussion under **Ideas** — good ones end up on this list with your
name on them (see [GOVERNANCE.md](GOVERNANCE.md) for how direction decisions
get made).
