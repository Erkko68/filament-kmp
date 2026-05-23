# Filament KMP Documentation

Welcome. These docs cover everything you need to build with `filament-kmp` — from a 5-minute Gradle setup to the lower-level Filament concepts the wrapper exposes.

## Getting started

- **[Getting Started](getting-started.md)** — Add the dependency, configure each platform, and render your first scene.
- **[Modules](modules.md)** — What each published artifact does, Maven coordinates, dependency graph.
- **[Platform Notes](platform-notes.md)** — Backend selection, per-platform gotchas, known issues.

## Compose Multiplatform integration

- **[Overview](compose/README.md)** — Component reference for the `filament-compose` DSL.
- **[Scope & Philosophy](compose/scope.md)** — What `filament-compose` is and isn't.
- **[Integration Strategies](compose/integration-strategies.md)** — How Filament's GPU output reaches the Compose canvas on each platform.
- **[Materials](compose/materials.md)** — Author `.mat` source, compile with `matc`, load and parameterise at runtime.

## For contributors

- **[Repository Structure](repo-structure.md)** — How the C++, JNI, Kotlin, and prebuilt layers fit together.
- **[Automation & Scripts](scripts/README.md)** — Internal tooling for prebuilts, API coverage, releases.

## Upstream Filament documentation

Filament KMP is a thin wrapper. For engine concepts — PBR, lighting, materials, the render pipeline — go to the source:

- **[Filament Engine](https://google.github.io/filament/Filament.md.html)** — Authoritative reference for PBR theory, lighting, scene graph, render pipeline.
- **[Materials](https://google.github.io/filament/Materials.md.html)** — Material system, surface shading model, `matc` compiler reference.
- **[`google/filament` on GitHub](https://github.com/google/filament)** — Source, issues, releases.

> [!TIP]
> When something is not documented here, it's almost certainly documented in the upstream Filament docs. The API surface is intentionally kept close to Android Filament so cross-referencing works directly.

---

[← Back to main README](../README.md)
