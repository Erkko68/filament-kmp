package dev.filament.kmp

actual class Engine {
    actual val isValid: Boolean
        get() = TODO("Not yet implemented")

    actual fun createRenderer(): Renderer {
        TODO("Not yet implemented")
    }

    actual fun destroyRenderer(renderer: Renderer) {
    }

    actual fun createScene(): Scene {
        TODO("Not yet implemented")
    }

    actual fun destroyScene(scene: Scene) {
    }

    actual fun createView(): View {
        TODO("Not yet implemented")
    }

    actual fun destroyView(view: View) {
    }

    actual fun close() {
    }

    actual companion object {
        actual fun create(config: EngineConfig): Engine {
            TODO("Not yet implemented")
        }
    }
}