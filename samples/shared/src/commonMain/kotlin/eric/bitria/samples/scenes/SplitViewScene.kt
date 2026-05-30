package eric.bitria.samples.scenes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import eric.bitria.samples.shared.resources.Res
import io.github.erkko68.filament.LightManager
import io.github.erkko68.filament.compose.FilamentView
import io.github.erkko68.filament.compose.orbitGestures
import io.github.erkko68.filament.compose.rememberFilamentScene
import io.github.erkko68.filament.compose.rememberOrbitCameraState
import io.github.erkko68.filament.compose.rememberSceneClock
import io.github.erkko68.filament.compose.scene.Color as FilColor
import io.github.erkko68.filament.compose.scene.Direction
import io.github.erkko68.filament.compose.scene.GltfInstance
import io.github.erkko68.filament.compose.scene.Light
import io.github.erkko68.filament.compose.scene.PostProcessing
import io.github.erkko68.filament.compose.scene.Position
import io.github.erkko68.filament.compose.scene.Projection
import io.github.erkko68.filament.compose.scene.Scale
import io.github.erkko68.filament.compose.scene.SkyboxSource
import io.github.erkko68.filament.compose.scene.Vignette
import io.github.erkko68.filament.compose.scene.rememberCameraState
import io.github.erkko68.filament.compose.scene.rememberGltfAsset
import io.github.erkko68.filament.compose.scene.rememberSkyboxState
import org.jetbrains.compose.resources.ExperimentalResourceApi

/**
 * One `rememberFilamentScene` (the animated `BoxAnimated.glb`) rendered through two
 * `FilamentView`s side by side. The scene — geometry, light, animation — is declared once and
 * shared; each view brings its own camera and post-processing value. The left view is an
 * orbitable perspective; the right is a top-down camera with a vignette, showing that view
 * configuration is per-view and independent of the shared world.
 */
@OptIn(ExperimentalResourceApi::class)
@Composable
fun SplitViewScene(onBack: () -> Unit) {
    val leftCamera = rememberCameraState(
        eye        = Position(0f, 2f, 5f),
        target     = Position(0f, 0.5f, 0f),
        projection = Projection.Perspective(fovDegrees = 45.0),
    )
    val leftOrbit = rememberOrbitCameraState(leftCamera)

    // Top-down: looking straight down the Y axis, so the "up" direction is -Z.
    val topCamera = rememberCameraState(
        eye        = Position(0f, 7f, 0f),
        target     = Position(0f, 0.5f, 0f),
        up         = Direction(0f, 0f, -1f),
        projection = Projection.Perspective(fovDegrees = 45.0),
    )

    val skybox = rememberSkyboxState(source = SkyboxSource.Color(FilColor(0.08f, 0.10f, 0.14f)))

    val time by rememberSceneClock()

    // Declared once, rendered by both views below.
    val scene = rememberFilamentScene(skyboxState = skybox) {
        Light(
            type      = LightManager.Type.DIRECTIONAL,
            direction = Direction(0.3f, -1f, -0.5f),
            intensity = 100_000f,
        )
        GltfInstance(
            asset          = rememberGltfAsset { Res.readBytes("files/models/BoxAnimated.glb") },
            position       = Position(0f, 0f, 0f),
            scale          = Scale(1f),
            animationIndex = 0,
            animationTime  = time,
        )
    }

    Box(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxSize()) {
            ViewPane(label = "Perspective", modifier = Modifier.weight(1f)) {
                FilamentView(
                    scene = scene,
                    modifier = Modifier
                        .fillMaxSize()
                        .onSizeChanged { leftOrbit.setViewport(it.width, it.height) }
                        .orbitGestures(leftOrbit),
                    cameraState = leftCamera,
                )
            }

            Box(Modifier.fillMaxHeight().width(1.dp).background(Color.Black.copy(alpha = 0.4f)))

            ViewPane(label = "Top-down + vignette", modifier = Modifier.weight(1f)) {
                FilamentView(
                    scene = scene,
                    modifier = Modifier.fillMaxSize(),
                    cameraState = topCamera,
                    postProcessing = PostProcessing(vignette = Vignette()),
                )
            }
        }
        BackButton(onClick = onBack, modifier = Modifier.align(Alignment.TopStart))
    }
}

@Composable
private fun ViewPane(
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(modifier) {
        content()
        Text(
            text     = label,
            style    = MaterialTheme.typography.labelMedium,
            color    = Color.White,
            modifier = Modifier.align(Alignment.BottomCenter).padding(12.dp),
        )
    }
}
