package io.github.erkko68.filament.compose.scene

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Entity
import io.github.erkko68.filament.LightManager
import io.github.erkko68.filament.compose.FilamentSceneScope
import io.github.erkko68.filament.compose.LocalFilamentEngine
import io.github.erkko68.filament.compose.LocalFilamentScene
import io.github.erkko68.filament.compose.OnFrame
import kotlin.math.sqrt


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
 * Sun-disk appearance parameters for [SunLight].
 */
data class SunParams(
    val angularRadius: Float = 1.9f,
    val haloSize: Float = 10f,
    val haloFalloff: Float = 80f,
)

/**
 * How a light's `intensity` value is interpreted, mirroring the three intensity setters
 * on the core [LightManager.Builder].
 *
 * - [LUMINOUS_POWER] — illuminance in lux for directional lights, luminous power in lumen for
 *   point/spot lights (the default and most common).
 * - [CANDELA] — luminous intensity in candela; for directional lights this equals lux.
 * - [WATTS] — electrical watts; combined with `efficiency` it becomes `683 · efficiency · watts`
 *   lumen. Typical efficiencies: incandescent 0.022, halogen 0.07, LED 0.087, fluorescent 0.107.
 */
enum class LightUnit { LUMINOUS_POWER, CANDELA, WATTS }

// Per-light shadow config ([ShadowConfig]) and the view-level [Shadows] technique live in Shadows.kt.

// ── Internal: change-detection snapshot ───────────────────────────────────────

internal data class LightSnapshot(
    val type: LightManager.Type,
    val direction: Direction,
    val position: Position,
    val color: Color,
    val intensity: Float,
    val intensityUnit: LightUnit,
    val efficiency: Float,
    val shadow: ShadowConfig?,
    val castLight: Boolean,
    val falloff: Float,
    val cone: SpotCone,
    val sun: SunParams,
    val lightChannels: Set<Int>,
    val followGroupRotation: Boolean = false,
) {
    // Build-time component creation. `shadow`/`castLight` (and `type`) can only be set here — the
    // runtime LightManager exposes no setter for shadow options or castLight — so a change to any
    // of them rebuilds the component (see the build-keyed effect in LightNode).
    fun buildInto(engine: Engine, entity: Entity) {
        val builder = LightManager.Builder(type)
            .direction(direction.x, direction.y, direction.z)
            .position(position.x, position.y, position.z)
            .color(color.r, color.g, color.b)
            .castLight(castLight)
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
        builder.castShadows(shadow != null)
        if (shadow != null) builder.shadowOptions(shadow.toShadowOptions())
        for (channel in 0..7) builder.lightChannel(channel, channel in lightChannels)
        builder.build(engine, entity)
    }

    // Cheap per-change setters for the dynamic subset (everything except shadow/castLight/type).
    fun applyRuntime(engine: Engine, entity: Entity) {
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
        lm.setShadowCaster(li, shadow != null)
        for (channel in 0..7) lm.setLightChannel(li, channel, channel in lightChannels)

        // Position via transform so Group hierarchy works. Translation only — directional lights
        // derive their direction from `direction`, not the transform's rotation.
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
}

// ── Internal: shared scene-graph lifecycle for all typed lights ───────────────

/**
 * Owns the entity/scene/transform lifecycle for a typed light and drives it from [snapshot].
 * Build-time-only params (`type`/`shadow`/`castLight`) rebuild the light component; everything else
 * flows through the cheap runtime setters, gated by `LightSnapshot` value equality.
 */
@Composable
internal fun FilamentSceneScope.LightNode(snapshot: LightSnapshot) {
    val engine = LocalFilamentEngine.current
    val scene  = LocalFilamentScene.current
    val parent = LocalParentEntity.current

    val entity = remember { engine.getEntityManager().create() }

    // Entity destruction is registered *first* so it runs *last* on dispose (Compose tears effects
    // down in reverse registration order), keeping the entity live while the cleanups below run.
    DisposableEffect(entity) {
        onDispose { engine.getEntityManager().destroy(entity) }
    }

    // Transform component + scene membership, independent of light parameters.
    DisposableEffect(entity) {
        val tm = engine.getTransformManager()
        if (!tm.hasComponent(entity)) tm.create(entity)
        scene.addEntity(entity)
        onDispose {
            scene.removeEntity(entity)
            tm.destroy(entity)
        }
    }

    // Build (and rebuild) the light component only when a build-time-only param changes.
    DisposableEffect(entity, snapshot.type, snapshot.shadow, snapshot.castLight) {
        snapshot.buildInto(engine, entity)
        onDispose { engine.getLightManager().destroy(entity) }
    }

    // Push setters only when a parameter actually changes — LightSnapshot's value equality gates it.
    DisposableEffect(entity, snapshot) {
        snapshot.applyRuntime(engine, entity)
        onDispose { }
    }

    DisposableEffect(entity, parent) {
        if (parent != null) {
            val tm = engine.getTransformManager()
            tm.setParent(tm.getInstance(entity), tm.getInstance(parent))
        }
        onDispose { }
    }

    // Opt-in: re-aim the light by the parent Group's world rotation each frame (the lighting analog
    // of CameraNode). Always registered but inert unless followGroupRotation is on and parented, so
    // the call count stays stable across recompositions.
    val world = remember { FloatArray(16) }
    OnFrame {
        if (!snapshot.followGroupRotation || parent == null) return@OnFrame
        val tm = engine.getTransformManager()
        if (!tm.hasComponent(parent)) return@OnFrame
        tm.getWorldTransform(tm.getInstance(parent), world)
        val d = snapshot.direction
        val x = world[0] * d.x + world[4] * d.y + world[8] * d.z
        val y = world[1] * d.x + world[5] * d.y + world[9] * d.z
        val z = world[2] * d.x + world[6] * d.y + world[10] * d.z
        val len = sqrt(x * x + y * y + z * z)
        if (len > 0f) {
            val lm = engine.getLightManager()
            lm.setDirection(lm.getInstance(entity), x / len, y / len, z / len)
        }
    }
}

// ── Public: type-specific light composables ───────────────────────────────────

/**
 * A directional light — parallel rays from infinitely far (the classic "sun" for shading, without
 * the visible disk; use [SunLight] for that). Only directional and spot lights can cast shadows.
 *
 * ```kotlin
 * DirectionalLight(
 *     direction = Direction(0.3f, -1f, -0.5f),
 *     intensity = 100_000f,           // lux
 *     shadow    = ShadowConfig(mapSize = 2048, cascades = 3),
 * )
 * ```
 *
 * @param direction Light direction in world space (need not be a unit vector).
 * @param color Linear-sRGB colour; channels may exceed 1 for an over-bright tint.
 * @param intensity Illuminance in lux (or whatever [intensityUnit] selects).
 * @param intensityUnit How [intensity] is interpreted — luminous power, candela, or watts ([LightUnit]).
 * @param efficiency Luminous efficiency fraction; only used when [intensityUnit] is [LightUnit.WATTS].
 * @param shadow Shadow-map config ([ShadowConfig]), or null for no shadows.
 * @param castLight Whether the light emits illumination (false = shadow-only).
 * @param lightChannels Which channels (0–7) this light affects; a renderable is lit only if it
 *   shares an enabled channel. Channel 0 is the default.
 * @param followGroupRotation When inside a [Group], re-aim [direction] by the group's rotation each
 *   frame (e.g. a sun rig that tilts with its parent). Off by default.
 */
@Composable
fun FilamentSceneScope.DirectionalLight(
    direction: Direction = Direction(0.3f, -1f, -0.5f),
    color: Color = Color(1f, 1f, 1f),
    intensity: Float = 100_000f,
    intensityUnit: LightUnit = LightUnit.LUMINOUS_POWER,
    efficiency: Float = 1f,
    shadow: ShadowConfig? = null,
    castLight: Boolean = true,
    lightChannels: Set<Int> = setOf(0),
    followGroupRotation: Boolean = false,
) = LightNode(
    LightSnapshot(LightManager.Type.DIRECTIONAL, direction, Position(0f), color, intensity,
        intensityUnit, efficiency, shadow, castLight, falloff = 10f, cone = SpotCone(),
        sun = SunParams(), lightChannels = lightChannels, followGroupRotation = followGroupRotation),
)

/**
 * A directional light that also draws a sun disk and halo in the sky ([LightManager.Type.SUN]).
 * Same shading as [DirectionalLight] plus the visible disk configured by [sun].
 *
 * @param direction Direction the sunlight travels in world space (need not be a unit vector).
 * @param color Linear-sRGB colour of the light.
 * @param intensity Illuminance in lux (or whatever [intensityUnit] selects).
 * @param intensityUnit How [intensity] is interpreted ([LightUnit]).
 * @param efficiency Luminous efficiency fraction; only used when [intensityUnit] is [LightUnit.WATTS].
 * @param sun Sun-disk appearance — angular radius and halo ([SunParams]).
 * @param shadow Shadow-map config ([ShadowConfig]), or null for no shadows.
 * @param castLight Whether the light emits illumination (false = shadow-only).
 * @param lightChannels Channels (0–7) this light affects; channel 0 is the default.
 * @param followGroupRotation When inside a [Group], re-aim [direction] by the group's rotation each frame.
 */
@Composable
fun FilamentSceneScope.SunLight(
    direction: Direction = Direction(0.3f, -1f, -0.5f),
    color: Color = Color(1f, 1f, 1f),
    intensity: Float = 100_000f,
    intensityUnit: LightUnit = LightUnit.LUMINOUS_POWER,
    efficiency: Float = 1f,
    sun: SunParams = SunParams(),
    shadow: ShadowConfig? = null,
    castLight: Boolean = true,
    lightChannels: Set<Int> = setOf(0),
    followGroupRotation: Boolean = false,
) = LightNode(
    LightSnapshot(LightManager.Type.SUN, direction, Position(0f), color, intensity, intensityUnit,
        efficiency, shadow, castLight, falloff = 10f, cone = SpotCone(), sun = sun,
        lightChannels = lightChannels, followGroupRotation = followGroupRotation),
)

/**
 * A point light — emits from [position] in all directions, intensity diminishing with the inverse
 * square law out to [falloff].
 *
 * **Point lights cannot cast shadows.** A shadow map is a single depth texture rendered from one
 * projection; a point light's 360° emission won't fit one projection (it would need a 6-face cube
 * map), so Filament — being performance-focused — doesn't implement it. For shadow-casting
 * omnidirectional-style lighting, use a wide-cone [FocusedSpotLight] (a cone fits one projection).
 *
 * @param position World position; driven via a transform, so the light follows an enclosing [Group].
 * @param color Linear-sRGB colour of the light.
 * @param intensity Luminous power in lumen (or whatever [intensityUnit] selects).
 * @param intensityUnit How [intensity] is interpreted ([LightUnit]).
 * @param efficiency Luminous efficiency fraction; only used when [intensityUnit] is [LightUnit.WATTS].
 * @param falloff Sphere-of-influence radius in world units; minimize overlap for performance.
 * @param castLight Whether the light emits illumination (false = no contribution).
 * @param lightChannels Channels (0–7) this light affects; channel 0 is the default.
 */
@Composable
fun FilamentSceneScope.PointLight(
    position: Position = Position(0f, 2f, 0f),
    color: Color = Color(1f, 1f, 1f),
    intensity: Float = 100_000f,
    intensityUnit: LightUnit = LightUnit.LUMINOUS_POWER,
    efficiency: Float = 1f,
    falloff: Float = 10f,
    castLight: Boolean = true,
    lightChannels: Set<Int> = setOf(0),
) = LightNode(
    LightSnapshot(LightManager.Type.POINT, Direction(0f, -1f, 0f), position, color, intensity,
        intensityUnit, efficiency, shadow = null, castLight = castLight, falloff = falloff,
        cone = SpotCone(), sun = SunParams(), lightChannels = lightChannels),
)

/**
 * A spot light — emits from [position] along [direction] within a [cone], decoupling the outer cone
 * from total illumination (easier to tweak than [FocusedSpotLight]). Can cast shadows.
 *
 * @param position World position; driven via a transform, so the light follows an enclosing [Group].
 * @param direction Aim direction in world space (need not be a unit vector).
 * @param color Linear-sRGB colour of the light.
 * @param intensity Luminous power in lumen (or whatever [intensityUnit] selects).
 * @param intensityUnit How [intensity] is interpreted ([LightUnit]).
 * @param efficiency Luminous efficiency fraction; only used when [intensityUnit] is [LightUnit.WATTS].
 * @param falloff Sphere-of-influence radius in world units.
 * @param cone Inner/outer cone half-angles in radians ([SpotCone]).
 * @param shadow Shadow-map config ([ShadowConfig]), or null for no shadows.
 * @param castLight Whether the light emits illumination (false = shadow-only).
 * @param lightChannels Channels (0–7) this light affects; channel 0 is the default.
 * @param followGroupRotation When inside a [Group], re-aim [direction] by the group's rotation each frame.
 */
@Composable
fun FilamentSceneScope.SpotLight(
    position: Position = Position(0f, 2f, 0f),
    direction: Direction = Direction(0f, -1f, 0f),
    color: Color = Color(1f, 1f, 1f),
    intensity: Float = 100_000f,
    intensityUnit: LightUnit = LightUnit.LUMINOUS_POWER,
    efficiency: Float = 1f,
    falloff: Float = 10f,
    cone: SpotCone = SpotCone(),
    shadow: ShadowConfig? = null,
    castLight: Boolean = true,
    lightChannels: Set<Int> = setOf(0),
    followGroupRotation: Boolean = false,
) = LightNode(
    LightSnapshot(LightManager.Type.SPOT, direction, position, color, intensity, intensityUnit,
        efficiency, shadow, castLight, falloff, cone, sun = SunParams(),
        lightChannels = lightChannels, followGroupRotation = followGroupRotation),
)

/**
 * A physically-correct spot light ([LightManager.Type.FOCUSED_SPOT]) — like [SpotLight], but the
 * outer cone angle affects total illumination (narrowing the cone concentrates the light). Can cast
 * shadows. Parameters match [SpotLight].
 *
 * @param position World position; driven via a transform, so the light follows an enclosing [Group].
 * @param direction Aim direction in world space (need not be a unit vector).
 * @param color Linear-sRGB colour of the light.
 * @param intensity Luminous power in lumen (or whatever [intensityUnit] selects).
 * @param intensityUnit How [intensity] is interpreted ([LightUnit]).
 * @param efficiency Luminous efficiency fraction; only used when [intensityUnit] is [LightUnit.WATTS].
 * @param falloff Sphere-of-influence radius in world units.
 * @param cone Inner/outer cone half-angles in radians ([SpotCone]); the outer angle scales total output.
 * @param shadow Shadow-map config ([ShadowConfig]), or null for no shadows.
 * @param castLight Whether the light emits illumination (false = shadow-only).
 * @param lightChannels Channels (0–7) this light affects; channel 0 is the default.
 * @param followGroupRotation When inside a [Group], re-aim [direction] by the group's rotation each frame.
 */
@Composable
fun FilamentSceneScope.FocusedSpotLight(
    position: Position = Position(0f, 2f, 0f),
    direction: Direction = Direction(0f, -1f, 0f),
    color: Color = Color(1f, 1f, 1f),
    intensity: Float = 100_000f,
    intensityUnit: LightUnit = LightUnit.LUMINOUS_POWER,
    efficiency: Float = 1f,
    falloff: Float = 10f,
    cone: SpotCone = SpotCone(),
    shadow: ShadowConfig? = null,
    castLight: Boolean = true,
    lightChannels: Set<Int> = setOf(0),
    followGroupRotation: Boolean = false,
) = LightNode(
    LightSnapshot(LightManager.Type.FOCUSED_SPOT, direction, position, color, intensity,
        intensityUnit, efficiency, shadow, castLight, falloff, cone, sun = SunParams(),
        lightChannels = lightChannels, followGroupRotation = followGroupRotation),
)

// ── Public: low-level builder escape hatch ────────────────────────────────────

/**
 * Low-level escape hatch: adds a light configured directly through the raw [LightManager.Builder],
 * for cases the typed composables ([DirectionalLight], [SpotLight], …) don't cover. You own the full
 * builder; this composable only manages the entity, scene membership, and (when inside a [Group])
 * the transform so the light follows the group.
 *
 * The component is built once and rebuilt only when [rebuildKey] changes — pass a value derived from
 * whatever inputs your [configure] reads so updates take effect. When the light is **not** parented,
 * positioning is via `builder.position(...)`; inside a [Group], it follows the group instead.
 *
 * ```kotlin
 * Light(LightManager.Type.SPOT, rebuildKey = intensity) {
 *     position(2f, 3f, 0f); direction(0f, -1f, 0f)
 *     intensity(intensity); spotLightCone(0.3f, 0.5f)
 *     castShadows(true)
 * }
 * ```
 */
@Composable
fun FilamentSceneScope.Light(
    type: LightManager.Type,
    rebuildKey: Any? = null,
    configure: LightManager.Builder.() -> Unit,
) {
    val engine = LocalFilamentEngine.current
    val scene  = LocalFilamentScene.current
    val parent = LocalParentEntity.current

    val entity = remember { engine.getEntityManager().create() }

    DisposableEffect(entity) {
        onDispose {
            val tm = engine.getTransformManager()
            if (tm.hasComponent(entity)) tm.destroy(entity)
            engine.getEntityManager().destroy(entity)
        }
    }

    DisposableEffect(entity) {
        scene.addEntity(entity)
        onDispose { scene.removeEntity(entity) }
    }

    DisposableEffect(entity, type, rebuildKey) {
        LightManager.Builder(type).apply(configure).build(engine, entity)
        onDispose { engine.getLightManager().destroy(entity) }
    }

    // Only create a transform when parented — otherwise leave positioning to builder.position(),
    // which a transform component would override.
    DisposableEffect(entity, parent) {
        if (parent != null) {
            val tm = engine.getTransformManager()
            if (!tm.hasComponent(entity)) tm.create(entity)
            tm.setParent(tm.getInstance(entity), tm.getInstance(parent))
        }
        onDispose { }
    }
}
