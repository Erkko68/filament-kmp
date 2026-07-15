# Real-backend rendering tests

Status: **implemented** — macOS/Metal, iOS sim, Android emulator; skips gracefully
where no backend exists.
Scope: tests for the `filament` / `gltfio` / `filament-utils` wrappers.

> Environment gating (`TestEnv.gpuBackendAvailable`, `@IgnoreJs`) lives in
> [`:kotlin:test-support`](test-support.md); the fixtures below consume it.

## What we built

[`RenderingTestFixture`](../../kotlin/filament/src/commonTest/kotlin/io/github/erkko68/filament/testutils/RenderingTestFixture.kt)
(+ `UtilsRenderingTestFixture`, `GltfioRenderingTestFixture`) create
`Engine.Backend.DEFAULT` and leave `engine == null` when no backend is available,
so tests early-return (`val engine = engine ?: return`) instead of failing. This
closed the coverage gap where the `NOOP` driver panics on GPU-resource creation.

Real-backend clusters added, all green on Metal:

- `MaterialRenderingTest`, `MaterialInstanceRenderingTest`, `TextureRenderingTest`,
  `RenderableManagerRenderingTest`, `RendererRenderingTest` (full frame + `readPixels`
  round-trip), `IBLPrefilterRenderingTest` (`run()` via a synthetic in-memory texture).

Assertions stay **binding-level**: handles are non-null, getters round-trip the
values they were built with, `readPixels` fills a buffer of the correct size —
never a golden image.

## Tier C: semantic frame assertions

One class of wrapper bug is invisible to binding-level assertions: **pixel-level
symptoms with no thrown error** (historically: uninitialized `ShadowOptions` C
struct → shadows silently absent; primitives built without `castShadows`;
`vsm` variants filtered out → soft-shadow panic; web UBO mismatch → black
materials). `FrameSemanticsTest` + `FrameProbe` close that hole with
**property-based pixel checks, not goldens**: a tiny lit scene (floor quad,
hovering caster quad, tilted sun, embedded `test_lit.filamat`) is rendered into
the readable swapchain and read back, and the tests assert *relations between
image regions* — the shadowed patch is darker than the open floor, the lit
centre is not black and shows its base colour, removing the sun changes the
frame, switching PCF→VSM still renders. Region relations hold on every
rasterizer (Metal, lavapipe, SwiftShader), so there are no golden files, no
per-platform baselines, and nothing to regenerate on Filament upgrades. They
run wherever the rendering fixtures already run, and are `@IgnoreJs` because
`readPixels` is a web no-op (below).

## Bugs found and fixed

- **`Material.getParameters()` read garbage / crashed** — the C struct
  `FilaMaterialParameterInfo` didn't match upstream `Material::ParameterInfo`
  (missing `isSampler`/`isSubpass`, wrong field types) yet the bridge
  `reinterpret_cast`s between them. Struct corrected + jvm/native readers updated.
- **Dead/mismatched View-option fields**, found by `ViewOptionsRoundTripTest` (which
  round-trips *every* field of each option struct, set → get): `AmbientOcclusionOptions.scale`
  was exposed in Kotlin but never marshalled (upstream has no such field) → removed
  across all targets; `minConeAngle` renamed to `minHorizonAngleRad` to match the
  native field it was already wired to.

### Not bugs (test-side adjustments)

Several disabled calls panic by design and the tests were adjusted, not the wrapper:
`setImage` needs `UPLOADABLE` usage; `setAxisAlignedBoundingBox` requires non-STATIC
geometry; `maskThreshold`/`doubleSided`/specular-AA setters need the matching material
capability.

## Still open

- `UbershaderProvider.createMaterialInstance` / `Material.createInstance` aborts on the
  `base_unlit_opaque` ubershader (ordinary materials work; `getMaterial`/`getDefaultInstance`
  work). The abort is inside prebuilt filament's `noexcept` `createInstance`; the release
  build strips the message and SIP blocks lldb here. Documented in
  `MaterialProviderRenderingTest`; needs a debug filament build to chase further.
- HDR/KTX loader paths still need real bundled assets.

## Why no visual-regression testing

We considered cross-platform screenshot/golden-image testing and **deliberately
rejected it**:

- We are **wrappers, not a renderer**. Comparing rendered output tests Filament
  (which Google already tests), not our marshalling layer.
- Bit-exact (or tight-tolerance) cross-platform pixel equality is **not achievable**
  across Metal / software-Vulkan / software-GLES / software-WebGL — different
  rasterization, AA, filtering, sRGB. It would mean per-platform goldens and constant
  maintenance for marginal value.
- `NOOP` already executes real native object construction and the full binding path
  for most of the API, deterministically and without a GPU; the real-backend fixtures
  cover the rest.

So: no screenshots, no goldens, no cross-platform pixel comparison, no GPU CI runners.
The Tier C frame assertions above are the deliberate middle path: they catch the
"wrong pixels, no exception" wrapper-bug class while keeping every one of these
objections intact (no baselines, rasterizer-invariant, wrapper-focused).

### Web caveat

`Renderer.readPixels` is not registered in upstream `jsbindings.cpp`, so Tier C
cannot run on web at all — even though two historical pixel-symptom bugs were
web-only. When the engine-side prebuilt is next rebuilt (the UBO/instancing
patch), adding the `readPixels` embind registration to the same build un-parks
frame assertions on web too.

## CI reality

Rendering tests run where a backend is already available and skip elsewhere:

| Runner | Backend | Cost |
|---|---|---|
| macOS desktop (JVM) | Metal | free (Apple-silicon runners have a usable GPU) |
| iOS simulator | Metal | free locally; **skipped under CI** — Gradle sets `FILAMENT_TEST_GPU=false` when `$CI` is present, so real-backend tests early-return. The CI macOS runner is a headless VM with no GPU, so its sim Metal aborts on real draw/compute. All real-backend tests run on a local GPU-backed sim. |
| Android emulator | GLES (SwiftShader) | free |
| Linux desktop | software Vulkan (lavapipe via `mesa-vulkan-drivers`) | one `apt-get` line — optional |
| Windows desktop | software (flaky) | rendering tests skip; `NOOP` coverage stays |
| JS / WASM | WebGL2 (headless-Chrome SwiftShader) | creation runs; `readPixels` guarded `!= null`, so render/readback may skip |
