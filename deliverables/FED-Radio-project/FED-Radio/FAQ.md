# FED-Radio — FAQ

Answers to the questions that come up in the first week.

## General

**Is this on the Play Store?**
No — and that's deliberate. FED-Radio is a private, in-house build. You
sideload the APK (see [INSTALL.md](INSTALL.md)) or let the repo's CI build it
for you. No store review, no listing, no third-party requirements.

**Does it work on iPhone / CarPlay?**
Not in this form. The car bridge is Android's `MediaBrowserServiceCompat` —
an Android-native mechanism. The web build works in any browser, including
iOS Safari, but CarPlay would need a separate (and much more locked-down)
integration.

**So the car itself has a dev mode I need to enable?**
No — the car has no settings to touch at all. The **phone's** Android Auto app
has a developer mode (tap *Version* ~10×), and its *Unknown sources* toggle is
what makes the car trust your sideloaded app. The head unit just displays
whatever the phone projects.

**What does the car actually do for me?**
Everything visual and physical: it draws the dark media UI (station list, play
buttons, progress bar, album art), routes audio through the car speakers, and
wires up the steering-wheel next/previous buttons. Your code only supplies the
station list and the audio.

## Stations & content

**I edited `index.html` but the car shows old stations.**
The catalog is compiled into the APK as an asset — editing the file on your
laptop changes nothing on the phone until you rebuild and reinstall
(`./gradlew assembleDebug && adb install -r ...`).

**Why is `data-id` required to be the last attribute?**
The car parser (`CarAudioEngine.kt`) matches `data-id="...">TITLE` with a
regex. It's the zero-dependency way to read the catalog — no HTML parser, no
libraries. The pattern is unit-tested; if you must change it, change the test
too.

**What's the easiest way to put my YouTube show in the car?**
Two-layer approach: set `data-yt` so phone/desktop users tap through to the
video, and drop an audio export of the same episode at
`assets/audio/<data-id>.m4a` so the car plays it offline. The car never
touches YouTube — by design.

**How big can bundled audio get?**
APKs handle hundreds of MB fine, but keep episodes in `.m4a`/`.mp3` rather
than raw `.wav` — roughly a tenth of the size at radio quality.

## Car behavior

**Tapping a station plays a short jingle, not my track.**
That's the Station ID fallback. It means no audio file matched that station's
`data-id` — drop `assets/audio/<data-id>.wav|.mp3|.m4a|.ogg` and rebuild.

**My app doesn't appear in the car's app list.**
Three usual suspects: (1) the *Unknown sources* toggle in Android Auto's
developer settings isn't on; (2) the phone's Android Auto version predates
your phone's Android version quirks — update it; (3) the car uses wireless
projection and needs a fresh connection — replug/re-pair once.

**Audio stops when navigation talks / a call comes in.**
That's correct behavior — `CarAudioEngine` requests and honors audio focus:
navigation ducks the volume, calls pause the station, and playback resumes
afterward.

**Does it drain my phone / need the screen on?**
The service holds a partial wake-lock **only while playing**, and runs as a
proper foreground media service with its notification. Screen off is fine.

## Building

**Gradle sync downloads dependencies — I thought this was zero-dependency?**
"Zero third-party" means no external *services/SDKs*: no analytics, no
streaming SDKs, no trackers. The app still uses Google's **first-party
AndroidX** libraries (media, appcompat, core) — they're the OS's own toolkit
and the Android Auto bridge literally cannot exist without `androidx.media`.

**`adb install` fails with a signature/versions error.**
Uninstall the old build first (`adb uninstall com.fedradio`), or bump
`versionCode`. Matching signatures matter once you start using a release
keystore instead of the debug one.

**Where's the artifact from CI?**
Repo → **Actions** → latest **Build APK** run → scroll to **Artifacts** →
`FED-Radio-debug-APK`.

More build detail in [BUILD.md](BUILD.md); car setup in [INSTALL.md](INSTALL.md).
