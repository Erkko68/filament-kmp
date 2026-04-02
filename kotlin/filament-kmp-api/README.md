# filament-kmp-api

Kotlin Multiplatform API layer for Filament C wrapper.

## Current scope

- Provides `expect/actual` platform split for all requested targets.
- Seeds cinterop with `filament/Engine.h`.
- Adds a first OO `Engine` wrapper (`Engine.create`, `Engine.close`) with native backing on Apple/Linux/Windows native targets.
- Android and Web currently expose explicit `TODO` runtime errors for `Engine.create` until their bridge layers are added.

## Next

1. Wire Android bridge (JNI/NDK) for `Engine`.
2. Add Web backend strategy (WASM bridge or JS-hosted backend).
3. Expand wrappers (`Scene`, `View`, `Renderer`) and ownership-safe relationships.
