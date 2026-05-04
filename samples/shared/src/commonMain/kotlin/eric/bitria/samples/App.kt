package eric.bitria.samples

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import eric.bitria.samples.shared.resources.Res
import io.github.erkko68.filament.LightManager
import io.github.erkko68.filament.compose.FilamentView
import io.github.erkko68.filament.compose.orbitGestures
import io.github.erkko68.filament.compose.rememberOrbitCameraState
import io.github.erkko68.filament.compose.scene.*
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App() {
    val cameraState = rememberCameraState(
        eye        = Position(0f, 1f, 4f),
        target     = Position(0f, 0.5f, 0f),
        projection = Projection.Perspective(fovDegrees = 45.0),
    )
    val orbit = rememberOrbitCameraState(cameraState)
    val skybox = rememberSkyboxState(source = SkyboxSource.Color(Color(0.1f, 0.125f, 0.15f)))

    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            FilamentView(
                modifier = Modifier
                    .fillMaxSize()
                    .onSizeChanged { orbit.setViewport(it.width, it.height) }
                    .orbitGestures(orbit),
                cameraState = cameraState,
                skyboxState = skybox,
            ) {
                Light(
                    type      = LightManager.Type.DIRECTIONAL,
                    direction = Direction(0.3f, -1f, -0.5f),
                    intensity = 100_000f,
                )

                Bloom(strength = 0.2f)
                Fog(enabled = true, color = Color(0.1f, 0.125f, 0.15f), density = 0.05f)
                AntiAliasing(fxaaEnabled = true)

                GltfInstance(
                    asset = rememberGltfAsset { Res.readBytes("files/models/Duck.glb") },
                    position = Position(0f, 0f, 0f),
                    scale = Scale(1f),
                )
            }

            Text(
                "Filament KMP Compose DSL",
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 48.dp),
                color    = ComposeColor.White,
            )
        }
    }
}
