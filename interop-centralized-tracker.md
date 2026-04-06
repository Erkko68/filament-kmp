# Native Interop Single Source of Truth

This is the only tracking and process document for:

- C wrapper implementation strategy (`c-wrapper`)
- `expect/actual` parity strategy (KMP API)
- verification scripts and Gradle tasks
- per-file progress tracking
- deprecated-method registry

## 1) Core Rules

- Work in micro-batches: **1-2 methods per batch**.
- Before coding a batch, update this file (`status = in-progress`).
- After coding, run all required verifications, then update this file (`done`/`blocked`).
- Do not start next batch until current batch is fully verified.

## 2) API Parity Rules (Java -> Expect/Actual)

For each method, this order is mandatory:

1. Java source of truth (`filament-main/android/filament-android/src/main/java/...`)
2. `expect` in `commonMain`
3. Android `actual` in `androidMain`
4. Native `actual` in `nativeMain`
5. C wrapper header + source if native symbol is missing
6. cinterop binding availability from `filament.def`

Comment/doc policy:

- `expect` keeps Java parity comments/docs.
- `actual` implementations do not need copied Java comments.
- Behavior must still match Java and Android semantics.

## 3) C Wrapper Implementation Strategy

When a native method needs a symbol that does not exist:

1. Add declaration in `c-wrapper/include/...`.
2. Add implementation in `c-wrapper/src/...`.
3. Ensure null/invalid guards in C wrapper (`if (!ptr) return ...`).
4. Keep conversions explicit (enum mapping switch, pointer casts).
5. Rebuild and run `c-wrapper` tests.

Native handle strategy:

- Wrapper classes store typed `CPointer<FilaX>?`.
- `getNativeObject(): Long` is edge/parity only.
- `destroy*` must immediately invalidate wrapper handle.

## 4) Deprecated Method Policy

- Deprecated methods are not active implementation targets.
- If encountered, mark as `deprecated-removed` in tables below.
- Keep a registry entry with source (Java/C++ deprecation marker).

## 5) Mandatory Verification Gates

Required after every batch:

1. Kotlin native compile (`compileKotlinMacosArm64`)
2. Kotlin metadata compile (`compileNativeMainKotlinMetadata`)
3. No new expect/actual mismatches
4. cinterop symbols resolve for touched methods
5. C wrapper header tests pass
6. C wrapper functionality tests pass (when impacted)
7. C wrapper parity audit reviewed for touched headers

## 6) Verification Commands

Kotlin/native:

```zsh
cd /Users/eric/IdeaProjects/filament-kmp-core/kotlin
./gradlew --no-daemon :filament-kmp-api:compileKotlinMacosArm64
./gradlew --no-daemon :filament-kmp-api:compileNativeMainKotlinMetadata
```

C wrapper tests:

```zsh
cd /Users/eric/IdeaProjects/filament-kmp-core/c-wrapper
./test/test_headers.sh
./test/test_functionality.sh
```

C wrapper parity audit:

```zsh
python3 /Users/eric/IdeaProjects/filament-kmp-core/c-wrapper/tools/filament_parity_audit.py
```

## 7) Batch Checklist (Use Per Method)

```md
### Method: <Class>.<methodName>
- [ ] Java method exists and behavior reviewed
- [ ] expect signature parity confirmed
- [ ] Android actual parity confirmed
- [ ] Native actual implemented
- [ ] C wrapper header symbol exists
- [ ] C wrapper source symbol exists
- [ ] cinterop symbol available
- [ ] lifecycle/invalidation rules preserved
- [ ] Kotlin compile gates pass
- [ ] c-wrapper tests pass
- [ ] tracker updated
```

## 8) Progress Table (Global Reset Baseline)

All rows are intentionally reset to zero baseline.

| File | Methods scoped | In progress | Done | Blocked | Deprecated removed |
|---|---:|---:|---:|---:|---:|
| `Engine.native.kt` | 0 | 0 | 0 | 0 | 0 |
| `SwapChain.native.kt` | 0 | 0 | 0 | 0 | 0 |
| `Renderer.native.kt` | 0 | 0 | 0 | 0 | 0 |
| `View.native.kt` | 0 | 0 | 0 | 0 | 0 |
| `Scene.native.kt` | 0 | 0 | 0 | 0 | 0 |
| `Camera.native.kt` | 0 | 0 | 0 | 0 | 0 |
| `EntityManager.native.kt` | 0 | 0 | 0 | 0 | 0 |
| `TransformManager.native.kt` | 0 | 0 | 0 | 0 | 0 |
| `RenderableManager.native.kt` | 0 | 0 | 0 | 0 | 0 |
| `LightManager.native.kt` | 0 | 0 | 0 | 0 | 0 |
| `BufferObject.native.kt` | 0 | 0 | 0 | 0 | 0 |
| `IndexBuffer.native.kt` | 0 | 0 | 0 | 0 | 0 |
| `VertexBuffer.native.kt` | 0 | 0 | 0 | 0 | 0 |
| `SkinningBuffer.native.kt` | 0 | 0 | 0 | 0 | 0 |
| `MorphTargetBuffer.native.kt` | 0 | 0 | 0 | 0 | 0 |
| `Material.native.kt` | 0 | 0 | 0 | 0 | 0 |
| `MaterialInstance.native.kt` | 0 | 0 | 0 | 0 | 0 |
| `Texture.native.kt` | 0 | 0 | 0 | 0 | 0 |
| `TextureSampler.native.kt` | 0 | 0 | 0 | 0 | 0 |
| `RenderTarget.native.kt` | 0 | 0 | 0 | 0 | 0 |
| `Stream.native.kt` | 0 | 0 | 0 | 0 | 0 |
| `Fence.native.kt` | 0 | 0 | 0 | 0 | 0 |
| `IndirectLight.native.kt` | 0 | 0 | 0 | 0 | 0 |
| `Skybox.native.kt` | 0 | 0 | 0 | 0 | 0 |
| `ColorGrading.native.kt` | 0 | 0 | 0 | 0 | 0 |
| `ToneMapper.native.kt` | 0 | 0 | 0 | 0 | 0 |
| `Colors.native.kt` | 0 | 0 | 0 | 0 | 0 |
| `Box.native.kt` | 0 | 0 | 0 | 0 | 0 |
| `SurfaceOrientation.native.kt` | 0 | 0 | 0 | 0 | 0 |
| `MathUtils.native.kt` | 0 | 0 | 0 | 0 | 0 |
| `Viewport.native.kt` | 0 | 0 | 0 | 0 | 0 |
| `NativeSurface.native.kt` | 0 | 0 | 0 | 0 | 0 |
| `Platform.native.kt` | 0 | 0 | 0 | 0 | 0 |
| `Filament.native.kt` | 0 | 0 | 0 | 0 | 0 |

## 9) Active Batch Log

Use this section for current 1-2 method batch only.

| Batch date | File | Methods | Status | Notes |
|---|---|---|---|---|
| _pending_ | - | - | - | - |

## 10) Deprecated Registry

| File | Method | Deprecation source | Action |
|---|---|---|---|
| _none yet_ | - | - | - |

## 11) How to Update This File After Each Batch

1. Increment `Methods scoped` for touched file(s).
2. Set `In progress` to method count at batch start.
3. Move counts from `In progress` to `Done` or `Blocked` after verification.
4. If deprecated method found, increment `Deprecated removed` and add registry row.
5. Add one line to `Active Batch Log` with result and blockers.

