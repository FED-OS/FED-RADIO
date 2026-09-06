# FED-Radio

<p align="center">
  <img src="docs/icon.png" width="120" alt="FED-Radio logo" />
</p>

<p align="center">
  <strong>In-house radio. Built from scratch. Zero third-party.</strong><br>
  One local HTML file powers a radio app that runs on <strong>desktop, phone, tablet, and your car's Android Auto dashboard</strong>.
</p>

<p align="center">
  <a href="https://ko-fi.com/YOUR_USERNAME"><img height="36" src="https://ko-fi.com/img/githubbutton_sm.svg" alt="Buy Me a Coffee at ko-fi.com" /></a>
</p>

---

## What is FED-Radio?

FED-Radio is a private, SiriusXM-style radio project built entirely in-house:

- **One source of truth** — a single local `index.html` "brain" holds your station list. No databases, no APIs, no third-party SDKs.
- **Every screen** — phone, tablet, and desktop render that HTML in a WebView. The web build (this repo on GitHub Pages) plays in any browser.
- **The car** — when your phone plugs into a car with Android Auto, the `CarAudioEngine` service parses that same HTML file as raw text and publishes your stations to the dashboard. Android Auto draws the play/pause UI, progress bar, and track list itself, and routes audio through the car speakers — steering-wheel next/previous buttons included.
- **Works out of the box** — a synthesized station-ID jingle is bundled in `assets/audio/`, so the car has something real to play the moment you install.

```
[ assets/index.html ]  (the brain — your station list, hardcoded)
        |
        +--> Phone / Tablet / Desktop : WebView renders it visually
        +--> Browser (GitHub Pages)  : root index.html + styles.css
        +--> Car (Android Auto)      : CarAudioEngine parses it as text,
                                       plays audio/station_id.wav + your files
```

## The station contract

Everything in FED-Radio flows from one HTML pattern:

```html
<div class="station" data-yt="YOUTUBE_ID" data-id="track-01">Track One: Your Title</div>
```

- `data-id` — the station's unique id. **Must be the last attribute** (the car parser's regex depends on it). The car plays `app/src/main/assets/audio/<data-id>.wav` for it (`.mp3`/`.m4a`/`.ogg` also work).
- `data-yt` — optional. On phone/tablet/web, tapping the card opens that YouTube video.
- The text between `>` and `</div>` — the title shown everywhere, including the car screen.

Add a station = copy a block, pick an id, drop an audio file. That's it.

## Quick start (5 minutes)

1. **Get the APK**
   - Build it yourself: see [BUILD.md](BUILD.md), or
   - Grab `FED-Radio-debug-APK` from the latest [Actions run](../../actions) (artifact), or
   - Open this repo in Android Studio and press ▶.
2. **Enable sideloading for the car** (one-time, on your phone):
   - Open the **Android Auto** app → scroll to the bottom → tap **Version** ~10 times until it says *"You are now a developer!"*
   - Three-dot menu (top right) → **Developer settings** → check **Unknown sources**.
3. **Install** the APK on your phone (`adb install app-debug.apk`, or just open it).
4. **Plug into your car.** FED-Radio appears alongside Spotify. Tap a station, hear the station ID, done — the car handles every button and every speaker.
5. **Make it yours**: replace the placeholder stations in `app/src/main/assets/index.html` with your real video IDs, and (if you want offline audio) drop `.wav` files named after each `data-id` into `app/src/main/assets/audio/`.

Full walkthroughs: [INSTALL.md](INSTALL.md) · [usage.md](usage.md) · car behavior explained in the [FAQ](FAQ.md).

## Repository layout

```
FED-Radio/
├── index.html + styles.css        # Web build (GitHub Pages ready)
├── app/
│   └── src/main/
│       ├── assets/index.html      # THE BRAIN (car + WebView source of truth)
│       ├── assets/audio/          # Bundled audio (station ID included)
│       └── java/com/fedradio/
│           ├── MainActivity.kt    # Phone/tablet view (WebView)
│           └── CarAudioEngine.kt  # Android Auto bridge (MediaBrowserServiceCompat)
├── .github/workflows/             # CI: APK build + dependency review
├── docs/                          # Icon art, social image
└── (governance & guides: see SUMMARY.md)
```

The complete annotated tree lives in [SUMMARY.md](SUMMARY.md).

## Ground rules

- **In-house only.** No third-party SDKs, no analytics, no external services, no app stores required.
- **Local first.** The only network use is optional YouTube links in the phone view. The car bridge is fully offline.
- **Keep the parser contract.** `data-id` stays the last attribute; station blocks stay single-line.

## License

[MIT](LICENSE) — do whatever you want, just keep the notice. See [COPYING.md](COPYING.md) and [NOTICE.md](NOTICE.md).

## Contributing

Station ideas, bug fixes, and dashboard tweaks are welcome — read [CONTRIBUTING.md](CONTRIBUTING.md) first. Roadmap: [ROADMAP.md](ROADMAP.md). Architecture decisions: [ADR.md](ADR.md).

*Built from scratch. Broadcast from the garage.* 📻🚗
