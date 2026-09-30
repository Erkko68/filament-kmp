# Roadmap

## Stability & long-term maintenance

With `0.2.0` the project dropped the `-beta` label: the major development phase and the
internal repository restructuring (prebuilt pipeline, vendored web externals, CI matrix,
API-surface enforcement) are done, and the focus shifts to tracking upstream and hardening.

- **Upstream tracking** — each Filament feature release (1.73 → 1.74 → …) is picked up as a
  minor release following [docs/internals/upgrading-filament.md](docs/internals/upgrading-filament.md);
  upstream point releases and wrapper fixes ship as patches. Minor releases are the ongoing
  channel — see [README → Versioning & stability](README.md#versioning--stability).
- **Path to `1.0.0`** — a major bump is reserved for maturity and very large changes: a
  stabilized public API and the known issue backlog worked down. It is not tied to any
  upstream Filament version. Until then, minor releases may still adjust public API (always
  listed in the [changelog](CHANGELOG.md)).
- **Known gaps** — per-platform differences are marked with `@PlatformGap` and listed in
  [Platform Notes](docs/guide/platform-notes.md#api-coverage); since every platform calls the same C
  API, the only ones left come from WebGL and single-threaded wasm.

## The generated C API

**Done** (next release): every platform calls one `Fila*` C API generated from Filament's public C++
headers, and the Kotlin API follows C++ (names, owners, defaults). Android no longer uses upstream's
Java bindings, web no longer uses embind, and a Filament upgrade is a regenerate plus a reviewed diff.
How it works: [The Generated C API](docs/internals/c-api.md).

What's next for it:

- **Surface native panics as Kotlin exceptions.** A Filament precondition failure aborts the
  process today. Catching `utils::Panic` in the generated C wrappers and rethrowing on the Kotlin side
  would make misuse debuggable, starting with the desktop and Android JNI paths.
- **Shrink the hand-written remainder.** The few `c/<module>/manual` functions are callbacks that
  take C++ types and arrays C++ fills; each one the generator learns is one less to maintain.
- **Track upstream's header annotations.**
  [google/filament#10410](https://github.com/google/filament/pull/10410) annotates the headers and
  [#10426](https://github.com/google/filament/pull/10426) generates the Android Java from them
  (1.77.2). Where upstream's annotations say what is API, the skip list in `c/api-headers.txt` can
  follow them instead of being maintained by hand.

## Compose Desktop: GPU-to-GPU frame sharing

> **Scope: `filament-compose` on desktop only.** This is about how Filament's frames reach the
> Compose Desktop canvas; the bindings and every other platform work the same either way.

### Where it stands

Compose Desktop has no public API to embed a native surface, so Filament renders offscreen and each
frame becomes a Skia `Image`. By default that goes through a CPU readback (correct everywhere, but
`W×H×4` bytes of GPU→CPU bandwidth per frame). Frames can instead stay on the GPU on every
desktop OS through an experimental opt-in
(`FilamentComposeDesktop.isGpuToGpuFrameSharingEnabled`):

| OS      | Compose draws with | How Filament's frame reaches it |
|---------|--------------------|---------------------------------|
| macOS   | Metal              | Metal engine renders into `MTLTexture`s on skiko's device |
| Windows | Direct3D 12        | Vulkan engine renders into shared D3D12 textures through a custom `VulkanPlatform` swap chain, synced by a shared fence |
| Linux   | OpenGL (GLX)       | OpenGL engine shares skiko's GLX context |

Skia wraps each finished texture on Compose's own `DirectContext` and snapshots it (a GPU-side
copy). Any failure prints a report, asks for an issue and falls back to CPU readback.

### Why it's experimental

Wrapping a foreign texture on skiko's context is public API (`BackendRenderTarget.makeMetal` /
`makeDirect3D` / `makeGL` → `Surface.makeFromBackendRenderTarget`). *Finding* Compose's context is
not: we reach into `SkiaLayer.redrawerManager.redrawer` for its `DirectContext`, draw lock and
device (the Metal adapter, the D3D12 `DirectXDevice`, the GLX context). Any skiko update can
reshape those internals; the fallback keeps the app rendering when it does.

**A skiko upgrade alone doesn't remove the reflection.** skiko 0.152 made
`Canvas.recordingContext` public ([JetBrains/skiko#1219](https://github.com/JetBrains/skiko/pull/1219))
and 0.154 adds `Canvas.surface` ([#1313](https://github.com/JetBrains/skiko/pull/1313)), but
Compose Desktop records every draw into a `Picture` that only skiko's internal redrawer replays on
the GPU, so inside Compose both return `null` (probed on Compose `1.13.0-alpha01`). When Compose
moves to skiko 0.152 the reflection paths change (the Metal context moves onto `MetalRedrawer`), but
they're still needed. They go away once Compose exposes a window's GPU context publicly.

### Next: skiko's move to Graphite

skiko is moving from Skia's Ganesh backend to **Graphite**, built for device-model APIs (Metal,
Vulkan, Dawn):

- `skiko-graphite` ships Metal and Vulkan contexts, recorders and `BackendTexture`s, and
  `Surface.wrapBackendTexture`, the Graphite way to draw into a foreign texture
  ([#1245](https://github.com/JetBrains/skiko/pull/1245),
  [#1261](https://github.com/JetBrains/skiko/pull/1261)).
- The AWT redrawers no longer create Ganesh objects natively
  ([#1266](https://github.com/JetBrains/skiko/pull/1266)), `skiko-graphite-awt` artifacts are
  published ([#1277](https://github.com/JetBrains/skiko/pull/1277)), and Ganesh is being split into
  its own `skiko-ganesh` module ([#1301](https://github.com/JetBrains/skiko/pull/1301)).

Once Compose draws its windows with Graphite, Filament's Metal/Vulkan textures can be handed over as
Graphite `BackendTexture`s, and on Vulkan both sides can share one device on every OS, with no
D3D12 or GLX glue. Our targets are already split per API, so the move is one new target, not a
rewrite. It still needs Compose to expose the window's Graphite context publicly.

### Endgame: both halves on Dawn

Filament has an experimental WebGPU backend that runs natively through **Dawn** (behind a build
flag), and Graphite supports Dawn too. With both sides on one `WGPUDevice`, sharing is a texture
wrap on every OS, and the web path joins the same model.

| Layer    | Today                          | Target                  | Status |
|----------|--------------------------------|-------------------------|--------|
| skiko    | Ganesh (GL / D3D12 / Metal)    | Graphite (Metal / Vulkan / Dawn) | In progress: graphite modules shipped, AWT redrawers not yet |
| Filament | OpenGL / Vulkan / Metal        | WebGPU / Dawn           | Experimental, behind a build flag |

### Track these

- **skiko: Graphite modules and AWT** — [#1245](https://github.com/JetBrains/skiko/pull/1245), [#1266](https://github.com/JetBrains/skiko/pull/1266), [#1301](https://github.com/JetBrains/skiko/pull/1301), [releases](https://github.com/JetBrains/skiko/releases)
- **JetBrains internship — Graphite backend support in Skiko** — https://internship.jetbrains.com/projects/1686
- **SKIKO-549 — Vulkan bindings** — https://youtrack.jetbrains.com/issue/SKIKO-549/Vulkan-bindings
- **compose-jb #382 — Expose skiko's renderApi** — https://github.com/JetBrains/compose-multiplatform/issues/382
- **Filament #2054 — WebGPU support** — https://github.com/google/filament/issues/2054
- **Filament BUILDING** (WebGPU build flag) — https://github.com/google/filament/blob/main/BUILDING.md
- **google/dawn — native WebGPU** — https://github.com/google/dawn
