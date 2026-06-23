package io.github.erkko68.filament.compose.scene

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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

/**
 * How the [Light]'s `intensity` value is interpreted, mirroring the three intensity setters
 * on the core [LightManager.Builder].
 *
 * - [LUMINOUS_POWER] — illuminance in lux for directional lights, luminous power in lumen for
 *   point/spot lights (the default and most common).
 * - [CANDELA] — luminous intensity in candela; for directional lights this equals lux.
 * - [WATTS] — electrical watts; combined with `efficiency` it becomes `683 · efficiency · watts`
 *   lumen. Typical efficiencies: incandescent 0.022, halogen 0.07, LED 0.087, fluorescent 0.107.
 */
enum class LightUnit { LUMINOUS_POWER, CANDELA, WATTS }

// ── Internal: change-detection key ───────────────────────────────────────────

internal data class LightSnapshot(
    val type: LightManager.Type,
    val direction: Direction,
    val position: Position,
    val color: Color,
    val intensity: Float,
    val intensityUnit: LightUnit,
    val efficiency: Float,
    val castShadows: Boolean,
    val falloff: Float,
    val cone: SpotCone,
    val sun: SunParams,
    val lightChannels: Set<Int>,
) {
    fun buildInto(engine: Engine, entity: Int) {
        val builder = LightManager.Builder(type)
            .direction(direction.x, direction.y, direction.z)
            .position(position.x, position.y, position.z)
            .color(color.r, color.g, color.b)
            .castShadows(castShadows)
            .falloff(falloff)
            .spotLightCone(cone.innerAngle, cone.outerAngle)
            .sunAngularRadius(sun.angularRadius)
            .sunHaloSize(sun.haloSize)
            .sunHaloFalloff(sun.haloFalloff)
        when (intensityUnit) {
            LightUnit.LUMINOUS_POWER -> builder.intensity(intensity)
            LightUnit.CANDELA        -> builder.intensityCandela(intensity)
            LightUnit.WATTS          -> builder.intensity(intensity, efficiency)
        }
        for (channel in 0..7) builder.lightChannel(channel, channel in lightChannels)
        builder.build(engine, entity)
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
 * // A physically-correct focused spot whose wattage is given as a 12 W LED bulb,
 * // affecting only objects on light channels 0 and 2.
 * Light(
 *     type          = LightManager.Type.FOCUSED_SPOT,
 *     position      = Position(2f, 3f, 0f),
 *     direction     = Direction(0f, -1f, 0f),
 *     intensity     = 12f,
 *     intensityUnit = LightUnit.WATTS,
 *     efficiency    = 0.087f,
 *     cone          = SpotCone(innerAngle = 0.3f, outerAngle = 0.5f),
 *     lightChannels = setOf(0, 2),
 * )
 * ```
 *
 * @param intensity Brightness in the units selected by [intensityUnit]. For [LightUnit.WATTS]
 *   this is the electrical wattage and is scaled by [efficiency].
 * @param intensityUnit How [intensity] is interpreted. See [LightUnit].
 * @param efficiency Luminous efficiency fraction, only used when [intensityUnit] is
 *   [LightUnit.WATTS].
 * @param lightChannels Which light channels (0–7) this light affects. A renderable is only lit
 *   if it shares at least one enabled channel with the light. Channel 0 is the default for both.
 */
@Composable
fun FilamentSceneScope.Light(
    type: LightManager.Type,
    color: Color = Color(1f, 1f, 1f),
    intensity: Float = 100_000f,
    intensityUnit: LightUnit = LightUnit.LUMINOUS_POWER,
    efficiency: Float = 1f,
    castShadows: Boolean = false,
    direction: Direction = Direction(0.3f, -1f, -0.5f),
    position: Position = Position(0f, 2f, 0f),
    falloff: Float = 10f,
    cone: SpotCone = SpotCone(),
    sun: SunParams = SunParams(),
    lightChannels: Set<Int> = setOf(0),
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
        LightSnapshot(type, direction, position, color, intensity, intensityUnit, efficiency,
            castShadows, falloff, cone, sun, lightChannels)
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

    // Push setters only when a parameter actually changes. LightSnapshot is a data class, so
    // its value equality gates the effect — no entity churn, no wasted setters per recompose.
    DisposableEffect(entity, LightSnapshot(type, direction, position, color, intensity, intensityUnit,
        efficiency, castShadows, falloff, cone, sun, lightChannels)) {
        val lm = engine.getLightManager()
        val li = lm.getInstance(entity)
        lm.setColor(li, color.r, color.g, color.b)
        when (intensityUnit) {
            LightUnit.LUMINOUS_POWER -> lm.setIntensity(li, intensity)
            LightUnit.CANDELA        -> lm.setIntensityCandela(li, intensity)
            LightUnit.WATTS          -> lm.setIntensity(li, intensity, efficiency)
        }
        lm.setDirection(li, direction.x, direction.y, direction.z)
        lm.setFalloff(li, falloff)
        lm.setSpotLightCone(li, cone.innerAngle, cone.outerAngle)
        lm.setSunAngularRadius(li, sun.angularRadius)
        lm.setSunHaloSize(li, sun.haloSize)
        lm.setSunHaloFalloff(li, sun.haloFalloff)
        lm.setShadowCaster(li, castShadows)
        for (channel in 0..7) lm.setLightChannel(li, channel, channel in lightChannels)

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
        onDispose { }
    }

    DisposableEffect(entity, parent) {
        if (parent != null) {
            val tm = engine.getTransformManager()
            tm.setParent(tm.getInstance(entity), tm.getInstance(parent))
        }
        onDispose { }
    }
}
