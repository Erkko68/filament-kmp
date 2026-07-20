# Compose Integration Strategies

To integrate the Filament 3D engine with Compose Multiplatform, a mechanism is required to display Filament's rendered output within the Compose UI tree. Each platform uses a different strategy depending on what the host UI framework exposes.

## 1. Native Surface Rendering (Android, iOS)

On platforms that expose a native GPU surface to the UI layer, `filament-compose` renders directly into that surface via a `SwapChain`. No pixel copies are involved.

### Android — `SurfaceView`

Filament renders into a `SwapChain` backed by a native Android `Surface` obtained from a `SurfaceView` embedded via `AndroidView`. The surface is created, resized, and destroyed through a `SurfaceHolder.Callback`.

### iOS — `CAMetalLayer`

Filament renders into a `SwapChain` backed by a `CAMetalLayer` that is embedded into a `UIKitView`. The layer's pixel format is set to `BGRA8Unorm` and its `drawableSize` is updated on every layout pass.

### Trade-offs

- **Pros**: Zero CPU overhead; no frame latency; the GPU renders directly to the display surface.
- **Cons**: The native view sits behind the Compose layer; Compose can only be overlaid *on top* of it. Multiple `FilamentView`s cannot be stacked over one another (see [Layering & Stacking](#layering--stacking)).

---

## 2. Offscreen Canvas + Per-View Blit (Web)

A Filament `Engine` is bound to a single WebGL context/canvas — `createSwapChain` takes no canvas argument, and `Renderer.readPixels` is not bound in the JS API — so each `FilamentView` cannot own its own GPU surface, and the JVM-style CPU readback (below) is unavailable.

Instead, all views of one engine share a `WebViewCompositor`:

1. **One offscreen render buffer**: the engine's `HTMLCanvasElement` (`engine.jsCanvas`) stays off-screen and is sized to span every view's window rect.
2. **One frame, many viewports**: each registered view is rendered into its own region of that canvas via `View.viewport` (Compose top-left origin is flipped to Filament's bottom-left).
3. **GPU-side blit**: each view's region is copied onto that view's own 2D `<canvas>` with `ctx.drawImage(engineCanvas, …)` — a canvas-to-canvas copy that reads straight from the WebGL canvas (no CPU readback). The blit runs in the same `requestAnimationFrame` tick as the render, before the browser clears the GL drawing buffer.
4. **Display**: each per-view 2D canvas is injected into the DOM through a `WebElementView` container `<div>` and pushed behind the Compose canvas (`zIndex: -1`). A transparent hole punched in the Compose layer (`BlendMode.Clear`) reveals it. The interop path is required — a plain DOM sibling canvas is *not* revealed by the hole-punch.

### Trade-offs

- **Pros**: Multiple independent views from one shared engine/scene; the copy stays on the GPU.
- **Cons**: One canvas-to-canvas copy per view per frame; views display behind Compose and cannot be stacked over one another (see [Layering & Stacking](#layering--stacking)).

---

## 3. Pixel Readback (JVM / Desktop)

On JVM/Desktop, there is no way to embed a native Filament surface inside a Skia/Compose canvas. Instead, Filament renders to an offscreen headless swap chain and the pixels are read back to the CPU each frame, then handed to Skia.

### How it Works

1. **Readable headless SwapChain**: Filament renders into an offscreen swap chain created with the `READABLE` config flag, sized to the composable (with a 150 ms resize debounce so textures aren't reallocated on every pixel of a window drag).
2. **Zero-copy double-buffered readback**: two slots each own a block of Skia-managed pixel memory (`Data`), and `Renderer.readPixels` writes the GPU→CPU copy *directly into it* — no intermediate `ByteArray`, no per-frame allocation. While one slot backs the image on screen, the other's readback is in flight, keeping the copy pipelined with rendering. Completion may fire on Filament's backend thread; an atomic per-slot state hands the result to the UI thread, which is the only place slots are recycled.
3. **Skia Image**: the completed slot is wrapped in a Skia `Image` via the `Data` overload of `Image.makeRaster`, which shares the pixels instead of copying them.
4. **Compose drawing**: the image is drawn onto a `Spacer` in a `drawBehind` modifier with linear sampling, which lets Compose widgets be overlaid on top of the 3D content. `readPixels` row order is backend-dependent — Metal delivers rows top-down, OpenGL bottom-up — so the draw flips vertically on OpenGL (pinned by the `readPixelsRowOrderMatchesBackendConvention` Tier C test).

### Trade-offs

- **Pros**: Compose widgets can be overlaid freely over the 3D content; the only per-frame cost beyond rendering is the GPU→CPU transfer itself.
- **Cons**: GPU→CPU transfer bandwidth scales with window size; 1–2 frame latency from the asynchronous readback pipeline.

---

## Layering & Stacking

How a `FilamentView` composites against the rest of the UI follows directly from its strategy:

- **JVM / Desktop** — the 3D output is an ordinary Skia `Image` drawn into the Compose scene. It participates in the Compose draw order like any other content, so Compose widgets can sit above *or* below it, and multiple `FilamentView`s can be freely stacked and interleaved with other Compose UI. Full integration.
- **Android, iOS, Web** — the 3D output lives in a native/DOM surface that sits **behind** the Compose layer and is revealed by a transparent hole. Compose UI can be drawn **on top** of a view, but you cannot place opaque Compose content *behind* it, and two `FilamentView`s cannot be stacked over one another (each owns a separate surface on the same plane — whichever is on top hides the other). Side-by-side / non-overlapping views (e.g. a split view) work correctly.

In short: desktop offers full layering because the frame is a texture inside Compose; the other platforms can only render the 3D plane *below* Compose.

## Future Direction

The stacking limitation on Android, iOS, and Web is a consequence of today's surface/context model, not a fundamental one. Newer GPU APIs with first-class shared-context and render-to-texture interop — **Vulkan** and **Metal** on mobile, **WebGPU** on the web — would let Filament render into a texture that the host UI's renderer (Skia/Skiko) can sample directly, the way the JVM/Metal path already shares a GPU texture with Skia. That would bring true in-tree compositing (and arbitrary stacking) to those platforms and retire the hole-punch and per-view blit workarounds. It depends on both Filament and Compose Multiplatform exposing those backends through their public surfaces.

## Summary

| Platform | Strategy | Per-frame copy | Compose overlay | Stack multiple views |
| :--- | :--- | :--- | :--- | :--- |
| **Android** | Native `SurfaceView` + SwapChain | None | On top only | No |
| **iOS** | Native `CAMetalLayer` + SwapChain | None | On top only | No |
| **Web** | Offscreen canvas + per-view `drawImage` blit | GPU canvas→canvas | On top only | No (side-by-side OK) |
| **JVM / Desktop** | Offscreen readable SwapChain + `readPixels` | GPU→CPU every frame | Above or below | Yes |
