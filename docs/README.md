# Filament KMP Documentation

Filament KMP is two things stacked: **Kotlin Multiplatform bindings to the Filament engine**
(`filament`, `gltfio`, `filament-utils`, `filamat`), and an **optional Compose Multiplatform
layer** on top (`filament-compose`). Both are first-class; pick whichever fits your app.

The bindings follow **Filament's C++ API** (same classes, names and defaults) on Android, iOS,
desktop and web, because every platform calls the same C API generated from Filament's headers.

```
docs/
├── guide/       using the library: setup, modules, the engine without Compose, platform notes
├── compose/     the filament-compose layer
├── migration/   upgrading between releases with breaking changes
└── internals/   for contributors: architecture, the generated C API, bindings, upgrades, testing
```

## Guide

- **[Getting Started](guide/getting-started.md)** — add the dependency, configure each platform, render your first scene.
- **[Modules](guide/modules.md)** — what each published artifact does, which dependency each target needs, and what Gradle actually downloads.
- **[Using the Engine Without Compose](guide/engine.md)** — own the render loop: `Engine` / `View` / `Renderer` / `SwapChain`, your own surface on each platform, headless rendering and readback, glTF loading, lifecycle and threading.
- **[Platform Notes](guide/platform-notes.md)** — backend selection, per-platform gotchas, web limits, desktop GPU frame sharing, error handling.

## Migration

- **[Migrating from 0.6.0](migration/from-0.6.0.md)** — the move to one generated C API and the C++-shaped Kotlin API: setup changes per platform, old → new API tables, behavior changes.

## Compose Multiplatform

- **[Overview](compose/README.md)** — component reference for the `filament-compose` DSL.
- **[Scope & Philosophy](compose/scope.md)** — what `filament-compose` is and isn't.
- **[Integration Strategies](compose/integration-strategies.md)** — how Filament's GPU output reaches the Compose canvas on each platform.
- **[Materials](compose/materials.md)** — author `.mat` source, compile with `matc`, load and parameterise at runtime.

## Internals (for contributors)

- **[Repository Structure](internals/repo-structure.md)** — how the C API, the Kotlin modules, the per-platform runtimes (`jni/`, `desktop/`, `android/`, `web/`) and the prebuilts fit together.
- **[The Generated C API](internals/c-api.md)** — how the `Fila*` C API is generated from Filament's C++ headers: scope, type mappings, hand-written leftovers, `apiGaps`.
- **[Native Bindings](internals/bindings.md)** — how the common Kotlin API calls the C API on each platform, and the rules at that boundary.
- **[Upgrading the Filament Version](internals/upgrading-filament.md)** — bumping `filaVersion`: scoping the diff, refreshing prebuilts, regenerating, adapting the Kotlin API, verifying.
- **Testing** — [environment gating](internals/testing/test-support.md) (`TestEnv`, `@IgnoreJs`), [real-backend rendering tests](internals/testing/rendering-backend-tests.md), [`filament-compose` tests](internals/testing/compose-tests.md).
- **[Automation & Scripts](../scripts/README.md)** — maintenance scripts for upgrade diffs, materials and test runs.

## Upstream Filament documentation

Filament KMP is a thin wrapper. For engine concepts (PBR, lighting, materials, the render pipeline) go to the source:

- **[Filament Engine](https://google.github.io/filament/Filament.md.html)** — PBR theory, lighting, scene graph, render pipeline.
- **[Materials](https://google.github.io/filament/Materials.md.html)** — material system, surface shading model, `matc` compiler reference.
- **[`google/filament` on GitHub](https://github.com/google/filament)** — source, headers, issues, releases.

> [!TIP]
> When something is not documented here, look in Filament's own docs and C++ headers: the Kotlin API
> uses the same names, so the C++ documentation applies directly.

---

[← Back to main README](../README.md)
