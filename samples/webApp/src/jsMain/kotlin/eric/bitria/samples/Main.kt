@file:OptIn(kotlin.js.ExperimentalJsExport::class)

package eric.bitria.samples

import io.github.erkko68.filament.NativeSurface
import io.github.erkko68.filament.Viewport
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.w3c.dom.HTMLCanvasElement
import eric.bitria.samples.shared.resources.Res

fun main() {
    // Expose the callback on window so the js() string literal can reach it
    kotlinx.browser.window.asDynamic()._filamentReady = ::startApp
    // Spread Filament into window so Kotlin externals (which expect top-level globals) can find them.
    // Filament uses '$' as separator (Camera$Fov) but the Kotlin externals use '_' (Camera_Fov),
    // so we also create '_'-aliased names for every '$'-named property.
    js("""Filament.init([''], function() {
        Object.assign(window, Filament);
        Object.getOwnPropertyNames(Filament).forEach(function(k) {
            if (k.indexOf('${'$'}') !== -1) window[k.replace(/\${'$'}/g, '_')] = Filament[k];
        });
        window._filamentReady();
    })""")
}

// Called once WASM is loaded — sets up Filament engine then starts the app
@JsExport
@Suppress("unused")
fun startApp() {
    val canvas = document.getElementById("filament-canvas") as? HTMLCanvasElement
        ?: error("No #filament-canvas element found")

    canvas.width = window.innerWidth
    canvas.height = window.innerHeight
    window.addEventListener("resize", callback = {
        canvas.width = window.innerWidth
        canvas.height = window.innerHeight
    })

    val controller = FilamentController()
    val scene = SampleScene(controller)

    // Engine.create(canvas) on JS — canvas is the sharedContext
    controller.initialize(canvas)
    scene.setup()

    val surface = NativeSurface(canvas)
    controller.setSurface(surface, canvas.width, canvas.height)

    MainScope().launch {
        try {
            val filamatBytes = Res.readBytes("files/materials/textured.filamat")
            scene.loadMaterialCube(filamatBytes)
            println("Main: Loaded textured.filamat")
        } catch (e: Exception) {
            println("Main: Failed to load textured.filamat: ${e.message}")
        }

        try {
            val glbBytes = Res.readBytes("files/models/Duck.glb")
            scene.loadGltfModel(glbBytes)
            println("Main: Loaded Duck.glb")
        } catch (e: Exception) {
            println("Main: Failed to load Duck.glb: ${e.message}")
        }
    }

    window.addEventListener("resize", callback = {
        val w = canvas.width
        val h = canvas.height
        controller.updateViewport(w, h)
        controller.setSurface(surface, w, h)
    })

    var startTime = -1.0
    fun frame(timestamp: Double) {
        if (startTime < 0.0) startTime = timestamp
        val elapsed = (timestamp - startTime) / 1000.0
        scene.updateAnimations(elapsed)
        controller.render(timestamp.toLong() * 1_000_000L)
        window.requestAnimationFrame(::frame)
    }
    window.requestAnimationFrame(::frame)
}
