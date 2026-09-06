# Deploying FED-Radio

FED-Radio is deliberately app-store-free. "Deployment" here means three
private distribution channels, none of which involve Google Play.

## 1. The APK (phones + cars)

**Build it** (see [BUILD.md](BUILD.md)):

```bash
./gradlew assembleDebug          # debug — fine for personal use
./gradlew assembleRelease        # release — needs a signing config (below)
```

**Distribute it privately:**

- GitHub **Actions artifact** — every push produces `FED-Radio-debug-APK`
  (30-day retention). Hand the link to anyone with repo access.
- GitHub **Release** — for versions you want to keep:
  attach `app-release.apk` (or the debug APK) to a tagged release
  (`v1.0.0`). Releases are permanent and easy to link.
- Any private channel you already trust (USB, your own server, Drive).

### Release signing (only if you want updates to install over each other)

Debug builds update fine for personal use. For a stable release identity:

1. Generate a keystore once:
   ```bash
   keytool -genkey -v -keystore fedradio.jks -alias fedradio \
           -keyalg RSA -keysize 2048 -validity 10000
   ```
2. Add to `app/build.gradle.kts` (never commit the keystore):
   ```kotlin
   signingConfigs {
       create("release") {
           storeFile = file(System.getenv("FEDRADIO_KEYSTORE") ?: "fedradio.jks")
           storePassword = System.getenv("FEDRADIO_STORE_PASS")
           keyAlias = "fedradio"
           keyPassword = System.getenv("FEDRADIO_KEY_PASS")
       }
   }
   ```
3. Point `buildTypes.release` at it and run `./gradlew assembleRelease`.
   Keep secrets in environment variables or GitHub Actions secrets, not the repo.

## 2. The web build (GitHub Pages)

The repo root *is* the site: `index.html` + `styles.css`, with bundled audio
served from `app/src/main/assets/audio/`.

1. Repo → **Settings → Pages**.
2. Source: **Deploy from a branch** → `main` → `/ (root)` → Save.
3. Your dashboard goes live at `https://<user>.github.io/FED-Radio/`.

Any static host works the same way — the web build is two files and one audio
path, zero build tooling.

## 3. In-car distribution (Android Automotive OS)

On AAOS cars the dashboard itself runs Android, so the APK installs on the car
directly through the vehicle's own sideloading flow — no phone projection
involved. Same APK, same `automotive_app_desc.xml`, same station contract.

## CI/CD recap

| Event | Workflow | Result |
|---|---|---|
| Push to main | `build.yml` | Debug APK artifact + unit tests |
| Pull request | `build.yml` + `dependency-review.yml` | APK + tests + vulnerability scan |
| Manual | `build.yml` (workflow_dispatch) | On-demand APK |

Tag a release when the dashboard feels right. That's the whole pipeline —
no store review, no waiting, no third-party infrastructure.
