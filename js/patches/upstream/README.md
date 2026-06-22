# Upstream Filament source patches (web prebuilt)

Patches applied to the **upstream Filament** source tree before building the web
prebuilt (`prebuilts/web/filament.js` + `.wasm`). These are *engine* patches —
they ship **inside the wasm**, not in this repo's Kotlin/JS code. They are
distinct from the `../filament.patch.d.ts` / `../filament.dts-overrides.json`
files, which only patch the *TypeScript externals* Karakum reads.

## How to apply / rebuild

```sh
git clone --filter=blob:none https://github.com/google/filament.git
cd filament && git checkout v1.72.0
git apply /path/to/js/patches/upstream/*.patch
export EMSDK=<emsdk 5.0.4>            # BUILDING.md pins 5.0.4
./build.sh -p wasm release           # builds host tools, then the wasm
# outputs: out/cmake-wasm-release/web/filament-js/{filament.js,filament.wasm}
cp out/cmake-wasm-release/web/filament-js/filament.{js,wasm} \
   /path/to/filament-kmp/prebuilts/web/
# and into samples/webApp/src/jsMain/resources/ (the web app loads that copy directly)
```

Copy only `filament.js` + `filament.wasm`. The `filament.d.ts` from a stock
1.71.6 build is fine to keep — Karakum generates clean externals from it (the old
dangling-`BlendMode` issue that forced keeping the 1.71.5 d.ts is resolved in
1.71.6). See [[project_karakum_js_externals]].

## Patches

### `0001-shaders-clamp-colored-penumbra-divisor.patch`

- **Base:** Filament `v1.72.0`.
- **Symptom:** On ANGLE-D3D11 (Chromium/Firefox on Windows) some faces render
  black in shadow-penumbra regions; macOS (ANGLE-Metal) and Android are fine.
  No GL warning or error — the geometry still draws and is still pickable, the
  shaded result is just black.
- **Cause:** 1.71.6 added the `coloredPenumbra` material feature. The standard
  and cloth shading models compute, under `#if defined(HAS_COLORED_PENUMBRA)`:
  ```glsl
  vec3 penumbraColor = min(vec3(1.0 / PI), Fd / (2.0 * (1.0 - pixel.diffuseColor)));
  Fd = mix(penumbraColor, Fd, pow4(occlusion));   // occlusion < 1 in penumbra
  ```
  `Fd / (2.0 * (1.0 - diffuseColor))` divides by zero for any white/near-white
  diffuse channel → Inf/NaN. `min(finite, NaN)` is undefined in GLSL: desktop GL
  and ANGLE-Metal return the finite operand, but **ANGLE-D3D11 propagates the
  NaN** → black fragment. Only live where the shaded fragment is in penumbra
  (`occlusion < 1`) and only for materials with `coloredPenumbra` enabled or the
  `SUBSURFACE` shading model (which always enables it). The subsurface model is
  unaffected — it uses the bounded scattered `color` as its penumbra term.
- **Fix:** clamp the divisor away from zero
  (`max(2.0 * (1.0 - diffuseColor), vec3(1e-4))`). The division was only ever a
  way to reach the `min(1.0 / PI, …)` energy-conserving cap, so the clamp yields
  the intended capped value without ever producing Inf/NaN. Two files:
  `shaders/src/surface_shading_model_standard.fs`,
  `shaders/src/surface_shading_model_cloth.fs`.
- **Upstream status:** merged into Filament `main` after the `v1.72.0` cut, so
  `v1.72.0` binaries still need this patch. Drop it once a release containing
  the fix (> 1.72.0) is adopted as `filaVersion`.

### `0002-web-set-gen-mipmappable-usage-in-createtexturefromimagefile.patch`

- **Base:** Filament `v1.72.0`.
- **Symptom:** Loading a PNG/JPEG texture on web (`TextureLoader.loadTexture`)
  throws a raw native value (`Uncaught <number>`) that escapes Kotlin/JS
  `catch (Throwable)` and crashes the app. JVM/Android/native are fine.
- **Cause:** The JS helper `Filament._createTextureFromImageFile`
  (`web/filament-js/utilities.js`, reached via `Engine.createTextureFromPng` /
  `createTextureFromJpeg` in `extensions.js`) builds the texture with no
  `usage` unless the caller passes one — so it defaults to
  `DEFAULT = UPLOADABLE | SAMPLEABLE` — then unconditionally calls
  `tex.generateMipmaps(engine)`. `generateMipmaps()` aborts unless the texture
  was created with `GEN_MIPMAPPABLE` (`0x0200`) usage, so the native call throws.
  The native loader (`c/filament-utils/cpp/TextureLoader.cpp`) sets
  `Usage::DEFAULT | Usage::GEN_MIPMAPPABLE` explicitly, which is why only web hits
  it. This is **not** a bindings/d.ts issue — the embind surface is correct; the
  bug is in upstream's hand-written JS helper.
- **Fix:** when no explicit `usage` is given and mipmaps will be generated, build
  with `TextureUsage.DEFAULT | TextureUsage.GEN_MIPMAPPABLE`. One file:
  `web/filament-js/utilities.js`.
- **Carried vs. applied:** the shipped prebuilt is **not** yet rebuilt with this
  patch. Instead the repo works around it Kotlin-side by passing
  `options.usage = UPLOADABLE | SAMPLEABLE | GEN_MIPMAPPABLE` from
  `TextureLoader.js.kt` (the helper honours an explicit `usage`). This patch
  records the engine-level fix for the next prebuilt rebuild and for upstreaming.
- **Upstream status:** not yet submitted. Candidate PR target is
  `web/filament-js/utilities.js` (the `_createTextureFromImageFile` helper) — not
  `jsbindings.cpp`. Once it lands upstream and a release with it is adopted,
  drop both this patch and the `TextureLoader.js.kt` workaround.

## Upstreamed (no longer carried here)

### `CONFIG_MAX_INSTANCES` array-size rewrite — merged into `v1.71.6`

The earlier `0001-webgl-rewrite-CONFIG_MAX_INSTANCES-array-size.patch` (ANGLE-D3D11
binding a uniform block smaller than the shader's `data[CONFIG_MAX_INSTANCES]`
array → `GL_INVALID_OPERATION: uniform buffer too small` → dropped instanced draws
→ black materials) **landed upstream in Filament 1.71.6**, so it is no longer
needed and has been removed. Stock 1.71.6 binaries already contain it.
