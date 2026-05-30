package io.github.erkko68.filament.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Scene
import io.github.erkko68.filament.compose.scene.ApplyIndirectLight
import io.github.erkko68.filament.compose.scene.ApplySkybox
import io.github.erkko68.filament.compose.scene.IndirectLightState
import io.github.erkko68.filament.compose.scene.SkyboxState

/**
 * A handle to a Filament [Scene] and its [Engine], produced by [rememberFilamentScene] and
 * consumed by one or more [FilamentView]s. The scene is the *world* — entities, lights,
 * skybox, IBL — and knows nothing about cameras or viewports.
 *
 * Pass the same handle to several [FilamentView]s to render one world through multiple
 * cameras and post-processing setups.
 */
class FilamentScene internal constructor(
    /** The engine backing this scene. Shared with every [FilamentView] that renders it. */
    val engine: Engine,
    internal val scene: Scene,
)

/**
 * Declares a Filament [Scene] as a value. The [content] lambda declares the world — lights,
 * models, primitives, groups — via scene composables; it emits no UI and runs once at this
 * call site regardless of how many [FilamentView]s later render the scene.
 *
 * ```kotlin
 * val scene = rememberFilamentScene(skyboxState = sky) {
 *     Light(type = LightManager.Type.SUN, ...)
 *     GltfInstance(asset = duck)
 * }
 * FilamentView(scene = scene, cameraState = cam) { Bloom(strength = 0.2f) }
 * ```
 *
 * @param engine Engine backing the scene. Defaults to a dedicated engine created and destroyed
 *   with this composable. Pass a [rememberFilamentEngine] value to share an engine.
 * @param skyboxState Optional hoisted skybox state. Null = no skybox (the default).
 * @param indirectLightState Optional hoisted IBL state. Null = no IBL (the default).
 * @param content Scene composables ([io.github.erkko68.filament.compose.scene.Light],
 *   `GltfInstance`, `Group`, primitives, …). They are extensions on [FilamentSceneScope].
 */
@Composable
fun rememberFilamentScene(
    engine: Engine = rememberFilamentEngine(),
    skyboxState: SkyboxState? = null,
    indirectLightState: IndirectLightState? = null,
    content: @Composable FilamentSceneScope.() -> Unit,
): FilamentScene {
    val scene = remember(engine) { engine.createScene() }

    // Registered before the content's effects so it disposes *after* them — entities are
    // removed from the scene before the scene itself is destroyed.
    DisposableEffect(engine, scene) {
        onDispose { engine.destroyScene(scene) }
    }

    val handle = remember(engine, scene) { FilamentScene(engine, scene) }

    CompositionLocalProvider(
        LocalFilamentEngine provides engine,
        LocalFilamentScene  provides scene,
    ) {
        if (skyboxState != null)         ApplySkybox(skyboxState, engine, scene)
        if (indirectLightState != null) ApplyIndirectLight(indirectLightState, engine, scene)
        FilamentSceneScopeInstance.content()
    }

    return handle
}
