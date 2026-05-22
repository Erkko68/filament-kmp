package eric.bitria.samples

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import eric.bitria.samples.shared.resources.Res
import io.github.erkko68.filament.LightManager
import io.github.erkko68.filament.MaterialInstance
import io.github.erkko68.filament.compose.FilamentView
import io.github.erkko68.filament.compose.LocalFilamentEngine
import io.github.erkko68.filament.compose.orbitGestures
import io.github.erkko68.filament.compose.rememberOrbitCameraState
import io.github.erkko68.filament.compose.scene.*
import io.github.erkko68.filament.compose.scene.primitives.Cube
import io.github.erkko68.filament.compose.scene.primitives.Cylinder
import io.github.erkko68.filament.compose.scene.primitives.Plane
import io.github.erkko68.filament.compose.scene.primitives.Sphere
import io.github.erkko68.filament.filamat.MaterialBuilder
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App() {
    val cameraState = rememberCameraState(
        eye        = Position(0f, 2f, 5f),
        target     = Position(0f, 0.5f, 0f),
        projection = Projection.Perspective(fovDegrees = 45.0),
    )
    val orbit = rememberOrbitCameraState(cameraState)
    val skybox = rememberSkyboxState(source = SkyboxSource.Color(Color(0.1f, 0.125f, 0.15f)))

    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            FilamentView(
                modifier = Modifier
                    .fillMaxSize()
                    .onSizeChanged { orbit.setViewport(it.width, it.height) }
                    .orbitGestures(orbit),
                cameraState = cameraState,
                skyboxState = skybox,
            ) {
                Light(
                    type      = LightManager.Type.DIRECTIONAL,
                    direction = Direction(0.3f, -1f, -0.5f),
                    intensity = 100_000f,
                )

                Bloom(strength = 0.2f)
                Fog(enabled = true, color = Color(0.1f, 0.125f, 0.15f), density = 0.05f)
                AntiAliasing(fxaaEnabled = true)

                GltfInstance(
                    asset    = rememberGltfAsset { Res.readBytes("files/models/Duck.glb") },
                    position = Position(0f, 0f, 0f),
                    scale    = Scale(1f),
                )

                // Primitives demo — the material is compiled once via filamat and shared
                // across all instances; each Cube/Sphere/etc gets its own cheap MaterialInstance
                // with a different baseColor. On JS rememberLitColorTemplate() returns null
                // because filamat throws there (see platform-notes.md).
                rememberLitColorTemplate()?.let { template ->
                    Cube(material     = rememberColorInstance(template, Color(0.85f, 0.25f, 0.35f)),
                         position     = Position(-2f, 0.5f, 0f), size = 0.7f)
                    Sphere(material   = rememberColorInstance(template, Color(0.25f, 0.6f, 0.9f)),
                           position   = Position(2f, 0.5f, 0f), radius = 0.45f)
                    Cylinder(material = rememberColorInstance(template, Color(0.95f, 0.85f, 0.3f)),
                             position = Position(0f, 0.6f, -2f), radius = 0.3f, height = 1.2f)
                    Plane(material    = rememberColorInstance(template, Color(0.35f, 0.35f, 0.4f)),
                          position    = Position(0f, -0.01f, 0f), width = 8f, depth = 8f)
                }
            }

            Text(
                "Filament KMP — Compose DSL + primitives",
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 48.dp),
                color    = ComposeColor.White,
            )
        }
    }
}

private var filamatInitialized = false

/**
 * Compiles a tiny LIT material once at runtime with a single `baseColor` parameter. Returns
 * null when [MaterialBuilder] is unavailable (JS/Web), letting callers omit the primitives.
 * The compiled [Material] is shared — make instances via [rememberColorInstance].
 */
@Composable
private fun rememberLitColorTemplate(): io.github.erkko68.filament.Material? {
    val engine = LocalFilamentEngine.current
    val material = remember(engine) {
        try {
            if (!filamatInitialized) {
                MaterialBuilder.init()
                filamatInitialized = true
            }
            val pkg = MaterialBuilder()
                .name("LitColor")
                .platform(MaterialBuilder.Platform.ALL)
                .targetApi(MaterialBuilder.TargetApi.ALL)
                .shading(MaterialBuilder.Shading.LIT)
                .uniformParameter(MaterialBuilder.UniformType.FLOAT3, "baseColor")
                .material("""
                    void material(inout MaterialInputs material) {
                        prepareMaterial(material);
                        material.baseColor.rgb = materialParams.baseColor;
                        material.roughness = 0.4;
                        material.metallic  = 0.0;
                    }
                """.trimIndent())
                .build()
            if (!pkg.isValid()) return@remember null
            io.github.erkko68.filament.Material.Builder()
                .payload(pkg.getBuffer())
                .build(engine)
        } catch (_: UnsupportedOperationException) {
            null
        }
    }
    DisposableEffect(material) {
        onDispose { material?.let { engine.destroyMaterial(it) } }
    }
    return material
}

/**
 * Creates and remembers a [MaterialInstance] from a shared [Material] template with the given
 * `baseColor`. Creating instances is cheap — the heavy shader compilation happens once in
 * [rememberLitColorTemplate].
 */
@Composable
private fun rememberColorInstance(template: io.github.erkko68.filament.Material, color: Color): MaterialInstance {
    val engine = LocalFilamentEngine.current
    val instance = remember(template, color) {
        template.createInstance().also {
            it.setParameter("baseColor", color.x, color.y, color.z)
        }
    }
    DisposableEffect(instance) {
        onDispose { engine.destroyMaterialInstance(instance) }
    }
    return instance
}
