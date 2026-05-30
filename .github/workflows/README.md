# .github/workflows/

CI pipelines. All workflows fetch Filament prebuilts via `./gradlew downloadPrebuilts_*` —
the matching `prebuilts/*` directories are cached per-target keyed on `filaVersion`, so
repeat runs skip the download.

| Workflow | Triggers | What it does |
| :--- | :--- | :--- |
| [`ci.yml`](ci.yml) | push / PR to `main` (docs-only changes skipped via `paths-ignore: ['**.md']`) | One job per platform (jvm matrix / js / ios / android). Each job sets up + builds the native library once, then runs **build → test → sample** as sequential steps that reuse those outputs. The sample steps build the `samples/` apps (a composite `includeBuild` of this repo) to verify the umbrella library is consumable end-to-end — catching breakage pure unit tests miss (Compose config, resource loading, native linking). Gate for merging — must be green. |
| [`pages.yml`](pages.yml) | push to `main` touching the web target (`js/**`, `kotlin/**/src/jsMain/**`, `samples/webApp/**`, … — see its `paths:` filter) / manual dispatch | Builds the `webApp` sample's production webpack bundle and deploys it to GitHub Pages. Already scoped to web-relevant paths, so docs changes never trigger it. |
| [`publish.yml`](publish.yml) | tag matching `[0-9]*` / manual dispatch | Releases to Maven Central. See [Releasing](#releasing) below. |

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
git tag -a 0.1.0-beta02 -m "Release 0.1.0-beta02"
git push origin 0.1.0-beta02
```

The tag's name becomes the published version (passed to Gradle as `-PlibVersion=${tag}`),
so the tag and `gradle.properties` should match.

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
([buildSrc/src/main/kotlin/filament-publish.gradle.kts](../../buildSrc/src/main/kotlin/filament-publish.gradle.kts)).

### Manual / re-run

`workflow_dispatch` accepts a `version` input — useful for re-publishing a botched release
under a new version without retagging. The dispatched run still requires the `build-natives`
matrix to succeed.
