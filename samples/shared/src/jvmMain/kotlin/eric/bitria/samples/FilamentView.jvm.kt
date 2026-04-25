package eric.bitria.samples

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

actual fun getPlatformBackend(): io.github.erkko68.filament.Engine.Backend {
    val os = System.getProperty("os.name").lowercase()
    return if (os.contains("mac")) {
        io.github.erkko68.filament.Engine.Backend.METAL
    } else {
        io.github.erkko68.filament.Engine.Backend.OPENGL
    }
}

@Composable
actual fun FilamentView(modifier: Modifier, controller: FilamentController) {
    val os = System.getProperty("os.name").lowercase()
    when {
        os.contains("mac") -> FilamentViewMacOS(modifier, controller)
        os.contains("win") -> FilamentViewWindows(modifier, controller)
        else -> FilamentViewLinux(modifier, controller)
    }
}
