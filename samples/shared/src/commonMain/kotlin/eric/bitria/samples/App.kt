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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import eric.bitria.samples.shared.resources.Res
import io.github.erkko68.filament.compose.FilamentView
import io.github.erkko68.filament.compose.scene.*
import io.github.erkko68.filament.utils.Float3
import io.github.erkko68.filament.utils.Quaternion
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App() {
    var duckBytes by remember { mutableStateOf<ByteArray?>(null) }
    
    LaunchedEffect(Unit) {
        try {
            duckBytes = Res.readBytes("files/models/Duck.glb")
            println("App: Loaded Duck.glb from resources")
        } catch (e: Exception) {
            println("App: Failed to load Duck.glb: ${e.message}")
        }
    }

    var rotationAngle by remember { mutableStateOf(0f) }
    
    FilamentRenderLoop { frameTimeNanos ->
        rotationAngle = (frameTimeNanos / 1_000_000_000f) * 45f // 45 degrees per second
    }

    var menuExpanded by remember { mutableStateOf(false) }
    val menuWidth by animateDpAsState(if (menuExpanded) 200.dp else 70.dp)

    MaterialTheme {
        Row(modifier = Modifier.fillMaxSize()) {
            // Expandable side menu
            Column(
                modifier = Modifier
                    .width(menuWidth)
                    .fillMaxHeight()
                    .background(Color.DarkGray)
                    .padding(8.dp)
            ) {
                Button(onClick = { menuExpanded = !menuExpanded }) {
                    Text(if (menuExpanded) "<" else ">")
                }

                if (menuExpanded) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Menu Item 1", color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Menu Item 2", color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Menu Item 3", color = Color.White)
                }
            }

            // Main Filament content
            Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                FilamentView(
                    modifier = Modifier.fillMaxSize()
                ) {
                    ColorSkybox(r = 0.1f, g = 0.125f, b = 0.15f)
                    
                    DirectionalLight(
                        direction = Float3(0.3f, -1f, -0.5f),
                        intensity = 100_000f
                    )
                    
                    PerspectiveCamera(
                        eye = Float3(0f, 1f, 4f),
                        target = Float3(0f, 0.5f, 0f)
                    )

                    duckBytes?.let { bytes ->
                        GltfModel(
                            bytes = bytes,
                            position = Float3(0f, 0f, 0f),
                            rotation = Quaternion.fromAxisAngle(Float3(0f, 1f, 0f), rotationAngle),
                            scale = Float3(1f)
                        )
                    }
                }

                Text(
                    "Filament KMP Compose DSL",
                    modifier = Modifier.align(Alignment.TopCenter).padding(top = 48.dp),
                    color = Color.White
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
