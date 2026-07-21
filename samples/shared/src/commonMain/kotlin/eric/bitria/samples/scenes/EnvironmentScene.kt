package eric.bitria.samples.scenes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import eric.bitria.samples.shared.resources.Res
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.compose.FilamentView
import io.github.erkko68.filament.compose.orbitGestures
import io.github.erkko68.filament.compose.rememberFilamentEngine
import io.github.erkko68.filament.compose.rememberFilamentScene
import io.github.erkko68.filament.compose.rememberOrbitCameraState
import io.github.erkko68.filament.compose.scene.Environment
import io.github.erkko68.filament.compose.scene.GltfInstance
import io.github.erkko68.filament.compose.scene.Position
import io.github.erkko68.filament.compose.scene.Projection
import io.github.erkko68.filament.compose.scene.rememberCameraState
import io.github.erkko68.filament.compose.scene.rememberGltfAsset
import io.github.erkko68.filament.compose.scene.rememberHDREnvironment
import io.github.erkko68.filament.compose.scene.rememberKTXEnvironment
import org.jetbrains.compose.resources.ExperimentalResourceApi

/**
 * Image-based lighting from a pre-baked **KTX** pair (generated from an HDR with Filament's
 * `cmgen`): the fast, offline-baked path with exact diffuse spherical harmonics.
 */
@OptIn(ExperimentalResourceApi::class)
@Composable
fun KTXEnvironmentScene(onBack: () -> Unit) {
    val engine = rememberFilamentEngine()
    val environment = rememberKTXEnvironment(
        engine = engine,
        ibl    = { Res.readBytes("files/environment/lightroom_ibl.ktx") },
        skybox = { Res.readBytes("files/environment/lightroom_skybox.ktx") },
    )
    EnvironmentSceneContent(onBack, engine, environment, source = "KTX (baked)")
}

/**
 * The same environment loaded from a raw equirectangular **HDR** — no `cmgen` step; the skybox
 * and reflections are prefiltered on the GPU at load. Diffuse is approximated, not baked.
 */
@OptIn(ExperimentalResourceApi::class)
@Composable
fun HDREnvironmentScene(onBack: () -> Unit) {
    val engine = rememberFilamentEngine()
    // Runtime HDR decoding isn't available on Web (filament.js has no Radiance/RGBE decoder), so the
    // load fails there. Surface it via onError and show a notice instead of an empty scene.
    var failed by remember { mutableStateOf(false) }
    val environment = rememberHDREnvironment(
        engine = engine,
        onError = { failed = true },
    ) { Res.readBytes("files/environment/lightroom.hdr") }

    if (failed) {
        UnsupportedNotice(
            onBack = onBack,
            message = "Runtime HDR decoding isn't supported on this platform (Web). " +
                "See the KTX environment sample for a pre-baked equivalent.",
        )
    } else {
        EnvironmentSceneContent(onBack, engine, environment, source = "HDR (runtime)")
    }
}

/** Centered message + back button shown when a sample can't run on the current platform. */
@Composable
private fun UnsupportedNotice(onBack: () -> Unit, message: String) {
    Box(Modifier.fillMaxSize()) {
        Text(message, Modifier.align(Alignment.Center).padding(32.dp))
        BackButton(onClick = onBack, modifier = Modifier.align(Alignment.TopStart))
    }
}

/**
 * Shared renderer for both environment samples: the Duck is lit *entirely* by [environment] (no
 * `Light` in the scene), the environment cubemap is the skybox, and the slider drives the IBL
 * intensity to show the state stays mutable after load.
 */
@Composable
private fun EnvironmentSceneContent(
    onBack: () -> Unit,
    engine: Engine,
    environment: Environment,
    source: String,
) {
    var intensity by remember { mutableStateOf(30_000f) }
    environment.indirectLightState.intensity = intensity

    val cameraState = rememberCameraState(
        initialEye        = Position(0f, 1.5f, 4f),
        initialTarget     = Position(0f, 0.5f, 0f),
        initialProjection = Projection.Perspective(fovDegrees = 45.0),
    )
    val orbit = rememberOrbitCameraState(cameraState)

    val scene = rememberFilamentScene(
        engine = engine,
        skyboxState = environment.skyboxState,
        indirectLightState = environment.indirectLightState,
    ) {
        GltfInstance(asset = rememberGltfAsset(engine = engine) { Res.readBytes("files/models/Duck.glb") })
    }

    Box(Modifier.fillMaxSize()) {
        FilamentView(
            scene = scene,
            modifier = Modifier
                .fillMaxSize()
                .orbitGestures(orbit),
            cameraState = cameraState,
        )

        Column(Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(24.dp)) {
            Text("Source: $source   ·   IBL intensity: ${intensity.toInt()}")
            Slider(value = intensity, onValueChange = { intensity = it }, valueRange = 0f..80_000f)
        }

        BackButton(onClick = onBack, modifier = Modifier.align(Alignment.TopStart))
    }
}
