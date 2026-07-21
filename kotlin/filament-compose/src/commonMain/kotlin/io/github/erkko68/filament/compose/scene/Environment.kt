package io.github.erkko68.filament.compose.scene

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.compose.LocalFilamentEngine
import io.github.erkko68.filament.utils.EquirectangularToCubemap
import io.github.erkko68.filament.utils.HDRLoader
import io.github.erkko68.filament.utils.IBLPrefilterContext
import io.github.erkko68.filament.utils.KTX1Loader
import io.github.erkko68.filament.utils.SpecularFilter
import kotlin.coroutines.cancellation.CancellationException

/**
 * A loaded image-based-lighting environment: the [indirectLightState] that lights the scene and
 * an optional [skyboxState] background. Obtain via [rememberKTXEnvironment] (pre-baked KTX) or
 * [rememberHDREnvironment] (raw `.hdr`) and feed the two states straight into
 * [io.github.erkko68.filament.compose.rememberFilamentScene]:
 *
 * ```kotlin
 * val engine = rememberFilamentEngine()
 * val env = rememberKTXEnvironment(
 *     engine = engine,
 *     ibl    = { Res.readBytes("environment/env_ibl.ktx") },
 *     skybox = { Res.readBytes("environment/env_skybox.ktx") },
 * )
 * val scene = rememberFilamentScene(
 *     engine = engine,
 *     skyboxState = env.skyboxState,
 *     indirectLightState = env.indirectLightState,
 * ) { ... }
 * ```
 *
 * The states are returned immediately and populate asynchronously as the KTX data decodes — the
 * scene simply renders without IBL/skybox until then. Both remain mutable afterward (e.g.
 * `env.indirectLightState.intensity = …`, `env.indirectLightState.rotation = …`).
 */
class Environment internal constructor(
    val indirectLightState: IndirectLightState,
    val skyboxState: SkyboxState?,
)

/**
 * Loads an IBL environment (and optional skybox) from KTX1 data and wires it into the scene's
 * indirect-light/skybox state. This is the convenience path over hand-wiring [KTX1Loader],
 * texture lifetimes, and [IndirectLightState]/[SkyboxState].
 *
 * Call it **outside** `rememberFilamentScene { }` (its result feeds the scene's parameters), so
 * the [engine] is typically hoisted via [io.github.erkko68.filament.compose.rememberFilamentEngine]
 * and shared with the scene.
 *
 * The returned [Environment] is non-null and stable; its states fill in once the KTX bytes
 * load. Failures (load lambda throwing, or undecodable KTX) are reported once via [onError] and
 * leave the corresponding state empty.
 *
 * For raw equirectangular `.hdr` images (no offline `cmgen` bake) use [rememberHDREnvironment].
 *
 * @param initialIntensity IBL intensity scale, applied to the state on first composition only —
 *   mutate `environment.indirectLightState.intensity` to change it afterwards.
 * @param key       Reloads when this changes. Defaults to [Unit] for static assets.
 * @param onError   Invoked once per failure (load threw, or the KTX failed to decode).
 * @param engine    Engine the environment's textures are allocated on. Defaults to the engine in
 *   the current composition scope; hoist and pass one explicitly when calling outside a scene.
 * @param skybox    Optional loader for the skybox cubemap KTX. Null = IBL only, no background.
 * @param ibl       Loader for the IBL KTX (prefiltered reflection cubemap + irradiance SH).
 */
@Composable
fun rememberKTXEnvironment(
    initialIntensity: Float = 30_000f,
    key: Any = Unit,
    onError: ((Throwable) -> Unit)? = null,
    engine: Engine = LocalFilamentEngine.current,
    skybox: (suspend () -> ByteArray)? = null,
    ibl: suspend () -> ByteArray,
): Environment {
    val indirectLightState = rememberIndirectLightState(initialIntensity = initialIntensity)
    val skyboxState = if (skybox != null) rememberSkyboxState() else null

    // IBL: prefiltered reflection cubemap + diffuse irradiance spherical harmonics.
    val iblBytes by produceState<ByteArray?>(null, engine, key) {
        value = loadOrReport(onError, ibl)
    }
    iblBytes?.let { bytes ->
        val reflections = remember(engine, bytes) {
            KTX1Loader.createTexture(engine, bytes, KTX1Loader.Options())
        }
        val sh = remember(bytes) { KTX1Loader.getSphericalHarmonics(bytes) }
        DisposableEffect(reflections, sh) {
            if (reflections != null) {
                indirectLightState.reflections = reflections
                if (sh != null) indirectLightState.irradianceSh = SphericalHarmonics(bands = 3, coefficients = sh)
            } else {
                onError?.invoke(IllegalArgumentException("Failed to decode IBL — not valid KTX1 data"))
            }
            onDispose {
                indirectLightState.reflections = null
                indirectLightState.irradianceSh = null
                reflections?.let { engine.destroyTexture(it) }
            }
        }
    }

    // Skybox: the environment cubemap rendered as the background.
    if (skybox != null && skyboxState != null) {
        val skyBytes by produceState<ByteArray?>(null, engine, key) {
            value = loadOrReport(onError, skybox)
        }
        skyBytes?.let { bytes ->
            val texture = remember(engine, bytes) {
                KTX1Loader.createTexture(engine, bytes, KTX1Loader.Options())
            }
            DisposableEffect(texture) {
                if (texture != null) {
                    skyboxState.source = SkyboxSource.Cubemap(texture)
                } else {
                    onError?.invoke(IllegalArgumentException("Failed to decode skybox — not valid KTX1 data"))
                }
                onDispose {
                    skyboxState.source = null
                    texture?.let { engine.destroyTexture(it) }
                }
            }
        }
    }

    return remember(indirectLightState, skyboxState) { Environment(indirectLightState, skyboxState) }
}

/**
 * Builds an [Environment] from an equirectangular **HDR** image instead of pre-baked KTX —
 * no `cmgen` step, just ship the `.hdr`. The reflection cubemap and skybox are prefiltered on
 * the GPU at load via [IBLPrefilterContext]/[EquirectangularToCubemap]/[SpecularFilter].
 *
 * The sibling of [rememberKTXEnvironment] (which loads pre-baked KTX) — same returned type, same
 * scene wiring:
 *
 * ```kotlin
 * val engine = rememberFilamentEngine()
 * val env = rememberHDREnvironment(engine = engine) { Res.readBytes("env/lobby.hdr") }
 * val scene = rememberFilamentScene(
 *     engine = engine,
 *     skyboxState = env.skyboxState,
 *     indirectLightState = env.indirectLightState,
 * ) { GltfInstance(asset = duck) }
 * ```
 *
 * **Tradeoff vs. [rememberKTXEnvironment]:** there's no baked diffuse irradiance (cmgen's
 * spherical harmonics). Filament approximates diffuse from the reflection's lowest mip — visibly
 * lower quality. Prefer KTX when you can bake offline; use this for raw `.hdr` workflows. The
 * prefilter runs on the GPU at load, so the first frames render before it lands.
 *
 * @param initialIntensity IBL intensity scale, applied to the state on first composition only —
 *   mutate `environment.indirectLightState.intensity` to change it afterwards.
 * @param showSkybox Render the environment cubemap as the skybox. False = IBL only.
 * @param format     Internal format for the decoded HDR equirect texture.
 * @param key        Reloads when this changes. Defaults to [Unit] for static assets.
 * @param onError    Invoked once if [hdr] throws or the bytes can't be decoded as HDR.
 * @param engine     Engine the environment's textures are allocated on. Defaults to the engine in
 *   the current composition scope; hoist and pass one explicitly when calling outside a scene.
 * @param hdr        Loader for the equirectangular HDR bytes (2:1 aspect).
 */
@Composable
fun rememberHDREnvironment(
    initialIntensity: Float = 30_000f,
    showSkybox: Boolean = true,
    format: Texture.InternalFormat = Texture.InternalFormat.R11F_G11F_B10F,
    key: Any = Unit,
    onError: ((Throwable) -> Unit)? = null,
    engine: Engine = LocalFilamentEngine.current,
    hdr: suspend () -> ByteArray,
): Environment {
    val indirectLightState = rememberIndirectLightState(initialIntensity = initialIntensity)
    val skyboxState = if (showSkybox) rememberSkyboxState() else null

    val hdrBytes by produceState<ByteArray?>(null, engine, key) {
        value = loadOrReport(onError, hdr)
    }
    hdrBytes?.let { bytes ->
        // Decode the HDR and run the GPU prefilter once per (engine, bytes, format). The work is
        // enqueued on the command stream, not blocking, so composition stays responsive.
        val prefiltered = remember(engine, bytes, format) { prefilterHdr(engine, bytes, format) }

        DisposableEffect(prefiltered) {
            if (prefiltered != null) {
                indirectLightState.reflections = prefiltered.reflections
                skyboxState?.source = SkyboxSource.Cubemap(prefiltered.skybox)
            } else {
                onError?.invoke(IllegalArgumentException("Failed to decode HDR — not a valid equirectangular HDR image"))
            }
            onDispose {
                indirectLightState.reflections = null
                skyboxState?.source = null
                prefiltered?.let {
                    engine.destroyTexture(it.reflections)
                    engine.destroyTexture(it.skybox)
                }
            }
        }
    }

    return remember(indirectLightState, skyboxState) { Environment(indirectLightState, skyboxState) }
}

/** Prefiltered output of [prefilterHdr]: the [skybox] environment cubemap and IBL [reflections]. */
private class PrefilteredHdr(val skybox: Texture, val reflections: Texture)

/**
 * Decodes equirectangular HDR [bytes] and prefilters them into an environment cubemap (skybox)
 * and a prefiltered specular cubemap (IBL reflections). Returns null if the bytes don't decode.
 * The transient equirect texture and the prefilter GPU state are destroyed before returning; the
 * two returned textures are owned by the caller.
 */
private fun prefilterHdr(engine: Engine, bytes: ByteArray, format: Texture.InternalFormat): PrefilteredHdr? {
    // HDRLoader.createTexture throws on Web (no Radiance/RGBE decoder in filament.js). Return null
    // so the caller reports it via onError instead of crashing composition.
    val equirect = try {
        HDRLoader.createTexture(engine, bytes, format)
    } catch (e: UnsupportedOperationException) {
        null
    } ?: return null
    val context = IBLPrefilterContext(engine)
    val toCubemap = EquirectangularToCubemap(context)
    val specular = SpecularFilter(context)
    try {
        val skybox = toCubemap.run(equirect)
        val reflections = specular.run(skybox)
        return PrefilteredHdr(skybox, reflections)
    } finally {
        // GPU reads of `equirect`/`skybox` are already enqueued before these destroy commands, so
        // ordered teardown is safe; the kept textures (skybox, reflections) outlive these filters.
        specular.destroy()
        toCubemap.destroy()
        context.destroy()
        engine.destroyTexture(equirect)
    }
}

private suspend fun loadOrReport(onError: ((Throwable) -> Unit)?, load: suspend () -> ByteArray): ByteArray? =
    try {
        load()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        onError?.invoke(e)
        null
    }
