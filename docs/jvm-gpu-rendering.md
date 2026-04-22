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

## Linux / Windows (Vulkan) — implementation

Skiko on Linux and Windows uses a **Vulkan** backend. The approach is the same but the texture type changes. The implementation is complete in the same `jvmMain` source set; platform dispatch happens at runtime via `System.getProperty("os.name")`.

### JNI layer (`VulkanUtils.cpp`)

Three functions, opaque handle pattern:

```cpp
// Returns opaque VulkanTextureHandle* (wraps VkImage + VkDeviceMemory).
// devicePtr: VkDevice*, physDevicePtr: VkPhysicalDevice* — both from Skiko reflection.
jlong nCreateVulkanTexture(jlong devicePtr, jlong physDevicePtr, jint width, jint height)

// Destroys VkImage + frees VkDeviceMemory, then deletes the handle struct.
void  nReleaseVulkanTexture(jlong handlePtr)

// Returns the VkImage handle from the opaque struct (for Filament importTexture).
jlong nGetVulkanImage(jlong handlePtr)
```

Unlike Metal where the `MTLTexture*` is passed directly to both Filament and Skia, the Vulkan path separates the concerns:
- `nCreateVulkanTexture` → opaque handle (owns `VkImage` + `VkDeviceMemory`)
- `nGetVulkanImage(handle)` → raw `VkImage` cast to `jlong` — passed to `importTexture()` and to the Skia `BackendRenderTarget`

### Skiko Vulkan device discovery

```
WindowSkiaLayerComponent
  └─ redrawerManager  (RedrawerManager)
       └─ redrawer     (VulkanRedrawer)
            └─ device  (VulkanDevice)
                 ├─ .device         → VkDevice*
                 └─ .physicalDevice → VkPhysicalDevice*
```

Field names verified against Skiko **0.9.37.4**. If they differ on your version, print `redrawer.javaClass.declaredFields.map { it.name }` to discover the correct names.

### `BackendRenderTarget` wrapping

`BackendRenderTargetKt._nMakeVulkan(width, height, imageInfoPtr)` is package-private. `imageInfoPtr` is a native `GrVkImageInfo*` pointer; the raw `VkImage` is passed here. Access pattern mirrors the Metal path (reflection on `access$_nMakeVulkan`).

> **Note:** verify the exact parameter signature of `_nMakeVulkan` in your Skiko version by inspecting `BackendRenderTargetKt` at runtime if the reflection call returns null.

### Synchronisation on Vulkan

`flushAndWait()` still provides the CPU barrier. For full pipeline correctness, consider a Vulkan pipeline barrier (image layout transition `COLOR_ATTACHMENT_OPTIMAL → SHADER_READ_ONLY_OPTIMAL`) after Filament's render pass and before Skia samples the image.

---

## Known limitations

| Limitation | Notes |
|---|---|
| Multi-GPU macOS | `skikoMetalDevicePtr()` returns Skiko's chosen device. Filament's `Engine.create(Backend.METAL)` independently selects the system default device. On most Macs these are the same; on multi-GPU systems they may differ. Fix: expose `Engine.Builder.sharedContext(queuePtr)` to JNI and call it with a queue from Skiko's device. |
| Skiko version coupling | Reflection paths (`redrawerManager`, `redrawer`, `contextHandler`, `context`) target Skiko **0.9.37.4**. If Skiko renames these internals the walk will silently return `null` and rendering will stop. |
| `BackendRenderTargetKt._nMakeMetal` | Package-private in Skiko. If it is removed or renamed, use `BackendRenderTarget(Long)` constructor directly if the internal handle format is stable. |
| Skia snapshot caching | `makeImageSnapshot()` on an externally-written surface requires a fresh `Surface` per frame to bypass Skia's "unchanged surface" cache. This costs one thin CPU-side struct allocation per frame — negligible. |
