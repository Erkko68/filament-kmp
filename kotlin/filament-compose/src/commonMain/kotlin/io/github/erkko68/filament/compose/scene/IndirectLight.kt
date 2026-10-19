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

    /**
     * Built-in ambients with no texture to load: `IndirectLight(irradiance = SphericalHarmonics.Daylight)`. They give
     * PBR models shape in their shadows before you have an environment of your own. They have no reflections, so
     * metals look dull. Baked with cmgen from Filament's CC0 environments and scaled to the same average brightness,
     * so they swap at the default `intensity`; `rotation` turns the brightest side.
     */
    companion object {
        /** A grey studio: soft light from above and the sides, darker below. Baked from `lightroom_14b`. */
        val NeutralStudio = SphericalHarmonics(
            bands = 3,
            coefficients = floatArrayOf(
                0.785451f, 0.785451f, 0.785451f,
                0.402526f, 0.402526f, 0.402526f,
                0.460539f, 0.460539f, 0.460539f,
                0.0839773f, 0.0839773f, 0.0839773f,
                0.0581439f, 0.0581439f, 0.0581439f,
                0.205268f, 0.205268f, 0.205268f,
                0.09268f, 0.09268f, 0.09268f,
                -0.0915337f, -0.0915337f, -0.0915337f,
                -0.00692572f, -0.00692572f, -0.00692572f,
            ),
        )

        /** A cool, bluish studio: soft light from above and the sides. Baked from Poly Haven's `studio_small_02`. */
        val CoolStudio = SphericalHarmonics(
            bands = 3,
            coefficients = floatArrayOf(
                0.691696f, 0.799512f, 0.922239f,
                0.351631f, 0.405232f, 0.470923f,
                -0.0897878f, -0.109664f, -0.136059f,
                0.201711f, 0.210771f, 0.266536f,
                -0.361677f, -0.423697f, -0.487987f,
                0.00209071f, 0.00234967f, 0.00335415f,
                0.00667798f, 0.00401055f, 0.00791221f,
                -0.16655f, -0.196367f, -0.237385f,
                0.108073f, 0.126756f, 0.142885f,
            ),
        )

        /** Outdoors at noon: blue sky above, darker ground below. Baked from Poly Haven's `noon_grass`. */
        val Daylight = SphericalHarmonics(
            bands = 3,
            coefficients = floatArrayOf(
                0.698321f, 0.798072f, 0.916988f,
                0.954358f, 0.971692f, 1.28397f,
                0.0366172f, 0.0481857f, 0.0914257f,
                0.065283f, 0.0564202f, 0.0469513f,
                0.111557f, 0.113561f, 0.101962f,
                0.0507378f, 0.0534188f, 0.067917f,
                -0.13201f, -0.131149f, -0.128027f,
                0.0141112f, 0.0197278f, 0.0395544f,
                -0.402214f, -0.40663f, -0.418737f,
            ),
        )

        /** A pink and orange evening sky, strongest from one side. Baked from Poly Haven's `the_sky_is_on_fire`. */
        val Sunset = SphericalHarmonics(
            bands = 3,
            coefficients = floatArrayOf(
                1.02883f, 0.703692f, 0.878698f,
                0.646674f, 0.542829f, 0.806677f,
                0.404464f, 0.175715f, 0.15167f,
                -0.387094f, -0.160993f, -0.0864539f,
                -0.0325909f, -0.00941229f, 0.00182543f,
                0.137703f, 0.0722613f, 0.0787944f,
                0.0313479f, 0.0116694f, 0.00430543f,
                -0.20782f, -0.00848111f, 0.0597548f,
                0.112549f, 0.0164904f, -0.0475147f,
            ),
        )

        /** Warm, even light from a sunlit stone square, strongest from one side. Baked from Poly Haven's `venetian_crossroads`. */
        val Warm = SphericalHarmonics(
            bands = 3,
            coefficients = floatArrayOf(
                0.905348f, 0.776374f, 0.522315f,
                0.468862f, 0.409614f, 0.284552f,
                0.692965f, 0.589981f, 0.375525f,
                -0.49691f, -0.425945f, -0.271649f,
                -0.0719124f, -0.0613608f, -0.0391061f,
                0.0964146f, 0.0826171f, 0.0531014f,
                0.012351f, 0.0103466f, 0.00616185f,
                -0.107898f, -0.0915287f, -0.0576814f,
                0.0022526f, 0.00146766f, -6.47498e-05f,
            ),
        )
    }
}

/**
 * Image-based lighting for the scene: [reflections] drives specular highlights and [irradiance] the diffuse
 * ambient light. Renders nothing while both are null (still loading). [intensity] and [rotation] are set on
 * the live light, so animating them is cheap.
 *
 * For a KTX or HDR file, [Environment] with [rememberKtxEnvironment]/[rememberHdrEnvironment] does this in
 * one call; use this for textures or harmonics of your own.
 *
 * ```kotlin
 * rememberFilamentScene {
 *     IndirectLight(reflections = envCubemap, irradiance = SphericalHarmonics(bands = 3, coefficients = sh))
 * }
 * ```
 *
 * With no environment of your own yet, `IndirectLight(irradiance = SphericalHarmonics.NeutralStudio)` lights the
 * scene evenly from a built-in neutral studio.
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
    reflections: Texture? = null,
    irradiance: SphericalHarmonics? = null,
    intensity: Float = 30_000f,
    rotation: Rotation = Rotation.Identity,
) = IndirectLightNode(reflections, irradiance, null, intensity, rotation)

/** [IndirectLight] with the diffuse irradiance as a cubemap instead of spherical harmonics. */
@Composable
fun FilamentSceneScope.IndirectLight(
    reflections: Texture? = null,
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
