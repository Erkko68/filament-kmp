# Plan: Close binding-coverage gaps with a real-backend test fixture

Status: **proposal / not yet implemented**
Scope owner: tests for the `filament` / `gltfio` / `filament-utils` wrappers

## TL;DR

We considered cross-platform screenshot/visual-regression testing and **deliberately
rejected it**. This project wraps Filament; it does not reimplement it. Google already
tests Filament's renderer. Our tests exist to prove the **wrapper marshalling layer**
(Kotlin → JNI / FFM / cinterop / WASM → native) is correct.

The real, in-scope problem the existing suite surfaced: a cluster of wrapper calls is
**executed by zero tests** because the `NOOP` backend driver panics when asked to create
GPU resources. The fix is a small, second test fixture that runs *only those* tests on a
real (or software) backend and asserts **binding behaviour** — not pixels.

No screenshots, no golden images, no cross-platform pixel comparison, no GPU CI runners.

## Why not visual regression

- We are **wrappers, not a renderer**. Comparing rendered output tests Filament, not us.
- Bit-exact (or tight-tolerance) cross-platform pixel equality is **not achievable** across
  Metal / software-Vulkan / software-GLES / software-WebGL — different rasterization, AA,
  filtering, sRGB. It would require per-platform golden images and constant maintenance for
  marginal value over what `NOOP` already gives us.
- `NOOP` already executes the real native object construction and the full binding path for
  the large majority of the API surface, deterministically and without a GPU.

## Current state

- Base fixture [`FilamentTestFixture`](../../kotlin/filament/src/commonTest/kotlin/io/github/erkko68/filament/testutils/FilamentTestFixture.kt)
  creates `Engine.create(Engine.Backend.NOOP)`. Same pattern in `GltfioTestFixture` and
  `UtilsTestFixture`.
- `createTestSurface()` returns dummies per platform (`NativeSurface(1L)`, `null`, a detached
  `<canvas>`).
- CI matrix: macOS (macosArm64 + iOS sim), Linux x64/arm64, Windows, JS (headless Chrome /
  Karma), Android (emulator on KVM). See [`.github/workflows/ci.yml`](../../.github/workflows/ci.yml).

## The coverage gap

The relevant bindings **are** present on every platform (verified): headless
`Engine.createSwapChain(width, height, flags)`, `View.renderTarget`, `RenderTarget`,
`Renderer.readPixels(...)` (jvm/native/android/js actuals), `Texture.PixelBufferDescriptor`.
What is missing is **tests that exercise them**, blocked by two root causes.

### Root cause A — `NOOP` panics on GPU-resource creation

The `NOOP` driver throws a driver-specific precondition panic when asked to compile/create
real GPU resources, so the following are commented out and currently untested:

| Test file | Disabled wrapper coverage |
|---|---|
| [`MaterialTest`](../../kotlin/filament/src/commonTest/kotlin/io/github/erkko68/filament/MaterialTest.kt) | `Material.Builder.build(payload)`, getters (`getName`, `getShading`, `getBlendingMode`, …) |
| [`MaterialInstanceTest`](../../kotlin/filament/src/commonTest/kotlin/io/github/erkko68/filament/MaterialInstanceTest.kt) | create instance, set/get parameters |
| [`RenderableManagerTest`](../../kotlin/filament/src/commonTest/kotlin/io/github/erkko68/filament/RenderableManagerTest.kt) | `Builder...material(matInst).build()`, component/instance/AABB getters |
| [`TextureTest`](../../kotlin/filament/src/commonTest/kotlin/io/github/erkko68/filament/TextureTest.kt) | `setImage`, `generateMipmaps` |
| [`RendererTest`](../../kotlin/filament/src/commonTest/kotlin/io/github/erkko68/filament/RendererTest.kt) | `beginFrame` / `endFrame`, `readPixels` |
| [`IBLPrefilterTest`](../../kotlin/filament-utils/src/commonTest/kotlin/io/github/erkko68/filament/utils/IBLPrefilterTest.kt) | `EquirectangularToCubemap.run`, prefilter |
| [`HDRLoaderTest`](../../kotlin/filament-utils/src/commonTest/kotlin/io/github/erkko68/filament/utils/HDRLoaderTest.kt) | `HDRLoader.createTexture` |
| [`KTX1LoaderTest`](../../kotlin/filament-utils/src/commonTest/kotlin/io/github/erkko68/filament/utils/KTX1LoaderTest.kt) | `getSphericalHarmonics` |

### Root cause B — one `@Ignore`d test crashes the runner

[`MaterialProviderTest.testGetMaterialAndCreateInstance`](../../kotlin/gltfio/src/commonTest/kotlin/io/github/erkko68/filament/gltfio/MaterialProviderTest.kt)
is `@Ignore`d ("Crashes the JVM/Android test runner; needs further investigation"). The
sibling tests' comments point at the cause: upstream's binding pre-allocates one slot per
ubershader spec and wraps every slot in `new Material(ptr)` unconditionally, so reading
before population dereferences null — the **same class of bug** the `0.1.1-rc02` changelog
fixed for `getMaterials()`. Likely a real wrapper bug (missing null-guard on `getMaterial` /
`createMaterialInstance`), not a Filament issue.

## Plan

### 1. `RenderingTestFixture`

A second fixture parallel to `FilamentTestFixture`, using `Engine.create(Backend.DEFAULT)`
(real backend) plus a headless `createSwapChain(w, h, flags)` — no window required.

- Probe backend availability in `@BeforeTest` and **skip gracefully** (e.g. `assumeTrue` /
  early-return) where no backend exists, so it never becomes a flaky failure.
- Provable locally on macOS today: the shipping JVM desktop renderer already does
  real-backend offscreen render + `readPixels` (Metal → Skia), so the path is known-good.

### 2. Re-enable the disabled assertions under the new fixture

Move each cluster above onto `RenderingTestFixture` and assert **binding behaviour only**:

- `build()` / create calls return non-null handles and don't crash.
- Getters round-trip the values they were built with.
- `readPixels` fills a buffer of the **correct size**. The strongest output assertion we make
  is a smoke check ("the readback is not 100% the clear colour") — never a golden image.

### 3. Resolve the `@Ignore`

Determine whether `getMaterial` / `createMaterialInstance` need the same null-guard as
`getMaterials()` (a real wrapper fix), or simply a real backend to compile ubershaders, and
fix accordingly. Remove the `@Ignore` once green.

### 4. (Optional follow-on) Deepen shallow assertions

A broader sweep: find tests that only assert "no crash" and add return-value / round-trip
assertions. Tracked separately so it doesn't block the gap-closing above.

## CI reality

No GPU runners, no goldens. Rendering tests run where a backend is already available and skip
elsewhere:

| Runner | Backend | Cost |
|---|---|---|
| macOS desktop + iOS sim | Metal | **free** (Apple-silicon runners have a usable GPU) |
| Android emulator | GLES (SwiftShader) | **free** |
| Linux desktop | software Vulkan (lavapipe via `mesa-vulkan-drivers`) | one `apt-get` line — optional |
| Windows desktop | software (flaky) | skip rendering tests; `NOOP` coverage stays |
| JS / WASM | WebGL2 (headless-Chrome SwiftShader) | material/texture creation should run; `readPixels` is guarded `!= null`, so render/readback may skip on web |

## Open decisions

1. **CI breadth** — run the rendering tests only where a backend is free
   (**macOS + Android + iOS**, recommended), or also pay the Linux-lavapipe setup?
2. **Scope of this pass** — just unblock the existing disabled tests + fix the `@Ignore`
   (bounded), or also fold in the broader "deepen shallow assertions" sweep?

## Suggested first move

Implement `RenderingTestFixture` and unblock **one** cluster (`MaterialTest`) end-to-end,
prove it green locally on macOS, then fan out to the rest + the `@Ignore`d test. Bounded and
verifiable before any CI change.
