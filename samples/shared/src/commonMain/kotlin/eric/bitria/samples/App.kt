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

@Composable
fun App() {
    val renderer = remember { FilamentRenderer() }
    
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
                "Filament KMP Scene",
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 48.dp),
                color = Color.Black
            )
        }
    }
}