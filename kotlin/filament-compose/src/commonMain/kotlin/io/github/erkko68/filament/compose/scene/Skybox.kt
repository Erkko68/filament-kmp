package io.github.erkko68.filament.compose.scene

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import io.github.erkko68.filament.Skybox
import io.github.erkko68.filament.compose.LocalFilamentEngine
import io.github.erkko68.filament.compose.LocalFilamentScene

/**
 * Adds a solid-color skybox to the scene.
 */
@Composable
fun ColorSkybox(
    r: Float = 0.1f,
    g: Float = 0.125f,
    b: Float = 0.15f,
    a: Float = 1.0f,
) {
    val engine = LocalFilamentEngine.current
    val scene = LocalFilamentScene.current

    DisposableEffect(r, g, b, a) {
        val skybox = Skybox.Builder()
            .color(r, g, b, a)
            .build(engine)
        scene.setSkybox(skybox)
        onDispose {
            scene.setSkybox(null)
            engine.destroySkybox(skybox)
        }
    }
}

/**
 * Adds a texture-based skybox from an IBL KTX cubemap.
 */
@Composable
fun ImageBasedSkybox(
    cubemapBytes: ByteArray,
    intensity: Float = 30_000f,
    showSun: Boolean = false,
) {
    val engine = LocalFilamentEngine.current
    val scene = LocalFilamentScene.current

    DisposableEffect(cubemapBytes, intensity, showSun) {
        // KTX1Loader is in filament-utils — caller loads the Texture and passes it directly.
        // This composable is a placeholder for the texture-based variant.
        // See IndirectLight composable for IBL reflections.
        onDispose { }
    }
}
