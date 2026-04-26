package io.github.erkko68.filament.compose.scene

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.Skybox as FilamentSkybox
import io.github.erkko68.filament.compose.LocalFilamentEngine
import io.github.erkko68.filament.compose.LocalFilamentScene


/**
 * Source for a [Skybox]: either a solid color or a cubemap texture.
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
 * Attaches a skybox to the scene.
 *
 * @param source     Color or cubemap skybox — see [SkyboxSource].
 * @param showSun    Render a sun disk (only meaningful with a directional SUN light).
 * @param intensity  Environment intensity applied on top of the skybox.
 * @param priority   Render priority; lower values render first (default 0).
 *
 * Example:
 * ```kotlin
 * Skybox(source = SkyboxSource.Color(Color(0.05f, 0.05f, 0.08f)))
 *
 * Skybox(
 *     source    = SkyboxSource.Cubemap(environmentTexture),
 *     showSun   = true,
 *     intensity = 30_000f,
 * )
 * ```
 */
@Composable
fun Skybox(
    source: SkyboxSource = SkyboxSource.Color(),
    showSun: Boolean = false,
    intensity: Float = 1.0f,
    priority: Int = 0,
) {
    val engine = LocalFilamentEngine.current
    val scene  = LocalFilamentScene.current

    DisposableEffect(source, showSun, intensity, priority) {
        val builder = FilamentSkybox.Builder()
            .showSun(showSun)
            .intensity(intensity)
            .priority(priority)

        when (source) {
            is SkyboxSource.Color   -> builder.color(source.rgb.r, source.rgb.g, source.rgb.b, source.alpha)
            is SkyboxSource.Cubemap -> builder.environment(source.texture)
        }

        val skybox = builder.build(engine)
        scene.setSkybox(skybox)
        onDispose {
            scene.setSkybox(null)
            engine.destroySkybox(skybox)
        }
    }
}
