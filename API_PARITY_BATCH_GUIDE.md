# API Parity Roadmap (Demo-Driven)

This document defines the next implementation plan to reach a clear product goal:

> Build and run a C-header Filament program that renders visible geometry with lighting and shadows,
> and keep extending parity from that baseline.

Updated: 2026-04-02

---

## North-Star Outcome

### Target Demo

Create and maintain a runnable sample under `c-wrapper/test/functionality/` that proves:

1. Engine + renderer + view + scene are configured from C.
2. Geometry is visible (triangle/mesh path).
3. At least one light affects the scene.
4. Shadow casting/receiving behavior is enabled and exercised.
5. Program runs cleanly in CI-style test invocation.

### Demo Acceptance Criteria

- Demo builds with `./test_functionality.sh`.
- Demo exits with code `0`.
- No deprecated API dependencies required.
- No `backend/platforms/*` exposure required.

---

## Current Direction

- `Batch F` manager parity work is treated as functionally complete for current goals.
- `Batch G` utility/query work is substantially complete for backend-agnostic C usage.
- `backend/platforms/*` remains explicitly deferred unless a concrete platform adapter requirement appears.
- A recursive parity audit now exists in `c-wrapper/tools/filament_parity_report.md` and is the source of truth for new batch intake.

---

## Report-Driven Intake Rules (Effective Now)

- Ignore all `backend/*` additions for this phase, including `backend/platforms/*`.
- Prioritize modules that unlock reusable app/sample plumbing first (`utils/*`), then scene-quality runtime APIs.
- Treat callback-heavy APIs as deferred unless we explicitly standardize callback ABI patterns in C first.
- Prefer small vertical slices: header + bridge + header tests + deterministic functionality coverage.

---

## Batch H (Now): Utils Foundation (Easy / High Reuse)

### Goal

Add low-risk utility modules that improve internal bridge ergonomics and future sample scaffolding.

### Scope

- `utils/Entity.h`
- `utils/EntityInstance.h`
- `utils/Status.h`

### Exit Criteria

- New wrappers compile and expose stable C ABI names.
- Header compile tests added under `c-wrapper/test/headers/`.
- Existing functionality suite remains green.

---

## Batch I: Utils Hardening (Selective)

### Goal

Close practical utility gaps without entering callback/listener complexity.

### Scope

- Complete safe pull-style gaps in `utils/EntityManager.h`.
- Defer listener registration surfaces for now:
  - `registerListener`
  - `unregisterListener`
  - `onEntitiesDestroyed`

### Exit Criteria

- Deterministic entity-manager queries usable from C.
- No callback ABI introduced in this batch.

---

## Batch J: Scene Quality Runtime Gaps (Filament Core)

### Goal

Improve visual-scene controllability for sample parity (lighting/shadow quality controls).

### Scope

- `filament/View.h` high-impact non-callback gaps:
  - `setSoftShadowOptions` / `getSoftShadowOptions`
  - `setVsmShadowOptions` / `getVsmShadowOptions`
  - `setMaterialGlobal` / `getMaterialGlobal` (if low-risk to bridge)
- `filament/Skybox.h`:
  - `setColor`

### Exit Criteria

- Demo-track programs can control key shadow and AA knobs through C.
- Added coverage in `headers_view.c`, `headers_skybox.c`, and targeted functionality checks.

---

## Batch K: Geometry/Bounds Runtime Completeness

### Goal

Close geometry utility gaps that affect robust renderable handling and diagnostics.

### Scope

- `filament/RenderableManager.h`:
  - `computeAABB`-related parity where practical.
- `filament/Box.h`:
  - expose useful box helpers needed by runtime scene logic.

### Exit Criteria

- Bounding-volume related flows are available from C without custom C++ side helpers.

---

## Batch L: Async-Adjacent Readiness (Non-callback first)

### Goal

Add readiness/query APIs that are useful before full callback standardization.

### Scope

- `filament/IndexBuffer.h`: `isCreationComplete`-style readiness helpers.
- `filament/VertexBuffer.h`: buffer-object/readiness helpers where callback-free.
- `filament/Texture.h`: readiness/query helpers that do not force callback ABI.

### Deferred inside batch

- Async callback entrypoints (`setBufferAtAsync`-style) until callback ABI policy is locked.

### Exit Criteria

- C callers can gate usage on resource readiness with deterministic polling/query APIs.

---

## Batch A (Now): Missing Filament Header Surface

### Goal

Unblock higher-level API parity by adding missing low-risk filament header surfaces.

### Scope

Add C headers and bridges for:

- `filament/ColorSpace.h`
- `filament/Exposure.h`
- `filament/MaterialEnums.h`
- `filament/MaterialChunkType.h`
- `filament/Options.h`
- `filament/FilamentAPI.h` (C compatibility/stub only)

### Implementation Rules

- C-safe only; no C++-macro mirroring in public C headers.
- Non-deprecated surfaces first.
- Null/invalid guards on every exported function.

### Tests

- Add/extend compile coverage in `c-wrapper/test/headers/`.
- Add runtime checks only for deterministic helpers in `c-wrapper/test/functionality/`.

### Exit Criteria

- All missing Batch A headers exist in `c-wrapper/include/filament/`.
- Header tests compile cleanly.
- Any runtime-safe helpers added have functionality checks.

---

## Batch B (Next): Engine Parity Expansion

### Goal

Expand `Engine` read/query/control surface needed to support the demo and future View/Renderer work.

### Scope (non-deprecated)

Prioritize:

- Capability and feature-level readbacks.
- Additional deterministic config getters.
- Max-count / limit query helpers that are practical in C.

Defer:

- Async command/callback APIs until callback model is standardized.

### Tests

- Extend `c-wrapper/test/headers/headers_engine.c`.
- Extend existing engine functionality programs under `c-wrapper/test/functionality/`.

### Exit Criteria

- Engine query parity required by demo setup is available in C.
- Header and functionality suites remain green.

---

## Demo Track (Runs in Parallel)

### D0: Baseline Scene Program

Create/extend a dedicated functionality program to render a minimal lit/shadowed scene from C APIs.

Suggested path:

- `c-wrapper/test/functionality/functionality_engine_scene_lighting_shadows.c`

### D1: Stabilize Inputs

- Use already wrapped manager surfaces (`Scene`, `LightManager`, `RenderableManager`, `TransformManager`).
- Keep assets/procedural geometry deterministic.
- Keep startup and teardown explicit in one file.

### D2: Add Assertions

- Assert non-null manager retrieval.
- Assert entity/component creation success.
- Assert key runtime state queries (instance validity, component presence, light/shadow toggles).

### D3: Keep It CI-Friendly

- No interactive dependency.
- Fail fast with clear messages.
- Exit `0` on success.

---

## Explicit Deferrals

### Deferred by Default

- `backend/platforms/*` (platform-coupled adapters).
- Platform instance/lifecycle virtual API exposure.
- Deprecated upstream API parity.
- Cross-thread callback-heavy APIs until a shared C callback ABI pattern is finalized.

### When to Reopen

Only reopen if we have a concrete product requirement that cannot be met through backend-agnostic C APIs.

---

## Execution Cadence

For each slice:

1. Add/adjust C header declarations.
2. Implement bridge with guards.
3. Add header compile coverage.
4. Add deterministic functionality checks (if runtime-safe).
5. Run full suites and keep green.

Primary validation commands:

- `cd c-wrapper/test && ./test_headers.sh`
- `cd c-wrapper/test && ./test_functionality.sh`

---

## Immediate Next Steps

1. Execute `Batch H` (`utils/Entity.h`, `utils/EntityInstance.h`, `utils/Status.h`).
2. Execute `Batch I` selective `utils/EntityManager.h` completion (no listeners/callbacks).
3. Move to `Batch J` view/shadow runtime controls and update demo-track assertions.

