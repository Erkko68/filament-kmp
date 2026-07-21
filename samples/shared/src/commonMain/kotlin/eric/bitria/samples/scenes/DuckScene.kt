package eric.bitria.samples.scenes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import eric.bitria.samples.shared.resources.Res
import io.github.erkko68.filament.compose.FilamentSceneView
import io.github.erkko68.filament.compose.orbitGestures
import io.github.erkko68.filament.compose.rememberOrbitCameraController
import io.github.erkko68.filament.compose.scene.Color as FilColor
import io.github.erkko68.filament.compose.scene.Direction
import io.github.erkko68.filament.compose.scene.GltfInstance
import io.github.erkko68.filament.compose.scene.LightIntensity
import io.github.erkko68.filament.compose.scene.DirectionalLight
import io.github.erkko68.filament.compose.scene.Position
import io.github.erkko68.filament.compose.scene.Projection
import io.github.erkko68.filament.compose.scene.Scale
import io.github.erkko68.filament.compose.scene.SkyboxSource
import io.github.erkko68.filament.compose.scene.rememberCameraState
import io.github.erkko68.filament.compose.scene.rememberGltfAsset
import io.github.erkko68.filament.compose.scene.rememberSkyboxState
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
@Composable
fun DuckScene(onBack: () -> Unit) {
    val cameraState = rememberCameraState(
        initialEye        = Position(0f, 2f, 5f),
        initialTarget     = Position(0f, 0.5f, 0f),
        initialProjection = Projection.Perspective(fovDegrees = 45.0),
    )
    val orbit  = rememberOrbitCameraController(cameraState)
    val skybox = rememberSkyboxState(initialSource = SkyboxSource.Color(FilColor(0.08f, 0.10f, 0.14f)))

    Box(Modifier.fillMaxSize()) {
        // Single-view convenience: scene declaration + one viewport in one call.
        FilamentSceneView(
            modifier = Modifier
                .fillMaxSize()
                .orbitGestures(orbit),
            cameraState = cameraState,
            skyboxState = skybox,
        ) {
            DirectionalLight(
                direction = Direction(0.3f, -1f, -0.5f),
                intensity = LightIntensity.LuminousPower(100_000f),
            )
            GltfInstance(
                asset    = rememberGltfAsset { Res.readBytes("files/models/Duck.glb") },
                position = Position(0f, 0f, 0f),
                scale    = Scale(1f),
            )
        }
        BackButton(onClick = onBack, modifier = Modifier.align(Alignment.TopStart))
    }
}
