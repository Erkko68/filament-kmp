# Filament Compose Documentation

The `filament-compose` module provides the integration between the [Filament](https://github.com/google/filament) rendering engine and **Compose Multiplatform**.

## Overview

- **[Scope & Philosophy](scope.md)**: Understand the goals and design principles behind `filament-compose`.
- **[Integration Strategies](integration-strategies.md)**: Details on the mechanism used to bridge Filament's GPU output with the Compose canvas (Pixel Readback vs. Zero-Copy).
- **[Materials](materials.md)**: Authoring `.mat` source, compiling with `matc`, loading at runtime, parameterising per-instance, and when to use runtime `filamat` instead.

## Scene vs. View

The API separates *what* you render from *where* you render it, mirroring Filament's own model:

- **`rememberFilamentScene { }`** declares the world (lights, models, primitives, environment) and returns a `FilamentScene` **value**. Its content lambda holds scene composables only — it emits no UI and runs once regardless of how many views render it.
- **`FilamentView(scene = …)`** is a leaf composable: one viewport (camera + post-processing + platform surface) onto a scene. Its look is configured *by value* (`cameraState`, `postProcessing`). Place several `FilamentView`s to render one scene through different cameras.

```kotlin
val scene = rememberFilamentScene(skyboxState = sky) {
    Light(type = LightManager.Type.SUN, ...)
    GltfInstance(asset = duck)
}

Row {
    FilamentView(scene, Modifier.weight(1f), cameraState = cam1,
        postProcessing = PostProcessing(bloom = Bloom(strength = 0.2f)))
    FilamentView(scene, Modifier.weight(1f), cameraState = cam2)
}
```

For the common single-view case, **`FilamentSceneView`** collapses the two into one call — its content lambda is the scene declaration, and the viewport is configured by the same value parameters:

```kotlin
FilamentSceneView(
    modifier = Modifier.fillMaxSize(),
    cameraState = cam,
    skyboxState = sky,
    postProcessing = PostProcessing(bloom = Bloom(strength = 0.2f)),
) {
    Light(type = LightManager.Type.SUN, ...)
    GltfInstance(asset = duck)
}
```

## Lifecycle and resource management

The Compose DSL manages Filament resource lifetimes through `DisposableEffect`:

- `rememberFilamentEngine` — destroys the `Engine` when leaving composition.
- `rememberFilamentScene` — destroys its `Scene` (and the engine, if it created one).
- `FilamentView` — destroys its `Renderer`, `View`, and `Camera`.
- `rememberGltfAsset` — destroys the loaded asset.
- `GltfInstance` — removes its entities from the scene.

If you create raw Filament objects through `FilamentEffect` (inside `rememberFilamentScene`), you are responsible for destroying them. `engine` and `scene` are properties of the effect scope:

```kotlin
rememberFilamentScene {
    FilamentEffect {
        val mat = Material.Builder().payload(bytes, bytes.size).build(engine)
        onDispose { engine.destroyMaterial(mat) }
    }
}
```

Forgetting to destroy Filament objects leaks GPU memory until the `Engine` itself is destroyed.

## Component Reference

### Core

| Composable | Description |
| :--- | :--- |
| `FilamentSceneView` | All-in-one entry point for the single-view case: declares a scene and renders it through one viewport in a single call. Equivalent to `rememberFilamentScene { … }` feeding one `FilamentView`. |
| `rememberFilamentScene` | Declares the world and returns a `FilamentScene` value. Owns the `Scene`; accepts hoisted skybox and IBL state. Defaults to a dedicated engine, or pass a shared `rememberFilamentEngine`. Use with `FilamentView` for multiple views over one scene. |
| `FilamentView` | Renders a `FilamentScene` through one viewport. Owns a `View`, `Camera`, and `Renderer`; configured by value (`cameraState`, `postProcessing`, render flags) into a platform surface. |
| `rememberFilamentViewState` | Hoisted handle exposing a view's live `View`/`Renderer` and `pick()` for imperative access. |
| `FilamentEffect` | Escape hatch for raw Filament access inside `rememberFilamentScene`. Scope provides the `engine`, `scene`, and a per-frame callback. |
| `rememberFilamentEngine` | Creates and remembers a shared `Engine` instance tied to the composition lifecycle. Pass to `rememberFilamentScene` to share a single GPU context. |
| `rememberSceneClock` | Returns a `State<Float>` ticking every frame (seconds since first composition). Drives ambient animation loops without boilerplate. |

### Camera

| Composable | Description |
| :--- | :--- |
| `rememberCameraState` | Generic hoisted camera state (position, target, projection). Pass to `FilamentView`'s `cameraState` parameter. Drives — and reads back — the per-view camera. |
| `rememberOrbitCameraState` | Orbit/turntable camera controller. Pair with `Modifier.orbitGestures`. |
| `rememberMapCameraState` | Top-down map camera controller. Pair with `Modifier.mapGestures`. |
| `rememberFlightCameraState` | Free-flight camera controller. Pair with `Modifier.flightGestures` and `FlightCameraLoop`. |
| `FlightCameraLoop` | Drives the flight camera animation each frame. |
| `Modifier.orbitGestures` | Attaches touch/mouse gestures for orbit camera control. |
| `Modifier.mapGestures` | Attaches touch/mouse gestures for map camera control. |
| `Modifier.flightGestures` | Attaches touch/mouse gestures for flight camera control. |
| `Modifier.pickOnTap` | Issues a Filament picking query on tap and delivers the result. Takes a `rememberFilamentViewState`. |

### Scene Objects

| Composable | Description |
| :--- | :--- |
| `Light` | Adds a light entity to the scene. Supports all Filament light types (directional, sun, point, spot, focused spot). |
| `GltfInstance` | Declarative glTF model instance. Requires a `GltfAsset` loaded with `rememberGltfAsset`. Accepts `position` / `rotation` / `scale` / `pivot` (mesh-space point that rotation/scale revolve around — defaults to the glTF's root origin). |
| `rememberGltfAsset(engine?, key?, onError?, load)` | Loads a glTF asset asynchronously via a suspending lambda. Returns `null` while loading **and** on failure — never throws inside composition. Pass `onError` to react to a thrown `load` lambda or unparseable bytes. `engine` defaults to the engine in scope from `rememberFilamentScene`; pass it explicitly to load the asset *outside* a scene. |

### Grouping

| Composable | Description |
| :--- | :--- |
| `Group` | Parents nested scene composables under a single transform entity. `position`/`rotation`/`scale`/`pivot` apply to the whole assembly; children's own transforms become local to the group. Groups nest cleanly and accept `Light`, `GltfInstance`, and primitive children. |

### Primitives

Pure-Kotlin mesh primitives that build a `VertexBuffer`/`IndexBuffer` and a single-primitive renderable internally. Place inside `rememberFilamentScene { }`. Every primitive accepts the same transform set — `position`, `rotation`, `scale`, `pivot` — plus an `onCreate: (entity: Int) -> Unit` callback that fires once the renderable is added to the scene (use it to register the entity with `view.pick` callbacks). When wrapped in a `Group { }` the primitive's transform becomes local to the group.

| Composable | Description |
| :--- | :--- |
| `Cube` | Axis-aligned cube centered on the origin. Per-face vertices so each face has its own normal. |
| `Sphere` | UV sphere with configurable `rings` × `segments` subdivision. |
| `Cylinder` | Y-axis cylinder with side wall + top/bottom caps. |
| `Plane` | Flat quad in the XZ plane. Two-sided by default — lit correctly from above *and* below without needing a `doubleSided` material. |

### Environment

| Composable | Description |
| :--- | :--- |
| `rememberSkyboxState` | Creates hoisted skybox state (KTX environment or solid color). Pass to `rememberFilamentScene`'s `skyboxState` parameter. |
| `rememberIndirectLightState` | Creates hoisted IBL state (KTX radiance, irradiance bands, intensity). Pass to `rememberFilamentScene`'s `indirectLightState` parameter. |

### Materials & Textures

| Composable | Description |
| :--- | :--- |
| `rememberMaterial(engine?, key?, onError?, load)` | Loads a compiled `.filamat` payload asynchronously and remembers the resulting `Material`. Returns `null` while loading and on failure; pass `onError` to react to a thrown `load` lambda. |
| `rememberMaterialInstance(material, engine?)` | Creates and remembers a `MaterialInstance` from a `Material`. Destroys it when leaving composition. |
| `rememberTexture(engine?, type?, key?, onError?, load)` | Asynchronously loads image bytes (PNG/JPEG/KTX1 depending on platform) and uploads the texture. Returns `null` while loading and on failure; pass `onError` to react to a thrown `load` lambda or undecodable bytes. |

`engine` defaults to the engine in scope from `rememberFilamentScene`; pass it explicitly to allocate the resource *outside* a scene (e.g. when sharing assets across multiple scenes, or loading before rendering starts):

```kotlin
val engine = rememberFilamentEngine()
val mat    = rememberMaterial(engine) { Res.readBytes("files/materials/lit_color.filamat") }
val duck   = rememberGltfAsset(engine) { Res.readBytes("files/models/Duck.glb") }

val scene = rememberFilamentScene(engine = engine) {
    GltfInstance(asset = duck, ...)
}
```

### Post-Processing

Post-processing is configured *by value*, not as composables: build a `PostProcessing` and pass it to `FilamentView`'s `postProcessing` parameter. Each effect is a singleton value class — a `null` field leaves Filament's native default (effect off), a non-null field enables and configures it. Re-applied automatically whenever the value changes, so animating an effect is just passing a new value.

```kotlin
FilamentView(
    scene = scene,
    cameraState = cam,
    postProcessing = PostProcessing(
        bloom        = Bloom(strength = 0.2f),
        antiAliasing = AntiAliasing(fxaaEnabled = true),
        shadows      = Shadows(type = View.ShadowType.PCF),
    ),
)
```

| Value class | Description |
| :--- | :--- |
| `Bloom` | Bloom / glow effect. |
| `Vignette` | Edge darkening vignette. |
| `Fog` | Volumetric fog. |
| `AmbientOcclusion` | Screen-space ambient occlusion (SSAO). |
| `AntiAliasing` | Anti-aliasing (FXAA / MSAA / TAA). |
| `ScreenSpaceReflections` | Screen-space reflections (SSR). |
| `ColorGrade` | Color grading (exposure, white balance, curves, tone mapping, …). |
| `DepthOfField` | Depth-of-field blur. |
| `Shadows` | Shadow rendering options (algorithm, VSM, soft-shadow params). |
| `DynamicResolution` | Dynamic resolution scaling. |
| `Dithering` | Tonemap-time dithering. `TEMPORAL` (default) hides 8-bit banding in dark gradients and bloom halos. |
| `RenderQuality` | Precision of the View's HDR color buffer (`HIGH` = RGBA16F where available). Lower precision causes emissive values above 1.0 to clip into bloom and produce banding. |
