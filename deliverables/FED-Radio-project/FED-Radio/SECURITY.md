# Security Policy — FED-Radio

## Supported versions

| Version | Supported |
|---------|-----------|
| 1.0.x   | ✅ (main branch) |
| < 1.0   | ❌ |

## Reporting a vulnerability

**Do not open a public issue for security problems.**

Email the maintainers listed in [MAINTAINERS.md](MAINTAINERS.md) directly with:

1. A description of the issue
2. Steps to reproduce (a PoC if possible)
3. Affected file(s) / component
4. Potential impact

You'll get an acknowledgment within 72 hours. We'll work the fix, credit you
in the [CHANGELOG.md](CHANGELOG.md) (unless you prefer to stay anonymous), and
coordinate disclosure timing with you.

## Security model — what this app deliberately does (and doesn't do)

FED-Radio is **local-first and offline in the car**:

* **No network in the car bridge.** `CarAudioEngine` only opens bundled audio
  from app assets. It cannot be pointed at remote media.
* **One optional network path.** The phone/tablet WebView may open YouTube
  links (`data-yt`). Those hand off to the OS browser/YouTube app — the
  WebView itself never renders third-party pages.
* **`usesCleartextTraffic="false"`** — HTTP (unencrypted) requests are blocked
  at the manifest level.
* **No third-party code** — no analytics, no ad SDKs, no trackers, no remote
  config. The dependency list is first-party AndroidX only, and
  `.github/workflows/dependency-review.yml` scans every PR for known-vulnerable
  versions.
* **Sideload awareness.** This app is distributed outside app stores. Only
  install APKs you built yourself or downloaded from this repository's CI
  artifacts. The debug build is unsigned-by-release-key by design — never
  distribute a debug APK publicly.

## Known hardening ideas

Tracked in [ROADMAP.md](ROADMAP.md): release signing, a WebView hardening
pass (disable file access from content URLs), and extracting the parser into
a pure module with a fuzzed test suite.
