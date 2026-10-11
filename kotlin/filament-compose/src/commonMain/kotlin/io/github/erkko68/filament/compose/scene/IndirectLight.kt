package io.github.erkko68.filament.compose.scene

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.compose.FilamentSceneScope
import io.github.erkko68.filament.compose.LocalFilamentEngine
import io.github.erkko68.filament.compose.LocalFilamentScene
import io.github.erkko68.filament.compose.internal.logWarn
import io.github.erkko68.filament.compose.internal.rememberOwned
import io.github.erkko68.filament.compose.noFilamentEngine
import io.github.erkko68.filament.compose.noFilamentScene
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
 * Image-based lighting for the scene: [reflections] drives specular highlights and [irradiance] the diffuse
 * ambient light. Renders nothing while both are null (still loading). [intensity] and [rotation] are set on
 * the live light, so animating them is cheap.
 *
 * For a KTX or HDR file, [Environment] with [rememberKTXEnvironment]/[rememberHDREnvironment] does this in
 * one call; use this for textures or harmonics of your own.
 *
 * ```kotlin
 * rememberFilamentScene {
 *     IndirectLight(reflections = envCubemap, irradiance = SphericalHarmonics(bands = 3, coefficients = sh))
 * }
 * ```
 *
 * A scene has one indirect light: declare one `IndirectLight` (or one [Environment]) per scene.
 *
 * @param reflections Prefiltered reflection cubemap, or null for none.
 * @param irradiance Diffuse irradiance as spherical harmonics. Null lets Filament approximate it from
 *   [reflections].
 * @param intensity Environment intensity in lux.
 * @param rotation Rotation of the environment around the scene.
 */
@Composable
fun FilamentSceneScope.IndirectLight(
    reflections: Texture?,
    irradiance: SphericalHarmonics? = null,
    intensity: Float = 30_000f,
    rotation: Rotation = Rotation.Identity,
) = IndirectLightNode(reflections, irradiance, null, intensity, rotation)

/** [IndirectLight] with the diffuse irradiance as a cubemap instead of spherical harmonics. */
@Composable
fun FilamentSceneScope.IndirectLight(
    reflections: Texture?,
    irradiance: Texture,
    intensity: Float = 30_000f,
    rotation: Rotation = Rotation.Identity,
) = IndirectLightNode(reflections, null, irradiance, intensity, rotation)

@Composable
private fun IndirectLightNode(
    reflections: Texture?,
    irradianceSh: SphericalHarmonics?,
    irradianceCubemap: Texture?,
    intensity: Float,
    rotation: Rotation,
) {
    if (reflections == null && irradianceSh == null && irradianceCubemap == null) return
    val engine = LocalFilamentEngine.current ?: noFilamentEngine()
    val scene = LocalFilamentScene.current ?: noFilamentScene()

    // Rebuilt only for new textures or harmonics; intensity and rotation are set on the live object below.
    // Textures we loaded (dependsOn) outlive it.
    val ibl = rememberOwned(engine, scene, reflections, irradianceSh, irradianceCubemap,
                            dependsOn = listOf(reflections, irradianceCubemap), create = {
        val builder = FilamentIndirectLight.Builder()
        reflections?.let { builder.reflections(it) }
        when {
            irradianceCubemap != null -> builder.irradiance(irradianceCubemap)
            irradianceSh      != null -> builder.irradiance(irradianceSh.bands, irradianceSh.coefficients)
        }
        builder.build(engine)
    }) { engine.destroy(it) }

    DisposableEffect(scene, ibl) {
        if (scene.indirectLight != null) logWarn("A scene has one indirect light: this IndirectLight replaces the one already declared.")
        scene.indirectLight = ibl
        // Not ours any more if another IndirectLight replaced it.
        onDispose { if (scene.indirectLight === ibl) scene.indirectLight = null }
    }

    DisposableEffect(ibl, intensity, rotation) {
        ibl.intensity = intensity
        ibl.rotation = rotation.toRotationMatrix()
        onDispose { }
    }
}
