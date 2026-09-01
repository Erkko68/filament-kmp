# Platform Notes

Per-platform behavior, backend selection, and known issues. See [Integration Strategies](compose/integration-strategies.md) for how Filament's GPU output reaches the Compose canvas on each target.

## Backend selection

Filament KMP follows Filament's default backend — you typically don't need to override it.

| Platform | Default backend | Override |
| :--- | :--- | :--- |
| Android | OpenGL ES 3.x (Vulkan on newer devices) | `Engine.Backend.VULKAN` |
| iOS / macOS (Kotlin/Native) | Metal | — (Metal only) |
| Desktop macOS (JVM) | Metal | `Engine.Backend.OPENGL` |
| Windows | Vulkan | `Engine.Backend.OPENGL` |
| Linux | Vulkan | `Engine.Backend.OPENGL` |
| Web | WebGL 2.0 | — (WebGL only) |

Override via `rememberFilamentEngine(backend = Engine.Backend.OPENGL)` or `Engine.create(Engine.Backend.OPENGL)`.

> [!TIP]
> If you see GPU crashes or rendering artifacts on a specific machine, **try the OpenGL backend** first — driver-level Vulkan bugs are far more common than OpenGL ones on consumer hardware.

## Android

- Uses the official `com.google.android.filament` Maven library — same code path Google uses internally.
- `SurfaceView` is used for rendering; Compose overlays on top are limited (see [Integration Strategies](compose/integration-strategies.md)). For full overlay support, render into a `TextureView` (not currently exposed by `filament-compose`).
- Minimum `compileSdk`: **34**. Minimum `minSdk`: **24**.
- `SwapChain.setFrameScheduledCallback(null)` stops your callback firing, but the engine keeps a no-op one installed. Upstream's `nSetFrameScheduledCallback` always builds a `JniCallback` and both Java overloads are `@NonNull`, so there is no way to hand it the empty callback Filament unsets on. `isFrameScheduledCallbackSet` answers from what you set through this wrapper, so it reads the same as on every other platform — only `com.google.android.filament.SwapChain.isFrameScheduledCallbackSet()`, read directly, still reports `true`.

### Screen rotation and configuration changes

By default Android destroys and recreates the `Activity` on rotation, which tears down the Compose composition and reloads all Filament assets. This behavior predates Compose — it existed to reload XML layouts and resource qualifiers (`layout-land/`, `values-night/`) automatically.

In a pure Compose app none of that applies: layouts are code, theming reacts to system broadcasts, and Filament's `SurfaceView` already handles the viewport update via `surfaceChanged`. To keep the composition alive across rotation, add `android:configChanges` to your `<activity>` in `AndroidManifest.xml`:

```xml
<activity
    android:name=".MainActivity"
    android:configChanges="orientation|screenSize|screenLayout|smallestScreenSize|uiMode|keyboard|keyboardHidden|navigation"
    ...>
```

This is standard practice for graphics, video, and game apps on Android. The `SurfaceView` still receives `surfaceChanged` on resize, so the viewport and aspect ratio update correctly without any extra code.

## iOS (Kotlin/Native)

- Renders via `CAMetalLayer` embedded in a `UIKitView`.
- Use static frameworks (`isStatic = true`) — keeps the Filament symbols inside your app binary and avoids dynamic-library loader issues.
- Published Apple targets are **`iosArm64`** and **`iosSimulatorArm64`**; there is no `iosX64` and no standalone macOS Kotlin/Native target. Desktop macOS is served by the **JVM** target, which binds the same C wrapper through Project Panama (FFM) rather than `cinterop` — a different code path with the same API.

### iOS Simulator: shadows render black

On the **iOS Simulator**, enabling shadows (directional/spot) produces a fully black scene — the simulator's Metal implementation lacks the depth-texture features Filament's shadow pass needs, so the pass fails to black rather than erroring (basic unshadowed rendering still works). This is a simulator limitation, not a bindings bug: the identical code path renders shadows correctly on macOS (JVM, Metal) where the marshalling was validated. **Test shadows on a real iOS device**, where the full Metal feature set is available.

## JVM / Desktop

### Pixel readback overhead

The Desktop integration renders to an offscreen readable swap chain and copies pixels back to the CPU each frame for Skia compositing. The readback lands directly in Skia-owned memory (no CPU-side re-copies or per-frame allocation), so the cost is the GPU→CPU transfer itself. Expect:

- **1–2 frames of latency** vs. a native swap-chain.
- **Transfer bandwidth** scaling with window size (a 4K window reads back ~33 MB/frame).
- A **150 ms resize debounce** before reallocating textures — drag-resizing feels slightly stuttery, but final layout is clean.

This is unavoidable with Compose Desktop today: there is no public API to embed a native rendering surface inside a Skia canvas.

### Native library loading

The FFM native runtime JAR (`io.github.erkko68.filament-ffm:filament-ffm:...`) bundles the combined `libfilament-c` shared library per platform (`.dll`, `.dylib`, `.so`) as JAR resources and extracts it to a temp directory on first use. No system installation of Filament is needed. Requires a **JDK 22+** runtime.

## Web / WASM

> [!WARNING]
> The web target is **experimental** and not feature-complete.

### Filament.js and WASM bundle

`filament.js` and `filament.wasm` must be placed in your app's `src/jsMain/resources/` directory so webpack serves them alongside your compiled JS. They are not propagated automatically from the library because Kotlin/JS klib resources are not picked up by webpack in downstream Maven consumers.

Download the files matching your `filaVersion` using the download task in the repo:

```bash
./gradlew downloadPrebuilts_web
# outputs prebuilts/web/filament.js, filament.wasm and filament.d.ts
```

Then copy `filament.js` and `filament.wasm` into your `src/jsMain/resources/`. (The
`filament.d.ts` is build-time only — the `:js` module's Kotlin externals are generated
against it by hand; see [`web/README.md`](../web/README.md).)

### Binding coverage

**Android, iOS, and Desktop/JVM expose the full common API.** Web is the only platform with
gaps — almost all because upstream `filament.js` (embind, `jsbindings.cpp`) does not register
the corresponding function. Every gap below is also marked in source with **`@PlatformGap`**
(from the core `filament` module), so it shows up in the IDE and in the generated
[API reference](https://erkko68.github.io/filament-kmp/api/) on the affected declaration itself.

| API | Behavior on web | Workaround |
| :--- | :--- | :--- |
| `filamat.MaterialBuilder` | Throws on construction | Compile materials offline with `matc`, load the `.filamat` via `Material.Builder().payload(...)` |
| `gltfio.UbershaderProvider` (`createMaterialInstance`/`getMaterial`) | Throws | Supply precompiled materials (e.g. the `filament-compose` standard materials) |
| `gltfio.FilamentAsset.getAssetInstances` / `getAssetInstanceCount` | Throws (embind "unbound types") | Track instances returned by `AssetLoader.createInstance` yourself |
| `gltfio.FilamentInstance.getMaterialInstances` | Throws (embind "unbound types") | — |
| `filament-utils.HDRLoader.createTexture` | Throws | Convert HDRs to KTX1 offline with `cmgen` |
| `filament-utils.IBLPrefilterContext` (`EquirectangularToCubemap`/`SpecularFilter`) | Silent no-op — `run` returns the input texture unchanged | Prefilter environments offline with `cmgen` and load the KTX |
| `LightManager.Builder.shadowOptions` | Silent no-op (embind can't marshal the `mat4f` field) | Per-light shadow options stay at Filament defaults |
| `View.AmbientOcclusionOptions.gtao` | Tracked locally only — `Options.h` marks the struct `%codegen_skip_javascript%`, so `filament.js` states outright that the binding does not exist | GTAO can be *selected* via `aoType`; it runs with Filament's default tuning |
| `Renderer.readPixels` (both overloads) | Bound, but the completion callback only fires once the browser has run more frames (~12 in a headless Chrome probe) — a synchronous poll loop inside one task never sees it | Keep rendering and re-check the buffer from `requestAnimationFrame`, not from a busy loop |
| `RenderableManager.Builder.geometryType` | Throws (embind "unbound types") | Omit it — geometry defaults to `DYNAMIC` |
| `RenderableManager.getMorphTargetCount` | Always returns `0` | Not registered with embind (it exists in C++). Count morph targets from the glTF JSON |
| `RenderableManager.setMorphWeights` | Only the first 4 weights apply; a non-zero `offset` is ignored | filament.js binds only the legacy 4-scalar form. 1–3 weights are zero-padded and animate correctly |
| `RenderableManager.setMorphTargetBufferOffsetAt` | Silent no-op | **None.** Callable on a `gltfio`-loaded renderable, but the offset cannot be changed on web — author the glTF so each primitive reads from the offset it needs |
| `Stream` | Throws on construction | External/native video streams have no web equivalent |
| `Engine.isValidStream` | Throws | `Stream` itself has no web equivalent |
| `Engine.isValid` | Returns `true` unconditionally — `filament.js` binds no engine-level validity check, only the `isValidX` family for resources | — |
| `Engine.isPaused` | Tracked locally only; does not pause rendering | Stop your own frame loop instead |
| `Fence.wait` | Non-blocking poll — WebGL cannot block the main thread, so the timeout is clamped to 0 | Poll across frames until `CONDITION_SATISFIED` |
| `Renderer.displayInfo`, `Renderer.frameRateOptions` | Tracked locally only — `setDisplayInfo`/`setFrameRateOptions` are not bound in `filament.js`; frame pacing is managed by the browser | — |
| `SwapChain.setFrameCompletedCallback` | Silent no-op — `filament.js` does not bind it, and binding it would not help: `OpenGLDriver::setFrameCompletedCallback` is an empty function, so it fires on Metal only (the same is true on Android and on Vulkan/GL desktop) | — |
| `SwapChain.setFrameScheduledCallback` | Tracked locally only — `filament.js` binds no frame-scheduled callback, so it never fires; `isFrameScheduledCallbackSet` reports what you set | — |
| `SwapChain.isFrameRateChangeSupported` | Returns false — display frame rate switching is not supported on web; pacing is browser-managed | — |
| `SurfaceOrientation.Builder.tangents` | Silent no-op | Provide normals/uvs and let the builder derive the orientation |

`TextureLoader` works for PNG, JPEG, and KTX1; it returns `null` only on decode failure or empty input. `KTX1Loader` works fully, including `getSphericalHarmonics`. `Manipulator` works fully — `filament-utils` ships a pure-Kotlin implementation on JS; `rememberOrbitCameraController` from `filament-compose` is the recommended ergonomic wrapper.

Suitable for simple scenes with custom materials. Not yet suitable for full glTF pipelines using the default ubershader, or image-based lighting via raw HDR files.

### Bundle size

The Filament.js + WASM blob adds **~2 MB compressed** to your web bundle. Lazy-load the `FilamentView`-containing screen if startup time matters.

## Threading model

All Filament objects are **bound to the thread that created the Engine**. The Compose DSL ensures all calls happen on the UI thread automatically. If you use the raw API:

- Create the `Engine` on the UI thread.
- Call all `engine.*` methods from the UI thread.
- Long-running asset preparation (decoding images, parsing glTF) can happen on a background thread; only the final GPU upload (`ResourceLoader`, `Texture.setImage`) must be on the engine thread.
