package eric.bitria.samples

import eric.bitria.samples.shared.resources.Res
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.ExperimentalResourceApi
import java.awt.BorderLayout
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JFrame
import javax.swing.SwingUtilities

@OptIn(ExperimentalResourceApi::class)
fun main() {
    SwingUtilities.invokeLater {
        val frame = JFrame("Filament KMP - Swing Sample")
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.layout = BorderLayout()

        val renderer = FilamentRenderer()
        val filamentPanel = FilamentPanel(renderer)

        frame.add(filamentPanel, BorderLayout.CENTER)
        frame.pack()
        frame.isVisible = true

        // Initialize Filament
        renderer.initialize()

        // Load assets
        runBlocking {
            try {
                val filamatBytes = Res.readBytes("files/materials/textured.filamat")
                renderer.setupResourceMaterialCube(filamatBytes)
                println("Main: Loaded textured.filamat")
            } catch (e: Exception) {
                println("Main: Failed to load textured.filamat: ${e.message}")
            }

            try {
                val glbBytes = Res.readBytes("files/models/Duck.glb")
                renderer.setupGltfModel(glbBytes)
                println("Main: Loaded Duck.glb")
            } catch (e: Exception) {
                println("Main: Failed to load Duck.glb: ${e.message}")
            }
        }

        filamentPanel.startRendering()

        frame.addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent?) {
                filamentPanel.stopRendering()
                renderer.destroy()
            }
        })
    }
}
