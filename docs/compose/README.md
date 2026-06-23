# Filament Compose Documentation

The `filament-compose` module provides the integration between the [Filament](https://github.com/google/filament) rendering engine and **Compose Multiplatform**.

## Overview

- **[Scope & Philosophy](scope.md)**: Understand the goals and design principles behind `filament-compose`.
- **[Integration Strategies](integration-strategies.md)**: How Filament's GPU output reaches the Compose canvas on each platform (native surface, web offscreen+blit, or pixel readback), plus the per-platform layering & stacking limitations.
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

## Driving updates

Continuous updates fall into **two different clocks** — confusing them is the most common source
of "why does this run too often / not often enough":

- **Per frame** — once per display refresh. Independent of Compose state; keeps running at the
  display's refresh rate. This is what you want for animation and continuous motion.
- **Per recomposition** — whenever the Compose state a block reads changes. Could be many times a
  frame, or not for seconds. This is for *syncing* values, not for time-based animation.

### `OnFrame` — the per-frame primitive

```kotlin
OnFrame { frame ->
    angle += frame.deltaSeconds * speed   // runs once per refresh, no recomposition
}
```

`OnFrame` runs its callback once per display refresh and hands you a `FrameInfo`
(`frameTimeNanos`, `deltaSeconds` — clamped against stalls — and `elapsedSeconds`). It does **not**
recompose. **Everything else per-frame is built on it:**

| Helper | Built on `OnFrame`; reach for it when… |
| :-- | :-- |
| `rememberAnimationState` | Playing/blending glTF skeletal animation — the high-level path. Don't hand-roll the timing. |
| `rememberSceneClock()` | You want elapsed **seconds as a `State<Float>`** to read in composition (orbit a `Group`, pulse a value). Reading it recomposes every frame — that's its whole point, and the one case you *want* a frame to recompose. |
| `FilamentEffect { onFrame { … } }` | Per-frame work from inside a `rememberFilamentScene` escape hatch, with the `engine`/`scene` in scope. The callback gets the same `FrameInfo`. |
| `rememberFlightCameraState` | Free-flight camera; it advances itself every frame (no separate loop composable needed). |

And the per-**recomposition** siblings, for completeness:

| Helper | Clock | When |
| :-- | :-- | :-- |
| `GltfInstance.onUpdate { … }` | Per recomposition | Syncing imperative glTF state (materials, bones) to Compose state. **Not** a frame loop. |
| `GltfInstance.onCreate { … }` | Once | One-time setup when the instance enters the scene. |

**Picking one:** animating a glTF → `rememberAnimationState`; elapsed time as a value in
composition → `rememberSceneClock`; any other per-frame side effect → `OnFrame` (or
`FilamentEffect`'s `onFrame` inside a scene); reacting to *state* changes rather than the clock →
`onUpdate`.

## Animating glTF models

`GltfInstance` offers three layers of animation control, from declarative to fully manual.

### 1. Hoisted playback with `rememberAnimationState` (recommended)

`rememberAnimationState` returns an observable, **auto-advancing** clock for one glTF `Animator`.
Pass it as `animationState` and it plays every frame and loops at the clip length — no scene clock
or `animationTime` plumbing:

```kotlin
val animation = rememberAnimationState(animationIndex = 0)
GltfInstance(asset = character, animationState = animation)
```

You can read it back during composition — `animation.time` and `animation.isTransitioning` are
snapshot state — and tweak `speed`, `loop`, and `crossFadeDuration` live.

### 2. Cross-fading between clips

Assigning a new `animationIndex` **cross-fades** from the outgoing clip to the new one over
`crossFadeDuration` seconds. This is the idiomatic "idle → walk → run" transition: just drive the
target index from your own state and the blend happens automatically.

```kotlin
val animation = rememberAnimationState(animationIndex = idle, crossFadeDuration = 0.25f)
GltfInstance(asset = character, animationState = animation)

Button(onClick = { animation.animationIndex = if (animation.animationIndex == idle) walk else idle }) {
    Text(if (animation.isTransitioning) "Blending…" else "Toggle")
}
```

Under the hood this uses Filament's `Animator.applyCrossFade`, which blends exactly **two** clips at
a time (the incoming clip plus the one it is fading from). That covers the common case; an arbitrary
N-track weighted mixer is out of scope for the hoisted state — drop to layer 3 for that.

### 3. Manual control and morph targets

For full control, drive the clip yourself with `animationIndex` + `animationTime` (e.g. fed from
`rememberSceneClock`), or reach the raw `Animator` through `GltfInstance`'s `onUpdate` escape hatch
and call `applyAnimation`/`applyCrossFade`/`updateBoneMatrices` directly — useful for custom
N-clip blending, event-driven scrubbing, or syncing playback to gameplay.

Vertex **morph targets** (blend shapes — facial expressions, etc.) are driven declaratively via
the `morphWeights` parameter, which is applied to every renderable in the instance that has morph
targets:

```kotlin
GltfInstance(asset = face, morphWeights = floatArrayOf(smile, blink, /* … */))
```

## Cameras that follow the scene graph

`CameraState` is normally hoisted state you set imperatively. To make a camera **follow an
entity** — a chase cam behind a car, a first-person view from a character's head, a camera bolted
to a moving rig — place a `CameraNode` *inside* the `Group` you want to track. Each frame it reads
that group's world transform and writes the driven `CameraState`'s `eye`/`target`/`up`, so the
camera inherits every translation and rotation of the group declaratively:

```kotlin
val cam = rememberCameraState()
val scene = rememberFilamentScene {
    Group(position = carPosition, rotation = carRotation) {
        GltfInstance(car)
        // Eye 6 units behind and 2 up, looking at the car's centre — all in the group's local space.
        CameraNode(cam, eyeOffset = Position(0f, 2f, -6f), targetOffset = Position(0f, 1f, 0f))
    }
}
FilamentView(scene, cameraState = cam)
```

The camera object still belongs to the `FilamentView` you pass `cam` to; `CameraNode` only drives
the state. The offsets are expressed in the group's local space.

## Rendering to a texture

`rememberRenderTarget` renders a scene **off-screen** through its own camera into a sampleable
`Texture` — the building block for mini-maps, in-world monitors/CCTV screens, portals, and live
thumbnails. It owns a private `View`/`Camera`/`Renderer` and redraws every frame via Filament's
`Renderer.renderStandaloneView`, independent of any on-screen `FilamentView`. Feed the result back
into a material like any other texture:

```kotlin
val scene  = rememberFilamentScene { /* world */ }
val mapCam = rememberCameraState(eye = Position(0f, 40f, 0f), target = Position(0f))
val mapTex = rememberRenderTarget(scene, mapCam, width = 256, height = 256)

val screen = rememberMaterialInstance(screenMaterial)
mapTex?.let { screen.setParameter("screen", it, TextureSampler()) }
Plane(material = screen)   // a surface displaying the off-screen render
```

Post-processing is **off by default**: the target carries a depth attachment, and Filament ignores
depth attachments when post-processing runs. Enable it only when you don't rely on the depth buffer.
The texture is `null` for a non-positive size.

## Light channels and intensity units

`Light` exposes two parts of Filament's light model beyond the basics:

- **`lightChannels`** — the set of channels (0–7) a light affects. A renderable is only lit by a
  light if they share an enabled channel (channel 0 is the default for both). Use this to make a
  light illuminate only some objects — e.g. a UI/preview light that ignores the rest of the scene.
- **`intensityUnit` + `efficiency`** — interpret `intensity` as luminous power/illuminance
  (`LightUnit.LUMINOUS_POWER`, the default), luminous intensity (`LightUnit.CANDELA`), or electrical
  wattage (`LightUnit.WATTS`, scaled by `efficiency` — e.g. `0.087` for an LED). Lets you dial lights
  in physical units instead of guessing lumen values.

```kotlin
Light(
    type          = LightManager.Type.FOCUSED_SPOT,
    intensity     = 12f,                 // a 12 W bulb…
    intensityUnit = LightUnit.WATTS,
    efficiency    = 0.087f,              // …at LED efficiency
    lightChannels = setOf(0, 2),        // only objects on channel 0 or 2
)
```

## Component Reference

The full, always-current list of composables, parameters, and types lives in the generated
**[API reference](https://erkko68.github.io/filament-kmp/api/)** (Dokka/KDoc). This section
covers only the conceptual notes that don't fit on a single declaration — the *why* and the
cross-cutting patterns. For *what each composable is*, follow the API reference.

### Primitives

Pure-Kotlin mesh primitives (`Cube`, `Sphere`, `Cylinder`, `Plane`, `Mesh`) build a
`VertexBuffer`/`IndexBuffer` and a single-primitive renderable internally. Place them inside
`rememberFilamentScene { }`. Every primitive accepts the same transform set — `position`,
`rotation`, `scale`, `pivot` — plus an `onCreate: (entity: Int) -> Unit` callback that fires once
the renderable is added to the scene (use it to register the entity with `view.pick` callbacks).
When wrapped in a `Group { }` the primitive's transform becomes local to the group. `Mesh` is the
escape hatch for custom triangle geometry the built-in primitives don't cover.

### Environment

`rememberKTXEnvironment` is the one-call path when you have a KTX IBL/skybox pair (e.g. from Filament's `cmgen`):

```kotlin
val engine = rememberFilamentEngine()          // shared, so the IBL and scene agree
val env = rememberKTXEnvironment(
    engine,
    ibl    = { Res.readBytes("files/environment/env_ibl.ktx") },
    skybox = { Res.readBytes("files/environment/env_skybox.ktx") },  // optional
)
val scene = rememberFilamentScene(
    engine = engine,
    skyboxState = env.skyboxState,
    indirectLightState = env.indirectLightState,
) {
    GltfInstance(asset = duck)                  // lit by the environment — no Light needed
}
```

### Materials & Textures

The loaders (`rememberMaterial`, `rememberMaterialInstance`, `rememberTexture`) all return `null`
while loading and on failure rather than throwing inside composition — pass `onError` to react.
Their `engine` defaults to the engine in scope from `rememberFilamentScene`; pass it explicitly to
allocate the resource *outside* a scene (e.g. when sharing assets across multiple scenes, or
loading before rendering starts):

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

The available effect value classes — `Bloom`, `Vignette`, `Fog`, `AmbientOcclusion`,
`AntiAliasing`, `ScreenSpaceReflections`, `ColorGrade`, `DepthOfField`, `Shadows`,
`DynamicResolution`, `Dithering`, `RenderQuality` — and their fields are documented in the
**[API reference](https://erkko68.github.io/filament-kmp/api/)**.
