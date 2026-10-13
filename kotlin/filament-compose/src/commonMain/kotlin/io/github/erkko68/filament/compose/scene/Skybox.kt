package io.github.erkko68.filament.compose.scene

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.compose.FilamentSceneScope
import io.github.erkko68.filament.compose.LocalFilamentEngine
import io.github.erkko68.filament.compose.LocalFilamentScene
import io.github.erkko68.filament.compose.internal.logWarn
import io.github.erkko68.filament.compose.internal.rememberOwned
import io.github.erkko68.filament.compose.noFilamentEngine
import io.github.erkko68.filament.compose.noFilamentScene
import io.github.erkko68.filament.Skybox as FilamentSkybox

/**
 * A solid-color background for the scene. The color is set on the live skybox, so animating it is cheap.
 *
 * ```kotlin
 * rememberFilamentScene {
 *     Skybox(color = LinearColor(0.05f, 0.05f, 0.08f))
 * }
 * ```
 *
 * A scene has one skybox: declare one `Skybox` (or one [Environment]) per scene.
 *
 * @param color Linear background color.
 * @param alpha Background alpha; below 1 only shows through a `transparent` view.
 * @param showSun Draw the sun disk of the scene's `SunLight`.
 * @param intensity Skybox intensity, used only while the scene has no [IndirectLight].
 * @param priority Draw-order priority of the skybox.
 */
@Composable
fun FilamentSceneScope.Skybox(
    color: LinearColor,
    alpha: Float = 1f,
    showSun: Boolean = false,
    intensity: Float = 1f,
    priority: Int = 0,
) = SkyboxNode(null, color, alpha, showSun, intensity, priority)

/**
 * A cubemap background for the scene, e.g. the [Environment.skybox] of a loaded environment or a
 * cubemap of your own. Renders nothing while [texture] is null (still loading).
 *
 * @param texture The cubemap to draw, or null while it loads.
 * @param showSun Draw the sun disk of the scene's `SunLight`.
 * @param intensity Skybox intensity, used only while the scene has no [IndirectLight].
 * @param priority Draw-order priority of the skybox.
 */
@Composable
fun FilamentSceneScope.Skybox(
    texture: Texture?,
    showSun: Boolean = false,
    intensity: Float = 1f,
    priority: Int = 0,
) {
    if (texture != null) SkyboxNode(texture, null, 1f, showSun, intensity, priority)
}

@Composable
private fun SkyboxNode(texture: Texture?, color: LinearColor?, alpha: Float, showSun: Boolean, intensity: Float, priority: Int) {
    val engine = LocalFilamentEngine.current ?: noFilamentEngine()
    val scene = LocalFilamentScene.current ?: noFilamentScene()

    // Only the color has a setter; anything else builds a new Skybox. A texture we loaded (dependsOn) outlives it.
    val skybox = rememberOwned(engine, scene, texture, showSun, intensity, priority, dependsOn = listOf(texture), create = {
        val builder = FilamentSkybox.Builder().showSun(showSun).intensity(intensity).priority(priority)
        if (texture != null) builder.environment(texture)
        builder.build(engine)
    }) { engine.destroy(it) }

    DisposableEffect(scene, skybox) {
        if (scene.skybox != null) logWarn("A scene has one skybox: this Skybox replaces the one already declared.")
        scene.skybox = skybox
        // Not ours any more if another Skybox replaced it.
        onDispose { if (scene.skybox === skybox) scene.skybox = null }
    }

    DisposableEffect(skybox, color, alpha) {
        if (color != null) skybox.setColor(color.r, color.g, color.b, alpha)
        onDispose { }
    }
}
