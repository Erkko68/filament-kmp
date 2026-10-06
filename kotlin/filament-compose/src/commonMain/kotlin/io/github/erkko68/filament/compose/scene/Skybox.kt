package io.github.erkko68.filament.compose.scene

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Scene
import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.compose.internal.rememberOwned
import io.github.erkko68.filament.Skybox as FilamentSkybox

/**
 * Source for a [SkyboxState]: either a solid color or a cubemap texture.
 */
@Immutable
sealed class SkyboxSource {
    /**
     * Solid-color skybox. [color] is (r, g, b); [alpha] controls blending with the clear color.
     */
    data class Color(
        val color: LinearColor = LinearColor(0.1f, 0.125f, 0.15f),
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
 * val sky = rememberSkyboxState(initialSource = SkyboxSource.Color(LinearColor(0.05f, 0.05f, 0.08f)))
 * val scene = rememberFilamentScene(skyboxState = sky) { ... }
 *
 * // Toggle at runtime
 * sky.source = SkyboxSource.Cubemap(envTexture)
 * sky.intensity = 30_000f
 * ```
 */
@Stable
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
 * Creates and remembers a [SkyboxState]. The `initial*` values seed the state on first
 * composition only; mutate the returned state's fields to change the skybox afterwards.
 *
 * @param initialSource     Color or cubemap skybox. Null = no skybox.
 * @param initialShowSun    Render a sun disk (only meaningful with a directional SUN light).
 * @param initialIntensity  Environment intensity applied on top of the skybox.
 * @param initialPriority   Render priority; lower values render first.
 */
@Composable
fun rememberSkyboxState(
    initialSource: SkyboxSource? = null,
    initialShowSun: Boolean = false,
    initialIntensity: Float = 1.0f,
    initialPriority: Int = 0,
): SkyboxState = remember {
    SkyboxState(initialSource, initialShowSun, initialIntensity, initialPriority)
}

/**
 * Internal: applies [SkyboxState] to the scene. A color is set on the live Filament [FilamentSkybox]; anything
 * else has no setter and builds a new one.
 */
@Composable
internal fun ApplySkybox(state: SkyboxState, engine: Engine, scene: Scene) {
    val source    = state.source
    val showSun   = state.showSun
    val intensity = state.intensity
    val priority  = state.priority
    val texture   = (source as? SkyboxSource.Cubemap)?.texture

    // A state change gets here a frame late, so a texture we loaded (dependsOn) stays alive until this lets go.
    val skybox = rememberOwned(engine, scene, source == null, texture, showSun, intensity, priority,
                               dependsOn = listOf(texture), create = {
        if (source == null) null else {
            val builder = FilamentSkybox.Builder()
                .showSun(showSun)
                .intensity(intensity)
                .priority(priority)
            if (texture != null) builder.environment(texture)
            builder.build(engine)
        }
    }) { engine.destroy(it) }

    DisposableEffect(scene, skybox) {
        scene.skybox = skybox
        onDispose { scene.skybox = null }
    }

    DisposableEffect(skybox, source) {
        if (skybox != null && source is SkyboxSource.Color) {
            skybox.setColor(source.color.r, source.color.g, source.color.b, source.alpha)
        }
        onDispose { }
    }
}
