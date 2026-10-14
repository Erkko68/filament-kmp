package eric.bitria.samples.awt

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.NativeSurface
import io.github.erkko68.filament.Renderer
import io.github.erkko68.filament.Skybox
import io.github.erkko68.filament.SwapChain
import io.github.erkko68.filament.Viewport
import java.awt.Canvas
import java.awt.Dimension
import java.awt.EventQueue
import java.awt.Frame
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.Timer
import kotlin.math.cos
import kotlin.math.roundToInt

/**
 * Filament rendering straight into an AWT [Canvas], without Compose.
 *
 * The view is filled by a skybox whose colour cycles, over a grey clear colour: if the viewport ever
 * falls short of the canvas (a resize, a display scale), the grey shows around it.
 *
 * Pass a backend to override Filament's default (Vulkan on Windows and Linux, Metal on macOS):
 * `./gradlew :awtApp:run --args=opengl`.
 */
fun main(args: Array<String>) {
    val backend = args.firstOrNull()?.let { Engine.Backend.valueOf(it.uppercase()) } ?: Engine.Backend.DEFAULT
    // Filament objects stay on the thread that created the engine: here, AWT's event dispatch thread.
    EventQueue.invokeLater { FilamentWindow(backend).open() }
}

private class FilamentWindow(backend: Engine.Backend) {
    private val engine = checkNotNull(Engine.create(backend)) { "Couldn't create a $backend engine" }
    private val renderer = engine.createRenderer().apply {
        clearOptions = Renderer.ClearOptions().apply {
            clearColor = doubleArrayOf(0.2, 0.2, 0.2, 1.0)
            clear = true
        }
    }
    private val skybox = Skybox.Builder().color(0f, 0f, 0f, 1f).build(engine)
    private val scene = engine.createScene().apply { skybox = this@FilamentWindow.skybox }
    private val cameraEntity = engine.entityManager.create()
    private val camera = engine.createCamera(cameraEntity)
    private val view = engine.createView().apply {
        scene = this@FilamentWindow.scene
        camera = this@FilamentWindow.camera
    }

    private val title = "Filament KMP - AWT (${engine.backend})"
    private val canvas = Canvas().apply { preferredSize = Dimension(800, 600) }
    private val frame = Frame(title)
    private var swapChain: SwapChain? = null
    private val startNanos = System.nanoTime()
    // A Swing timer fires on the event dispatch thread, the engine's thread.
    private val timer = Timer(16) { renderFrame() }

    fun open() {
        frame.add(canvas)
        frame.pack()
        frame.setLocationRelativeTo(null)
        frame.addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent) = close()
        })
        frame.isVisible = true
        // The canvas has its native window once its frame is showing.
        swapChain = engine.createSwapChain(NativeSurface(canvas))
        timer.start()
    }

    private fun renderFrame() {
        val swapChain = swapChain ?: return
        // The viewport is in pixels: the canvas size times the display scale (2 on Retina, 1.25 for 125 %...).
        val scale = canvas.graphicsConfiguration.defaultTransform
        val width = (canvas.width * scale.scaleX).roundToInt()
        val height = (canvas.height * scale.scaleY).roundToInt()
        if (width == 0 || height == 0) return
        if (view.viewport.width != width || view.viewport.height != height) {
            view.viewport = Viewport(0, 0, width, height)
            frame.title = "$title - ${width}x$height px @${scale.scaleX}x"
        }

        val t = (System.nanoTime() - startNanos) / 1e9
        fun channel(phase: Double) = (0.5 + 0.5 * cos(t + phase)).toFloat()
        skybox.setColor(channel(0.0), channel(2.1), channel(4.2), 1f)

        if (renderer.beginFrame(swapChain)) {
            renderer.render(view)
            renderer.endFrame()
        }
    }

    private fun close() {
        timer.stop()
        // The swap chain must be gone before the canvas loses its native window.
        swapChain?.let { engine.destroy(it) }
        swapChain = null
        engine.flushAndWait()
        frame.dispose()

        engine.destroy(view)
        engine.destroy(scene)
        engine.destroy(skybox)
        engine.destroy(renderer)
        engine.destroyCameraComponent(cameraEntity)
        engine.entityManager.destroy(cameraEntity)
        Engine.destroy(engine)
    }
}
