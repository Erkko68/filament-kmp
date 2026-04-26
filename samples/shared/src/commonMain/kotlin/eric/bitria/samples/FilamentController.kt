package eric.bitria.samples

import io.github.erkko68.filament.*
import io.github.erkko68.filament.filamat.MaterialBuilder
import io.github.erkko68.filament.gltfio.Gltfio

class FilamentController {
    var engine: Engine? = null
        private set
    var renderer: Renderer? = null
        private set
    var scene: Scene? = null
        private set
    var view: View? = null
        private set
    var camera: Camera? = null
        private set

    private var swapChain: SwapChain? = null

    fun initialize(backend: Engine.Backend = Engine.Backend.DEFAULT, sharedContext: Any? = null) {
        if (engine != null) return

        engine = if (sharedContext != null) Engine.create(sharedContext) else Engine.create(backend)
        MaterialBuilder.init()
        Gltfio.init()

        val engine = engine!!
        renderer = engine.createRenderer()
        scene = engine.createScene()
        view = engine.createView()
        camera = engine.createCamera()

        view!!.setScene(scene)
        view!!.setCamera(camera)

        camera!!.setProjection(45.0, 1.0, 0.1, 100.0, Camera.Fov.VERTICAL)
        camera!!.lookAt(0.0, 1.0, 10.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0)
        camera!!.setExposure(16.0f, 1.2f, 100.0f)

        view!!.setPostProcessingEnabled(false)

        val skybox = Skybox.Builder()
            .color(0.1f, 0.125f, 0.15f, 1.0f)
            .build(engine)
        scene!!.setSkybox(skybox)
    }

    fun setSurface(surface: NativeSurface, width: Int, height: Int) {
        val engine = engine ?: return
        destroySurface()

        swapChain = try {
            engine.createSwapChain(surface)
        } catch (_: Exception) {
            null
        }

        if (swapChain != null) {
            updateViewport(width, height)
        }
    }

    fun updateViewport(width: Int, height: Int) {
        if (width <= 0 || height <= 0) return
        view?.setViewport(Viewport(0, 0, width, height))
        val aspect = width.toDouble() / height.toDouble()
        camera?.setProjection(45.0, aspect, 0.1, 100.0, Camera.Fov.VERTICAL)
    }

    fun render(
        frameTimeNanos: Long,
        swapChain: SwapChain? = this.swapChain,
        flush: Boolean = true,
    ): Boolean {
        val renderer = renderer ?: return false
        val sc = swapChain ?: return false
        val view = view ?: return false

        if (renderer.beginFrame(sc, frameTimeNanos)) {
            renderer.render(view)
            renderer.endFrame()
            if (flush) engine?.flushAndWait()
            return true
        }
        return false
    }

    private fun destroySurface() {
        val engine = engine ?: return
        swapChain?.let { engine.destroySwapChain(it) }
        swapChain = null
    }

    fun destroy() {
        val engine = engine ?: return
        destroySurface()

        camera?.let { engine.destroyCamera(it) }
        view?.let { engine.destroyView(it) }
        scene?.let { s ->
            s.getSkybox()?.let { engine.destroySkybox(it) }
            engine.destroyScene(s)
        }
        renderer?.let { engine.destroyRenderer(it) }

        engine.destroy()
        this.engine = null
    }
}
