package eric.bitria.samples.scenes

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import eric.bitria.samples.shared.resources.Res
import io.github.erkko68.filament.LightManager
import io.github.erkko68.filament.View
import io.github.erkko68.filament.compose.FilamentView
import io.github.erkko68.filament.compose.LocalFilamentView
import io.github.erkko68.filament.compose.scene.Color as FilColor
import io.github.erkko68.filament.compose.scene.Direction
import io.github.erkko68.filament.compose.scene.GltfInstance
import io.github.erkko68.filament.compose.scene.Light
import io.github.erkko68.filament.compose.scene.Position
import io.github.erkko68.filament.compose.scene.Projection
import io.github.erkko68.filament.compose.scene.Scale
import io.github.erkko68.filament.compose.scene.SkyboxSource
import io.github.erkko68.filament.compose.scene.rememberCameraState
import io.github.erkko68.filament.compose.scene.rememberGltfAsset
import io.github.erkko68.filament.compose.scene.rememberSkyboxState
import org.jetbrains.compose.resources.ExperimentalResourceApi

private val DUCK_POSITIONS = listOf(
    Position(-1.5f, 0f, -1.5f),
    Position( 1.5f, 0f, -1.5f),
    Position(-1.5f, 0f,  1.5f),
    Position( 1.5f, 0f,  1.5f),
)

@OptIn(ExperimentalResourceApi::class)
@Composable
fun PickingScene(onBack: () -> Unit) {
    val cameraState = rememberCameraState(
        eye        = Position(0f, 4f, 5f),
        target     = Position(0f, 0f, 0f),
        projection = Projection.Perspective(fovDegrees = 40.0),
    )
    val skybox = rememberSkyboxState(source = SkyboxSource.Color(FilColor(0.08f, 0.10f, 0.14f)))

    val entityToIndex = remember { mutableStateMapOf<Int, Int>() }
    var lastTapped by remember { mutableIntStateOf(-1) }
    val capturedView = remember { mutableStateOf<View?>(null) }
    var boxSize by remember { mutableStateOf(IntSize.Zero) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { boxSize = it }
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val v = capturedView.value ?: return@detectTapGestures
                    val vw = v.viewport.width; val vh = v.viewport.height
                    val lw = boxSize.width.coerceAtLeast(1); val lh = boxSize.height.coerceAtLeast(1)
                    val px = (offset.x * vw / lw).toInt()
                    val py = vh - (offset.y * vh / lh).toInt()
                    v.pick(px, py) { result ->
                        lastTapped = entityToIndex[result.renderable] ?: -1
                    }
                }
            },
    ) {
        FilamentView(
            modifier = Modifier.fillMaxSize(),
            cameraState = cameraState,
            skyboxState = skybox,
        ) {
            val filamentView = LocalFilamentView.current
            SideEffect { capturedView.value = filamentView }
            Light(
                type      = LightManager.Type.DIRECTIONAL,
                direction = Direction(0.3f, -1f, -0.5f),
                intensity = 100_000f,
            )
            val duckAsset = rememberGltfAsset { Res.readBytes("files/models/Duck.glb") }
            DUCK_POSITIONS.forEachIndexed { idx, pos ->
                GltfInstance(
                    asset    = duckAsset,
                    position = pos,
                    scale    = Scale(1f),
                    onCreate = {
                        for (entity in instance.getEntities()) {
                            entityToIndex[entity] = idx
                        }
                    },
                )
            }
        }
        BackButton(onClick = onBack, modifier = Modifier.align(Alignment.TopStart))
        Text(
            text     = if (lastTapped >= 0) "Duck #${lastTapped + 1}" else "Tap a duck",
            style    = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(bottom = 24.dp),
        )
    }
}
