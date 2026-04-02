# Filament C Wrapper Adaptation Methodology

This note documents how to adapt Filament C++ API surface into this project's C ABI in a consistent way.

## Goals

- Keep a predictable C ABI (`Fila*` types/functions) for Kotlin/Native and C consumers.
- Mirror Filament API names and ownership semantics as closely as practical.
- Add APIs incrementally with compile and runtime test coverage.
- Keep Kotlin/Native interop predictable by preferring tokenized callback userdata when possible.

## Scope Guidance (Current)

- In scope now:
  - Filament core rendering/runtime APIs needed for scene creation and graphics workflows.
  - glTF loading surfaces (`gltfio/*`) needed for model loading pipelines.
  - Practical utility APIs used directly by app/runtime flows.
- Deferred for now:
  - `backend/platforms/*` wrappers and platform adapter internals.
  - Low-level utilities that are better handled directly in Kotlin unless they unblock a concrete feature.

## Naming and API Shape

- Prefix all public C symbols with `Fila`.
- Keep object ownership where Filament keeps it:
  - `Engine` owns creation/destruction of engine-owned objects (`Renderer`, `View`, `Scene`, `SwapChain`, `Fence`, etc.).
- Use opaque handles in `include/filament/Types.h`:
  - `typedef struct FilaX FilaX;`
- For overloaded C++ APIs, use explicit suffixes in C:
  - Example: `setTransformMat4f`, `setTransformMat4`.
- For getters that can fail on invalid input, prefer `bool` + out-parameter.
- For callback userdata, provide a token variant (`uintptr_t`) in addition to pointer-based APIs.

## Header and Source Layout

For each new Filament module `X`:

1. Public C header in `include/filament/X.h`.
2. Bridge implementation in `c-wrapper/src/filament/X.cpp`.
3. Build registration in `c-wrapper/CMakeLists.txt`.
4. Tests:
   - module compile test in `c-wrapper/test/module/x/x_module_compile.c`
   - signature lock test in `c-wrapper/test/signature/x/x_signature_compile.c`
   - linked smoke test in `c-wrapper/test/integration/programs/...`

## Conversion Rules

- Entity conversion:
  - C `FilaEntity` <-> `utils::Entity` via `utils::Entity::import/smuggle`.
- Instance conversion:
  - map C integer instance IDs to Filament instance wrapper types.
- Matrix conversion:
  - treat arrays as column-major (`[16]`) to match Filament math constructors.

## Safety Rules

- Add null/invalid guards in every bridge function.
- Use default-safe return values on invalid input:
  - pointers -> `NULL`
  - `bool` -> `false`
  - numeric/count -> `0`
  - enum status -> error value
- Do not throw exceptions from C ABI paths.
- For callback-heavy APIs, keep both forms:
  - pointer userdata form for C compatibility.
  - token userdata form for Kotlin/Native friendliness.

## Testing Strategy

- Compile-only tests are mandatory for every added symbol.
- Signature tests lock function pointer types to prevent ABI drift.
- Linked runtime smoke tests validate behavior and cleanup order.
- Keep integration tests bounded in time and deterministic.
- For every callback-capable API addition, add compile coverage for both pointer and token callback variants.

## Runtime and Build Modes

- Default mode builds wrapper + compile-only checks.
- Linked integration tests require host prebuilts:
  - `FILA_ENABLE_LINKED_TESTS=ON`

## Incremental Workflow

For each API batch:

1. Add/extend public C header.
2. Implement bridge methods.
3. Update build files.
4. Add/extend module + signature tests.
5. Add/extend linked smoke test.
6. Run:
   - `bash c-wrapper/test/test_headers.sh`
   - `bash c-wrapper/test/test_functionality.sh`

## Parity Audit Policy

- Treat parity report output as a triage tool, not an ABI proof.
- Ignore upstream deprecated methods when computing actionable parity gaps.
- Maintain a small curated false-positive ignore map for noisy method tokens.
- Keep deferred header families (for example `backend/platforms/*`) out of actionable parity totals.

## Parity Policy

- Target practical 1:1 adaptation over time.
- Start with low-risk slices first (lifecycle, queries, basic mutators).
- Add complex overloads and advanced callbacks in follow-up batches.

