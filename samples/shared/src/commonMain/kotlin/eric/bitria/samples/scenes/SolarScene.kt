package eric.bitria.samples.scenes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import eric.bitria.samples.rememberColorInstance
import eric.bitria.samples.rememberEmissiveInstance
import eric.bitria.samples.rememberEmissiveTemplate
import eric.bitria.samples.rememberLitColorTemplate
import io.github.erkko68.filament.LightManager
import io.github.erkko68.filament.compose.FilamentView
import io.github.erkko68.filament.compose.orbitGestures
import io.github.erkko68.filament.compose.rememberOrbitCameraState
import io.github.erkko68.filament.compose.rememberSceneClock
import io.github.erkko68.filament.compose.scene.AntiAliasing
import io.github.erkko68.filament.compose.scene.Bloom
import io.github.erkko68.filament.compose.scene.Dithering
import io.github.erkko68.filament.compose.scene.RenderQuality
import io.github.erkko68.filament.compose.scene.Color as FilColor
import io.github.erkko68.filament.compose.scene.Group
import io.github.erkko68.filament.compose.scene.Light
import io.github.erkko68.filament.compose.scene.Position
import io.github.erkko68.filament.compose.scene.Projection
import io.github.erkko68.filament.compose.scene.SkyboxSource
import io.github.erkko68.filament.compose.scene.primitives.Sphere
import io.github.erkko68.filament.compose.scene.rememberCameraState
import io.github.erkko68.filament.compose.scene.rememberSkyboxState
import io.github.erkko68.filament.utils.Float3
import io.github.erkko68.filament.utils.Quaternion
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private data class Planet(
    val color: FilColor,
    val radius: Float,
    val orbitRadius: Float,
    val orbitSpeed: Float, // radians per second
    val spinSpeed: Float,  // radians per second around its own Y axis
)

private val PLANETS = listOf(
    Planet(FilColor(0.65f, 0.65f, 0.70f), radius = 0.20f, orbitRadius = 1.8f, orbitSpeed = 1.6f, spinSpeed = 4.0f), // mercury
    Planet(FilColor(0.95f, 0.65f, 0.30f), radius = 0.32f, orbitRadius = 2.7f, orbitSpeed = 1.2f, spinSpeed = 2.5f), // venus
    Planet(FilColor(0.25f, 0.55f, 0.95f), radius = 0.36f, orbitRadius = 3.8f, orbitSpeed = 0.9f, spinSpeed = 3.0f), // earth
    Planet(FilColor(0.85f, 0.30f, 0.20f), radius = 0.26f, orbitRadius = 5.0f, orbitSpeed = 0.6f, spinSpeed = 2.0f), // mars
)

/**
 * Demonstrates `Group { }`, `Light` grouping, and `rememberSceneClock()` together: a small
 * solar system. The sun bobs up and down on the Y axis; each planet orbits the sun and spins
 * on its own axis. Every animated transform is just `time` plugged into `sin`/`cos`.
 */
@Composable
fun SolarScene(onBack: () -> Unit) {
    val cameraState = rememberCameraState(
        eye        = Position(0f, 4f, 9f),
        target     = Position(0f, 0f, 0f),
        projection = Projection.Perspective(fovDegrees = 50.0),
    )
    val orbit  = rememberOrbitCameraState(cameraState)
    val skybox = rememberSkyboxState(source = SkyboxSource.Color(FilColor(0.02f, 0.03f, 0.06f)))

    val time by rememberSceneClock()

    Box(Modifier.fillMaxSize()) {
        FilamentView(
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { orbit.setViewport(it.width, it.height) }
                .orbitGestures(orbit),
            cameraState = cameraState,
            skyboxState = skybox,
        ) {
            // RenderQuality(HIGH) forces an FP16 HDR color buffer so emissive values above 1.0
            // survive without clipping; Dithering masks any residual 8-bit step at the output.
            // A larger bloom resolution + 7 mip levels keeps the halo smooth on hi-DPI retina
            // displays where the default ~360 px chain would otherwise look pixelated.
            RenderQuality()
            Dithering()
            Bloom(strength = 0.6f, resolution = 480, levels = 7)
            AntiAliasing(fxaaEnabled = true)

            val litTmpl      = rememberLitColorTemplate()
            val emissiveTmpl = rememberEmissiveTemplate()
            if (litTmpl != null && emissiveTmpl != null) {
                // Sun assembly — the Group's transform moves the emissive sphere *and* the
                // point light together. The sphere uses an UNLIT + emissive material so it
                // appears self-illuminated regardless of scene lighting; bloom turns the
                // emissive output into the halo around the sun.
                val sunY = sin(time * 0.6f) * 0.6f
                Group(position = Position(0f, sunY, 0f)) {
                    Sphere(
                        material = rememberEmissiveInstance(
                            template  = emissiveTmpl,
                            color     = FilColor(1.0f, 0.85f, 0.40f),
                            intensity = 4.0f,
                        ),
                        radius = 0.7f,
                    )
                    Light(
                        type      = LightManager.Type.POINT,
                        color     = FilColor(1f, 0.85f, 0.5f),
                        intensity = 400_000f,
                        falloff   = 15f,
                        position  = Position(0f, 0f, 0f),
                    )
                }

                // Planets — each one is a Group at the orbit angle, containing a Sphere that
                // spins in its own local frame. Two nested transforms doing two animations.
                PLANETS.forEachIndexed { i, p ->
                    val orbitAngle = time * p.orbitSpeed + (i * PI.toFloat() / 2f)
                    val spinDegrees = (time * p.spinSpeed) * (180f / PI.toFloat())
                    val px = cos(orbitAngle) * p.orbitRadius
                    val pz = sin(orbitAngle) * p.orbitRadius
                    Group(position = Position(px, 0f, pz)) {
                        Sphere(
                            material = rememberColorInstance(litTmpl, p.color),
                            radius   = p.radius,
                            rotation = Quaternion.fromAxisAngle(Float3(0f, 1f, 0f), spinDegrees),
                        )
                    }
                }
            }
        }
        BackButton(onClick = onBack, modifier = Modifier.align(Alignment.TopStart))
    }
}
