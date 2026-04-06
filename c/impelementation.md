# Implementation Plan - Phase 2: Managers and Rendering Components

This phase continues the 1:1 mirroring of the Filament JNI source of truth into the new clean C-wrapper structure.

## Proposed Changes

### [NEW] Component Conversion (Batch 2: Managers)
- **RenderableManager**: `c/filament/c/RenderableManager.h`, `c/filament/cpp/RenderableManager.cpp`
    - Mapping Builder with all geometry and material variants.
    - Instance methods for bounding boxes, layers, and shadow settings.
- **TransformManager**: `c/filament/c/TransformManager.h`, `c/filament/cpp/TransformManager.cpp`
    - High-performance transform updates.
- **LightManager**: `c/filament/c/LightManager.h`, `c/filament/cpp/LightManager.cpp`
    - Builder for all light types (directional, point, etc.).

### [NEW] Component Conversion (Batch 3: Rendering Pipeline)
- **Renderer**: `c/filament/c/Renderer.h`, `c/filament/cpp/Renderer.cpp`
    - Rendering, display info, and readPixels.
- **View**: `c/filament/c/View.h`, `c/filament/cpp/View.cpp`
    - Extensive options mapping (Bloom, TAA, etc.).
- **Scene**: `c/filament/c/Scene.h`, `c/filament/cpp/Scene.cpp`
- **SwapChain**: `c/filament/c/SwapChain.h`, `c/filament/cpp/SwapChain.cpp`

### [MODIFY] Build System
- Update `c/CMakeLists.txt` to include all new files.

## Verification Plan

### Automated Tests
- Verification via successful compilation of the new `c/` directory against the Filament distribution.

### Manual Verification
- Visual audit of converted files against their JNI source of truth.
