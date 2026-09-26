# .github/workflows/

CI pipelines. All workflows fetch Filament prebuilts via `./gradlew downloadPrebuilts_*` —
the matching `prebuilts/*` directories are cached per-target keyed on `filaVersion`, so
repeat runs skip the download. See [Caches](#caches) for what is cached where.

| Workflow | Triggers | What it does |
| :--- | :--- | :--- |
| [`ci.yml`](ci.yml) | **push to `main`** and **every PR**, path-filtered per platform; **manual dispatch** (job picker) | One job per platform (jvm matrix / js / wasm / ios / android). Each job sets up + builds the native library once, then runs **build → test → sample** as sequential steps that reuse those outputs. The sample steps build the `samples/` apps (a composite `includeBuild` of this repo) to verify the umbrella library is consumable end-to-end — catching breakage pure unit tests miss (Compose config, resource loading, native linking). See [Running CI](#running-ci). |
| [`status-{jvm,js,wasm,ios,android}.yml`](status-jvm.yml) | `workflow_run` after **CI** completes on `main` | Reflect each platform job's conclusion from the latest `main` CI run into their own conclusion, powering the per-platform README badges. A job *skipped* by the path filter counts as passing — only a real failure turns a badge red. |
| [`pages.yml`](pages.yml) | PRs touching build config (see its `paths:` filter) / **published release** / manual dispatch | Builds the `webApp` sample's production webpack bundle and the Dokka API docs. PRs only validate; a **published release** (or dispatch) deploys both to GitHub Pages (docs under `/api`), and a release also archives that version's docs on the orphan `docs-archive` branch for re-publishing under `/api/older/<version>` — see [Versioned API docs](#versioned-api-docs). |
| [`publish.yml`](publish.yml) | tag matching `[0-9]*` / manual dispatch | Releases to Maven Central, then cuts the matching GitHub release with that version's CHANGELOG section as its notes. See [Releasing](#releasing) below. |

## Running CI

The `changes` job maps the PR's (or push's) changed files to platforms with
[`.github/scripts/ci-changes.sh`](../scripts/ci-changes.sh), and only those jobs run:

| Changed | Runs |
| :--- | :--- |
| `*.md`, `docs/`, other workflows, `scripts/dev/*` | nothing (only `ci-gate`, so the PR still gets a status) |
| `c/` | jvm, web, ios |
| `java/`, `kotlin/*/src/jvm*`, `kotlin/*/api/`, `samples/desktopApp/` | jvm |
| `web/`, `kotlin/*/src/{web,js,wasmJs}*`, `samples/webApp/`, emsdk/wasm-lib scripts | web-runtime → js + wasm |
| `kotlin/*/src/{native,ios}*`, `samples/iosApp/` | ios |
| `kotlin/*/src/android*`, `samples/androidApp/` | android |
| anything else (`commonMain`, Gradle files, `ci.yml`, …) | everything |

Try it locally: `printf '%s\n' c/foo.cpp | .github/scripts/ci-changes.sh -`.

- **Main runs too** — pushes to `main` run the same filtered jobs. That is what keeps the caches
  warm: a PR can only restore caches saved by its own ref or by `main`.
- **Concurrency** — a new push to a PR cancels the in-flight run for that ref
  (`cancel-in-progress`), so only the latest commit is built.
- **External (fork) PRs** wait for a maintainer to click **"Approve and run"** — this is
  GitHub's native fork-PR approval (*Settings → Actions → General → Fork pull request workflows
  from outside collaborators → "Require approval for all outside collaborators"*), **not**
  anything in the workflow. Collaborators' PRs run with no approval step.
- **Manually** — *Actions → CI → Run workflow* (`workflow_dispatch`) with a `jobs` input to
  pick `all` / `jvm` / `web` / `ios` / `android`. Handy for re-checking one platform. Dispatch
  runs don't enforce the merge gate.
- **iOS XCFramework assembly** (release-mode K/N linking, ~18 min) is skipped on PRs and runs
  only on push to `main` / dispatch — it verifies the distribution artifacts, which `publish.yml`
  needs. PRs still link & run iOS via the simulator tests and the sample `xcodebuild` step.

### The merge gate

`ci-gate` is a tiny aggregator job (`needs:` every platform job, `if: always()`) that fails if the
`changes` filter failed or any platform job failed or was cancelled; jobs the filter skipped pass.
`main` is branch-protected to require `ci-gate`, so a PR can't merge until the selected jobs are green.

To change which jobs are required, edit the `ci-gate` `needs:` list and the branch-protection
`required_status_checks.contexts` together.

## Caches

The repo gets 10 GB of Actions cache; past that, GitHub evicts least-recently-used entries. A run can
restore caches from its own ref and from `main` only, so the rules are:

- **Gradle** (`setup-gradle-cached`): written only by `main`; transforms, JDKs and build-cache excluded.
- **Prebuilts, emsdk, wasm libs**: saved on a miss by any ref. Once `main` has them, PRs hit and never save.
  The wasm libs (~50 min to build) are keyed on `filaVersion` + `build-wasm-libs.sh`.
- **AVD** (2+ GB, fixed key): saved only by `main`.
- **publish.yml** only restores: a tag ref's caches are invisible to every other run.

Check usage with `gh cache list --sort size_in_bytes` and `gh api repos/{owner}/{repo}/actions/cache/usage`.

## Releasing

The publish workflow is a two-phase pipeline:

1. **`build-natives`** — matrix job (macOS arm64, Linux x64, Linux arm64, Windows x64) that
   runs `:java:cmakeBuildFilamentCJvm` to produce the combined `libfilament-c.{dylib,so,dll}`
   for each host. Outputs are uploaded as `c-<arch>` artifacts.
2. **`publish`** — runs on `macos-latest` (needed for iOS framework signing / lipo).
   Downloads all `c-*` artifacts via `merge-multiple: true` into one flat `c-artifacts/`
   directory, then invokes `publishAllPublicationsToMavenCentralRepository` for every module
   with `-PcArtifactsDir=...` so the `:java` module bundles every platform's native into its
   resources (the `:kotlin:*` jvm artifacts pick it up transitively).

### Versioned API docs

The docs job labels the site with `libVersion` from `gradle.properties` and Dokka's versioning
plugin renders a version dropdown. Only a published release goes live — PRs build the site and throw it away, so the deployed docs
always describe a released version. Released versions live as `<version>/` directories on the orphan
**`docs-archive`** branch: every run clones it into `build/previousDocs` before generating, but
only a **published release** writes back — it force-pushes a single fresh commit with the tag's
docs added (a store, not a history, otherwise the repo would gain a full copy of the site per
build). Each version is archived once, built from its tag.

A generated site is ~90 MB and GitHub Pages caps one site at 1 GB, so the archive keeps only the
four newest versions (the tags stay, so anything older can be rebuilt).

The archive starts empty, so the first release after this landed shows no dropdown — it appears
from the second one on. Releases older than that are not backfilled; their docs can still be
rebuilt from their tags with `./gradlew dokkaGenerate`.

### How to cut a release

```bash
# 1. Bump libVersion in gradle.properties + the samples version catalog, commit.
# 2. Tag (no `v` prefix — the workflow's tag filter is [0-9]*).
git tag -a 0.3.0 -m "Release 0.3.0"
git push origin 0.3.0
```

The tag's name becomes the published version (passed to Gradle as `-PlibVersion=${tag}`),
so the tag and `gradle.properties` should match.

Once Maven Central succeeds, the last step creates the **GitHub release** for that tag, using the
tag's own `## [<version>]` section of [CHANGELOG.md](../../CHANGELOG.md) as the release notes — so
the changelog is the single source of truth and nothing is written twice. It runs only on a tag
push (a `workflow_dispatch` run has no tag to hang a release off), and if the changelog has no
section for that version it logs a warning and falls back to GitHub's generated notes rather than
publishing an empty release. Re-running a publish for a tag that already has a release will fail on
this step — delete the release first, or edit it by hand.

### Required secrets

Configured at the repo level under Settings → Secrets and variables → Actions:

| Secret | Purpose |
| :--- | :--- |
| `MAVENCENTRAL_USERNAME` / `MAVENCENTRAL_PASSWORD` | Central Portal user token. |
| `SIGNING_KEY` | ASCII-armored PGP private key (single line, `\n`-escaped). |
| `SIGNING_KEY_ID` | Last 8 hex chars of the key fingerprint. |
| `SIGNING_PASSWORD` | PGP key passphrase. |

The vanniktech publish plugin reads them as `ORG_GRADLE_PROJECT_*` env vars; signing
activates automatically because the convention plugin gates on `signingInMemoryKeyId`
([build-logic/src/main/kotlin/filament-publish.gradle.kts](../../build-logic/src/main/kotlin/filament-publish.gradle.kts)).

### Manual / re-run

`workflow_dispatch` accepts a `version` input — useful for re-publishing a botched release
under a new version without retagging. The dispatched run still requires the `build-natives`
matrix to succeed.
