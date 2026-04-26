package io.github.erkko68.filament.compose.scene

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.IndirectLight as FilamentIndirectLight
import io.github.erkko68.filament.compose.LocalFilamentEngine
import io.github.erkko68.filament.compose.LocalFilamentScene

/**
 * Spherical harmonics coefficients for diffuse irradiance.
 *
 * @param bands        Number of SH bands (1, 2, or 3 — determines coefficient count: 1, 4, or 9).
 * @param coefficients Packed RGB SH coefficients; length must be `bands² × 3`.
 */
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
 * Attaches Image-Based Lighting (IBL) to the scene for physically-based reflections and diffuse
 * irradiance.
 *
 * Provide at most one of [irradianceCubemap] or [irradianceSh] — if both are set, [irradianceCubemap]
 * takes precedence. [reflections] is independent and controls specular highlights.
 *
 * @param reflections      Specular reflection cubemap texture.
 * @param irradianceCubemap Diffuse irradiance cubemap texture.
 * @param irradianceSh     Diffuse irradiance via spherical harmonics (alternative to cubemap).
 * @param intensity        IBL intensity scale (default 30 000).
 * @param rotation         Optional 3×3 column-major rotation matrix (9 floats). Null = identity.
 *
 * Example:
 * ```kotlin
 * IndirectLight(
 *     reflections = envCubemap,
 *     irradianceSh = SphericalHarmonics(bands = 3, coefficients = shCoefficients),
 *     intensity = 50_000f,
 * )
 * ```
 */
@Composable
fun IndirectLight(
    reflections: Texture? = null,
    irradianceCubemap: Texture? = null,
    irradianceSh: SphericalHarmonics? = null,
    intensity: Float = 30_000f,
    rotation: FloatArray? = null,
) {
    val engine = LocalFilamentEngine.current
    val scene  = LocalFilamentScene.current

    val rotationKey = rotation?.toList()

    DisposableEffect(reflections, irradianceCubemap, irradianceSh, intensity, rotationKey) {
        val builder = FilamentIndirectLight.Builder()
            .intensity(intensity)

        reflections?.let { builder.reflections(it) }

        when {
            irradianceCubemap != null -> builder.irradiance(irradianceCubemap)
            irradianceSh      != null -> builder.irradiance(irradianceSh.bands, irradianceSh.coefficients)
        }

        rotation?.let { builder.rotation(it) }

        val ibl = builder.build(engine)
        scene.setIndirectLight(ibl)
        onDispose {
            scene.setIndirectLight(null)
            engine.destroyIndirectLight(ibl)
        }
    }
}
