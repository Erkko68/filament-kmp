# Migrating from 0.7

0.8.0 changes two parts of the `filament-compose` API. The other modules are unaffected.

## 1. The skybox and environment are scene content

`rememberFilamentScene` and `FilamentSceneView` no longer take `skyboxState` / `indirectLightState`, and
`SkyboxState`, `IndirectLightState`, `SkyboxSource` and their `remember*` functions are gone. Declare the
background and the lighting inside the scene, like a light:

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
| `SkyboxSource.Cubemap(texture)` | `Skybox(environment = texture)` |
| `rememberIndirectLightState(…)` + `indirectLightState =` | `IndirectLight(reflections, irradiance, intensity, rotation)` in the scene |
| `state.intensity = x`, `state.rotation = r` | pass `intensity = x`, `rotation = r`; hold them in your own state to change them |
| `rememberFilamentScene(engine, environment) { }` | `rememberFilamentScene { Environment(environment) }` |

The environment loaders now have the shape of every other loader: the engine comes from the scene, so
there is no engine to hoist.

```kotlin
// 0.7
val engine = rememberFilamentEngine()
val env = rememberKTXEnvironment(engine = engine, ibl = { … }, skybox = { … })
val scene = rememberFilamentScene(engine, env) { GltfInstance(asset = duck) }

// 0.8
val scene = rememberFilamentScene {
    Environment(rememberKTXEnvironment(ibl = { … }, skybox = { … }))
    GltfInstance(asset = duck)
}
```

- `engine` is no longer the first, required parameter: pass `engine = engine` to load outside a scene.
- `initialIntensity` moved to `Environment(…, intensity = )`.
- `rememberHDREnvironment(showSkybox = false)` became `Environment(…, skyboxEnabled = false)`.
- `Environment` exposes `reflections`, `irradiance` and `skybox` instead of two state objects.

A scene has one skybox and one indirect light. A second `Skybox`, `IndirectLight` or `Environment` in
the same scene replaces the first and logs a warning.

## 2. `GltfInstance` takes one animation input

`animationIndex` and `animationTime` are gone; they were ignored whenever `animationState` was set.

| 0.7 | 0.8 |
| :--- | :--- |
| `animationIndex = 0, animationTime = clock` | `animationState = rememberAnimationState()` |
| A fixed pose at time `t` | an `AnimationState` with `isPaused = true`, then `seek(t)` |
| Anything else | leave `animationState` null and drive `instance.animator` from `onUpdate` |

`AnimationState.seek` now moves the playback clock; in 0.7 the next frame put it back.
