package io.github.erkko68.filament.compose.scene

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.LightManager
import io.github.erkko68.filament.compose.LocalFilamentEngine
import io.github.erkko68.filament.compose.LocalFilamentScene


// ── Public: type-specific parameter groups ────────────────────────────────────

/**
 * Spot / focused-spot cone angles (half-angles in radians).
 * [innerAngle] must be ≤ [outerAngle].
 */
data class SpotCone(
    val innerAngle: Float = 0.5f,
    val outerAngle: Float = 0.6f,
)

/**
 * Sun-disk appearance parameters for [LightManager.Type.SUN] lights.
 */
data class SunParams(
    val angularRadius: Float = 1.9f,
    val haloSize: Float = 10f,
    val haloFalloff: Float = 80f,
)

// ── Internal: change-detection key ───────────────────────────────────────────

internal data class LightSnapshot(
    val type: LightManager.Type,
    val direction: Direction,
    val position: Position,
    val color: Color,
    val intensity: Float,
    val castShadows: Boolean,
    val falloff: Float,
    val cone: SpotCone,
    val sun: SunParams,
) {
    fun buildInto(engine: Engine, entity: Int) {
        LightManager.Builder(type)
            .direction(direction.x, direction.y, direction.z)
            .position(position.x, position.y, position.z)
            .color(color.r, color.g, color.b)
            .intensity(intensity)
            .castShadows(castShadows)
            .falloff(falloff)
            .spotLightCone(cone.innerAngle, cone.outerAngle)
            .sunAngularRadius(sun.angularRadius)
            .sunHaloSize(sun.haloSize)
            .sunHaloFalloff(sun.haloFalloff)
            .build(engine, entity)
    }
}

// ── Composable ────────────────────────────────────────────────────────────────

/**
 * Adds a light to the scene. [type] is Filament's own [LightManager.Type] enum.
 *
 * Set only the parameters relevant to your light type — irrelevant ones are ignored
 * by Filament's builder. Type-specific groups ([cone], [sun]) use sensible defaults.
 *
 * Example:
 * ```kotlin
 * Light(
 *     type      = LightManager.Type.SUN,
 *     direction = Direction(0.3f, -1f, -0.5f),
 *     intensity = 110_000f,
 *     sun       = SunParams(angularRadius = 2.4f),
 *     castShadows = true,
 * )
 *
 * Light(
 *     type      = LightManager.Type.SPOT,
 *     position  = Position(2f, 3f, 0f),
 *     direction = Direction(0f, -1f, 0f),
 *     intensity = 50_000f,
 *     falloff   = 8f,
 *     cone      = SpotCone(innerAngle = 0.3f, outerAngle = 0.5f),
 * )
 * ```
 */
@Composable
fun Light(
    type: LightManager.Type,
    color: Color = Color(1f, 1f, 1f),
    intensity: Float = 100_000f,
    castShadows: Boolean = false,
    direction: Direction = Direction(0.3f, -1f, -0.5f),
    position: Position = Position(0f, 2f, 0f),
    falloff: Float = 10f,
    cone: SpotCone = SpotCone(),
    sun: SunParams = SunParams(),
) {
    val engine = LocalFilamentEngine.current
    val scene = LocalFilamentScene.current
    val snapshot = LightSnapshot(type, direction, position, color, intensity, castShadows, falloff, cone, sun)

    DisposableEffect(snapshot) {
        val entity = engine.getEntityManager().create()
        snapshot.buildInto(engine, entity)
        scene.addEntity(entity)
        onDispose {
            scene.removeEntity(entity)
            engine.getLightManager().destroy(entity)
            engine.getEntityManager().destroy(entity)
        }
    }
}
