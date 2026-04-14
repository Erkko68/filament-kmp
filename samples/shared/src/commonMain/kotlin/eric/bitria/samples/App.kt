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
    val renderer = remember { FilamentRenderer() }
    
    // Load assets from composeResources
    LaunchedEffect(Unit) {
        try {
            val filamatBytes = Res.readBytes("files/materials/textured.filamat")
            renderer.setupResourceMaterialCube(filamatBytes)
            println("App: Loaded textured.filamat from resources")
        } catch (e: Exception) {
            println("App: Failed to load textured.filamat: ${e.message}")
        }

        try {
            val glbBytes = Res.readBytes("files/models/Duck.glb")
            renderer.setupGltfModel(glbBytes)
            println("App: Loaded model.glb from resources")
        } catch (e: Exception) {
            println("App: Failed to load model.glb (this is expected if not yet added): ${e.message}")
        }
    }

    DisposableEffect(Unit) {
        renderer.initialize()
        onDispose {
            renderer.destroy()
        }
    }

    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            FilamentView(
                modifier = Modifier.fillMaxSize(),
                renderer = renderer
            )
            
            Text(
                "Filament KMP: Runtime Obj | Res Obj | Gltfio Obj",
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 48.dp),
                color = Color.White
            )
        }
    }
}