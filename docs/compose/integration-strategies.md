# Compose Integration Strategies

To integrate the Filament 3D engine with Compose Multiplatform, a mechanism is required to display Filament's rendered output within the Compose UI tree. `filament-compose` supports two primary strategies depending on the platform and performance requirements.

## 1. Pixel Readback Approach (Default)

The **Pixel Readback** approach is the most portable method and is used by default on most platforms (Windows, Linux, Android).

### How it Works
1. **Offscreen Rendering**: Filament renders the scene into a `RenderTarget` backed by an offscreen texture.
2. **`readPixels` Synchronization**: Once a frame is rendered, `Renderer.readPixels` is used to pull the image data from the GPU into a CPU-accessible `ByteArray`. This is performed asynchronously to minimize pipeline stalls.
3. **Skia Image Wrapping**: The pixel data is wrapped into a Skia `Image` using `Image.makeRaster`.
4. **Compose Drawing**: The resulting image is drawn onto the Compose canvas using a `Spacer` with a `drawBehind` modifier.

### Trade-offs
- **Pros**: Highly portable; allows easy overlaying of Compose widgets on top of the 3D content.
- **Cons**: CPU overhead from GPU-to-CPU copies; slight latency (1-2 frames) due to asynchronous readback.

---

## 2. Zero-Copy Rendering (macOS - Experimental)

> [!WARNING]
> **Experimental**: The Zero-Copy implementation is currently in an experimental stage, considered "hacky," and is not fully supported in the main branch. This code has been moved to a specific testing branch for further evaluation.

Zero-copy rendering allows Filament and Skia (Compose's renderer) to share the same GPU memory, eliminating the need to copy pixel data to the CPU.

### The Metal Approach
On macOS, this is achieved using **Metal texture sharing**:
1. A native `MTLTexture` is allocated that is accessible to both engines.
2. Filament renders directly into this texture.
3. Skia wraps the same texture handle as a `BackendRenderTarget`, allowing Compose to draw it directly.

This results in near-zero overhead and is recommended for high-performance experiments on macOS, though it remains unsupported for production use in its current state.

### Why not Windows/Linux?
On Windows and Linux, achieving zero-copy is currently blocked by limitations in the current version of Skiko (the rendering layer for Compose Desktop). This would require consistent Vulkan/DirectX interop, which is not yet fully exposed or managed by the underlying framework.

---

## Summary Table

| Strategy | Platform Support | Performance | Status |
| :--- | :--- | :--- | :--- |
| **ReadPixel** | All (Windows, Linux, Android, iOS) | Medium | **Stable** |
| **Zero-Copy** | macOS (Metal) | High | **Experimental** (Testing Branch) |
