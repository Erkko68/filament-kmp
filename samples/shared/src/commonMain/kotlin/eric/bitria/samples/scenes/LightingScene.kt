package eric.bitria.samples.scenes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import eric.bitria.samples.shared.resources.Res
import io.github.erkko68.filament.compose.FilamentSceneView
import io.github.erkko68.filament.compose.orbitGestures
import io.github.erkko68.filament.compose.rememberOrbitCameraController
import io.github.erkko68.filament.compose.scene.Direction
import io.github.erkko68.filament.compose.scene.DirectionalLight
import io.github.erkko68.filament.compose.scene.GltfInstance
import io.github.erkko68.filament.compose.scene.LightIntensity
import io.github.erkko68.filament.compose.scene.LinearColor
import io.github.erkko68.filament.compose.scene.PointLight
import io.github.erkko68.filament.compose.scene.Position
import io.github.erkko68.filament.compose.scene.Projection
import io.github.erkko68.filament.compose.scene.ShadowConfig
import io.github.erkko68.filament.compose.scene.Shadows
import io.github.erkko68.filament.compose.scene.Skybox
import io.github.erkko68.filament.compose.scene.SpotCone
import io.github.erkko68.filament.compose.scene.SpotLight
import io.github.erkko68.filament.compose.scene.primitives.Plane
import io.github.erkko68.filament.compose.scene.rememberCameraState
import io.github.erkko68.filament.compose.scene.rememberColorMaterialInstance
import io.github.erkko68.filament.compose.scene.rememberGltfAsset
import kotlin.math.cos
import kotlin.math.sin
import org.jetbrains.compose.resources.ExperimentalResourceApi

private enum class LightKind { Directional, Point, Spot }

/**
 * Interactive showcase of the typed light composables: switch between a [DirectionalLight],
 * [PointLight] and [SpotLight] and tweak their parameters live with sliders, lighting a glTF model
 * over a ground plane. Shadows (and soft PCSS shadows) can be toggled to see the per-light
 * [ShadowConfig] working together with the view-level [Shadows] technique.
 */
@OptIn(ExperimentalResourceApi::class)
@Composable
fun LightingScene() {
    val cameraState = rememberCameraState(
        initialEye        = Position(0f, 3f, 7f),
        initialTarget     = Position(0f, 1f, 0f),
        initialProjection = Projection.Perspective(fovDegrees = 45.0),
    )
    val orbit  = rememberOrbitCameraController(cameraState)
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
            shadows = if (!shadows) null
                      else if (soft) Shadows.Pcss()
                      else Shadows.Pcf,
        ) {
            Skybox(color = LinearColor(0.03f, 0.03f, 0.05f))
            when (kind) {
                LightKind.Directional -> DirectionalLight(
                    direction = dir,
                    intensity = LightIntensity.LuminousPower(intensity * 150_000f),
                    shadow    = if (shadows) ShadowConfig(mapSize = 4096) else null,
                )
                LightKind.Point -> PointLight(
                    position  = Position(0f, 4f, 2f),
                    color     = LinearColor(1f, 0.9f, 0.7f),
                    intensity = LightIntensity.LuminousPower(intensity * 20_000_000f),
                    falloff   = 25f,
                )
                LightKind.Spot -> SpotLight(
                    position  = Position(0f, 6f, 2f),
                    direction = dir,
                    intensity = LightIntensity.LuminousPower(intensity * 30_000_000f),
                    falloff   = 30f,
                    cone      = SpotCone(innerAngle = coneOuter * 0.6f, outerAngle = coneOuter),
                    shadow    = if (shadows) ShadowConfig(mapSize = 4096) else null,
                )
            }

            // Ground plane (receives shadows) + a glTF caster.
            Plane(
                material    = rememberColorMaterialInstance(LinearColor(0.35f, 0.35f, 0.4f)),
                width       = 14f,
                depth       = 14f,
                castShadows = false,  // pure ground receiver
            )
            GltfInstance(asset = rememberGltfAsset { Res.readBytes("files/models/Duck.glb") })
        }

        ControlPanel {
            ChoiceRow(LightKind.entries, kind, label = { it.name }) { kind = it }
            LabeledSlider("Intensity", intensity, 0f..1f) { intensity = it }
            if (kind != LightKind.Point) LabeledSlider("Azimuth", azimuth, 0f..6.28f) { azimuth = it }
            if (kind == LightKind.Spot) LabeledSlider("Cone", coneOuter, 0.15f..1.4f) { coneOuter = it }
            if (kind != LightKind.Point) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = shadows, onClick = { shadows = !shadows }, label = { Text("Shadows") })
                    FilterChip(selected = soft, onClick = { soft = !soft }, enabled = shadows, label = { Text("Soft (PCSS)") })
                }
            }
        }
    }
}
