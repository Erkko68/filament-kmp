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
values they were built with, `readPixels` fills a buffer of the correct size. The
strongest output check is a smoke test ("readback is not 100% the clear colour") —
never a golden image.

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

## CI reality

Rendering tests run where a backend is already available and skip elsewhere:

| Runner | Backend | Cost |
|---|---|---|
| macOS desktop (JVM) | Metal | free (Apple-silicon runners have a usable GPU) |
| iOS simulator | Metal | free; the full render-to-readable-swapchain test (`RendererRenderingTest`) is skipped on Kotlin/Native — the CI macOS runner is a headless VM with no real GPU, so its sim Metal aborts on that path (passes on a local sim backed by the host GPU). Other binding tests run. |
| Android emulator | GLES (SwiftShader) | free |
| Linux desktop | software Vulkan (lavapipe via `mesa-vulkan-drivers`) | one `apt-get` line — optional |
| Windows desktop | software (flaky) | rendering tests skip; `NOOP` coverage stays |
| JS / WASM | WebGL2 (headless-Chrome SwiftShader) | creation runs; `readPixels` guarded `!= null`, so render/readback may skip |
