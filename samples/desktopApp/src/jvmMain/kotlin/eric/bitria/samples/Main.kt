package eric.bitria.samples

import androidx.compose.ui.window.singleWindowApplication

fun main() {
    // Filament-KMP shares its GPU output with Skiko zero-copy on macOS (Metal) and
    // on Linux/Windows when Skiko uses its real OpenGL renderer. The Windows default
    // is DirectX/ANGLE, which has no shared GL context — force OPENGL so the
    // zero-copy path is reachable. Without this Windows falls back to CPU readback.
    val os = System.getProperty("os.name").lowercase()
    if ("win" in os || ("nux" in os || "nix" in os)) {
        System.setProperty("skiko.renderApi", "OPENGL")
    }

    singleWindowApplication(
        title = "Filament KMP Sample - Desktop"
    ) {
        App()
    }
}
