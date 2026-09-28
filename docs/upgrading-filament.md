# Upgrading the Filament version

This guide walks through bumping `filament-kmp` to a new upstream Filament release —
from scoping the diff, to refreshing prebuilts, to adding/removing binding surface
across all five platforms, to verifying the result.

It is written for contributors. If you only *consume* the library, you never need this.

---

## Mental model: why an upgrade touches many layers

`filament-kmp` is a thin wrapper. Every platform binds the same C API (`c/`, the `Fila*`
functions), and the Kotlin API is written once in `commonMain` on top of it (see
[Native Bindings](bindings.md)). A new Filament method touches two hand-written layers:

| Layer | What you edit | Reaches |
| :--- | :--- | :--- |
| **C API** (`c/<module>/c/*.h` + `cpp/*.cpp`) | the `Fila*` shim over the C++ method | every platform |
| **Kotlin** (`kotlin/<module>/src/commonMain`) | the public method + its `external fun` declaration | every platform |

Everything between them is derived: the JNI forwarders are generated from the Kotlin declarations
at build time, Native calls the C symbol directly, and web finds it in the wasm exports.

### Two sources of truth

- **Android Java API → the public Kotlin surface.** If a method exists in the Filament Android
  Java API, we add it. If it does **not** exist there (e.g. it's a C++‑only or Web‑only addition),
  we **do not** add it. `scripts/dev/check-common-api.sh` audits this.
- **Filament C++ API → our C API (`c/`).** The C shims follow the C++ headers; all platforms bind
  the same `Fila*` functions, so there is no separate per-platform surface to audit.

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
scripts/dev/check-common-api.sh                          # Android API members missing from commonMain

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
scripts/dev/check-common-api.sh     # Android Java members absent from commonMain expects
```

It is **diagnostic only** — it never edits anything. It prints the gaps; you decide what to
act on, following the two-sources-of-truth rule above.

`check-common-api.sh` checks five kinds of surface per module — whole classes, nested types,
enum/`ALL_CAPS` constants, public methods, and non-private fields — with Kotlin comments
stripped before matching (so a KDoc mention doesn't count as coverage). The field check exists
because Filament's option structs (`ShadowOptions`, `FogOptions`, `Engine.Config`, …) expose
their state as bare fields rather than accessors, so a method-only audit never looked inside
them. Members `@Deprecated` upstream are flagged informationally and don't fail the run.
Intentional gaps (Android-only plumbing, Java SAM interfaces replaced by Kotlin lambdas,
deprecated API) are suppressed via
[`scripts/dev/check-common-api-ignores.txt`](../scripts/dev/check-common-api-ignores.txt) —
add `Class` or `Class.member` entries there with a comment saying why, rather than editing the
script. The script exits non-zero when anything unsuppressed is missing, so it's CI-able.
Matching is still token-level within a module, so occasional false "covered" results remain
possible — verify a suspicious pass by grepping for the specific member name.

### 5. Apply the changes

#### Adding a method

Use the Android Java diff as the worklist. For each new public method — e.g.
`ColorGrading.Builder.fastMath(boolean)`:

1. **C header** — declare the shim in `c/<module>/c/<Class>.h`. Fixed-width types only (no
   `size_t`), no structs by value — see [Declaring a binding](bindings.md#declaring-a-binding):
   ```c
   void FilaColorGradingBuilder_fastMath(FilaColorGradingBuilder* builder, bool fastMath);
   ```
2. **C impl** — implement in `c/<module>/cpp/<Class>.cpp`:
   ```cpp
   void FilaColorGradingBuilder_fastMath(FilaColorGradingBuilder* builder, bool fastMath) {
       FILA_CAST(ColorGrading::Builder, builder)->fastMath(fastMath);
   }
   ```
3. **Kotlin** — the public method and its binding, in the class's `commonMain` file:
   ```kotlin
   fun fastMath(fastMath: Boolean): Builder = apply { FilaColorGradingBuilder_fastMath(nativeBuilder, fastMath) }

   @ExternalSymbolName("FilaColorGradingBuilder_fastMath")
   private external fun FilaColorGradingBuilder_fastMath(builder: NativePointer, fastMath: Boolean)
   ```

That's the whole job: no generator to run, nothing per platform. The next build regenerates the
JNI glue and relinks each native library.

> [!TIP]
> When a method needs the *internal handle of another wrapped object* (e.g.
> `Engine.Builder.colorGrading` takes a `ColorGrading.Builder`), expose that object's native
> handle as `internal` rather than `private`, and pass it through the C shim.

#### Removing / deprecating a method

If upstream removes or `@Deprecated`s a method, check whether it is exposed in our surface
(`grep` the `kotlin/` tree). If it isn't exposed, there's nothing to do. If it is, mirror the
upstream change (delete the method, its `external fun` and the C shim, or add `@Deprecated`).

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
| C shim headers / impl | `c/<module>/c/*.h`, `c/<module>/cpp/*.cpp` |
| API classes + `external fun` bindings | `kotlin/<module>/src/commonMain/kotlin/.../*.kt` |
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
