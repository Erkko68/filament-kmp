package eric.bitria.samples

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import eric.bitria.samples.shared.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App() {
    val controller = remember { FilamentController() }
    val scene = remember { SampleScene(controller) }
    
    // Initialize engine once
    val initialized = remember { mutableStateOf(false) }
    if (!initialized.value) {
        controller.initialize()
        scene.setup()
        initialized.value = true
    }

    // Load assets from composeResources
    LaunchedEffect(Unit) {
        try {
            val filamatBytes = Res.readBytes("files/materials/textured.filamat")
            scene.loadMaterialCube(filamatBytes)
            println("App: Loaded textured.filamat from resources")
        } catch (e: Exception) {
            println("App: Failed to load textured.filamat: ${e.message}")
        }

        try {
            val glbBytes = Res.readBytes("files/models/Duck.glb")
            scene.loadGltfModel(glbBytes)
            println("App: Loaded model.glb from resources")
        } catch (e: Exception) {
            println("App: Failed to load model.glb: ${e.message}")
        }
    }

    // Animation loop
    var startTime by remember { mutableStateOf(-1L) }
    FilamentRenderLoop { frameTimeNanos ->
        if (startTime < 0) startTime = frameTimeNanos
        val elapsed = (frameTimeNanos - startTime).toDouble() / 1_000_000_000.0
        scene.updateAnimations(elapsed)
    }

    DisposableEffect(Unit) {
        onDispose {
            scene.destroy()
            controller.destroy()
        }
    }

    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize(0.5f)) {
            FilamentView(
                modifier = Modifier.fillMaxSize(),
                controller = controller
            )
            
            Text(
                "Filament KMP: Runtime Obj | Res Obj | Gltfio Obj",
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 48.dp),
                color = Color.White
            )
        }
    }
}