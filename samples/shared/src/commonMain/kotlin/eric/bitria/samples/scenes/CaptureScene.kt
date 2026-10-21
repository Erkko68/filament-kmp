package eric.bitria.samples.scenes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import io.github.erkko68.filament.compose.FilamentSceneView
import io.github.erkko68.filament.compose.orbitGestures
import io.github.erkko68.filament.compose.rememberFilamentViewState
import io.github.erkko68.filament.compose.rememberOrbitCameraController
import io.github.erkko68.filament.compose.scene.Direction
import io.github.erkko68.filament.compose.scene.DirectionalLight
import io.github.erkko68.filament.compose.scene.LightIntensity
import io.github.erkko68.filament.compose.scene.LinearColor
import io.github.erkko68.filament.compose.scene.Position
import io.github.erkko68.filament.compose.scene.ShadowConfig
import io.github.erkko68.filament.compose.scene.Skybox
import io.github.erkko68.filament.compose.scene.primitives.Cube
import io.github.erkko68.filament.compose.scene.primitives.Plane
import io.github.erkko68.filament.compose.scene.primitives.Sphere
import io.github.erkko68.filament.compose.scene.rememberCameraState
import io.github.erkko68.filament.compose.scene.rememberColorMaterialInstance
import io.github.erkko68.filament.compose.scene.rememberShadowCatcherMaterialInstance
import kotlinx.coroutines.launch

/** `FilamentViewState.capture()` shown beside the live view, over a shadow-catcher ground that shows only the shadows. */
@Composable
fun CaptureScene(onBack: () -> Unit) {
    val cameraState = rememberCameraState(initialEye = Position(0f, 2f, 6f), initialTarget = Position(0f, 0.8f, 0f))
    val viewState = rememberFilamentViewState()
    val scope = rememberCoroutineScope()
    var shot by remember { mutableStateOf<ImageBitmap?>(null) }
    Box(Modifier.fillMaxSize()) {
        FilamentSceneView(
            modifier = Modifier.fillMaxSize().orbitGestures(rememberOrbitCameraController(cameraState)),
            cameraState = cameraState,
            viewState = viewState,
        ) {
            Skybox(color = LinearColor(0.35f, 0.55f, 0.35f))
            DirectionalLight(
                direction = Direction(0.4f, -1f, -0.3f),
                intensity = LightIntensity.LuminousPower(100_000f),
                shadow = ShadowConfig(mapSize = 2048),
            )
            Cube(rememberColorMaterialInstance(LinearColor(0.9f, 0.15f, 0.15f)), position = Position(-1.5f, 2.2f, 0f), size = 0.8f)
            Sphere(rememberColorMaterialInstance(LinearColor(0.15f, 0.35f, 0.95f)), position = Position(1.5f, 0.5f, 0f), radius = 0.5f)
            Plane(rememberShadowCatcherMaterialInstance(), width = 10f, depth = 10f, castShadows = false)
        }

        Column(Modifier.align(Alignment.TopEnd).padding(16.dp), horizontalAlignment = Alignment.End) {
            Button(onClick = { scope.launch { shot = viewState.capture() } }) { Text("Capture") }
            shot?.let {
                Image(it, "capture", Modifier.padding(top = 8.dp).width(240.dp).border(2.dp, Color.White).background(Color.Black))
                Text("${it.width}×${it.height}", color = Color.White)
            }
        }

        BackButton(onClick = onBack, modifier = Modifier.align(Alignment.TopStart))
    }
}
