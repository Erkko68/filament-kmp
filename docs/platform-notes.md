# Platform Notes

This page collects per-platform behavior, backend selection, and known issues. It complements [Integration Strategies](compose/integration-strategies.md), which covers *how* Filament's output reaches the Compose canvas; this page covers *what to watch out for* on each target.

## Backend selection

Filament KMP follows Filament's default backend selection — you typically don't need to override it.

| Platform | Default backend | Override |
| :--- | :--- | :--- |
| Android | OpenGL ES 3.x (Vulkan on newer devices) | `Engine.Backend.VULKAN` |
| iOS / macOS | Metal | — (Metal only) |
| Windows | Vulkan | `Engine.Backend.OPENGL` |
| Linux | Vulkan | `Engine.Backend.OPENGL` |
| Desktop macOS (JVM) | Metal | `Engine.Backend.OPENGL` |
| Web | WebGL 2.0 | — (WebGL only) |

Override via `rememberFilamentEngine(backend = Engine.Backend.OPENGL)` or `Engine.create(Engine.Backend.OPENGL)`.

> [!TIP]
> If you see GPU crashes or rendering artifacts on a specific machine, **try the OpenGL backend** first — driver-level Vulkan bugs are far more common than OpenGL ones on consumer hardware.

## Android

- Uses the official `com.google.android.filament` Maven library — same code path Google uses internally.
- `SurfaceView` is used for rendering; Compose overlays on top are limited (see [Integration Strategies](compose/integration-strategies.md)). For full overlay support, render into a `TextureView` (not currently exposed by `filament-compose`).
- Minimum `compileSdk`: **34**. Minimum `minSdk`: **24**.

## iOS / macOS

- Renders via `CAMetalLayer` embedded in a `UIKitView`.
- Static frameworks recommended (`isStatic = true`) — keeps the native Filament symbols in your app binary rather than producing dynamic-framework loader headaches.
- macOS via JVM (Compose Desktop) and macOS via Kotlin/Native are **different code paths**. The Kotlin/Native path uses the C-wrapper; the JVM path uses the JNI bindings.

## JVM / Desktop

### Pixel readback overhead

The Desktop integration renders to an offscreen `RenderTarget` and copies pixels back to the CPU each frame for Skia compositing. Expect:

- **1–2 frames of latency** vs. a native swap-chain.
- **CPU overhead** scaling with window size (a 4K window copies ~33 MB/frame).
- A **150 ms resize debounce** before reallocating textures — drag-resizing feels slightly stuttery, but final layout is clean.

This is unavoidable with Compose Desktop today: there is no public API to embed a native rendering surface inside a Skia canvas.

### Windows JVM shutdown crash

> [!WARNING]
> On Windows with the Vulkan backend, the JVM occasionally crashes inside `gltfio-jni.dll` during process teardown — *after* your app has already closed cleanly. The crash sets a non-zero exit code, which Gradle reports as `BUILD FAILED`.

Workaround: call `exitProcess(0)` after your Compose application returns so the JVM skips the broken native static destructors:

```kotlin
fun main() {
    singleWindowApplication(title = "My App") { App() }
    kotlin.system.exitProcess(0)
}
```

This is the same workaround Filament's own desktop samples use on Windows. It only affects the exit code — the app and all `DisposableEffect` cleanup run normally.

### Native library loading

The JNI runtime JAR (`io.github.erkko68.filament-jni:filament:...`) bundles platform-specific shared libraries (`.dll`, `.dylib`, `.so`) as JAR resources and extracts them to a temp directory on first use. No system installation of Filament is needed.

## Web / WASM

> [!WARNING]
> The web target is **experimental** and not feature-complete.

Current limitations:

- `MaterialProvider.createMaterialInstance()` and `getMaterial()` return `null` — the default ubershader path doesn't work. You must supply your own materials.
- `KTX1Loader`, `HDRLoader`, `TextureLoader` factories return `null` on JS. Use simpler image formats and the standard `Texture.Builder`.
- `Manipulator` builder methods return `null`. Use `filament-compose`'s `rememberOrbitCameraState` (Kotlin-side implementation) instead.

Suitable for: simple scenes with custom materials, no glTF asset loading.
Not yet suitable for: full glTF pipelines, image-based lighting via KTX.

Track web parity in the issue tracker before depending on it for production.

### Bundle size

The Filament.js + WASM blob adds **~2 MB compressed** to your web bundle. Lazy-load the `FilamentView`-containing screen if startup time matters.

## Threading model

All Filament objects are **bound to the thread that created the Engine**. The Compose DSL ensures all calls happen on the UI thread for you. If you drop down to the raw API:

- Create the `Engine` on the UI thread.
- Call `engine.*` methods only from the UI thread.
- Long-running asset preparation (decoding image files, parsing glTF) can happen on a background thread; only the final GPU upload (`ResourceLoader`, `Texture.setImage`) must be on the engine thread.

## Memory and lifecycle

The Compose DSL manages Filament resource lifetimes through `DisposableEffect`:

- `rememberFilamentEngine` → destroys the engine when leaving composition.
- `FilamentView` → destroys its `Renderer`, `Scene`, `View`, `Camera`.
- `rememberGltfAsset` → destroys the asset.
- `GltfInstance` → removes its entities from the scene.

If you create raw objects through `FilamentEffect`, you are responsible for destroying them. The standard pattern:

```kotlin
FilamentEffect { engine, _, _ ->
    val mat = Material.Builder().payload(bytes, bytes.size).build(engine)
    onDispose { engine.destroyMaterial(mat) }
}
```

Forgetting to destroy Filament objects leaks GPU memory until the `Engine` itself is destroyed.
