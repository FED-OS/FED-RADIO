<!-- Root-level fallback PR template.
     GitHub prefers .github/PULL_REQUEST_TEMPLATE.md; this copy exists so
     PRs opened against hosts/tools that read the root are still covered.
     Keep both files in sync. -->

## Description
What does this PR change and why?

## Type of change
- [ ] Bug fix
- [ ] New feature
- [ ] Station / content change
- [ ] Documentation
- [ ] Build / CI

## Checklist
- [ ] I updated `app/src/main/assets/index.html` AND the root `index.html` together (if stations changed)
- [ ] New audio files are `.wav`/`.mp3`/`.m4a`/`.ogg` in `app/src/main/assets/audio/` named after the station `data-id`
- [ ] `data-id` remains the **last attribute** in each station block (car parser contract)
- [ ] I tested the debug APK on a phone (and in the car if media code changed)
- [ ] No third-party SDKs or external services were added (in-house rule)

## Screenshots
If UI changed, paste before/after shots.
