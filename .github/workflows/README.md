# .github/workflows/

CI pipelines. All workflows fetch Filament prebuilts via `./gradlew downloadPrebuilts_*` —
the matching `prebuilts/*` directories are cached per-target keyed on `filaVersion`, so
repeat runs skip the download.

| Workflow | Triggers | What it does |
| :--- | :--- | :--- |
| [`build.yml`](build.yml) | push / PR to `main` | Compiles every KMP target on the relevant host (Android + JVM + JS on Linux, iOS on macOS) and runs unit tests. Gate for merging — must be green. |
| [`samples.yml`](samples.yml) | push / PR to `main` | Builds the `samples/` apps for each platform to verify the umbrella library is consumable end-to-end. Catches breakage that pure unit tests miss (Compose configuration, resource loading, native linking). |
| [`publish.yml`](publish.yml) | tag matching `[0-9]*` / manual dispatch | Releases to Maven Central. See [Releasing](#releasing) below. |

## Releasing

The publish workflow is a two-phase pipeline:

1. **`build-jni`** — matrix job (macOS arm64, Linux x64, Linux arm64, Windows x64) that
   runs `:java:filament:cmakeBuild` to produce `libfilament-jni.{dylib,so,dll}` for each
   host. Outputs are uploaded as `jni-<arch>` artifacts.
2. **`publish`** — runs on `macos-latest` (needed for iOS framework signing / lipo).
   Downloads all `jni-*` artifacts via `merge-multiple: true` into one flat
   `jni-artifacts/` directory, then invokes `publishAllPublicationsToMavenCentralRepository`
   for every module with `-PjniArtifactsDir=...` so each `:java:*` module bundles the
   right native into its resources.

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
under a new version without retagging. The dispatched run still requires the `build-jni`
matrix to succeed.
