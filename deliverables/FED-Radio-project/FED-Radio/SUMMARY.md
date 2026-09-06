# FED-Radio — Project Summary

A one-screen map of the whole project: what it is, how it's built, where
everything lives.

## The elevator pitch

FED-Radio is a private, SiriusXM-style radio system built **entirely
in-house** from one local HTML file. Phone, tablet, and desktop render that
file as a dark media dashboard (in a WebView or any browser via GitHub
Pages). When the phone plugs into an Android Auto car, a native
`MediaBrowserServiceCompat` bridge parses the **same file as raw text** and
publishes the stations to the car's dashboard — Android Auto draws the whole
media UI, routes audio through the car speakers, and wires up the
steering-wheel controls. Zero third-party services, zero app stores, one
file to edit.

## The architecture, one diagram

```
                    assets/index.html   (THE BRAIN — hardcoded station list)
                            │
        ┌───────────────────┼─────────────────────┐
        ▼                   ▼                     ▼
  MainActivity.kt     root index.html      CarAudioEngine.kt
  (WebView renders    + styles.css         (parses brain as raw text,
   the brain)          (GitHub Pages /      publishes stations to
                        any browser)        Android Auto; plays bundled
                                            audio via MediaPlayer)
        │                   │                     │
   phone / tablet      desktop / any          car dashboard:
   YouTube handoff     browser                play/pause, skip, seek,
   + bundled audio     + bundled audio        steering wheel, speakers
```

## File map (what lives where)

| Path | Role |
|---|---|
| `app/src/main/assets/index.html` | **The brain** — station catalog; parser contract inside |
| `app/src/main/assets/audio/` | Bundled audio; `station_id.wav` jingle included |
| `app/src/main/java/com/fedradio/MainActivity.kt` | Phone/tablet WebView view |
| `app/src/main/java/com/fedradio/CarAudioEngine.kt` | Android Auto bridge (browse + playback + audio focus + notification) |
| `app/src/main/res/xml/automotive_app_desc.xml` | Declares the app to the car subsystem |
| `app/src/main/res/` | Icons (all densities), adaptive icons, layouts, theme, strings |
| `index.html` + `styles.css` (root) | Web build — GitHub Pages ready |
| `.github/workflows/` | CI: APK build + dependency review |
| `.github/ISSUE_TEMPLATE/`, `PULL_REQUEST_TEMPLATE.md`, `DISCUSSION_WELCOME_README.md` | Community automation |
| `docs/` | Icon art + social preview |
| `prompts/`, `wiki/`, `discussion/` | Working folders (see their READMEs) |
| Everything else at root | Guides + governance (table below) |

## Doc index

| If you want to… | Read |
|---|---|
| Install & get it in the car | [INSTALL.md](INSTALL.md) |
| Build the APK (3 ways) | [BUILD.md](BUILD.md) |
| Distribute / publish / sign | [DEPLOYMENT.md](DEPLOYMENT.md) |
| Run the broadcast day-to-day | [usage.md](usage.md) |
| Fix a common problem | [FAQ.md](FAQ.md) |
| Get help | [SUPPORT.md](SUPPORT.md) |
| Report a vulnerability | [SECURITY.md](SECURITY.md) |
| Contribute | [CONTRIBUTING.md](CONTRIBUTING.md) |
| See what's planned | [ROADMAP.md](ROADMAP.md) |
| Understand the design calls | [ADR.md](ADR.md) |
| See how it's run | [GOVERNANCE.md](GOVERNANCE.md) |
| Track releases | [CHANGELOG.md](CHANGELOG.md) |

## The three rules

1. **Zero third-party** — first-party AndroidX only; no services, no SDKs.
2. **One brain** — station edits touch both `index.html` files together.
3. **Parser contract** — `data-id` stays the last attribute in a station block.

## Stack (the whole thing)

Kotlin · AndroidX (`media`, `appcompat`, `core-ktx`) · `MediaBrowserServiceCompat` ·
`MediaPlayer` + `AssetFileDescriptor` · WebView · Gradle (Kotlin DSL) ·
GitHub Actions · plain HTML/CSS/JS · one synthesized WAV.
