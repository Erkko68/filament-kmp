# Upstream Filament source patches (web prebuilt)

Patches applied to the **upstream Filament** source tree before building the web
prebuilt (`prebuilts/web/filament.js` + `.wasm` + `.d.ts`). These are *engine*
patches — they ship **inside the wasm**, not in this repo's Kotlin/JS code. They
are distinct from the `../filament.patch.d.ts` / `../filament.dts-overrides.json`
files, which only patch the *TypeScript externals* Karakum reads.

## How to apply / rebuild

```sh
git clone --filter=blob:none https://github.com/google/filament.git
cd filament && git checkout <base SHA below>
git apply /path/to/js/patches/upstream/*.patch
export EMSDK=<emsdk 5.0.4>            # BUILDING.md pins 5.0.4
./build.sh -p wasm release           # builds host tools, then the wasm
# outputs: out/cmake-wasm-release/web/filament-js/{filament.js,filament.wasm,filament.d.ts}
cp out/cmake-wasm-release/web/filament-js/filament.{js,wasm} \
   /path/to/filament-kmp/prebuilts/web/
```

> **d.ts note:** copy only `filament.js` + `filament.wasm`. We intentionally keep
> the **`filaVersion` (1.71.5) `filament.d.ts`**, not the HEAD one. The fix is a
> runtime C++ change that ships in the wasm and doesn't touch the binding surface.
> The HEAD d.ts is a pure *superset* (new `camutils`/`viewer`/automation helpers
> the project doesn't bind) and its appended `viewer` block has a dangling
> `BlendMode` reference (should be `View$BlendMode`) that breaks Karakum codegen.
> The 1.71.5 d.ts still accurately describes the subset the engine exposes, so the
> generated externals match the committed `jsMain` actuals. Drop this note once
> `filaVersion` is bumped to a release that contains this fix and a clean d.ts.

## Patches

### `0001-webgl-rewrite-CONFIG_MAX_INSTANCES-array-size.patch`

- **Base:** Filament `main` @ `34e9ee43efb7e2555d2a0561d353ea01899bb6e3`
  (post-1.71.5; includes the froxel fix #10000, so only the instancing array
  remains).
- **Symptom:** On ANGLE-D3D11 (Chromium/Windows) and some Android/Linux GLES
  drivers, every `glDrawElementsInstanced` trips
  `GL_INVALID_OPERATION: ...uniform buffer that is too small`; the draw is
  dropped and lit materials render black. Chromium/macOS (ANGLE-Metal)
  tolerates it.
- **Cause:** Those drivers don't fold
  `const int CONFIG_MAX_INSTANCES = SPIRV_CROSS_CONSTANT_ID_1` into the
  uniform-block array length `PerRenderableData data[CONFIG_MAX_INSTANCES]`.
  They size the block from the matc-baked default (`CONFIG_MAX_INSTANCES` is
  **64** in matc, which isn't built with `__EMSCRIPTEN__`), while the web engine
  binds only `sizeof(PerRenderableUib)` (`CONFIG_MAX_INSTANCES` is **8** here).
  Bound 8 < declared 64 → "too small".
- **Why engine-side, not matc:** matc emits one shared GLES shader for *both*
  WebGL and native Android (both are `TargetApi::OPENGL` / `ShaderModel::MOBILE`
  — there is no WebGL target in the enum). Baking a literal would break Android,
  where the runtime legitimately binds 64. The divergence is per-driver, so the
  fix is gated on a driver-workaround flag in the GL backend.
- **Fix (3 files):**
  - `OpenGLContext.h` — adds a `bugs.spec_constant_array_size_not_folded` flag
    (+ its `mBugDatabase` entry).
  - `OpenGLContext.cpp` — sets the flag in `initBugs` when the renderer is
    ANGLE-on-D3D11 (`GL_RENDERER` contains `"ANGLE"` and `"Direct3D11"` —
    Chromium/Firefox on Windows, including through WebGL).
  - `ShaderCompilerService.cpp` — when the flag is set, rewrites the symbolic
    array length `[CONFIG_MAX_INSTANCES]` to the literal the engine actually
    binds (read from the spec-constant array, so FL0=1 is handled, and the value
    is correct on every platform — 8 on web, 64 on native), padded with spaces
    to preserve byte count. Spec-constant-aware revival of the 2022 hack at
    `fe3790cb9` (#5859), removed when spec constants were assumed portable.
- **Why the flag, not `#ifdef __EMSCRIPTEN__`:** the flag only rewrites on
  drivers that actually miscompile (compliant drivers — incl. ANGLE-Metal on
  macOS Chrome — keep the real spec constant and its small-array perf win), it
  also covers affected *native* GLES drivers, and it matches Filament's existing
  `bugs` precedent (more likely to merge). Add more offending renderer strings
  to the `initBugs` match as they're confirmed.
- **Upstream status:** prior PR #10053 (broader, hardcoded, `__EMSCRIPTEN__`-only)
  was closed as presumed-redundant after #10000; but #10000 only covered the
  froxel buffers, not `CONFIG_MAX_INSTANCES`. This patch is the basis for a new,
  narrower PR.
