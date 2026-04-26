package io.github.erkko68.filament.compose.scene

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import io.github.erkko68.filament.LightManager
import io.github.erkko68.filament.compose.LocalFilamentEngine
import io.github.erkko68.filament.compose.LocalFilamentScene
import io.github.erkko68.filament.utils.Float3

/**
 * Adds a directional light (infinite distance, like the sun without disk).
 */
@Composable
fun DirectionalLight(
    direction: Float3 = Float3(0.3f, -1f, -0.5f),
    color: Float3 = Float3(1f, 1f, 1f),
    intensity: Float = 100_000f,
    castShadows: Boolean = false,
) {
    val engine = LocalFilamentEngine.current
    val scene = LocalFilamentScene.current

    DisposableEffect(direction, color, intensity, castShadows) {
        val entity = engine.getEntityManager().create()
        LightManager.Builder(LightManager.Type.DIRECTIONAL)
            .direction(direction.x, direction.y, direction.z)
            .color(color.x, color.y, color.z)
            .intensity(intensity)
            .castShadows(castShadows)
            .build(engine, entity)
        scene.addEntity(entity)
        onDispose {
            scene.removeEntity(entity)
            engine.getLightManager().destroy(entity)
            engine.getEntityManager().destroy(entity)
        }
    }
}

/**
 * Adds a sun light (directional + visible disk in sky).
 */
@Composable
fun SunLight(
    direction: Float3 = Float3(0.3f, -1f, -0.5f),
    color: Float3 = Float3(1f, 0.98f, 0.95f),
    intensity: Float = 110_000f,
    angularRadius: Float = 1.9f,
    haloSize: Float = 10f,
    haloFalloff: Float = 80f,
    castShadows: Boolean = true,
) {
    val engine = LocalFilamentEngine.current
    val scene = LocalFilamentScene.current

    DisposableEffect(direction, color, intensity, angularRadius, haloSize, haloFalloff, castShadows) {
        val entity = engine.getEntityManager().create()
        LightManager.Builder(LightManager.Type.SUN)
            .direction(direction.x, direction.y, direction.z)
            .color(color.x, color.y, color.z)
            .intensity(intensity)
            .sunAngularRadius(angularRadius)
            .sunHaloSize(haloSize)
            .sunHaloFalloff(haloFalloff)
            .castShadows(castShadows)
            .build(engine, entity)
        scene.addEntity(entity)
        onDispose {
            scene.removeEntity(entity)
            engine.getLightManager().destroy(entity)
            engine.getEntityManager().destroy(entity)
        }
    }
}

/**
 * Adds a point light at a world-space position.
 */
@Composable
fun PointLight(
    position: Float3 = Float3(0f, 2f, 0f),
    color: Float3 = Float3(1f, 1f, 1f),
    intensity: Float = 100_000f,
    falloffRadius: Float = 10f,
    castShadows: Boolean = false,
) {
    val engine = LocalFilamentEngine.current
    val scene = LocalFilamentScene.current

    DisposableEffect(position, color, intensity, falloffRadius, castShadows) {
        val entity = engine.getEntityManager().create()
        LightManager.Builder(LightManager.Type.POINT)
            .position(position.x, position.y, position.z)
            .color(color.x, color.y, color.z)
            .intensity(intensity)
            .falloff(falloffRadius)
            .castShadows(castShadows)
            .build(engine, entity)
        scene.addEntity(entity)
        onDispose {
            scene.removeEntity(entity)
            engine.getLightManager().destroy(entity)
            engine.getEntityManager().destroy(entity)
        }
    }
}
