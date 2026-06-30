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
    SunLight(direction = Direction(0.3f, -1f, -0.5f))
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
    SunLight(direction = Direction(0.3f, -1f, -0.5f))
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

## Vector types

`Position`, `Direction`, `Scale`, and `Color` are distinct immutable data classes (not
`typealias`es for `Float3`). Being distinct, the compiler stops you passing a `Color` where a
`Position` is expected; being **immutable**, they're stable Compose inputs — passing them to scene
composables doesn't trigger the needless recompositions a mutable `Float3` would.

Construct them directly (`Position(x, y, z)`, `Color(r, g, b)`, `Position(0f)` for uniform), read
components (`.x/.y/.z`, and `.r/.g/.b` for `Color`), and use the common operators (`+`, `-`,
`* scalar`) in-domain. To cross into filament-utils `Float3` vector math (cross, dot, swizzles),
hop with the `Position(float3)` constructors, `toFloat3()`, or `Float3.toPosition()` /
`toDirection()` / `toScale()` / `toColor()` — needed only for that advanced math.

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

`GltfInstance` offers four layers of animation control, from declarative to fully manual.

### 1. Hoisted playback with `rememberAnimationState` (recommended)

`rememberAnimationState` returns an observable, **auto-advancing** clock for one glTF `Animator`.
Pass it as `animationState` and it plays every frame and loops at the clip length — no scene clock
or `animationTime` plumbing:

```kotlin
val animation = rememberAnimationState(animationIndex = 0)
GltfInstance(asset = character, animationState = animation)
```

You can read it back during composition — `animation.time`, `animation.progress` and
`animation.isTransitioning` are snapshot state — and tweak `speed`, `loop`, and `crossFadeDuration`
live. Set `animation.isPaused = true` to freeze playback, or `animation.seek(seconds)` to scrub.

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
a time. For a held, multi-clip blend (rather than a one-shot transition), use the mixer below.

### 3. Multi-track mixer (blend trees)

Add tracks with `rememberAnimationTrack` to hold and blend several clips at once by `weight` — a
blend tree. While any tracks are present they drive playback and `animationIndex` is ignored. Each
track keeps its own `time`/`speed`/`loop` and exposes read-only `progress` and `isFinished`:

```kotlin
val animation = rememberAnimationState(animationIndex = null)

// Declare one track per clip; drive the weight from a parameter — e.g. movement speed 0..1:
rememberAnimationTrack(animation, walkIndex, weight = 1f - moveSpeed)
rememberAnimationTrack(animation, runIndex,  weight = moveSpeed)
```

Each call registers a track for as long as it stays in composition and removes it on the way out —
no manual cleanup. It returns the `AnimationTrack` if you need to read `progress`/`isFinished` or
call `seek`. Weights are normalized internally, so they need not sum to 1; a track at weight 0 keeps
its clock running so it's already in phase when you blend it back in.

Prefer names over magic indices with `rememberAnimationNames(asset)`, which returns the clip names
by index once the asset is ready — `names.indexOf("Walk")`.

**Driving the mixer from a game loop.** The blend engine is `AnimationMixer`, a plain
(non-`@Composable`) object — `AnimationState` just wraps one as `animation.mixer`, and
`rememberAnimationTrack` is declarative sugar over `mixer.addTrack`. For a game that runs its own
animation state machine *outside* composition, hold a mixer with `rememberAnimationMixer()`, add
tracks imperatively, and drive it from `OnFrame` with the instance's `Animator` — no per-clip
composables, no coupling of your animation graph to the composition tree:

```kotlin
val mixer = rememberAnimationMixer()
val walk = remember { mixer.addTrack(walkIndex) }
val run  = remember { mixer.addTrack(runIndex, weight = 0f) }
var animator by remember { mutableStateOf<Animator?>(null) }

GltfInstance(asset = character, onCreate = { animator = instance.getAnimator() })

OnFrame { frame ->
    walk.weight = 1f - moveSpeed; run.weight = moveSpeed   // computed by your game logic
    animator?.let { mixer.apply(it, frame.deltaSeconds) }
}
```

`mixer.apply(animator, dt)` advances every track and pushes the blended pose. Set `mixer.isPaused =
true` to freeze, `mixer.removeTrack`/`clearTracks` to tear down. This is the path for a "serious"
game: clips are just indices (hundreds cost nothing until sampled), and only the 2–4 tracks you
actually blend at any instant cost anything.

Filament blends the **whole skeleton**, so per-bone masks and additive layers (e.g. wave with the
upper body while the legs keep walking) are *not* expressible through either mixer surface — reach
for the raw `Animator` in layer 4 if you need them.

### 4. Manual control and morph targets

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

## Lights

Lights are declared with **typed composables** inside `rememberFilamentScene { }` —
`DirectionalLight`, `SunLight`, `PointLight`, `SpotLight`, and `FocusedSpotLight` — each exposing only
the parameters that light type actually uses. `Light(type = …)` remains as a low-level escape hatch
over the raw `LightManager.Builder`.

Shadow casting is opt-in per light via `shadow = ShadowConfig(...)` (`null` disables it):

```kotlin
DirectionalLight(
    direction = Direction(0.3f, -1f, -0.5f),
    intensity = 100_000f,                  // lux
    shadow    = ShadowConfig(mapSize = 4096),
)
```

Only **directional, spot, and focused-spot** lights can cast shadows — **point lights cannot** (a
shadow map is rendered from a single projection, which a point light's 360° emission has no
equivalent for). For a shadow-casting omnidirectional-style light, use a wide-cone `FocusedSpotLight`.
See [Shadows](#shadows) for the per-light `ShadowConfig` vs. view-wide technique split.

## Light channels and intensity units

Every light composable exposes two parts of Filament's light model beyond the basics:

- **`lightChannels`** — the set of channels (0–7) a light affects. A renderable is only lit by a
  light if they share an enabled channel (channel 0 is the default for both). Use this to make a
  light illuminate only some objects — e.g. a UI/preview light that ignores the rest of the scene.
- **`intensityUnit` + `efficiency`** — interpret `intensity` as luminous power/illuminance
  (`LightUnit.LUMINOUS_POWER`, the default), luminous intensity (`LightUnit.CANDELA`), or electrical
  wattage (`LightUnit.WATTS`, scaled by `efficiency` — e.g. `0.087` for an LED). Lets you dial lights
  in physical units instead of guessing lumen values.

```kotlin
FocusedSpotLight(
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
They also take `castShadows`/`receiveShadows` (both default `true`) — set `castShadows = false` on a
pure ground/receiver `Plane` to avoid it shadowing itself. When wrapped in a `Group { }` the
primitive's transform becomes local to the group. `Mesh` is the escape hatch for custom triangle
geometry the built-in primitives don't cover.

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

For the common cases, the **built-in standard materials** need no `.mat` authoring, no `matc`, and no
asset shipping (they work on Web too): `rememberColorMaterialInstance`,
`rememberUnlitColorMaterialInstance`, `rememberTexturedMaterialInstance`, and
`rememberEmissiveMaterialInstance` each return a ready `MaterialInstance` for a primitive.

```kotlin
Cube(material = rememberColorMaterialInstance(Color(0.9f, 0.25f, 0.3f)))
```

For custom materials, the loaders (`rememberMaterial`, `rememberMaterialInstance`, `rememberTexture`)
all return `null` while loading and on failure rather than throwing inside composition — pass
`onError` to react. Their `engine` defaults to the engine in scope from `rememberFilamentScene`; pass
it explicitly to allocate the resource *outside* a scene (e.g. when sharing assets across multiple
scenes, or loading before rendering starts):

```kotlin
val engine = rememberFilamentEngine()
val mat    = rememberMaterial(engine) { Res.readBytes("files/materials/lit_color.filamat") }
val duck   = rememberGltfAsset(engine) { Res.readBytes("files/models/Duck.glb") }

val scene = rememberFilamentScene(engine = engine) {
    GltfInstance(asset = duck, ...)
}
```

The keyed `rememberMaterialInstance(material, key) { … }` overload re-applies parameters declaratively
on change — see [Materials](materials.md#updating-parameters-live).

### Post-Processing

Post-processing is configured *by value*, not as composables: build a `PostProcessing` and pass it to `FilamentView`'s `postProcessing` parameter. Each effect is a singleton value class — a `null` field leaves Filament's native default (effect off), a non-null field enables and configures it. Re-applied automatically whenever the value changes, so animating an effect is just passing a new value.

```kotlin
FilamentView(
    scene = scene,
    cameraState = cam,
    postProcessing = PostProcessing(
        bloom        = Bloom(strength = 0.2f),
        antiAliasing = AntiAliasing(fxaaEnabled = true),
    ),
)
```

The available effect value classes — `Bloom`, `Vignette`, `Fog`, `AmbientOcclusion`,
`AntiAliasing`, `ScreenSpaceReflections`, `ColorGrade`, `DepthOfField`,
`DynamicResolution`, `Dithering`, `RenderQuality` — and their fields are documented in the
**[API reference](https://erkko68.github.io/filament-kmp/api/)**.

### Shadows

Shadows are a render setting, not post-processing, so they're a `FilamentView` parameter of their
own. The `shadows` parameter selects the *view-wide technique* (`null` disables shadowing entirely):

```kotlin
FilamentView(
    scene = scene,
    cameraState = cam,
    shadows = Shadows.Pcss(),   // soft shadows; or Shadows.Pcf (default), Vsm, Dpcf, or null to disable
)
```

Per-light shadow-map quality (resolution, bias, cascades, penumbra size) is set separately via each
light's `shadow = ShadowConfig(...)`. Soft shadows need both halves: a `Shadows.Pcss`/`Shadows.Dpcf`
technique on the view *and* a `ShadowConfig.bulbRadius` on the casting light.
