# Governance — FED-Radio

How the broadcast is run.

## Model

**Benevolent dictatorship with a do-ocracy floor.** The project founder
(see [MAINTAINERS.md](MAINTAINERS.md)) holds final say on direction and
design; anyone who does the work gets a real voice in what they build.
For a project this size, structure should be lighter than the code.

## Roles

- **Founder / Lead maintainer** — owns the vision (the in-house, zero
  third-party rule), merges or rejects, releases versions, holds the release
  keystore if one is created.
- **Contributors** — anyone with a merged PR. Contributors get input
  proportional to their involvement, and are credited in
  [AUTHORS.md](AUTHORS.md) and the [CHANGELOG.md](CHANGELOG.md).
- **Community** — everyone in Discussions. Ideas welcome; the roadmap
  ([ROADMAP.md](ROADMAP.md)) is the record of what's in and what's out.

## Ground rules (enforced)

1. **Zero third-party.** PRs adding external services, analytics, streaming
   SDKs, or trackers will be declined. First-party AndroidX only.
2. **One brain.** Station changes must touch both `index.html` files (app
   asset + web root) in the same PR.
3. **Parser contract is sacred.** `data-id` stays the last attribute; changes
   require the regex, its unit test, and both HTML files updated together.
4. **Docs move with code.** A feature isn't merged until the relevant doc
   (usage/FAQ/CHANGELOG) reflects it.

## Decision making

- **Small stuff** (bug fixes, styling, docs): open a PR; maintainer merges.
- **Direction changes** (new platforms, breaking the parser contract,
  accepting any dependency): open a Discussion first. After ~a week of
  community input, the lead decides, and the call is recorded in
  [ADR.md](ADR.md).
- **Disagreements:** argue the technical merits in the thread. If it can't be
  resolved, the lead rules and writes it down.

## Releases

Versioned per [semver](https://semver.org/) (`1.x.y`), tagged, and noted in
[CHANGELOG.md](CHANGELOG.md). Release APKs attach to GitHub Releases
(see [DEPLOYMENT.md](DEPLOYMENT.md)).

## Amending this document

Via PR, like everything else — but changes here require lead sign-off.
