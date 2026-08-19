# .github/workflows/

CI pipelines. All workflows fetch Filament prebuilts via `./gradlew downloadPrebuilts_*` —
the matching `prebuilts/*` directories are cached per-target keyed on `filaVersion`, so
repeat runs skip the download.

| Workflow | Triggers | What it does |
| :--- | :--- | :--- |
| [`ci.yml`](ci.yml) | **push to `main`** and **every PR** (no path filters — docs included); **manual dispatch** (job picker) | One job per platform (jvm matrix / js / wasm / ios / android). Each job sets up + builds the native library once, then runs **build → test → sample** as sequential steps that reuse those outputs. The sample steps build the `samples/` apps (a composite `includeBuild` of this repo) to verify the umbrella library is consumable end-to-end — catching breakage pure unit tests miss (Compose config, resource loading, native linking). See [Running CI](#running-ci). |
| [`status-{jvm,js,wasm,ios,android}.yml`](status-jvm.yml) | `workflow_run` after **CI** completes on `main` | Reflect each platform job's conclusion from the latest `main` CI run into their own conclusion, powering the per-platform README badges. A *skipped* job (verified-merge, see below) counts as passing — only a real failure turns a badge red. |
| [`pages.yml`](pages.yml) | push to `main` touching the web target (`web/**`, `kotlin/**/src/**`, `samples/webApp/**`, … — see its `paths:` filter) / manual dispatch | Builds the `webApp` sample's production webpack bundle and deploys it to GitHub Pages. Already scoped to web-relevant paths, so docs changes never trigger it. |
| [`publish.yml`](publish.yml) | tag matching `[0-9]*` / manual dispatch | Releases to Maven Central, then cuts the matching GitHub release with that version's CHANGELOG section as its notes. See [Releasing](#releasing) below. |

## Running CI

The full platform matrix runs on **push to `main`** and on **every PR** — **no path filters**, so
even a docs-only PR runs it and the required `ci-gate` check always reports (this mirrors
google/filament's always-run presubmit and avoids PRs stuck with no status). The matrix is expensive
(native prebuilts, Android emulator, iOS XCFrameworks), so:

- **Verified-merge skip** — the `check-verification` job skips the platform jobs on a push to `main`
  whose commit is GitHub-**verified** (the signed merge commit a UI merge of a PR produces — that PR
  already ran the matrix). Direct/unverified pushes to `main` still run the full matrix. This is the
  one place CI is *not* re-run; if your merge flow yields unverified `main` commits, CI simply runs
  (wasteful, never wrong).
- **Concurrency** — a new push to a PR cancels the in-flight run for that ref
  (`cancel-in-progress`), so only the latest commit is built.
- **External (fork) PRs** wait for a maintainer to click **"Approve and run"** — this is
  GitHub's native fork-PR approval (*Settings → Actions → General → Fork pull request workflows
  from outside collaborators → "Require approval for all outside collaborators"*), **not**
  anything in the workflow. Collaborators' PRs run with no approval step.
- **Manually** — *Actions → CI → Run workflow* (`workflow_dispatch`) with a `jobs` input to
  pick `all` / `jvm` / `js` / `ios` / `android`. Handy for re-checking one platform. Dispatch
  runs don't enforce the merge gate.
- **iOS XCFramework assembly** (release-mode K/N linking, ~18 min) is skipped on PRs and runs
  only on push to `main` / dispatch — it verifies the distribution artifacts, which `publish.yml`
  needs. PRs still link & run iOS via the simulator tests and the sample `xcodebuild` step.

### The merge gate

`ci-gate` is a tiny aggregator job (`needs: [jvm, js, wasm, ios, android]`, `if: always()`) that, **on
PRs**, fails unless every platform job succeeded. `main` is branch-protected to require `ci-gate`
(strict / up-to-date), so a PR can't merge until the matrix is green. On push to `main` and on manual
dispatch it only summarizes — so the intentionally-skipped jobs from a verified merge don't fail it.

To change which jobs are required, edit the `ci-gate` `needs:` list and the branch-protection
`required_status_checks.contexts` together.

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
