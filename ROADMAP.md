# Roadmap

## Stability & long-term maintenance

With `0.2.0` the project dropped the `-beta` label: the major development phase and the
internal repository restructuring (prebuilt pipeline, vendored web externals, CI matrix,
API-surface enforcement) are done, and the focus shifts to tracking upstream and hardening.

- **Upstream tracking** — each Filament feature release (1.73 → 1.74 → …) is picked up as a
  minor release following [docs/upgrading-filament.md](docs/upgrading-filament.md);
  upstream point releases and wrapper fixes ship as patches. Minor releases are the ongoing
  channel — see [README → Versioning & stability](README.md#versioning--stability).
- **Path to `1.0.0`** — a major bump is reserved for maturity and very large changes: a
  stabilized public API and the known issue backlog worked down. It is not tied to any
  upstream Filament version. Until then, minor releases may still adjust public API (always
  listed in the [changelog](CHANGELOG.md)).
- **Known gaps** — per-platform binding gaps are tracked via `@PlatformGap` and the coverage
  table in [Platform Notes](docs/platform-notes.md); web-specific limits now come only from
  WebGL and single-threaded wasm, since every platform calls our own C API.

## A C API generated from Filament's headers

Every platform calls the same C wrapper (`c/`): JVM and Android over JNI, iOS through Kotlin/Native,
web as wasm. That wrapper is **generated from Filament's public C++ headers**, so the Kotlin API
tracks C++ (names, owners, defaults) instead of drifting the way hand-written bindings do.

```
c/api-headers.txt ─► clang JSON AST ─► C++ API model ─► generateCApi ─► c/<module>/generated (Fila* C + C++ forwarders)
                                                                   └─► generateKotlinExternals ─► <pkg>.capi externals
                                                                                                  └─► generateBindings ─► JNI forwarders, wasm tables
```

- **Scope is declared, not inferred.** `c/api-headers.txt` lists each module's headers plus a skip
  list (debug hooks, engine internals), and every skipped member is noted in the generated header.
  An entry that names nothing fails the build, so the list can't go stale.
- **One mapping per C++ shape.** Classes become opaque handles; value structs are handles with
  field getters/setters seeded from the C++ defaults; strings, sequences (`Slice`,
  `FixedCapacityVector`, `std::array`), `std::optional`, buffer uploads and callbacks each have one C
  shape; templates are instantiated from a table. Overloads get type-based suffixes.
- **Hand-written code is the exception.** What has no mechanical mapping (callbacks that take C++
  types, pointers the C side can't own) goes in `c/<module>/manual`, and the generated header names
  the C++ signature it stands in for.
- **Upgrading Filament is a regenerate.** A new release means regenerating, reviewing the diff and
  adapting the Kotlin layer; `./gradlew apiGaps` reports any C++ API the C layer doesn't reach.

Upstream is moving the same way for its own bindings:
[google/filament#10410](https://github.com/google/filament/pull/10410) annotates the headers and
[#10426](https://github.com/google/filament/pull/10426) generates the Android Java from them. We
don't depend on it, since Android runs on our C API too, but both read the same headers.

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
