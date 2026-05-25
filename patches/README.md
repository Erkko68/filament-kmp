# Local patches to upstream Filament

Patches applied on top of the upstream Filament source tree before producing the
`filament.js` / `filament.wasm` shipped in [`prebuilts/web/`](../prebuilts/web/)
and [`samples/webApp/src/jsMain/resources/`](../samples/webApp/src/jsMain/resources/).

Each patch is also written so its body — particularly the in-source comment — is
directly usable as the description of an upstream PR.

| Patch | Target | Upstream status | What breaks without it |
| :--- | :--- | :--- | :--- |
| [`0001-webgl-rewrite-spec-constant-array-sizes.patch`](0001-webgl-rewrite-spec-constant-array-sizes.patch) | `google/filament` `main` (latest) | Not yet filed | Lit materials render invisible on Chromium-on-Windows and several Android GLES drivers; every `glDrawElementsInstanced` aborts with `It is undefined behaviour to use a uniform buffer that is too small`. |
| [`0001-webgl-rewrite-spec-constant-array-sizes-v1.71.4.patch`](0001-webgl-rewrite-spec-constant-array-sizes-v1.71.4.patch) | `google/filament` `v1.71.4` (currently pinned via `filaVersion`) | Same | Same patch, only the `#include` block layout differs — `v1.71.4` predates the header-reorg commit `c7eff58d`. |

Both files apply the identical logic; pick whichever matches the source tree
you're patching. If `filaVersion` is bumped to a release that includes
`c7eff58d` (the post-v1.71.4 include reorder), drop the `-v1.71.4` variant.

## Known upstream inconsistencies (not patched)

[`UPSTREAM_INCONSISTENCIES.md`](UPSTREAM_INCONSISTENCIES.md) catalogues every
mismatch we've found between Filament's WebGL/JS surface as documented by
`filament.d.ts` and what `jsbindings.cpp` actually exposes. Three buckets:
**d.ts lies** (fixed locally in `filament.js.kt`), **embind getter/setter
asymmetries** (one-line upstream PRs worth filing), and **classes/methods not
exposed in JS at all** (worked around with stubs). Skim before bumping
`filaVersion` so you know what to re-verify.

## Applying the patches

```bash
# Local build path (currently pinned at v1.71.4):
git clone --branch v1.71.4 --single-branch https://github.com/google/filament.git
cd filament
git apply /path/to/filament-kmp/patches/0001-webgl-rewrite-spec-constant-array-sizes-v1.71.4.patch

# Or for filing upstream / against current main:
git clone --depth 1 https://github.com/google/filament.git
cd filament
git apply /path/to/filament-kmp/patches/0001-webgl-rewrite-spec-constant-array-sizes.patch
```

Then build the WebGL output with Filament's standard helper:

```bash
./build/common/get-emscripten.sh
source ./emsdk/emsdk_env.sh
export EMSDK="$PWD/emsdk"
./build.sh -p wasm release
```

Output lands at `out/cmake-wasm-release/web/filament-js/filament.{js,wasm}`. Copy
both files to `prebuilts/web/` and `samples/webApp/src/jsMain/resources/`.

When bumping `filaVersion`, re-run [`scripts/upgrade-diff.sh`](../scripts/upgrade-diff.sh)
on the old/new tags, re-apply each patch on top of the new source, and rebuild.

---

## Patch 0001 — WebGL: rewrite spec-constant array sizes inline

**Summary.** Filament's WebGL backend relies on the GLSL compiler propagating the
runtime-overridden value of a `const int` (initialized from a `#define`'d
`SPIRV_CROSS_CONSTANT_ID_*`) into uniform-block array sizes. ANGLE-on-D3D11
(Chromium-on-Windows) and several Android GLES drivers do not do this — they
compute `GL_UNIFORM_BLOCK_DATA_SIZE` from the matc-baked default — so the
shader-declared block size diverges from the bound range and every
`glDrawElementsInstanced` fails the WebGL2 validator. On strict drivers the
draw is silently skipped, producing invisible geometry. This patch rewrites
the three affected symbolic array sizes in `ShaderCompilerService::compileShaders`
to literals that match what the engine binds.

**File.** [`filament/backend/src/opengl/ShaderCompilerService.cpp`](https://github.com/google/filament/blob/v1.71.4/filament/backend/src/opengl/ShaderCompilerService.cpp)

### Root cause

matc emits, for each spec-constant-sized array:

```glsl
#ifndef SPIRV_CROSS_CONSTANT_ID_<id>
#define SPIRV_CROSS_CONSTANT_ID_<id> <baked-default>
#endif
const int CONFIG_FOO = SPIRV_CROSS_CONSTANT_ID_<id>;
// ...
SomeType x[CONFIG_FOO];  // <-- array size is the symbolic name
```

`ShaderCompilerService::compileShaders` prepends
`#define SPIRV_CROSS_CONSTANT_ID_<id> <runtime>` to the assembled shader, so
`const int CONFIG_FOO` resolves to the runtime value and the engine binds UBO
ranges sized off that value (see `RenderPass.cpp:388-390`, `View.cpp:884-885`).

The GLSL ES 3.00 spec lets uniform-block array sizes be constant integral
expressions, which includes `const int` variables initialized from constant
expressions. Most drivers honor this. ANGLE-on-D3D11 and several Android GLES
drivers do not: their uniform-block layout pass uses the matc-baked default
(the value after the `#ifndef`/`#define`/`#endif` block, before the runtime
override is preprended). The shader and the engine then disagree about how
big the block is.

Concretely, in any LIT material's fragment shader at FL1:

| Symbolic name | matc default | engine binds (typical Chromium WebGL2) | Result on broken drivers |
| :--- | ---: | ---: | :--- |
| `CONFIG_MAX_INSTANCES` | 64 | 8 (`sizeof(PerRenderableUib)` with `__EMSCRIPTEN__`) | **shader expects 16384 B, bound 2048 B → too-small UBO** |
| `CONFIG_FROXEL_BUFFER_HEIGHT` | 1024 | matches default | fine when `MAX_UNIFORM_BLOCK_SIZE == 16 KiB`, but the validator still emits the warning on some configurations |
| `CONFIG_FROXEL_RECORD_BUFFER_HEIGHT` | 1024 | matches default | same |

Either of those mismatches causes Chromium WebGL2's validator to abort every
`glDrawElementsInstanced` call:

```
GL_INVALID_OPERATION: glDrawElementsInstanced:
  It is undefined behaviour to use a uniform buffer that is too small.
```

On ANGLE-Metal (Chromium-on-macOS) the underlying API tolerates the mismatch
and rendering still works, masking the bug. On ANGLE-D3D11 and some Android
drivers the draw is dropped — lit geometry disappears.

### History

This is the same bug class as
[`fe3790cb9` (2022)](https://github.com/google/filament/commit/fe3790cb9)
("WASM: Set CONFIG_MAX_INSTANCES to a small value + hack the GLSL"), which
replaced `[64]` with `[8] ` in the shader text. That hack was removed when
specialization constants became the preferred mechanism. The replacement
mechanism relies on driver behavior the upstream team has now reconfirmed is
not portable; this patch revives the spirit of the original hack in a
spec-constant-aware form.

### Fix

In `ShaderCompilerService::compileShaders`, immediately after the existing
`process_GOOGLE_cpp_style_line_directive` / `process_OVR_multiview2` calls,
walk the shader source and rewrite each symbolic array size to a literal:

```cpp
#ifdef __EMSCRIPTEN__
auto rewriteArraySize = [&](std::string_view symbolic, size_t value) {
    char replacement[32];
    int const n = std::snprintf(replacement, sizeof(replacement), "[%zu", value);
    if (n <= 0 || (size_t)n >= symbolic.size()) {
        return;
    }
    std::memset(replacement + n, ' ', symbolic.size() - n - 1);
    replacement[symbolic.size() - 1] = ']';
    std::string_view view{ shader_src, shader_len };
    for (size_t p = view.find(symbolic); p != std::string_view::npos;
            p = view.find(symbolic, p + symbolic.size())) {
        std::memcpy(shader_src + p, replacement, symbolic.size());
    }
};
rewriteArraySize("[CONFIG_MAX_INSTANCES]", 8);
rewriteArraySize("[CONFIG_FROXEL_BUFFER_HEIGHT]", 1024);
rewriteArraySize("[CONFIG_FROXEL_RECORD_BUFFER_HEIGHT]", 1024);
#endif
```

Properties:
- **Same-size in-place substitution**. The replacement strings are padded with
  spaces so the byte count is preserved. This lets us edit `shader_src`
  directly and keeps the `splitShaderSource()` string_views that come later
  valid.
- **`__EMSCRIPTEN__`-gated**. Native OpenGL drivers (macOS GL, desktop Linux/
  Windows native GL, Android NDK GL) do propagate the `const int` correctly,
  so the rewrite only fires when we're producing the WebGL build.
- **Hardcoded values, not header includes**. The backend cannot include
  `EngineEnums.h` for layering reasons (same precedent as the local
  `constexpr size_t CONFIG_STEREO_EYE_COUNT = 8` a few lines above in the
  same function). The hardcoded values mirror what the engine binds when
  built with `__EMSCRIPTEN__`.

### Verification

Before the patch, rendering any LIT material on Chromium-on-Windows produces
hundreds of `GL_INVALID_OPERATION: glDrawElementsInstanced: ... uniform buffer
that is too small` errors per frame and the geometry is invisible. After the
patch the warnings stop and rendering matches Chromium-on-macOS.

A diagnostic build was used during bring-up that logged the number of
substitutions per shader stage. For the unmodified `lit_color.mat` shipped
with the filament-kmp sample:

```
[filament-kmp WebGL patch] stage=1 inv=26
    MAX_INSTANCES=1  FROXEL_BUF=1  FROXEL_REC_BUF=1   <-- LitColor fragment
```

All three rewrites are needed to silence the validator and render correctly.

### Things deliberately *not* done

- **No new TargetApi.** Adding `TargetApi::WEBGL` and making matc bake the
  literal at compile time would be the cleanest fix, but it would require
  shipping per-target `.filamat` blobs and is intrusive. The runtime
  rewrite is one localized change.
- **No driver detection.** The substitution runs unconditionally on
  emscripten because the cost is one `string_view::find` per shader stage
  and the rewrite is safe on conforming drivers (they would have computed
  the same array size anyway).
- **No coverage of every spec-constant array.** Only the three that appear
  as array sizes in shipped materials at FL1 are rewritten. If new
  spec-constant-sized arrays are introduced upstream, they would need to
  be added here.
