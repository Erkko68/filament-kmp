# Filament Compose Documentation

The `filament-compose` module provides the integration between the [Filament](https://github.com/google/filament) rendering engine and **Compose Multiplatform**.

## Overview

- **[Scope & Philosophy](scope.md)**: Understand the goals and design principles behind `filament-compose`.
- **[Integration Strategies](integration-strategies.md)**: Details on the mechanism used to bridge Filament's GPU output with the Compose canvas (Pixel Readback vs. Zero-Copy).

## Lifecycle and resource management

The Compose DSL manages Filament resource lifetimes through `DisposableEffect`:

- `rememberFilamentEngine` — destroys the `Engine` when leaving composition.
- `FilamentView` — destroys its `Renderer`, `Scene`, `View`, and `Camera`.
- `rememberGltfAsset` — destroys the loaded asset.
- `GltfInstance` — removes its entities from the scene.

If you create raw Filament objects through `FilamentEffect`, you are responsible for destroying them:

```kotlin
FilamentEffect { engine, _, _ ->
    val mat = Material.Builder().payload(bytes, bytes.size).build(engine)
    onDispose { engine.destroyMaterial(mat) }
}
```

Forgetting to destroy Filament objects leaks GPU memory until the `Engine` itself is destroyed.

## Component Reference

### Core

| Composable | Description |
| :--- | :--- |
| `FilamentView` | Main entry point. Creates an Engine, Scene, View, and Renderer; accepts hoisted state for camera, skybox, and IBL; renders into a platform surface. |
| `FilamentEffect` | Escape hatch for raw Filament access. Provides the Engine, Scene, View, and a per-frame callback. |
| `rememberFilamentEngine` | Creates and remembers a shared `Engine` instance tied to the composition lifecycle. Pass the result to multiple `FilamentView`s to share a single GPU context. |

### Camera

| Composable | Description |
| :--- | :--- |
| `rememberCameraState` | Generic hoisted camera state (position, target, projection). Pass to `FilamentView`'s `cameraState` parameter. |
| `rememberOrbitCameraState` | Orbit/turntable camera controller. Pair with `Modifier.orbitGestures`. |
| `rememberMapCameraState` | Top-down map camera controller. Pair with `Modifier.mapGestures`. |
| `rememberFlightCameraState` | Free-flight camera controller. Pair with `Modifier.flightGestures` and `FlightCameraLoop`. |
| `FlightCameraLoop` | Drives the flight camera animation each frame. |
| `Modifier.orbitGestures` | Attaches touch/mouse gestures for orbit camera control. |
| `Modifier.mapGestures` | Attaches touch/mouse gestures for map camera control. |
| `Modifier.flightGestures` | Attaches touch/mouse gestures for flight camera control. |
| `Modifier.pickOnTap` | Issues a Filament picking query on tap and delivers the result. |

### Scene Objects

| Composable | Description |
| :--- | :--- |
| `Light` | Adds a light entity to the scene. Supports all Filament light types (directional, sun, point, spot, focused spot). |
| `GltfInstance` | Declarative glTF model instance. Requires a `GltfAsset` loaded with `rememberGltfAsset`. |
| `rememberGltfAsset(bytes)` | Loads a glTF asset synchronously from a `ByteArray`. |
| `rememberGltfAsset(key, load)` | Loads a glTF asset asynchronously via a suspending lambda. Returns `null` while loading. |

### Environment

| Composable | Description |
| :--- | :--- |
| `rememberSkyboxState` | Creates hoisted skybox state (KTX environment or solid color). Pass to `FilamentView`'s `skyboxState` parameter. |
| `rememberIndirectLightState` | Creates hoisted IBL state (KTX radiance, irradiance bands, intensity). Pass to `FilamentView`'s `indirectLightState` parameter. |

### Materials & Textures

| Composable | Description |
| :--- | :--- |
| `rememberMaterial` | Loads a compiled Filament material from a `ByteArray` and remembers it. |
| `rememberMaterialInstance` | Creates and remembers a `MaterialInstance` from a `Material`. |
| `rememberTexture` | Uploads a texture to the GPU and remembers it. |

### Post-Processing

All post-processing composables must be placed inside the `content` lambda of `FilamentView`. They configure the Filament `View` options and are automatically re-applied on recomposition.

| Composable | Description |
| :--- | :--- |
| `Bloom` | Bloom / glow effect. |
| `Vignette` | Edge darkening vignette. |
| `Fog` | Volumetric fog. |
| `AmbientOcclusion` | Screen-space ambient occlusion (SSAO). |
| `AntiAliasing` | Anti-aliasing (FXAA or MSAA). |
| `ScreenSpaceReflections` | Screen-space reflections (SSR). |
| `ColorGrade` | Color grading (exposure, white balance, curves, …). |
| `ToneMapping` | Tone mapping operator selection. |
| `DepthOfField` | Depth-of-field blur. |
| `Shadows` | Shadow rendering options. |
| `DynamicResolution` | Dynamic resolution scaling. |
