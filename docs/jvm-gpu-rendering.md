# JVM GPU Rendering — Filament + Skia/Compose Desktop

GPU-to-GPU texture sharing between the Filament 3D engine and Skiko/Compose Desktop on JVM targets (macOS today, Linux/Windows roadmap).

---

## How it works

```
Filament (3D engine)
  └─ renders into MTLTexture (shared GPU memory)
       └─ imported as Filament RenderTarget (importTexture)
            │   flushAndWait() ← CPU sync barrier
            ▼
Skia (via Skiko / Compose Desktop)
  └─ wraps same MTLTexture as BackendRenderTarget
       └─ makeImageSnapshot() → Image in Skiko's DirectContext
            └─ drawImage() on Compose canvas ← displayed each frame
```

No CPU readback. Both engines read/write the same GPU memory.

---

## File map

| File | Role |
|---|---|
| `java/filament/src/main/cpp/MetalUtils.mm` | macOS JNI — creates/releases the shared MTLTexture |
| `java/filament/src/main/java/.../jni/Texture.java` | JVM declaration of `nCreateMetalTexture` / `nReleaseMetalTexture` |
| `samples/shared/src/commonMain/.../FilamentRenderer.kt` | Filament setup, offscreen render target, animation loop |
| `samples/shared/src/jvmMain/.../FilamentView.jvm.kt` | Compose composable — wires Filament output into Skia canvas |
| `java/filament/CMakeLists.txt` | Conditional native compilation per platform |

---

## macOS implementation (Metal)

### JNI layer (`MetalUtils.mm`)

Two functions, no global state:

```objc
// Creates a BGRA8Unorm MTLTexture on devicePtr's device.
// Usage flags: ShaderRead | RenderTarget | ShaderWrite
// Storage: MTLStorageModeShared (GPU-accessible, coherent)
// Returns: __bridge_retained pointer — caller must call nReleaseMetalTexture.
jlong nCreateMetalTexture(jlong devicePtr, jint width, jint height)

// Releases a handle from nCreateMetalTexture via __bridge_transfer (ARC).
void  nReleaseMetalTexture(jlong handle)
```

`devicePtr` is Skiko's `MTLDevice*`, obtained on the Kotlin side via `skikoMetalDevicePtr()`. Passing it explicitly ensures the texture is on the same GPU as the compositor — important on multi-GPU systems.

### Why `MTLStorageModeShared`?

`MTLStorageModeShared` means the texture lives in memory visible to both CPU and GPU without explicit blits. It allows Skia to read the texture as a sampled image without a separate copy, which is what `makeFromBackendRenderTarget` + `makeImageSnapshot()` relies on.

`MTLStorageModePrivate` (GPU-only) would be faster for pure GPU-GPU work but prevents Skia from reading unless you add an explicit blit to a separate staging texture.

### Skiko DirectContext discovery

Compose's drawing canvas is backed by a Skiko `RenderNode` (not a plain `Surface`), so `Canvas._owner` is not a `Surface` and does not carry a `DirectContext`. The context lives in Skiko's internal `MetalContextHandler.context` field, reached via this reflection path:

```
Window.getWindows()
  └─ AWT component tree
       └─ WindowSkiaLayerComponent
            └─ .redrawerManager   (RedrawerManager)
                 └─ .redrawer      (MetalRedrawer)
                      └─ .contextHandler  (MetalContextHandler)
                           └─ .context    (DirectContext)  ← what we need
```

The walk uses `getDeclaredField` + superclass traversal (necessary because `getDeclaredFields` does not include inherited fields). The result is cached for the lifetime of the process in `cachedSkikoCtx`.

**Why the same DirectContext matters:** Skia GPU images are context-bound. An image created in context A cannot be drawn on a canvas in context B — the draw silently no-ops. Using Skiko's own context ensures the snapshot is drawable on the Compose canvas without a CPU round-trip.

### Per-frame snapshot cycle

```
drawBehind {
    jvmRenderer.render(frameTime)         // Filament renders; internally calls flushAndWait()
    makeMetalBackendRT(w, h, textureHandle) // wrap texture as Skia render target (no GPU alloc)
    Surface.makeFromBackendRenderTarget(…)  // wrap as Skia surface (kUninit — no clear)
    surface.makeImageSnapshot()             // image referencing the same MTLTexture
    surface.close()
    canvas.drawImage(snapshot, 0, 0)
    prevSnapshot.close(); prevSnapshot = snapshot  // keep alive one extra frame (see below)
}
```

**Why recreate the surface every frame?**
Skia caches `makeImageSnapshot()` on surfaces it considers "unchanged" (no Skia draws were issued). From Skia's perspective the surface is always clean because Filament — not Skia — wrote to the texture. Recreating the `Surface` + `BackendRenderTarget` wrapper each frame is cheap (no GPU allocation; it just updates a thin CPU-side struct) and forces Skia to issue a fresh texture read.

**Why keep `prevSnapshot` alive one extra frame?**
The Compose rendering pipeline records draw commands into a `RenderNode` during the draw phase, then Skiko replays the node in a separate GPU pass. Between recording and replay, Skia's ref-counting keeps the `SkImage*` alive as long as the `RenderNode` holds it. However, the JVM `Image.close()` calls `SkImage::unref()` — if the JVM object is collected before the node is replayed, the native image could be freed. Storing the snapshot in `prevSnapshot` and closing it on the *next* frame guarantees the image outlives the current node's GPU pass.

### Synchronisation

`flushAndWait()` (called inside `FilamentRenderer.render()`) commits Filament's Metal command buffer and CPU-blocks until the GPU finishes. Because Skiko replays the RenderNode in a subsequent GPU pass (after the Compose draw phase), Filament's writes are always complete before Skia reads the texture. No Metal fence or semaphore is required — the CPU serialisation is the barrier.

---

## Adding a new JVM platform

### Linux / Windows (Vulkan)

Skiko on Linux and Windows uses a **Vulkan** backend. The approach is the same but the texture type changes.

**1. Create `java/filament/src/main/cpp/VulkanUtils.cpp`:**

```cpp
// Implement these two functions for Vulkan:
//
// jlong nCreateVulkanTexture(jlong devicePtr, jlong physDevicePtr, jint width, jint height)
//   - Cast devicePtr to VkDevice, physDevicePtr to VkPhysicalDevice
//   - Create VkImage with:
//       format:     VK_FORMAT_B8G8R8A8_UNORM
//       usage:      VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT | VK_IMAGE_USAGE_SAMPLED_BIT
//       tiling:     VK_IMAGE_TILING_OPTIMAL
//       layout:     VK_IMAGE_LAYOUT_UNDEFINED (transition before first use)
//   - Allocate VkDeviceMemory (VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT)
//   - Return the VkImage handle cast to jlong
//
// void nReleaseVulkanTexture(jlong imageHandle, jlong devicePtr)
//   - Cast handles, call vkDestroyImage + vkFreeMemory
```

**2. Update `CMakeLists.txt`** (already stubbed):
```cmake
elseif (UNIX)
    target_sources(filament-jni PRIVATE src/main/cpp/VulkanUtils.cpp)
    target_link_libraries(filament-jni PRIVATE vulkan)
```

**3. Update `Texture.java`:**
```java
// Add alongside the macOS declarations:
public static native long nCreateVulkanTexture(long devicePtr, long physDevicePtr, int width, int height);
public static native void nReleaseVulkanTexture(long imageHandle, long devicePtr);
```

**4. Get Skiko's Vulkan device pointers:**

On the Kotlin side the reflection path differs. Skiko's `VulkanContextHandler` exposes the Vulkan handles — use the same depth-5 field walk (`findSkikoContext()`) to locate a `GrDirectContext` on Linux/Windows. You will also need:
- `VkDevice*` for texture creation
- `VkPhysicalDevice*` for memory type selection

These live in Skiko's `VulkanDevice` / `VulkanExtensions` structures and can be retrieved similarly to how `MetalContextHandler.context` is reached.

**5. Create `FilamentView.linux.kt` / `FilamentView.windows.kt`:**

The composable structure is identical to the macOS version. Replace:
- `skikoMetalDevicePtr()` → `skikoVulkanDevicePtr()` (reflecting Skiko's VkDevice)
- `makeMetalBackendRT(…)` → `makeVulkanBackendRT(…)` (via `BackendRenderTargetKt._nMakeVulkan`)
- `nCreateMetalTexture` → `nCreateVulkanTexture`
- `nReleaseMetalTexture` → `nReleaseVulkanTexture`
- The surface format may differ: check Skiko's Vulkan surface format (commonly `VK_FORMAT_B8G8R8A8_UNORM`)

**Synchronisation on Vulkan:**

`flushAndWait()` still provides the CPU barrier. For full pipeline correctness, consider adding a Vulkan pipeline barrier (image layout transition `COLOR_ATTACHMENT_OPTIMAL → SHADER_READ_ONLY_OPTIMAL`) after Filament's render pass completes and before Skia samples the image.

---

## Known limitations

| Limitation | Notes |
|---|---|
| Multi-GPU macOS | `skikoMetalDevicePtr()` returns Skiko's chosen device. Filament's `Engine.create(Backend.METAL)` independently selects the system default device. On most Macs these are the same; on multi-GPU systems they may differ. Fix: expose `Engine.Builder.sharedContext(queuePtr)` to JNI and call it with a queue from Skiko's device. |
| Skiko version coupling | Reflection paths (`redrawerManager`, `redrawer`, `contextHandler`, `context`) target Skiko **0.9.37.4**. If Skiko renames these internals the walk will silently return `null` and rendering will stop. |
| `BackendRenderTargetKt._nMakeMetal` | Package-private in Skiko. If it is removed or renamed, use `BackendRenderTarget(Long)` constructor directly if the internal handle format is stable. |
| Skia snapshot caching | `makeImageSnapshot()` on an externally-written surface requires a fresh `Surface` per frame to bypass Skia's "unchanged surface" cache. This costs one thin CPU-side struct allocation per frame — negligible. |
