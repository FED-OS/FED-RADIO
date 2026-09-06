# Architecture Decision Records — FED-Radio

Numbered log of the calls that shaped this project, newest context first.
Format: status · context · decision · consequences.

---

## ADR-001 — HTML file as the station database

**Status:** Accepted (v1.0.0)

**Context:** FED-Radio needs a station catalog readable by (a) a WebView on
phone/tablet, (b) any browser via GitHub Pages, and (c) the Android Auto
bridge — with zero third-party libraries and no external services.

**Decision:** A single hand-authored `index.html` is the catalog. Station
blocks follow one pattern:

```html
<div class="station" data-yt="..." data-id="track-01">Title</div>
```

The app asset (`app/src/main/assets/index.html`) is the source of truth; the
repo-root `index.html` is its web twin and must be edited in lockstep
(enforced by the PR checklist).

**Consequences:** One file to edit, every platform updated. The cost is a
strict authoring contract (see ADR-002) and no dynamic data — which is
exactly the point.

---

## ADR-002 — Raw-text regex parsing for the car bridge

**Status:** Accepted (v1.0.0)

**Context:** The car bridge must read the catalog without shipping an HTML
parser or any dependency.

**Decision:** `CarAudioEngine` reads `index.html` as raw text and extracts
stations with the regex `data-id="([^"]*)">([^<]*)`. The contract:
`data-id` is the **last attribute** in the tag, the title is the bare text
node, one station per line.

**Consequences:** Zero-dependency parsing that anyone can read. Fragile to
HTML reformatting — mitigated by the unit test on the pattern
(`CarAudioEngineUnitTest.kt`) and the documented contract in CONTRIBUTING.md.
Planned hardening: extract to a pure `StationParser` with a fixture suite
(see ROADMAP.md).

---

## ADR-003 — Bundled audio + jingle fallback in the car

**Status:** Accepted (v1.0.0)

**Context:** Android Auto prohibits video and heavy web content on the
dashboard. The project refuses external streaming services. The first build
must produce sound in the car with zero authoring effort.

**Decision:** The car bridge plays only **bundled** audio from
`app/src/main/assets/audio/<data-id>.{wav,mp3,m4a,ogg}` via
`MediaPlayer` + `AssetFileDescriptor`. Missing files fall back to the
synthesized `station_id.wav` ident. Finished tracks auto-advance (radio
behavior). Audio extensions are added to `androidResources.noCompress` so
`openFd()` seeking works.

**Consequences:** Guaranteed-out-of-the-box playback, fully offline,
steering-wheel controls free from Android Auto's templates. Long-form content
should use compressed formats (`.m4a`) to keep the APK lean. YouTube content
reaches the car only as pre-exported audio — streaming it live is explicitly
rejected (ROADMAP.md).

---

## ADR-004 — AndroidX-only dependency policy

**Status:** Accepted (v1.0.0)

**Context:** "Zero third-party" must be defined precisely enough to enforce
in review.

**Decision:** The only allowed dependencies are first-party AndroidX
libraries (`core-ktx`, `appcompat`, `media`) — the OS's own toolkit, required
for `MediaBrowserServiceCompat` to exist at all. Everything else (analytics,
streaming SDKs, remote config, crash reporters, even image loaders) is
banned; `dependency-review.yml` scans PRs.

**Consequences:** A dependency list a human can hold in their head, no
third-party code paths to audit, and a crisp ground rule for governance. The
cost: some wheel reinvention, accepted deliberately.

---

## ADR-005 — Private sideloaded distribution

**Status:** Accepted (v1.0.0)

**Context:** The project is a private radio system; Play Store review adds
requirements (and rejections) with no benefit for an audience of one garage.

**Decision:** Distribution is sideloaded APKs from CI artifacts and GitHub
Releases, plus the GitHub Pages web build. Car visibility comes from the
Android Auto developer-mode **Unknown sources** toggle on the phone.

**Consequences:** No store gates, instant updates, total privacy. Users must
flip one toggle on their phone (documented in INSTALL.md) and trust only APKs
built by themselves or the repo's CI (see SECURITY.md).

---

*New records are appended; superseded decisions are marked, never deleted.*
