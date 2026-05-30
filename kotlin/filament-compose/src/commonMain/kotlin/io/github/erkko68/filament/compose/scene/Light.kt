package io.github.erkko68.filament.compose.scene

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.LightManager
import io.github.erkko68.filament.compose.FilamentSceneScope
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
fun FilamentSceneScope.Light(
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
    val scene  = LocalFilamentScene.current
    val parent = LocalParentEntity.current

    val entity = remember { engine.getEntityManager().create() }

    // Entity destruction is registered *first* so it runs *last* on dispose (Compose tears
    // effects down in reverse registration order). That way the light/transform cleanup
    // below still sees a live entity when it runs.
    DisposableEffect(entity) {
        onDispose { engine.getEntityManager().destroy(entity) }
    }

    // Build light + transform components once (rebuild only if the light type changes — type
    // is locked at LightManager.Builder construction). Keeping the entity stable across
    // parameter changes lets the per-frame setters below stay cheap.
    DisposableEffect(entity, type) {
        // Initial component setup uses the current snapshot of values so the first frame is
        // correct; subsequent changes flow through the in-place setters in the SideEffect.
        LightSnapshot(type, direction, position, color, intensity, castShadows, falloff, cone, sun)
            .buildInto(engine, entity)

        // A transform component lets the light obey Group transforms. When present, Filament
        // computes the light's world position from the transform chain instead of the builder's
        // position(). For an ungrouped light, an identity-ish transform with the requested
        // translation gives the same effective position.
        val tm = engine.getTransformManager()
        if (!tm.hasComponent(entity)) tm.create(entity)

        scene.addEntity(entity)
        onDispose {
            scene.removeEntity(entity)
            engine.getLightManager().destroy(entity)
            tm.destroy(entity)
        }
    }

    // Per-recomposition updates — cheap setters, no entity churn.
    SideEffect {
        val lm = engine.getLightManager()
        val li = lm.getInstance(entity)
        lm.setColor(li, color.r, color.g, color.b)
        lm.setIntensity(li, intensity)
        lm.setDirection(li, direction.x, direction.y, direction.z)
        lm.setFalloff(li, falloff)
        lm.setSpotLightCone(li, cone.innerAngle, cone.outerAngle)
        lm.setSunAngularRadius(li, sun.angularRadius)
        lm.setSunHaloSize(li, sun.haloSize)
        lm.setSunHaloFalloff(li, sun.haloFalloff)
        lm.setShadowCaster(li, castShadows)

        // Position via transform so Group hierarchy works. Translation only — directional
        // lights derive their direction from `direction`, not the transform's rotation.
        val tm = engine.getTransformManager()
        tm.setTransform(
            tm.getInstance(entity),
            floatArrayOf(
                1f, 0f, 0f, 0f,
                0f, 1f, 0f, 0f,
                0f, 0f, 1f, 0f,
                position.x, position.y, position.z, 1f,
            ),
        )
    }

    DisposableEffect(entity, parent) {
        if (parent != null) {
            val tm = engine.getTransformManager()
            tm.setParent(tm.getInstance(entity), tm.getInstance(parent))
        }
        onDispose { }
    }
}
