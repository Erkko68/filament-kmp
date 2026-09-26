package eric.bitria.samples.scenes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.VertexBuffer.VertexAttribute
import io.github.erkko68.filament.compose.FilamentSceneView
import io.github.erkko68.filament.compose.LocalFilamentEngine
import io.github.erkko68.filament.compose.orbitGestures
import io.github.erkko68.filament.compose.rememberOrbitCameraController
import io.github.erkko68.filament.compose.scene.Direction
import io.github.erkko68.filament.compose.scene.DirectionalLight
import io.github.erkko68.filament.compose.scene.LightIntensity
import io.github.erkko68.filament.compose.scene.LinearColor
import io.github.erkko68.filament.compose.scene.Position
import io.github.erkko68.filament.compose.scene.Projection
import io.github.erkko68.filament.compose.scene.SkyboxSource
import io.github.erkko68.filament.compose.scene.primitives.Sphere
import io.github.erkko68.filament.compose.scene.rememberCameraState
import io.github.erkko68.filament.compose.scene.rememberMaterial
import io.github.erkko68.filament.compose.scene.rememberMaterialInstance
import io.github.erkko68.filament.compose.scene.rememberSkyboxState
import io.github.erkko68.filament.filamat.Filamat
import io.github.erkko68.filament.filamat.MaterialBuilder
import kotlin.time.TimeSource

/** Fragment shaders compiled at runtime by filamat; picking one rebuilds the material. */
private val PATTERNS = listOf(
    "Stripes" to "float t = step(0.5, fract(uv.x * 12.0)); vec3 c = mix(vec3(0.9, 0.3, 0.2), vec3(1.0, 0.9, 0.7), t);",
    "Checker" to "vec2 g = floor(uv * vec2(16.0, 8.0)); float t = mod(g.x + g.y, 2.0); vec3 c = mix(vec3(0.1), vec3(0.95), t);",
    "Rings"   to "float t = 0.5 + 0.5 * sin(length(uv - 0.5) * 60.0); vec3 c = mix(vec3(0.1, 0.3, 0.9), vec3(0.3, 0.9, 0.8), t);",
)

private fun compile(engine: Engine, pattern: String): Pair<ByteArray, Long> {
    val start = TimeSource.Monotonic.markNow()
    Filamat.init() // idempotent; on web filamat-kmp.wasm must already be loaded (see webApp Main.kt)
    val pkg = MaterialBuilder()
        .name("RuntimePattern")
        .shading(MaterialBuilder.Shading.LIT)
        .require(VertexAttribute.UV0)
        .material(
            """
            void material(inout MaterialInputs material) {
                prepareMaterial(material);
                vec2 uv = getUV0();
                $pattern
                material.baseColor = vec4(c, 1.0);
                material.roughness = 0.4;
            }
            """.trimIndent()
        )
        .platform(MaterialBuilder.Platform.ALL)
        .targetApi(
            when (engine.backend) {
                Engine.Backend.METAL -> MaterialBuilder.TargetApi.METAL
                Engine.Backend.VULKAN -> MaterialBuilder.TargetApi.VULKAN
                else -> MaterialBuilder.TargetApi.OPENGL
            }
        )
        .build()
    check(pkg.isValid) { "filamat failed to compile the material" }
    return pkg.buffer to start.elapsedNow().inWholeMilliseconds
}

@Composable
fun RuntimeMaterialScene(onBack: () -> Unit) {
    val cameraState = rememberCameraState(
        initialEye        = Position(0f, 0f, 4f),
        initialTarget     = Position(0f, 0f, 0f),
        initialProjection = Projection.Perspective(fovDegrees = 45.0),
    )
    val orbit  = rememberOrbitCameraController(cameraState)
    val skybox = rememberSkyboxState(initialSource = SkyboxSource.Color(LinearColor(0.08f, 0.10f, 0.14f)))
    var selected by remember { mutableStateOf(0) }
    var status by remember { mutableStateOf("Compiling…") }

    Box(Modifier.fillMaxSize()) {
        FilamentSceneView(
            modifier = Modifier.fillMaxSize().orbitGestures(orbit),
            cameraState = cameraState,
            skyboxState = skybox,
        ) {
            val engine = LocalFilamentEngine.current!!
            val material = rememberMaterial(key = selected, onError = { status = "Error: ${it.message}" }) {
                val (bytes, ms) = compile(engine, PATTERNS[selected].second)
                status = "Compiled ${PATTERNS[selected].first} in $ms ms (${bytes.size / 1024} KB)"
                bytes
            }
            DirectionalLight(
                direction = Direction(0.3f, -1f, -0.5f),
                intensity = LightIntensity.LuminousPower(100_000f),
            )
            rememberMaterialInstance(material)?.let { Sphere(material = it, radius = 1f) }
        }
        BackButton(onClick = onBack, modifier = Modifier.align(Alignment.TopStart))
        Column(
            modifier = Modifier.align(Alignment.BottomCenter).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(status, color = Color.White, style = MaterialTheme.typography.bodyMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PATTERNS.forEachIndexed { i, (label, _) ->
                    FilterChip(
                        selected = i == selected,
                        onClick = { selected = i; status = "Compiling…" },
                        label = { Text(label) },
                    )
                }
            }
        }
    }
}
