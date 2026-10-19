package eric.bitria.samples.scenes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import eric.bitria.samples.shared.resources.Res
import io.github.erkko68.filament.compose.FilamentSceneView
import io.github.erkko68.filament.compose.orbitGestures
import io.github.erkko68.filament.compose.rememberOrbitCameraController
import io.github.erkko68.filament.compose.scene.LinearColor
import io.github.erkko68.filament.compose.scene.Direction
import io.github.erkko68.filament.compose.scene.GltfInstance
import io.github.erkko68.filament.compose.scene.LightIntensity
import io.github.erkko68.filament.compose.scene.DirectionalLight
import io.github.erkko68.filament.compose.scene.Position
import io.github.erkko68.filament.compose.scene.Projection
import io.github.erkko68.filament.compose.scene.Scale
import io.github.erkko68.filament.compose.scene.Skybox
import io.github.erkko68.filament.compose.scene.rememberCameraState
import io.github.erkko68.filament.compose.scene.rememberGltfAsset
import org.jetbrains.compose.resources.ExperimentalResourceApi

private val DUCK_POSITIONS = listOf(
    Position(-1.5f, 0f, -1.5f),
    Position( 1.5f, 0f, -1.5f),
    Position(-1.5f, 0f,  1.5f),
    Position( 1.5f, 0f,  1.5f),
)

@OptIn(ExperimentalResourceApi::class)
@Composable
fun PickingScene(onBack: () -> Unit) {
    val cameraState = rememberCameraState(
        initialEye        = Position(0f, 4f, 5f),
        initialTarget     = Position(0f, 0f, 0f),
        initialProjection = Projection.Perspective(fovDegrees = 40.0),
    )
    val orbit = rememberOrbitCameraController(cameraState)
    var lastTapped by remember { mutableIntStateOf(-1) }

    Box(Modifier.fillMaxSize()) {
        FilamentSceneView(
            // Drags orbit; a tap that hits a duck reaches its onTap.
            modifier = Modifier.fillMaxSize().orbitGestures(orbit),
            cameraState = cameraState,
        ) {
            Skybox(color = LinearColor(0.08f, 0.10f, 0.14f))
            DirectionalLight(
                direction = Direction(0.3f, -1f, -0.5f),
                intensity = LightIntensity.LuminousPower(100_000f),
            )
            val duckAsset = rememberGltfAsset { Res.readBytes("files/models/Duck.glb") }
            DUCK_POSITIONS.forEachIndexed { idx, pos ->
                GltfInstance(
                    asset    = duckAsset,
                    position = pos,
                    scale    = Scale(1f),
                    onTap    = { lastTapped = idx },
                )
            }
        }
        BackButton(onClick = onBack, modifier = Modifier.align(Alignment.TopStart))
        Text(
            text     = if (lastTapped >= 0) "Duck #${lastTapped + 1}" else "Tap a duck",
            style    = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(bottom = 24.dp),
        )
    }
}
