# Filament Android API -> KMP expect/actual migration

This document tracks the proceeding for the 1:1 migration from
`filament-main/android/filament-android/src/main/java/com/google/android/filament` into KMP.

## Scope

- Include API classes from `com.google.android.filament`.
- Exclude Android-specific platform internals (`AndroidPlatform`, `AndroidPlatform21`, `Platform`, `NativeSurface`, `android/*`, `proguard/*`).
- Keep Android comments/Javadocs when converting.
- Keep `jsMain` and `nativeMain` as TODO actuals when implementation is not ready.
- `Entity` / `EntityInstance` are now shared common annotations (not expect/actual) to keep usage stable across all targets.

## Step 1 - Baseline (already implemented)

Implemented classes:
- `Box`
- `Engine`
- `Renderer`
- `Scene`
- `View`

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
- `ToneMapper`
- `TransformManager`
- `VertexBuffer`

## Next batch (auto-continue)

Planned next classes:
- `SwapChain`
- `RenderTarget`
- `Texture`
- `BufferObject`

