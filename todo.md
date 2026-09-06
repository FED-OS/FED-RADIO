# FED-Radio — Project TODO

Simple board. One brain, two files, zero third-party platforms.
When something is done, move it to the Done list (dated). No project trackers, no apps.

## TODO (Next Up)

- [ ] Replace `VIDEO_ID_1`, `VIDEO_ID_2`, `VIDEO_ID_3` placeholders in `app/src/main/assets/index.html` with real YouTube video IDs
- [ ] Drop real per-station audio into `app/src/main/assets/audio/` as `track-01.wav`, `track-02.wav`, `track-03.wav` (car playback uses these; anything other than the bundled station-ID jingle)
- [ ] Put my Ko-fi username into the footer button in both `index.html` files (`YOUR_USERNAME` placeholder)
- [ ] First real sideload test on my phone + car (follow INSTALL.md)
- [ ] Turn on GitHub Pages for the web dashboard (Settings → Pages → deploy from branch)
- [ ] Record a 2-minute demo video for the Discussions tab

## Later (Nice-to-Have)

- [ ] More stations (just copy a station block in the brain — takes 60 seconds)
- [ ] Real artwork per station (swap `drawable-nodpi/ic_station_art.png`)
- [ ] Explore local network streaming of my own media files (still in-house, still no third-party)

## Done

- [x] 2026-09-06 — v2.1.0 The Overnight Evolution: auto-advance (broadcast ends → dial rolls to the next frequency, matching the car), desktop keyboard tuning (← → Space), installable web build (own manifest + locally drawn icons — still zero third-party)
- [x] 2026-09-06 — v2.0.0 The Print Edition: complete recomposition — broadsheet masthead, ticker tape, SMPTE test-pattern monitor, live broadcast clock, FM tuning-dial navigation; native paper/ink theme to match
- [x] 2026-09-06 — v1.1.0 Studio Update: embedded YouTube player + now-playing strip + VU standby on phone/web; fixed car station-list parsing (plain-text titles) and phantom comment stations; unit tests now parse the real brain file (6 tests)
- [x] 2026-09-06 — v1.0.0 baseline: app + car service + web dashboard + docs, first-party AndroidX only
- [x] 2026-09-06 — Bundled station-ID jingle so the car has real audio out of the box
- [x] 2026-09-06 — CI workflow building the debug APK on every push

## Ground Rules (always)

Zero third-party dependencies. The brain is one HTML file. `data-id` is always the last attribute. When in doubt, read CONTRIBUTING.md.
