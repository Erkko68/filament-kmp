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

A Filament `Engine` is bound to a single WebGL context/canvas, so each `FilamentView` cannot own its own GPU surface. The JVM-style CPU readback (below) would stall on web, where `Renderer.readPixels` only completes after the browser runs more frames.

Instead, all views of one engine share a `WebViewCompositor`:

1. **One offscreen render buffer**: the engine's `HTMLCanvasElement` (`engine.canvas`) stays off-screen and is sized to span every view's window rect.
2. **One frame, many viewports**: each registered view is rendered into its own region of that canvas via `View.viewport` (Compose top-left origin is flipped to Filament's bottom-left).
3. **GPU-side blit**: each view's region is copied onto that view's own 2D `<canvas>` with `ctx.drawImage(engineCanvas, …)` — a canvas-to-canvas copy that reads straight from the WebGL canvas (no CPU readback). The blit runs in the same `requestAnimationFrame` tick as the render, before the browser clears the GL drawing buffer.
4. **Display**: each per-view 2D canvas is injected into the DOM through a `WebElementView` container `<div>` and pushed behind the Compose canvas (`zIndex: -1`). A transparent hole punched in the Compose layer (`BlendMode.Clear`) reveals it. The interop path is required — a plain DOM sibling canvas is *not* revealed by the hole-punch.

### Trade-offs

- **Pros**: Multiple independent views from one shared engine/scene; the copy stays on the GPU.
- **Cons**: One canvas-to-canvas copy per view per frame; views display behind Compose and cannot be stacked over one another (see [Layering & Stacking](#layering--stacking)).

---

## 3. Offscreen Texture into Skia (JVM / Desktop)

Compose Desktop has no public API to embed a native surface inside its Skia canvas, so Filament renders offscreen, sized to the composable (with a 150 ms resize debounce so textures aren't reallocated on every pixel of a window drag), and each finished frame becomes a Skia `Image` drawn onto a `Spacer` in a `drawBehind` modifier with linear sampling. How the frame reaches Skia depends on whether GPU-to-GPU frame sharing is on.

### Default: CPU readback

1. **Readable headless SwapChain**: Filament renders into a swap chain created with the `READABLE` config flag.
2. **Readback into Skia memory**: `Renderer.readPixels` writes each frame straight into a fresh block of Skia-managed memory (`Data`), with no intermediate `ByteArray`. Up to two readbacks are in flight, keeping the copy pipelined with rendering; completion may fire on Filament's backend thread and hands the newest frame to the UI thread atomically.
3. **Skia Image**: the `Data` overload of `Image.makeRaster` wraps the pixels without copying them. The image keeps its memory alive, so a frame outlives the swap chain that produced it (e.g. across a resize).
4. **Row order**: `readPixels` row order is backend-dependent (Metal top-down, OpenGL bottom-up), so the draw flips vertically on OpenGL (pinned by the `readPixelsRowOrderMatchesBackendConvention` Tier C test).

### Experimental: GPU-to-GPU frame sharing

With `FilamentComposeDesktop.isGpuToGpuFrameSharingEnabled` (see [Platform Notes](../platform-notes.md#gpu-to-gpu-frame-sharing-experimental)), Filament renders into textures on Compose's own GPU context, found through skiko internals, and Skia wraps each finished texture on its `DirectContext` and snapshots it:

| OS | Compose draws with | Filament side |
| :--- | :--- | :--- |
| **macOS** | Metal | Metal engine renders into `MTLTexture`s on skiko's device |
| **Windows** | Direct3D 12 | Vulkan engine on skiko's GPU renders into shared D3D12 textures (custom `VulkanPlatform` swap chain), signalling a shared fence |
| **Linux** | OpenGL (GLX) | OpenGL engine shares skiko's GLX context and renders into its textures |

The snapshot is a GPU-side copy, and the CPU waits for it before Filament reuses the texture. If a setup isn't covered, or anything fails, the view falls back to CPU readback.

### Trade-offs

- **Pros**: Compose widgets can be overlaid freely over the 3D content; with GPU sharing on, frames never leave the GPU.
- **Cons**: CPU readback pays GPU→CPU bandwidth that scales with window size, plus 1–2 frames of latency. GPU sharing depends on skiko internals, so a skiko update can break it (it then falls back to readback).

---

## Layering & Stacking

How a `FilamentView` composites against the rest of the UI follows directly from its strategy:

- **JVM / Desktop** — the 3D output is an ordinary Skia `Image` drawn into the Compose scene. It participates in the Compose draw order like any other content, so Compose widgets can sit above *or* below it, and multiple `FilamentView`s can be freely stacked and interleaved with other Compose UI. Full integration.
- **Android, iOS, Web** — the 3D output lives in a native/DOM surface that sits **behind** the Compose layer and is revealed by a transparent hole. Compose UI can be drawn **on top** of a view, but you cannot place opaque Compose content *behind* it, and two `FilamentView`s cannot be stacked over one another (each owns a separate surface on the same plane — whichever is on top hides the other). Side-by-side / non-overlapping views (e.g. a split view) work correctly.

In short: desktop offers full layering because the frame is a texture inside Compose; the other platforms can only render the 3D plane *below* Compose.

## Transparency

`FilamentView(transparent = true)` (and the same parameter on `FilamentSceneView`) makes the view's
background alpha-0 so Compose content **behind** it shows through — the one case where Android, iOS
and Web escape the "3D plane below Compose" rule above, because the surface moves in front and
composites by alpha instead of being revealed by a hole punch.

It sets `BlendMode.TRANSLUCENT` plus a `clear = true`, alpha-0 `ClearOptions` — the default
(`clear = false`, `discard = true`) leaves untouched swapchain pixels undefined, which shows up as
opaque garbage. Each platform then needs its own surface change:

| Platform | What changes |
| :--- | :--- |
| **Android** | `SurfaceView` → `TextureView` with `isOpaque = false`; swapchain gets `CONFIG_TRANSPARENT`. A `TextureView` composites in the view hierarchy rather than owning a hardware layer, so it costs more than the opaque path. |
| **iOS** | `CAMetalLayer.opaque = false`, swapchain gets `CONFIG_TRANSPARENT`, and the interop view is `placedAsOverlay` — as a normal interop view Compose punches a hole for it and erases whatever was drawn behind. |
| **Web** | The per-view 2D canvas moves *in front* of the Compose canvas (`zIndex: 1`, `pointer-events: none`) with no hole punch, and the blit `clearRect`s first so the view's own alpha survives. `Engine.create` also always requests `alpha: true` on the WebGL context — it defaults to `alpha: false`, which forces every frame opaque regardless of blend mode. |
| **JVM / Desktop** | The readback image switches from `OPAQUE` to `PREMUL` alpha (GPU-shared frames are premultiplied already); the rest of the path already composites through Compose. |

The surface type and its swapchain flags are fixed when the platform view is created, so toggling
`transparent` at runtime rebuilds the surface (via `key(transparent)`) rather than mutating it.

Two consequences worth knowing: on Android/iOS/Web the surface is now on top, so Compose content
drawn *above* the view in the layout shows **through** it rather than covering it (draw the
foreground UI as part of the scene, or accept the see-through). And transparency is per-view state,
not a scene property — two views of one scene can differ.

## Future Direction

The stacking limitation on Android, iOS, and Web is a consequence of today's surface/context model, not a fundamental one. Newer GPU APIs with first-class shared-context and render-to-texture interop — **Vulkan** and **Metal** on mobile, **WebGPU** on the web — would let Filament render into a texture that the host UI's renderer (Skia/Skiko) can sample directly, the way the desktop's experimental GPU-sharing path already hands Skia a Filament texture. That would bring true in-tree compositing (and arbitrary stacking) to those platforms and retire the hole-punch and per-view blit workarounds. It depends on both Filament and Compose Multiplatform exposing those backends through their public surfaces.

## Summary

| Platform | Strategy | Per-frame copy | Compose overlay | Stack multiple views | `transparent = true` |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Android** | Native `SurfaceView` + SwapChain | None | On top only | No | `TextureView`, `CONFIG_TRANSPARENT` |
| **iOS** | Native `CAMetalLayer` + SwapChain | None | On top only | No | non-opaque layer, `placedAsOverlay` |
| **Web** | Offscreen canvas + per-view `drawImage` blit | GPU canvas→canvas | On top only | No (side-by-side OK) | canvas in front, `alpha: true` context |
| **JVM / Desktop** | Offscreen SwapChain → Skia `Image` | GPU→CPU every frame (GPU-side with the experimental opt-in) | Above or below | Yes | `PREMUL` readback |
