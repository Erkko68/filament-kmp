# Native C Interop Mapping Batches

This checklist tracks Kotlin/Native `actual` mappings to the C wrapper ABI.

## Batch 0 - Interop Contract Lock

- [ ] `kotlin/filament-kmp-api/src/nativeInterop/cinterop/filament.def`
  - Confirm all required headers are listed and ordered.
  - Add linker/compiler opts if needed for local/native CI usage.
- [ ] `kotlin/filament-kmp-api/build.gradle.kts`
  - Verify cinterop task setup, include paths, static/shared library linkage.
- [ ] `c-wrapper/CMakeLists.txt`
  - Ensure all currently referenced symbols are exported by the built wrapper.

## Batch 1 - Engine and Core Lifecycle (start here)

- [ ] `kotlin/filament-kmp-api/src/nativeMain/kotlin/dev/filament/kmp/Engine.native.kt`
  - Map backend + feature-level getters/setters.
  - Map automatic instancing toggles.
  - Map config readback + stereoscopic limits.
  - Map flush / flushAndWait / pause / unprotected.
  - Map feature flag get/set/exists.
  - Keep destroy + invalidation policy consistent with wrappers.
- [ ] `kotlin/filament-kmp-api/src/nativeMain/kotlin/dev/filament/kmp/SwapChain.native.kt`
  - Wire native handle validity + object pointer.
  - Support headless and native-window creation paths from `Engine`.
- [ ] `kotlin/filament-kmp-api/src/nativeMain/kotlin/dev/filament/kmp/Renderer.native.kt`
  - Wire begin/end frame + render path + native object handle.
- [ ] `kotlin/filament-kmp-api/src/nativeMain/kotlin/dev/filament/kmp/View.native.kt`
  - Start with native handle validity and essential view binding methods.
- [ ] `kotlin/filament-kmp-api/src/nativeMain/kotlin/dev/filament/kmp/Scene.native.kt`
  - Wire entity add/remove + skybox/IBL references.

## Batch 2 - Managers and Entity Components

- [ ] `kotlin/filament-kmp-api/src/nativeMain/kotlin/dev/filament/kmp/EntityManager.native.kt`
- [ ] `kotlin/filament-kmp-api/src/nativeMain/kotlin/dev/filament/kmp/TransformManager.native.kt`
- [ ] `kotlin/filament-kmp-api/src/nativeMain/kotlin/dev/filament/kmp/RenderableManager.native.kt`
- [ ] `kotlin/filament-kmp-api/src/nativeMain/kotlin/dev/filament/kmp/LightManager.native.kt`
- [ ] `kotlin/filament-kmp-api/src/nativeMain/kotlin/dev/filament/kmp/Camera.native.kt`

## Batch 3 - GPU Resources and Materials

- [ ] `kotlin/filament-kmp-api/src/nativeMain/kotlin/dev/filament/kmp/BufferObject.native.kt`
- [ ] `kotlin/filament-kmp-api/src/nativeMain/kotlin/dev/filament/kmp/IndexBuffer.native.kt`
- [ ] `kotlin/filament-kmp-api/src/nativeMain/kotlin/dev/filament/kmp/VertexBuffer.native.kt`
- [ ] `kotlin/filament-kmp-api/src/nativeMain/kotlin/dev/filament/kmp/SkinningBuffer.native.kt`
- [ ] `kotlin/filament-kmp-api/src/nativeMain/kotlin/dev/filament/kmp/MorphTargetBuffer.native.kt`
- [ ] `kotlin/filament-kmp-api/src/nativeMain/kotlin/dev/filament/kmp/Material.native.kt`
- [ ] `kotlin/filament-kmp-api/src/nativeMain/kotlin/dev/filament/kmp/MaterialInstance.native.kt`
- [ ] `kotlin/filament-kmp-api/src/nativeMain/kotlin/dev/filament/kmp/Texture.native.kt`
- [ ] `kotlin/filament-kmp-api/src/nativeMain/kotlin/dev/filament/kmp/TextureSampler.native.kt`
- [ ] `kotlin/filament-kmp-api/src/nativeMain/kotlin/dev/filament/kmp/RenderTarget.native.kt`
- [ ] `kotlin/filament-kmp-api/src/nativeMain/kotlin/dev/filament/kmp/Stream.native.kt`
- [ ] `kotlin/filament-kmp-api/src/nativeMain/kotlin/dev/filament/kmp/Fence.native.kt`

## Batch 4 - Lighting, Post, and Utility Types

- [ ] `kotlin/filament-kmp-api/src/nativeMain/kotlin/dev/filament/kmp/IndirectLight.native.kt`
- [ ] `kotlin/filament-kmp-api/src/nativeMain/kotlin/dev/filament/kmp/Skybox.native.kt`
- [ ] `kotlin/filament-kmp-api/src/nativeMain/kotlin/dev/filament/kmp/ColorGrading.native.kt`
- [ ] `kotlin/filament-kmp-api/src/nativeMain/kotlin/dev/filament/kmp/ToneMapper.native.kt`
- [ ] `kotlin/filament-kmp-api/src/nativeMain/kotlin/dev/filament/kmp/Colors.native.kt`
- [ ] `kotlin/filament-kmp-api/src/nativeMain/kotlin/dev/filament/kmp/Box.native.kt`
- [ ] `kotlin/filament-kmp-api/src/nativeMain/kotlin/dev/filament/kmp/SurfaceOrientation.native.kt`
- [ ] `kotlin/filament-kmp-api/src/nativeMain/kotlin/dev/filament/kmp/MathUtils.native.kt`
- [ ] `kotlin/filament-kmp-api/src/nativeMain/kotlin/dev/filament/kmp/Viewport.native.kt`

## ABI sources we should touch in parallel when needed

- `c-wrapper/include/filament/Engine.h`
- `c-wrapper/src/filament/Engine.cpp`
- `c-wrapper/include/filament/Renderer.h`
- `c-wrapper/src/filament/Renderer.cpp`
- `c-wrapper/include/filament/SwapChain.h`
- `c-wrapper/src/filament/SwapChain.cpp`
- `c-wrapper/include/filament/Scene.h`
- `c-wrapper/src/filament/Scene.cpp`
- `c-wrapper/include/filament/View.h`
- `c-wrapper/src/filament/View.cpp`

## Current first target

`Engine.native.kt` (Batch 1) - implement low-risk direct calls first:

1. `getBackend`
2. `getSupportedFeatureLevel`
3. `getActiveFeatureLevel`
4. `setActiveFeatureLevel`
5. `setAutomaticInstancingEnabled`
6. `isAutomaticInstancingEnabled`
7. `getMaxStereoscopicEyes`
8. `getSteadyClockTimeNano`

