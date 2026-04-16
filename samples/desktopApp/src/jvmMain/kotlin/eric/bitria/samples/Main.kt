package eric.bitria.samples

import androidx.compose.ui.window.singleWindowApplication

fun main() {
    System.setProperty("sun.java2d.metal", "true")
    System.setProperty("compose.interop.blending", "true")
    singleWindowApplication(
        title = "Filament KMP Sample - Desktop"
    ) {
        App()
    }
}
