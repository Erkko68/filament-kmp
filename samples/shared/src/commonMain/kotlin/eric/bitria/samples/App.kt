package eric.bitria.samples

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import eric.bitria.samples.scenes.AnimationScene
import eric.bitria.samples.scenes.CameraTourScene
import eric.bitria.samples.scenes.CaptureScene
import eric.bitria.samples.scenes.DuckScene
import eric.bitria.samples.scenes.HDREnvironmentScene
import eric.bitria.samples.scenes.KTXEnvironmentScene
import eric.bitria.samples.scenes.LabelsScene
import eric.bitria.samples.scenes.LightingScene
import eric.bitria.samples.scenes.ParticlesScene
import eric.bitria.samples.scenes.PickingScene
import eric.bitria.samples.scenes.PrimitivesScene
import eric.bitria.samples.scenes.RuntimeMaterialScene
import eric.bitria.samples.scenes.SolarScene
import eric.bitria.samples.scenes.SplitViewScene
import eric.bitria.samples.scenes.TextureScene
import eric.bitria.samples.scenes.TransparentScene
import kotlinx.coroutines.launch

private class Sample(val title: String, val content: @Composable () -> Unit)

private val SAMPLES = listOf(
    Sample("Duck") { DuckScene() },
    Sample("Primitives") { PrimitivesScene() },
    Sample("Lighting") { LightingScene() },
    Sample("Picking") { PickingScene() },
    Sample("Labels") { LabelsScene() },
    Sample("Camera Tour") { CameraTourScene() },
    Sample("Capture") { CaptureScene() },
    Sample("Solar") { SolarScene() },
    Sample("Animation") { AnimationScene() },
    Sample("Split View") { SplitViewScene() },
    Sample("Texture (PNG)") { TextureScene() },
    Sample("Environment (KTX)") { KTXEnvironmentScene() },
    Sample("Environment (HDR)") { HDREnvironmentScene() },
    Sample("Transparency (GLB)") { TransparentScene() },
    Sample("Runtime Material (filamat)") { RuntimeMaterialScene() },
    Sample("Particles") { ParticlesScene() },
)

/** Wide windows keep the sample list beside the scene; narrow ones open it from a menu button. */
@Composable
fun App() {
    // Dark, so controls read over the scenes' dark backgrounds.
    MaterialTheme(colorScheme = darkColorScheme()) {
        var current by remember { mutableStateOf(SAMPLES.first()) }
        val drawer = rememberDrawerState(DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val wide = maxWidth >= 600.dp
            ModalNavigationDrawer(
                drawerState = drawer,
                // Only while open, so an edge drag still orbits the camera.
                gesturesEnabled = drawer.isOpen,
                drawerContent = {
                    ModalDrawerSheet(Modifier.width(280.dp)) {
                        SampleList(current) { current = it; scope.launch { drawer.close() } }
                    }
                },
            ) {
                // One tree for both widths, so crossing the breakpoint doesn't reload the scene.
                Row(Modifier.fillMaxSize()) {
                    if (wide) {
                        PermanentDrawerSheet(Modifier.width(240.dp).fillMaxHeight()) {
                            SampleList(current) { current = it }
                        }
                    }
                    Box(Modifier.weight(1f).fillMaxHeight()) {
                        key(current) { current.content() }
                        if (!wide) {
                            MenuButton(
                                title = current.title,
                                onClick = { scope.launch { drawer.open() } },
                                modifier = Modifier.align(Alignment.TopStart),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SampleList(current: Sample, onSelect: (Sample) -> Unit) {
    Column(
        Modifier
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .verticalScroll(rememberScrollState())
            .padding(12.dp),
    ) {
        Text(
            "Filament KMP",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 16.dp),
        )
        SAMPLES.forEach { sample ->
            NavigationDrawerItem(
                label = { Text(sample.title) },
                selected = sample == current,
                onClick = { onSelect(sample) },
            )
        }
    }
}

/** Hamburger (three bars; the samples don't pull in an icon library) and the open sample's name. */
@Composable
private fun MenuButton(title: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    FilledTonalButton(
        onClick = onClick,
        modifier = modifier.windowInsetsPadding(WindowInsets.safeDrawing).padding(8.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            repeat(3) { Box(Modifier.size(18.dp, 2.dp).background(LocalContentColor.current)) }
        }
        Text(title, modifier = Modifier.padding(start = 12.dp))
    }
}
