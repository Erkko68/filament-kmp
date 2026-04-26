# Compose Integration: ReadPixel Approach

To integrate the Filament 3D engine with Compose Multiplatform, we need a way to display Filament's rendered output within the Compose UI tree. Since Filament and Skia (the rendering engine behind Compose) usually manage their own GPU contexts and surfaces, sharing data between them can be challenging.

## How it Works

The primary integration strategy used in `filament-compose` is the **Pixel Readback** approach. This method is highly compatible across platforms, including Android, JVM (Windows/Linux/macOS), and potentially Web.

### 1. Offscreen Rendering
Instead of rendering directly to the screen, we configure Filament to render into a `RenderTarget` backed by a `Texture`.
- A `SwapChain` is created with the size of the Composable.
- Filament renders the scene normally into this offscreen buffer.

### 2. `readPixels` Synchronization
Once the frame is rendered, we use Filament's `Renderer.readPixels` method to pull the image data from the GPU back into a CPU-accessible `ByteArray`.
- This is an asynchronous operation in Filament to minimize pipeline stalls.
- We use a `PixelBufferDescriptor` with a callback that notifies Compose when the pixels are ready.

### 3. Skia Image Wrapping
When the pixel data arrives on the CPU:
- We wrap the `ByteArray` into a Skia `Image` using `Image.makeRaster`.
- This `Image` is then stored in a Compose `mutableStateOf`.

### 4. Compose Drawing
The final step is drawing the captured image onto the Compose canvas:
- We use a `Spacer` with a `drawBehind` modifier.
- Inside `drawBehind`, we call `canvas.nativeCanvas.drawImageRect` to paint the latest Filament frame.

## Performance Considerations

### Advantages
- **Portability**: Works on any platform that supports basic Filament and Skia.
- **Layering**: Since the result is a standard Compose drawing operation, you can easily overlay Compose widgets (Buttons, Text, etc.) on top of the 3D content.

### Disadvantages
- **CPU Overhead**: Moving pixels from GPU to CPU and back is expensive, especially at high resolutions (4K).
- **Latency**: There is a 1-2 frame delay between the Filament render and the Compose display due to the asynchronous readback.

For high-performance desktop applications on macOS, we also support a **Zero-Copy** approach (see [Zero-Copy Rendering](zero-copy.md)).
