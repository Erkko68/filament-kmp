# Roadmap — Cross-platform GPU sharing & the Dawn convergence

> **Scope: `filament-compose` only.** This roadmap is about the display bridge in the
> `:kotlin:filament-compose` module — how Filament's offscreen output is presented inside Compose
> Multiplatform (skiko/Skia). It does **not** affect the core `:kotlin:filament` bindings, the
> material/scene APIs, or any other module; those work the same regardless of how the final image
> reaches the Compose canvas.
>
> Status: forward-looking. The interim approach (CPU readback everywhere, Metal GPU sharing on
> macOS) is what ships today; everything below is the plan for closing the Linux/Windows
> zero-copy gap.

## The problem

To display Filament's offscreen output inside Compose Multiplatform we hand a Filament-rendered
texture to skiko/Skia (which owns the Compose canvas). Doing this **without a CPU round-trip**
requires Filament and skiko to share a GPU resource. Whether that's possible comes down to the
GPU API's *resource model*:

- **Device-resource model** (Metal, D3D12, Vulkan, WebGPU): resources belong to the *device*.
  You wrap an externally-created texture on skiko's existing context — no context sharing needed.
  This is exactly why macOS works: Filament and skiko both use the system `MTLDevice`, so a
  Filament `MTLTexture` can be wrapped as a Skia `BackendRenderTarget`.
- **Context-resource model** (OpenGL): resources belong to a *context / share-group*. skiko would
  have to create Filament's context inside its own share-group and expose its `GLXContext`/`HGLRC`.
  skiko's legacy desktop GL redrawer does not do this — **this is the wall we hit on Linux.**

### Where each platform stands today

| OS      | skiko backend (today) | Resource model | GPU sharing today |
|---------|-----------------------|----------------|-------------------|
| macOS   | Metal                 | device         | ✅ works (Metal texture wrap) |
| Windows | Direct3D 12           | device         | ⚠️ possible via Vulkan→D3D12 external-memory, not built |
| Linux   | OpenGL                | context        | ❌ blocked by skiko's legacy GL context handling |

Interim fallback on all platforms: **CPU readback** (`SwapChain` READABLE → `readPixels` →
`Image.makeRaster` → draw), double-buffered to pipeline the GPU→CPU copy. Correct everywhere,
but pays `W×H×4` of bandwidth per frame.

## The endgame: both halves migrate to Dawn

The clean, universal fix is for **both** sides to render on **Dawn** (Google's native WebGPU
implementation, backed by D3D12 / Metal / Vulkan):

- **skiko → Graphite → Dawn/Vulkan.** Skia's next-gen backend (Graphite) is designed around
  modern device-model APIs (Metal, Vulkan, Dawn, D3D12). When skiko migrates off Ganesh, a
  Vulkan/Dawn desktop backend arrives with it.
- **Filament → WebGPU → Dawn.** Filament already has a WebGPU backend that runs on desktop
  natively via Dawn (not web-only). It's experimental and behind a build flag today, but actively
  developed by Google.

WebGPU is a device-resource model, and Dawn supports shared/external texture memory. So once both
halves are on Dawn, sharing reduces to: **create one `WGPUDevice`, render both Filament and skiko
on it, wrap Filament's `wgpu::Texture` on skiko's Graphite recorder.** This is cleaner than raw
Vulkan because there is no per-OS VK↔D3D12↔GL import glue — both sides speak the same Dawn device
on every platform, and it dissolves the Linux GL-context wall entirely. It also subsumes the
existing macOS Metal path and the web path into one model.

### The two waits are the same bet

| Layer    | Today                       | Target              | Status |
|----------|-----------------------------|---------------------|--------|
| skiko    | Ganesh (GL / D3D12 / Metal) | Graphite + Dawn/Vulkan | Major issue, staffed (JetBrains internship), no public date |
| Filament | OpenGL / Vulkan / Metal     | WebGPU / Dawn       | Experimental, active, behind a build flag |

Both must land **together** for the shared-Dawn-device payoff. Realistic horizon: 2026–2027.

## Interim options (until Dawn convergence lands)

1. **Keep CPU readback** as the universal fallback (current `main`).
2. **macOS Metal sharing** — already proven; the device-model wrap pattern.
3. **Filament external-memory export patch** — patch Filament's Vulkan backend to export the render
   target as external memory (opaque-FD on Linux, NT-handle on Windows), then import into skiko's
   native API (D3D12 on Windows, GL via `GL_EXT_memory_object_fd` on Linux). Highest cost, but the
   work carries straight over to the Dawn endgame.
4. **Native overlay / hole-punch** — render Filament's native `SwapChain` into a child surface
   layered into the Compose window (`compose.interop.blending` for z-order). Zero readback, no
   skiko cooperation, no engine patch — but can't blend Compose between 3D layers.

## What we can do now

The Filament half is the half we control. Concretely: build Filament with the WebGPU/Dawn backend
enabled on desktop (`WEBGPU_OPTION` / `-Wj` in `build.sh`) and run the existing samples against it
to gauge how close that half already is. skiko's half we can only watch.

## Track these

- **skiko: Switch from Ganesh to Graphite** — https://github.com/JetBrains/skiko/issues/982
- **SKIKO-549 — Vulkan bindings** — https://youtrack.jetbrains.com/issue/SKIKO-549/Vulkan-bindings
- **JetBrains internship — Graphite backend support in Skiko** — https://internship.jetbrains.com/projects/1686
- **Filament #2054 — WebGPU added support to be future proof** — https://github.com/google/filament/issues/2054
- **Filament RELEASE_NOTES** — https://github.com/google/filament/blob/main/RELEASE_NOTES.md
- **Filament BUILDING** (WebGPU build flag) — https://github.com/google/filament/blob/main/BUILDING.md
- **google/dawn — native WebGPU (D3D12 / Metal / Vulkan / GL)** — https://github.com/google/dawn
- **Skia Vulkan backend** — https://skia.org/docs/user/special/vulkan/
- **compose-jb #382 — Expose skiko's renderApi** — https://github.com/JetBrains/compose-multiplatform/issues/382
