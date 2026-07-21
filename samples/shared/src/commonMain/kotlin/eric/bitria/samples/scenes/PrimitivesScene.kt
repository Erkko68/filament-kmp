package eric.bitria.samples.scenes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.erkko68.filament.compose.FilamentSceneView
import io.github.erkko68.filament.compose.orbitGestures
import io.github.erkko68.filament.compose.rememberOrbitCameraState
import io.github.erkko68.filament.compose.scene.Color as FilColor
import io.github.erkko68.filament.compose.scene.Direction
import io.github.erkko68.filament.compose.scene.DirectionalLight
import io.github.erkko68.filament.compose.scene.Position
import io.github.erkko68.filament.compose.scene.Projection
import io.github.erkko68.filament.compose.scene.SkyboxSource
import io.github.erkko68.filament.compose.scene.primitives.Cube
import io.github.erkko68.filament.compose.scene.primitives.Cylinder
import io.github.erkko68.filament.compose.scene.primitives.Plane
import io.github.erkko68.filament.compose.scene.primitives.Sphere
import io.github.erkko68.filament.compose.scene.rememberCameraState
import io.github.erkko68.filament.compose.scene.rememberColorMaterialInstance
import io.github.erkko68.filament.compose.scene.rememberSkyboxState

@Composable
fun PrimitivesScene(onBack: () -> Unit) {
    val cameraState = rememberCameraState(
        initialEye        = Position(0f, 2.5f, 6f),
        initialTarget     = Position(0f, 0.5f, 0f),
        initialProjection = Projection.Perspective(fovDegrees = 45.0),
    )
    val orbit  = rememberOrbitCameraState(cameraState)
    val skybox = rememberSkyboxState(initialSource = SkyboxSource.Color(FilColor(0.08f, 0.10f, 0.14f)))

    Box(Modifier.fillMaxSize()) {
        FilamentSceneView(
            modifier = Modifier
                .fillMaxSize()
                .orbitGestures(orbit),
            cameraState = cameraState,
            skyboxState = skybox,
        ) {
            DirectionalLight(
                direction = Direction(0.3f, -1f, -0.5f),
                intensity = 100_000f,
            )
            // Built-in LIT colour material — no .mat to author or ship.
            Cube(    material = rememberColorMaterialInstance(FilColor(0.90f, 0.25f, 0.30f)),
                     position = Position(-2f, 0.5f, 0f), size = 0.75f)
            Sphere(  material = rememberColorMaterialInstance(FilColor(0.20f, 0.55f, 0.95f)),
                     position = Position( 2f, 0.5f, 0f), radius = 0.48f)
            Cylinder(material = rememberColorMaterialInstance(FilColor(0.95f, 0.82f, 0.25f)),
                     position = Position(0f, 0.6f, -2f), radius = 0.3f, height = 1.2f)
            Plane(   material = rememberColorMaterialInstance(FilColor(0.28f, 0.30f, 0.36f)),
                     position = Position(0f, -0.01f, 0f), width = 9f, depth = 9f)
        }
        BackButton(onClick = onBack, modifier = Modifier.align(Alignment.TopStart))
    }
}
