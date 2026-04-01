# Filament KMP C Wrapper - Batch Operating Guide

This document is the forward-looking execution standard for every API batch in this repository.
Use it as the source of truth for scope control, implementation order, quality gates, and handoff.

Related reference: `AGENT_FILAMENT_API_METHODOLOGY.md`.

---

## 1) Batch Mission

Each batch delivers a coherent, low-risk slice of Filament C++ API parity into the C ABI.

Every batch must:
- Preserve ABI predictability (`Fila*` naming and stable signatures).
- Follow ownership semantics used by Filament (especially `Engine`-owned objects).
- Include compile-only coverage and linked runtime validation.
- Leave evidence (tests, logs, and decisions) for future maintainers.

---

## 2) Definition of a Batch

A batch is a bounded unit of work that can be reviewed and shipped independently.

Required traits:
- Single theme (for example: one subsystem, one family of builders, or one feature path).
- Clear entry/exit criteria.
- Explicit risk list.
- Complete implementation-to-validation path in one pass.

Avoid mixing unrelated features in one batch.

---

## 3) Standard Procedure (End-to-End)

### Phase 0 - Intake and Scope Lock

1. Identify exact upstream targets in `filament-prebuilts/include/filament/`.
2. List symbols to add/extend (types, enums, functions).
3. Mark non-goals for this batch.
4. Record dependency constraints (if any) on earlier batches.

Exit criteria:
- Scope list exists and is frozen for this iteration.
- Risks and assumptions are written.

### Phase 1 - API Design for C ABI

1. Define `Fila*` symbol names and overload suffix strategy.
2. Confirm ownership and lifetime behavior.
3. Define null/invalid input behavior and return defaults.
4. Validate enum and struct mappings for ABI safety.

Exit criteria:
- Public API shape is final before implementation starts.

### Phase 2 - Header Work

1. Add or update public headers under `c-wrapper/include/filament/`.
2. Keep opaque handles in `Types.h` where needed.
3. Ensure declarations are minimal, consistent, and C-safe.

Exit criteria:
- Header compiles cleanly in isolation via module compile tests.

### Phase 3 - Bridge Implementation

1. Implement adapters in `c-wrapper/src/filament/`.
2. Add conversion logic (entity, instance, matrix) per methodology.
3. Add defensive guards in every exported function.
4. Avoid C++ exceptions crossing C ABI boundaries.

Exit criteria:
- New symbols resolve and behavior is guarded.

### Phase 4 - Build Integration

1. Register new sources in `c-wrapper/CMakeLists.txt`.
2. Register tests in the test CMake files.
3. Confirm no accidental platform-specific breakage.

Exit criteria:
- Build graph includes all new files and test targets.

### Phase 5 - Test Coverage

1. Module compile tests: header usability and includes.
2. Signature compile tests: lock function pointer ABI.
3. Linked integration smoke: runtime behavior and cleanup order.

Exit criteria:
- All three test layers exist for the new API surface.

### Phase 6 - Validation Run

Run the standard scripts:

```bash
bash c-wrapper/test/test_module.sh
bash c-wrapper/test/test_integration.sh
```

If linked tests are unavailable locally, note it explicitly and record what was run.

Exit criteria:
- Validation results are captured with pass/fail and any skipped conditions.

### Phase 7 - Review and Merge Readiness

1. Verify naming, ownership, guards, and test completeness.
2. Confirm no unintended ABI drift.
3. Confirm docs and artifacts are updated.

Exit criteria:
- Batch is releasable without follow-up fixes for core behavior.

### Phase 8 - Handoff

1. Publish batch record (scope, files, tests, risks, decisions).
2. Add next-batch seeds (what is now unblocked and recommended).
3. Track deferred work separately from completed scope.

Exit criteria:
- Another engineer can continue from artifacts only.

---

## 4) Quality Gates (Must Pass)

Gate 1: Scope Gate
- Exact symbols, ownership model, and non-goals documented.

Gate 2: ABI Gate
- Naming, signatures, and type mappings stable and test-locked.

Gate 3: Safety Gate
- Null/invalid guards present; default-safe return behavior defined.

Gate 4: Build Gate
- CMake wiring complete for sources and tests.

Gate 5: Runtime Gate
- Linked smoke path validates functionality and cleanup order.

Gate 6: Evidence Gate
- Batch evidence pack is complete and attached.

---

## 5) Required Artifacts Per Batch

Minimum artifact set:
- Updated headers in `c-wrapper/include/filament/`.
- Updated bridges in `c-wrapper/src/filament/`.
- CMake registration changes.
- Module compile tests.
- Signature compile tests.
- Linked integration test(s) or explicit rationale if deferred.
- Batch decision record (scope decisions and tradeoffs).
- Validation evidence (what command ran, what passed, what was skipped).

---

## 6) Risk Management Rules

- Keep each batch small enough to bisect quickly.
- Prefer low-risk parity slices before advanced callback or descriptor surfaces.
- Do not mix behavior changes with broad refactors in the same batch.
- If a risk cannot be tested in linked mode, mark as open risk and document mitigation.

---

## 7) Forward Execution Cadence

For all upcoming batches, follow this cadence:

1. Plan and scope lock.
2. Implement headers and bridges.
3. Wire build and tests.
4. Run validation scripts.
5. Publish evidence and next-batch recommendations.

This cadence is mandatory unless explicitly overridden in the batch decision record.

---

## 8) Batch Template (Copy for New Batch)

Use this template for every new batch:

```markdown
# Batch <ID>: <Title>

## Goal
- <one-paragraph mission>

## Scope
- In scope:
  - <symbol/module>
- Out of scope:
  - <deferred item>

## Upstream Mapping
- Source headers:
  - <filament-prebuilts/include/...>

## API Decisions
- Naming:
  - <Fila symbol rules>
- Ownership:
  - <engine-owned vs caller-owned>
- Safety defaults:
  - <null/invalid behavior>

## File Changes
- Headers:
  - <path>
- Sources:
  - <path>
- Build files:
  - <path>

## Tests
- Module compile:
  - <path>
- Signature compile:
  - <path>
- Linked integration:
  - <path>

## Validation Evidence
- Commands run:
  - `bash c-wrapper/test/test_module.sh`
  - `bash c-wrapper/test/test_integration.sh`
- Results:
  - <pass/fail/skip with reason>

## Risks and Mitigations
- Risk:
  - <description>
- Mitigation:
  - <action>

## Exit Criteria Checklist
- [ ] Scope gate passed
- [ ] ABI gate passed
- [ ] Safety gate passed
- [ ] Build gate passed
- [ ] Runtime gate passed
- [ ] Evidence gate passed

## Next Batch Seeds
- <what is now unblocked>
```

---

## 9) Working Rule

If there is a conflict between convenience and ABI safety, choose ABI safety.
