package eric.bitria.samples

import dev.filament.kmp.*

class FilamentRenderer : FilamentViewRenderer {
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
    var swapChain: SwapChain? = null
        private set
    var skybox: Skybox? = null
        private set

    fun initialize() {
        engine = Engine.create()
        renderer = engine!!.createRenderer()
        scene = engine!!.createScene()
        view = engine!!.createView()
        camera = engine!!.createCamera()

        view!!.setScene(scene)
        view!!.setCamera(camera)

        // Simple red skybox for visual testing
        skybox = Skybox.Builder()
            .color(1.0f, 0.0f, 0.0f, 1.0f)
            .build(engine!!)
        scene!!.setSkybox(skybox)
        
        // Setup initial camera
        camera!!.setProjection(45.0, 1.0, 0.1, 100.0, Camera.Fov.VERTICAL)
        camera!!.lookAt(0.0, 0.0, 4.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0)
    }

    override fun onSurfaceAvailable(surface: NativeSurface, width: Int, height: Int) {
        engine?.let {
            if (swapChain != null) {
                it.destroySwapChain(swapChain!!)
            }
            swapChain = it.createSwapChain(surface)
            onSurfaceResized(width, height)
        }
    }

    override fun onSurfaceResized(width: Int, height: Int) {
        if (width <= 0 || height <= 0) return

        view?.setViewport(Viewport(0, 0, width, height))
        val aspect = width.toDouble() / height.toDouble()
        camera?.setProjection(45.0, aspect, 0.1, 100.0, Camera.Fov.VERTICAL)
    }

    override fun onSurfaceDetached() {
        engine?.let {
            if (swapChain != null) {
                it.destroySwapChain(swapChain!!)
                swapChain = null
            }
        }
    }

    override fun render(frameTimeNanos: Long) {
        val renderer = renderer ?: return
        val swapChain = swapChain ?: return
        val view = view ?: return

        if (renderer.beginFrame(swapChain, frameTimeNanos)) {
            renderer.render(view)
            renderer.endFrame()
        }
    }

    fun destroy() {
        engine?.let {
            if (skybox != null) it.destroySkybox(skybox!!)
            if (camera != null) it.destroyCamera(camera!!)
            if (view != null) it.destroyView(view!!)
            if (scene != null) it.destroyScene(scene!!)
            if (renderer != null) it.destroyRenderer(renderer!!)
            if (swapChain != null) it.destroySwapChain(swapChain!!)
            it.destroy()
        }
        engine = null
        renderer = null
        scene = null
        view = null
        camera = null
        swapChain = null
        skybox = null
    }
}
