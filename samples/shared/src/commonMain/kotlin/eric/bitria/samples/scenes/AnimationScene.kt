package eric.bitria.samples.scenes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import eric.bitria.samples.shared.resources.Res
import io.github.erkko68.filament.compose.FilamentSceneView
import io.github.erkko68.filament.compose.orbitGestures
import io.github.erkko68.filament.compose.rememberFilamentEngine
import io.github.erkko68.filament.compose.rememberOrbitCameraController
import io.github.erkko68.filament.compose.scene.LinearColor
import io.github.erkko68.filament.compose.scene.Direction
import io.github.erkko68.filament.compose.scene.GltfInstance
import io.github.erkko68.filament.compose.scene.LightIntensity
import io.github.erkko68.filament.compose.scene.DirectionalLight
import io.github.erkko68.filament.compose.scene.Position
import io.github.erkko68.filament.compose.scene.Projection
import io.github.erkko68.filament.compose.scene.Scale
import io.github.erkko68.filament.compose.scene.SkyboxSource
import io.github.erkko68.filament.compose.scene.rememberAnimationNames
import io.github.erkko68.filament.compose.scene.rememberAnimationState
import io.github.erkko68.filament.compose.scene.rememberCameraState
import io.github.erkko68.filament.compose.scene.rememberGltfAsset
import io.github.erkko68.filament.compose.scene.rememberSkyboxState
import org.jetbrains.compose.resources.ExperimentalResourceApi

/**
 * Switches between the three skeletal clips of `Fox.glb` (Survey / Walk / Run) with
 * [rememberAnimationState]. The state auto-advances every frame and, when [AnimationState.animationIndex]
 * changes, cross-fades from the outgoing clip over [AnimationState.crossFadeDuration] — drive that with
 * the slider to see hard cuts vs. smooth blends. Clip names come from [rememberAnimationNames].
 */
@OptIn(ExperimentalResourceApi::class)
@Composable
fun AnimationScene(onBack: () -> Unit) {
    val cameraState = rememberCameraState(
        initialEye        = Position(0f, 1.2f, 3.2f),
        initialTarget     = Position(0f, 0.8f, 0f),
        initialProjection = Projection.Perspective(fovDegrees = 45.0),
    )
    val orbit  = rememberOrbitCameraController(cameraState)
    val skybox = rememberSkyboxState(initialSource = SkyboxSource.Color(LinearColor(0.08f, 0.10f, 0.14f)))

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
            skyboxState = skybox,
        ) {
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

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.45f))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                clipNames.forEachIndexed { index, name ->
                    val selected = index == animation.animationIndex
                    Button(
                        onClick = { animation.animationIndex = index },
                        colors = if (selected) ButtonDefaults.buttonColors()
                                 else ButtonDefaults.outlinedButtonColors(),
                    ) { Text(name ?: "Clip $index") }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Cross-fade ${(crossFade * 1000).toInt()} ms",
                    color = Color.White,
                    modifier = Modifier.padding(end = 12.dp),
                )
                Slider(
                    value = crossFade,
                    onValueChange = { crossFade = it },
                    valueRange = 0f..1.5f,
                )
            }
        }

        BackButton(onClick = onBack, modifier = Modifier.align(Alignment.TopStart))
    }
}
