# API Parity Roadmap (Demo-Driven)

This document defines the next implementation plan to reach a clear product goal:

> Build and run a C-header Filament program that renders visible geometry with lighting and shadows,
> and keep extending parity from that baseline.

Updated: 2026-04-01

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

1. Execute Batch A header additions in the exact order above.
2. Add/expand the demo functionality program for geometry + lighting + shadows.
3. Begin Batch B engine query expansion only as needed to remove demo blockers.

