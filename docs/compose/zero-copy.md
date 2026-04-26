# Zero-Copy Rendering

Zero-copy rendering is the "holy grail" of 3D UI integration. It allows the 3D engine (Filament) and the UI engine (Skia/Compose) to share the same GPU memory, eliminating the need to copy pixel data to the CPU.

## The Metal Approach (macOS)

On macOS, we can achieve zero-copy rendering using **Metal texture sharing**.

### The Mechanism
1. We allocate a native `MTLTexture` that is accessible to both the CPU and GPU.
2. Filament is instructed to use this texture as its rendering target.
3. Skia (via Skiko) wraps the *same* `MTLTexture` handle as a `BackendRenderTarget`.
4. Compose draws this shared texture directly.

This results in near-zero overhead and allows for extremely high-resolution 3D viewports without taxing the CPU.

## Platform Limitations

While zero-copy is highly desirable, it is currently not feasible on all platforms due to the way Skia/Skiko is configured.

### Windows and Linux
On Windows and Linux, achieving zero-copy with Filament and Compose is currently blocked by several factors:

1. **Skiko Backend**: At the moment, Skiko (the rendering layer for Compose Desktop) primarily uses OpenGL or its own internal abstractions. While it has some support for Vulkan, it is not consistently exposed or utilized in a way that allows easy external texture sharing.
2. **Vulkan Availability**: Achieving zero-copy on these platforms would typically require using the **Vulkan** API. However, since Skiko often does not use Vulkan by default, we cannot establish a shared GPU memory path between Filament's Vulkan backend and the Compose compositor.
3. **Complexity of Interop**: Unlike Metal, which has a very straightforward `MTLTexture` sharing model, Vulkan and OpenGL interop involves complex synchronization primitives (semaphores, fences) and memory layout transitions that are difficult to manage through the current Skiko API surface.

### Current Status
Because of these limitations, `filament-compose` defaults to the **ReadPixel approach** on Windows and Linux. This ensures that the library "just works" out of the box, even if it doesn't yet reach the peak performance possible on macOS.

## Future Roadmap
We are monitoring updates to Skiko and Compose Multiplatform. If stable Vulkan or specialized DirectX sharing becomes available in Skiko, we plan to implement zero-copy paths for Windows and Linux.
