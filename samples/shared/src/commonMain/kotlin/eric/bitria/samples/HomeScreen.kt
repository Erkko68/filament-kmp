package eric.bitria.samples

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private val ENTRIES = listOf(
    "Duck"       to Screen.Duck,
    "Primitives" to Screen.Primitives,
    "Picking"    to Screen.Picking,
    "Solar"      to Screen.Solar,
    "Animation"  to Screen.Animation,
    "Split View" to Screen.SplitView,
    "Environment" to Screen.Environment,
)

@Composable
fun HomeScreen(onNavigate: (Screen) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Filament KMP", style = MaterialTheme.typography.headlineMedium)
        ENTRIES.forEach { (label, screen) ->
            Button(onClick = { onNavigate(screen) }, modifier = Modifier.fillMaxWidth()) {
                Text(label)
            }
        }
    }
}
