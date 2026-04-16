package eric.bitria.samples

import io.github.erkko68.filament.NativeSurface
import java.awt.Canvas
import java.awt.Color
import java.awt.Dimension
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import javax.swing.JPanel
import kotlin.math.ceil

class FilamentPanel(private val renderer: FilamentViewRenderer) : JPanel() {
    private val canvas = Canvas()
    private var nativeSurface: NativeSurface? = null

    init {
        // Tight layout with no gaps
        layout = java.awt.BorderLayout(0, 0)
        
        // Match Filament's dark theme to hide any sub-pixel gaps
        background = Color.BLACK
        canvas.background = Color.BLACK
        
        // Interop blending works best when components handle their own opacity
        isOpaque = true
        
        add(canvas, java.awt.BorderLayout.CENTER)

        canvas.addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent) {
                updateViewport()
            }
        })
    }

    private fun updateViewport() {
        val scale = canvas.graphicsConfiguration.defaultTransform.scaleX
        // Use ceil to ensure we cover the entire surface, avoiding 1-pixel black gaps
        renderer.onSurfaceResized(
            ceil(canvas.width * scale).toInt(),
            ceil(canvas.height * scale).toInt()
        )
    }

    fun setupNativeSurface() {
        if (nativeSurface != null) return

        if (canvas.isDisplayable) {
            val scale = canvas.graphicsConfiguration.defaultTransform.scaleX
            nativeSurface = NativeSurface(canvas)
            renderer.onSurfaceAvailable(
                nativeSurface!!,
                ceil(canvas.width * scale).toInt(),
                ceil(canvas.height * scale).toInt()
            )
        }
    }

    fun dispose() {
        renderer.onSurfaceDetached()
        nativeSurface = null
    }

    override fun getPreferredSize(): Dimension {
        return Dimension(800, 600)
    }
    
    override fun addNotify() {
        super.addNotify()
        setupNativeSurface()
    }

    override fun removeNotify() {
        dispose()
        super.removeNotify()
    }
}
