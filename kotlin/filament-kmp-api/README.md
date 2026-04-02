# filament-kmp-api

Kotlin Multiplatform API layer for Filament C wrapper.

## Current scope

- Provides `expect/actual` platform split for all requested targets.
- Seeds cinterop with `filament/Engine.h`.
- Keeps APIs intentionally small until the first Engine/Scene wrappers are added.

## Next

1. Expand cinterop header surface (core + gltfio wrappers).
2. Add first native handle wrappers (`Engine`, `Scene`, `View`).
3. Add platform-specific linker options per target.

