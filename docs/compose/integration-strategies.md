# Compose Integration Strategies

To integrate the Filament 3D engine with Compose Multiplatform, a mechanism is required to display Filament's rendered output within the Compose UI tree. Each platform uses a different strategy depending on what the host UI framework exposes.

## 1. Native Surface Rendering (Android, iOS, Web)

On platforms that expose a native GPU surface to the UI layer, `filament-compose` renders directly into that surface via a `SwapChain`. No pixel copies are involved.

### Android — `SurfaceView`

Filament renders into a `SwapChain` backed by a native Android `Surface` obtained from a `SurfaceView` embedded via `AndroidView`. The surface is created, resized, and destroyed through a `SurfaceHolder.Callback`.

### iOS — `CAMetalLayer`

Filament renders into a `SwapChain` backed by a `CAMetalLayer` that is embedded into a `UIKitView`. The layer's pixel format is set to `BGRA8Unorm` and its `drawableSize` is updated on every layout pass.

### Web — WebGL Canvas

Filament renders into a `SwapChain` backed by an `HTMLCanvasElement` (`engine.jsCanvas`) that is injected into the DOM via a `WebElementView` container `<div>`. The canvas is positioned absolutely inside the container and sized to fill it.

### Trade-offs

- **Pros**: Zero CPU overhead; no frame latency; the GPU renders directly to the display surface.
- **Cons**: Compose widgets cannot be overlaid on top of the 3D surface — the native view always renders behind or on top of the Compose layer depending on platform compositing.

---

## 2. Pixel Readback (JVM / Desktop)

On JVM/Desktop, there is no way to embed a native Filament surface inside a Skia/Compose canvas. Instead, Filament renders to an offscreen `RenderTarget` and the pixels are read back to the CPU each frame, then handed to Skia.

### How it Works

1. **Offscreen RenderTarget**: Filament renders into a `RenderTarget` backed by a color texture (`RGBA8`) and a depth texture (`DEPTH32F`).
2. **Double-buffered readback**: Two `ByteArray` pixel buffers alternate each frame. While one buffer's GPU→CPU copy is in flight, the next frame can already issue a new `readPixels` call on the other buffer — keeping the readback pipelined with rendering.
3. **Skia Image**: When a readback completes, the buffer is wrapped into a Skia `Image` via `Image.makeRaster`.
4. **Compose drawing**: The image is drawn onto a `Spacer` using a `drawBehind` modifier, which lets Compose widgets be overlaid on top of the 3D content.

A 150 ms debounce is applied to resize events to avoid reallocating textures on every pixel change during window dragging.

### Trade-offs

- **Pros**: Compose widgets can be overlaid freely over the 3D content.
- **Cons**: CPU overhead from GPU→CPU copies every frame; 1–2 frame latency from the asynchronous readback pipeline.

---

## Summary

| Platform | Strategy | GPU→CPU copy | Compose overlay |
| :--- | :--- | :--- | :--- |
| **Android** | Native `SurfaceView` + SwapChain | None | Limited |
| **iOS** | Native `CAMetalLayer` + SwapChain | None | Limited |
| **Web** | WebGL Canvas + SwapChain | None | Limited |
| **JVM / Desktop** | Offscreen RenderTarget + `readPixels` | Every frame | Yes |
