# `:web` — Web runtime (the C API compiled to wasm)

Builds the C API ([`c/`](../c)) with Emscripten, loads it, and gives the `js` and `wasmJs` targets its
`Fila*` exports. No embind, no upstream `filament.js`.

Published as **`io.github.erkko68.filament:web`** and pulled in by every `:kotlin:*` web target. The
`.js`/`.wasm` files are **not** in the klib (webpack never sees klib resources); apps take them from the
GitHub release (see [getting started](../docs/getting-started.md)).

| File | Contents |
| :--- | :--- |
| `filament-kmp.{js,wasm}` | filament + gltfio + filament-utils in one instance (they share `Engine` pointers). Loaded by `Filament.initJs`. |
| `filamat-kmp.{js,wasm}` | The runtime material compiler, separate and optional (~6.4 MB). Loaded by `Filamat.initJs`. Linked with a 4 MB stack: glslang overflows the 64 KB default. |

## Build

- **`cmakeBuild`** builds [`CMakeLists.txt`](CMakeLists.txt) (added by `c/CMakeLists.txt` for `wasm`) with the
  Emscripten SDK's toolchain file. Its inputs: `:setupEmsdk` (emsdk into `.emsdk/`, `emsdkVersion` in
  `gradle.properties`), `:prebuilts_wasm` (Filament's wasm libraries, built from source once per
  `filaVersion` since upstream ships none), and `:generateBindings` (each runtime's export list and type tables,
  from the common externals).
- **`stageFilamentWasm` / `stageFilamatWasm`** copy the outputs to `build/filamentWasm` and `build/filamatWasm`
  for the tests, the samples and the release assets. `FILA_WASM_PREBUILT=<dir>` stages an already built
  runtime instead (CI's js/wasm jobs).

## How common code reaches the wasm

The API classes in `commonMain` declare `external fun FilaX(...)` next to them (see
[Native Bindings](../docs/bindings.md)). On js and wasmJs such a top-level external resolves to the global
`FilaX`: [`src/wasm/fila-globals.js`](src/wasm/fila-globals.js), linked in with `--post-js`, installs every
`_FilaX` export there once the runtime is up, fixing up what js can't pass as-is (C `bool` → boolean, `float`
→ shortest f32 decimal, 64-bit args → BigInt) from the generated type tables.

The wasm is single-threaded: fence and `flushAndWait` timeouts are clamped to 0, and anything that waits on
a fence internally (`Stream`) cannot work.

## Tests

`ExportParityTest` checks that every common external is exported with the arity Kotlin declares; `F32Test`
covers the f32 round trip. The Karma bootstrap
([`gradle/karma/filament-karma-bootstrap.js`](../gradle/karma/filament-karma-bootstrap.js)) instantiates the
modules before any Kotlin test runs.

```sh
./gradlew :web:jsTest :web:wasmJsTest
```
