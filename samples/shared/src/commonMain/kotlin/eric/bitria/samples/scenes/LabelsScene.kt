package eric.bitria.samples.scenes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.erkko68.filament.compose.FilamentSceneView
import io.github.erkko68.filament.compose.orbitGestures
import io.github.erkko68.filament.compose.rememberOrbitCameraController
import io.github.erkko68.filament.compose.scene.Direction
import io.github.erkko68.filament.compose.scene.DirectionalLight
import io.github.erkko68.filament.compose.scene.LightIntensity
import io.github.erkko68.filament.compose.scene.LinearColor
import io.github.erkko68.filament.compose.scene.Position
import io.github.erkko68.filament.compose.scene.Projection
import io.github.erkko68.filament.compose.scene.ShadowConfig
import io.github.erkko68.filament.compose.scene.Skybox
import io.github.erkko68.filament.compose.scene.anchorTo
import io.github.erkko68.filament.compose.scene.primitives.Cube
import io.github.erkko68.filament.compose.scene.primitives.Cylinder
import io.github.erkko68.filament.compose.scene.primitives.Plane
import io.github.erkko68.filament.compose.scene.primitives.Sphere
import io.github.erkko68.filament.compose.scene.rememberCameraState
import io.github.erkko68.filament.compose.scene.rememberColorMaterialInstance

private val CUBE     = Position(-2f, 0.5f, 0f)
private val SPHERE   = Position( 2f, 0.5f, 0f)
private val CYLINDER = Position( 0f, 0.6f, -2f)

/** Compose UI pinned to 3D points with `Modifier.anchorTo`: orbit and the labels follow their objects. */
@Composable
fun LabelsScene(onBack: () -> Unit) {
    val cameraState = rememberCameraState(
        initialEye        = Position(0f, 2.5f, 6f),
        initialTarget     = Position(0f, 0.5f, 0f),
        initialProjection = Projection.Perspective(fovDegrees = 45.0),
    )
    val orbit = rememberOrbitCameraController(cameraState)
    Box(Modifier.fillMaxSize()) {
        FilamentSceneView(
            modifier = Modifier.fillMaxSize().orbitGestures(orbit),
            cameraState = cameraState,
        ) {
            Skybox(color = LinearColor(0.08f, 0.10f, 0.14f))
            DirectionalLight(
                direction = Direction(0.3f, -1f, -0.5f),
                intensity = LightIntensity.LuminousPower(100_000f),
                shadow    = ShadowConfig(mapSize = 2048),
            )
            Cube(rememberColorMaterialInstance(LinearColor(0.90f, 0.25f, 0.30f)), position = CUBE, size = 0.75f)
            Sphere(rememberColorMaterialInstance(LinearColor(0.20f, 0.55f, 0.95f)), position = SPHERE, radius = 0.48f)
            Cylinder(rememberColorMaterialInstance(LinearColor(0.95f, 0.82f, 0.25f)), position = CYLINDER, radius = 0.3f, height = 1.2f)
            Plane(rememberColorMaterialInstance(LinearColor(0.28f, 0.30f, 0.36f)), position = Position(0f, -0.01f, 0f), width = 9f, depth = 9f)
        }

        // Each label sits just above its object; a point behind the camera hides its label.
        Label("Cube", Modifier.anchorTo(cameraState, Position(CUBE.x, 1.3f, CUBE.z)))
        Label("Sphere", Modifier.anchorTo(cameraState, Position(SPHERE.x, 1.3f, SPHERE.z)))
        Label("Cylinder", Modifier.anchorTo(cameraState, Position(CYLINDER.x, 1.6f, CYLINDER.z)))

        BackButton(onClick = onBack, modifier = Modifier.align(Alignment.TopStart))
    }
}

@Composable
private fun Label(text: String, modifier: Modifier) {
    Text(
        text,
        color = Color.White,
        style = MaterialTheme.typography.labelLarge,
        // anchorTo comes first: what follows it moves with the point.
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
    )
}
