package eric.bitria.samples

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
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
    
    LaunchedEffect(Unit) {
        controller.initialize()
        scene.setup()

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

    var menuExpanded by remember { mutableStateOf(false) }
    val menuWidth by animateDpAsState(if (menuExpanded) 200.dp else 70.dp)

    MaterialTheme {
        Row(modifier = Modifier.fillMaxSize()) {
            // Expandable side menu
            Column(
                modifier = Modifier
                    .width(menuWidth)
                    .fillMaxHeight()
                    .background(Color.DarkGray)
                    .padding(8.dp)
            ) {
                Button(onClick = { menuExpanded = !menuExpanded }) {
                    Text(if (menuExpanded) "<" else ">")
                }

                if (menuExpanded) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Menu Item 1", color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Menu Item 2", color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Menu Item 3", color = Color.White)
                }
            }

            // Main Filament content
            Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
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
}
