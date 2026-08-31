# `:web` — Kotlin external declarations for Filament.js

This module provides the `external` Kotlin declarations that the Web targets
(`js` + `wasmJs`, shared `webMain`) bind against. They are **committed,
hand-maintained sources** under `src/webMain/kotlin/…/web/`, one file per
Filament.js class/interface, plus interop helpers under `…/web/interop/`.

## Source of truth

Filament ships a TypeScript definition, `filament.d.ts`, alongside `filament.js`
/ `filament.wasm`. That d.ts is hand-maintained upstream and **lags the real
binding surface** registered via embind in upstream
`web/filament-js/jsbindings.cpp` — methods are missing, mis-typed,
under-arity'd, or misspelled. The externals here are therefore maintained
against **`jsbindings.cpp`**, not the d.ts.

Run [`scripts/dev/check-js-bindings.sh`](../scripts/dev/check-js-bindings.sh) on
every Filament version bump: it diffs the embind registrations in
`jsbindings.cpp` against the declarations here and prints what's missing.

Four entries in that report are deliberately left undeclared:

| Reported missing | Why |
| :--- | :--- |
| `EntityManager.getActiveEntityCount` | Behind `#if FILAMENT_UTILS_TRACK_ENTITIES`, which the release bundle is not built with — the symbol is in `jsbindings.cpp` but not in the shipped wasm |
| `Engine.isValidStream` | `Stream` has no JS class binding, so no argument of that type can exist |
| `Ktx1Bundle.*` | KTX1 assets load through `Engine.createIblFromKtx1` / `createTextureFromKtx1`; the bundle type is not part of our public surface |
| `MeshReader.loadMeshFromBuffer`, `MeshReader$MaterialRegistry.keys` | `filamesh` is not part of our public surface |

## Conventions

- Naming: upstream's `$`-separated flat names map to `_`
  (`Texture$Builder` → `Texture_Builder`, `gltfio$AssetLoader` →
  `gltfio_AssetLoader`), since `$` is illegal in Kotlin identifiers.
- Types come from the multiplatform kotlin-wrappers artifacts (`js.array.*`,
  `web.html.*`, …) and `kotlinx-browser` (`org.w3c.dom.*`,
  `org.khronos.webgl.*`), so a single declaration set compiles for both `js`
  and `wasmJs`. The wrappers BOM is pinned in `build.gradle.kts` (newer
  wrappers made the TypedArrays generic, which these declarations don't
  parameterise).
- wasmJs restrictions apply to every declaration: no nested objects in external
  interfaces, no `Any` at the interop boundary (use `JsAny`), `js(...)` bodies
  only as single-expression top-level functions (put helpers in
  `interop/JsInterop.kt`).

## History

Until 0.1.3 these files were generated at build time by
[Karakum](https://github.com/karakum-team/karakum) from the upstream d.ts plus
a curated patch overlay. The generated output was vendored and the pipeline
(npm/Karakum toolchain, d.ts overlay, post-generation patch script) retired —
hand-maintenance against `jsbindings.cpp` is more direct than patching a d.ts
that itself needs auditing against `jsbindings.cpp`.
