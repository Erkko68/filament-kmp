package io.github.erkko68.filament.compose.scene

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Scene
import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.Skybox as FilamentSkybox

/**
 * Source for a [SkyboxState]: either a solid color or a cubemap texture.
 */
sealed class SkyboxSource {
    /**
     * Solid-color skybox. [rgb] is (r, g, b); [alpha] controls blending with the clear color.
     */
    data class Color(
        val rgb: io.github.erkko68.filament.compose.scene.Color = io.github.erkko68.filament.compose.scene.Color(0.1f, 0.125f, 0.15f),
        val alpha: Float = 1.0f,
    ) : SkyboxSource()

    /**
     * Environment cubemap skybox. [texture] must be a CUBEMAP-type [Texture].
     */
    data class Cubemap(val texture: Texture) : SkyboxSource()
}

/**
 * Hoisted, observable skybox state. Pass to
 * [io.github.erkko68.filament.compose.rememberFilamentScene] via `skyboxState = ...`. A null
 * [source] removes the skybox entirely.
 *
 * ```kotlin
 * val sky = rememberSkyboxState(source = SkyboxSource.Color(Color(0.05f, 0.05f, 0.08f)))
 * val scene = rememberFilamentScene(skyboxState = sky) { ... }
 *
 * // Toggle at runtime
 * sky.source = SkyboxSource.Cubemap(envTexture)
 * sky.intensity = 30_000f
 * ```
 */
class SkyboxState internal constructor(
    initialSource: SkyboxSource?,
    initialShowSun: Boolean,
    initialIntensity: Float,
    initialPriority: Int,
) {
    var source: SkyboxSource? by mutableStateOf(initialSource)
    var showSun: Boolean      by mutableStateOf(initialShowSun)
    var intensity: Float      by mutableStateOf(initialIntensity)
    var priority: Int         by mutableStateOf(initialPriority)
}

/**
 * Creates and remembers a [SkyboxState].
 *
 * @param source     Color or cubemap skybox. Null = no skybox.
 * @param showSun    Render a sun disk (only meaningful with a directional SUN light).
 * @param intensity  Environment intensity applied on top of the skybox.
 * @param priority   Render priority; lower values render first.
 */
@Composable
fun rememberSkyboxState(
    source: SkyboxSource? = null,
    showSun: Boolean = false,
    intensity: Float = 1.0f,
    priority: Int = 0,
): SkyboxState = remember {
    SkyboxState(source, showSun, intensity, priority)
}

/**
 * Internal: applies [SkyboxState] to the scene. Builds a fresh Filament [FilamentSkybox]
 * whenever any field of the state changes, and tears down the old one.
 */
@Composable
internal fun ApplySkybox(state: SkyboxState, engine: Engine, scene: Scene) {
    val source    = state.source
    val showSun   = state.showSun
    val intensity = state.intensity
    val priority  = state.priority

    DisposableEffect(scene, source, showSun, intensity, priority) {
        val skybox: FilamentSkybox? = if (source == null) {
            scene.skybox = null
            null
        } else {
            val builder = FilamentSkybox.Builder()
                .showSun(showSun)
                .intensity(intensity)
                .priority(priority)
            when (source) {
                is SkyboxSource.Color   -> builder.color(source.rgb.r, source.rgb.g, source.rgb.b, source.alpha)
                is SkyboxSource.Cubemap -> builder.environment(source.texture)
            }
            builder.build(engine).also { scene.skybox = it }
        }
        onDispose {
            if (skybox != null) {
                scene.skybox = null
                engine.destroySkybox(skybox)
            }
        }
    }
}
