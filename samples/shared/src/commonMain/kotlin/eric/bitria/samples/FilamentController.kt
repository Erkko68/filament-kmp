package eric.bitria.samples

import io.github.erkko68.filament.*
import io.github.erkko68.filament.filamat.MaterialBuilder
import io.github.erkko68.filament.gltfio.Gltfio

/**
 * Manages the core Filament engine resources and the rendering life-cycle.
 * This class is platform-agnostic and relies on external surface configuration.
 */
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

    // Current surface resources
    var swapChain: SwapChain? = null
        private set
    var renderTarget: RenderTarget? = null
        private set
    var targetWidth: Int = 0
        private set
    var targetHeight: Int = 0
        private set

    fun initialize(backend: Engine.Backend = Engine.Backend.DEFAULT) {
        if (engine != null) return
        
        // Initialize global Filament systems
        engine = Engine.create(backend)
        MaterialBuilder.init()
        Gltfio.init()
        
        val engine = engine!!
        renderer = engine.createRenderer()
        scene = engine.createScene()
        view = engine.createView()
        camera = engine.createCamera()

        view!!.setScene(scene)
        view!!.setCamera(camera)

        // Setup default camera
        camera!!.setProjection(45.0, 1.0, 0.1, 100.0, Camera.Fov.VERTICAL)
        camera!!.lookAt(0.0, 1.0, 10.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0)
        camera!!.setExposure(16.0f, 1.2f, 100.0f)
        
        view!!.setPostProcessingEnabled(false)
        
        // Neutral skybox
        val skybox = Skybox.Builder()
            .color(0.1f, 0.125f, 0.15f, 1.0f)
            .build(engine)
        scene!!.setSkybox(skybox)
    }

    /**
     * Configures the controller to use a direct native surface (e.g., a window).
     */
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

    /**
     * Configures the controller to use an offscreen render target and swapchain.
     */
    fun setOffscreenSurface(width: Int, height: Int, target: RenderTarget, sc: SwapChain) {
        val engine = engine ?: return
        destroySurface()
        
        renderTarget = target
        swapChain = sc
        targetWidth = width
        targetHeight = height
        
        view?.setRenderTarget(renderTarget)
        updateViewport(width, height)
    }

    fun updateViewport(width: Int, height: Int) {
        if (width <= 0 || height <= 0) return
        view?.setViewport(Viewport(0, 0, width, height))
        val aspect = width.toDouble() / height.toDouble()
        camera?.setProjection(45.0, aspect, 0.1, 100.0, Camera.Fov.VERTICAL)
    }

    fun render(frameTimeNanos: Long) {
        val renderer = renderer ?: return
        val swapChain = swapChain ?: return
        val view = view ?: return

        if (renderer.beginFrame(swapChain, frameTimeNanos)) {
            renderer.render(view)
            renderer.endFrame()
            engine?.flushAndWait()
        }
    }

    private fun destroySurface() {
        val engine = engine ?: return
        swapChain?.let { engine.destroySwapChain(it) }
        swapChain = null
        
        renderTarget?.let { engine.destroyRenderTarget(it) }
        renderTarget = null
        
        view?.setRenderTarget(null)
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
