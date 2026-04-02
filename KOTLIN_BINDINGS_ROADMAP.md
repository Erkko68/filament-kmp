# Kotlin Bindings Roadmap (Filament C Wrapper)

Updated: 2026-04-02

## Objective

Build a Kotlin Multiplatform library on top of the C wrapper with a solid base for:

- Scene creation and rendering workflows
- Material, lighting, camera, and resource management
- glTF model loading
- Practical runtime utilities

This roadmap assumes `backend/platforms/*` stays deferred.

## Current Decisions

- Treat parity audit as heuristic and filtered (false positives + deprecated APIs excluded).
- Focus parity work on user-facing Filament core and glTF flows.
- Keep pointer-based callbacks for C compatibility, but add token-based callback APIs (`uintptr_t`) for Kotlin/Native safety.

## Progress Snapshot (2026-04-02)

- Completed: token callback variants for descriptor/sync pathways (M1 foundation).
- Completed: `View` picking bridge APIs with Kotlin-friendly token callback options (M2 core).
- Completed: first M3 glTF parity slice:
  - `FilamentAsset`: scene-filtered entity injection + asset instance enumeration.
  - `AssetLoader`: name manager + material provider accessors.
- Completed: second M3 glTF parity slice:
  - `NodeManager` wrapper (component/instance/extras/scene-membership core APIs).
  - `AssetLoader`: `getNodeManager` accessor.
- Completed: fixture-gated deterministic glTF load/unload smoke (`FILA_TEST_GLTF_GLB`).
- Next focus: transform-node oriented surfaces and broader glTF runtime assertions with real fixtures.

## Milestones

## M1 - Kotlin-Safe ABI Foundation

Goal: remove major interop friction before broad Kotlin API generation.

- Add token callback variants for callback-heavy entry points used by Kotlin.
- Keep null-safe, deterministic return contracts (`false`/`0`/`NULL`).
- Ensure compile coverage for both pointer and token callback variants.

Exit criteria:

- Token callback support present for descriptor and sync callback paths.
- Header test suite remains green.

## M2 - View / Scene Interaction Completeness

Goal: complete high-value interaction APIs needed by app-level tooling and editors.

- `View` picking APIs bridged with C-friendly result structs.
- Fill remaining practical `View` controls used in Kotlin-facing scene code.
- Add runtime checks for null-safety and basic scheduling behavior.

Exit criteria:

- Picking callable from C ABI with pointer + token callback options.
- `View` functionality tests remain deterministic and green.

## M3 - glTF Workflow Readiness

Goal: ensure model loading flows are usable end-to-end from Kotlin.

- Audit and fill gaps in `gltfio/*` wrappers needed for:
  - asset creation
  - resource loading
  - instance/animator orchestration
- Add focused runtime tests for lifecycle and null-safety.

Exit criteria:

- At least one deterministic glTF load/unload smoke path validated through C wrappers.
- API shape is stable enough for Kotlin wrappers without per-platform special casing.

## M4 - Kotlin API Layer v1

Goal: expose a coherent Kotlin API over stable C ABI primitives.

- Generate/author Kotlin Native bindings for core + glTF paths.
- Encapsulate native handles with clear ownership lifecycle (`close`/`dispose` semantics).
- Keep callback interop token-first in Kotlin public APIs.

Exit criteria:

- Kotlin sample can create engine, scene, camera, light, renderable, and load a glTF asset.
- Basic rendering loop and teardown run without leaks/crashes in target environments.

## M5 - Quality and Parity Hardening

Goal: improve confidence and reduce maintenance risk.

- Expand parity-guided closure for remaining high-impact non-deprecated APIs.
- Add CI automation for header + functionality test lanes.
- Track deferred domains explicitly (not blockers for v1 unless product requirements change).

Exit criteria:

- Stable release candidate for Kotlin wrapper with documented supported surface.
- Known deferred APIs listed with rationale and planned follow-up phases.

## Explicit Deferrals

- `backend/platforms/*` platform adapter wrappers
- Deprecated upstream APIs unless required for compatibility
- Internal/low-level utility APIs without direct product value

## Tracking

Use the parity report as triage input and maintain two views:

- `Actionable parity`: in-scope, non-deprecated, high-value API gaps
- `Deferred parity`: platform/internal/deprecated or low-value gaps

