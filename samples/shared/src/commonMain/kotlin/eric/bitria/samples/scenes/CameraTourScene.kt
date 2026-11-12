package eric.bitria.samples.scenes

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import eric.bitria.samples.shared.resources.Res
import io.github.erkko68.filament.compose.FilamentSceneView
import io.github.erkko68.filament.compose.orbitGestures
import io.github.erkko68.filament.compose.rememberOrbitCameraController
import io.github.erkko68.filament.compose.scene.CameraPath
import io.github.erkko68.filament.compose.scene.CameraTransitions
import io.github.erkko68.filament.compose.scene.Direction
import io.github.erkko68.filament.compose.scene.DirectionalLight
import io.github.erkko68.filament.compose.scene.GltfInstance
import io.github.erkko68.filament.compose.scene.IndirectLight
import io.github.erkko68.filament.compose.scene.LightIntensity
import io.github.erkko68.filament.compose.scene.LinearColor
import io.github.erkko68.filament.compose.scene.Position
import io.github.erkko68.filament.compose.scene.Projection
import io.github.erkko68.filament.compose.scene.ShadowConfig
import io.github.erkko68.filament.compose.scene.Skybox
import io.github.erkko68.filament.compose.scene.SphericalHarmonics
import io.github.erkko68.filament.compose.scene.Viewpoint
import io.github.erkko68.filament.compose.scene.primitives.Cube
import io.github.erkko68.filament.compose.scene.primitives.Cylinder
import io.github.erkko68.filament.compose.scene.primitives.Plane
import io.github.erkko68.filament.compose.scene.primitives.Sphere
import io.github.erkko68.filament.compose.scene.rememberCameraState
import io.github.erkko68.filament.compose.scene.rememberColorMaterialInstance
import io.github.erkko68.filament.compose.scene.rememberGltfAsset
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi

private val HOME = Viewpoint(eye = Position(0f, 4f, 9f), target = Position(0f, 0.5f, 0f))

// Ease-out-back: a custom timing is any Easing, here one that overshoots and settles.
private val OVERSHOOT = Easing { t -> val u = t - 1f; 1f + 2.70158f * u * u * u + 1.70158f * u * u }

private val TRANSITIONS = listOf(
    "Smooth" to CameraTransitions.Smooth,
    "Quick" to CameraTransitions.Quick,
    "Cinematic" to CameraTransitions.Cinematic,
    // A spring's overshoot carries the camera past the stop and back.
    "Spring" to spring<Float>(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessVeryLow),
    "Custom" to tween<Float>(1400, easing = OVERSHOOT),
)

// A custom route is any CameraPath: this one turns to face the new target first, then moves there.
private val LOOK_FIRST = CameraPath { from, to, t ->
    if (t < 0.5f) CameraPath.Straight.at(from, from.copy(target = to.target), t * 2f)
    else CameraPath.Straight.at(from.copy(target = to.target), to, t * 2f - 1f)
}

private val PATHS = listOf(
    "Orbit" to CameraPath.Orbit,
    "Straight" to CameraPath.Straight,
    "Arc" to CameraPath.Arc(lift = 3f),
    "Look first" to LOOK_FIRST,
)

@OptIn(ExperimentalResourceApi::class)
@Composable
fun CameraTourScene() {
    val cameraState = rememberCameraState(
        initialEye        = HOME.eye,
        initialTarget     = HOME.target,
        initialProjection = Projection.Perspective(fovDegrees = 45.0),
    )
    val orbit = rememberOrbitCameraController(cameraState)
    val scope = rememberCoroutineScope()
    var transition by remember { mutableStateOf(TRANSITIONS.first()) }
    var path by remember { mutableStateOf(PATHS.first()) }
    // A newer flight or a touch on the view cancels the running one.
    fun fly(to: Viewpoint) = scope.launch { cameraState.animateTo(to, transition.second, path.second) }

    // Each stop frames an object: its centre, a radius that encloses it, and the direction to see it from. Worked out
    // when the flight starts, as fitting depends on the view's shape.
    val stops = listOf<Pair<String, () -> Viewpoint>>(
        "Duck" to { cameraState.fitting(Position(-3f, 0.75f, 0f), 0.9f, from = Direction(1f, 0.4f, 1f)) },
        "Sphere" to { cameraState.fitting(Position(3f, 1f, -1f), 1f, from = Direction(1f, 1f, 0.3f)) },
        "Tower" to { cameraState.fitting(Position(0f, 1.5f, -4f), 1.6f, from = Direction(-1f, 0.15f, 1f)) },
        "Cube" to { cameraState.fitting(Position(0f, 0.25f, 2.5f), 0.45f, from = Direction(0.3f, 1.5f, 1f)) },
        "Overview" to { cameraState.fitting(Position(0f, 0.5f, -0.5f), 5f, from = Direction(0f, 0.8f, 1f)) },
    )

    var saved by rememberSaveable(stateSaver = Viewpoint.Saver) { mutableStateOf(HOME) }

    var touring by remember { mutableStateOf(false) }
    LaunchedEffect(touring) {
        if (!touring) return@LaunchedEffect
        // Grabbing the camera cancels the flight under way, which ends the tour.
        try {
            while (true) for ((_, stop) in stops) {
                cameraState.animateTo(stop(), transition.second, path.second)
                delay(1000)
            }
        } finally {
            touring = false
        }
    }

    Box(Modifier.fillMaxSize()) {
        FilamentSceneView(
            modifier = Modifier.fillMaxSize().orbitGestures(orbit),
            cameraState = cameraState,
        ) {
            Skybox(color = LinearColor(0.08f, 0.10f, 0.14f))
            IndirectLight(irradiance = SphericalHarmonics.NeutralStudio)
            DirectionalLight(
                direction = Direction(0.3f, -1f, -0.5f),
                intensity = LightIntensity.LuminousPower(100_000f),
                shadow    = ShadowConfig(mapSize = 2048),
            )
            GltfInstance(
                asset     = rememberGltfAsset { Res.readBytes("files/models/Duck.glb") },
                position  = Position(-3f, 0.75f, 0f),
                fitToSize = 1.5f,
            )
            Sphere(rememberColorMaterialInstance(LinearColor(0.20f, 0.55f, 0.95f)), position = Position(3f, 1f, -1f), radius = 1f)
            Cylinder(rememberColorMaterialInstance(LinearColor(0.95f, 0.82f, 0.25f)), position = Position(0f, 1.5f, -4f), radius = 0.4f, height = 3f)
            Cube(rememberColorMaterialInstance(LinearColor(0.90f, 0.25f, 0.30f)), position = Position(0f, 0.25f, 2.5f), size = 0.5f)
            Plane(rememberColorMaterialInstance(LinearColor(0.28f, 0.30f, 0.36f)), position = Position(0f, -0.01f, 0f), width = 14f, depth = 14f)
        }
        ControlPanel {
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = touring, onClick = { touring = !touring }, label = { Text("Tour") })
                stops.forEach { (name, stop) ->
                    AssistChip(onClick = { touring = false; fly(stop()) }, label = { Text(name) })
                }
                AssistChip(onClick = { touring = false; fly(HOME) }, label = { Text("Home") })
                AssistChip(onClick = { saved = cameraState.viewpoint }, label = { Text("Save view") })
                AssistChip(onClick = { touring = false; fly(saved) }, label = { Text("Go to saved") })
            }
            ChoiceRow(TRANSITIONS, transition, label = { it.first }, title = "Timing") { transition = it }
            ChoiceRow(PATHS, path, label = { it.first }, title = "Path") { path = it }
        }
    }
}
