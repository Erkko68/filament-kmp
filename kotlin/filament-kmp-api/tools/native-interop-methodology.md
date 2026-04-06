# Native Interop Methodology (Safe, Incremental)

This document defines how we implement Kotlin/Native interop safely in very small batches.

## Goals

- Keep behavior aligned with Java API as source of truth.
- Prevent expect/actual drift.
- Prevent C wrapper and cinterop symbol drift.
- Land changes in 1-2 method batches with immediate verification.

## Batch Size Rule

- Implement only **1-2 methods per batch**.
- Do not start the next batch until current batch is verified.

## Source of Truth Order

For each method, always validate in this order:

1. Java API definition (`filament-main/android/filament-android/.../*.java`)
2. KMP `expect` (`commonMain`)
3. Android `actual` (`androidMain`)
4. Native `actual` (`nativeMain`)
5. C wrapper header/source (`c-wrapper/include`, `c-wrapper/src`)
6. cinterop exposure (`src/nativeInterop/cinterop/filament.def`)

## Standard Workflow (per batch)

1. Pick 1-2 methods and register them in `native-cinterop-batches.md` as `in-progress`.
2. Verify Java method exists and capture semantics (params, return, lifecycle behavior, nullable rules).
3. Confirm `expect` signature and docs parity requirements.
4. Confirm Android `actual` behavior for the same method.
5. Implement/update Native `actual`.
6. If symbol missing, add C wrapper declaration + implementation.
7. Ensure symbol is reachable from cinterop (`filament.def` and generated bindings).
8. Run compile checks.
9. Mark method done only if all checks pass.

## Per-Method Checklist

Copy this block for every method:

```md
### Method: <Class>.<methodName>
- [ ] Java method exists and behavior reviewed
- [ ] `expect` signature matches Java-facing API
- [ ] Android `actual` parity verified
- [ ] Native `actual` implemented
- [ ] C wrapper symbol exists in header
- [ ] C wrapper symbol implemented in source
- [ ] cinterop exposes symbol
- [ ] lifecycle/invalidation behavior matches class policy
- [ ] compile check passes
- [ ] batch tracker updated
```

## Validation Gates

Minimum required after each batch:

1. Kotlin native compile gate
2. No new expect/actual mismatches
3. No unresolved cinterop symbols
4. No lifecycle regression in destroy/invalidate paths for touched classes

## Recommended Command Gate

```zsh
cd /Users/eric/IdeaProjects/filament-kmp-core/kotlin
./gradlew --no-daemon :filament-kmp-api:compileKotlinMacosArm64
```

Use additional checks when needed:

```zsh
cd /Users/eric/IdeaProjects/filament-kmp-core/kotlin
./gradlew --no-daemon :filament-kmp-api:compileNativeMainKotlinMetadata
```

## Lifecycle Safety Policy

- Native wrapper classes own typed `CPointer<...>?` handles.
- `getNativeObject(): Long` is only a parity bridge, not primary internal state.
- `destroy*` must invalidate wrapper state immediately after native destroy.
- Any method on destroyed wrapper must fail fast or return safe false/null per class policy.

## Rollback Rule

Rollback entire batch if any of the following occurs:

- expect/actual mismatch introduced
- C symbol compiles but behavior diverges from Java semantics
- cinterop symbol unresolved on supported native target
- lifecycle invalidation policy violated

## Batch Tracking States

Use these status values in `native-cinterop-batches.md`:

- `[ ]` not started
- `[~]` partial
- `[x]` fully validated
- `[!]` blocked (note reason)

## Definition of Done (per method)

A method is done only when:

- implementation exists in native actual,
- required C/cinterop path exists,
- compile gate passes,
- tracker is updated,
- and method semantics match Java + Android behavior.

