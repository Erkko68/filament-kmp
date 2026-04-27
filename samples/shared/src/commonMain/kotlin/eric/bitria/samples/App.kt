package eric.bitria.samples

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.unit.dp
import eric.bitria.samples.shared.resources.Res
import io.github.erkko68.filament.LightManager
import io.github.erkko68.filament.compose.FilamentView
import io.github.erkko68.filament.compose.scene.*
import io.github.erkko68.filament.utils.Quaternion
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

    var rotationAngle by remember { mutableStateOf(0f) }

    FilamentRenderLoop { frameTimeNanos ->
        rotationAngle = (frameTimeNanos / 1_000_000_000f) * 45f
    }

    var menuExpanded by remember { mutableStateOf(false) }
    val menuWidth by animateDpAsState(if (menuExpanded) 200.dp else 70.dp)

    MaterialTheme {
        Row(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .width(menuWidth)
                    .fillMaxHeight()
                    .background(ComposeColor.DarkGray)
                    .padding(8.dp)
            ) {
                Button(onClick = { menuExpanded = !menuExpanded }) {
                    Text(if (menuExpanded) "<" else ">")
                }
                if (menuExpanded) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Menu Item 1", color = ComposeColor.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Menu Item 2", color = ComposeColor.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Menu Item 3", color = ComposeColor.White)
                }
            }

            Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                FilamentView(modifier = Modifier.fillMaxSize()) {

                    Skybox(source = SkyboxSource.Color(Color(0.1f, 0.125f, 0.15f)))

                    Light(
                        type      = LightManager.Type.DIRECTIONAL,
                        direction = Direction(0.3f, -1f, -0.5f),
                        intensity = 100_000f,
                    )

                    Camera(
                        eye        = Position(0f, 1f, 4f),
                        target     = Position(0f, 0.5f, 0f),
                        projection = Projection.Perspective(fovDegrees = 45.0),
                    )

                    Bloom(strength = 0.2f)
                    Fog(enabled = true, color = Color(0.1f, 0.125f, 0.15f), density = 0.05f)
                    AntiAliasing(fxaaEnabled = true)

                    duckBytes?.let { bytes ->
                        GltfModel(
                            bytes          = bytes,
                            position       = Position(0f, 0f, 0f),
                            rotation       = Quaternion.fromAxisAngle(Direction(0f, 1f, 0f), rotationAngle),
                            scale          = Scale(1f),
                        )
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
}

@Composable
internal fun FilamentRenderLoop(onFrame: (Long) -> Unit) {
    val currentOnFrame by rememberUpdatedState(onFrame)
    LaunchedEffect(Unit) {
        while (true) {
            withFrameNanos { frameTimeNanos ->
                currentOnFrame(frameTimeNanos)
            }
        }
    }
}
