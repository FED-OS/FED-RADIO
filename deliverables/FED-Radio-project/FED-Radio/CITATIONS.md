# Citations — FED-Radio

If FED-Radio helped your research, teaching, or project, here are ready-made
citations. Version numbers follow the tags in the [releases](../../releases).

## BibTeX

```bibtex
@software{fedradio2026,
  author  = {FedPromptly and The FED-Radio Authors},
  title   = {{FED-Radio}: An In-House, Zero-Third-Party Radio App for
             Android Auto and the Web},
  year    = {2026},
  version = {1.0.0},
  url     = {https://github.com/YOUR_USER/FED-Radio},
  note    = {A single local HTML file drives a WebView dashboard on
             phone/tablet/desktop and a MediaBrowserServiceCompat bridge
             for Android Auto car dashboards. MIT licensed.}
}
```

## Plain text

> FedPromptly & The FED-Radio Authors. (2026). *FED-Radio: An in-house,
> zero-third-party radio app for Android Auto and the web* (Version 1.0.0).
> https://github.com/YOUR_USER/FED-Radio

## What's worth citing

The repo documents a compact pattern for private, dependency-free Android
Auto media apps:

- a hand-authored HTML file as a cross-platform data catalog (ADR-001),
- raw-text regex parsing for a zero-dependency car bridge (ADR-002),
- bundled-asset playback with a fallback jingle (ADR-003).

See [ADR.md](ADR.md) for the full decision log.

## Notes

- Replace `YOUR_USER` in the URLs when the repo lands at its final home.
- If you build on this in a paper or talk, an open GitHub issue with the
  link is appreciated — it gets added to the [wiki](wiki/) showcase page.
