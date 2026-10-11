# Migrating from 0.7

0.8.0 changes two parts of the `filament-compose` API: how a scene gets its background and environment
lighting, and how a `GltfInstance` is animated. The other modules are unaffected, and so is the Filament
version, so your `.filamat` files keep working.

The compiler finds almost every change. The two it does not find are marked **silent** below.

## Why

**The skybox and the environment lighting were the odd ones out.** The API has one rule for how you
change something: a value parameter when only your app writes it, a `remember*State` holder when the
framework writes it back (the camera, an animation). Nothing in the framework ever wrote to
`SkyboxState` or `IndirectLightState`, yet you had to create them, hoist them, and pass them to the scene
as parameters, while every light sat inside the scene as a composable. In 0.8 the background and the
lighting are scene content too: you declare them next to the lights and change them by recomposing.

**The environment loaders needed an engine no other loader needed.** They ran outside the scene, so you
had to hoist a `rememberFilamentEngine()` only to hand it to them. Now that an environment is declared
inside the scene, its loader takes the engine from the scene like `rememberGltfAsset` and
`rememberMaterial` do. The loaders are also renamed to the casing the rest of the module uses.

**`GltfInstance` had two animation inputs, and one silently lost.** `animationIndex`/`animationTime` were
ignored whenever `animationState` was set. There is now one input, `animationState`, and it covers what
the other two did.

## How

### 1. Move the skybox and lighting into the scene

Remove `skyboxState` / `indirectLightState` from `rememberFilamentScene` and `FilamentSceneView`, delete
the `remember*State` calls, and declare the same thing inside the scene:

```kotlin
// 0.7
val sky = rememberSkyboxState(SkyboxSource.Color(LinearColor(0.1f, 0.12f, 0.15f)))
FilamentSceneView(skyboxState = sky) { … }

// 0.8
FilamentSceneView {
    Skybox(color = LinearColor(0.1f, 0.12f, 0.15f))
    …
}
```

| 0.7 | 0.8 |
| :--- | :--- |
| `rememberSkyboxState(SkyboxSource.Color(c))` + `skyboxState =` | `Skybox(color = c)` in the scene |
| `SkyboxSource.Cubemap(texture)` | `Skybox(texture = texture)` |
| `rememberIndirectLightState(…)` + `indirectLightState =` | `IndirectLight(reflections, irradiance, intensity, rotation)` in the scene |
| `state.intensity = x`, `state.rotation = r` | pass `intensity = x`, `rotation = r`; hold them in your own state to change them |
| `rememberFilamentScene(engine, environment) { }` | `rememberFilamentScene { Environment(environment) }` |

A scene has one skybox and one indirect light, so declare each once. With two in the same scene the one
built last wins and a warning is logged.

### 2. Update the environment loaders

Rename them, move the call into the scene, and drop the hoisted engine:

```kotlin
// 0.7
val engine = rememberFilamentEngine()
val env = rememberKTXEnvironment(engine = engine, ibl = { … }, skybox = { … })
val scene = rememberFilamentScene(engine, env) { GltfInstance(asset = duck) }

// 0.8
val scene = rememberFilamentScene {
    Environment(rememberKtxEnvironment(ibl = { … }, skybox = { … }))
    GltfInstance(asset = duck)
}
```

| 0.7 | 0.8 |
| :--- | :--- |
| `rememberKTXEnvironment` / `rememberHDREnvironment` | `rememberKtxEnvironment` / `rememberHdrEnvironment` |
| `rememberKTXEnvironment(engine, …)` | no engine inside a scene; `engine = engine` to load outside one |
| `initialIntensity = x` | `Environment(…, intensity = x)` |
| `rememberHDREnvironment(showSkybox = false)` | `Environment(…, skyboxEnabled = false)` |
| `environment.skyboxState` / `environment.indirectLightState` | `environment.skybox`, `environment.reflections`, `environment.irradiance` |

> [!WARNING]
> **Silent:** a leftover positional engine, `rememberKtxEnvironment(engine, ibl = { … })`, still compiles.
> The first parameter is now the reload `key`, so the engine is taken as the key. Pass it as
> `engine = engine`, or remove it inside a scene.

### 3. Give `GltfInstance` an `AnimationState`

`animationIndex` and `animationTime` are gone:

| 0.7 | 0.8 |
| :--- | :--- |
| `animationIndex = 0, animationTime = clock` | `animationState = rememberAnimationState()` |
| A fixed pose at time `t` | an `AnimationState` with `isPaused = true`, then `seek(t)` |
| Anything else | leave `animationState` null and drive `instance.animator` from `onUpdate` |

> [!WARNING]
> **Silent:** two behaviours of `AnimationState` changed without a signature change.
> - `seek` now moves the playback clock. In 0.7 the next frame put it back, so remove any workaround
>   that re-seeks every frame.
> - One state passed to several `GltfInstance`s of the same asset now plays them in step at normal
>   speed. In 0.7 each instance advanced the clock, so two played at double speed; if you halved `speed`
>   to compensate, restore it.

## New in 0.8, nothing to change

- `rememberRenderTargetTexture(shadows = …)` sets the shadow technique of an off-screen view, as
  `FilamentView` does. The default is unchanged.
