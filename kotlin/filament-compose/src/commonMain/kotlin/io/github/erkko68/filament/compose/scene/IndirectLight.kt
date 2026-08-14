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
import io.github.erkko68.filament.IndirectLight as FilamentIndirectLight

/**
 * Spherical harmonics coefficients for diffuse irradiance.
 *
 * @param bands        Number of SH bands (1, 2, or 3 — determines coefficient count: 1, 4, or 9).
 * @param coefficients Packed RGB SH coefficients; length must be `bands² × 3`.
 */
// @Immutable: read-only, and equality is by content (below) — the compiler would otherwise infer
// unstable from the FloatArray and force every holder to recompose.
@Immutable
data class SphericalHarmonics(
    val bands: Int,
    val coefficients: FloatArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SphericalHarmonics) return false
        return bands == other.bands && coefficients.contentEquals(other.coefficients)
    }

    override fun hashCode(): Int = 31 * bands + coefficients.contentHashCode()
}

/**
 * Hoisted, observable Image-Based Lighting (IBL) state. Pass to
 * [io.github.erkko68.filament.compose.rememberFilamentScene] via `indirectLightState = ...`.
 *
 * Provide at most one of [irradianceCubemap] or [irradianceSh]. If both are set,
 * [irradianceCubemap] takes precedence. [reflections] is independent and controls
 * specular highlights.
 *
 * ```kotlin
 * val ibl = rememberIndirectLightState(
 *     reflections  = envCubemap,
 *     irradianceSh = SphericalHarmonics(bands = 3, coefficients = shCoeffs),
 *     intensity    = 50_000f,
 * )
 * val scene = rememberFilamentScene(indirectLightState = ibl) { ... }
 * ```
 */
@Stable
class IndirectLightState internal constructor(
    initialReflections: Texture?,
    initialIrradianceCubemap: Texture?,
    initialIrradianceSh: SphericalHarmonics?,
    initialIntensity: Float,
    initialRotation: Rotation?,
) {
    var reflections: Texture?            by mutableStateOf(initialReflections)
    var irradianceCubemap: Texture?      by mutableStateOf(initialIrradianceCubemap)
    var irradianceSh: SphericalHarmonics? by mutableStateOf(initialIrradianceSh)
    var intensity: Float                  by mutableStateOf(initialIntensity)
    var rotation: Rotation?               by mutableStateOf(initialRotation)
}

/**
 * Creates and remembers an [IndirectLightState]. The `initial*` values seed the state on first
 * composition only; mutate the returned state's fields to change the IBL afterwards.
 *
 * @param initialReflections       Specular reflection cubemap texture.
 * @param initialIrradianceCubemap Diffuse irradiance cubemap texture.
 * @param initialIrradianceSh      Diffuse irradiance via spherical harmonics (alternative to cubemap).
 * @param initialIntensity         IBL intensity scale.
 * @param initialRotation          Optional rotation of the environment. Null = identity.
 */
@Composable
fun rememberIndirectLightState(
    initialReflections: Texture? = null,
    initialIrradianceCubemap: Texture? = null,
    initialIrradianceSh: SphericalHarmonics? = null,
    initialIntensity: Float = 30_000f,
    initialRotation: Rotation? = null,
): IndirectLightState = remember {
    IndirectLightState(initialReflections, initialIrradianceCubemap, initialIrradianceSh,
        initialIntensity, initialRotation)
}

/**
 * Internal: applies [IndirectLightState] to the scene. Rebuilds the Filament
 * [FilamentIndirectLight] whenever any field changes.
 */
@Composable
internal fun ApplyIndirectLight(state: IndirectLightState, engine: Engine, scene: Scene) {
    val reflections       = state.reflections
    val irradianceCubemap = state.irradianceCubemap
    val irradianceSh      = state.irradianceSh
    val intensity         = state.intensity
    val rotation          = state.rotation

    DisposableEffect(scene, reflections, irradianceCubemap, irradianceSh, intensity, rotation) {
        val builder = FilamentIndirectLight.Builder().intensity(intensity)
        reflections?.let { builder.reflections(it) }
        when {
            irradianceCubemap != null -> builder.irradiance(irradianceCubemap)
            irradianceSh      != null -> builder.irradiance(irradianceSh.bands, irradianceSh.coefficients)
        }
        rotation?.let { builder.rotation(it.toRotationMatrix()) }

        val ibl = builder.build(engine)
        scene.indirectLight = ibl
        onDispose {
            scene.indirectLight = null
            engine.destroyIndirectLight(ibl)
        }
    }
}
