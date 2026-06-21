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
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import eric.bitria.samples.shared.resources.Res
import io.github.erkko68.filament.compose.FilamentView
import io.github.erkko68.filament.compose.orbitGestures
import io.github.erkko68.filament.compose.rememberFilamentEngine
import io.github.erkko68.filament.compose.rememberFilamentScene
import io.github.erkko68.filament.compose.rememberOrbitCameraState
import io.github.erkko68.filament.compose.scene.GltfInstance
import io.github.erkko68.filament.compose.scene.Position
import io.github.erkko68.filament.compose.scene.Projection
import io.github.erkko68.filament.compose.scene.rememberCameraState
import io.github.erkko68.filament.compose.scene.rememberGltfAsset
import io.github.erkko68.filament.compose.scene.rememberIBLEnvironment
import org.jetbrains.compose.resources.ExperimentalResourceApi

/**
 * Image-based lighting: the Duck is lit *entirely* by the environment — no [Light] in the
 * scene — and the environment cubemap is drawn as the skybox. [rememberIBLEnvironment] loads
 * both from a pair of KTX files (generated from an HDR with Filament's `cmgen`) and hands back
 * the skybox + IBL states to feed straight into [rememberFilamentScene].
 *
 * The slider drives `indirectLightState.intensity` to show the state stays mutable after load.
 */
@OptIn(ExperimentalResourceApi::class)
@Composable
fun EnvironmentScene(onBack: () -> Unit) {
    // IBL must share the scene's engine, so hoist it explicitly.
    val engine = rememberFilamentEngine()

    val environment = rememberIBLEnvironment(
        engine = engine,
        ibl    = { Res.readBytes("files/environment/lightroom_ibl.ktx") },
        skybox = { Res.readBytes("files/environment/lightroom_skybox.ktx") },
    )

    var intensity by remember { mutableStateOf(30_000f) }
    environment.indirectLightState.intensity = intensity

    val cameraState = rememberCameraState(
        eye        = Position(0f, 1.5f, 4f),
        target     = Position(0f, 0.5f, 0f),
        projection = Projection.Perspective(fovDegrees = 45.0),
    )
    val orbit = rememberOrbitCameraState(cameraState)

    val scene = rememberFilamentScene(
        engine = engine,
        skyboxState = environment.skyboxState,
        indirectLightState = environment.indirectLightState,
    ) {
        // No Light{} — the IBL is the only light source.
        GltfInstance(asset = rememberGltfAsset(engine) { Res.readBytes("files/models/Duck.glb") })
    }

    Box(Modifier.fillMaxSize()) {
        FilamentView(
            scene = scene,
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { orbit.setViewport(it.width, it.height) }
                .orbitGestures(orbit),
            cameraState = cameraState,
        )

        Column(
            Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(24.dp),
        ) {
            Text("IBL intensity: ${intensity.toInt()}")
            Slider(
                value = intensity,
                onValueChange = { intensity = it },
                valueRange = 0f..80_000f,
            )
        }

        BackButton(onClick = onBack, modifier = Modifier.align(Alignment.TopStart))
    }
}
