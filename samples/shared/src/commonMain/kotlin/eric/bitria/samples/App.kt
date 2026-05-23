package eric.bitria.samples

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import eric.bitria.samples.scenes.DuckScene
import eric.bitria.samples.scenes.PickingScene
import eric.bitria.samples.scenes.PrimitivesScene
import eric.bitria.samples.scenes.SolarScene

@Composable
fun App() {
    MaterialTheme {
        var screen: Screen by remember { mutableStateOf(Screen.Home) }
        when (screen) {
            Screen.Home       -> HomeScreen(onNavigate = { screen = it })
            Screen.Duck       -> DuckScene(onBack = { screen = Screen.Home })
            Screen.Primitives -> PrimitivesScene(onBack = { screen = Screen.Home })
            Screen.Picking    -> PickingScene(onBack = { screen = Screen.Home })
            Screen.Solar      -> SolarScene(onBack = { screen = Screen.Home })
        }
    }
}
