package eric.bitria.samples.scenes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import eric.bitria.samples.shared.resources.Res
import io.github.erkko68.filament.compose.FilamentSceneView
import io.github.erkko68.filament.compose.orbitGestures
import io.github.erkko68.filament.compose.rememberFilamentEngine
import io.github.erkko68.filament.compose.rememberOrbitCameraController
import io.github.erkko68.filament.compose.scene.Direction
import io.github.erkko68.filament.compose.scene.DirectionalLight
import io.github.erkko68.filament.compose.scene.GltfInstance
import io.github.erkko68.filament.compose.scene.LightIntensity
import io.github.erkko68.filament.compose.scene.LinearColor
import io.github.erkko68.filament.compose.scene.Position
import io.github.erkko68.filament.compose.scene.Projection
import io.github.erkko68.filament.compose.scene.Scale
import io.github.erkko68.filament.compose.scene.Skybox
import io.github.erkko68.filament.compose.scene.rememberAnimationNames
import io.github.erkko68.filament.compose.scene.rememberAnimationState
import io.github.erkko68.filament.compose.scene.rememberCameraState
import io.github.erkko68.filament.compose.scene.rememberGltfAsset
import org.jetbrains.compose.resources.ExperimentalResourceApi

/**
 * Switches between the three skeletal clips of `Fox.glb` (Survey / Walk / Run) with
 * [rememberAnimationState]. The state auto-advances every frame and, when [AnimationState.animationIndex]
 * changes, cross-fades from the outgoing clip over [AnimationState.crossFadeDuration] — drive that with
 * the slider to see hard cuts vs. smooth blends. Clip names come from [rememberAnimationNames].
 */
@OptIn(ExperimentalResourceApi::class)
@Composable
fun AnimationScene() {
    val cameraState = rememberCameraState(
        initialEye        = Position(0f, 1.2f, 3.2f),
        initialTarget     = Position(0f, 0.8f, 0f),
        initialProjection = Projection.Perspective(fovDegrees = 45.0),
    )
    val orbit  = rememberOrbitCameraController(cameraState)
    // Hoist the engine so the asset (and its clip names) can be loaded outside the scene content.
    val engine = rememberFilamentEngine()
    val fox = rememberGltfAsset(engine = engine) { Res.readBytes("files/models/Fox.glb") }
    val clipNames = rememberAnimationNames(fox)

    var crossFade by remember { mutableFloatStateOf(0.3f) }
    val animation = rememberAnimationState(initialAnimationIndex = 0, initialCrossFadeDuration = crossFade)
    // Keep the live state in sync with the slider.
    animation.crossFadeDuration = crossFade

    Box(Modifier.fillMaxSize()) {
        FilamentSceneView(
            modifier = Modifier
                .fillMaxSize()
                .orbitGestures(orbit),
            engine = engine,
            cameraState = cameraState,
        ) {
            Skybox(color = LinearColor(0.08f, 0.10f, 0.14f))
            DirectionalLight(
                direction = Direction(0.3f, -1f, -0.5f),
                intensity = LightIntensity.LuminousPower(100_000f),
            )
            GltfInstance(
                asset          = fox,
                position       = Position(0f, 0f, 0f),
                // Fox is authored very large (~140 units tall); scale it down to frame.
                scale          = Scale(0.018f),
                animationState = animation,
            )
        }

        ControlPanel {
            ChoiceRow(clipNames.indices.toList(), animation.animationIndex ?: -1, label = { clipNames[it] ?: "Clip $it" }) {
                animation.animationIndex = it
            }
            LabeledSlider("Fade ${(crossFade * 1000).toInt()} ms", crossFade, 0f..1.5f) { crossFade = it }
        }

        // The asset arrives before its GPU upload finishes: wait for isReady, not just non-null.
        if (fox?.isReady != true) {
            LinearProgressIndicator(
                progress = { fox?.progress ?: 0f },
                modifier = Modifier.align(Alignment.Center).padding(horizontal = 64.dp).fillMaxWidth(),
            )
        }
    }
}
