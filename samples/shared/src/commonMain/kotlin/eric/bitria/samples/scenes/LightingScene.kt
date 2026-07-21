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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import eric.bitria.samples.shared.resources.Res
import io.github.erkko68.filament.compose.FilamentSceneView
import io.github.erkko68.filament.compose.orbitGestures
import io.github.erkko68.filament.compose.rememberOrbitCameraState
import io.github.erkko68.filament.compose.scene.Color as FilColor
import io.github.erkko68.filament.compose.scene.DirectionalLight
import io.github.erkko68.filament.compose.scene.Direction
import io.github.erkko68.filament.compose.scene.GltfInstance
import io.github.erkko68.filament.compose.scene.PointLight
import io.github.erkko68.filament.compose.scene.Position
import io.github.erkko68.filament.compose.scene.PostProcessing
import io.github.erkko68.filament.compose.scene.Projection
import io.github.erkko68.filament.compose.scene.ShadowConfig
import io.github.erkko68.filament.compose.scene.Shadows
import io.github.erkko68.filament.compose.scene.SpotCone
import io.github.erkko68.filament.compose.scene.SpotLight
import io.github.erkko68.filament.compose.scene.primitives.Plane
import io.github.erkko68.filament.compose.scene.rememberCameraState
import io.github.erkko68.filament.compose.scene.rememberColorMaterialInstance
import io.github.erkko68.filament.compose.scene.rememberGltfAsset
import io.github.erkko68.filament.compose.scene.rememberSkyboxState
import io.github.erkko68.filament.compose.scene.SkyboxSource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import kotlin.math.cos
import kotlin.math.sin

private enum class LightKind { Directional, Point, Spot }

/**
 * Interactive showcase of the typed light composables: switch between a [DirectionalLight],
 * [PointLight] and [SpotLight] and tweak their parameters live with sliders, lighting a glTF model
 * over a ground plane. Shadows (and soft PCSS shadows) can be toggled to see the per-light
 * [ShadowConfig] working together with the view-level [Shadows] technique.
 */
@OptIn(ExperimentalResourceApi::class)
@Composable
fun LightingScene(onBack: () -> Unit) {
    val cameraState = rememberCameraState(
        initialEye        = Position(0f, 3f, 7f),
        initialTarget     = Position(0f, 1f, 0f),
        initialProjection = Projection.Perspective(fovDegrees = 45.0),
    )
    val orbit  = rememberOrbitCameraState(cameraState)
    val skybox = rememberSkyboxState(initialSource = SkyboxSource.Color(FilColor(0.03f, 0.03f, 0.05f)))

    var kind by remember { mutableStateOf(LightKind.Directional) }
    var intensity by remember { mutableFloatStateOf(0.6f) }  // 0..1, mapped per type
    var azimuth by remember { mutableFloatStateOf(0.6f) }    // radians, around Y
    var coneOuter by remember { mutableFloatStateOf(0.6f) }  // radians, spot outer half-angle
    var shadows by remember { mutableStateOf(true) }
    var soft by remember { mutableStateOf(false) }

    // A downward light direction swept around Y by the azimuth slider.
    val dir = Direction(sin(azimuth) * 0.7f, -1f, cos(azimuth) * 0.7f)

    Box(Modifier.fillMaxSize()) {
        FilamentSceneView(
            modifier = Modifier
                .fillMaxSize()
                .orbitGestures(orbit),
            cameraState = cameraState,
            skyboxState = skybox,
            shadows = if (!shadows) null
                      else if (soft) Shadows.Pcss()
                      else Shadows.Pcf,
        ) {
            when (kind) {
                LightKind.Directional -> DirectionalLight(
                    direction = dir,
                    intensity = intensity * 150_000f,
                    shadow    = if (shadows) ShadowConfig(mapSize = 4096, bulbRadius = 0.03f) else null,
                )
                LightKind.Point -> PointLight(
                    position  = Position(0f, 4f, 2f),
                    color     = FilColor(1f, 0.9f, 0.7f),
                    intensity = intensity * 20_000_000f,
                    falloff   = 25f,
                )
                LightKind.Spot -> SpotLight(
                    position  = Position(0f, 6f, 2f),
                    direction = dir,
                    intensity = intensity * 30_000_000f,
                    falloff   = 30f,
                    cone      = SpotCone(innerAngle = coneOuter * 0.6f, outerAngle = coneOuter),
                    shadow    = if (shadows) ShadowConfig(mapSize = 4096, bulbRadius = 0.05f) else null,
                )
            }

            // Ground plane (receives shadows) + a glTF caster.
            Plane(
                material    = rememberColorMaterialInstance(FilColor(0.35f, 0.35f, 0.4f)),
                width       = 14f,
                depth       = 14f,
                castShadows = false,  // pure ground receiver
            )
            GltfInstance(asset = rememberGltfAsset { Res.readBytes("files/models/Duck.glb") })
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
                LightKind.entries.forEach { k ->
                    val selected = k == kind
                    Button(
                        onClick = { kind = k },
                        colors = if (selected) ButtonDefaults.buttonColors()
                                 else ButtonDefaults.outlinedButtonColors(),
                    ) { Text(k.name) }
                }
            }
            LabeledSlider("Intensity", intensity, 0f..1f) { intensity = it }
            if (kind != LightKind.Point) {
                LabeledSlider("Azimuth", azimuth, 0f..6.28f) { azimuth = it }
            }
            if (kind == LightKind.Spot) {
                LabeledSlider("Cone", coneOuter, 0.15f..1.4f) { coneOuter = it }
            }
            if (kind != LightKind.Point) {
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Shadows", color = Color.White)
                    Switch(checked = shadows, onCheckedChange = { shadows = it })
                    Text("Soft (PCSS)", color = Color.White)
                    Switch(checked = soft, onCheckedChange = { soft = it }, enabled = shadows)
                }
            }
        }

        BackButton(onClick = onBack, modifier = Modifier.align(Alignment.TopStart))
    }
}

@Composable
private fun LabeledSlider(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = Color.White, modifier = Modifier.padding(end = 12.dp))
        Slider(value = value, onValueChange = onValueChange, valueRange = range)
    }
}
