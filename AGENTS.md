# AGENTS.md — instructions for AI agents working on FED-Radio

Applies to any coding agent (autonomous or assisted) operating in this repo.
Human-facing context: [README.md](README.md) · [SUMMARY.md](SUMMARY.md) ·
[CLAUDE.md](CLAUDE.md) has model-specific notes.

## Prime directives

1. **Preserve the zero-third-party rule.** No new dependencies beyond
   first-party AndroidX (`core-ktx`, `appcompat`, `media`). No network code
   in the car bridge. No analytics, no remote config, no exceptions.
2. **Preserve the parser contract.** Station blocks must keep `data-id` as
   the LAST attribute before the closing `>`: `<div class="station" data-yt="ID" data-id="track-01">Title</div>`.
   Pattern enforced by regex in `CarAudioEngine.kt` and unit
   tests in `app/src/test/java/com/fedradio/CarAudioEngineUnitTest.kt`.
   Update both together or not at all.
3. **One brain, two copies.** Station edits sync
   `app/src/main/assets/index.html` and root `index.html` in the same change.
4. **Don't break the build.** Run `./gradlew assembleDebug
   testDebugUnitTest` before declaring any code task complete.

## Repo map

| Area | Location |
|---|---|
| Car bridge (Android Auto) | `app/src/main/java/com/fedradio/CarAudioEngine.kt` |
| Phone/tablet WebView view | `app/src/main/java/com/fedradio/MainActivity.kt` |
| Station catalog ("the brain") | `app/src/main/assets/index.html` |
| Web build | `index.html`, `styles.css` (repo root) |
| Bundled audio | `app/src/main/assets/audio/` (fallback: `station_id.wav`) |
| Android Auto declaration | `app/src/main/res/xml/automotive_app_desc.xml` |
| CI | `.github/workflows/build.yml`, `dependency-review.yml` |

## Operating rules

- **Read before writing.** Consult [ADR.md](ADR.md) before changing any
  decision it records; propose a new ADR if you're overturning one.
- **Docs move with code.** UI/behavior changes update `usage.md`/`FAQ.md`;
  releases update `CHANGELOG.md`.
- **Kotlin style:** idiomatic, minimal, commented only where logic is
  non-obvious. No experiments in `main` — branch.
- **Audio:** bundled assets only; keep `.wav` for short idents, `.m4a`/`.mp3`
  for episodes. Never modify `noCompress` behavior without explaining why
  (it is required for `openFd()` seeking).
- **Icons/art:** replace, don't edit binaries; regenerate from source if a
  pipeline exists (see `tools/` history).
- **Commits:** imperative subject lines (`Add skip-to-next steering support`).
- **When blocked or uncertain:** stop and open an issue/question rather than
  guessing around the prime directives.

## Definition of done (per task)

- [ ] Compiles: `./gradlew assembleDebug`
- [ ] Tests pass: `./gradlew testDebugUnitTest`
- [ ] Both HTML files updated (if stations changed)
- [ ] Parser test updated (if contract changed)
- [ ] Docs updated (if user-visible behavior changed)
