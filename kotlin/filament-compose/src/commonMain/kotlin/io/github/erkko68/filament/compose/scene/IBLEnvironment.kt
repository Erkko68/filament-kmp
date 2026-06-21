package io.github.erkko68.filament.compose.scene

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.utils.KTX1Loader
import kotlin.coroutines.cancellation.CancellationException

/**
 * A loaded image-based-lighting environment: the [indirectLightState] that lights the scene and
 * an optional [skyboxState] background. Obtain via [rememberIBLEnvironment] and feed the two
 * states straight into [io.github.erkko68.filament.compose.rememberFilamentScene]:
 *
 * ```kotlin
 * val engine = rememberFilamentEngine()
 * val env = rememberIBLEnvironment(
 *     engine,
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
class IBLEnvironment internal constructor(
    val indirectLightState: IndirectLightState,
    val skyboxState: SkyboxState?,
)

/**
 * Loads an IBL environment (and optional skybox) from KTX1 data and wires it into the scene's
 * indirect-light/skybox state. This is the convenience path over hand-wiring [KTX1Loader],
 * texture lifetimes, and [IndirectLightState]/[SkyboxState].
 *
 * Call it **outside** `rememberFilamentScene { }` (its result feeds the scene's parameters), so
 * the [engine] must be hoisted via [io.github.erkko68.filament.compose.rememberFilamentEngine]
 * and shared with the scene.
 *
 * The returned [IBLEnvironment] is non-null and stable; its states fill in once the KTX bytes
 * load. Failures (load lambda throwing, or undecodable KTX) are reported once via [onError] and
 * leave the corresponding state empty.
 *
 * @param engine    The hoisted engine, shared with the scene that consumes the returned states.
 * @param intensity IBL intensity scale.
 * @param key       Reloads when this changes. Defaults to [Unit] for static assets.
 * @param onError   Invoked once per failure (load threw, or the KTX failed to decode).
 * @param skybox    Optional loader for the skybox cubemap KTX. Null = IBL only, no background.
 * @param ibl       Loader for the IBL KTX (prefiltered reflection cubemap + irradiance SH).
 */
@Composable
fun rememberIBLEnvironment(
    engine: Engine,
    intensity: Float = 30_000f,
    key: Any = Unit,
    onError: ((Throwable) -> Unit)? = null,
    skybox: (suspend () -> ByteArray)? = null,
    ibl: suspend () -> ByteArray,
): IBLEnvironment {
    val indirectLightState = rememberIndirectLightState(intensity = intensity)
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

    return remember(indirectLightState, skyboxState) { IBLEnvironment(indirectLightState, skyboxState) }
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
