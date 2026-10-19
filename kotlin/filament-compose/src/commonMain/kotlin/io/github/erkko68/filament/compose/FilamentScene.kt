package io.github.erkko68.filament.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Scene
import io.github.erkko68.filament.compose.scene.LocalStandardMaterials
import io.github.erkko68.filament.compose.scene.StandardMaterialCache
import io.github.erkko68.filament.compose.internal.rememberOwned

/**
 * A handle to a Filament [Scene] and its [Engine], produced by [rememberFilamentScene] and
 * consumed by one or more [FilamentView]s. The scene is the *world* — entities, lights,
 * skybox, environment — and knows nothing about cameras or viewports.
 *
 * Pass the same handle to several [FilamentView]s to render one world through multiple
 * cameras and post-processing setups.
 */
class FilamentScene internal constructor(
    /** The engine backing this scene. Shared with every [FilamentView] that renders it. */
    val engine: Engine,
    internal val scene: Scene,
    internal val tapTargets: TapTargets = TapTargets(),
)

/**
 * Declares a Filament [Scene] as a value. The [content] lambda declares the world — lights,
 * models, primitives, groups — via scene composables; it emits no UI and runs once at this
 * call site regardless of how many [FilamentView]s later render the scene.
 *
 * ```kotlin
 * val scene = rememberFilamentScene {
 *     Skybox(color = LinearColor(0.05f, 0.05f, 0.08f))
 *     DirectionalLight(intensity = LightIntensity.LuminousPower(100_000f))
 *     GltfInstance(asset = duck)
 * }
 * FilamentView(scene = scene, cameraState = cam,
 *     postProcessing = PostProcessing(bloom = Bloom(strength = 0.2f)))
 * ```
 *
 * @param engine Engine backing the scene. Defaults to a dedicated engine created and destroyed
 *   with this composable. Pass a [rememberFilamentEngine] value to share an engine.
 * @param content Scene composables ([io.github.erkko68.filament.compose.scene.Light],
 *   `GltfInstance`, `Group`, primitives, `Skybox`, `Environment`, …). They are extensions on [FilamentSceneScope].
 */
@Composable
fun rememberFilamentScene(
    engine: Engine = rememberFilamentEngine(),
    content: @Composable FilamentSceneScope.() -> Unit,
): FilamentScene {
    // Remembered before the content so it's destroyed *after* it — entities leave the scene first.
    val scene = rememberOwned(engine, create = { engine.createScene() }) { engine.destroy(it) }

    val handle = remember(engine, scene) { FilamentScene(engine, scene) }

    // Shared, lazily-built cache of the built-in materials, scoped to this scene so repeated
    // convenience-helper calls reuse one base Material per type. Disposed with the scene.
    val standardMaterials = rememberOwned(engine, create = { StandardMaterialCache(engine) }) { it.dispose() }

    CompositionLocalProvider(
        LocalFilamentEngine    provides engine,
        LocalFilamentScene     provides scene,
        LocalStandardMaterials provides standardMaterials,
        LocalTapTargets        provides handle.tapTargets,
    ) {
        FilamentSceneScopeInstance.content()
    }

    return handle
}
