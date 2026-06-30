package eric.bitria.samples.scenes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import eric.bitria.samples.shared.resources.Res
import io.github.erkko68.filament.TextureSampler
import io.github.erkko68.filament.compose.FilamentSceneView
import io.github.erkko68.filament.compose.orbitGestures
import io.github.erkko68.filament.compose.rememberFilamentEngine
import io.github.erkko68.filament.compose.rememberOrbitCameraState
import io.github.erkko68.filament.compose.scene.Color as FilColor
import io.github.erkko68.filament.compose.scene.Direction
import io.github.erkko68.filament.compose.scene.DirectionalLight
import io.github.erkko68.filament.compose.scene.Position
import io.github.erkko68.filament.compose.scene.Projection
import io.github.erkko68.filament.compose.scene.SkyboxSource
import io.github.erkko68.filament.compose.scene.primitives.Sphere
import io.github.erkko68.filament.compose.scene.rememberCameraState
import io.github.erkko68.filament.compose.scene.rememberMaterial
import io.github.erkko68.filament.compose.scene.rememberMaterialInstance
import io.github.erkko68.filament.compose.scene.rememberSkyboxState
import io.github.erkko68.filament.utils.TextureLoader
import org.jetbrains.compose.resources.ExperimentalResourceApi

/**
 * Loads a **PNG** with [TextureLoader] and binds it as the `albedo` of a lit material on a
 * sphere (the UV grid makes the mapping legible as you orbit). The texture loads asynchronously
 * and is destroyed with the composable.
 */
@OptIn(ExperimentalResourceApi::class)
@Composable
fun TextureScene(onBack: () -> Unit) {
    val engine = rememberFilamentEngine()
    val cameraState = rememberCameraState(
        eye        = Position(0f, 0f, 3f),
        target     = Position(0f, 0f, 0f),
        projection = Projection.Perspective(fovDegrees = 45.0),
    )
    val orbit  = rememberOrbitCameraState(cameraState)
    val skybox = rememberSkyboxState(source = SkyboxSource.Color(FilColor(0.08f, 0.10f, 0.14f)))

    // Decode the PNG into a Filament texture once the bytes arrive; tear it down on dispose.
    val pngBytes by produceState<ByteArray?>(null) { value = Res.readBytes("files/textures/uv_grid.png") }
    val texture = remember(engine, pngBytes) {
        pngBytes?.let { TextureLoader.loadTexture(engine, it, TextureLoader.TextureType.COLOR) }
    }
    DisposableEffect(texture) {
        onDispose { texture?.let { engine.destroyTexture(it) } }
    }

    Box(Modifier.fillMaxSize()) {
        FilamentSceneView(
            engine = engine,
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { orbit.setViewport(it.width, it.height) }
                .orbitGestures(orbit),
            cameraState = cameraState,
            skyboxState = skybox,
        ) {
            DirectionalLight(
                direction = Direction(0.3f, -1f, -0.5f),
                intensity = 100_000f,
            )
            // Escape hatch: load a hand-authored .filamat and bind its `albedo` sampler
            // reactively with rememberMaterialInstance. (For the common case the built-in
            // rememberTexturedMaterialInstance does this with no .mat to author or ship.)
            val material = rememberMaterial { Res.readBytes("files/materials/textured.filamat") }
            val sampler = remember {
                TextureSampler(
                    TextureSampler.MinFilter.LINEAR_MIPMAP_LINEAR,
                    TextureSampler.MagFilter.LINEAR,
                    TextureSampler.WrapMode.REPEAT,
                )
            }
            if (material != null && texture != null) {
                val instance = rememberMaterialInstance(material, texture) {
                    setParameter("albedo", texture, sampler)
                }
                Sphere(material = instance, radius = 1f)
            }
        }
        BackButton(onClick = onBack, modifier = Modifier.align(Alignment.TopStart))
    }
}
