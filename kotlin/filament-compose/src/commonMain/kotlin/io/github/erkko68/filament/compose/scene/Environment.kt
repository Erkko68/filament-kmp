package io.github.erkko68.filament.compose.scene

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ProduceStateScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Texture
import io.github.erkko68.filament.compose.FilamentSceneScope
import io.github.erkko68.filament.compose.LocalFilamentEngine
import io.github.erkko68.filament.compose.internal.rememberOwned
import io.github.erkko68.filament.compose.noFilamentEngine
import io.github.erkko68.filament.interop.singleThreaded
import io.github.erkko68.filament.utils.DecodedImage
import io.github.erkko68.filament.utils.HDRLoader
import io.github.erkko68.filament.utils.IBLPrefilterContext
import io.github.erkko68.filament.utils.Ktx1Bundle
import io.github.erkko68.filament.utils.Ktx1Reader
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.withContext

/**
 * A loaded image-based-lighting environment, from [rememberKtxEnvironment] (pre-baked KTX) or
 * [rememberHdrEnvironment] (raw `.hdr`). Place it in a scene with the [Environment] composable, or
 * hand its parts to [IndirectLight] and [Skybox] yourself.
 *
 * Every part is null until its data has loaded, and stays null if the load failed.
 *
 * @property reflections Prefiltered reflection cubemap.
 * @property irradiance Diffuse irradiance harmonics, where the source carries them (KTX does, HDR doesn't).
 * @property skybox Background cubemap, where one was asked for.
 */
@Immutable
class Environment internal constructor(
    val reflections: Texture?,
    val irradiance: SphericalHarmonics?,
    val skybox: Texture?,
)

/**
 * Lights the scene with a loaded [environment] and draws its skybox, if it has one:
 *
 * ```kotlin
 * FilamentSceneView(cameraState = cam) {
 *     Environment(rememberKtxEnvironment(
 *         ibl    = { Res.readBytes("files/environment/env_ibl.ktx") },
 *         skybox = { Res.readBytes("files/environment/env_skybox.ktx") },
 *     ))
 *     GltfInstance(asset = duck)   // lit by the environment, no light needed
 * }
 * ```
 *
 * Shorthand for an [IndirectLight] plus a [Skybox]; declare those two yourself to set the skybox's own
 * options. A scene has one of each, so declare one `Environment` per scene.
 *
 * @param environment The loaded environment. The scene renders without it until its data arrives.
 * @param intensity Environment intensity in lux.
 * @param rotation Rotation of the lighting around the scene. Filament's skybox does not rotate with it.
 * @param skyboxEnabled Draw the environment's skybox as the background. False lights the scene only.
 */
@Composable
fun FilamentSceneScope.Environment(
    environment: Environment,
    intensity: Float = 30_000f,
    rotation: Rotation = Rotation.Identity,
    skyboxEnabled: Boolean = true,
) {
    IndirectLight(environment.reflections, environment.irradiance, intensity, rotation)
    if (skyboxEnabled) Skybox(environment.skybox)
}

/**
 * Loads an IBL environment (and optional skybox) from KTX1 data, e.g. the pair Filament's `cmgen` bakes.
 * Returns at once; the [Environment]'s parts fill in as the data decodes. Failures (a loader throwing, or
 * undecodable KTX) are reported once via [onError] and leave that part null.
 *
 * Like the other loaders, call it inside `rememberFilamentScene { }`, or outside with a hoisted [engine]
 * to share one environment between scenes.
 *
 * For raw equirectangular `.hdr` images (no offline bake) use [rememberHdrEnvironment].
 *
 * @param key     Reloads when this changes. Defaults to [Unit] for static assets.
 * @param onError Invoked once per failure (load threw, or the KTX failed to decode).
 * @param engine  Engine the textures are allocated on. Defaults to the engine in the current
 *   composition scope, which only exists inside `rememberFilamentScene { }`.
 * @param skybox  Optional loader for the skybox cubemap KTX. Null = lighting only, no background.
 * @param ibl     Loader for the IBL KTX (prefiltered reflection cubemap + irradiance harmonics).
 */
@Composable
fun rememberKtxEnvironment(
    key: Any = Unit,
    onError: ((Throwable) -> Unit)? = null,
    engine: Engine = LocalFilamentEngine.current ?: noFilamentEngine(),
    skybox: (suspend () -> ByteArray)? = null,
    ibl: suspend () -> ByteArray,
): Environment {
    val iblBytes by produceState<ByteArray?>(null, key) {
        value = loadOrReport(onError, ibl)
    }
    val reflections = iblBytes?.let { rememberKtxCubemap(engine, it, "IBL", onError) }
    // The harmonics are only worth having with the reflections they were baked alongside.
    val irradiance = remember(iblBytes, reflections) {
        iblBytes?.takeIf { reflections != null }?.let { bytes ->
            val sh = FloatArray(9 * 3)
            if (Ktx1Bundle(bytes).use { it.getSphericalHarmonics(sh) }) SphericalHarmonics(bands = 3, coefficients = sh) else null
        }
    }

    val skyTexture = if (skybox != null) {
        val skyBytes by produceState<ByteArray?>(null, key) {
            value = loadOrReport(onError, skybox)
        }
        skyBytes?.let { rememberKtxCubemap(engine, it, "skybox", onError) }
    } else null

    return remember(reflections, irradiance, skyTexture) { Environment(reflections, irradiance, skyTexture) }
}

@Composable
private fun rememberKtxCubemap(engine: Engine, bytes: ByteArray, what: String, onError: ((Throwable) -> Unit)?): Texture? {
    val texture = rememberOwned(engine, bytes, create = {
        Ktx1Reader.createTexture(engine, Ktx1Bundle(bytes), srgb = false)
    }) { engine.destroy(it) }
    if (texture == null) {
        LaunchedEffect(bytes) {
            onError?.invoke(IllegalArgumentException("Failed to decode $what — not valid KTX1 data"))
        }
    }
    return texture
}

/**
 * Builds an [Environment] from an equirectangular **HDR** image instead of pre-baked KTX — no `cmgen`
 * step, just ship the `.hdr`. The reflection cubemap and skybox are prefiltered on the GPU at load via
 * [IBLPrefilterContext]'s EquirectangularToCubemap and SpecularFilter.
 *
 * ```kotlin
 * rememberFilamentScene {
 *     Environment(rememberHdrEnvironment { Res.readBytes("env/lobby.hdr") })
 *     GltfInstance(asset = duck)
 * }
 * ```
 *
 * **Tradeoff vs. [rememberKtxEnvironment]:** there's no baked diffuse irradiance (cmgen's spherical
 * harmonics). Filament approximates diffuse from the reflection's lowest mip — visibly lower quality.
 * Prefer KTX when you can bake offline; use this for raw `.hdr` workflows. The prefilter runs on the GPU
 * at load, so the first frames render before it lands.
 *
 * @param format  Internal format for the decoded HDR equirect texture.
 * @param key     Reloads when this changes. Defaults to [Unit] for static assets.
 * @param onError Invoked once if [hdr] throws or the bytes can't be decoded as HDR.
 * @param engine  Engine the textures are allocated on. Defaults to the engine in the current
 *   composition scope, which only exists inside `rememberFilamentScene { }`.
 * @param hdr     Loader for the equirectangular HDR bytes (2:1 aspect).
 */
@Composable
fun rememberHdrEnvironment(
    format: Texture.InternalFormat = Texture.InternalFormat.R11F_G11F_B10F,
    key: Any = Unit,
    onError: ((Throwable) -> Unit)? = null,
    engine: Engine = LocalFilamentEngine.current ?: noFilamentEngine(),
    hdr: suspend () -> ByteArray,
): Environment {
    // Decoded off the main thread; the prefilter stays on it, since it needs the engine.
    val image by produceState<DecodedImage?>(null, key, format, engine) {
        loadOwned(onError, hdr) { bytes ->
            HDRLoader.decode(bytes) ?: throw IllegalArgumentException("Failed to decode HDR — not a valid equirectangular HDR image")
        }
    }
    val prefiltered = image?.let { decoded ->
        // Kept as long as the environment: destroying it waits for the backend, seconds on a slow GPU, and that
        // has only just been given the prefilter.
        val context = rememberOwned(engine, create = { IBLPrefilterContext(engine) }) { it.destroy() }
        rememberOwned(engine, decoded, create = { prefilterHdr(engine, context, decoded, format) }) {
            engine.destroy(it.reflections)
            engine.destroy(it.skybox)
        }
    }

    return remember(prefiltered) { Environment(prefiltered?.reflections, null, prefiltered?.skybox) }
}

/** Prefiltered output of [prefilterHdr]: the [skybox] environment cubemap and IBL [reflections]. */
private class PrefilteredHdr(val skybox: Texture, val reflections: Texture)

/**
 * Prefilters a decoded equirectangular HDR [image] into an environment cubemap (skybox)
 * and a prefiltered specular cubemap (IBL reflections). Returns null if it can't be uploaded.
 * The transient equirect texture and the filters are destroyed before returning; the two
 * returned textures are owned by the caller.
 */
private fun prefilterHdr(engine: Engine, context: IBLPrefilterContext, image: DecodedImage, format: Texture.InternalFormat): PrefilteredHdr? {
    val equirect = image.createTexture(engine, format) ?: return null
    val toCubemap = IBLPrefilterContext.EquirectangularToCubemap(context)
    val specular = IBLPrefilterContext.SpecularFilter(context)
    try {
        val skybox = toCubemap(equirect)
        val reflections = specular(skybox)
        return PrefilteredHdr(skybox, reflections)
    } finally {
        // GPU reads of `equirect`/`skybox` are already enqueued before these destroy commands, so
        // ordered teardown is safe; the kept textures (skybox, reflections) outlive these filters.
        specular.destroy()
        toCubemap.destroy()
        engine.destroy(equirect)
    }
}

private suspend fun <T> loadOrReport(onError: ((Throwable) -> Unit)?, load: suspend () -> T): T? =
    try {
        load()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        onError?.invoke(e)
        null
    }

/**
 * Runs [load] on the caller's thread and [decode] off it, so a slow decode can't drop frames; web has no other thread.
 * The result holds native memory: it's closed once this producer is cancelled, delivered or not.
 */
internal suspend fun <T : AutoCloseable> ProduceStateScope<T?>.loadOwned(
    onError: ((Throwable) -> Unit)?,
    load: suspend () -> ByteArray,
    decode: suspend (ByteArray) -> T,
) {
    var decoded: T? = null
    try {
        value = loadOrReport(onError) {
            val bytes = load()
            // Kept from inside the block: withContext discards what a cancelled block returns.
            withContext(if (singleThreaded) EmptyCoroutineContext else Dispatchers.Default) { decode(bytes).also { decoded = it } }
        }
        awaitCancellation()
    } finally {
        decoded?.close()
    }
}
