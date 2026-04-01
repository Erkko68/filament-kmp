# API Parity Batch Guide (Filament + Backend)

This guide captures the current parity gaps and proposes the next implementation batches.

## Audit Snapshot (2026-04-01)

### 1) File-Level Gaps

#### `filament` headers missing in C wrapper
- `filament/ColorSpace.h`
- `filament/Exposure.h`
- `filament/FilamentAPI.h`
- `filament/MaterialChunkType.h`
- `filament/MaterialEnums.h`
- `filament/Options.h`

#### `backend` headers missing in C wrapper
- `backend/platforms/` (entire folder not surfaced in C)
- `backend/README.md` (documentation only)

### 2) Major Partial-Parity Zones

These headers exist in C, but are still significantly narrower than upstream API surface:
- `c-wrapper/include/filament/Engine.h`
- `c-wrapper/include/filament/View.h`
- `c-wrapper/include/filament/Renderer.h`
- `c-wrapper/include/filament/Material.h`
- `c-wrapper/include/filament/MaterialInstance.h`
- `c-wrapper/include/filament/Texture.h`
- `c-wrapper/include/backend/Platform.h`
- `c-wrapper/include/backend/DriverEnums.h`

### 3) Notes
- `backend/platforms/*` is high-risk and platform-coupled; keep it as a late batch unless explicitly needed.
- `FilamentAPI.h` in C should likely be a compatibility/stub surface (not a direct C++ macro mirror).
- Continue excluding deprecated APIs unless needed for compatibility.

---

## Proposed Batch Roadmap

## Batch A: Missing Filament Header Surface (Low Risk, High Unlock)

**Goal**: Eliminate missing file-level gaps in `filament/` headers.

**Scope**
- Add C headers and bridge implementations for:
  - `ColorSpace`
  - `Exposure`
  - `MaterialEnums`
  - `MaterialChunkType`
  - `Options`
  - `FilamentAPI` (C-safe compatibility surface)

**Dependencies**
- None beyond existing type system.

**Tests**
- New module compile tests per new header.
- Extend smoke tests where static helpers are runtime-safe.

---

## Batch B: Engine Parity Expansion (Core Queries + Runtime Controls)

**Goal**: Expand `Engine` parity after stereo/config additions.

**Scope (non-deprecated only)**
- Add missing read-only query helpers and safe toggles first:
  - backend/support/feature-level getters
  - additional config readbacks
  - max-instance/count style queries as C helpers where practical
- Defer async command APIs until callback model is standardized.

**Dependencies**
- `backend/DriverEnums` and `backend/Platform` enums must remain aligned.

**Tests**
- Extend `c-wrapper/test/module/engine_test.c`.
- Extend `engine_scene_*` smoke programs with new engine queries.

---

## Batch C: View + Renderer Option Parity

**Goal**: Fill large parity gaps in rendering controls.

**Scope**
- `View`: stereoscopic options, post-process controls, quality knobs, dynamic resolution, render quality buckets.
- `Renderer`: frame pacing/query APIs that are deterministic in smoke mode.

**Dependencies**
- Batch B (engine capability/config readback) recommended first.
- `Options.h` from Batch A should be in place.

**Tests**
- Extend `view_test.c` and `renderer_test.c`.
- Add/expand integration smoke with readback assertions.

---

## Batch D: Material + MaterialInstance Full Parameter Surface

**Goal**: Bring parameter setting/getting APIs closer to parity.

**Scope**
- Add missing parameter overload families (scalar/vector/matrix/texture variants).
- Add enum-backed material property queries and blending/shading model exposure.
- Keep callback/async compile hooks for later unless required.

**Dependencies**
- Batch A (`MaterialEnums`, `MaterialChunkType`).

**Tests**
- Expand `material_test.c`.
- Expand material parameter smoke programs.

---

## Batch E: Texture + Sampler + Stream Advanced Paths

**Goal**: Complete practical texture upload/sampler/stream parity.

**Scope**
- Add missing `Texture`/`TextureSampler` controls and utility queries.
- Add stream/acquired image dependent paths that are deterministic.

**Dependencies**
- Backend descriptor and pixel descriptor parity should be stable first.

**Tests**
- Extend `texture_test.c`, `texture_sampler_test.c`, `stream_test.c`.
- Integration coverage in existing texture/stream smoke programs.

---

## Batch F: Scene/Light/Renderable Advanced Managers

**Goal**: Complete higher-level scene graph manager parity.

**Scope**
- Fill advanced builder/query/update gaps in:
  - `LightManager`
  - `RenderableManager`
  - `Scene`
  - `TransformManager` edge utilities

**Dependencies**
- Batch C/D for enum/options consistency.

**Tests**
- Expand module tests for each manager.
- Extend engine scene smoke suites.

---

## Batch G: Backend Platform + DriverEnums Completion

**Goal**: Close remaining backend gaps that impact engine/runtime behavior.

**Scope**
- Complete utility/query coverage in:
  - `backend/Platform.h`
  - `backend/DriverEnums.h`
- Keep APIs C-safe and backend-agnostic.

**Dependencies**
- Batches B/C should be mostly complete.

**Tests**
- Extend existing backend module tests.
- Add deterministic smoke checks where runtime backend supports readback.

---

## Batch H: Backend `platforms/*` (Optional / Late)

**Goal**: Decide whether platform-specific backend adapters should be exposed in C.

**Scope options**
1. **Defer indefinitely** (recommended default).
2. Expose a minimal subset for specific host platforms.
3. Full platform API parity (highest maintenance cost).

**Dependencies**
- Clear product requirement for platform-level customization.

**Tests**
- Platform-specific integration harnesses.

---

## Execution Rules for Every Batch

- Keep strict non-deprecated parity targets.
- Keep null/invalid guards in every C export.
- Prefer extending existing module/smoke tests before adding new binaries.
- Run and record:
  - `bash c-wrapper/test/test_module.sh`
  - `bash c-wrapper/test/test_integration.sh`

---

## Immediate Next Recommendation

Start with **Batch A** (missing headers) and then **Batch B** (Engine expansion). This gives the best unblock ratio for later View/Renderer/Material parity work.

