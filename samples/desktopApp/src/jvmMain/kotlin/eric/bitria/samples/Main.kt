package eric.bitria.samples

import androidx.compose.ui.window.singleWindowApplication

fun main() {
    // Compose picks its GPU when the first window opens, and Filament renders on the same one.
    // Prefer the discrete GPU on hybrid laptops; -Dskiko.gpu.priority=auto|integrated overrides it.
    if (System.getProperty("skiko.gpu.priority") == null) {
        System.setProperty("skiko.gpu.priority", "discrete")
    }
    singleWindowApplication(
        title = "Filament KMP Sample - Desktop"
    ) {
        App()
    }
}
