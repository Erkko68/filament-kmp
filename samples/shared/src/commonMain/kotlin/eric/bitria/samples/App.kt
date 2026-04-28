package eric.bitria.samples

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
    var duckBytes by remember { mutableStateOf<ByteArray?>(null) }

    LaunchedEffect(Unit) {
        try {
            duckBytes = Res.readBytes("files/models/Duck.glb")
        } catch (e: Exception) {
            println("App: Failed to load Duck.glb: ${e.message}")
        }
    }

    val orbit = rememberOrbitCameraState(
        eye    = Position(0f, 1f, 4f),
        target = Position(0f, 0.5f, 0f),
    )

    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            FilamentView(
                modifier = Modifier
                    .fillMaxSize()
                    .onSizeChanged { orbit.setViewport(it.width, it.height) }
                    .orbitGestures(orbit),
            ) {
                Skybox(source = SkyboxSource.Color(Color(0.1f, 0.125f, 0.15f)))

                Light(
                    type      = LightManager.Type.DIRECTIONAL,
                    direction = Direction(0.3f, -1f, -0.5f),
                    intensity = 100_000f,
                )

                Camera(
                    eye        = orbit.eye,
                    target     = orbit.target,
                    up         = orbit.up,
                    projection = Projection.Perspective(fovDegrees = 45.0),
                )

                Bloom(strength = 0.2f)
                Fog(enabled = true, color = Color(0.1f, 0.125f, 0.15f), density = 0.05f)
                AntiAliasing(fxaaEnabled = true)

                duckBytes?.let { bytes ->
                    val duck = rememberGltfAsset(bytes)
                    GltfInstance(duck, position = Position(0f, 0f, 0f), scale = Scale(1f))
                }
            }

            Text(
                "Filament KMP Compose DSL",
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 48.dp),
                color    = ComposeColor.White,
            )
        }
    }
}
