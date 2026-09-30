# Upgrading the Filament version

This guide walks through bumping `filament-kmp` to a new upstream Filament release —
from scoping the diff, to refreshing prebuilts, to adding/removing binding surface
across all five platforms, to verifying the result.

It is written for contributors. If you only *consume* the library, you never need this.

---

## Mental model: why an upgrade touches many layers

`filament-kmp` is a thin wrapper. Every platform binds the same C API (`c/`, the `Fila*`
functions), and the Kotlin API is written once in `commonMain` on top of it (see
[Native Bindings](bindings.md)). A new Filament method is generated up to the Kotlin wrapper:

| Layer | Where | Written by |
| :--- | :--- | :--- |
| **C API** | `c/<module>/generated` | `./gradlew generateCApi`, from the headers in `c/api-headers.txt` |
| **C API leftovers** | `c/<module>/manual` | hand, for each `TODO(handwritten)` the generator leaves |
| **Kotlin externals** | `kotlin/<module>/src/commonMain/.../capi` | `./gradlew generateKotlinExternals` |
| **Kotlin API** | `kotlin/<module>/src/commonMain` | hand: the public method over the externals |

Everything after that is derived: the JNI forwarders are generated from the externals at build
time, Native calls the C symbol directly, and web finds it in the wasm exports.

### Two sources of truth

- **Filament C++ API → our C API (`c/`).** The C shims follow the C++ headers; all platforms bind
  the same `Fila*` functions, so there is no separate per-platform surface to audit.
- **Our C API → the Kotlin externals.** Every `Fila*` function gets a common `external fun`.

`./gradlew apiGaps` audits both.

---

## TL;DR checklist

```sh
# 1. Scope the change (tags optional: defaults to filaVersion → latest upstream release)
scripts/dev/upgrade-diff.sh --summary                    # then re-run without --summary on hot areas

# 2. Bump the version
#    edit gradle.properties -> filaVersion=<new>

# 3. Refresh prebuilts (version-stamped: redone automatically on a version bump)
./gradlew prebuilts prebuilts_wasm                       # libs + headers at <new>; wasm is a source build (slow)

# 4. Audit the surface
./gradlew apiGaps                                        # C++ API missing from c/, Fila* missing from Kotlin

# 5. Apply changes (per-layer recipe below), update tests
scripts/dev/rebuild-materials.sh                         # recompile every .filamat when MATERIAL_VERSION changed

# 6. Verify
scripts/dev/run-tests.sh                                 # jvm + js + ios (+ android if a device is attached)
```

---

## Step by step

### 1. Scope the diff

```sh
scripts/dev/upgrade-diff.sh --summary                 # filaVersion → latest upstream release
scripts/dev/upgrade-diff.sh v1.71.6 --summary         # filaVersion → explicit tag
scripts/dev/upgrade-diff.sh v1.71.5 v1.71.6 --summary # explicit old and new
```

The report opens with a **HIGHLIGHTS** section that extracts the historically surprising
bits so they can't be missed in a big diff: the `MATERIAL_VERSION` bump, `CONFIG_MAX_*`
changes, feature-flag additions/default flips, and added/removed Android Java classes.

Below that, it diffs the upstream tree between the two tags across every surface that drives our bindings:
public C++ headers, backend headers, **Android Java sources**, material/engine enums,
feature-flag defaults, and `RELEASE_NOTES.md`. First run clones upstream into
`scripts/dev/.filament-src-cache/` (~200 MB, gitignored); later runs reuse it.

Start with `--summary` to see *which files* changed, then re-run **without** `--summary` on the
interesting areas for full unified diffs. Pay particular attention to:

- **Android Java sources** — drives the `expect`/`actual` additions. New `public` methods here
  are the ones you add.
- **`MATERIAL_VERSION`** (in `MaterialEnums.h`) — any change means every shipped `.filamat` must
  be recompiled with the new `matc`.
- **`CONFIG_MAX_INSTANCES`** and other WebGL workaround constants — relevant to the
  "uniform buffer too small" class of bugs.
- **`FeatureFlagManager.h`** defaults — silent behavior flips between releases.
- **`RELEASE_NOTES.md`** — often the only place behavior-only changes (no API delta) are written
  down.

### 2. Bump `filaVersion`

Edit [`gradle.properties`](../gradle.properties):

```properties
filaVersion=1.71.6
```

### 3. Refresh the prebuilts

Both libraries and headers are version-stamped: each `prebuilts_<id>` task takes `filaVersion` as
an input and records a stamp inside `prebuilts/<id>/lib`, redoing the work on any mismatch. Bumping
`filaVersion` and building is enough — stale header/library mixes (the old `symbol(s) not found`
linker-error class) can no longer happen silently.

```sh
./gradlew prebuilts prebuilts_wasm
```

`prebuilts/` is gitignored, so deleting it is safe. Upstream publishes no wasm (or windows-arm64)
static libraries, so those `prebuilts_<id>` tasks build them from the release's source tarball
(`BuildFromSourceTask`: host tools, then the Emscripten build with `libfilamat`). CI caches the result
per `filaVersion`. Also check upstream `BUILDING.md` for a new emsdk version and bump `emsdkVersion` in
`gradle.properties` to match.

### 4. Audit the public surface

```sh
./gradlew apiGaps    # writes build/reports/api-gaps.txt
```

The C++ side is compared by symbol, never by parsing names: the public methods of every `*_PUBLIC`
class as clang's AST reports them (inline ones included), plus template instances only the
libraries define, minus every symbol the `c/` objects mention when built at `-O0` (so inline calls
stay calls). Struct fields and enum values aren't covered. The task runs on macOS and Linux hosts (it needs
`clang++`, `nm` and `c++filt`).

### 5. Apply the changes

#### Adding a method

Use `build/reports/api-gaps.txt` as the worklist. Run `./gradlew generateCApi generateKotlinExternals`:
the new method gets its `Fila*` function and Kotlin external, or a `TODO(handwritten)` naming why it
can't be bridged — write that one in `c/<module>/manual/<Record>Manual.{h,cpp}`. Then call the external
from the Kotlin wrapper, e.g. for `ColorGrading::Builder::fastMath(bool)`:

```kotlin
fun fastMath(fastMath: Boolean): Builder = apply { FilaColorGradingBuilder_fastMath(nativeBuilder, fastMath) }
```

API the header only declares under an `#if` (e.g. `UTILS_HAS_THREADING`) is still generated on every
target; where the condition is false it panics, so give the Kotlin side a `@PlatformGap`.

#### Removing / deprecating a method

If upstream removes or `@Deprecated`s a method, check whether it is exposed in our surface
(`grep` the `kotlin/` tree). If it isn't exposed, there's nothing to do. If it is, mirror the
upstream change (regenerate, then delete the Kotlin method or add `@Deprecated`).

### 6. Update tests

Add the new methods to the existing builder-chain tests in `commonTest`
(`ColorGradingTest`, `MaterialBuilderTest`, `EngineTest`, …) so all five targets exercise them.
A builder method just needs to appear in the chain; a method with observable behavior deserves an
assertion.

### 6b. Regenerate the embedded standard materials

`filament-compose` ships five precompiled built-in materials (`StandardMaterial.Lit`/`Unlit`/
`Textured`/`Emissive`/`Transparent`) as embedded `.filamat` blobs — their `.mat` sources and compiled outputs live
in [`kotlin/filament-compose/src/commonMain/materials/`](../kotlin/filament-compose/src/commonMain/materials/),
and the `generateEmbeddedMaterials` Gradle task base64-encodes them into a generated Kotlin object.

A compiled `.filamat` is tied to the Filament ABI, so **whenever `MATERIAL_VERSION` changes** (watch
the step-1 diff), every committed blob must be recompiled with the `matc` of the matching release —
including the two that live outside `filament-compose` (the `emissive` test material and the
`textured` sample material):

```sh
scripts/dev/rebuild-materials.sh
```

The script pulls `matc` out of the release tarball cached by step 3, recompiles every `.mat` in the
repo in place and syncs the shared `emissive.filamat` copy. Commit the refreshed blobs; the embed
tasks pick them up on the next build. `StandardMaterialLifecycleTest` (Tier-B) fails to build a
material if a blob is stale or corrupt.

### 7. Verify

```sh
scripts/dev/run-tests.sh                 # everything the host supports
# or scope it:
scripts/dev/run-tests.sh jvm js          # specific targets
```

The matrix that actually matters per binding path:

| Target | Validates |
| :--- | :--- |
| `:kotlin:*:jvmTest` | C shim rebuild + JNI glue — the desktop `libfilament-c` links against the new prebuilts |
| `:kotlin:*:jsTest`, `:kotlin:*:wasmJsTest` | the wasm links against the new `prebuilts/wasm` and exports every `Fila*` the Kotlin side binds |
| `:kotlin:*:iosSimulatorArm64Test` | C shim + direct `@SymbolName` calls resolve at link time |
| `:kotlin:*:connectedAndroidDeviceTest` | the Android `.so` + JNI glue per ABI (needs a device/emulator) |

A green `jvmTest`/`iosSimulatorArm64Test` is strong evidence the C shim links and the new symbols
resolve against the refreshed prebuilts. A green `jsTest` proves the same for the wasm build.

Finally, walk the samples on each platform — silent renderer behavior changes (default values in
`BloomOptions`, `FogOptions`, etc.) don't show up in any header diff.

---

## Worked example: 1.71.5 → 1.71.6

A patch release with three new public methods, all present in the Android Java API:

| Method | Layers touched |
| :--- | :--- |
| `ColorGrading.Builder.fastMath(Boolean)` | C shim + 4 actuals |
| `MaterialBuilder.coloredPenumbra(Boolean)` | C shim + 4 actuals |
| `Engine.Builder.colorGrading(ColorGrading.Builder)` | C shim + exposed `ColorGrading.Builder` handle as `internal` + 4 actuals |

(At the time each platform had its own `actual`, hence "4 actuals"; web also needed extra
overlay work over upstream's embind `filament.js`. Today each row is the C shim plus the common
Kotlin method and its `external fun`.)

What was **not** added, per the source-of-truth rule:

- `Camera.getEyeFromViewMatrix`, `TransformManager.getChildrenRange`, `ColorGrading.exportLut` —
  C++‑only, not in the Android Java API.
- `View.filterWidth` / `View.minVarianceScale` got `@Deprecated` upstream but aren't exposed in
  our surface, so nothing to remove.

The first build failed at link time (`VertexBuffer::Builder::build` became `const` in 1.71.6 plus
the three new symbols were all undefined) — because the prebuilt **libs were still 1.71.5** while
the **headers had refreshed to 1.71.6**. Clearing `prebuilts/*/lib` and
re-running the download fixed it. This is the footgun in step 3.

---

## Reference: where each surface lives

| Surface | Path |
| :--- | :--- |
| Generated C API / hand-written leftovers | `c/<module>/generated`, `c/<module>/manual` |
| Which headers are API, skipped declarations | `c/api-headers.txt` |
| Generated Kotlin externals | `kotlin/<module>/src/commonMain/kotlin/.../capi/` |
| API classes | `kotlin/<module>/src/commonMain/kotlin/.../*.kt` |
| Interop runtime (per platform) | `kotlin/filament/src/{common,jni,native,web}Main/.../interop/` |
| JNI forwarders, wasm export tables (generated at build time) | `build/generated/bindings/` (from the `external fun`s) |
| Platform actuals (surfaces, loading) | `kotlin/<module>/src/{android,jvm,native,web}Main/.../*.kt` |
| Filament libs / headers | `prebuilts/<id>/lib/` (downloaded, or source-built for `wasm`/`windows-arm64`), `include/` (gitignored) |
| Version | `gradle.properties` → `filaVersion` |

See also: [`scripts/README.md`](../scripts/README.md) (script reference),
[`web/README.md`](../web/README.md) (web bindings),
[`repo-structure.md`](repo-structure.md) (binding architecture).

---

[← Back to docs](README.md)
