# `:web` — Web runtime (the C API compiled to wasm)

The web counterpart of [`:java`](../java/README.md). It builds the same C wrapper ([`c/`](../c))
with Emscripten and exposes it to the `js` and `wasmJs` targets through Kotlin externals generated
from the C headers. There is no embind and no upstream `filament.js`.

Published as **`io.github.erkko68.filament:web`** and pulled in transitively by every
`:kotlin:*` web target, so consumers never add it by hand. The `.js`/`.wasm` files are **not**
inside the klib (webpack never sees klib resources); apps download them from the GitHub release
(see [getting started](../docs/getting-started.md)).

## Outputs

| File | Contents |
| :--- | :--- |
| `filament-kmp.{js,wasm}` | filament + gltfio + filament-utils in one instance (they share `Engine` pointers). Loaded by `Filament.initJs`. |
| `filamat-kmp.{js,wasm}` | The runtime material compiler, separate and optional (~6.4 MB). Loaded by `Filamat.initJs`. Linked with a 4 MB stack: glslang overflows the 64 KB default. |

## What it does ([`build.gradle.kts`](build.gradle.kts))

1. **`setupEmsdk`** installs emsdk into `.emsdk/` ([`setup-emsdk.sh`](../scripts/dev/setup-emsdk.sh)),
   pinned to the version upstream Filament builds with.
2. **`generateWasmExternals`** ([`WasmExternals.kt`](../build-logic/src/main/kotlin/WasmExternals.kt))
   parses `c/*/c/*.h` with Emscripten's clang (AST + record layouts) and emits, per C module, an
   `external interface` of raw exports plus cinterop-named wrappers (`FilaEngine_createView(...)`),
   so `webMain` actuals read like `nativeMain` ones. Also enum constants and struct field offsets.
   `:kotlin:filamat` runs the same task for its own module.
3. **`buildFilamentWasm`** runs CMake with `-DFILAMENT_PLATFORM=wasm`, linking the wasm Filament
   libraries from `prebuilts/wasm/`. Upstream publishes none, so
   [`build-wasm-libs.sh`](../scripts/dev/build-wasm-libs.sh) builds them once per `filaVersion`.
4. **`stageFilamentWasm` / `stageFilamatWasm`** copy the outputs to `build/filamentWasm` and
   `build/filamatWasm` for tests, the samples and the release assets.

## Calling convention

- Pointers are wasm32 addresses (`Int`); C `bool` is `Int`; 64-bit integers are `JsBigInt`
  (use `toI64()`/`toKotlinLong()`: Kotlin/JS `Long.toJsBigInt()` is a no-op cast).
- Strings and arrays are copied into the wasm heap (`heapScoped`, `usePinned`). Heap views go
  stale when memory grows, so they are re-read on every access.
- If a C++ builder keeps a pointer until `build()` (Material `package`, `SurfaceOrientation`,
  skinning bones), the copy must live until then; `usePinned` frees it too early.
- Callbacks go through one `addFunction` trampoline per signature plus an id registry
  (`Callbacks.kt`).
- The wasm is single-threaded: fence and `flushAndWait` timeouts are clamped to 0, and anything
  that waits on a fence internally (`Stream`) cannot work.

## Tests

`ExportParityTest` checks that every declared `Fila*` function is exported with the right
arity; `ClearCanvasTest` renders a frame. The Karma bootstrap
([`gradle/karma/filament-karma-bootstrap.js`](../gradle/karma/filament-karma-bootstrap.js))
instantiates the modules before any Kotlin test runs.

```sh
scripts/dev/build-wasm-libs.sh          # once per filaVersion
./gradlew :web:jsTest :web:wasmJsTest
```
