# Filament Android API -> KMP expect/actual migration

This document tracks the proceeding for the 1:1 migration from
`filament-main/android/filament-android/src/main/java/com/google/android/filament` into KMP.

## Scope

- Include API classes from `com.google.android.filament`.
- Exclude Android-specific platform internals (`AndroidPlatform`, `AndroidPlatform21`, `Platform`, `NativeSurface`, `android/*`, `proguard/*`).
- Keep Android comments/Javadocs when converting.
- Keep `jsMain` and `nativeMain` as TODO actuals when implementation is not ready.
- `Entity` / `EntityInstance` are now shared common annotations (not expect/actual) to keep usage stable across all targets.
- `Engine` and `View` are currently sample-partial wrappers and will be expanded to full parity.
- New expect APIs preserve/adapt Android Java method comments.

## Step 1 - Baseline (already implemented)

Implemented classes:
- `Box`
- `Engine` (sample-partial)
- `Renderer`
- `Scene`
- `View` (sample-partial)

Missing classes:
- `BufferObject`
- `Camera`
- `ColorGrading`
- `Colors`
- `Entity`
- `EntityInstance`
- `EntityManager`
- `Filament`
- `IndexBuffer`
- `IndirectLight`
- `LightManager`
- `Material`
- `MaterialInstance`
- `MathUtils`
- `MorphTargetBuffer`
- `RenderTarget`
- `RenderableManager`
- `SkinningBuffer`
- `Skybox`
- `Stream`
- `SurfaceOrientation`
- `SwapChain`
- `SwapChainFlags`
- `Texture`
- `TextureSampler`
- `ToneMapper`
- `TransformManager`
- `VertexBuffer`
- `Viewport`

## Step 2 - Utility/value batch (in progress)

Implemented classes:
- `Viewport`
- `SwapChainFlags`
- `MathUtils`
- `Entity`
- `EntityInstance`

Missing classes:
- `BufferObject`
- `Camera`
- `ColorGrading`
- `Colors`
- `EntityManager`
- `Fence`
- `Filament`
- `IndexBuffer`
- `IndirectLight`
- `LightManager`
- `Material`
- `MaterialInstance`
- `MorphTargetBuffer`
- `RenderTarget`
- `RenderableManager`
- `SkinningBuffer`
- `Skybox`
- `Stream`
- `SurfaceOrientation`
- `SwapChain`
- `Texture`
- `TextureSampler`
- `ToneMapper`
- `TransformManager`
- `VertexBuffer`

## Step 3 - Core synchronization batch (auto-continued)

Implemented classes:
- `Fence`

Missing classes:
- `BufferObject`
- `Camera`
- `ColorGrading`
- `Colors`
- `EntityManager`
- `Filament`
- `IndexBuffer`
- `IndirectLight`
- `LightManager`
- `Material`
- `MaterialInstance`
- `MorphTargetBuffer`
- `RenderTarget`
- `RenderableManager`
- `SkinningBuffer`
- `Skybox`
- `Stream`
- `SurfaceOrientation`
- `SwapChain`
- `Texture`
- `TextureSampler`
- `ToneMapper`
- `TransformManager`
- `VertexBuffer`

## Step 4 - Utilities and entity lifecycle batch (auto-continued)

Implemented classes:
- `Colors`
- `EntityManager`
- `Filament`

Missing classes:
- `BufferObject`
- `Camera`
- `ColorGrading`
- `IndexBuffer`
- `IndirectLight`
- `LightManager`
- `Material`
- `MaterialInstance`
- `MorphTargetBuffer`
- `RenderTarget`
- `RenderableManager`
- `SkinningBuffer`
- `Skybox`
- `Stream`
- `SurfaceOrientation`
- `SwapChain`
- `Texture`
- `TextureSampler`
- `ToneMapper`
- `TransformManager`
- `VertexBuffer`

## Step 5 - Sampler batch (auto-continued)

Implemented classes:
- `TextureSampler`

Missing classes:
- `BufferObject`

## Step 6 - Surface and target batch (auto-continued)

Implemented classes:
- `SwapChain`
- `RenderTarget`
- `Texture`

Missing classes:
- `BufferObject`
- `Camera`
- `ColorGrading`
- `IndexBuffer`
- `IndirectLight`
- `LightManager`
- `Material`
- `MaterialInstance`
- `MorphTargetBuffer`
- `RenderableManager`
- `SkinningBuffer`
- `Skybox`
- `Stream`
- `SurfaceOrientation`
- `ToneMapper`
- `TransformManager`
- `VertexBuffer`

## Step 7 - Geometry buffer batch (auto-continued)

Implemented classes:
- `BufferObject`
- `IndexBuffer`
- `VertexBuffer`

Missing classes:
- `Camera`
- `ColorGrading`
- `IndirectLight`
- `LightManager`
- `Material`
- `MaterialInstance`
- `MorphTargetBuffer`
- `RenderableManager`
- `SkinningBuffer`
- `Skybox`
- `Stream`
- `SurfaceOrientation`
- `ToneMapper`
- `TransformManager`

## Step 8 - Material and transform-manager batch (auto-continued)

Implemented classes:
- `Material`
- `MaterialInstance`
- `RenderableManager`
- `TransformManager`

Missing classes:
- `Camera`
- `ColorGrading`
- `IndirectLight`
- `LightManager`
- `MorphTargetBuffer`
- `SkinningBuffer`
- `Skybox`
- `Stream`
- `SurfaceOrientation`
- `ToneMapper`

## Step 9 - Camera and environment lighting batch (auto-continued)

Implemented classes:
- `Camera`
- `LightManager`
- `IndirectLight`
- `Skybox`

Missing classes:
- `ColorGrading`
- `MorphTargetBuffer`
- `SkinningBuffer`
- `Stream`
- `SurfaceOrientation`
- `ToneMapper`

## Next batch (auto-continue)

Planned next classes:
- `MorphTargetBuffer`
- `SkinningBuffer`
- `Stream`
- `SurfaceOrientation`

## Step 10 - Color pipeline batch (auto-continued)

Implemented classes:
- `ColorGrading`
- `ToneMapper`

Missing classes:
- `MorphTargetBuffer`
- `SkinningBuffer`
- `Stream`
- `SurfaceOrientation`

## Step 11 - Buffer/stream/surface batch (auto-continued)

Implemented classes:
- `MorphTargetBuffer`
- `SkinningBuffer`
- `Stream`
- `SurfaceOrientation`

Missing classes:
- None

## Parity audit backlog (from java->expect script)

Source report:
- `kotlin/filament-kmp-api/tools/filament_java_expect_parity_report.md`

Priority classes with notable missing API surface:
- `Engine`
- `View`
- `Renderer`
- `Scene`
- `RenderableManager`
- `Material`
- `MaterialInstance`
- `Texture`
- `TransformManager`

Note:
- Internal helper APIs such as `invalidate()` are tracked as KMP lifecycle helpers and may appear as "only in expect" in parity reports.

## Step 12 - Scene and transform parity expansion (in progress)

Planned fixes:
- Expand `Scene` expect/actual to include Android public APIs from `Scene.java`.
- Expand `TransformManager` expect/actual to include accurate-translation, FP64 overloads, and local-transform transaction APIs.
- Keep strict Android Javadocs/KDocs parity for all updated expect declarations.

