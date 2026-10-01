# Platform Notes

Per-platform behavior, backend selection, and known issues. See [Integration Strategies](../compose/integration-strategies.md) for how Filament's GPU output reaches the Compose canvas on each target.

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

- Binds Filament through JNI over the same `c/` wrapper as JVM, iOS and web (`:jni` bindings + `:android` runtime, published as `filament-jni` + `filament-jni-android`), not the upstream `filament-android` AAR.
- `SurfaceView` is used for rendering; Compose overlays on top are limited (see [Integration Strategies](../compose/integration-strategies.md)). For full overlay support, render into a `TextureView` (not currently exposed by `filament-compose`).
- Minimum `compileSdk`: **37**. Minimum `minSdk`: **24**.

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
- Published Apple targets are **`iosArm64`** and **`iosSimulatorArm64`**; there is no `iosX64` and no standalone macOS Kotlin/Native target. Desktop macOS is served by the **JVM** target, which reaches the same C wrapper through JNI rather than `@SymbolName` — a different code path with the same API.

### iOS Simulator: shadows render black

On the **iOS Simulator**, enabling shadows (directional/spot) produces a fully black scene — the simulator's Metal implementation lacks the depth-texture features Filament's shadow pass needs, so the pass fails to black rather than erroring (basic unshadowed rendering still works). This is a simulator limitation, not a bindings bug: the identical code path renders shadows correctly on macOS (JVM, Metal) where the marshalling was validated. **Test shadows on a real iOS device**, where the full Metal feature set is available.

## JVM / Desktop

### GPU-to-GPU frame sharing (experimental)

By default each frame is copied back through the CPU (below). Opt in to render Filament's frames into textures shared with Compose's own GPU context instead: Metal on macOS, Direct3D 12 on Windows, OpenGL on Linux.

```kotlin
@OptIn(ExperimentalGpuToGpuFrameSharing::class)
fun main() {
    FilamentComposeDesktop.isGpuToGpuFrameSharingEnabled = true
    application { /* … */ }
}
```

Set it before the first `rememberFilamentEngine()`. Only engines that `rememberFilamentEngine()` creates afterwards, inside the window that shows them, share frames, and their backend must match Compose's API: `Engine.Backend.DEFAULT` always does, otherwise `METAL` on macOS, `VULKAN` on Windows, `OPENGL` on Linux. It relies on skiko internals, so it's experimental:

- If a setup isn't covered (e.g. Compose fell back to software rendering, or the backend doesn't match Compose's API), it logs one line with the reason and uses CPU readback.
- Views find Compose's GPU context through their window, so use one Compose surface per window (a `ComposeWindow`, or a single `ComposePanel`); with several in one window a view may pick another surface's context.
- If it **fails**, it prints a report (versions, GPU, stack trace) to the console, switches to CPU readback for the rest of the session and asks you to [open an issue](https://github.com/Erkko68/filament-kmp/issues/new) with the report. A crash inside the GPU driver can't be caught this way.

### Pixel readback overhead

By default the Desktop integration renders to an offscreen readable swap chain and copies pixels back to the CPU each frame for Skia compositing. The readback lands directly in Skia-owned memory that the frame's image then wraps (no CPU-side re-copies), so the cost is the GPU→CPU transfer plus one buffer allocation per frame. Expect:

- **1–2 frames of latency** vs. a native swap-chain.
- **Transfer bandwidth** scaling with window size (a 4K window reads back ~33 MB/frame).
- A **150 ms resize debounce** before reallocating textures — drag-resizing feels slightly stuttery, but final layout is clean.

Compose Desktop has no public API to embed a native rendering surface inside a Skia canvas; [GPU-to-GPU frame sharing](#gpu-to-gpu-frame-sharing-experimental) avoids the copy through skiko internals, at the cost of being experimental.

### GPU selection

Shared textures can't cross GPUs, so with [GPU-to-GPU frame sharing](#gpu-to-gpu-frame-sharing-experimental) on, Filament must render on the GPU Compose picked.

**macOS**: Filament's Metal engine always uses the system default GPU. If Compose picks another one (e.g. `skiko.gpu.priority=integrated` on a dual-GPU Mac), the views log one line and use CPU readback.

**Windows**: Compose Desktop draws with Direct3D 12. With frame sharing on, `filament-compose` runs Filament's Vulkan backend on the **same GPU**. Compose picks that GPU when the first window opens: by default the system's default adapter, which on hybrid laptops is usually the **integrated** one. To render on the discrete GPU, ask for it before opening any window:

```kotlin
fun main() {
    System.setProperty("skiko.gpu.priority", "discrete") // or "integrated" / "auto"
    application { /* … */ }
}
```

or pass `-Dskiko.gpu.priority=discrete` (e.g. `jvmArgs += "-Dskiko.gpu.priority=discrete"` in `compose.desktop.application`). This moves your whole Compose app to that GPU, not only the Filament views. Only `Engine.Backend.DEFAULT` / `VULKAN` engines share frames on Windows; others use CPU readback.

### Native library loading

Each platform's `libfilament-c` (`.dll`, `.dylib`, `.so`) ships in its `filament-jni-runtime-<os>-<arch>` jar. `Filament.init()` extracts it once into a content-hash-keyed cache dir (`~/.filament-kmp/`) and loads it; no system installation of Filament is needed. Runs on any **JDK 17+**. See [`desktop/README.md`](../../desktop/README.md) for the loader's knobs.

## Web / WASM

### filament-kmp.js and WASM bundle

The web targets call the same C API as JVM and iOS (`c/`), compiled with Emscripten into
`filament-kmp.wasm` (filament + gltfio + filament-utils). The `:web` module holds the generated
Kotlin externals; it is a transitive dependency, nothing to add by hand.

Webpack doesn't pick up klib resources in downstream apps, so the runtime files ship as assets on
each [GitHub release](https://github.com/Erkko68/filament-kmp/releases). Download the ones matching
your filament-kmp version into your app's `src/webMain/resources/`:

| File | Needed for |
| :--- | :--- |
| `filament-kmp.js` + `filament-kmp.wasm` | Everything (always) |
| `filamat-kmp.js` + `filamat-kmp.wasm` | Only runtime material compilation (`MaterialBuilder`) |

Load the `.js` files with `<script>` tags before your app bundle. Each `.js` fetches its `.wasm`
from the same directory. Always use the files from the same release as the library: the externals
and the wasm exports must match.

### API coverage

Web exposes the same common API as the other platforms, by construction: every `Fila*` function is
exported from the wasm and has a generated external. What remains are limits of WebGL and
single-threaded wasm, marked in source with **`@PlatformGap`** so they show up in the IDE and the
[API reference](https://erkko68.github.io/filament-kmp/api/):

| API | Behavior on web | Workaround |
| :--- | :--- | :--- |
| `Renderer.readPixels` (both overloads) | Asynchronous: the pixels land when the browser has run more frames, then the callback fires. A synchronous poll loop inside one task never sees it | Keep rendering and check the buffer from the callback or `requestAnimationFrame` |
| `Stream` | `setDimensions` throws: `FStream` waits on a fence internally, which single-threaded wasm rejects | External video streams have no WebGL source anyway |
| `Engine.isPaused`, `Engine.Builder.paused` | Tracked locally only; pausing needs threads | Stop your own frame loop instead |
| `Fence.wait`, `Engine.flushAndWait` | The timeout is clamped to 0 (a non-blocking poll); a `FLUSH` has already executed every command | Poll across frames until `CONDITION_SATISFIED` |
| `SwapChain.setFrameCompletedCallback` | Never fires: the OpenGL/WebGL backend implements it as a no-op (same on Android and GL desktop) | — |
| `SwapChain.isFrameRateChangeSupported` | Returns false; pacing is managed by the browser | — |

Engine-level WebGL issues (for example the spot-light shadow context loss on some GPUs) are
upstream bugs in Filament's WebGL backend, not binding gaps.

### Runtime material compilation (filamat)

`MaterialBuilder` works on web through a separate, optional `filamat-kmp.wasm` (~6.4 MB). Serve
`filamat-kmp.js` + `.wasm` next to `filament-kmp.js`, then call `MaterialBuilder.initJs { MaterialBuilder.init() }`
before building materials.

Limits:
- The compiler runs on a fixed **4 MB wasm stack**. A very large or deeply nested shader can overflow
  it: the build fails with `memory access out of bounds` and `filamat-kmp.wasm` stays unusable until
  the page reloads.
- Compilation is synchronous and blocks the main thread (there are no worker threads).

For big materials, or anything that must load fast, compile offline with `matc` and load the
`.filamat` via `Material.Builder().payload(...)`.

### Bundle size

`filament-kmp.wasm` is ~2.8 MB (~1.1 MB gzipped), plus ~230 KB of JS glue. `filamat-kmp.wasm` adds
~6.4 MB (~1.9 MB gzipped) only if you serve it. Lazy-load the `FilamentView` screen, or call
`MaterialBuilder.initJs` only when you need it, if startup time matters.

## Error handling

Filament reports errors two ways, and the bindings keep both:

- **Recoverable failures return null.** `Engine.create()` / `Engine.Builder.build()` when no backend
  can start, `Material.Builder.build()` for a payload that isn't a `.filamat` (checked before it
  reaches Filament), `Engine.getFeatureFlag(name)` for an unknown flag, gltfio's `createAsset` for
  bytes it can't parse. Check them.
- **Precondition failures abort the process.** Filament panics on misuse (a buffer smaller than
  declared, a parameter name the material doesn't have, destroying an `Engine` that still owns
  objects, a `.filamat` compiled for another `MATERIAL_VERSION`). It's a bug to fix, not an error to
  handle, so on Android, desktop and iOS it can't be caught: Filament's message and call stack go to
  logcat / stderr / the console, then the app terminates. On Android the message is also the crash
  report's abort message. On web the call throws a JS error carrying the message (`JsException` on
  wasmJs), which `catch (e: Throwable)` can see; treat the module as broken afterwards. Most come
  from destroy order: destroy what you created, in reverse order, before the engine.

## Threading model

All Filament objects are **bound to the thread that created the Engine**. The Compose DSL ensures all calls happen on the UI thread automatically. If you use the raw API:

- Create the `Engine` on the UI thread.
- Call all `engine.*` methods from the UI thread.
- Long-running asset preparation (decoding images, parsing glTF) can happen on a background thread; only the final GPU upload (`ResourceLoader`, `Texture.setImage`) must be on the engine thread.
