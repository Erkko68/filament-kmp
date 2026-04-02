package dev.filament.kmp

import com.google.android.filament.Engine as AndroidFilamentEngine

actual class Engine private constructor(
    private var androidEngine: AndroidFilamentEngine?,
) {
    actual val isValid: Boolean
        get() = androidEngine != null

    actual fun createRenderer(): Renderer {
        val engine = requireNotNull(androidEngine) { "Engine is closed." }
        return Renderer(engine.createRenderer())
    }

    actual fun destroyRenderer(renderer: Renderer) {
        val engine = requireNotNull(androidEngine) { "Engine is closed." }
        val handle = renderer.androidRenderer ?: return
        engine.destroyRenderer(handle)
        renderer.invalidate()
    }

    actual fun createScene(): Scene {
        val engine = requireNotNull(androidEngine) { "Engine is closed." }
        return Scene(engine.createScene())
    }

    actual fun destroyScene(scene: Scene) {
        val engine = requireNotNull(androidEngine) { "Engine is closed." }
        val handle = scene.androidScene ?: return
        engine.destroyScene(handle)
        scene.invalidate()
    }

    actual fun createView(): View {
        val engine = requireNotNull(androidEngine) { "Engine is closed." }
        return View(engine.createView())
    }

    actual fun destroyView(view: View) {
        val engine = requireNotNull(androidEngine) { "Engine is closed." }
        val handle = view.androidView ?: return
        engine.destroyView(handle)
        view.invalidate()
    }

    actual fun close() {
        androidEngine?.destroy()
        androidEngine = null
    }

    actual companion object {
        actual fun create(config: EngineConfig): Engine {
            require(config == EngineConfig()) {
                "Android Engine.create currently supports default EngineConfig only."
            }
            return Engine(AndroidFilamentEngine.create())
        }
    }
}