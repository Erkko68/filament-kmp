# Testing strategy

Tests catch the bugs a consumer would hit, are written the way a consumer uses the library, and **run on
every CI target**: JVM (macOS, Linux, Windows), JS, Wasm, the iOS simulator and the Android emulator. Most
of those have no GPU or an emulated one, so a test that needs a real GPU cannot be the default.

> Environment gating (`TestEnv`, `assumeRealBackend()`, `@IgnoreJs`) and the shared fixtures live in
> [`:kotlin:test-support`](test-support.md). The compose layer has its own page:
> [`filament-compose` tests](compose-tests.md).

## NOOP runs everything but the rasterizer

On a `NOOP` engine (Filament 1.78), on **all five targets**:

| Works on NOOP | Result |
|---|---|
| `Material.Builder`, `createInstance`, `setParameter` / `getParameter` | values round-trip |
| `VertexBuffer` / `IndexBuffer` / `RenderableManager.Builder`, lights, scene membership | ✅ |
| `Texture` + `setImage`, `Skybox`, `IndirectLight` with a cubemap, glTF | ✅ |
| `beginFrame` / `render` / `endFrame` on a headless swapchain | ✅ |
| `readPixels` callback | fires (buffer is all zeros) |
| `View.pick` callback | fires after a few frames (with no renderable) |
| Destroying everything, then `Engine.destroy` | clean |

NOOP runs Filament's whole CPU side (scene graph, culling, material system, frame graph, command stream)
and only skips rasterization. What needs a real backend:

- **pixel values**, and what a pick hits
- **async calls** (`runCommandAsync`, `set*Async`): NOOP aborts with "Engine not configured for async operations"
- **GPU-computed results** such as `IBLPrefilterContext` filter output

## Two tiers

### Tier 1: state tests (NOOP, `commonTest`, every target)

The default for every test. A test is a **consumer scenario**: build what a user builds (the
[samples](../../../samples/) are the reference), drive it as they would (state changes, recomposition,
frames, disposal), and assert through Filament's own public queries. Those queries are exactly what a
wrapper bug corrupts:

- scene membership; the renderable's material instance, AABB, shadow and layer flags
- `MaterialInstance.getParameter` after a Compose state change
- world transforms after parenting, animation or a moved node
- `LightManager`, `Camera` and `View` values after a state change
- frames rendered by the render loop; pick callbacks delivered
- after disposal: scene empty, entities dead, nothing drawn with a destroyed object, no leaks

Rendering a realistic scene for a few frames on NOOP is a meaningful test in itself: it drives culling,
shadow setup, variant selection and parameter packing, and catches native panics.

### Tier 2: pixel tests (real backend)

A small set for the bug classes only pixels show. They live in the `pixelTest` source set, compiled for
jvm and web only (the targets with a rasterizer in CI), and extend
[`RealBackendFixture`](../../../kotlin/test-support/src/commonMain/kotlin/io/github/erkko68/filament/testsupport/RealBackendFixture.kt):
`assumeRealBackend()` reports them as skipped on a host without a rasterizer, and where there is one,
failing to get a real engine fails the test.

| Module | Tier 2 tests |
|---|---|
| `filament` | `FrameSemanticsTest` (below, including `readPixels` row order), `ReadbackTest` (both `readPixels` overloads), `AsyncCallTest` |
| `filamat` | `CompiledMaterialPixelTest`: a runtime-compiled material's parameter is the colour on screen |
| `filament-utils` | `IBLPrefilterTest`; `LoadedTexturePixelTest`: a `TextureLoader` PNG sampled by a material compiled with filamat (a pixelTest-only dependency) |
| `filament-compose` | `FilamentViewRealBackendTest` (`jvmTest`): the view shows its scene (`captureToImage`) and `capture()` reads it back in screen order, what a pick hits and where, and a click or a jittery tap reaching `onTap`; `AmbientPresetsRealBackendTest`, `ExposurePresetsRealBackendTest`: the lighting presets look as documented |

Materials compiled at runtime for a real backend need `targetApi(ALL)`: filamat's default is OpenGL only,
which Metal rejects. Readback tests are `@IgnoreJs`: see the web caveat below.

## Rules for every test

- **No silent skips.** No `engine ?: return`, no `if (bytes.isEmpty()) return`. A test runs, is reported as skipped, or isn't compiled for that target.
- **Assert what can be wrong.** No `assertNotNull` on a non-null type, no `size == 3` on an array the test allocated, no `>= 0` on a count.
- **No mirror tests.** Don't check a hand-typed ordinal against the enum, or set a Kotlin field and read the same field back. Round-trip **through C** (set on the Filament object, read back from it) or don't test it.
- **Derive expectations from upstream**, from Filament's source at the pinned tag, never from observed output.
- **No dead branches.** A `for` / `if` over fixture data must be known to execute (Duck has no animations; Fox does).
- **No "call every method" tests.** A call with no assertion only proves the symbol links, and any scenario test that uses it proves that too.
- **Collect, tear down, then assert** when materials are alive: a failing assertion before teardown makes the fixture abort on live instances and hides the real failure.
- **Compare handles, not wrappers.** Getters that read from C (`MaterialInstance.material`, `getMaterialInstanceAt`, texture getters) return a fresh wrapper each call: compare `nativeObject`, captured before destroying (destroy clears it).
- **No `@BeforeTest` in a fixture subclass**: web runs it before the fixture's. Override a hook (`createEngine()`) or use a helper.
- **Arrays must match what the native side reads**: a short array is an out-of-bounds native read that crashes intermittently.
- **A pixel test must fail when its subject breaks.** Check it once by breaking the input (another tint, an unbound texture).

## Tier C: semantic frame assertions

One class of wrapper bug is invisible to binding-level assertions: **pixel-level symptoms with no thrown
error** (historically: uninitialized `ShadowOptions` C struct → shadows silently absent; primitives built
without `castShadows`; `vsm` variants filtered out → soft-shadow panic; web UBO mismatch → black
materials). `FrameSemanticsTest` + `FrameProbe` close that hole with **property-based pixel checks, not
goldens**: a tiny lit scene (floor quad, hovering caster quad, tilted sun, embedded `test_lit.filamat`) is
rendered into the readable swapchain and read back, and the tests assert *relations between image
regions*: the shadowed patch is darker than the open floor, the lit centre is not black and shows its base
colour, removing the sun changes the frame, switching PCF→VSM still renders. Region relations hold on
every rasterizer (Metal, lavapipe, SwiftShader), so there are no golden files, no per-platform baselines,
and nothing to regenerate on Filament upgrades. The other pixel tests reuse `FrameProbe` and
`regionStats` the same way.

## Bugs found and fixed

- **`Material.getParameters()` read garbage / crashed**: the C struct `FilaMaterialParameterInfo` didn't
  match upstream `Material::ParameterInfo` (missing `isSampler`/`isSubpass`, wrong field types) yet the
  bridge `reinterpret_cast`s between them. Struct corrected + jvm/native readers updated.
- **Dead/mismatched View-option fields**, found by `ViewOptionsRoundTripTest` (which round-trips *every*
  field of each option struct, set → get): `AmbientOcclusionOptions.scale` was exposed in Kotlin but never
  marshalled (upstream has no such field) → removed across all targets; `minConeAngle` renamed to
  `minHorizonAngleRad` to match the native field it was already wired to.
- **Short arrays read out of bounds**: every array now reaches C with the element count C touches, and a
  shorter one throws.
- **`MaterialProvider.materials` aborted** on the ubershader cache's not-yet-compiled slots.

### Not bugs (test-side adjustments)

Several disabled calls panic by design and the tests were adjusted, not the wrapper: `setImage` needs
`UPLOADABLE` usage; `setAxisAlignedBoundingBox` requires non-STATIC geometry;
`maskThreshold`/`doubleSided`/specular-AA setters need the matching material capability.

## Still open

- `MaterialProvider.createMaterialInstance` / `Material.createInstance` aborts on the `base_unlit_opaque`
  ubershader (ordinary materials work; `getMaterial`/`getDefaultInstance` work). The abort is inside
  prebuilt filament's `noexcept` `createInstance`; the release build strips the message and SIP blocks
  lldb here. Not exercised by any test; needs a debug filament build to chase further.

## Why no visual-regression testing

We considered cross-platform screenshot/golden-image testing and **deliberately rejected it**:

- We are **wrappers, not a renderer**. Comparing rendered output tests Filament (which Google already
  tests), not our marshalling layer.
- Bit-exact (or tight-tolerance) cross-platform pixel equality is **not achievable** across Metal /
  software-Vulkan / software-GLES / software-WebGL: different rasterization, AA, filtering, sRGB. It would
  mean per-platform goldens and constant maintenance for marginal value.
- `NOOP` already executes real native object construction and the full binding path for most of the API,
  deterministically and without a GPU; the pixel tests cover the rest.

So: no screenshots, no goldens, no cross-platform pixel comparison, no GPU CI runners. The Tier C frame
assertions above are the deliberate middle path.

### Web caveat

`Renderer.readPixels` is bound on web, but its callback fires only after the browser runs more frames,
and a synchronous test never yields to it. So readback tests skip on web (`@IgnoreJs`), even though two
historical pixel-symptom bugs were web-only. An async variant of `FrameProbe` that awaits
`requestAnimationFrame` would un-park them.

## CI reality

Real-backend tests run where a backend is already available and skip elsewhere:

| Runner | Backend | Cost |
|---|---|---|
| macOS desktop (JVM) | Metal | free (Apple-silicon runners have a usable GPU) |
| iOS simulator | Metal | free locally; **skipped under CI**: Gradle sets `FILAMENT_TEST_GPU=false` when `$CI` is present, as the CI runner's simulator has no GPU. `pixelTest` doesn't compile for iOS. |
| Android emulator | GLES (SwiftShader) | free; `pixelTest` doesn't compile for Android |
| Linux desktop | software Vulkan (lavapipe via `mesa-vulkan-drivers`) | one `apt-get` line, optional |
| Windows desktop | software (flaky) | real-backend tests skip; `NOOP` coverage stays |
| JS / WASM | WebGL2 (headless-Chrome SwiftShader) | engine and frames run; readback tests are `@IgnoreJs` |
