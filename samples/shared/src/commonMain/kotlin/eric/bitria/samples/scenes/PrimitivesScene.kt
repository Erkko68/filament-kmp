package eric.bitria.samples.scenes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.github.erkko68.filament.compose.FilamentSceneView
import io.github.erkko68.filament.compose.orbitGestures
import io.github.erkko68.filament.compose.rememberOrbitCameraController
import io.github.erkko68.filament.compose.scene.Direction
import io.github.erkko68.filament.compose.scene.DirectionalLight
import io.github.erkko68.filament.compose.scene.IndirectLight
import io.github.erkko68.filament.compose.scene.LightIntensity
import io.github.erkko68.filament.compose.scene.LinearColor
import io.github.erkko68.filament.compose.scene.Position
import io.github.erkko68.filament.compose.scene.Projection
import io.github.erkko68.filament.compose.scene.ShadowConfig
import io.github.erkko68.filament.compose.scene.Skybox
import io.github.erkko68.filament.compose.scene.SphericalHarmonics
import io.github.erkko68.filament.compose.scene.primitives.Cube
import io.github.erkko68.filament.compose.scene.primitives.Cylinder
import io.github.erkko68.filament.compose.scene.primitives.Plane
import io.github.erkko68.filament.compose.scene.primitives.Sphere
import io.github.erkko68.filament.compose.scene.rememberCameraState
import io.github.erkko68.filament.compose.scene.rememberColorMaterialInstance

@Composable
fun PrimitivesScene() {
    val cameraState = rememberCameraState(
        initialEye        = Position(0f, 2.5f, 6f),
        initialTarget     = Position(0f, 0.5f, 0f),
        initialProjection = Projection.Perspective(fovDegrees = 45.0),
    )
    val orbit  = rememberOrbitCameraController(cameraState)
    var ambient by remember { mutableStateOf(AMBIENTS[1]) }
    var sun by remember { mutableStateOf(true) }
    Box(Modifier.fillMaxSize()) {
        FilamentSceneView(
            modifier = Modifier
                .fillMaxSize()
                .orbitGestures(orbit),
            cameraState = cameraState,
        ) {
            Skybox(color = LinearColor(0.08f, 0.10f, 0.14f))
            // A built-in ambient: the shadowed sides keep their shape without an environment file.
            ambient.second?.let { IndirectLight(irradiance = it) }
            if (sun) {
                DirectionalLight(
                    direction = Direction(0.3f, -1f, -0.5f),
                    intensity = LightIntensity.LuminousPower(100_000f),
                    // A shadow map for this light; the primitives cast and receive shadows by default.
                    shadow    = ShadowConfig(mapSize = 2048),
                )
            }
            // Built-in LIT colour material — no .mat to author or ship.
            Cube(    material = rememberColorMaterialInstance(LinearColor(0.90f, 0.25f, 0.30f)),
                     position = Position(-2f, 0.5f, 0f), size = 0.75f)
            Sphere(  material = rememberColorMaterialInstance(LinearColor(0.20f, 0.55f, 0.95f)),
                     position = Position( 2f, 0.5f, 0f), radius = 0.48f)
            Cylinder(material = rememberColorMaterialInstance(LinearColor(0.95f, 0.82f, 0.25f)),
                     position = Position(0f, 0.6f, -2f), radius = 0.3f, height = 1.2f)
            Plane(   material = rememberColorMaterialInstance(LinearColor(0.28f, 0.30f, 0.36f)),
                     position = Position(0f, -0.01f, 0f), width = 9f, depth = 9f)
        }
        ControlPanel {
            ChoiceRow(AMBIENTS, ambient, label = { it.first }, title = "Ambient") { ambient = it }
            FilterChip(selected = sun, onClick = { sun = !sun }, label = { Text("Sun + shadows") })
        }
    }
}

private val AMBIENTS = listOf(
    "None" to null,
    "Neutral studio" to SphericalHarmonics.NeutralStudio,
    "Cool studio" to SphericalHarmonics.CoolStudio,
    "Daylight" to SphericalHarmonics.Daylight,
    "Sunset" to SphericalHarmonics.Sunset,
    "Warm" to SphericalHarmonics.Warm,
)
