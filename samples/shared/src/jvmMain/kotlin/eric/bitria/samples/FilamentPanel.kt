package eric.bitria.samples

import io.github.erkko68.filament.NativeSurface
import java.awt.Canvas
import java.awt.Dimension
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import javax.swing.JPanel
import javax.swing.SwingUtilities
import javax.swing.Timer

class FilamentPanel(private val renderer: FilamentViewRenderer) : JPanel() {
    private val canvas = Canvas()
    private var nativeSurface: NativeSurface? = null
    private var renderTimer: Timer? = null

    init {
        layout = java.awt.BorderLayout()
        add(canvas, java.awt.BorderLayout.CENTER)

        canvas.addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent) {
                renderer.onSurfaceResized(canvas.width, canvas.height)
            }
        })
    }

    fun setupNativeSurface() {
        if (nativeSurface != null) return
        
            if (canvas.isDisplayable) {
                nativeSurface = NativeSurface(canvas)
                renderer.onSurfaceAvailable(nativeSurface!!, canvas.width, canvas.height)
            }
        }
    }

    fun startRendering() {
        if (renderTimer != null) return
        
        setupNativeSurface()

        renderTimer = Timer(16) { // ~60 FPS
            if (nativeSurface == null && canvas.isDisplayable) {
                 setupNativeSurface()
            }
            if (nativeSurface != null) {
                renderer.render(System.nanoTime())
            }
        }
        renderTimer?.start()
    }

    fun stopRendering() {
        renderTimer?.stop()
        renderTimer = null
        dispose()
    }

    fun dispose() {
        renderer.onSurfaceDetached()
        nativeSurface = null
    }

    override fun getPreferredSize(): Dimension {
        return Dimension(800, 600)
    }
    
    override fun removeNotify() {
        stopRendering()
        super.removeNotify()
    }
}
